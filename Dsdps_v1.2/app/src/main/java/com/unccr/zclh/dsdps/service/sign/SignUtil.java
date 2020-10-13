package com.unccr.zclh.dsdps.service.sign;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Map;

public class SignUtil {

    public static String checkSign(Map<String, String> param) {
        String[] seq = {"mac", "sn", "time"};
        String token = "0XN122BX2m3rhr8327263fb783YZn67b67a3ex621378Y357";
        String str = "";
        for (String k : seq) {
            str += k + "=" + param.get(k) + "&&";
        }
        str += "token=" + token;
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            byte[] bytes = digest.digest(str.getBytes());
            String hexString = "";
            for (byte b : bytes) {
                int temp = b & 255;
                if (temp < 16 && temp >= 0) {
                    hexString = hexString + "0" + Integer.toHexString(temp);
                } else {
                    hexString = hexString + Integer.toHexString(temp);
                }
            }
            str = hexString.toUpperCase();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String getMD5(String str) {
        byte[] secretBytes = null;
        try {
            // 生成一个MD5加密计算摘要
            MessageDigest md = MessageDigest.getInstance("MD5");
            //对字符串进行加密
            md.update(str.getBytes());
            //获得加密后的数据
            secretBytes = md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("没有md5这个算法！");
        }
        int i;//定义整型
        //声明StringBuffer对象
        StringBuffer buf = new StringBuffer("");
        for (int offset = 0; offset < secretBytes.length; offset++) {
            i = secretBytes[offset];//将首个元素赋值给i
            if (i < 0)
                i += 256;
            if (i < 16)
                buf.append("0");//前面补0
            buf.append(Integer.toHexString(i));//转换成16进制编码
        }
        return buf.toString();
    }
}
