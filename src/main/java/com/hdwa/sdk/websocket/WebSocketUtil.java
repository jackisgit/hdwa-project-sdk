package com.hdwa.sdk.websocket;

import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.repository.ObjectInfo;
import com.hdwa.sdk.entity.repository.RepositoryContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataSet;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.ComputeUtil;
import com.hdwa.sdk.utils.KeywordUtil;
import com.hdwa.sdk.utils.PathUtil;
import com.hdwa.sdk.utils.RWDUtil;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class WebSocketUtil {
    public static ExecutorService executor = ExecutorBuilder.create().setCorePoolSize(4).setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(102400)).setHandler(new ThreadPoolExecutor.AbortPolicy()).build();
    public static Map<String, Object> idMap = new ConcurrentHashMap<String, Object>();

    public static Map<String, Map<String, Boolean>> objId2idList = new ConcurrentHashMap<String, Map<String, Boolean>>();
    public static Map<String, Map<String, Boolean>> id2objIdList = new ConcurrentHashMap<String, Map<String, Boolean>>();

    public static Map<String, Map<String, Boolean>> objInfoId2idList = new ConcurrentHashMap<String, Map<String, Boolean>>();
    public static Map<String, Map<String, Boolean>> id2objInfoIdList = new ConcurrentHashMap<String, Map<String, Boolean>>();

    public static Map<String, Map<String, Object>> id2objId2Tag = new ConcurrentHashMap<String, Map<String, Object>>();

    public static Map<String, Map<String, Boolean>> path2idList = new ConcurrentHashMap<String, Map<String, Boolean>>();
    public static Map<String, List<JSONArray>> id2pathList = new ConcurrentHashMap<String, List<JSONArray>>();

    public static synchronized void ProcessReceive(String id, Object ContentJSON) {
        ProcessDisconnected(id);

        idMap.put(id, ContentJSON);
        Map<String, Boolean> objIdList = new ConcurrentHashMap<String, Boolean>();
        id2objIdList.put(id, objIdList);
        Map<String, Boolean> objInfoIdList = new ConcurrentHashMap<String, Boolean>();
        id2objInfoIdList.put(id, objInfoIdList);
        Map<String, Object> objId2Tag = new ConcurrentHashMap<String, Object>();
        id2objId2Tag.put(id, objId2Tag);
        List<JSONArray> pathList = new CopyOnWriteArrayList<JSONArray>();
        id2pathList.put(id, pathList);

        JSONArray objArray = (JSONArray) ContentJSON;
        for (int i = 0; i < objArray.size(); i++) {
            JSONObject objJSON = objArray.getJSONObject(i);
            // 对象IOT订阅
            if (objJSON.containsKey("objId")) {
                String objId = (String) objJSON.get("objId");
                if (objJSON.containsKey("websocket_tag")) {
                    objId2Tag.putIfAbsent(objId, objJSON.get("websocket_tag"));
                }
                if (objJSON.containsKey("infoCodeArray")) {
                    JSONArray infoCodeArray = (JSONArray) objJSON.get("infoCodeArray");
                    for (int ii = 0; ii < infoCodeArray.size(); ii++) {
                        String infoCode = (String) infoCodeArray.get(ii);
                        String objInfoId = objId + "-" + infoCode;
                        objInfoId2idList.putIfAbsent(objInfoId, new ConcurrentHashMap<String, Boolean>());
                        objInfoId2idList.get(objInfoId).putIfAbsent(id, true);
                        objInfoIdList.putIfAbsent(objInfoId, true);
                    }
                } else {
                    objId2idList.putIfAbsent(objId, new ConcurrentHashMap<String, Boolean>());
                    objId2idList.get(objId).putIfAbsent(id, true);
                    objIdList.putIfAbsent(objId, true);
                }
            } else if (objJSON.containsKey("path")) {
                JSONArray pathArray = (JSONArray) objJSON.get("path");
                pathList.add(pathArray);
                path2idList.putIfAbsent(pathArray.toJSONString(), new ConcurrentHashMap<String, Boolean>());
                path2idList.get(pathArray.toJSONString()).putIfAbsent(id, true);
            }
        }

        ProcessFirstSend(id);
    }

    public static synchronized void ProcessDisconnected(String id) {
        for (String objId : objId2idList.keySet()) {
            Map<String, Boolean> idList = objId2idList.get(objId);
            idList.remove(id);
        }
        id2objIdList.remove(id);

        for (String objInfoId : objInfoId2idList.keySet()) {
            Map<String, Boolean> idList = objInfoId2idList.get(objInfoId);
            idList.remove(id);
        }
        id2objInfoIdList.remove(id);

        id2objId2Tag.remove(id);

        for (String path : path2idList.keySet()) {
            Map<String, Boolean> idList = path2idList.get(path);
            idList.remove(id);
            if (idList.size() == 0) {
                path2idList.remove(path);
            }
        }
        id2pathList.remove(id);

        idMap.remove(id);
    }

    private static void ProcessFirstSend(String id) {
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    RepositoryImpl Repository = RepositoryContainer.instance;
                    Map<String, Object> objId2Tag = id2objId2Tag.get(id);

                    JSONArray sendArray = new JSONArray();
                    Map<String, JSONObject> sendObj = new ConcurrentHashMap<String, JSONObject>();
                    Map<String, Boolean> objIdList = id2objIdList.get(id);
                    Map<String, Boolean> objInfoIdList = id2objInfoIdList.get(id);
                    for (String objId : objIdList.keySet()) {
                        if (Repository.id2sdv.containsKey(objId)) {
                            SceneDataObject sdo = Repository.id2sdv.get(objId);
                            String classCode = (String) sdo.get("classCode").value_prim.value;
                            SceneDataSet infoArray = Repository.infoArrayDic.get(classCode);
                            String[] infoCodes = sdo.keySet().toArray(new String[0]);
                            for (String infoCode : infoCodes) {
                                SceneDataValue infoValue = sdo.get(infoCode);
                                if ((RWDUtil.isRunParam(infoArray.set, infoCode) || RWDUtil.isSetParam(infoArray.set, infoCode))
                                        && infoValue.value_prim != null && infoValue.value_prim.value != null) {
                                    if (!sendObj.containsKey(objId)) {
                                        sendObj.put(objId, new JSONObject());
                                    }
                                    JSONObject sendItem = sendObj.get(objId);
                                    sendItem.put(infoCode, infoValue.value_prim.value);
                                }
                            }
                        }
                    }
                    for (String objInfoId : objInfoIdList.keySet()) {
                        int index_ = objInfoId.indexOf('-');
                        String objId = objInfoId.substring(0, index_);
                        String infoCode = objInfoId.substring(index_ + 1);
                        if (Repository.id2sdv.containsKey(objId)) {
                            SceneDataObject sdo = Repository.id2sdv.get(objId);
                            String classCode = (String) sdo.get("classCode").value_prim.value;
                            SceneDataSet infoArray = Repository.infoArrayDic.get(classCode);
                            {
                                SceneDataValue infoValue = sdo.get(infoCode);
                                if ((RWDUtil.isRunParam(infoArray.set, infoCode) || RWDUtil.isSetParam(infoArray.set, infoCode))
                                        && infoValue.value_prim != null && infoValue.value_prim.value != null) {
                                    if (!sendObj.containsKey(objId)) {
                                        sendObj.put(objId, new JSONObject());
                                    }
                                    JSONObject sendItem = sendObj.get(objId);
                                    sendItem.put(infoCode, infoValue.value_prim.value);
                                }
                            }
                        }
                    }
                    for (String objId : sendObj.keySet()) {
                        JSONObject sendItem = sendObj.get(objId);
                        sendItem.put("id", objId);
                        if (objId2Tag.containsKey(objId)) {
                            sendItem.put("websocket_tag", objId2Tag.get(objId));
                        }
                        sendArray.add(sendItem);
                        // 发送
                        SendBatch(id, sendArray);
                    }

                    // path
                    // ProcessFirstSend_path(Repository, id, sendArray);
                    ProcessFirstSend_path(Repository.objectData, id, sendArray);

                    SendAndClear(id, sendArray);
                }
            };
            WebSocketUtil.executor.execute(new Thread(runnable));
        } catch (Exception e) {
            log.error("handlerSubMessage", e);
        }
    }

    // 严格匹配路径，已废弃
    public static void ProcessFirstSend_path(RepositoryImpl Repository, String id, JSONArray sendArray) {
        List<JSONArray> pathList = id2pathList.get(id);
        for (JSONArray pathArray : pathList) {
            Object valueObject = ComputeUtil.getValueObject(Repository, pathArray);
            if (valueObject instanceof SceneDataValue) {
                SceneDataValue currData = (SceneDataValue) valueObject;
                int depth = 1;
                // if (currData.rel_property != null) {
                // depth = Integer.parseInt(currData.rel_property.read_level);
                // }
                Object data = currData.toJSON(true, depth);
                JSONObject sendItem = new JSONObject();
                sendItem.put("path", pathArray);
                sendItem.put("data", data);
                sendArray.add(sendItem);
                // 发送
                SendBatch(id, sendArray);
            }
        }
    }

    // 支持路径通配符*
    public static void ProcessFirstSend_path(SceneDataObject objectData, String id, JSONArray sendArray) {
        for (String key : objectData.keySetSelf()) {
            if (KeywordUtil.containsKey(key)) {
                continue;
            }
            SceneDataValue sdv = objectData.get(key);
            ProcessFirstSend_path(sdv, id, sendArray);
        }
    }

    // 支持路径通配符*
    public static void ProcessFirstSend_path(SceneDataValue sdv, String id, JSONArray sendArray) {
        List<JSONArray> pathList = id2pathList.get(id);
        try {
            if (sdv.rel_property != null) {
                if (sdv.rel_property.propertyValueSchema.equals("JSONObject")) {
                    if (sdv.value_object != null) {
                        JSONArray pathArray = new JSONArray();
                        PathUtil.getDataPath(sdv, pathArray);
                        if (pathMatch(pathList, pathArray)) {
                            Object JSON = sdv.value_object.toJSON(1);
                            JSONObject sendItem = new JSONObject();
                            sendItem.put("path", pathArray);
                            sendItem.put("data", JSON);
                            sendArray.add(sendItem);
                            // 发送
                            SendBatch(id, sendArray);
                        }
                        ProcessFirstSend_path(sdv.value_object, id, sendArray);
                    }
                } else if (sdv.rel_property.propertyValueSchema.equals("JSONArray")) {
                    if (sdv.value_array != null) {
                        JSONArray pathArray = new JSONArray();
                        PathUtil.getDataPath(sdv, pathArray);
                        if (pathMatch(pathList, pathArray)) {
                            Object JSON = sdv.value_array.toJSON(1);
                            JSONObject sendItem = new JSONObject();
                            sendItem.put("path", pathArray);
                            sendItem.put("data", JSON);
                            sendArray.add(sendItem);
                            // 发送
                            SendBatch(id, sendArray);
                        }
                        if (sdv.value_array.isSingleValueSet) {
                        } else {
                            for (SceneDataObject sdoInner : sdv.value_array.set) {
                                ProcessFirstSend_path(sdoInner, id, sendArray);
                            }
                        }
                    }
                } else {
                    if (sdv.value_prim != null) {
                        JSONArray pathArray = new JSONArray();
                        PathUtil.getDataPath(sdv, pathArray);
                        if (pathMatch(pathList, pathArray)) {
                            Object JSON = sdv.value_prim.value;
                            JSONObject sendItem = new JSONObject();
                            sendItem.put("path", pathArray);
                            sendItem.put("data", JSON);
                            sendArray.add(sendItem);
                            // 发送
                            SendBatch(id, sendArray);
                        }
                    }
                }
            } else {
                if (sdv.value_array != null) {
                    if (sdv.value_array.isSingleValueSet) {
                        JSONArray pathArray = new JSONArray();
                        PathUtil.getDataPath(sdv, pathArray);
                        if (pathMatch(pathList, pathArray)) {
                            Object JSON = sdv.value_array.toJSON(1);
                            JSONObject sendItem = new JSONObject();
                            sendItem.put("path", pathArray);
                            sendItem.put("data", JSON);
                            sendArray.add(sendItem);
                            // 发送
                            SendBatch(id, sendArray);
                        }
                    }
                } else if (sdv.value_object != null) {
                } else if (sdv.value_prim != null) {
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void SendBatch(String id, JSONArray sendArray) {
        if (sendArray.size() > 16) {
            WebSocketChannelPool.Send(id, sendArray);
            sendArray.clear();
        }
    }

    public static void SendAndClear(String id, JSONArray sendArray) {
        if (sendArray.size() > 0) {
            WebSocketChannelPool.Send(id, sendArray);
            sendArray.clear();
        }
    }

    private static boolean pathMatch(List<JSONArray> pathList, JSONArray pathArray) {
        boolean match = false;
        for (JSONArray pathArrayInner : pathList) {
            if (pathArrayInner.size() != pathArray.size()) {
                continue;
            }
            boolean matchOne = true;
            for (int i = 0; i < pathArrayInner.size(); i++) {
                String item1 = pathArrayInner.getString(i);
                String item2 = pathArray.getString(i);
                if (!item1.equals("*") && !item1.equals(item2)) {
                    matchOne = false;
                    break;
                }
            }
            if (matchOne) {
                match = true;
                break;
            }
        }
        return match;
    }

    public static void ProcessComputeOccur(SceneDataValue sdv) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    JSONArray pathArray = new JSONArray();
                    PathUtil.getDataPath(sdv, pathArray);
                    int[] flagArray = new int[pathArray.size()];
                    while (true) {
                        JSONArray pathArrayInner = pathString(pathArray, flagArray);
                        if (path2idList.containsKey(pathArrayInner.toString())) {
                            Map<String, Boolean> idList = path2idList.get(pathArrayInner.toString());
                            JSONArray sendArray = new JSONArray();
                            JSONObject sendItem = new JSONObject();
                            sendItem.put("path", pathArray);
                            int depth = 1;
                            // if (sdv.rel_property != null) {
                            // depth = Integer.parseInt(sdv.rel_property.read_level);
                            // }
                            Object data = sdv.toJSON(true, depth);
                            sendItem.put("data", data);
                            sendArray.add(sendItem);
                            for (String id : idList.keySet()) {
                                SendAndClear(id, sendArray);
                            }
                        }
                        boolean all_0 = false;
                        for (int i = 0; i < flagArray.length; i++) {
                            if (flagArray[i] == 0) {
                                flagArray[i] = 1;
                                break;
                            } else {
                                flagArray[i] = 0;
                                if (i == flagArray.length - 1) {
                                    all_0 = true;
                                    break;
                                }
                            }
                        }
                        if (all_0) {
                            break;
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        WebSocketUtil.executor.execute(new Thread(runnable));
    }

    private static JSONArray pathString(JSONArray pathArray, int[] flagArray) {
        JSONArray result = new JSONArray();
        for (int i = 0; i < pathArray.size(); i++) {
            String pathItem = pathArray.getString(i);
            int flag = flagArray[i];
            result.add(flag == 0 ? pathItem : "*");
        }
        return result;
    }

    public static void ProcessIOTReceived(JSONObject json) {
        try {
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    RepositoryImpl Repository = RepositoryContainer.instance;
                    String type = json.getString("type");
                    if (type.equals("iot") || type.equals("text")) {
                        String message = json.getString("data");
                        String[] splits = message.split(";");
                        for (String id : idMap.keySet()) {
                            Map<String, Object> objId2Tag = id2objId2Tag.get(id);
                            try {
                                Map<String, Boolean> objIdList = id2objIdList.get(id);
                                Map<String, Boolean> objInfoIdList = id2objInfoIdList.get(id);
                                JSONArray sendArray = new JSONArray();
                                Map<String, JSONObject> sendObj = new ConcurrentHashMap<String, JSONObject>();
                                for (int i = 0; i < splits.length; i += 4) {
                                    // String time = splits[i + 0];
                                    String meter = splits[i + 1];
                                    String funcid = splits[i + 2];
                                    String value = splits[i + 3];
                                    Object valueObj = null;
                                    try {
                                        valueObj = Long.parseLong(value);
                                    } catch (Exception e) {
                                        try {
                                            valueObj = Double.parseDouble(value);
                                        } catch (Exception exp) {
                                            valueObj = value;
                                        }
                                    }
                                    String point = meter + "-" + funcid;
                                    if (!Repository.point2ObjectInfoList.containsKey(point)) {
                                        continue;
                                    }
                                    List<ObjectInfo> ObjectInfoList = Repository.point2ObjectInfoList.get(point);
                                    for (ObjectInfo ObjectInfo : ObjectInfoList) {
                                        if (objIdList.containsKey(ObjectInfo.objId)
                                                || objInfoIdList.containsKey(ObjectInfo.objId + "-" + ObjectInfo.infoCode)) {
                                            if (!sendObj.containsKey(ObjectInfo.objId)) {
                                                sendObj.put(ObjectInfo.objId, new JSONObject());
                                            }
                                            JSONObject sendItem = sendObj.get(ObjectInfo.objId);
                                            sendItem.put(ObjectInfo.infoCode, valueObj);
                                        }
                                    }
                                }
                                for (String objId : sendObj.keySet()) {
                                    JSONObject sendItem = sendObj.get(objId);
                                    sendItem.put("id", objId);
                                    if (objId2Tag.containsKey(objId)) {
                                        sendItem.put("websocket_tag", objId2Tag.get(objId));
                                    }
                                    sendArray.add(sendItem);
                                }
                                if (sendArray.size() > 0) {
                                    SendBatch(id, sendArray);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } else if (type.equals("pointset")) {
                        String message = json.getString("data");
                        String[] splits = message.split(";");
                        for (String id : idMap.keySet()) {
                            try {
                                Map<String, Boolean> objIdList = id2objIdList.get(id);
                                Map<String, Boolean> objInfoIdList = id2objInfoIdList.get(id);
                                JSONArray sendArray = new JSONArray();
                                Map<String, JSONObject> sendObj = new ConcurrentHashMap<String, JSONObject>();
                                for (int i = 0; i < splits.length; i += 4) {
                                    // String time = splits[i + 0];
                                    String meter = splits[i + 1];
                                    String funcid = splits[i + 2];
                                    String value = splits[i + 3];
                                    Object valueObj = null;
                                    try {
                                        valueObj = Long.parseLong(value);
                                    } catch (Exception e) {
                                        try {
                                            valueObj = Double.parseDouble(value);
                                        } catch (Exception exp) {
                                            valueObj = value;
                                        }
                                    }
                                    String point = meter + "-" + funcid;
                                    if (!Repository.set2ObjectInfoList.containsKey(point)) {
                                        continue;
                                    }
                                    List<ObjectInfo> ObjectInfoList = Repository.set2ObjectInfoList.get(point);
                                    for (ObjectInfo ObjectInfo : ObjectInfoList) {
                                        if (objIdList.containsKey(ObjectInfo.objId)
                                                || objInfoIdList.containsKey(ObjectInfo.objId + "" + ObjectInfo.infoCode)) {
                                            if (!sendObj.containsKey(ObjectInfo.objId)) {
                                                sendObj.put(ObjectInfo.objId, new JSONObject());
                                            }
                                            JSONObject sendItem = sendObj.get(ObjectInfo.objId);
                                            sendItem.put(ObjectInfo.infoCode, valueObj);
                                        }
                                    }
                                }
                                for (String objId : sendObj.keySet()) {
                                    JSONObject sendItem = sendObj.get(objId);
                                    sendItem.put("id", objId);
                                    sendArray.add(sendItem);
                                }
                                if (sendArray.size() > 0) {
                                    SendBatch(id, sendArray);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            };
            WebSocketUtil.executor.execute(new Thread(runnable));
        } catch (Exception e) {
            log.error("handlerSubMessage", e);
        }
    }
}
