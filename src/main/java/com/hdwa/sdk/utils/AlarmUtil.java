package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataPrimitive;
import com.hdwa.sdk.entity.scene.DataSet;
import com.hdwa.sdk.entity.scene.DataValue;
import lombok.extern.slf4j.Slf4j;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author abao
 * @since 2023/9/5
 */
@Slf4j
public class AlarmUtil {
    /**
     * 报警改变字段
     */
    public static List<String> alarmColChange = Arrays.asList("state", "status", "treatState", "treatMode", "nature");
    public static JSONArray history_alarm_path = new JSONArray();
    static Map<Integer, String> stateMap = new HashMap<>(16);
    static Map<Integer, String> statusMap = new HashMap<>(16);
    static Map<Integer, String> treatStateMap = new HashMap<>(16);
    static Map<Integer, String> treatModeMap = new HashMap<>(16);
    static Map<Integer, String> natureMap = new HashMap<>(16);
    static Map<String, String> levelMap = new HashMap<>(16);
    static Map<String, String> categoryMap = new HashMap<>(16);

    static {
        history_alarm_path.add("基础对象");
        history_alarm_path.add("历史报警");
        stateMap.put(1, "未恢复");
        stateMap.put(2, "已恢复");
        stateMap.put(3, "已过期");
        statusMap.put(1, "未恢复");
        statusMap.put(2, "已恢复");
        statusMap.put(3, "已过期");
        treatStateMap.put(1, "未处理");
        treatStateMap.put(2, "处理中");
        treatStateMap.put(3, "已处理");
        treatModeMap.put(1, "忽略");
        treatModeMap.put(2, "转工单");
        treatModeMap.put(3, "关闭报警");
        natureMap.put(1, "误报");
        natureMap.put(2, "真实故障");
        natureMap.put(3, "设备测试");
        levelMap.put("1", "严重");
        levelMap.put("2", "较重");
        levelMap.put("3", "一般");
        categoryMap.put("Eq", "设备报警");
        categoryMap.put("Sy", "系统报警");
        categoryMap.put("Sp", "空间报警");
    }

    /**
     * 刷新报警数据
     *
     * @param projectId
     * @param groupCode
     * @param alarmUrl
     * @param repository
     * @return
     */
    public static JSONArray alarmRefresh(String projectId, String groupCode, String alarmUrl, RepositoryImpl repository) {
        try {
            JSONArray result = new JSONArray();
            //查询报警记录数据
            //log.warn("*****请求服务获取报警数据");
            JSONObject paramObject = new JSONObject();
            paramObject.put("appId", 0);
            paramObject.put("userId", "systemId");
            paramObject.put("projectId", projectId);
            paramObject.put("groupCode", groupCode);
            paramObject.put("dealType", 0);
            paramObject.put("state", 1);
            paramObject.put("size", 5000);
            paramObject.put("current", 1);
            JSONObject resultDataAram = OkHttpClientUtil.httpPost(paramObject, alarmUrl + UrlConstant.ALARM_RECORD_PAGE);
            if (resultDataAram == null || resultDataAram.getIntValue(BaseDecConstant.CODE) != 200 || resultDataAram.getJSONObject(BaseDecConstant.DATA) == null) {
                log.error("查询报警记录数据异常：{}", resultDataAram);
                return new JSONArray();
            }
            JSONArray content = resultDataAram.getJSONObject(BaseDecConstant.DATA).getJSONArray(BaseDecConstant.RECORDS);
            if (content == null) {
                log.error("报警记录数据为null");
                return new JSONArray();
            }
            JSONArray ids = new JSONArray();
            Map<String, JSONObject> alarmMap = new HashMap<>(16);
            for (int i = 0; i < content.size(); i++) {
                JSONObject alarm = content.getJSONObject(i);
                if (alarm.containsKey("treatMode")) {
                    String id = (String) alarm.get("id");
                    int treatMode = alarm.getIntValue("treatMode");
                    if (treatMode == 2) {
                        ids.add(id);
                        alarmMap.put(id, alarm);
                    }
                }
            }

            //查询工单状态
            if (ids.size() > 0) {
                JSONObject param = new JSONObject();
                param.put("projectId", projectId);
                param.put("groupCode", groupCode);
                param.put("ids", ids);
                JSONObject resultDataOrder = OkHttpClientUtil.httpPost(param, alarmUrl + UrlConstant.QUERY_ORDER_STATE);

                if (resultDataOrder == null || resultDataOrder.getIntValue(BaseDecConstant.CODE) != 200 || resultDataOrder.getJSONArray(BaseDecConstant.DATA) == null) {
                    log.error("查询报警工单记录数据异常，{}", resultDataOrder);
                } else {
                    JSONArray data = resultDataOrder.getJSONArray(BaseDecConstant.DATA);
                    if (data == null) {
                        log.error("报警工单记录数据为null");
                    } else {
                        //log.warn("*****查询工单状态完成" + ids.size());
                        for (int i = 0; i < data.size(); i++) {
                            JSONObject orderStateItem = data.getJSONObject(i);
                            String alarmId = (String) orderStateItem.get("alarmId");
                            JSONObject alarm = alarmMap.get(alarmId);
                            alarm.put("orderId", orderStateItem.get("orderId"));
                            alarm.put("orderStateDesc", orderStateItem.get("orderStateDesc"));
                        }
                    }
                }
            }
            result.addAll(content);

            for (int i = 0; i < result.size(); i++) {
                JSONObject alarm = result.getJSONObject(i);
                try {
                    updateAlarm(alarm, repository);
                } catch (Exception e) {
                    log.error("处理报警记录出现异常", e);
                }
            }
            return result;
        } catch (Exception e) {
            log.error("查询报警数据出现异常", e);
        }
        return new JSONArray();
    }

    /**
     * 添加报警数据属性
     *
     * @param alarm
     */
    public static void updateAlarm(JSONObject alarm, RepositoryImpl repository) {
        SimpleDateFormat sdf_T = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat sdf_blank = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");

        if (alarm.containsKey("triggerInfo")) {
            Object object = alarm.get("triggerInfo");
            if (object instanceof String) {
                String string = alarm.getString("triggerInfo");
                JSONObject json = JSON.parseObject(string);
                alarm.put("triggerInfo", json);
            }
        }

        JSONObject triggerInfo = (JSONObject) alarm.get("triggerInfo");
        if (triggerInfo != null && triggerInfo.size() == 1) {
            String[] infos = triggerInfo.keySet().toArray(new String[0]);
            alarm.put("triggerMainInfo", infos[0]);
        }

        if (!alarm.containsKey("floorId")) {
            String objId = (String) alarm.get("objId");
            DataObject sdo = repository.id2sdv.get(objId);
            if (sdo != null) {
                DataValue floor = sdo.get("所在楼层-id");
                if (floor != null && floor.valuePrim != null && floor.valuePrim.value instanceof String) {
                    String floorString = (String) floor.valuePrim.value;
                    alarm.put("floorId", floorString);
                }
            }
            if (sdo != null) {
                DataValue floor = sdo.get("所属楼层-id");
                if (floor != null && floor.valuePrim != null && floor.valuePrim.value instanceof String) {
                    String floorString = (String) floor.valuePrim.value;
                    alarm.put("floorId", floorString);
                }
            }
        }
        if (alarm.containsKey("objId")) {
            alarm.put("objectId", alarm.get("objId"));
        }
        if (alarm.containsKey("objName")) {
            alarm.put("objectName", alarm.get("objName"));
        }
        if (alarm.containsKey("state")) {
            alarm.put("state_name", stateMap.get(alarm.get("state")));
            alarm.put("stateName", stateMap.get(alarm.get("state")));
        }
        if (alarm.containsKey("state")) {
            alarm.put("status", alarm.get("state"));
        }
        if (alarm.containsKey("status")) {
            alarm.put("status_name", statusMap.get(alarm.get("status")));
            alarm.put("statusName", statusMap.get(alarm.get("status")));
        }
        if (alarm.containsKey("treatState")) {
            alarm.put("treatState_name", treatStateMap.get(alarm.get("treatState")));
            alarm.put("treatStateName", treatStateMap.get(alarm.get("treatState")));
        }
        if (alarm.containsKey("treatMode")) {
            alarm.put("treatMode_name", treatModeMap.get(alarm.get("treatMode")));
            alarm.put("treatModeName", treatModeMap.get(alarm.get("treatMode")));
        }
        if (alarm.containsKey("nature")) {
            alarm.put("nature_name", natureMap.get(alarm.get("nature")));
            alarm.put("natureName", natureMap.get(alarm.get("nature")));
        }
        if (alarm.containsKey("level")) {
            alarm.put("level_name", levelMap.get(alarm.get("level")));
            alarm.put("levelName", levelMap.get(alarm.get("level")));
        }
        if (alarm.containsKey("objType")) {
            alarm.put("category", alarm.get("objType"));
        }
        if (alarm.containsKey("category")) {
            alarm.put("category_name", categoryMap.get(alarm.get("category")));
            alarm.put("categoryName", categoryMap.get(alarm.get("category")));
        }
        Object commentObject = alarm.get("comment");
        if (commentObject == null) {
            alarm.put("comments", new JSONArray());
        } else {
            if (commentObject instanceof String) {
                JSONArray comments = JSON.parseArray((String) commentObject);
                for (int i = 0; i < comments.size(); i++) {
                    JSONObject one_comment = comments.getJSONObject(i);
                    if (one_comment.get("commentTime") != null) {
                        Object commentTime = one_comment.get("commentTime");
                        one_comment.put("commentTime", getTime(sdf_T, sdf_blank, sdf, commentTime));
                    }
                }
                alarm.put("comments", comments);
            } else {
                alarm.put("comments", new JSONArray());
            }
        }
        alarm.remove("comment");

        if (alarm.get("triggerTime") != null) {
            Object commentTime = alarm.get("triggerTime");
            alarm.put("triggerTime", getTime(sdf_T, sdf_blank, sdf, commentTime));
        }
        if (alarm.get("endTime") != null) {
            Object commentTime = alarm.get("endTime");
            alarm.put("endTime", getTime(sdf_T, sdf_blank, sdf, commentTime));
        }
        if (alarm.get("createTime") != null) {
            Object commentTime = alarm.get("createTime");
            alarm.put("createTime", getTime(sdf_T, sdf_blank, sdf, commentTime));
        }
        if (alarm.get("updateTime") != null) {
            Object commentTime = alarm.get("updateTime");
            alarm.put("updateTime", getTime(sdf_T, sdf_blank, sdf, commentTime));
        }

        if (alarm.containsKey("createTime")) {
            alarm.put("create_time", alarm.get("createTime"));
        }
        if (alarm.containsKey("endTime")) {
            alarm.put("close_time", alarm.get("endTime"));
        }

        if (alarm.containsKey("concern")) {
            Object concern = alarm.get("concern");
            if (concern instanceof Boolean) {
                boolean concernBoolean = (Boolean) concern;
                alarm.put("concern", concernBoolean ? 1 : 0);
            }
        }

        // 给报警增加ibmsSceneCode和ibmsClassCode
        if (alarm.containsKey("objectId")) {
            String objectId = (String) alarm.get("objectId");
            if (repository.id2sdv.containsKey(objectId)) {
                DataObject sdoInner = repository.id2sdv.get(objectId);
                if (sdoInner.containsKey("ibmsSceneCode")) {
                    DataValue sdvInner = sdoInner.get("ibmsSceneCode");
                    alarm.put("ibmsSceneCode", sdvInner.valuePrim.value);
                }
                if (sdoInner.containsKey("ibmsClassCode")) {
                    DataValue sdvInner = sdoInner.get("ibmsClassCode");
                    alarm.put("ibmsClassCode", sdvInner.valuePrim.value);
                }
                if (sdoInner.containsKey("localName")) {
                    DataValue sdvInner = sdoInner.get("localName");
                    alarm.put("localName", sdvInner.valuePrim.value);
                }
                if (sdoInner.containsKey("localId")) {
                    DataValue sdvInner = sdoInner.get("localId");
                    alarm.put("localId", sdvInner.valuePrim.value);
                }
            }
        }
    }

    /**
     * 计算报警数据
     *
     * @param repository
     * @param AlarmJob
     */
    public static void calculatedAlarm(RepositoryImpl repository, JSONObject AlarmJob) {
        try {
            String type = (String) AlarmJob.get("type");
            switch (type) {
                case "refresh":
                    JSONArray Content = AlarmJob.getJSONArray("Content");
                    AlarmUtil.exeRefresh(Content, repository);
                    break;
                case "alarm":
                    JSONObject alarm = AlarmJob.getJSONObject("alarm");
                    AlarmUtil.exeProcessAlarm(alarm, true, repository);
                    break;
                case "order": {
                    String id = AlarmJob.getString("id");
                    JSONObject alarm_order = AlarmJob.getJSONObject("alarm_order");
                    AlarmUtil.exeProcessOrderDesc(id, alarm_order);
                    break;
                }
                case "comment": {
                    String id = AlarmJob.getString("id");
                    JSONObject dtoJSON = AlarmJob.getJSONObject("dtoJSON");
                    AlarmUtil.exeProcessAlarmComment(id, dtoJSON);
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static String getTime(SimpleDateFormat sdf_T, SimpleDateFormat sdf_blank, SimpleDateFormat sdf, Object object) {
        String result;
        if (object instanceof Long) {
            Long timeLong = (Long) object;
            result = sdf_T.format(new Date(timeLong));
        } else if (object instanceof String) {
            String timeString = (String) object;
            try {
                result = sdf_T.format(sdf_blank.parse(timeString));
            } catch (Exception e) {
                try {
                    result = sdf_T.format(sdf.parse(timeString));
                } catch (Exception e1) {
                    result = object.toString();
                }
            }
        } else {
            result = object.toString();
        }
        return result;
    }


    public static void processOrderDesc(String id, JSONObject alarm_order) {
        JSONObject AlarmJob = new JSONObject();
        AlarmJob.put("type", "order");
        AlarmJob.put("id", id);
        AlarmJob.put("alarm_order", alarm_order);
        DataContainer.alarmBuffer.offer(AlarmJob, 16384);
    }


    public static void processAlarm(JSONObject alarm) {
        JSONObject AlarmJob = new JSONObject();
        AlarmJob.put("type", "alarm");
        AlarmJob.put("alarm", alarm);
        DataContainer.alarmBuffer.offer(AlarmJob, 16384);
    }

    public static void exeRefresh(JSONArray Content, RepositoryImpl repository) {
        DataContainer.alarmArray.set.clear();
        for (String objId : DataContainer.id2alarmList.keySet()) {
            DataContainer.id2alarmList.get(objId).valueArray.set.clear();
            DataContainer.id2alarmCount.get(objId).valuePrim.value = 0;
        }

        try {
            for (int i = 0; i < Content.size(); i++) {
                JSONObject alarm = Content.getJSONObject(i);
                try {
                    exeProcessAlarm(alarm, true, repository);
                } catch (Exception e) {
                    log.error("exe_refresh", e);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }


    public static void exeProcessAlarm(JSONObject alarm, boolean addWaitCompute, RepositoryImpl repository) {
        String id = (String) alarm.get("id");
        String objId = (String) alarm.get("objId");
        String treatState = alarm.get("treatState").toString();
        boolean alarmArrayRowChange = false;
        boolean alarmListRowChange = false;
        boolean alarmCountChange = false;
        Map<String, Boolean> colChangeMap = new HashMap<>(16);
        {
            DataValue alarmList = DataContainer.id2alarmList.get(objId);
            DataValue alarmCount = DataContainer.id2alarmCount.get(objId);
            if (alarmList == null) {
                DataValue sv_alarmList = new DataValue(null, null, "报警列表", null);
                sv_alarmList.finish = true;
                sv_alarmList.valueArray = new DataSet(false);
                sv_alarmList.valueArray.set = new CopyOnWriteArrayList<>();
                sv_alarmList.valueArray.setRowChange(true);
                DataContainer.id2alarmList.putIfAbsent(objId, sv_alarmList);
            }
            if (alarmCount == null) {
                DataValue sv_alarmCount = new DataValue(null, null, "报警数量", null);
                sv_alarmCount.finish = true;
                sv_alarmCount.valuePrim = new DataPrimitive();
                sv_alarmCount.valuePrim.value = 0;
                sv_alarmCount.valuePrim.change = true;
                DataContainer.id2alarmCount.putIfAbsent(objId, sv_alarmCount);
            }
            alarmList = DataContainer.id2alarmList.get(objId);
            alarmCount = DataContainer.id2alarmCount.get(objId);

            // 从设备下删除
            int existIndex = -1;
            for (int i = 0; i < alarmList.valueArray.set.size(); i++) {
                DataObject sdoInner = alarmList.valueArray.set.get(i);
                String idInner = (String) sdoInner.get("id").valuePrim.value;
                if (idInner.equals(id)) {
                    existIndex = i;
                    break;
                }
            }
            // 从报警列表下删除
            List<Integer> existIndexList = new CopyOnWriteArrayList<>();
            for (int index_alarm = 0; index_alarm < DataContainer.alarmArray.set.size(); index_alarm++) {
                DataObject alarmItem = DataContainer.alarmArray.set.get(index_alarm);
                if (alarmItem != null && alarmItem.valueObject != null) {
                    if (alarmItem.valueObject.get("id").valuePrim.value.equals(id)) {
                        existIndexList.add(0, index_alarm);
                    }
                }

            }

            if ("3".equals(treatState)) {
                if (existIndex != -1) {
                    alarmList.valueArray.set.remove(existIndex);
                    alarmListRowChange = true;
                }
                for (int removeIndex : existIndexList) {
                    DataContainer.alarmArray.set.remove(removeIndex);
                    alarmArrayRowChange = true;
                }
            } else {
                // 只替换非空字段
                DataObject sdoAlarm = BaseApiUtil.objectToSdo(alarm);
                if (existIndex != -1) {
                    DataObject sdoExist = alarmList.valueArray.set.get(existIndex);
                    for (String key : sdoAlarm.keySet()) {
                        DataValue sdvInner = sdoAlarm.get(key);
                        if (sdvInner == null || sdvInner.valuePrim == null || sdvInner.valuePrim.value == null) {
                            continue;
                        }
                        if (sdoExist.get(key) == null || sdoExist.get(key).valuePrim == null
                                || !sdvInner.valuePrim.value.equals(sdoExist.get(key).valuePrim.value)) {
                            sdoExist.put(key, sdoAlarm.get(key));
                            colChangeMap.put(key, true);
                        }
                    }
                } else {
                    alarmList.valueArray.set.add(sdoAlarm);
                    alarmListRowChange = true;
                }
                if (existIndexList.size() > 0) {
                    for (int removeIndex : existIndexList) {
                        DataObject sdoInner = DataContainer.alarmArray.set.get(removeIndex);
                        for (String key : sdoAlarm.keySet()) {
                            DataValue sdvInner = sdoAlarm.get(key);
                            if (sdvInner == null || sdvInner.valuePrim == null || sdvInner.valuePrim.value == null) {
                                continue;
                            }
                            sdoInner.put(key, sdoAlarm.get(key));
                        }
                    }
                } else {
                    DataContainer.alarmArray.set.add(sdoAlarm);
                    alarmArrayRowChange = true;
                }
            }
            if (!alarmCount.valuePrim.value.equals(alarmList.valueArray.set.size())) {
                alarmCount.valuePrim.value = alarmList.valueArray.set.size();
                alarmCountChange = true;
            }
        }
        if (addWaitCompute && repository.enable_factor) {
            if (alarmArrayRowChange) {
                repository.addWaitCompute(DataContainer.alarmArray);
            } else {
                for (String col : AlarmUtil.alarmColChange) {
                    if (colChangeMap.containsKey(col)) {
                        repository.addWaitCompute(DataContainer.alarmArray, col);
                    }
                }
            }
            if (repository.id2sdv.containsKey(objId)) {
                DataObject objSDV = repository.id2sdv.get(objId);
                DataValue sv_alarmList = objSDV.get("报警列表");
                DataValue sv_alarmCount = objSDV.get("报警数量");
                if (alarmListRowChange) {
                    repository.addWaitCompute(sv_alarmList);
                } else {
                    for (String col : AlarmUtil.alarmColChange) {
                        if (colChangeMap.containsKey(col)) {
                            repository.addWaitCompute(sv_alarmList.valueArray, col);
                        }
                    }
                }
                if (alarmCountChange) {
                    repository.addWaitCompute(sv_alarmCount);
                }
            }
        }
    }

    public static void exeProcessAlarmComment(String id, JSONObject dtoJSON) {
        DataSet alarmList = DataContainer.alarmArray;
        for (int i = 0; i < alarmList.set.size(); i++) {
            DataObject sdoInner = alarmList.set.get(i);
            String idInner = (String) sdoInner.get("id").valuePrim.value;
            if (idInner.equals(id)) {
                DataValue sdvInner = sdoInner.get("comments");
                JSONArray comments = (JSONArray) sdvInner.valuePrim.value;
                comments.add(dtoJSON);
                break;
            }
        }
    }

    public static void exeProcessOrderDesc(String id, JSONObject alarm_order) {
        for (DataObject sdo : DataContainer.alarmArray.set) {
            String idInner = (String) sdo.get("id").valuePrim.value;
            if (idInner.equals(id)) {
                for (String key : alarm_order.keySet()) {
                    if ("alarmId".equals(key) || "pushType".equals(key)) {
                        continue;
                    }
                    Object value = alarm_order.get(key);
                    DataValue orderIdInner = sdo.get(key);
                    if (orderIdInner != null) {
                        orderIdInner.valuePrim.value = value;
                    } else {
                        DataValue svInner = new DataValue(null, null, null, null);
                        svInner.finish = true;
                        svInner.valuePrim = new DataPrimitive();
                        svInner.valuePrim.value = value;
                        sdo.put(key, svInner);
                    }
                }
                break;
            }
        }
    }
}
