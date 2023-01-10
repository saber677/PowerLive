package com.liveQIQI.tool.utils;

import com.liveQIQI.enums.UnitEnum;
import com.liveQIQI.service.impl.BinaryHandleInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Objects;

@Component
public class BinaryHandleUtil {

    @Autowired
    private BinaryHandleInstance instance;

    public BinaryHandleUtil setUnit(Integer offset, UnitEnum unit, Integer value){

        if (Objects.isNull(value)){
            throw new RuntimeException(" ===> value is null");
        }

        String valueStr = Integer.toHexString(value);
        StringBuffer stringBuffer = instance.handleByteData(offset, unit, valueStr);
        String s = stringBuffer.toString();
        return this;
    }

    public String getHexBytesStr(){
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

    public byte[] HexStrToByteArray2() {

        String hexStr = this.getHexBytesStr();

        if (Objects.isNull(hexStr)) {
            return null;
        }

        if (Objects.equals(hexStr, 0)) {
            return new byte[0];
        }

        int parseInt = Integer.parseInt(hexStr, 16);
        BigInteger bigInteger = BigInteger.valueOf(parseInt);
        return bigInteger.toByteArray();
    }


    public byte[] HexByteArray(){
        return this.HexStrToByteArray();
    }

}
