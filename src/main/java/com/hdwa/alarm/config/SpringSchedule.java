package com.hdwa.alarm.config;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.alarm.kafka.KafkaProducer;
import com.hdwa.alarm.vo.NettyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
@Slf4j
public class SpringSchedule {

    @Autowired
    KafkaProducer kafkaProducer;

    @Scheduled(cron = "${alarm.get.all.alarmdefine.cron}")
    public void allResetCron() {
        NettyMessage message = new NettyMessage("", 4, CommonConst.projectId, CommonConst.groupCode);
        JSONObject content = new JSONObject();
        content.put("groupCode", CommonConst.groupCode);
        content.put("projectId", CommonConst.projectId);
        message.setContent(Collections.singletonList(content));
        kafkaProducer.send(message);
    }

   /* @Scheduled(initialDelay = 2000, fixedDelay = 60000)
    public void connectCron() {
        NettyMessage<?> message = new NettyMessage<>("", 3, CommonConst.projectId, CommonConst.groupCode);

        try {
            kafkaProducer.send(message);
        } catch (Exception e) {
            log.error("定时任务发送报警消息失败", e);
        }
    }*/
}