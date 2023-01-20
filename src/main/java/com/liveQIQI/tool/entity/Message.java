package com.liveQIQI.tool.entity;

import com.liveQIQI.enums.KafkaTopicEnum;
import com.liveQIQI.enums.SocketMessageType;
import com.liveQIQI.model.vo.LiveRespDanMuVO;
import lombok.Data;
import java.util.Date;

@Data
public class Message {

    private Long id;

    private String msg;

    private KafkaTopicEnum topic;

    private Date date;

    private SocketMessageType messageType;

    private LiveRespDanMuVO liveRespDanMuVO;

    public Message(Date date) {
        this.date = date;
    }

    public static Message build(){
        return new Message(new Date());
    }


    public Message id(Long id) {
        this.id = id;
        return this;
    }

    public Message msg(String msg) {
        this.msg = msg;
        return this;
    }

    public Message topic(KafkaTopicEnum topic) {
        this.topic = topic;
        return this;
    }


    public Message messageType(SocketMessageType messageType) {
        this.messageType = messageType;
        return this;
    }

    public Message liveRespDanMuVO(LiveRespDanMuVO liveRespDanMuVO) {
        this.liveRespDanMuVO = liveRespDanMuVO;
        return this;
    }
}
