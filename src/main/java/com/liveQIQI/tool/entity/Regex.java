package com.liveQIQI.tool.entity;

public class Regex {

    /**
     * 匹配b站返回的字符串前缀
     */
    public static final String LIVE_RESPONSE_JSON_STR = "\\{\"cmd\"[^\\}].*";
    public static final String KEY_VALUE_JSON_STR = "\\{\"([a-zA-Z_]+)\\\":(.+)}";

}
