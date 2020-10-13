package com.hch.utils;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;

/**
 * User: hch
 * Date: 2016-05-14
 * Time: 17:20
 * FIXME
 */
public class Base64Util {
    public static String base64Encode(String s) {
        if (s == null) return null;
        return (new BASE64Encoder()).encode( s.getBytes() );
    }

    // 将 BASE64 编码的字符串 s 进行解码
    public static String base64Decoder(String s) {
        if (s == null) return null;
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            byte[] b = decoder.decodeBuffer(s);
            return new String(b);
        } catch (Exception e) {
            return null;
        }
    }
}
