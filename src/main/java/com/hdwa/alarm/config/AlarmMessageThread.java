package com.hdwa.alarm.config;

import com.hdwa.alarm.service.AlarmQuartzServiceImpl;
import com.redxun.core.cache.alarm.ExpireAlarmQueue;
import com.redxun.core.entity.alarm.ExpireAlarmMessageVO;
import lombok.extern.slf4j.Slf4j;

/**
 * AlarmMessage消费线程
 **/
@Slf4j
public class AlarmMessageThread implements Runnable {

    AlarmQuartzServiceImpl alarmQuartzService;

    public AlarmMessageThread(AlarmQuartzServiceImpl alarmQuartzService) {
        this.alarmQuartzService = alarmQuartzService;
    }

    @Override
    public void run() {
        try {
            //启动后先休息20秒，这样报警netty  websocket  quartz都启动了
            Thread.sleep(20000);
            while (true) {
                ExpireAlarmMessageVO expireAlarmMessage = ExpireAlarmQueue.getExpireAlarmMessageQueue().consume();
                log.info("剩余过期消息总数:{}", ExpireAlarmQueue.getExpireAlarmMessageQueue().size());
                if ("1".equals(expireAlarmMessage.getType())) {
                    alarmQuartzService.addExpireJob(expireAlarmMessage.getStartTime(), expireAlarmMessage.getJobName(), expireAlarmMessage.getJobGroupName(), expireAlarmMessage.getJobDataMap());
                } else if ("2".equals(expireAlarmMessage.getType())) {
                    alarmQuartzService.deleteExpireJob(expireAlarmMessage.getJobName(), expireAlarmMessage.getJobGroupName());
                }
            }
        } catch (Exception e) {
            log.error("报警过期消息队列消费失败", e);
        }
    }
}