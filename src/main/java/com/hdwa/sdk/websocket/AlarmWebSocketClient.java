package com.hdwa.sdk.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.AlarmJob;
import com.hdwa.sdk.utils.AlarmUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 报警数据采集
 */
@Slf4j
public class AlarmWebSocketClient extends WebSocketClient {

    /**
     * WebSocket连接地址
     */
    public URI url;

    /**
     * 报警服务url
     */
    private final String alarmUrl;

    /**
     * 项目id
     */
    private final String projectId;

    /**
     * 集团编码
     */
    private final String groupCode;

    ThreadPoolExecutor executor = new ThreadPoolExecutor(4, 8, 10,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(),
            Executors.defaultThreadFactory()
    );


    public AlarmWebSocketClient(URI url, String alarmUrl, String projectId, String groupCode) {
        super(url);
        this.url = url;
        this.alarmUrl = alarmUrl;
        this.projectId = projectId;
        this.groupCode = groupCode;
    }

    @Override
    public void onOpen(ServerHandshake arg0) {
        log.warn("*****alarmWebSocket连接已打开: " + url.toString());
        try {
            RepositoryImpl repository = DataContainer.projectMap.get(projectId);
            JSONObject AlarmJob = new JSONObject();
            AlarmJob.put("type", "refresh");
            DataContainer.alarmBuffer.offer(AlarmJob, 16384);
            JSONArray content = new JSONArray();
            try {
                content = AlarmUtil.alarmRefresh(projectId, groupCode, alarmUrl, repository);
            } catch (Exception e) {
                log.error(e.getMessage(), e);
            }
            AlarmJob.put("Content", content);
            log.warn("*****查询到报警数据: " + content.size());
            //多线程处理报警
            executor.execute(new AlarmJob(projectId));
        } catch (Exception e) {
            log.error("*****alarmWebSocket打开报警连接操作时异常", e);
        }
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        log.warn("*****alarmWebSocket连接已关闭: " + url.toString());
    }

    @Override
    public void onError(Exception arg0) {
        log.error("*****alarmWebSocket连接错误: " + url.toString());
    }

    @Override
    public void onMessage(String arg0) {
        try {
            log.warn("*****接收到报警处理数据：" + arg0);
            RepositoryImpl repository = DataContainer.projectMap.get(projectId);
            JSONObject alarm = (JSONObject) JSON.parse(arg0);
            if ((Integer) alarm.get("pushType") == 3) {
                String alarmId = (String) alarm.get("alarmId");
                AlarmUtil.processOrderDesc(alarmId, alarm);
            } else {
                AlarmUtil.updateAlarm(alarm, repository);
                AlarmUtil.processAlarm(alarm);
            }
        } catch (Exception e) {
            log.error("*****alarmWebSocket报警数据解析异常", e);
        }

        //多线程处理报警
        executor.execute(new AlarmJob(projectId));
    }
}
