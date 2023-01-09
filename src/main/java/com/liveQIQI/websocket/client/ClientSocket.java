package com.liveQIQI.websocket.client;


import com.alibaba.fastjson.JSONObject;
import com.liveQIQI.enums.UnitEnum;
import com.liveQIQI.utils.BinaryHandleUtil;
import com.liveQIQI.utils.JsScriptUtil;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Objects;

@ClientEndpoint
@Component
public class ClientSocket {

    private static final Logger logger = LoggerFactory.getLogger(ClientSocket.class);

    private Session session;

    String url = "wss://dsa-cn-live-comet-01.chat.bilibili.com:2245/sub";

    @Autowired
    private BinaryHandleUtil binaryHandleUtil;

//    public void init(){
//        try {
//            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
//            URI uri = URI.create(url);
//            ClientSocket clientSocket = new ClientSocket();
//            container.connectToServer(clientSocket, uri);
//        } catch (DeploymentException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

    @PostConstruct
    void init() {
        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            URI uri = URI.create(url);
            ClientSocket clientSocket = new ClientSocket();
            container.connectToServer(clientSocket, uri);
        } catch (DeploymentException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logger.info("===" + "连接 billibili 成功");
        JSONObject json = new JSONObject();
        json.put("uid", 0);
        json.put("room_id", 25878472);
        json.put("protover", 3);
        json.put("platform", "web");
//        json.put("clientver", "1.4.0");
        json.put("type", 2);
        json.put("key", "u91Sk476h2FV7KCv59U35sEAuVNmQUq0yBfeqJVHIKZqkxVPe_hJYD1GKS-cS43jNMVpD_TEih7K-ybwqpGvotO7luheMjhEi0w7wjILrm8WJ-dL4xid3D0rnJiP7QruH3xTVYNw41xffdF5-UM=");

        ByteArrayBuffer bytes = certification(json);
        if (Objects.isNull(bytes)) {
            throw new RuntimeException("认证数据结果为空");
        }
        byte[] data = bytes.getRawData();

        String s = new BinaryHandleUtil().setUnit(0, UnitEnum.UNIT32, data.length + 16);
//        String s1 = new BinaryHandleUtil().setUnit(4, UnitEnum.UNIT16, 16);
//        String s2 = new BinaryHandleUtil().setUnit(6, UnitEnum.UNIT16, 1);
//        String s3 = new BinaryHandleUtil().setUnit(8, UnitEnum.UNIT32, 7);
//        StringBuffer stringBuffer = new StringBuffer();
//        stringBuffer.append(s).append(s1).append(s2).append(s3);
        for (int i = 0; i < data.length; i++) {
            System.out.println(data[i] + "");
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(data);
        certifyRequest(25878472, url);
        try {
            session.getBasicRemote().sendBinary(byteBuffer);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @OnClose
    public void onClose() {
        logger.info("===" + "关闭 billibili 成功");
    }

    @OnError
    public void onError(Throwable e) {
        logger.info("===" + "连接bilibili 发生异常");
        logger.error(e.getMessage(), e);
    }

    @OnMessage
    public void onMessage(Session session, byte[] message) {
        try {
            String messageStr = new String(message, "utf-8");
            logger.info("===" + "接收到来自bilibili的数据" + messageStr);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private ByteArrayBuffer certification(JSONObject jsonObject) {
        ByteArrayBuffer bytes = new ByteArrayBuffer();
        String jsonStr = jsonObject.toJSONString();
        int length = jsonStr.length();
        char c;
        for (int i = 0; i < length; i++) {
            c = jsonStr.charAt(i);
            if (c >= 0x010000 && c <= 0x10FFFF) {
                bytes.write(((c >> 18) & 0x07) | 0xF0);
                bytes.write(((c >> 12) & 0x3F) | 0x80);
                bytes.write(((c >> 6) & 0x3F) | 0x80);
                bytes.write((c & 0x3F) | 0x80);
            } else if (c >= 0x000800 && c <= 0x00FFFF) {
                bytes.write(((c >> 12) & 0x0F) | 0xE0);
                bytes.write(((c >> 6) & 0x3F) | 0x80);
                bytes.write((c & 0x3F) | 0x80);
            } else if (c >= 0x000080 && c <= 0x0007FF) {
                bytes.write(((c >> 6) & 0x1F) | 0xC0);
                bytes.write((c & 0x3F) | 0x80);
            } else {
                bytes.write(c & 0xFF);
            }
        }
        return bytes;
    }

    private void certifyRequest(Integer roomId, String url) {
        logger.info("===" + "发送认证请求");
        JsScriptUtil.certifyRequest(roomId, url);
    }

    public static byte[] HexStrToByteArray(String hexStr) {

        if (hexStr == null) {

            return null;

        }

        if (hexStr.length() == 0) {

            return new byte[0];

        }

        byte[] byteArray = new byte[hexStr.length() / 2];

        for (int i = 0; i < byteArray.length; i++) {

            String subStr = hexStr.substring(2 * i, 2 * i + 2);

            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));

        }

        return byteArray;

    }
}
