package com.hdwa.alarm.kafka;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.alarm.config.CommonConst;
import com.hdwa.alarm.vo.NettyMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.Collections;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "spring.kafka", name = "enable", havingValue = "true")
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    /**
     * 边缘端报警发送topic
     */
    @Value("${spring.kafka.producer.topics}")
    private String topicEdgeAlarm;

    @Bean
    @Order(2)
    public void queryDefine() {
        //启动的时候发送消息,获取全部报警定义
        NettyMessage<JSONObject> nettyMessage = new NettyMessage<>("", 4, CommonConst.projectId, CommonConst.groupCode);
        JSONObject content = new JSONObject();
        content.put("groupCode", CommonConst.groupCode);
        content.put("projectId", CommonConst.projectId);
        nettyMessage.setContent(Collections.singletonList(content));
        send(nettyMessage);
    }

    public void send(NettyMessage<?> message) {
        //发送消息
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicEdgeAlarm, message);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable throwable) {
                //发送失败的处理
                log.info(topicEdgeAlarm + " - 边缘端 发送消息失败：" + throwable.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> stringObjectSendResult) {
                //成功的处理
                log.info(topicEdgeAlarm + " - 边缘端 发送消息成功：" + stringObjectSendResult.toString());
            }
        });
    }
}