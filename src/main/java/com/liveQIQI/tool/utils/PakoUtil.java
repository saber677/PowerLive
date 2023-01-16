package com.liveQIQI.tool.utils;

public class PakoUtil {


        public static byte[] receive(String arrInt){

            /**
             * 将数字字符串 ->  byte[]
             */
            String[] a = arrInt.split(",");//arrInt: 0, 0, 0, 26, 0, 16, 0, 1, 0, 0, 0, 8,
            byte[] clientBytes = new byte[a.length];
            int i = 0;
            for (String e : a) {
                clientBytes[i] = Integer.valueOf(e).byteValue();
                i++;
            }
            return clientBytes;
        }

        /**
         * 发送给 Pako 的数据格式
         * @param bytes 服务端生成的字节数组
         * @return String 发送给 pako.js 的数据格式
         */
        public static String send(byte[] bytes) {
            String[] ints = new String[bytes.length];
            int j=0;
            for(byte e:bytes) {
                int t = e;
                if(t<0) {
                    t = 256+t;
                }
                ints[j++] = String.valueOf(t);

            }
            return String.join(",", ints);
        }

}
