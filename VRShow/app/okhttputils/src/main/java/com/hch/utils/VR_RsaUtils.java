package com.hch.utils;


import com.hch.filedownloader.util.StringUtils;
import com.hch.util.io.stream.ByteArrayOutputStream;
import com.hch.utils.rsa.Base64;
import com.hch.utils.rsa.RSACoder;

/**
 * RSA加密/解密工具类
 * 
 * @author 易茂剑
 * 
 */
public class VR_RsaUtils {
	/*
	* 16进制数字字符集
	*/
	private static String hexString="0123456789ABCDEF";

	/**
	* @Title: encodeByPublicKey
	* @Description: TODO 对字符串进行公钥加密
	* @author hechuang 
	* @param @param inputStr
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	*/ 
	public static String encodeByPublicKey(String inputStr){
		if(StringUtils.isEmpty(inputStr)){
			return  null;
		}
		byte[] data = inputStr.getBytes();

		byte[] encodedData = null;
		try {
			encodedData = RSACoder.encryptByPublicKey(data, OkhttpConstant.RSA_KEY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return Base64.encode(encodedData);
	}
	/**
	* @Title: decryptByPublicKey
	* @Description: TODO 对字符串进行公钥解密
	* @author hechuang
	* @param @param inputStr
	* @param @return    设定文件
	* @return String    返回类型
	* @throws
	*/
	public static String decryptByPublicKey(String inputStr){
		if(inputStr == null){
			return "";
		}

		byte[] data =Base64.decode(inputStr);

		byte[] encodedData = null;
		try {
			encodedData = RSACoder.decryptByPublicKey(data, OkhttpConstant.RSA_KEY);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return new String(encodedData);
	}


	/*
	* 将字符串编码成16进制数字,适用于所有字符（包括中文）
	*/
	public static String encodeTo16(String str)
	{
		//根据默认编码获取字节数组
		byte[] bytes=str.getBytes();
		StringBuilder sb=new StringBuilder(bytes.length*2);
		//将字节数组中每个字节拆解成2位16进制整数
		for(int i=0;i<bytes.length;i++)
		{
			sb.append(hexString.charAt((bytes[i]&0xf0)>>4));
			sb.append(hexString.charAt((bytes[i]&0x0f)>>0));
		}
		return sb.toString();
	}

	/*
	* 将16进制数字解码成字符串,适用于所有字符（包括中文）
	*/
	public static String decodeFrom16(String bytes)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream(bytes.length()/2);
		//将每2位16进制整数组装成一个字节
		for(int i=0;i<bytes.length();i+=2) {
			baos.write((hexString.indexOf(bytes.charAt(i))<<4 |hexString.indexOf(bytes.charAt(i+1))));
		}
		return new String(baos.toByteArray());
	}


}
