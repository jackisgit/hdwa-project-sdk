package com.hdwa.sdk.utils;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.InstructControlParam;
import com.hdwa.sdk.entity.repository.RepositoryContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * 指令控制
 */
@Slf4j
public class ControlUtil {


    public static JSONArray setPoints(RepositoryImpl repository, InstructControlParam param) throws Exception {
        JSONArray result;
        Object valueObject = CalculateApiJsonUtil.getValueObject(repository, param.getPath());

        List<SceneDataObject> sdoList = new CopyOnWriteArrayList<>();
        if (valueObject instanceof SceneDataValue) {
            SceneDataValue currData = (SceneDataValue) valueObject;
            if (currData.value_array != null) {
                SceneDataValue detail = currData.parentObjectData.get("详情");
                result = setInner(repository, detail, currData.value_array.set, param.getInfoValueSet(), sdoList, param);
            } else if (currData.value_object != null) {
                result = setPoints(repository, currData.value_object, param.getInfoValueSet(), sdoList);
            } else {
                throw new Exception();
            }
        } else {
            SceneDataObject currData = (SceneDataObject) valueObject;
            result = setPoints(repository, currData, param.getInfoValueSet(), sdoList);
        }

        boolean all_success = true;
        for (int i = 0; i < result.size(); i++) {
            JSONObject item = result.getJSONObject(i);
            String status = (String) item.get("status");
            if (status == null || !status.endsWith("finish:success")) {
                all_success = false;
                break;
            }
        }

        if (all_success) {
            setControlValue(param.getPath(), param.getInfoValueSet());
        }
        // TODO: 2023/9/7 先去除日志记录 
        //ControlUtil.saveOperationLog(param.getUserId(), param.getUsername(), sdoList, param.getInfoValueSet(), result);
        return result;
    }


    public static void setControlValue(JSONArray path, JSONObject infoValueSet) {
        JSONObject exist_value = RepositoryContainer.RepositoryProject.controlValueMap.putIfAbsent(path.toJSONString(), infoValueSet);
        if (exist_value != null) {
            for (String info_code : infoValueSet.keySet()) {
                exist_value.put(info_code, infoValueSet.get(info_code));
            }
        }
    }

    public static JSONObject getControlValue(JSONArray path) {
        JSONObject result = RepositoryContainer.RepositoryProject.controlValueMap.get(path.toString());
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
    public static void saveOperationLog(String userId, String userName, List<SceneDataObject> sdoList, JSONObject infoValueSet, JSONArray points) {
        try {
            JSONObject postParam = new JSONObject();
            postParam.put("groupCode", RepositoryContainer.RepositoryProject.groupCode);
            postParam.put("projectId", RepositoryContainer.RepositoryProject.projectId);
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
                SceneDataObject sdo = sdoList.get(0);
                postParam.put("objId", sdo.get("id").value_prim.value);
            } else {
                JSONArray objs = new JSONArray();
                for (SceneDataObject sdo : sdoList) {
                    objs.add(sdo.get("id").value_prim.value);
                }
                postParam.put("objs", objs);
            }
            {
                SceneDataObject sdo = sdoList.get(0);
                Object objType = sdo.get("objType").value_prim.value;
                Object objName = sdo.get("localName").value_prim.value;
                Object systemCode = null;
                String classCode = (String) sdo.get("classCode").value_prim.value;
                Object belongSystem = sdo.get("所属场景") != null ? sdo.get("所属场景").value_prim.value : null;

                List<SceneDataObject> infoList = RepositoryContainer.instance.infoArrayDic.get(classCode).set;
                postParam.put("objType", objType);
                postParam.put("objName", objName);
                postParam.put("systemCode", systemCode);
                postParam.put("classCode", classCode);
                postParam.put("functionType", "remoteControl");
                StringBuilder sb = new StringBuilder();
                for (String key : infoValueSet.keySet()) {
                    Object infoValue = infoValueSet.get(key);
                    SceneDataObject infoDef = null;
                    for (SceneDataObject infoDefInner : infoList) {
                        String code = (String) infoDefInner.get("code").value_prim.value;
                        if (code.equals(key)) {
                            infoDef = infoDefInner;
                            break;
                        }
                    }
                    String infoName = key;
                    if (infoDef != null) {
                        infoName = (String) infoDef.get("name").value_prim.value;
                        infoValue = ControlUtil.value2CanRead(infoDef, infoValue);
                    }
                    sb.append("[").append(infoName).append("]").append("设为：").append("[").append(infoValue).append("]");
                    sb.append(";");
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
                postParam.put("operateDetail", "【" + belongSystem + "】：" + objName + "-" + sb);
                postParam.put("sourceType", 1);
            }
            //saveOperationLog(postParam);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    public static Object value2CanRead(SceneDataObject infoDef, Object infoValue) {
        if (infoDef.containsKey("dataSource")) {
            JSONArray dataSource = (JSONArray) infoDef.get("dataSource").value_prim.value;
            for (Object o : dataSource) {
                JSONObject item = (JSONObject) o;
                String code = item.getString("code");
                double value1 = Double.parseDouble(infoValue.toString());
                double value2 = Double.parseDouble(code);
                if (value1 == value2) {
                    String name = item.getString("name");
                    return name;
                }
            }
        }
        return infoValue;
    }


    /**
     * 保存日志
     *
     * @param postParam
     */
    private static void saveOperationLog(JSONObject postParam) {

    /*    String post_url = Constant.zkt_control_url + "/operationLog/saveOperationLog";
        String post_result = HttpClientUtil.instance("zkt_control").post(post_url, postParam.toJSONString());
        JSONObject result = JSON.parseObject(post_result);
        log.debug(result.toJSONString());*/
    }

    private static JSONArray setPoints(RepositoryImpl Repository, SceneDataObject object, JSONObject infoValueSet, List<SceneDataObject> sdoList)
            throws Exception {
        JSONArray result = null;
        //系统级别只下自己的控制点位
        if (object.parentObjectData != null && object.parentObjectData.get("名称") != null) {
            SceneDataValue sceneDataValue = object.parentObjectData.get("名称");
            if (sceneDataValue.value_prim != null) {
                SceneDataPrimitive sceneDataPrimitive = sceneDataValue.value_prim;
                if (sceneDataPrimitive.value != null) {
                    String name = sceneDataPrimitive.value.toString();
                    if (name.contains("冷源") || name.contains("热源")) {
                        result = setInner(Repository, object, infoValueSet, sdoList);
                    }
                }
            }
        } else if (object.containsKey("清单")) {//控制系统下的设备清单
            SceneDataValue list = object.get("清单");
            SceneDataValue detail = object.get("详情");
            result = setInner(Repository, detail, list.value_array.set, infoValueSet, sdoList, null);
        } else {//控制单设备
            result = setInner(Repository, object, infoValueSet, sdoList);
        }
        return result;
    }

    private static JSONArray setInner(RepositoryImpl Repository, SceneDataObject object, JSONObject infoValueSet, List<SceneDataObject> sdoList)
            throws Exception {
        JSONArray points = new JSONArray();
        build_points(Repository, object, infoValueSet, points);
        sdoList.add(object);
        build_object(object, infoValueSet);
        return points;
    }

    private static JSONArray setInner(RepositoryImpl repository, SceneDataValue detail, List<SceneDataObject> objectArray, JSONObject infoValueSet,
                                      List<SceneDataObject> sdoList, InstructControlParam params) throws Exception {
        if (detail != null) {
            log.warn("-----下发时有详情:" + detail.value_object);
            build_object(detail.value_object, infoValueSet);
        }
        JSONArray points = new JSONArray();
        JSONObject resultJsonObject = FilterUtil.postPage(repository, JSONObject.parseObject(params.toString()));

        //筛选真实要下发的数据
        List<Object> list = ((List<Map>) resultJsonObject.get("content")).stream().map(map -> map.get("id")).collect(Collectors.toList());
        for (SceneDataObject object : objectArray) {
            //真实下发的id包含清单就下发
            if (list.size() > 0 && !list.contains(object.get("id").value_prim.value)) {
                continue;
            }
            log.warn("-----下发的设备:" + object.get("id").value_prim.value);
            build_points(repository, object, infoValueSet, points);
            sdoList.add(object);
            build_object(object, infoValueSet);
        }
        return points;
    }

    private static void build_points(RepositoryImpl Repository, SceneDataObject object, JSONObject infoValueSet, JSONArray points) {
        String id = (String) object.get("id").value_prim.value;
        JSONObject obj = Repository.id2object.get(id);
        for (String key : infoValueSet.keySet()) {
            String infoValue = (String) obj.get(key);
            if (infoValue != null && infoValue.length() > 0) {
                int index_ = infoValue.lastIndexOf("-");
                String meter = infoValue.substring(0, index_);
                int funcid = Integer.parseInt(infoValue.substring(index_ + 1));
                JSONObject point = new JSONObject();
                if (key.equals("manualAutoSet")) {
                    point.put("virtual", true);
                }
                point.put("meter", meter);
                point.put("funcid", funcid);
                point.put("data", infoValueSet.get(key));
                points.add(point);
            }
        }
    }

    private static void build_object(SceneDataObject object, JSONObject infoValueSet) {
        for (String key : infoValueSet.keySet()) {
            SceneDataValue sdv = object.get(key);
            if (sdv != null) {
                sdv.value_prim = new SceneDataPrimitive();
                sdv.value_prim.value = infoValueSet.get(key);
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
            if (status.equals("finish:success")) {
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
