package com.hdwa.alarm.websocket;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ExecutorBuilder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.alarm.config.CommonConst;
import com.hdwa.alarm.service.AlarmHandleServiceImpl;
import com.hdwa.alarm.util.LockUtil;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * WebSocketClient连接，接受IOT采集数据
 **/
@Component
@Slf4j
public class WebSocketClientFactory {

    ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(8)
            .setMaxPoolSize(16)
            .setWorkQueue(new LinkedBlockingQueue<>(102400))
            .setHandler(new ThreadPoolExecutor.AbortPolicy())
            .build();
    AtomicInteger total = new AtomicInteger(0);

    /**
     * iot-project服务的webSocket地址
     */
    @Value("${url.iotWebSocket}")
    private String iotWebSocketUrl;

    @Autowired
    private AlarmHandleServiceImpl alarmHandleService;

    private WebSocketClient outCallWebSocketClientHolder;

    public WebSocketClient getOutCallWebSocketClientHolder() {
        return outCallWebSocketClientHolder;
    }

    public void setOutCallWebSocketClientHolder(WebSocketClient outCallWebSocketClientHolder) {
        this.outCallWebSocketClientHolder = outCallWebSocketClientHolder;
    }

    RepositoryImpl repository = DataContainer.projectMap.get(BaseDecConstant.CURRENT_PROJECT_ID);

    /**
     * 创建websocket对象
     */
    private WebSocketClient createNewWebSocketClient() throws URISyntaxException {
        String url = iotWebSocketUrl + "?projectId=" + CommonConst.projectId.substring(2) + "&type=pointset,iot&getFullData=true";
        log.warn("*****alarm-iot-project连接地址为:[{}]", url);
        WebSocketClient webSocketClient = new WebSocketClient(new URI(url)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
            }

            @Override
            public void onMessage(String msg) {
                String[] splits = ((JSONObject) JSON.parse(msg)).getString(BaseDecConstant.DATA).split(";");
                //仪表号
                String meter = splits[1];
                //功能号
                String funcId = splits[2];
                //点位
                String point = meter + "-" + funcId;
                //没绑点不处理数据
                if (repository == null || repository.point2ObjectInfoList.get(point) == null) {
                    return;
                }
                try {
                    while (!LockUtil.getInstance().isExecute()) {
                        try {
                            log.warn("等待获取锁....");
                            LockUtil.getInstance().lock.lockInterruptibly();
                            log.warn("已经获取锁....");
                        } catch (Exception e) {
                            log.error("数据加锁处理发生异常", e);
                        } finally {
                            LockUtil.getInstance().lock.unlock();
                        }
                    }
                    executor.execute(() -> {
                        try {
                            alarmHandleService.handleIOTData(msg);
                        } catch (Exception e) {
                            log.error("数据处理失败", e);
                        }
                    });
                } catch (Exception e) {
                    log.error("数据处理失败", e);
                }

                total.getAndIncrement();
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.warn("*****alarm-关闭连接,code[{}],reson[{}],remote[{}]", code, reason, remote);
            }

            @Override
            public void onError(Exception e) {
                log.error("连接异常", e);
            }
        };
        //默认为60 ，阻塞时间稍长会超时，此处改成 1200
        webSocketClient.setConnectionLostTimeout(1200);
        webSocketClient.connect();
        return webSocketClient;
    }

    /**
     * 项目启动或连接失败的时候打开新链接,进行连接认证
     * 需要加同步，不然会创建多个连接
     */
    public synchronized WebSocketClient retryOutCallWebSocketClient() {
        try {
            // 关闭旧的websocket连接, 避免占用资源
            WebSocketClient oldOutCallWebSocketClientHolder = this.getOutCallWebSocketClientHolder();
            if (null != oldOutCallWebSocketClientHolder) {
                log.warn("*****alarm-关闭旧的websocket连接");
                oldOutCallWebSocketClientHolder.close();
            }

            log.warn("*****alarm-打开新的websocket连接，并进行认证");
            WebSocketClient webSocketClient = this.createNewWebSocketClient();
            // 每次创建新的就放进去
            this.setOutCallWebSocketClientHolder(webSocketClient);

            return webSocketClient;
        } catch (URISyntaxException e) {
            log.error("retryOutCallWebSocketClient失败:", e);
        }

        return null;
    }

    /**
     * 发送消息
     * 注意： 要加超时设置，避免很多个都在同时超时占用资源
     */
    public void sendMsg(WebSocketClient webSocketClient, String message) {
        log.warn("websocket向服务端发送消息，消息为：{}", message);
        TimeInterval timer = DateUtil.timer();
        while (!webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
            log.debug("正在建立通道，请稍等");
            if (timer.interval() >= 5000) {
                log.error("超过5秒钟还未打开连接，超时，不再等待");
                return;
            }
        }
        webSocketClient.send(message);
    }

    /**
     * 启动专门的定时任务去自动断开重新连接（60秒钟监测一次）
     */
    @Scheduled(initialDelay = 20000, fixedDelay = 60000)
    public void holderConnected() {
        try {
            WebSocketClient outCallWebSocketClientHolder = this.getOutCallWebSocketClientHolder();
            if (null == outCallWebSocketClientHolder) {
                log.warn("当前连接还未建立");
                return;
            }
            if (!outCallWebSocketClientHolder.isOpen()) {
                log.error("连接已经断开，正在重新连接，当前状态为[{}]", outCallWebSocketClientHolder.getReadyState());
                if (outCallWebSocketClientHolder.getReadyState().equals(WebSocket.READYSTATE.NOT_YET_CONNECTED)) {
                    outCallWebSocketClientHolder.connect();
                } else if (outCallWebSocketClientHolder.getReadyState().equals(WebSocket.READYSTATE.CLOSING) || outCallWebSocketClientHolder.getReadyState().equals(WebSocket.READYSTATE.CLOSED)) {
                    outCallWebSocketClientHolder.reconnect();
                }
            }
        } catch (Exception e) {
            log.error("连接发生异常，尝试重新连接", e);
            //重连异常后删除旧的连接并重新连接
            retryOutCallWebSocketClient();
        }
    }
}