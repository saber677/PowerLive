package com.liveQIQI.model.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ApiModel
public class LiveRespDanMuVO {

    @ApiParam(value = "用户ID")
    private Integer uid;

    @ApiParam(value = "用户名")
    private String uName;

    @ApiParam(value = "弹幕内容")
    private String content;
}
