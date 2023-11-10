package com.hdwa;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * @author abao
 * @since 2023/8/10
 */
@SpringBootApplication
@EnableScheduling
public class HdwaProjectSdkApplication {

    public static void main(String[] args) {
        SpringApplication.run(HdwaProjectSdkApplication.class, args);
    }

}
