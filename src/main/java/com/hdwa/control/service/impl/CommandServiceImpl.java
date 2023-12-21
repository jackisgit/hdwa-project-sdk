package com.hdwa.control.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.date.TimeInterval;
import cn.hutool.core.thread.ExecutorBuilder;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.hdwa.control.job.CommandJob;
import com.hdwa.control.service.CommandService;
import com.hdwa.control.utils.DateUtils;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

import static org.quartz.SimpleScheduleBuilder.simpleSchedule;

/**
 * @description: 控制命令下发服务
 * @since: 2023/8/17
 * @version: V4.0
 */
@Slf4j
@Service
public class CommandServiceImpl implements CommandService {

    @Autowired
    @Qualifier("quartzScheduler-control")
    Scheduler quartzSchedulerControl;

    ExecutorService executor = ExecutorBuilder.create()
            .setCorePoolSize(5)
            .setMaxPoolSize(10)
            .setWorkQueue(new LinkedBlockingQueue<>(102400))
            .setHandler(new ThreadPoolExecutor.AbortPolicy())
            .build();

    /**
     * 添加定时器
     *
     * @param startTime    开始执行时间
     * @param jobName      job名称
     * @param jobGroupName job分组
     * @param jobDataMap   触发器的数据（任务相关的数据都放在这里）
     * @return
     */
    @Override
    public String addCommand(Date startTime, String jobName, String jobGroupName, JobDataMap jobDataMap, String msg) {
        executor.execute(() -> {
            String jobKeyString = jobName + "_" + jobGroupName;
            synchronized (jobKeyString.intern()) {
                try {
                    LocalDateTime nowDateTime = LocalDateTime.now().withSecond(0);
                    Date date = Date.from(nowDateTime.atZone(ZoneId.systemDefault()).toInstant());
                    if (startTime.before(date)) {
                        log.warn("执行时间【{}】为历史时间，自动忽略跳过", DateUtils.formatDate(startTime));
                        return;
                    }
                    JobKey jobKey = new JobKey(jobName, jobGroupName);
                    JobDetail jobDetail = JobBuilder.newJob(CommandJob.class).withIdentity(jobKey).build();
                    Trigger trigger = TriggerBuilder.newTrigger().startAt(startTime)
                            .withIdentity("trigger_" + jobName, jobName)
                            .usingJobData(jobDataMap)
                            .withSchedule(simpleSchedule().withMisfireHandlingInstructionIgnoreMisfires()).build();
                    // 已经存在的job实例和触发器自动覆盖
                    HashSet<Trigger> triggerSet = new HashSet<>();
                    Date fireTime = trigger.getFireTimeAfter(date);
                    if (fireTime != null) {
                        log.warn("设置定时任务的执行时间为:【{}】, msg: {}", DateUtils.formatDate(fireTime), msg);
                    } else {
                        log.warn("【{}】执行时间为空", jobKey);
                        return;
                    }
                    triggerSet.add(trigger);
                    quartzSchedulerControl.scheduleJob(jobDetail, triggerSet, true);
                } catch (SchedulerException e) {
                    log.error("添加定时任务报错", e);
                }
            }
        });
        return "success";
    }

    @Override
    public synchronized String deleteCommandAfterFlagDate(Date flagDate) throws SchedulerException {
        TimeInterval timer = DateUtil.timer();
        String hour = DateUtils.format(DateUtils.date2LocalDateTime(flagDate), DateUtils.sdfHour);
        List<JobKey> jobkeyList = new ArrayList<>();
        List<String> jobGroupNames = quartzSchedulerControl.getJobGroupNames();
        for (String jobGroupName : jobGroupNames) {
            GroupMatcher<JobKey> matcher = GroupMatcher.groupEquals(jobGroupName);
            Set<JobKey> jobkeySet = quartzSchedulerControl.getJobKeys(matcher);
            if (jobGroupName.compareTo(hour) > 0) {
                // 分组使用的小时，大于标志小时的未来任务可以直接删除，否则就需要根据具体时间判断
                jobkeyList.addAll(jobkeySet);
            } else {
                for (JobKey jobKey : jobkeySet) {
                    List<? extends Trigger> triggers = null;
                    try {
                        triggers = quartzSchedulerControl.getTriggersOfJob(jobKey);
                        for (Trigger trigger : triggers) {
                            Date nextFireTime = trigger.getNextFireTime();
                            if (ObjectUtil.isNotNull(nextFireTime) && nextFireTime.after(flagDate)) {
                                jobkeyList.add(jobKey);
                            }
                        }
                    } catch (SchedulerException e) {
                        log.error(e.getMessage(), e);
                        try {
                            quartzSchedulerControl.deleteJob(jobKey);
                        } catch (Exception e2) {
                            log.error(e2.getMessage(), e2);
                        }
                    }
                }
            }
        }
        log.warn("要删除控制指令数量：{} 指令集为：[{}]", jobkeyList.size(), JSONObject.toJSONString(jobkeyList));
        if (CollectionUtil.isNotEmpty(jobkeyList)) {
            jobkeyList = jobkeyList.stream().distinct().collect(Collectors.toList());
            quartzSchedulerControl.deleteJobs(jobkeyList);
        }
        log.warn("删除定时任务执行毫秒数为：{} 毫秒", timer.interval());
        return "success!";
    }

}