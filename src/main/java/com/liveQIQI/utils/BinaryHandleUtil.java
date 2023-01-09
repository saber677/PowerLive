package com.liveQIQI.utils;

import com.liveQIQI.enums.UnitEnum;
import com.liveQIQI.service.impl.BinaryHandleInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class BinaryHandleUtil {

    @Autowired
    private BinaryHandleInstance instance;

    public String setUnit(Integer offset, UnitEnum unit, Integer value){

        if (Objects.isNull(value)){
            throw new RuntimeException(" ===> value is null");
        }

        String valueStr = Integer.toHexString(value);
        StringBuffer stringBuffer = new BinaryHandleInstance().handleByteData(offset, unit, valueStr);
        String s = stringBuffer.toString();

        return s;

    }



}
