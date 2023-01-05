package com.liveQIQI.websocket.server;

import com.liveQIQI.config.WebSocketConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;

@ServerEndpoint(value = "/server/{roomId}", configurator = WebSocketConfig.class)
@Component
public class ServerSocket {

    private static final Logger logger = LoggerFactory.getLogger(ServerSocket.class);

    private Integer roomId;

    private String message = "好你妈比, 王立全我草你妈比";


    @OnOpen
    public void onOpen(Session session, @PathParam("roomId") Integer roomId){
        logger.info("===" + "开始连接websocket");
    }

    @OnClose
    public void onClose(){
        logger.info("===" + "关闭连接websocket");

    }

    @OnError
    public void onError(Throwable e){
        logger.error("===" + "websocket出现异常");

    }

    @OnMessage
    public void onMessage(Session session, @PathParam("roomId") Integer roomId, String message){
        try {
            this.roomId = roomId;
            session.getBasicRemote().sendText( message + ": " + this.message);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }


}
