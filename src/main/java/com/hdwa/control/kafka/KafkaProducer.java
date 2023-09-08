package com.hdwa.control.kafka;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Configuration
@Slf4j
@ConditionalOnProperty(prefix = "spring.kafka", name = "enable", havingValue = "true")
public class KafkaProducer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void send(String topic, Object message) {
        // 发送消息
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topic, JSONObject.toJSONString(message));
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> sendResult) {
                // 发送成功的处理

            }

            @Override
            public void onFailure(Throwable throwable) {
                // 发送失败的处理
                log.info(topic + " 边端控制发送消息失败：" + throwable.getMessage());
            }
        });
    }

}