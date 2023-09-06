package com.hdwa.sdk.config;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.hdwa.sdk.cache.AlarmInfoCache;
import com.hdwa.sdk.entity.ZktAlarmRecord;
import com.hdwa.sdk.service.ZktAlarmRecordServiceImpl;
import com.hdwa.sdk.vo.AlarmRecordVO;
import com.redxun.core.entity.alarm.netty.NettyMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.Arrays;
import java.util.Date;
import java.util.concurrent.atomic.AtomicLong;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@Slf4j
public class AlarmExpireJob extends QuartzJobBean {

    private final AtomicLong nums = new AtomicLong(1L);
    @Autowired
    ZktAlarmRecordServiceImpl zktAlarmRecordService;
    @Autowired
    AlarmInfoCache alarmInfoCache;

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

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getEndInfo() {
        return endInfo;
    }

    public void setEndInfo(String endInfo) {
        this.endInfo = endInfo;
    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        try {
            JobDataMap mergedJobDataMap = context.getMergedJobDataMap();
            log.info("----------------开始---------------------{}", alarmRecord);
            log.warn("refireCount:[{}],过期/恢复时间：[{}/{}],实际执行时间：[{}]", context.getRefireCount(), StringUtil.getString(expireTime), StringUtil.getString(endTime), DateUtil.format(context.getFireTime(), DatePattern.ISO8601_PATTERN));
            if (StringUtils.isNotBlank(alarmRecord)) {
                ZktAlarmRecord zktAlarmRecordDO = StringUtil.tranferItemToDTO(alarmRecord, ZktAlarmRecordDO.class);
                //立即过期，过期的时候可能还没有报警记录ID,需要重新执行下
                String alarmId = alarmRecordRepository.findById(zktAlarmRecordDO.getDefinitionId()).orElse(new ZktAlarmRecordDO()).getAlarmId();
                if (StringUtil.isEmpty(alarmId)) {
                    log.info("refire:[{}]", refire);
                    mergedJobDataMap.put("refire", String.valueOf(StringUtil.getInt(refire) + 1));
                    reFireJob(context, mergedJobDataMap);
                    log.info("----------------结束---------------------");
                    return;
                }
                log.info("报警参数为：[{}]", zktAlarmRecordDO.toString());
                NettyMessage<AlarmRecordVO> nettyMessage = new NettyMessage<>(6);
                nettyMessage.setStreamId(nums.getAndIncrement());
                AlarmRecordVO message = AlarmRecordVO.builder()
                        .id(alarmId)
                        .state(StringUtil.getInt(state))
                        .groupCode(CommonConst.groupCode)
                        .projectId(CommonConst.projectId)
                        .build();
                if ("2".equals(state)) {
                    message.setEndInfo(endInfo);
                    message.setEndTime(DateUtils.parse(endTime));
                }
                if("3".equals(state)){
                    message.setEndTime(DateUtils.parse(expireTime));
                }
                nettyMessage.setContent(Arrays.asList(message));
                //{"id","123", "state":1, "groupCode":"wd", "projectId":"Pj123"}
                nettyClient.sendMessage(nettyMessage);
                //已经过期的时候删除掉这条报警定义了，保证不会再次产生报警
                AlarmState alarmState = new AlarmState(defineId);
                alarmInfoCache.setAlarmState(defineId, alarmState);
                if (alarmRecordRepository.existsById(zktAlarmRecordDO.getDefinitionId())) {
                    alarmRecordRepository.deleteById(zktAlarmRecordDO.getDefinitionId());
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

    public String getAlarmRecord() {
        return alarmRecord;
    }

    public void setAlarmRecord(String alarmRecord) {
        this.alarmRecord = alarmRecord;
    }

    public String getRefire() {
        return refire;
    }

    public void setRefire(String refire) {
        this.refire = refire;
    }

    public String getExpireTime() {
        return expireTime;
    }

    public void setExpireTime(String expireTime) {
        this.expireTime = expireTime;
    }

    public String getDefineId() {
        return defineId;
    }

    public void setDefineId(String defineId) {
        this.defineId = defineId;
    }
}
