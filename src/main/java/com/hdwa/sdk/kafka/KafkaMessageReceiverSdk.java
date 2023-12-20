package com.hdwa.sdk.kafka;

import com.alibaba.fastjson.JSONObject;
import com.hdwa.sdk.constant.BaseDecConstant;
import com.hdwa.sdk.service.LoadDataMainService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

/**
 * @author abao
 * @since 2023/9/8
 * kafka消费
 */
@Slf4j
@Configuration
public class KafkaMessageReceiverSdk {

    private static final String projectId;

    static {
        projectId = System.getProperty(BaseDecConstant.PROJECT_ID);
    }

    @Autowired
    private LoadDataMainService loadDataMainService;

    /**
     * @Description 监听云端消息
     */
    @KafkaListener(topics = "${spring.kafka.sdk.consumer.properties.topic}", containerFactory = "sdkKafkaListenerContainerFactory", groupId = "${spring.kafka.sdk.consumer.properties.group.id}")
    public void receiveSsMsg(ConsumerRecord<String, String> record, Acknowledgment ack) {
        MessageDto msg = JSONObject.parseObject(record.value(), MessageDto.class);
        if (projectId.equals(msg.getProjectId())) {
            log.warn("===============================开始消费DMP消息：{}", record.value());
            //全量更新/更新逻辑编组数据/更新接口数据
            boolean flag;
            if ("0".equals(msg.getMsgType())) {
                flag = loadDataMainService.main();
                if (flag) {
                    log.warn("===============================更新所有数据-成功===============================");
                } else {
                    log.error("===============================更新所有数据-失败===============================");
                }
            } else if ("1".equals(msg.getMsgType())) {
                flag = loadDataMainService.logicGroupMain();
                if (flag) {
                    log.warn("===============================更新逻辑编组数据-成功============================");
                } else {
                    log.error("===============================更新逻辑编组数据-失败============================");
                }
            } else if ("2".equals(msg.getMsgType())) {
                flag = loadDataMainService.logicApiMain();
                if (flag) {
                    log.warn("===============================更新接口数据-成功============================");
                } else {
                    log.error("===============================更新接口数据-失败============================");
                }
            } else {
                log.error("===============================错误消息类型：" + msg.getMsgType() + "============================");
            }
        }

        ack.acknowledge();
    }

}
