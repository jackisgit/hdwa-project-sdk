package com.hdwa;

import com.redxun.common.ribbon.annotation.EnableFeignInterceptor;
import com.redxun.log.mq.LogOutput;
import com.redxun.msgsend.mq.MsgOutput;
import com.redxun.web.mq.ErrLogOutput;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author abao
 * @since 2023/8/10
 */
@EnableDiscoveryClient
@EnableTransactionManagement
@EnableFeignInterceptor
@EnableFeignClients
@SpringBootApplication
@EnableBinding({LogOutput.class, ErrLogOutput.class, MsgOutput.class})
@ComponentScan(basePackages = {"com.hdwa.*", "com.redxun.core.cache.alarm", "com.redxun.core.constant.alarm"})
public class HdwaProjectSdkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdwaProjectSdkApplication.class, args);
    }

    @Bean
    MeterRegistryCustomizer<MeterRegistry> configurer(
            @Value("${spring.application.name}") String applicationName) {
        return (registry) -> registry.config().commonTags("application", applicationName);
    }
}