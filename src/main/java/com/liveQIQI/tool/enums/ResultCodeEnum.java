package com.liveQIQI.tool.enums;

public enum ResultCodeEnum {

    ERROR_CODE(1),
    SUCCESS_CODE(0),
    ;

    private Integer code;

    ResultCodeEnum(Integer code) {
        this.code = code;
    }

    public Integer getCode() {
        return code;
    }
}
