package com.liveQIQI.controller;

import com.liveQIQI.tool.entity.Result;
import com.liveQIQI.tool.utils.ResultUtil;
import com.liveQIQI.websocket.client.ClientSocket;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/socket")
public class SocketController {

    private static final Logger logger = LoggerFactory.getLogger(SocketController.class);

    @Autowired
    private ClientSocket clientSocket;

    @ApiOperation(value = "获取bilibili弹幕")
    @PostMapping(value = "/open")
    public Result open(@ApiParam(value = "房间号") @RequestParam(value = "roomId", required = false) Integer roomId) {
        try {
            clientSocket.start(roomId);
        } catch (Exception e) {
            logger.error(" ===> 获取bilibili弹幕错误:{}", e.getMessage());
        }
        return ResultUtil.success();
    }

}
