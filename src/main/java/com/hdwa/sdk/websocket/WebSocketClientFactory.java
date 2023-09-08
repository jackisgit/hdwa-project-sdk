package com.hdwa.sdk.websocket;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ExecutorBuilder;
import com.hdwa.sdk.config.CommonConst;
import com.hdwa.sdk.service.AlarmHandleServiceImpl;
import com.redxun.core.util.alarm.GZIPCompressUtil;
import com.redxun.core.util.alarm.LockUtil;
import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
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
            .setCorePoolSize(5)
            .setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(102400))
            .setHandler(new ThreadPoolExecutor.AbortPolicy())
            .build();
    
    @Autowired
    private AlarmHandleServiceImpl alarmHandleService;
    
    AtomicInteger total = new AtomicInteger(0);
    
    private WebSocketClient outCallWebSocketClientHolder;

    public WebSocketClient getOutCallWebSocketClientHolder() {
        return outCallWebSocketClientHolder;
    }

    public void setOutCallWebSocketClientHolder(WebSocketClient outCallWebSocketClientHolder) {
        this.outCallWebSocketClientHolder = outCallWebSocketClientHolder;
    }

    /**
     * 创建websocket对象
     */
    private WebSocketClient createNewWebSocketClient() throws URISyntaxException {
        log.info("iot-project连接地址为:[{}]", CommonConst.websocket);
        WebSocketClient webSocketClient = new WebSocketClient(new URI(CommonConst.websocket)) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
            }

            @Override
            public void onMessage(String msg) {
                String message = msg;
                if (CommonConst.compress) {
                    try {
                        byte[] byteArrayMsg = GZIPCompressUtil.uncompress(msg.getBytes(StandardCharsets.ISO_8859_1));
                        message = new String(byteArrayMsg);
                    } catch (Exception e) {
                        log.error("解密失败！", e);
                    }
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
                    String finalMessage = message;
                    executor.execute(() -> {
                        try {
                            alarmHandleService.handleIOTData(finalMessage);
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
                log.warn("关闭连接,code[{}],reson[{}],remote[{}]", code, reason, remote);
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
                log.info("关闭旧的websocket连接");
                oldOutCallWebSocketClientHolder.close();
            }

            log.info("打开新的websocket连接，并进行认证");
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
        log.info("websocket向服务端发送消息，消息为：{}", message);
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
     * 启动专门的定时任务去自动断开重新连接（10秒钟监测一次）
     */
    @Scheduled(initialDelay = 20000, fixedDelay = 60000)
    public void holderConnected() {
        try {
            WebSocketClient outCallWebSocketClientHolder = this.getOutCallWebSocketClientHolder();
            if (null == outCallWebSocketClientHolder) {
                log.info("当前连接还未建立");
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