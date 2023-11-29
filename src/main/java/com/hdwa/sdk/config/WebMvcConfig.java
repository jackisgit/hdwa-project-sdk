package com.hdwa.sdk.config;

import com.hdwa.sdk.interceptor.TokenInterceptor;
import com.hdwa.sdk.service.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author abao
 * @since 2023/11/24
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Autowired
    private TokenService tokenService;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 注册拦截器，并指定拦截路径
        registry.addInterceptor(new TokenInterceptor(tokenService)).addPathPatterns("/**");
    }
}
