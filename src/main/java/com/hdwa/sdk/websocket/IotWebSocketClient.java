package com.hdwa.sdk.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.repository.PathDataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Date;

@Slf4j
public class IotWebSocketClient extends WebSocketClient {

    /**
     * WebSocket连接地址
     */
    public URI url;

    /**
     * 项目id
     */
    private final String projectId;

    /**
     * 数据仓库
     */
    private RepositoryImpl repositoryNew;

    public IotWebSocketClient(URI url, String projectId) {
        super(url);
        this.url = url;
        this.projectId = projectId;
    }


    public IotWebSocketClient(URI url, String projectId, RepositoryImpl repository) {
        super(url);
        this.url = url;
        this.projectId = projectId;
        this.repositoryNew = repository;
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.warn("iotWebSocket连接已打开: " + url.toString());
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        log.warn("iotWebSocket连接已关闭: " + url.toString());
    }

    @Override
    public void onError(Exception arg0) {
        log.error("iotWebSocket连接错误: " + url.toString());
    }

    Date lastTime = new Date();
    int count = 0;

    /**
     * 接收上报数据 格式为{"data":"202208250851;HSWDBA2.3030015;64;23","type":"iot"}
     */
    @Override
    public void onMessage(String arg0) {
        RepositoryImpl repository = PathDataContainer.projectMap.get(projectId);
        count++;
        Date currTime = new Date();
        if (currTime.getTime() / (1000L * 60) != lastTime.getTime() / (1000L * 60)) {
            lastTime = currTime;
            log.warn("iotWebSocket-1分钟接收到数据数量: " + count);
            count = 0;
        }

        JSONObject json = (JSONObject) JSON.parse(arg0);
        WebSocketUtil.ProcessIOTReceived(json);
        String type = json.getString("type");
        if (type.equals("iot") || type.equals("text")) {
            String[] splits = json.getString("data").split(";");

            for (int i = 0; i < splits.length; i += 4) {
                String meter = splits[i + 1];//点位
                String funcid = splits[i + 2];//功能号
                String value = splits[i + 3];//采集值
                String point = meter + "-" + funcid;
                try {
                    //原始场景数据 SceneDataPrimitive
                    SceneDataPrimitive sdvInner = new SceneDataPrimitive();
                    sdvInner.change = true;
                    SceneDataPrimitive exist_sdv = PathDataContainer.point2sdv.putIfAbsent(point, sdvInner);
                    if (exist_sdv == null) {
                        PathDataContainer.sdv2point.putIfAbsent(sdvInner, point);
                    }
                    SceneDataPrimitive data = PathDataContainer.point2sdv.get(point);
                    if (type.equals("iot")) {
                        if (value.endsWith(".0")) {
                            value = value.substring(0, value.length() - ".0".length());
                        }
                        Object valueNew;
                        try {
                            valueNew = Long.parseLong(value);
                        } catch (Exception e1) {
                            try {
                                valueNew = Double.parseDouble(value);
                            } catch (Exception e) {
                                valueNew = value;
                            }
                        }
                        boolean valueEqual = valueNew.equals(data.value);
                        data.value = valueNew;
                        // 加入计算队列
                        if (!valueEqual) {
                            repository.ProcessIOT(point);
                        }
                    } else {
                        data.value = value;
                    }
                } catch (Exception e) {
                    log.error("iot和text数据解析异常", e);
                }
            }
        } else if (type.equals("pointset")) {
            String message = json.getString("data");
            String[] splits = message.split(";");
            count++;
            int i = 0;
            String meter = splits[i + 1];
            String funcid = splits[i + 2];
            String value = splits[i + 3];
            String point = meter + "-" + funcid;
            try {
                SceneDataPrimitive sdvInner = new SceneDataPrimitive();
                sdvInner.change = true;
                SceneDataPrimitive exist_sdv = PathDataContainer.set2sdv.putIfAbsent(point, sdvInner);
                if (exist_sdv == null) {
                    PathDataContainer.sdv2set.putIfAbsent(sdvInner, point);
                }
                SceneDataPrimitive data = PathDataContainer.set2sdv.get(point);
                if (value.endsWith(".0")) {
                    value = value.substring(0, value.length() - ".0".length());
                }
                Object valueNew;
                try {
                    valueNew = Long.parseLong(value);
                } catch (Exception e1) {
                    try {
                        valueNew = Double.parseDouble(value);
                    } catch (Exception e) {
                        valueNew = value;
                    }
                }
                boolean valueEqual = valueNew.equals(data.value);
                data.value = valueNew;
                // 加入计算队列
                if (!valueEqual) {
                    repository.ProcessIOT(point);
                }
            } catch (Exception e) {
                log.error("设置参数数据解析异常", e);
            }
        }
    }
}
