package com.android.mytest.utils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

public class SignUtil {

    public static String checkSign(Map<String,String> param) {
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
}
