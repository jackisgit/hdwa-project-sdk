package com.hdwa.control.job;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * <p>
 * 定时任务
 * </p>
 *
 * @author chenxin
 * @since 2023/10/23
 */
@Slf4j
@Configuration
@EnableScheduling
public class CommonTask {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Scheduled(cron = "0 0/1 * * * ?")
    public void test() {

    }

}