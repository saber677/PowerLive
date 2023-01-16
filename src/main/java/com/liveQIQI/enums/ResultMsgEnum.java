package com.liveQIQI.enums;


public enum ResultMsgEnum {

    ERROR_MSG_DEFAULT("error"),
    SUCCESS_MSG_DEFAULT("success"),
    ;

    private String msg;

    ResultMsgEnum(String msg) {
        this.msg = msg;
    }

    public String getMsg() {
        return msg;
    }
}
