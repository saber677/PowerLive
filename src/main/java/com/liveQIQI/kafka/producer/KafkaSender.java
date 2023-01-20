package com.liveQIQI.kafka.producer;

import com.alibaba.fastjson.JSON;
import com.liveQIQI.tool.entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import java.util.Objects;

@Component
public class KafkaSender {

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMsg(Message message){
        kafkaTemplate.send(message.getTopic().name(), buildMsgStr(message));
    }

    private String buildMsgStr(Message message){

        if (Objects.isNull(message)){
            return null;
        }
        return JSON.toJSONString(message);
    }

}
