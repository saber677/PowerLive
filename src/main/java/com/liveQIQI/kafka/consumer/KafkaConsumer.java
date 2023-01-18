package com.liveQIQI.kafka.consumer;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    @KafkaListener(topics = {"topic01"})
    public void listen(ConsumerRecord<?, ?> record) {
        logger.info(" ===> KafkaConsumer:{}", "consumer 获取监听");
    }

}
