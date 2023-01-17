package com.liveQIQI.service;
import com.alibaba.fastjson.JSONObject;
import com.liveQIQI.model.vo.LiveRespDanMuVO;

public interface LiveResponseMsgService {

    LiveRespDanMuVO ToVOFromJson(JSONObject jsonObject);
}
