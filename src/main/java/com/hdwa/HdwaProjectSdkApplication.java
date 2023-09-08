package com.hdwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * @author abao
 * @since 2023/8/10
 */
//@EnableDiscoveryClient
//@EnableTransactionManagement
//@EnableFeignClients
@SpringBootApplication
@ComponentScan(basePackages = {"com.hdwa.*", "com.redxun.*"})
public class HdwaProjectSdkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdwaProjectSdkApplication.class, args);
    }
}