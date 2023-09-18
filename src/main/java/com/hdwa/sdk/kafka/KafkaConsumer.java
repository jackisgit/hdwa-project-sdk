package com.hdwa.sdk.kafka;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.service.LoadDataMainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

/**
 * @author abao
 * @since 2023/9/8
 * kafka消费
 */
@Slf4j
@Component
public class KafkaConsumer {

    @Autowired
    private LoadDataMainService loadDataMainService;

    private static final String projectId;

    static {
        projectId = System.getProperty(BaseDecConstant.PROJECT_ID);
    }


    /**
     * @Description 监听云端消息
     */
    @KafkaListener(topics = {"dmpToSdk"}, groupId = "${spring.kafka.consumer.properties.group.id}")
    public void receiveSsMsg(ConsumerRecord<String, String> record) {
        log.warn("=======接收到DMP消息：{}", record.value());
        MessageDto msg = JSONObject.parseObject(record.value(), MessageDto.class);
        if (projectId.equals(msg.getProjectId())) {
            log.warn("=======开始消费DMP消息：{}", record.value());
            loadDataMainService.downLoadDataMain();
            loadDataMainService.loadDataMain();
        }
    }

}
