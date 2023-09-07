package com.hdwa.sdk.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hdwa.sdk.entity.ZktAlarmRecord;
import com.hdwa.sdk.kafka.KafkaProducer;
import com.hdwa.sdk.service.ZktAlarmRecordServiceImpl;
import com.redxun.core.cache.alarm.AlarmInfoCache;
import com.redxun.core.constant.alarm.CommonConst;
import com.redxun.core.entity.alarm.AlarmRecordVO;
import com.redxun.core.entity.alarm.AlarmStateVO;
import com.redxun.core.entity.alarm.netty.NettyMessage;
import com.redxun.core.util.alarm.StringUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Collections;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Slf4j
@Data
public class AlarmExpireJob extends QuartzJobBean {

    private final AtomicLong nums = new AtomicLong(1L);

    @Autowired
    ZktAlarmRecordServiceImpl zktAlarmRecordService;

    @Autowired
    AlarmInfoCache alarmInfoCache;

    @Autowired
    KafkaProducer kafkaProducer;

    /**
     * 报警记录信息详情
     */
    private String alarmRecord;
    /**
     * 重试次数
     */
    private String refire;
    /**
     * 过期时间
     */
    private String expireTime;
    /**
     * 报警定义标识（itemcode+objId）
     */
    private String defineId;
    /**
     * 报警状态（2-恢复 3-过期）
     */
    private String state;
    /**
     * 恢复时间
     */
    private String endTime;
    /**
     * 恢复时刻信息
     */
    private String endInfo;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
            log.info("----------------开始---------------------{}", alarmRecord);
            log.warn("refireCount:[{}],过期/恢复时间：[{}/{}],实际执行时间：[{}]", context.getRefireCount(), StringUtil.getString(expireTime), StringUtil.getString(endTime), DateUtil.format(context.getFireTime(), DatePattern.ISO8601_PATTERN));
            if (StringUtils.isNotBlank(alarmRecord)) {
                ZktAlarmRecord zktAlarmRecordDO = StringUtil.tranferItemToDTO(alarmRecord, ZktAlarmRecord.class);
                //立即过期，过期的时候可能还没有报警记录ID,需要重新执行下
                ZktAlarmRecord res = zktAlarmRecordService.getById(zktAlarmRecordDO.getId());
                if (res == null) {
                    res = new ZktAlarmRecord();
                }
                String alarmId = res.getAlarmId();

                if (StringUtil.isEmpty(alarmId)) {
                    log.info("refire:[{}]", refire);
                    mergedJobDataMap.put("refire", String.valueOf(StringUtil.getInt(refire) + 1));
                    reFireJob(context, mergedJobDataMap);
                    log.info("----------------结束---------------------");
                    return;
                }
                log.info("报警参数为：[{}]", zktAlarmRecordDO.toString());
                NettyMessage<AlarmRecordVO> nettyMessage = new NettyMessage<>("", 6, CommonConst.projectId, CommonConst.groupCode);
                nettyMessage.setStreamId(nums.getAndIncrement());
                AlarmRecordVO message = AlarmRecordVO.builder()
                        .id(alarmId)
                        .state(StringUtil.getInt(state))
                        .groupCode(CommonConst.groupCode)
                        .projectId(CommonConst.projectId)
                        .build();
                if ("2".equals(state)) {
                    message.setEndInfo(endInfo);
                    message.setEndTime(com.redxun.core.util.alarm.DateUtil.parse(endTime));
                }
                if("3".equals(state)){
                    message.setEndTime(com.redxun.core.util.alarm.DateUtil.parse(expireTime));
                }
                nettyMessage.setContent(Collections.singletonList(message));
                //{"id","123", "state":1, "groupCode":"wd", "projectId":"Pj123"}
                // todo 改为kafka消息推送
                //nettyClient.sendMessage(nettyMessage);
                kafkaProducer.send(nettyMessage);
                //已经过期的时候删除掉这条报警定义了，保证不会再次产生报警
                AlarmStateVO alarmState = new AlarmStateVO(defineId);
                alarmInfoCache.setAlarmState(defineId, alarmState);
                if (zktAlarmRecordService.getById(zktAlarmRecordDO.getId()) != null) {
                    zktAlarmRecordService.delete(zktAlarmRecordDO.getId());
                }
            }
            log.info("----------------结束---------------------");
        } catch (Exception e) {
            log.error("job hander error", e);
        }

    }

    private void reFireJob(JobExecutionContext context, JobDataMap mergedJobDataMap) {
        try {
            if (StringUtil.getInt(refire) > 1) {
                log.error("重新执行依然失败,信息为：{}[{}]", state, alarmRecord);
            } else {
                Scheduler scheduler = context.getScheduler();
                Trigger trigger = context.getTrigger();
                DateTime dateTime = DateUtil.offsetSecond(new Date(), 300);
                Trigger newTrigger = TriggerBuilder.newTrigger()
                        .startAt(dateTime.toJdkDate())
                        .withIdentity(trigger.getKey())
                        .usingJobData(mergedJobDataMap)
                        .withSchedule(simpleSchedule().withMisfireHandlingInstructionIgnoreMisfires()).build();
                Date rescheduleJob = scheduler.rescheduleJob(trigger.getKey(), newTrigger);
            }
        } catch (Exception e) {
            log.error("获取不到报警记录ID.重新获取报错！", e);
        }
    }
}