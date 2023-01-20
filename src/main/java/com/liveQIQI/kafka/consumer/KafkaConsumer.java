package com.liveQIQI.kafka.consumer;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liveQIQI.enums.SocketMessageType;
import com.liveQIQI.model.vo.LiveRespDanMuVO;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaConsumer {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumer.class);

    private static final String MSG_TYPE = "messageType";

    //    @KafkaListener(topics = {"#{'${kafka.topics}'.split(',')}"})
    @KafkaListener(topics = {"#{T(com.liveQIQI.enums.KafkaTopicEnum).getStrByName('topic01')}"})
    public void listen(ConsumerRecord<?, ?> record) {

        handleVOByMsgType(record.value().toString());
        logger.info(" ===> KafkaConsumer 获取监听:{}", record.value().toString());
    }

    private void handleVOByMsgType(String recordStr){
        JSONObject json = JSON.parseObject(recordStr);
        SocketMessageType messageType = json.getObject(MSG_TYPE, SocketMessageType.class);
        switch (messageType) {
            case LIVE_DANMU:
                LiveRespDanMuVO vo = json.getObject("liveRespDanMuVO", LiveRespDanMuVO.class);
                //todo 存到数据库
                break;
            //todo
        }
    }


}
