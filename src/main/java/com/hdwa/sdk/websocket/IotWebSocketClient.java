package com.hdwa.sdk.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.SceneDataPrimitive;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Date;

/**
 * iot实施数据采集
 */
@Slf4j
public class IotWebSocketClient extends WebSocketClient {

    /**
     * WebSocket连接地址
     */
    private final URI url;

    /**
     * 项目id
     */
    private final String projectId;

    /**
     * 统计时间
     */
    private Date lastTime = new Date();

    /**
     * 统计数量
     */
    private int count = 0;

    public IotWebSocketClient(URI url, String projectId) {
        super(url);
        this.url = url;
        this.projectId = projectId;
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.warn("*****iotWebSocket连接已打开: " + url.toString());
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        //log.warn("*****iotWebSocket连接已关闭: " + url.toString());
    }

    @Override
    public void onError(Exception arg0) {
        //log.error("*****iotWebSocket连接错误: " + url.toString(), arg0);
    }


    @Override
    public void onMessage(String arg0) {
        //RepositoryImpl repository = DataContainer.projectMap.get(projectId);
        count++;
        Date currTime = new Date();
        if (currTime.getTime() / (1000L * 60) != lastTime.getTime() / (1000L * 60)) {
            lastTime = currTime;
            log.warn("*****iotWebSocket-1分钟接收到数据数量: " + count);
            count = 0;
        }

        JSONObject json = (JSONObject) JSON.parse(arg0);
        //WebSocketUtil.ProcessIOTReceived(json);
        String type = json.getString(BaseDecConstant.TYPE);
        String[] splits = json.getString(BaseDecConstant.DATA).split(";");
        //仪表号
        String meter = splits[1];
        //功能号
        String funcId = splits[2];
        //采集值
        String value = splits[3];
        //点位
        String point = meter + "-" + funcId;

        if (type.equals("iot") || type.equals("text")) {
            try {
                SceneDataPrimitive sdvInner = new SceneDataPrimitive();
                sdvInner.change = true;
                SceneDataPrimitive exist_sdv = DataContainer.point2sdv.putIfAbsent(point, sdvInner);
                if (exist_sdv == null) {
                    DataContainer.sdv2point.putIfAbsent(sdvInner, point);
                }
                SceneDataPrimitive data = DataContainer.point2sdv.get(point);
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
                    data.value = valueNew;
                    // 加入计算队列
                  /*  if (!valueEqual) {
                        repository.processIotData(point);
                    }*/
                } else {
                    data.value = value;
                }
            } catch (Exception e) {
                log.error("*****iotWebSocket iot和text数据解析异常", e);
            }

        } else if (type.equals("pointset")) {
            try {
                SceneDataPrimitive sdvInner = new SceneDataPrimitive();
                sdvInner.change = true;
                SceneDataPrimitive exist_sdv = DataContainer.set2sdv.putIfAbsent(point, sdvInner);
                if (exist_sdv == null) {
                    DataContainer.sdv2set.putIfAbsent(sdvInner, point);
                }
                SceneDataPrimitive data = DataContainer.set2sdv.get(point);
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
                data.value = valueNew;
                // 加入计算队列
                //if (!valueEqual) {
                //    repository.processIotData(point);
                //}
            } catch (Exception e) {
                log.error("*****iotWebSocket设置参数数据解析异常", e);
            }
        }
    }
}
