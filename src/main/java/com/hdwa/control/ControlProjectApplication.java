package com.hdwa.control;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * @author chenxin
 */
@SpringBootApplication
@EnableScheduling
@EnableFeignClients
public class ControlProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(ControlProjectApplication.class, args);
    }

}