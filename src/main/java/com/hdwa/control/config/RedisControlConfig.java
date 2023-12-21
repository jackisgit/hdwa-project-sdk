package com.hdwa.control.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;


/**
 * @author abao
 * @since 2023/11/29
 * control redis配置
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisControlConfig {

    @Bean(name = "secondaryRedisConnectionFactory")
    public RedisConnectionFactory secondaryRedisConnectionFactory(
            @Qualifier("secondaryRedisProperties") RedisProperties redisProperties) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
        jedisConnectionFactory.setPort(redisProperties.getPort());
        jedisConnectionFactory.setHostName(redisProperties.getHost());
        return jedisConnectionFactory;
    }

    @Bean(name = "secondaryRedisTemplate")
    public RedisTemplate<String, String> secondaryRedisTemplate(
            @Qualifier("secondaryRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        Jackson2JsonRedisSerializer jackson2JsonRedisSerializer = new Jackson2JsonRedisSerializer(Object.class);
        ObjectMapper om = new ObjectMapper();
        jackson2JsonRedisSerializer.setObjectMapper(om);
        StringRedisSerializer stringRedisSerializer = new StringRedisSerializer();
        // key采用String的序列化方式
        template.setKeySerializer(stringRedisSerializer);
        // hash的key也采用String的序列化方式
        template.setHashKeySerializer(stringRedisSerializer);
        // value序列化方式采用jackson
        template.setValueSerializer(jackson2JsonRedisSerializer);
        // hash的value序列化方式采用jackson
        template.setHashValueSerializer(jackson2JsonRedisSerializer);
        template.afterPropertiesSet();
        return template;
    }

    @Bean(name = "secondaryRedisProperties")
    @ConfigurationProperties(prefix = "spring.redis.secondary")
    public RedisProperties secondaryRedisProperties() {
        return new RedisProperties();
    }

}