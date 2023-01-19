package com.liveQIQI.config;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {

    @Value("spring.kafka.producer.retries")
    private String retries;

    @Value("spring.kafka.producer.batch-size")
    private String size;

    @Value("spring.kafka.producer.buffer-memory")
    private String memory;

    @Value("spring.kafka.bootstrap-servers")
    private String bootstrapAddress;

    @Value("spring.kafka.producer.key.serialize")
    private String key;

    @Value("spring.kafka.producer.value.serialize")
    private String value;

    @Bean
    public KafkaTemplate kafkaTemplate(){
        return new KafkaTemplate(producerFactory());
    }

    @Bean
    public ProducerFactory producerFactory(){
        Map<String, Object> configProps = new HashMap<>();
        configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress); //bootstrapAddress holds address of kafka server
        configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, key);
        configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, value);
        configProps.put(ProducerConfig.RETRIES_CONFIG, retries);
        configProps.put(ProducerConfig.BATCH_SIZE_CONFIG, size);
        configProps.put(ProducerConfig.BUFFER_MEMORY_CONFIG, memory);
        return new DefaultKafkaProducerFactory<>(configProps);
    }
}
