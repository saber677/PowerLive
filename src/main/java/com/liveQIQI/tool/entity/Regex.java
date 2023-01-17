package com.liveQIQI.tool.entity;

public class Regex {

    /**
     * 匹配b站返回的弹幕前缀
     */
    public static final String LIVE_RESPONSE_DANMU_MSG = "\\{\"cmd\":\"DANMU_MSG\",[^\\}].*";

    /**
     * 匹配b站响应通用前缀
     */
    public static final String LIVE_RESPONSE_MSG = "\\{\"cmd\"[^\\}].*";

    /**
     * 匹配json字符串
     */
    @Deprecated
    public static final String KEY_VALUE_MSG = "\\{\"([a-zA-Z_]+)\\\":(.+)}";

    /**
     * 匹配双字节的字符
     */
    @Deprecated
    public static final String BYTE2_MSG = "[\\x00-\\xff].?";

}
