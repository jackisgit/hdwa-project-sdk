package com.hdwa.sdk.websocket;

import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.ObjectInfo;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataObject;
import com.hdwa.sdk.entity.scene.SceneDataValue;
import com.hdwa.sdk.utils.AttributeFilteringUtil;
import com.hdwa.sdk.utils.PathUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

@Slf4j
public class WebSocketUtil {
    public static ExecutorService executor = ExecutorBuilder.create().setCorePoolSize(4).setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(102400)).setHandler(new ThreadPoolExecutor.AbortPolicy()).build();
    public static Map<String, Object> idMap = new ConcurrentHashMap<String, Object>();

    public static Map<String, Map<String, Boolean>> objId2idList = new HashMap<>(16);
    public static Map<String, Map<String, Boolean>> id2objIdList = new HashMap<>(16);

    public static Map<String, Map<String, Boolean>> objInfoId2idList = new HashMap<>(16);
    public static Map<String, Map<String, Boolean>> id2objInfoIdList = new HashMap<>(16);

    public static Map<String, Map<String, Object>> id2objId2Tag = new HashMap<>(16);

    public static Map<String, Map<String, Boolean>> path2idList = new HashMap<>(16);
    public static Map<String, List<JSONArray>> id2pathList = new HashMap<>(16);

    public static Map<String, Channel> id2Channel = new HashMap<>(16);


    public static void Send(String id, Object content) {
        try {
            Channel channel = id2Channel.get(id);
            if (channel != null) {
                String sendString = JSONObject.toJSONString(content, SerializerFeature.WriteMapNullValue);
                log.info("WebSocket send " + channel.remoteAddress().toString() + "\t" + sendString);
                channel.writeAndFlush(new TextWebSocketFrame(sendString));
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    // 支持路径通配符*
    public static void ProcessFirstSend_path(SceneDataObject objectData, String id, JSONArray sendArray) {
        for (String key : objectData.keySetSelf()) {
            if (AttributeFilteringUtil.containsKey(key)) {
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
                        if (!sdv.value_array.isSingleValueSet) {
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
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private static void SendBatch(String id, JSONArray sendArray) {
        if (sendArray.size() > 16) {
            Send(id, sendArray);
            sendArray.clear();
        }
    }

    public static void SendAndClear(String id, JSONArray sendArray) {
        if (sendArray.size() > 0) {
            Send(id, sendArray);
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
        Runnable runnable = () -> {
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
            Runnable runnable = () -> {
                RepositoryImpl Repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);
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
            };
            WebSocketUtil.executor.execute(new Thread(runnable));
        } catch (Exception e) {
            log.error("解析iot数据出现异常", e);
        }
    }
}
