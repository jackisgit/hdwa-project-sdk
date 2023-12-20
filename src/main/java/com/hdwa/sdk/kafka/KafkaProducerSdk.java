package com.hdwa.sdk.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.FailureCallback;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.SuccessCallback;

/**
 * @author abao
 * @since 2023/9/8
 * kafka生产
 */
@Slf4j
@Component
public class KafkaProducerSdk {

    @Autowired
    private KafkaTemplate kafkaTemplateSdk;


    public <K, T> void sendMessage(T message) {
        String topic = ((MessageDto) message).getTopic();
        log.warn("send kafka message,topic:{},message:{}", topic, message);
        ListenableFuture<SendResult<K, T>> listenableFuture = kafkaTemplateSdk.send(topic, message);

        //成功回调
        SuccessCallback<SendResult<K, T>> successCallback = result -> log.warn("发送成功,topic:{},message:{}", topic, message);

        //失败回调
        FailureCallback failureCallback = e -> {
            log.error("发送失败,topic:{},message:{}", topic, message);
            throw new RuntimeException(e);
        };
        listenableFuture.addCallback(successCallback, failureCallback);
    }
}
