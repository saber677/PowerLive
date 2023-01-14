package com.liveQIQI.service.impl;

import com.liveQIQI.enums.UintEnum;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
@Data
public class BinaryHandleInstance {

    private static final char ZERO = '0';
    private StringBuffer reqParam;

    public StringBuffer handleByteData(Integer offset, UintEnum unit, String valueStr) {

        valueStr = fillValueStr(unit, valueStr);

        try {
            if (Objects.isNull(this.reqParam)) {
                this.reqParam = new StringBuffer();
            }
            this.reqParam.insert(finalOffset(offset), valueStr);
        } catch (Exception e) {
            throw new RuntimeException(" ===> valueStr or offset is error");
        }
        return reqParam;
    }

    private String fillValueStr(UintEnum unit, String valueStr) {
        while (valueStr.length() < lengthFromUnit(unit)) {
            valueStr = ZERO + valueStr;
        }
        return valueStr;
    }

    private Integer lengthFromUnit(UintEnum unit) {
        return unit.getByteNum();
    }

    private Integer finalOffset(Integer offset) {
        return 2 * offset;
    }

}
