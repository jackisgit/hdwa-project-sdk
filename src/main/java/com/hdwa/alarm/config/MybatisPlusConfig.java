package com.hdwa.alarm.config;

import com.redxun.datasource.MyBatisConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.hdwa.alarm.mapper*"})
public class MybatisPlusConfig extends MyBatisConfig {
}