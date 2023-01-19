package com.liveQIQI.tool.entity;

import com.liveQIQI.enums.KafkaTopicEnum;
import com.liveQIQI.model.vo.LiveRespDanMuVO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Message {

    private Long id;

    private String msg;

    private KafkaTopicEnum topic;

    private Date date;

    private LiveRespDanMuVO vo;

}
