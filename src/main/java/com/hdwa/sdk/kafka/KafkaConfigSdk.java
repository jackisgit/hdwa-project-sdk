package com.hdwa.sdk.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class KafkaConfigSdk {

    /**
     * 读取kafka配置
     * Primary注解表示默认以这个为准
     *
     * @return kafka配置
     */
    @Primary
    @ConfigurationProperties(prefix = "spring.kafka.sdk")
    @Bean
    public KafkaProperties sdkKafkaProperties() {
        return new KafkaProperties();
    }

    /**
     * 构建kafka的生产者发送template
     *
     * @param sdkKafkaProperties kafka配置
     * @return kafka的生产者发送template
     */
    @Bean("kafkaTemplateSdk")
    public KafkaTemplate<String, Object> kafkaTemplate(
            @Autowired @Qualifier("sdkKafkaProperties") KafkaProperties sdkKafkaProperties) {
        return new KafkaTemplate<>(sdkProducerFactory(sdkKafkaProperties));
    }

    /**
     * 构建kafka的消费者监听容器工厂
     *
     * @param sdkKafkaProperties kafka配置
     * @return kafka的消费者监听容器工厂
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    sdkKafkaListenerContainerFactory(@Autowired @Qualifier("sdkKafkaProperties") KafkaProperties sdkKafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(sdkConsumerFactory(sdkKafkaProperties));
        //factory.setBatchListener(true);
        //factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        return factory;
    }

    /**
     * 新建kafka的消费者工厂
     *
     * @param sdkKafkaProperties kafka配置
     * @return kafka的消费者工厂
     */
    private ConsumerFactory<? super Integer, ? super String> sdkConsumerFactory(KafkaProperties sdkKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(sdkKafkaProperties.buildConsumerProperties());
    }

    /**
     * 新建kafka的生产者工厂
     *
     * @param sdkKafkaProperties kafka配置
     * @return kafka的生产者工厂
     */
    private DefaultKafkaProducerFactory<String, Object> sdkProducerFactory(KafkaProperties sdkKafkaProperties) {
        return new DefaultKafkaProducerFactory<>(sdkKafkaProperties.buildProducerProperties());
    }
}