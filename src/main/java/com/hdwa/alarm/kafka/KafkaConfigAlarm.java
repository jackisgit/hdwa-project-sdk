package com.hdwa.alarm.kafka;

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
public class KafkaConfigAlarm {

    /**
     * 读取kafka配置
     * Primary注解表示默认以这个为准
     *
     * @return kafka配置
     */
    @ConfigurationProperties(prefix = "spring.kafka.alarm")
    @Bean
    public KafkaProperties alarmKafkaProperties() {
        return new KafkaProperties();
    }

    /**
     * 构建kafka的生产者发送template
     *
     * @param alarmKafkaProperties kafka配置
     * @return kafka的生产者发送template
     */
    @Bean("kafkaTemplateAlarm")
    public KafkaTemplate<String, Object> kafkaTemplate(
            @Autowired @Qualifier("alarmKafkaProperties") KafkaProperties alarmKafkaProperties) {
        return new KafkaTemplate<>(alarmProducerFactory(alarmKafkaProperties));
    }

    /**
     * 构建kafka的消费者监听容器工厂
     *
     * @param alarmKafkaProperties kafka配置
     * @return kafka的消费者监听容器工厂
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    alarmKafkaListenerContainerFactory(@Autowired @Qualifier("alarmKafkaProperties") KafkaProperties alarmKafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(alarmConsumerFactory(alarmKafkaProperties));
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    /**
     * 新建kafka的消费者工厂
     *
     * @param alarmKafkaProperties kafka配置
     * @return kafka的消费者工厂
     */
    private ConsumerFactory<? super Integer, ? super String> alarmConsumerFactory(KafkaProperties alarmKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(alarmKafkaProperties.buildConsumerProperties());
    }

    /**
     * 新建kafka的生产者工厂
     *
     * @param alarmKafkaProperties kafka配置
     * @return kafka的生产者工厂
     */
    private DefaultKafkaProducerFactory<String, Object> alarmProducerFactory(KafkaProperties alarmKafkaProperties) {
        return new DefaultKafkaProducerFactory<>(alarmKafkaProperties.buildProducerProperties());
    }
}