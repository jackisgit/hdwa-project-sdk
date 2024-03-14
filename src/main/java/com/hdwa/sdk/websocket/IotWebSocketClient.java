package com.hdwa.sdk.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.entity.scene.DataPrimitive;
import com.hdwa.sdk.utils.CustomThreadFactory;
import com.hdwa.sdk.utils.IotJob;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Date;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

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
    ThreadPoolExecutor executor = new ThreadPoolExecutor(8, 16, 60,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            new CustomThreadFactory("iot-threadPool")
    );
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

    RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);

    @Override
    public void onMessage(String arg0) {
        String[] splits = ((JSONObject) JSON.parse(arg0)).getString(BaseDecConstant.DATA).split(";");
        //仪表号
        String meter = splits[1];
        //功能号
        String funcId = splits[2];
        //采集值
        String value = splits[3];
        //点位
        String point = meter + "-" + funcId;

        //没绑点数据不接收
        if (repository == null || repository.point2ObjectInfoList.get(point) == null) {
            return;
        }

        count++;
        Date currTime = new Date();
        if (currTime.getTime() / (1000L * 60) != lastTime.getTime() / (1000L * 60)) {
            lastTime = currTime;
            log.warn("*****iotWebSocket-1分钟接收到数据数量: " + count);
            count = 0;
        }
        try {
            //采集值处理
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

            boolean valueEqual;
            DataPrimitive data = DataContainer.point2sdv.get(point);
            //没有点位值
            if (data == null || data.value == null) {
                valueEqual = true;
            } else {
                valueEqual = !valueNew.equals(data.value);
            }

            // 改变的值才需要计算
            if (valueEqual) {
                //新的值放进来
                data = new DataPrimitive();
                data.change = true;
                data.value = valueNew;
                DataContainer.sdv2point.put(data, point);
                DataContainer.point2sdv.put(point, data);
                //多线程解析数据
                executor.execute(new IotJob(point, repository));
            }
        } catch (Exception e) {
            log.error("*****iotWebSocket数据解析异常", e);
        }
    }
}
