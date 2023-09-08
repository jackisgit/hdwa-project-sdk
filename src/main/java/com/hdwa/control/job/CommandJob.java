package com.hdwa.control.job;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.control.client.EmsControlClient;
import com.hdwa.control.entity.*;
import com.hdwa.control.kafka.KafkaProducer;
import com.hdwa.control.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.Date;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Slf4j
public class CommandJob extends QuartzJobBean {

    @Autowired
    EmsControlClient emsControlClient;

    @Autowired
    private KafkaProducer kafkaProducer;

    @Value("${iot.project.url:127.0.0.1:8852}")
    public String url;

    @Value("${group.control.project.iotid}")
    public String projectId;

    @Value("${misfire.discard.minute:10}")
    public int misfireDiscardMinute;

    @Value("${spring.kafka.enable:false}")
    public boolean enable;

    @Value("${spring.kafka.producer.edgeTopic}")
    public String topics;

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
                String value = JSONObject.parseObject(command.getPointAction()).getString("value");
                if (value.contains("true")) {
                    value = "1";
                } else if (value.contains("false")) {
                    value = "0";
                }
                PointSetParam pointSetParam = new PointSetParam(command.getMeterId(), Integer.parseInt(command.getFuncId()), Double.parseDouble(value));
                try {
                    BatchPointSetParam batchPointSetParam = new BatchPointSetParam(Collections.singletonList(pointSetParam));
                    BatchPointSetResult batchPointSetResult = emsControlClient.pointSetBatch(batchPointSetParam);
                    log.info("下发控制指令:[{}]:[{}] 的执行结果为：[{}]", url + "/pointsetbatch_post", JSONObject.toJSONString(batchPointSetParam), JSONObject.toJSONString(batchPointSetResult));
                } catch (Exception e) {
                    log.error("下发控制指令失败", e);
                }
                ControlCommandMessage message = new ControlCommandMessage(2);
                command.setCommandResult(2);
                message.setContent(Collections.singletonList(command));
                kafkaProducer.send(topics, message);
                log.info("边端指令反馈云端2, message: {}", JSONObject.toJSONString(message));
            }
            log.info("定时任务[{}]执行时长为：{} 毫秒", context.getJobDetail().getKey(), timer.interval());
        } catch (Exception e) {
            log.error("控制边缘端异常: " + e.getMessage(), e);
        }
    }

}