package com.hdwa.alarm.kafka;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Slf4j
@Configuration
@EnableConfigurationProperties({KafkaProperties.class})
@ConditionalOnProperty(prefix = "spring.kafka", name = "enable", havingValue = "true")
public class KafkaCommonConfig implements InitializingBean {
    private final KafkaProperties properties;

    @Value("${spring.kafka.consumer.topics}")
    private String edgeTopic;

    public KafkaCommonConfig(KafkaProperties properties) {
        this.properties = properties;
    }

    //构造消费者属性map，ConsumerConfig中的可配置属性比spring boot自动配置要多
    private Map<String, Object> consumerProperties() {
//        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        return properties.buildConsumerProperties();
    }

    /**
     * 不使用spring boot默认方式创建的DefaultKafkaConsumerFactory，重新定义创建方式
     *
     * @return
     */
    @Bean("consumerFactory")
    public DefaultKafkaConsumerFactory consumerFactory() {
        return new DefaultKafkaConsumerFactory(consumerProperties());
    }

    @Bean("listenerContainerFactory")
    //个性化定义消费者
    public ConcurrentKafkaListenerContainerFactory listenerContainerFactory(DefaultKafkaConsumerFactory consumerFactory) {
        //指定使用DefaultKafkaConsumerFactory
        ConcurrentKafkaListenerContainerFactory factory = new ConcurrentKafkaListenerContainerFactory();
        factory.setConsumerFactory(consumerFactory);

//        //设置消费者ack模式为手动，看需求设置
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);
        //设置可批量拉取消息消费，拉取数量一次3，看需求设置
        factory.setConcurrency(1);
        factory.setBatchListener(true);
        return factory;
    }

    //创建生产者配置map，ProducerConfig中的可配置属性比spring boot自动配置要多
    private Map<String, Object> producerProperties() {
        return properties.buildProducerProperties();
    }

    /**
     * 不使用spring boot的KafkaAutoConfiguration默认方式创建的DefaultKafkaProducerFactory，重新定义
     *
     * @return
     */
    @Bean("produceFactory")
    public DefaultKafkaProducerFactory produceFactory() {
        return new DefaultKafkaProducerFactory(producerProperties());
    }

    /**
     * 不使用spring boot的KafkaAutoConfiguration默认方式创建的KafkaTemplate，重新定义
     *
     * @param produceFactory
     * @return
     */
    @Bean
    public KafkaTemplate kafkaTemplate(DefaultKafkaProducerFactory produceFactory) {
        return new KafkaTemplate(produceFactory);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        String topicName = wireTopics();
        System.setProperty("topicName", topicName);
        log.info("### set system config topic:{}", topicName);
    }

    private String wireTopics() {
        Set<String> topicSet = new HashSet<>();
        topicSet.add(edgeTopic);
        return StringUtils.join(topicSet, ",");
    }
}
