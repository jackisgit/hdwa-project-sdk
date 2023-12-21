package com.hdwa.control.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.control.constant.RequestUrlConstant;
import com.hdwa.control.entity.BatchPointSetParam;
import com.hdwa.control.entity.ControlCommand;
import com.hdwa.control.entity.ControlCommandMessage;
import com.hdwa.control.entity.PointSetParam;
import com.hdwa.control.kafka.KafkaProducerControl;
import com.hdwa.control.utils.DateUtils;
import com.hdwa.sdk.utils.OkHttpClientUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.temporal.ChronoUnit;
import java.util.*;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Slf4j
public class CommandJob extends QuartzJobBean {


    @Value("${url.iotProject}")
    public String url;
    @Value("${misfire.discard.minute:10}")
    public int misfireDiscardMinute;
    @Value("${spring.kafka.control.producer.edgeTopic}")
    public String topics;
    @Autowired
    private KafkaProducerControl kafkaProducerControl;
    @Qualifier("secondaryRedisTemplate")
    @Autowired
    private RedisTemplate<String, String> secondaryRedisTemplate;

    @Override
    protected void executeInternal(JobExecutionContext context) {
        try {
            TimeInterval timer = DateUtil.timer();
            JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
            Date fireTime = context.getFireTime();
            Date scheduledFireTime = context.getScheduledFireTime();
            long delayMinute = DateUtils.betweenTwoTime(DateUtils.date2LocalDateTime(scheduledFireTime), DateUtils.date2LocalDateTime(fireTime), ChronoUnit.MINUTES);
            if (delayMinute > misfireDiscardMinute) {
                log.warn("丢弃历史数据{}：{}分钟", context.getJobDetail().getKey(), delayMinute);
                return;
            }
            String controlCommandStr = mergedJobDataMap.getString("controlCommand");
            log.warn("controlCommand：" + controlCommandStr);
            if (StringUtils.isNotBlank(controlCommandStr)) {
                ControlCommand command = JSONUtil.toBean(controlCommandStr, ControlCommand.class);
                ControlCommandMessage message = new ControlCommandMessage(2);
                // 获取设备手自动状态
                Object manualAutoSetValue = secondaryRedisTemplate.opsForValue().get(command.getManualAutoSet());
                List<ControlCommand> responseContent = new ArrayList<>();
                if (!Objects.equals(manualAutoSetValue, "1.0") && !Objects.equals(manualAutoSetValue, 1.0d)) {
                    responseContent.add(new ControlCommand(command.getId(), -1));
                    log.warn("【任务执行时】设备[{}]手自动状态未设置自动, {}: {}", command.getObjectId(), command.getManualAutoSet(), manualAutoSetValue);
                    message.setContent(responseContent);
                    kafkaProducerControl.send(topics, message);
                    return;
                }
                String value = JSONObject.parseObject(command.getPointAction()).getString("value");
                if (value.contains("true")) {
                    value = "1";
                } else if (value.contains("false")) {
                    value = "0";
                }
                PointSetParam pointSetParam = new PointSetParam(command.getMeterId(), Integer.parseInt(command.getFuncId()), Double.parseDouble(value));
                try {
                    BatchPointSetParam batchPointSetParam = new BatchPointSetParam(Collections.singletonList(pointSetParam));
                    JSONObject batchPointSetResult = OkHttpClientUtil.httpPost(JSONObject.parseObject(JSON.toJSONString(batchPointSetParam)), url + RequestUrlConstant.POINT_SET_BATCH_POST);

                    log.warn("下发控制指令:[{}]:[{}] 的执行结果为：[{}]", url + "/pointsetbatch_post", JSONObject.toJSONString(batchPointSetParam), JSONObject.toJSONString(batchPointSetResult));
                } catch (Exception e) {
                    log.error("下发控制指令失败", e);
                }

                command.setCommandResult(2);
                message.setContent(Collections.singletonList(command));
                kafkaProducerControl.send(topics, message);
                log.warn("边端指令反馈云端2, message: {}", JSONObject.toJSONString(message));
            }
            log.warn("定时任务[{}]执行时长为：{} 毫秒", context.getJobDetail().getKey(), timer.interval());
        } catch (Exception e) {
            log.error("控制边缘端异常: " + e.getMessage(), e);
        }
    }

}