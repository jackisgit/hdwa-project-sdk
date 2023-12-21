package com.hdwa.control.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

@Configuration
public class KafkaConfigControl {

    /**
     * 读取kafka配置
     * Primary注解表示默认以这个为准
     *
     * @return kafka配置
     */
    @ConfigurationProperties(prefix = "spring.kafka.control")
    @Bean
    public KafkaProperties controlKafkaProperties() {
        return new KafkaProperties();
    }

    /**
     * 构建kafka的生产者发送template
     *
     * @param controlKafkaProperties kafka配置
     * @return kafka的生产者发送template
     */
    @Bean("kafkaTemplateControl")
    public KafkaTemplate<String, Object> kafkaTemplate(
            @Autowired @Qualifier("controlKafkaProperties") KafkaProperties controlKafkaProperties) {
        return new KafkaTemplate<>(controlProducerFactory(controlKafkaProperties));
    }

    /**
     * 构建kafka的消费者监听容器工厂
     *
     * @param controlKafkaProperties kafka配置
     * @return kafka的消费者监听容器工厂
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    controlKafkaListenerContainerFactory(@Autowired @Qualifier("controlKafkaProperties") KafkaProperties controlKafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(controlConsumerFactory(controlKafkaProperties));
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    /**
     * 新建kafka的消费者工厂
     *
     * @param controlKafkaProperties kafka配置
     * @return kafka的消费者工厂
     */
    private ConsumerFactory<? super Integer, ? super String> controlConsumerFactory(KafkaProperties controlKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(controlKafkaProperties.buildConsumerProperties());
    }

    /**
     * 新建kafka的生产者工厂
     *
     * @param controlKafkaProperties kafka配置
     * @return kafka的生产者工厂
     */
    private DefaultKafkaProducerFactory<String, Object> controlProducerFactory(KafkaProperties controlKafkaProperties) {
        return new DefaultKafkaProducerFactory<>(controlKafkaProperties.buildProducerProperties());
    }
}