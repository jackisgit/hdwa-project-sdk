package com.hdwa.sdk.config;

import com.alibaba.nacos.api.config.ConfigService;
import com.alibaba.nacos.api.config.listener.Listener;
import com.alibaba.nacos.api.exception.NacosException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigServiceInit {

    @Bean
    public ConfigService init(){
        return new ConfigService() {
            @Override
            public String getConfig(String s, String s1, long l) throws NacosException {
                return null;
            }

            @Override
            public String getConfigAndSignListener(String s, String s1, long l, Listener listener) throws NacosException {
                return null;
            }

            @Override
            public void addListener(String s, String s1, Listener listener) throws NacosException {

            }

            @Override
            public boolean publishConfig(String s, String s1, String s2) throws NacosException {
                return false;
            }

            @Override
            public boolean publishConfig(String s, String s1, String s2, String s3) throws NacosException {
                return false;
            }

            @Override
            public boolean removeConfig(String s, String s1) throws NacosException {
                return false;
            }

            @Override
            public void removeListener(String s, String s1, Listener listener) {

            }

            @Override
            public String getServerStatus() {
                return null;
            }

            @Override
            public void shutDown() throws NacosException {

            }
        };
    }
}