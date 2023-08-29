package com.hdwa.control.init;

import com.hdwa.control.constant.CommonConst;
import lombok.extern.slf4j.Slf4j;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;

import javax.servlet.ServletContextAttributeListener;

@Service
@Order(1)
@Slf4j
public class InitRunner implements ServletContextAttributeListener, CommandLineRunner {

    @Value("${spring.kafka.enable:false}")
    public boolean enable;

    @Autowired
    @Qualifier("quartzScheduler")
    Scheduler quartzScheduler;

    @Override
    public void run(String... args) throws Exception {
        try {
            if (!quartzScheduler.isStarted()) {
                log.info("quartz定时任务将在延迟20秒后启动...");
                quartzScheduler.startDelayed(20);
            } else {
                log.info("quartz定时任务已经启动...");
            }
        } catch (SchedulerException e) {
            // TODO Auto-generated catch block
            log.error("quartz定时任务启动失败", e);
        }
    }

    /**
     * 项目名称
     */
    @Value("${group.control.project.id}")
    public void setProjectId(String value) {
        CommonConst.projectId = value;
    }

    /**
     * 集团编码
     */
    @Value("${group.control.code}")
    public void setGroupCode(String value) {
        CommonConst.groupCode = value;
    }

}