package com.hdwa.alarm.service;

import cn.hutool.core.date.DateUtil;
import com.hdwa.alarm.config.AlarmExpireJob;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashSet;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * 报警定时任务服务
 **/
@Slf4j
@Service
public class AlarmQuartzServiceImpl {

    @Autowired
    @Qualifier("quartzScheduler")
    Scheduler quartzScheduler;

    /**
     * 添加定时器
     */
    public String addExpireJob(Date startTime, String jobName, String jobGroupName, JobDataMap jobDataMap) throws SchedulerException {
        if (startTime.before(new Date())) {
            log.error("执行时间【{}】为历史时间，继续添加", com.hdwa.alarm.util.DateUtil.formatDate(startTime));
        }
        JobKey jobKey = new JobKey(jobName, jobGroupName);
        JobDetail jobDetail = JobBuilder.newJob(AlarmExpireJob.class).withIdentity(jobKey).requestRecovery().build();
        Trigger trigger = TriggerBuilder
                .newTrigger()
                .startAt(startTime)
                .withIdentity("trigger_" + jobName, jobGroupName)
                .usingJobData(jobDataMap)
                .withSchedule(simpleSchedule().withMisfireHandlingInstructionIgnoreMisfires()).build();
        //已经存在的job实例和触发器自动覆盖 job实 例 唯 一标识：
        HashSet<Trigger> triggerSet = new HashSet<>();
        Date fireTime = trigger.getFireTimeAfter(DateUtil.offsetMinute(startTime, -1).toJdkDate());
        if (fireTime != null) {
            log.warn("执行时间为:【{}】，设置时间为【{}】", com.hdwa.alarm.util.DateUtil.formatDate(fireTime), com.hdwa.alarm.util.DateUtil.formatDate(startTime));
        } else {
            log.warn("执行时间为 空!");
        }
        triggerSet.add(trigger);
        quartzScheduler.scheduleJob(jobDetail, triggerSet, true);

        return "success";
    }

    /**
     * 删除定时任务
     */
    public synchronized String deleteExpireJob(String jobName, String jobGroupName) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroupName);
        boolean checkExists = quartzScheduler.checkExists(jobKey);
        if (checkExists) {
            quartzScheduler.deleteJob(jobKey);
        }

        return "success!";
    }
}