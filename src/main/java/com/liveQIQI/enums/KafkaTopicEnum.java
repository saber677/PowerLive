package com.liveQIQI.enums;

import org.apache.commons.lang3.StringUtils;

public enum KafkaTopicEnum {

    topic01,
    ;

    public static String getStrByName(String name){

        for (KafkaTopicEnum value : KafkaTopicEnum.values()) {
            if (StringUtils.equals(value.name(), name)){
                return value.name();
            }
        }
        return null;
    }

}
