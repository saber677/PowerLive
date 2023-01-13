package com.liveQIQI.websocket.client;


import ch.qos.logback.core.encoder.ByteArrayUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liveQIQI.enums.UnitEnum;
import com.liveQIQI.tool.utils.BinaryHandleUtil;
import com.liveQIQI.tool.utils.JsScriptUtil;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import io.swagger.util.Json;
import org.apache.tomcat.util.buf.ByteBufferUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.adapter.standard.ConvertingEncoderDecoderSupport;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.zip.DataFormatException;
import java.util.zip.Inflater;

@ClientEndpoint
@Component
public class ClientSocket {

    private static final Logger logger = LoggerFactory.getLogger(ClientSocket.class);

    private Session session;

    private ByteBuffer byteBuffer;

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
            container.connectToServer(ClientSocket.this, uri);
        } catch (DeploymentException | IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        logger.info(" ===> 连接 billibili 成功");

        JSONObject json = new JSONObject();
        json.put("uid", 0);
        json.put("roomid", 25878472);//todo _
        json.put("protover", 3);
        json.put("platform", "web");
//        json.put("clientver", "1.4.0");
        json.put("type", 2);
        json.put("key", "u91Sk476h2FV7KCv59U35sEAuVNmQUq0yBfeqJVHIKZqkxVPe_hJYD1GKS-cS43jNMVpD_TEih7K-ybwqpGvotO7luheMjhEi0w7wjILrm8WJ-dL4xid3D0rnJiP7QruH3xTVYNw41xffdF5-UM=");

        ByteArrayBuffer dataBytes = buildCertifyByte(json);
        if (Objects.isNull(dataBytes)) {
            throw new RuntimeException(" ===> 认证数据结果为空");
        }
        int size = dataBytes.size();
        byte[] bytes = dataBytes.getRawData();

        binaryHandleUtil.setUnit(0, UnitEnum.UNIT32, size + 16)
                .setUnit(4, UnitEnum.UNIT16, 16)
                .setUnit(6, UnitEnum.UNIT16, 1)
                .setUnit(8, UnitEnum.UNIT32, 7)
                .setUnit(12, UnitEnum.UNIT32, 1);
        for (int i = 0; i < size; i++) {
            binaryHandleUtil.setUnit(16 + i, UnitEnum.UNIT8, Integer.parseInt(bytes[i] + ""));
        }

        ByteBuffer byteBuffer = ByteBuffer.wrap(binaryHandleUtil.HexByteArray());
        try {
            session.getBasicRemote().sendBinary(byteBuffer);
            binaryHandleUtil.clearInstanceBuffer();//清楚缓存
            heartbeat();//心跳请求 保持连接
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }

    }

    @OnClose
    public void onClose() {
        logger.info(" ===> 关闭 billibili 成功");
    }

    @OnError
    public void onError(Throwable e) {
        logger.info(" ===> 连接bilibili 发生异常");
        logger.error(e.getMessage(), e);
    }

    @OnMessage
    public void onMessage(Session session, byte[] message) {
        logger.info(" ===> message:{}", message);
        ByteBuffer byteBuffer = ByteBuffer.wrap(message);
        logger.info(" ===> byteBuffer:{}", byteBuffer.toString());

        StringBuilder stringBuilder = new StringBuilder();
        while (byteBuffer.hasRemaining()) {
            byte b = byteBuffer.get();
            String s = Byte.toString(b);
            stringBuilder.append(s);
        }
        String toString = stringBuilder.toString();
        logger.info(" ===> 接收到来自bilibili的数据" + toString);

        try {
            Integer packageLength = readIntFromByteArray(byteBuffer, 0, 4);
            Integer headLength = readIntFromByteArray(byteBuffer, 4, 2);
            Integer ver = readIntFromByteArray(byteBuffer, 6, 2);
            Integer op = readIntFromByteArray(byteBuffer, 8, 4);
            Integer seq = readIntFromByteArray(byteBuffer, 12, 4);
            if (Objects.equals(op, 5)) {
                String body;
                int offset = 0;
                while (offset < message.length) {
                    int countPackageLength = readIntFromByteArray(byteBuffer, offset + 0, 4);
                    int countHeadLength = 16;
                    byte[] car = new byte[countHeadLength + countPackageLength];
                    logger.info(" ===> packageLength:{}", packageLength);
                    logger.info(" ===> car:{}, offset:{}, length:{}", car.length, offset + countHeadLength, countPackageLength - countHeadLength - offset);
                    byteBuffer.rewind();
                    logger.info(" ===> position:{}", byteBuffer.position());
                    logger.info(" ===> limit:{}", byteBuffer.limit());
                    byteBuffer.get(car, offset + countHeadLength, countPackageLength - countHeadLength - offset);
    //                let body = "{}"
                    Inflater inflater = new Inflater();
                    if (Objects.equals(ver, 2)) {
                        //协议版本为 2 时  数据有进行压缩
                        int inflate = inflater.inflate(car);
                        String s = new String(car, 0, inflate, "UTF-8");
                        logger.info(" ===> s:{}", s);
                    } else {
                        //协议版本为 0 时  数据没有进行压缩
                        int inflate = inflater.inflate(car);
                    }
    //                if (body) {
    //                    // 同一条消息中可能存在多条信息，用正则筛出来
    //                    const group = body.split(/[\x00-\x1f]+/);
    //                    group.forEach(item => {
    //                    try {
    //                        result.body.push(JSON.parse(item));
    //                    }catch (e) {
    //                        // 忽略非JSON字符串，通常情况下为分隔符
    //                    }
    //                    });
    //                }
                    offset += countPackageLength;
                }
            }
        } catch (DataFormatException e) {
            logger.error(e.getMessage(), e);
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
        }
        byteBuffer.clear();
    }

    private ByteArrayBuffer buildCertifyByte(JSONObject jsonObject) {
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

 /*   private void certifyRequest(Integer roomId, String url) {
        logger.info("===" + "发送认证请求");
        JsScriptUtil.certifyRequest(roomId, url);
    }*/

    @Scheduled(fixedRate = 30 * 1000)
    private void heartbeat() {
        binaryHandleUtil.setUnit(0, UnitEnum.UNIT32, 0)
                .setUnit(4, UnitEnum.UNIT16, 16)
                .setUnit(6, UnitEnum.UNIT16, 1)
                .setUnit(8, UnitEnum.UNIT32, 2)
                .setUnit(12, UnitEnum.UNIT32, 1);
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryHandleUtil.HexByteArray());
            this.session.getBasicRemote().sendBinary(byteBuffer);
            logger.info(" ===> heartbeat ing");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Integer readIntFromByteArray(byte[] byteArray, Integer start, Integer len) {
        Double result = 0.0;
        for (int i = len - 1; i >= 0; i--) {
            result += Math.pow(256, len - i - 1) * byteArray[start + i];
        }
        logger.info(" ===> result = " + result.intValue());
        return result.intValue();
    }

    private Integer readIntFromByteArray(ByteBuffer byteBuffer, Integer start, Integer len) {
        Double result = 0.0;
        for (int i = len - 1; i >= 0; i--) {
            result += Math.pow(256, len - i - 1) * byteBuffer.get(start + i);
        }
        logger.info(" ===> result = " + result.intValue());
        return result.intValue();
    }

    private Integer readIntFromByteArray(char[] byteBuffer, Integer start, Integer len) {
        Double result = 0.0;
        for (int i = len - 1; i >= 0; i--) {
            result += Math.pow(256, len - i - 1) * byteBuffer[start + i];
        }
        logger.info(" ===> result = " + result.intValue());
        return result.intValue();
    }

}
