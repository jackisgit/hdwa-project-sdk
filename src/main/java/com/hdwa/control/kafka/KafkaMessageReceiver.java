package com.hdwa.control.kafka;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.control.constant.CommonConst;
import com.hdwa.control.entity.ControlCommand;
import com.hdwa.control.entity.ControlCommandMessage;
import com.hdwa.control.service.CommandService;
import com.hdwa.control.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.*;

@Configuration
@ConditionalOnProperty(prefix = "spring.kafka", name = "enable", havingValue = "true")
@Slf4j
public class KafkaMessageReceiver {

    @Autowired
    private CommandService commandService;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Value("${spring.kafka.producer.edgeTopic}")
    public String topic;

    /**
     * listenerContainerFactory设置了批量拉取消息，因此参数是List<ConsumerRecord<Integer, String>>，否则是ConsumerRecord
     *
     * @param integerStringConsumerRecords
     * @param acknowledgment
     */
    @KafkaListener(topics = {"#{'${topicName}'.split(',')}"}, containerFactory = "listenerContainerFactory")
    public void registryReceiver(List<ConsumerRecord<Integer, String>> integerStringConsumerRecords, Acknowledgment acknowledgment) {
        Iterator<ConsumerRecord<Integer, String>> it = integerStringConsumerRecords.iterator();
        while (it.hasNext()) {
            ConsumerRecord<Integer, String> consumerRecords = it.next();
            //转换数据
            try {
                ControlCommandMessage message = JSON.parseObject(consumerRecords.value(), ControlCommandMessage.class);
                String consumerProjectId = message.getProjectId();
                if (!Objects.equals(consumerProjectId, CommonConst.projectId)) {
                    continue;
                }
                log.info("云端消息接收=========================================, message:{}", JSONUtil.toJsonStr(message));
                handlerMsg(message);
            } catch (Exception e) {
                log.error("控制边缘端异常: " + e.getMessage(), e);
            }
            //dosome
            acknowledgment.acknowledge();
        }
    }

    private void handlerMsg(ControlCommandMessage message) throws Exception {
        // 删除指定时间点后的所有定时任务
        String timeFlag = message.getClearBeforeTimeFlag();
        if (StringUtils.isNotBlank(timeFlag)) {
            Date flagDate = DateUtils.parseDate(timeFlag);
            commandService.deleteCommandAfterFlagDate(flagDate);
        }
        // 处理控制指令
        ControlCommandMessage response = new ControlCommandMessage(2);
        response.setStreamId(message.getStreamId());
        List<ControlCommand> responseContent = new ArrayList<>();
        List<ControlCommand> content = message.getContent();
        if (!CollectionUtils.isEmpty(content)) {
            for (ControlCommand command : content) {
                String value = JSONObject.parseObject(command.getPointAction()).getString("value");
                if (StringUtils.isAnyBlank(command.getFuncId(), command.getMeterId(), value, command.getCommandTime())) {
                    responseContent.add(new ControlCommand(command.getId(), -2));
                    log.warn("command is not valid; [{}]", JSON.toJSONString(command));
                    continue;
                }
                // 获取设备手自动状态
                Object manualAutoSetValue = redisTemplate.opsForValue().get(command.getManualAutoSet());
                if (!Objects.equals(manualAutoSetValue, "1.0") && !Objects.equals(manualAutoSetValue, 1.0d)) {
                    responseContent.add(new ControlCommand(command.getId(), -1));
                    log.info("【生成任务时】设备[{}]手自动状态未设置自动, {}: {}", command.getObjectId(), command.getManualAutoSet(), manualAutoSetValue);
                    continue;
                }
                LocalDateTime commandTime = DateUtils.parse(command.getCommandTime());
                Date startTime = DateUtils.localDateTime2Date(commandTime);
                String hour = DateUtils.format(commandTime, DateUtils.sdfHour);
                String jobName = command.getFuncId() + "_" + command.getMeterId() + DateUtils.format(commandTime);
                JobDataMap jobDataMap = new JobDataMap();
                jobDataMap.put("controlCommand", JSONUtil.toJsonStr(command));
                try {
                    commandService.addCommand(startTime, jobName, hour, jobDataMap, JSON.toJSONString(command));
                    responseContent.add(new ControlCommand(command.getId(), 1));
                } catch (SchedulerException e) {
                    responseContent.add(new ControlCommand(command.getId(), 0));
                    log.error("addCommand error: ", e);
                }
            }
            response.setContent(responseContent);
            kafkaProducer.send(topic, response);
            log.info("边端指令反馈云端1, response: {}", JSONObject.toJSONString(response));
        }
    }

}