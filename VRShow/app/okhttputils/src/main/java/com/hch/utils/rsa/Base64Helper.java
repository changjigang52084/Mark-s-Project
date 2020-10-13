package com.hch.utils.rsa;

import java.io.IOException;

import Decoder.BASE64Decoder;
import Decoder.BASE64Encoder;


/**
 * RSA64编码或转码工具类
 * 
 * @author 易茂剑
 * 
 */
public class Base64Helper {

	/**
	 * 编码Byte[]转字符串
	 * 
	 * @param byteArray
	 * @return
	 */
	public static String encode(byte[] byteArray) {
		BASE64Encoder base64Encoder = new BASE64Encoder();
		return base64Encoder.encode(byteArray);
	}

	/**
	 * 编码字符中转Byte[]
	 * 
	 * @param base64EncodedString
	 * @return
	 */
	public static byte[] decode(String base64EncodedString) {
		BASE64Decoder base64Decoder = new BASE64Decoder();
		try {
			return base64Decoder.decodeBuffer(base64EncodedString);
		} catch (IOException e) {
			return null;
		}
	}

}