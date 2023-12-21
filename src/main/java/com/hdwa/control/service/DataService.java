package com.hdwa.control.service;

import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.control.entity.ControlCommand;
import com.hdwa.control.entity.ControlCommandMessage;
import com.hdwa.control.kafka.KafkaProducerControl;
import com.hdwa.control.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.quartz.JobDataMap;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author abao
 * @since 2023/12/20
 */
@Slf4j
@Service
public class DataService {
    @Value("${spring.kafka.control.producer.edgeTopic}")
    public String topic;
    @Autowired
    private CommandService commandService;
    @Qualifier("secondaryRedisTemplate")
    @Autowired
    private RedisTemplate<String, String> secondaryRedisTemplate;
    @Autowired
    private KafkaProducerControl kafkaProducerControl;

    public void handlerMsg(ControlCommandMessage message) throws Exception {
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
                if (StringUtils.isBlank(command.getFuncId()) || StringUtils.isBlank(command.getMeterId()) || StringUtils.isBlank(value) || StringUtils.isBlank(command.getCommandTime())) {
                    responseContent.add(new ControlCommand(command.getId(), -2));
                    log.warn("command is not valid; [{}]", JSON.toJSONString(command));
                    continue;
                }
                // 获取设备手自动状态
                Object manualAutoSetValue = secondaryRedisTemplate.opsForValue().get(command.getManualAutoSet());
                if (!Objects.equals(manualAutoSetValue, "1.0") && !Objects.equals(manualAutoSetValue, 1.0d)) {
                    responseContent.add(new ControlCommand(command.getId(), -1));
                    log.warn("【生成任务时】设备[{}]手自动状态未设置自动, {}: {}", command.getObjectId(), command.getManualAutoSet(), manualAutoSetValue);
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
            kafkaProducerControl.send(topic, response);
            log.warn("边端指令反馈云端1, response: {}", JSONObject.toJSONString(response));
        }
    }
}
