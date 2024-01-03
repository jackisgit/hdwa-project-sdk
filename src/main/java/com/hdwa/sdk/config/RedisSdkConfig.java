package com.hdwa.sdk.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.data.redis.RedisProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * @author abao
 * @since 2023/11/29
 * sdk redis配置
 */
@Configuration
@EnableConfigurationProperties(RedisProperties.class)
public class RedisSdkConfig {

    @Primary
    @Bean(name = "primaryRedisConnectionFactory")
    public RedisConnectionFactory primaryRedisConnectionFactory(
            @Qualifier("primaryRedisProperties") RedisProperties redisProperties) {
        JedisConnectionFactory jedisConnectionFactory = new JedisConnectionFactory();
       /* //开发环境不是哨兵配置
        if (System.getProperty(BaseDecConstant.SPRING_PROFILES_ACTIVE).equals(BaseDecConstant.DEV)) {
            jedisConnectionFactory = new JedisConnectionFactory();
        } else {
            RedisSentinelConfiguration sentinelConfig = new RedisSentinelConfiguration()
                    .master(redisProperties.getSentinel().getMaster());
            List<String> sentinelNodes = redisProperties.getSentinel().getNodes();
            for (String sentinelNode : sentinelNodes) {
                String[] parts = sentinelNode.split(":");
                if (parts.length == 2) {
                    sentinelConfig.addSentinel(new RedisNode(parts[0], Integer.parseInt(parts[1])));
                }
            }
            jedisConnectionFactory = new JedisConnectionFactory(sentinelConfig);
        }*/
        jedisConnectionFactory.setPort(redisProperties.getPort());
        jedisConnectionFactory.setHostName(redisProperties.getHost());
        jedisConnectionFactory.setDatabase(redisProperties.getDatabase());
        jedisConnectionFactory.setTimeout((int) redisProperties.getTimeout().getSeconds());
        jedisConnectionFactory.setPassword(redisProperties.getPassword());
        return jedisConnectionFactory;
    }

    @Primary
    @Bean(name = "primaryRedisTemplate")
    public RedisTemplate<String, String> redisTemplate(
            @Qualifier("primaryRedisConnectionFactory") RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        return template;
    }

    @Primary
    @Bean(name = "primaryRedisProperties")
    @ConfigurationProperties(prefix = "spring.redis.primary")
    public RedisProperties primaryRedisProperties() {
        return new RedisProperties();
    }

}
