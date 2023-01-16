package com.liveQIQI.websocket.client;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.liveQIQI.enums.UintEnum;
import com.liveQIQI.tool.entity.Regex;
import com.liveQIQI.tool.utils.BinaryHandleUtil;
import com.sun.xml.internal.ws.util.ByteArrayBuffer;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.websocket.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.DataFormatException;

@ClientEndpoint
@Component
public class ClientSocket {

    private static final Logger logger = LoggerFactory.getLogger(ClientSocket.class);

    private static final Pattern PATTERN = Pattern.compile(Regex.KEY_VALUE_JSON_STR);

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
        json.put("roomid", 21144080);//todo _
        json.put("protover", 1);
        json.put("platform", "web");
        json.put("clientver", "1.4.0");
//        json.put("type", 2);
//        json.put("key", "u91Sk476h2FV7KCv59U35sEAuVNmQUq0yBfeqJVHIKZqkxVPe_hJYD1GKS-cS43jNMVpD_TEih7K-ybwqpGvotO7luheMjhEi0w7wjILrm8WJ-dL4xid3D0rnJiP7QruH3xTVYNw41xffdF5-UM=");

        ByteArrayBuffer dataBytes = buildCertifyByte(json);
        if (Objects.isNull(dataBytes)) {
            throw new RuntimeException(" ===> 认证数据结果为空");
        }
        int size = dataBytes.size();
        byte[] bytes = dataBytes.getRawData();

        binaryHandleUtil.setUnit(0, UintEnum.UINT32, size + 16)
                .setUnit(4, UintEnum.UINT16, 16)
                .setUnit(6, UintEnum.UINT16, 1)
                .setUnit(8, UintEnum.UINT32, 7)
                .setUnit(12, UintEnum.UINT32, 1);
        for (int i = 0; i < size; i++) {
            binaryHandleUtil.setUnit(16 + i, UintEnum.UINT8, Integer.parseInt(bytes[i] + ""));
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
//        logger.info(" ===> message:{}", message);
        int[] uintArray = binaryHandleUtil.toUintArrayFromByteArray(message);

        Integer packageLength = readIntFromByteArray(uintArray, 0, 4);
        Integer headLength = readIntFromByteArray(uintArray, 4, 2);
        Integer ver = readIntFromByteArray(uintArray, 6, 2);
        Integer op = readIntFromByteArray(uintArray, 8, 4);
        Integer seq = readIntFromByteArray(uintArray, 12, 4);
        if (Objects.equals(op, 5)) {
            String bodyStr;
            int offset = 0;
            while (offset < packageLength) {
                int countPackageLength = readIntFromByteArray(uintArray, offset + 0, 4);
                int countHeadLength = 16;
                int[] data = Arrays.copyOfRange(uintArray, offset + countHeadLength, offset + countPackageLength);
                StringBuffer buffer = new StringBuffer();
                if (Objects.equals(ver, 2)) {
                    //协议版本为 2 时  数据有进行压缩
                    for (int value : data) {
                        buffer = binaryHandleUtil.toBinaryStrFromUintArray(value, buffer);
                    }
                    bodyStr = binaryHandleUtil.getStrByDecompress(buffer);
                    logger.info(" ===> strByDecompress:{}", bodyStr);
                } else {
                    //协议版本为 0 时  数据没有进行压缩
                    for (int value : data) {
                        byte[] array = binaryHandleUtil.getByteArrayFromInt(value);
                        String strByByteArray = new String(array, StandardCharsets.UTF_8);
                        buffer.append(strByByteArray);
                    }
                    bodyStr = buffer.toString();
                    logger.info(" ===> str:{}", bodyStr);
                }

                // 同一条消息中可能存在多条信息，用正则筛出来
                Matcher matcher = PATTERN.matcher(bodyStr);
                String group = "";
                Map map = null;
                while (matcher.find()) {
                    try {
                        group = matcher.group();
                        logger.info(" ===> group:{}", group);
//                        map = JSON.parseObject(group, Map.class);
                    } catch (Exception e) {
                        logger.error(e.getMessage(), e);
                    }
                }

//                group.forEach(item = > {
//                try {
//                    result.body.push(JSON.parse(item));
//                } catch (e) {
//                    // 忽略非JSON字符串，通常情况下为分隔符
//                }
//                                    });
                offset += countPackageLength;
            }
        }
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
        binaryHandleUtil.setUnit(0, UintEnum.UINT32, 0)
                .setUnit(4, UintEnum.UINT16, 16)
                .setUnit(6, UintEnum.UINT16, 1)
                .setUnit(8, UintEnum.UINT32, 2)
                .setUnit(12, UintEnum.UINT32, 1);
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(binaryHandleUtil.HexByteArray());
            this.session.getBasicRemote().sendBinary(byteBuffer);
            logger.info(" ===> heartbeat ing");
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

    private Integer readIntFromByteArray(int[] byteBuffer, Integer start, Integer len) {
        Double result = 0.0;
        for (int i = len - 1; i >= 0; i--) {
            result += Math.pow(256, len - i - 1) * byteBuffer[start + i];
        }
        return result.intValue();
    }

    private void clearBuffer(StringBuffer buffer) {
        buffer.delete(0, buffer.length());
    }

    public static void main(String[] args) throws DataFormatException, UnsupportedEncodingException {
//        String s = "{\"cmd\":\"WATCHED_CHANGE\",\"data\":{\"num\":35970810,\"text_small\":\"3597.0万\",\"text_large\":\"3597.0万人看过\"}}\u0003�\u0010\u0005{\"cmd\":\"DANMU_MSG\",\"info\":[[0,1,25,16777215,1673858806363,11607964,0,\"6011d5ae\",0,0,0,\"\",0,\"{}\",\"{}\",{\"mode\":0,\"show_player_type\":0,\"extra\":\"{\\\"send_from_me\\\":false,\\\"mode\\\":0,\\\"color\\\":16777215,\\\"dm_type\\\":0,\\\"font_size\\\":25,\\\"player_mode\\\":1,\\\"show_player_type\\\":0,\\\"content\\\":\\\"666\\\",\\\"user_hash\\\":\\\"1611781550\\\",\\\"emoticon_unique\\\":\\\"\\\",\\\"bulge_display\\\":0,\\\"recommend_score\\\":2,\\\"main_state_dm_color\\\":\\\"\\\",\\\"objective_state_dm_color\\\":\\\"\\\",\\\"direction\\\":0,\\\"pk_direction\\\":0,\\\"quartet_direction\\\":0,\\\"anniversary_crowd\\\":0,\\\"yeah_space_type\\\":\\\"\\\",\\\"yeah_space_url\\\":\\\"\\\",\\\"jump_to_url\\\":\\\"\\\",\\\"space_type\\\":\\\"\\\",\\\"space_url\\\":\\\"\\\",\\\"animation\\\":{},\\\"emots\\\":null}\"},{\"activity_identity\":\"\",\"activity_source\":0,\"not_show\":0}],\"666\",[1085936274,\"SkywalkerForever\",0,0,0,10000,1,\"\"],[10,\"Zc憨憨\",\"魔法Zc目录\",3044248,9272486,\"\",0,9272486,9272486,9272486,0,1,13164144],[0,0,9868950,\"\\u003e50000\",0],[\"\",\"\"],0,0,null,{\"ts\":1673858806,\"ct\":\"77365047\"},0,0,null,null,0,42]}";
        String s = "{\"cmd\":\"DANMU_MSG\",\"info\":[[0,1,25,16777215,1673881110416,1673881101,0,\"748aa4fc\",0,0,0,\"\",0,\"{}\",\"{}\",{\"mode\":0,\"show_player_type\":0,\"extra\":\"{\\\"send_from_me\\\":false,\\\"mode\\\":0,\\\"color\\\":16777215,\\\"dm_type\\\":0,\\\"font_size\\\":25,\\\"player_mode\\\":1,\\\"show_player_type\\\":0,\\\"content\\\":\\\"kana;\\\",\\\"user_hash\\\":\\\"1955243260\\\",\\\"emoticon_unique\\\":\\\"\\\",\\\"bulge_display\\\":0,\\\"recommend_score\\\":0,\\\"main_state_dm_color\\\":\\\"\\\",\\\"objective_state_dm_color\\\":\\\"\\\",\\\"direction\\\":0,\\\"pk_direction\\\":0,\\\"quartet_direction\\\":0,\\\"anniversary_crowd\\\":0,\\\"yeah_space_type\\\":\\\"\\\",\\\"yeah_space_url\\\":\\\"\\\",\\\"jump_to_url\\\":\\\"\\\",\\\"space_type\\\":\\\"\\\",\\\"space_url\\\":\\\"\\\",\\\"animation\\\":{},\\\"emots\\\":null}\"},{\"activity_identity\":\"\",\"activity_source\":0,\"not_show\":0}],\"kana;\",[49515343,\"nenpenAIagi\",0,0,0,10000,1,\"\"],[],[0,0,9868950,\"\\u003e50000\",0],[\"\",\"\"],0,0,null,{\"ts\":1673881110,\"ct\":\"762E818\"},0,0,null,null,0,7]}\u0000\u0000\u0000{\u0000\u0010\u0000\u0000\u0000\u0000\u0000\u0005\u0000\u0000\u0000\u0000{\"cmd\":\"WATCHED_CHANGE\",\"data\":{\"num\":35975380,\"text_small\":\"3597.5万\",\"text_large\":\"3597.5万人看过\"}}";

//        Pattern compile = Pattern.compile("[\\x00-\\xff].?");
        Pattern compile = Pattern.compile("\\{\"cmd\"[^\\}].*");
        Matcher matcher = compile.matcher(s);

        StringBuffer stringBuffer = new StringBuffer();
        while (matcher.find()) {
            String group = matcher.group();
            stringBuffer.append(group);
//            logger.info(" ===> result:{}", group);
        }
        logger.info(" ===> result:{}", stringBuffer.toString());


    }
}