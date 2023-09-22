package com.hdwa;

import com.redxun.cache.CacheAutoConfiguration;
import com.redxun.common.ribbon.RibbonAutoConfigure;
import com.redxun.config.SysConfig;
import com.redxun.idempotence.IdempotenceSupportAdvice;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author abao
 * @since 2023/8/10
 */
@SpringBootApplication(exclude = {CacheAutoConfiguration.class, SysConfig.class, IdempotenceSupportAdvice.class,  RibbonAutoConfigure.class})
public class HdwaProjectSdkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdwaProjectSdkApplication.class, args);
    }
}