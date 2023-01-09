package com.liveQIQI.enums;

public enum UnitEnum {

    UNIT32(8),
    UNIT16(4),
    UNIT8(2),

    ;
    private Integer byteNum;

    public Integer getByteNum() {
        return byteNum;
    }

    public void setByteNum(Integer byteNum) {
        this.byteNum = byteNum;
    }

    UnitEnum(Integer byteNum) {
        this.byteNum = byteNum;
    }
}
