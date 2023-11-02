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
public class HuidaKafkaConfig {

    /**
     * 读取惠达kafka配置
     * Primary注解表示默认以这个为准
     *
     * @return 惠达kafka配置
     */
    @Primary
    @ConfigurationProperties(prefix = "spring.kafka")
    @Bean
    public KafkaProperties huidaKafkaProperties() {
        return new KafkaProperties();
    }

    /**
     * 构建惠达kafka的生产者发送template
     *
     * @param huidaKafkaProperties 惠达kafka配置
     * @return 惠达kafka的生产者发送template
     */
    @Bean("kafkaTemplate")
    public KafkaTemplate<String, Object> kafkaTemplate(
            @Autowired @Qualifier("huidaKafkaProperties") KafkaProperties huidaKafkaProperties) {
        return new KafkaTemplate<>(huidaProducerFactory(huidaKafkaProperties));
    }

    /**
     * 构建惠达kafka的消费者监听容器工厂
     *
     * @param huidaKafkaProperties 惠达kafka配置
     * @return 惠达kafka的消费者监听容器工厂
     */
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<Integer, String>>
    huidaKafkaListenerContainerFactory(@Autowired @Qualifier("huidaKafkaProperties") KafkaProperties huidaKafkaProperties) {
        ConcurrentKafkaListenerContainerFactory<Integer, String> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(huidaConsumerFactory(huidaKafkaProperties));
        factory.setBatchListener(true);
        factory.setConcurrency(1);
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;
    }

    /**
     * 新建惠达kafka的消费者工厂
     *
     * @param huidaKafkaProperties 惠达kafka配置
     * @return 惠达kafka的消费者工厂
     */
    private ConsumerFactory<? super Integer, ? super String> huidaConsumerFactory(KafkaProperties huidaKafkaProperties) {
        return new DefaultKafkaConsumerFactory<>(huidaKafkaProperties.buildConsumerProperties());
    }

    /**
     * 新建惠达kafka的生产者工厂
     *
     * @param huidaKafkaProperties 惠达kafka配置
     * @return 惠达kafka的生产者工厂
     */
    private DefaultKafkaProducerFactory<String, Object> huidaProducerFactory(KafkaProperties huidaKafkaProperties) {
        return new DefaultKafkaProducerFactory<>(huidaKafkaProperties.buildProducerProperties());
    }
}