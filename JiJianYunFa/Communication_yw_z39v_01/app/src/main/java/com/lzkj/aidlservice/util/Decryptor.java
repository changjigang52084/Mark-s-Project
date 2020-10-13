package com.lzkj.aidlservice.util;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

/**
 *@author kchang changkai@lz-mr.com
 *@Description:加密解密的类
 *@time:2016年4月15日 下午2:33:33
 */
public class Decryptor {
	
	public static final String enCipherMsg(String plainText) throws Exception {
        byte[] cipherText = null;

        try {
        	Cipher cipher = Cipher.getInstance(SystemConstant.DECRYPTOR_ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, getSystemKey());
            cipherText = cipher.doFinal(plainText.getBytes());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return byteArr2HexStr(cipherText);
    }
	
	public static String deCipherMsg(String cipherText) throws Exception {
        byte[] sourceText = null;
        try {
        	Cipher cipher = Cipher.getInstance(SystemConstant.DECRYPTOR_ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, getSystemKey());
            sourceText = cipher.doFinal(hexStr2ByteArr(cipherText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new String(sourceText);

    }
	
	/* Private Method */
	private static Key getSystemKey(){
        return new SecretKeySpec(SystemConstant.DECRYPTOR_KEY.getBytes(), "DES");
	}
	
	/**  
     * 将byte数组转换为表示16进制值的字符串， 如：byte[]{8,18}转换为：0813， 和public static byte[]  
     * hexStr2ByteArr(String strIn) 互为可逆的转换过程  
     * @param arrB  需要转换的byte数组  
     * @return 转换后的字符串  
     * @throws Exception 本方法不处理任何异常，所有异常全部抛出  
     */    
    private static String byteArr2HexStr(byte[] arrB) throws Exception {    
        int iLen = arrB.length;    
        // 每个byte用两个字符才能表示，所以字符串的长度是数组长度的两倍    
        StringBuffer sb = new StringBuffer(iLen * 2);    
        for (int i = 0; i < iLen; i++) {    
            int intTmp = arrB[i];    
            // 把负数转换为正数    
            while (intTmp < 0) {    
                intTmp = intTmp + 256;    
            }    
            // 小于0F的数需要在前面补0    
            if (intTmp < 16) {    
                sb.append("0");    
            }    
            sb.append(Integer.toString(intTmp, 16));    
        }    
        return sb.toString();    
    }    
    /**  
     * 将表示16进制值的字符串转换为byte数组， 和public static String byteArr2HexStr(byte[] arrB)  
     * 互为可逆的转换过程  
     * @param strIn 需要转换的字符串  
     * @return 转换后的byte数组  
     */    
    private static byte[] hexStr2ByteArr(String strIn) throws Exception {    
        byte[] arrB = strIn.getBytes();    
        int iLen = arrB.length;    
        // 两个字符表示一个字节，所以字节数组长度是字符串长度除以2    
        byte[] arrOut = new byte[iLen / 2];    
        for (int i = 0; i < iLen; i = i + 2) {    
            String strTmp = new String(arrB, i, 2);    
            arrOut[i / 2] = (byte) Integer.parseInt(strTmp, 16);    
        }    
        return arrOut;    
    }
}
