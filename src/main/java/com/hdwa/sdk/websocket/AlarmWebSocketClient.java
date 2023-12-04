package com.hdwa.sdk.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.entity.repository.DataContainer;
import com.hdwa.sdk.entity.repository.RepositoryImpl;
import com.hdwa.sdk.utils.AlarmJob;
import com.hdwa.sdk.utils.AlarmUtil;
import lombok.extern.slf4j.Slf4j;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.util.Date;

/**
 * 报警数据采集
 */
@Slf4j
public class AlarmWebSocketClient extends WebSocketClient {

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
    /**
     * WebSocket连接地址
     */
    public URI url;


    /**
     * 统计时间
     */
    private Date lastTime = new Date();

    /**
     * 统计数量
     */
    private int count = 0;


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
            AlarmJob.put(BaseDecConstant.TYPE, BaseDecConstant.REFRESH);
            DataContainer.alarmBuffer.offer(AlarmJob, 16384);
            JSONArray content = AlarmUtil.alarmRefresh(projectId, groupCode, alarmUrl, repository);
            AlarmJob.put(BaseDecConstant.CONTENT, content);
            log.warn("*****查询到报警数据: " + content.size());
            //多线程处理报警
            BaseDecConstant.EXECUTOR.execute(new AlarmJob(projectId));
        } catch (Exception e) {
            log.error("*****alarmWebSocket打开报警连接操作时异常", e);
        }
    }

    @Override
    public void onClose(int arg0, String arg1, boolean arg2) {
        //log.warn("*****alarmWebSocket连接已关闭: " + url.toString());
    }

    @Override
    public void onError(Exception arg0) {
        //log.error("*****alarmWebSocket连接错误: " + url.toString());
    }

    @Override
    public void onMessage(String arg0) {
        count++;
        Date currTime = new Date();
        if (currTime.getTime() / (1000L * 60) != lastTime.getTime() / (1000L * 60)) {
            lastTime = currTime;
            log.warn("*****alarmWebSocket-1分钟接收到数据数量: " + count);
            count = 0;
        }

        try {
            JSONObject alarm = (JSONObject) JSON.parse(arg0);
            if (alarm.get(BaseDecConstant.ID) == null) {
                log.warn("*****接收到报警处理数据：" + alarm);
            }
            //3为转工单
            if (alarm.get(BaseDecConstant.PUSH_TYPE) != null) {
                if ((Integer) alarm.get(BaseDecConstant.PUSH_TYPE) == 3) {
                    String alarmId = (String) alarm.get(BaseDecConstant.ALARM_ID);
                    AlarmUtil.processOrderDesc(alarmId, alarm);
                } else {
                    AlarmUtil.updateAlarm(alarm, DataContainer.projectMap.get(projectId));
                    AlarmUtil.processAlarm(alarm);
                }
            } else {
                log.error("*****接收到报警数据异常，id：" + alarm.get(BaseDecConstant.ID) + "，缺失属性：" + BaseDecConstant.PUSH_TYPE);
            }

        } catch (Exception e) {
            log.error("*****alarmWebSocket报警数据解析异常", e);
        }

        //多线程处理报警
        BaseDecConstant.EXECUTOR.execute(new AlarmJob(projectId));
    }
}
