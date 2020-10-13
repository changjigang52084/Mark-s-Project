package com.hch.utils;


import java.security.MessageDigest;

/**
 * MD5加密工具类
 * 
 * @author 易茂剑
 * 
 */
public class VR_MD5Util {

	/**
	 * MD5加密
	 */
	public final static String MD5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F' };
		try {
			byte[] btInput = s.getBytes();
			// 获得MD5摘要算法的 MessageDigest 对象
			MessageDigest mdInst = MessageDigest.getInstance("MD5");
			// 使用指定的字节更新摘要
			mdInst.update(btInput);
			// 获得密文
			byte[] md = mdInst.digest();
			// 把密文转换成十六进制的字符串形式
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte byte0 = md[i];
				str[k++] = hexDigits[byte0 >>> 4 & 0xf];
				str[k++] = hexDigits[byte0 & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	* @Title: MD5Authentication
	* @Description: TODO md5鉴权
	* @author hechuang 
	* @param @param authorization
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	*/ 
	public static String  MD5Authentication(String authorization){
		return MD5Encryption.md5Small(authorization);
	}
	
	public static void main(String[] args) {
		System.out.println(MD5("000000"));
	}

}
