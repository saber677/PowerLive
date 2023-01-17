package com.liveQIQI.tool.utils;

import com.liveQIQI.enums.UintEnum;
import com.liveQIQI.service.impl.BinaryHandleInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Objects;

@Component
public class BinaryHandleUtil {

    private static final String COMMA = ",";

    @Autowired
    private BinaryHandleInstance instance;

    public BinaryHandleUtil setUnit(Integer offset, UintEnum unit, Integer value) {

        if (Objects.isNull(value)) {
            throw new RuntimeException(" ===> value is null");
        }

        String valueStr = Integer.toHexString(value);
        instance.handleByteData(offset, unit, valueStr);
        return this;
    }

    public String getHexBytesStr() {
        return this.instance.getReqParam().toString();
    }

    public byte[] HexStrToByteArray() {

        String hexStr = this.getHexBytesStr();

        if (Objects.isNull(hexStr)) {
            return null;
        }

        if (Objects.equals(hexStr, 0)) {
            return new byte[0];
        }

        byte[] byteArray = new byte[hexStr.length() / 2];

        for (int i = 0; i < byteArray.length; i++) {
            String subStr = hexStr.substring(2 * i, 2 * i + 2);
            byteArray[i] = ((byte) Integer.parseInt(subStr, 16));
        }
        return byteArray;
    }

    public byte[] HexByteArray() {
        return this.HexStrToByteArray();
    }

    public void clearInstanceBuffer() {
        instance.setReqParam(new StringBuffer());
    }

    public int[] toUintArrayFromByteArray(byte[] bytes) {
        int[] uintArray = new int[bytes.length];
        for (int i = 0; i < bytes.length; i++) {
            uintArray[i] = Byte.toUnsignedInt(bytes[i]);
        }
        return uintArray;
    }

    public StringBuffer toBinaryStrFromUintArray(int value, StringBuffer builder) {

        if (Objects.isNull(value)){
            throw new RuntimeException(" value is null");
        }

        String param1 = new String(String.valueOf(value));
        String param2 = new String(COMMA);
        builder.append(param1);
        builder.append(param2);
        return builder;
    }

    public String getStrByDecompress(StringBuffer buffer){
        buffer.deleteCharAt(buffer.length() - 1);
        String binaryStr = buffer.toString();//binaryStr: 0, 0, 0, 26, 0, 16, 0, 1, 0, 0, 0, 8

        if (Objects.isNull(binaryStr)){
            throw new RuntimeException("binaryStr is null");
        }

        byte[] clientBytes = PakoUtil.receive(binaryStr);
        byte[] bytes = ZlibUtil.decompress(clientBytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

    public byte[] getByteArrayFromInt(int value){
        byte[] bytes = new byte[1];
        bytes[0] =  (byte) (value & 0xFF);
//        bytes[1] =  (byte) ((value>>8) & 0xFF);
//        bytes[2] =  (byte) ((value>>16) & 0xFF);
//        bytes[3] =  (byte) ((value>>24) & 0xFF);
        return bytes;
    }

}
