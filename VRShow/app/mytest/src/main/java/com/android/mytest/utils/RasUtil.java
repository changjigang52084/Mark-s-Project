package com.android.mytest.utils;

import com.hch.filedownloader.util.StringUtils;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.rsa.Base64;
import com.hch.utils.rsa.RSACoder;

public class RasUtil {

    /**
     * 对字符串进行公钥加密
     * @param inputStr
     * @return
     */
    public static String encodeByPublicKey(String inputStr){
        if(StringUtils.isEmpty(inputStr)){
            return null;
        }
        byte[] data = inputStr.getBytes();

        byte[] encodeData = null;
        try{
            encodeData = RSACoder.encryptByPublicKey(data, OkhttpConstant.RSA_KEY);
        }catch (Exception e){
            e.printStackTrace();
        }
        return Base64.encode(encodeData);
    }

    /**
     * 对字符串进行公钥解密
     * @param inputStr
     * @return
     */
    public static String decryptByPublicKey(String inputStr){
        if(null == inputStr){
            return "";
        }
        byte[] data = Base64.decode(inputStr);

        byte[] decodeData = null;
        try{
            decodeData = RSACoder.decryptByPublicKey(data,OkhttpConstant.RSA_KEY);
        }catch (Exception e){
            e.printStackTrace();
        }
        return new String(decodeData);
    }
}
