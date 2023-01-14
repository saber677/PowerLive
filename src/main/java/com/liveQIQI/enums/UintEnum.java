package com.liveQIQI.enums;

public enum UintEnum {

    UINT32(8),
    UINT16(4),
    UINT8(2),

    ;
    private Integer byteNum;

    public Integer getByteNum() {
        return byteNum;
    }

    public void setByteNum(Integer byteNum) {
        this.byteNum = byteNum;
    }

    UintEnum(Integer byteNum) {
        this.byteNum = byteNum;
    }
}
