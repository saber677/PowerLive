package com.liveQIQI.kafka.producer;

import com.liveQIQI.enums.KafkaTopicEnum;
import com.liveQIQI.tool.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMsg(Message message){
        kafkaTemplate.send(message.getTopic().name(), message.getMsg());
    }

}
