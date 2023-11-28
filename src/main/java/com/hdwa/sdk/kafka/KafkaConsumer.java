package com.hdwa.sdk.kafka;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.service.LoadDataMainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
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
        MessageDto msg = JSONObject.parseObject(record.value(), MessageDto.class);
        if (projectId.equals(msg.getProjectId())) {
            log.warn("===============================开始消费DMP消息：{}", record.value());
            //全量更新或者只更新逻辑编组数据
            if ("0".equals(msg.getMsgType())) {
                log.warn("===============================开始更新所有数据===============================");
                loadDataMainService.main();
            } else if ("1".equals(msg.getMsgType())) {
                log.warn("===============================开始更新逻辑编组数据===============================");
                loadDataMainService.logicGroupMain();
            }
        }
    }

}
