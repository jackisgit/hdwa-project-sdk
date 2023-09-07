package com.hdwa.sdk.config;

import com.redxun.datasource.MyBatisConfig;
import com.redxun.db.config.DefaultMybatisPlusConfig;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan({"com.hdwa.sdk.mapper*"})
public class MybatisPlusConfig extends MyBatisConfig {
}