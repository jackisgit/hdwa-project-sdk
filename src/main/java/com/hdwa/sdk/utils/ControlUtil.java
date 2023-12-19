package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.constant.UrlConstant;
import com.hdwa.sdk.entity.InstructControlParam;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.DataObject;
import com.hdwa.sdk.entity.scene.DataPrimitive;
import com.hdwa.sdk.entity.scene.DataValue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 指令控制
 */
@Slf4j
public class ControlUtil {

    /**
     * @param repository
     * @param param
     * @return
     * @throws Exception
     */
    public static JSONObject setPoints(RepositoryImpl repository, InstructControlParam param) throws Exception {
        JSONArray result;
        Object valueObject = CalculateApiJsonUtil.getValueObject(repository, param.getPath());

        List<DataObject> sdoList = new CopyOnWriteArrayList<>();
        if (valueObject instanceof DataValue) {
            DataValue currData = (DataValue) valueObject;
            if (currData.valueArray != null) {
                DataValue detail = currData.parentObjectData.get("详情");
                result = setInner(repository, detail, currData.valueArray.set, param.getInfoValueSet(), sdoList, param);
            } else if (currData.valueObject != null) {
                result = setPoints(repository, currData.valueObject, param.getInfoValueSet(), sdoList);
            } else {
                throw new Exception();
            }
        } else {
            DataObject currData = (DataObject) valueObject;
            result = setPoints(repository, currData, param.getInfoValueSet(), sdoList);
        }

        JSONObject jsonObject = new JSONObject();
        jsonObject.put("objectList", sdoList);
        jsonObject.put("points", result);
        return jsonObject;
    }


    public static void setControlValue(JSONArray path, JSONObject infoValueSet) {
        JSONObject exist_value = DataContainer.controlValueMap.putIfAbsent(path.toJSONString(), infoValueSet);
        if (exist_value != null) {
            for (String info_code : infoValueSet.keySet()) {
                exist_value.put(info_code, infoValueSet.get(info_code));
            }
        }
    }

    public static JSONObject getControlValue(JSONArray path) {
        JSONObject result = DataContainer.controlValueMap.get(path.toString());
        if (result == null) {
            result = new JSONObject();
        }
        return result;
    }


    /**
     * 保存日志
     *
     * @param userId
     * @param userName
     * @param sdoList
     * @param infoValueSet
     * @param points
     */
    public static void saveOperationLog(String userId, String userName, List<DataObject> sdoList, JSONObject infoValueSet, JSONArray points, String url) {
        try {
            JSONObject postParam = new JSONObject();
            postParam.put("groupCode", BaseDecConstant.WD);
            postParam.put("projectId", BaseDecConstant.CURRENT_PROJECT_ID);
            if (userId == null || userId.length() == 0) {
                postParam.put("userId", "systemId");
            } else {
                postParam.put("userId", userId);
            }
            if (userName == null || userName.length() == 0) {
                postParam.put("userName", "系统");
            } else {
                postParam.put("userName", userName);
            }
            if (sdoList.size() == 1) {
                DataObject sdo = sdoList.get(0);
                postParam.put("objId", sdo.get("id").valuePrim.value);
            } else {
                JSONArray objs = new JSONArray();
                for (DataObject sdo : sdoList) {
                    objs.add(sdo.get("id").valuePrim.value);
                }
                postParam.put("objs", objs);
            }
            {
                DataObject sdo = sdoList.get(0);
                Object belongSystem = sdo.get("subSystemName").valuePrim.value;

                Object objName = sdo.get("localName").valuePrim.value;
                String classCode = (String) sdo.get("classCode").valuePrim.value;
                postParam.put("ibmsSceneCode", sdo.get("ibmsSceneCode").valuePrim.value);
                postParam.put("ibmsSceneName", sdo.get("subSystemName").valuePrim.value);
                postParam.put("ibmsClassCode", sdo.get("ibmsClassCode").valuePrim.value);
                postParam.put("ibmsClassName", sdo.get("数据字典类型名称").valuePrim.value);
                postParam.put("classCode", classCode);
                postParam.put("systemType", "控制指令下发");
                postParam.put("module", "指令控制");
                postParam.put("logSource", "user");


                RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
                List<DataObject> infoList = repository.infoArrayDic.get(classCode).set;

                StringBuilder sb = new StringBuilder();
                for (String key : infoValueSet.keySet()) {
                    Object infoValue = infoValueSet.get(key);
                    DataObject infoDef = null;
                    for (DataObject infoDefInner : infoList) {
                        String code = (String) infoDefInner.get("code").valuePrim.value;
                        if (code.equals(key)) {
                            infoDef = infoDefInner;
                            break;
                        }
                    }
                    String infoName = key;
                    if (infoDef != null) {
                        infoName = (String) infoDef.get("name").valuePrim.value;
                        infoValue = ControlUtil.value2CanRead(infoDef, infoValue);
                    }
                    sb.append("【").append(infoName).append("】").append("设为：").append("【").append(infoValue).append("】");
                    sb.append("；");
                }
                JSONArray success_points = new JSONArray();
                JSONArray failure_points = new JSONArray();
                for (int i = 0; i < points.size(); i++) {
                    JSONObject point = points.getJSONObject(i);
                    String status = (String) point.get("status");
                    if (status.equals("finish:success")) {
                        success_points.add(point);
                    } else {
                        failure_points.add(point);
                    }
                }
                sb.append("控制结果：").append(points.size()).append("个控制指令").append(success_points.size() > 0 ? ("，" + success_points.size() + "个成功") : "").append(failure_points.size() > 0 ? ("，" + failure_points.size() + "个失败") : "");
                postParam.put("details", "【" + belongSystem + "】：" + objName + "-" + sb);

            }

            OkHttpClientUtil.httpPost(postParam, url + UrlConstant.SAVE_LOG_URL).getBoolean(BaseDecConstant.SUCCESS);
        } catch (Exception e) {
            log.error("保存日志操作失败", e);
        }
    }

    public static Object value2CanRead(DataObject infoDef, Object infoValue) {
        if (infoDef.containsKey("dataSource")) {
            JSONArray dataSource = (JSONArray) infoDef.get("dataSource").valuePrim.value;
            for (Object o : dataSource) {
                JSONObject item = (JSONObject) o;
                String code = item.getString("code");
                double value1 = Double.parseDouble(infoValue.toString());
                double value2 = Double.parseDouble(code);
                if (value1 == value2) {
                    return item.getString("name");
                }
            }
        }
        return infoValue;
    }

    private static JSONArray setPoints(RepositoryImpl Repository, DataObject object, JSONObject infoValueSet, List<DataObject> sdoList)
            throws Exception {
        JSONArray result = null;
        //系统级别只下自己的控制点位
        if (object.parentObjectData != null && object.parentObjectData.get("名称") != null) {
            DataValue dataValue = object.parentObjectData.get("名称");
            if (dataValue.valuePrim != null) {
                DataPrimitive dataPrimitive = dataValue.valuePrim;
                if (dataPrimitive.value != null) {
                    String name = dataPrimitive.value.toString();
                    if (name.contains("ACCC") || name.contains("ACCH")) {
                        result = setInner(Repository, object, infoValueSet, sdoList);
                    }
                }
            }
        } else if (object.containsKey("清单")) {//控制系统下的设备清单
            DataValue list = object.get("清单");
            DataValue detail = object.get("详情");
            result = setInner(Repository, detail, list.valueArray.set, infoValueSet, sdoList, null);
        } else {//控制单设备
            result = setInner(Repository, object, infoValueSet, sdoList);
        }
        return result;
    }

    private static JSONArray setInner(RepositoryImpl Repository, DataObject object, JSONObject infoValueSet, List<DataObject> sdoList)
            throws Exception {
        JSONArray points = new JSONArray();
        build_points(Repository, object, infoValueSet, points);
        sdoList.add(object);
        build_object(object, infoValueSet);
        return points;
    }

    private static JSONArray setInner(RepositoryImpl repository, DataValue detail, List<DataObject> objectArray, JSONObject infoValueSet,
                                      List<DataObject> sdoList, InstructControlParam params) {
        if (detail != null) {
            log.warn("-----下发时有详情:" + detail.valueObject);
            build_object(detail.valueObject, infoValueSet);
        }
        JSONArray points = new JSONArray();

        //查询需要下发的设备
        JSONArray resultData = (JSONArray) CalculateApiJsonUtil.getValueJson(CalculateApiJsonUtil.getValueObject(repository, params.getPath()));
        //筛选真实要下发的数据
        List<Object> list = resultData.stream().map(map -> ((JSONObject) map).getString(BaseDecConstant.ID)).collect(Collectors.toList());

        for (DataObject object : objectArray) {
            //真实下发的id包含清单就下发
            if (list.size() > 0 && !list.contains(object.get("id").valuePrim.value)) {
                continue;
            }
            log.warn("-----下发的设备Id:" + object.get("id").valuePrim.value);
            build_points(repository, object, infoValueSet, points);
            sdoList.add(object);
            build_object(object, infoValueSet);
        }
        return points;
    }

    private static void build_points(RepositoryImpl Repository, DataObject object, JSONObject infoValueSet, JSONArray points) {
        String id = (String) object.get("id").valuePrim.value;
        JSONObject obj = Repository.id2object.get(id);
        for (String key : infoValueSet.keySet()) {
            String infoValue = (String) obj.get(key);
            if (infoValue != null && infoValue.length() > 0) {
                int index_ = infoValue.lastIndexOf("-");
                String meter = infoValue.substring(0, index_);
                int funcid = Integer.parseInt(infoValue.substring(index_ + 1));
                JSONObject point = new JSONObject();
                if ("manualAutoSet".equals(key)) {
                    point.put("virtual", true);
                }
                point.put("meter", meter);
                point.put("funcid", funcid);
                point.put("data", infoValueSet.get(key));
                points.add(point);
            }
        }
    }

    private static void build_object(DataObject object, JSONObject infoValueSet) {
        for (String key : infoValueSet.keySet()) {
            DataValue sdv = object.get(key);
            if (sdv != null) {
                sdv.valuePrim = new DataPrimitive();
                sdv.valuePrim.value = infoValueSet.get(key);
            }
        }
    }


    /**
     * 处理返回值
     *
     * @param result
     * @param array
     */
    public static void disposeResult(JSONObject result, JSONArray array) {
        JSONArray success_points = new JSONArray();
        JSONArray failure_points = new JSONArray();
        for (int i = 0; i < array.size(); i++) {
            JSONObject point = array.getJSONObject(i);
            String status = (String) point.get("status");
            if ("finish:success".equals(status)) {
                success_points.add(point);
            } else {
                failure_points.add(point);
            }
        }
        if (failure_points.size() > 0) {
            result.put("result", "failure");
            result.put("failurePoints", failure_points);
            result.put("failureCount", failure_points.size());
        } else {
            result.put("result", "success");
        }
        if (success_points.size() > 0) {
            result.put("successCount", success_points.size());
            // result.put("success_points", success_points);
        }
    }
}
