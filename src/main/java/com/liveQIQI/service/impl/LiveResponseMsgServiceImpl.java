package com.liveQIQI.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.liveQIQI.model.vo.LiveRespDanMuVO;
import com.liveQIQI.service.LiveResponseMsgService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Objects;

@Service
public class LiveResponseMsgServiceImpl implements LiveResponseMsgService {

    private static final String DANMU_INFO = "info";

    @Override
    public LiveRespDanMuVO toVOFromJson(JSONObject jsonObject) {

        if (Objects.isNull(jsonObject)) {
            return null;
        }

        JSONArray info = jsonObject.getJSONArray(DANMU_INFO);
        if (CollectionUtils.isEmpty(info)) {
            return null;
        }

        JSONArray userInfo = info.getJSONArray(2);
        if (CollectionUtils.isEmpty(userInfo)){
            return null;
        }

        Integer uid = userInfo.getInteger(0);
        String uName = userInfo.getString(1);
        String content = info.getString(1);
        return LiveRespDanMuVO.builder()
                .uid(uid)
                .uName(uName)
                .content(content)
                .build();
    }
}
