package com.hdwa.control.kafka;

import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

@Component
@Slf4j
public class KafkaProducerControl {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplateControl;

    public void send(String topic, Object message) {
        // 发送消息
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplateControl.send(topic, JSONObject.toJSONString(message));

        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onSuccess(SendResult<String, Object> sendResult) {
                // 发送成功的处理
                log.warn("发送成功,topic:{},message:{}", topic, message);
            }

            @Override
            public void onFailure(Throwable throwable) {
                // 发送失败的处理
                log.warn(topic + " 边端控制发送消息失败：" + throwable.getMessage());
            }
        });
    }

}