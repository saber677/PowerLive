package com.liveQIQI.controller;

import com.liveQIQI.tool.entity.Result;
import com.liveQIQI.tool.utils.ResultUtil;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.BindingResultUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/socket")
public class SocketController {

    @ApiOperation(value = "获取bilibili弹幕")
    @PostMapping(value = "/open")
    public Result open(@ApiParam(value = "房间号") @RequestParam Long roomId){
        return ResultUtil.success();
    }

}
