package com.liveQIQI.service.impl;

import com.liveQIQI.enums.UnitEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
public class BinaryHandleInstance {

    private static final char ZERO = '0';
    private StringBuffer reqParam = new StringBuffer();

    public StringBuffer handleByteData(Integer offset, UnitEnum unit, String valueStr) {

        valueStr = fillValueStr(unit, valueStr);

        try {
            this.reqParam.insert(finalOffset(offset), valueStr);
        } catch (Exception e) {
            throw new RuntimeException(" ===> the value of offset is error");
        }
        return reqParam;
    }

    private String fillValueStr(UnitEnum unit, String valueStr) {
        while (valueStr.length() < lengthFromUnit(unit)) {
            valueStr = ZERO + valueStr;
        }
        return valueStr;
    }

    private Integer lengthFromUnit(UnitEnum unit) {
        return unit.getByteNum();
    }

    private Integer finalOffset(Integer offset) {
        return 2 * offset;
    }


}