package com.hdwa.control.init;

import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContextAttributeListener;

@Service
@Order(1)
@Slf4j
public class InitRunnerControl implements ServletContextAttributeListener, CommandLineRunner {

    @Autowired
    @Qualifier("quartzScheduler-control")
    Scheduler quartzSchedulerControl;

    @Override
    public void run(String... args) {
        try {
            if (!quartzSchedulerControl.isStarted()) {
                log.warn("quartz定时任务将在延迟20秒后启动...");
                quartzSchedulerControl.startDelayed(20);
            } else {
                log.warn("quartz定时任务已经启动...");
            }
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            log.error("quartz定时任务启动失败", e);
        }
    }
}