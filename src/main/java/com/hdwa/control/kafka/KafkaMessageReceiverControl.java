package com.hdwa.control.kafka;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.hdwa.control.constant.CommonConst;
import com.hdwa.control.entity.ControlCommandMessage;
import com.hdwa.control.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;

import java.util.List;
import java.util.Objects;

@Slf4j
@Configuration
public class KafkaMessageReceiverControl {

    @Autowired
    private DataService dataService;

    /**
     * listenerContainerFactory设置了批量拉取消息，因此参数是List<ConsumerRecord<Integer, String>>，否则是ConsumerRecord
     *
     * @param integerStringConsumerRecords
     * @param acknowledgment
     */
    @KafkaListener(
            containerFactory = "controlKafkaListenerContainerFactory",
            topics = "${spring.kafka.control.consumer.topics}",
            groupId = "${spring.kafka.control.consumer.group-id}")
    public void registryReceiver(List<ConsumerRecord<Integer, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        for (ConsumerRecord<Integer, String> consumerRecords : integerStringConsumerRecords) {
            //转换数据
            try {
                ControlCommandMessage message = JSON.parseObject(consumerRecords.value(), ControlCommandMessage.class);
                String consumerProjectId = message.getProjectId();
                if (!Objects.equals(consumerProjectId, CommonConst.projectId)) {
                    continue;
                }
                log.warn("云端消息接收=========================================, message:{}", JSONUtil.toJsonStr(message));
                dataService.handlerMsg(message);
            } catch (Exception e) {
                log.error("控制边缘端异常: " + e.getMessage(), e);
            }
            //dosome
            acknowledgment.acknowledge();
        }
    }
}