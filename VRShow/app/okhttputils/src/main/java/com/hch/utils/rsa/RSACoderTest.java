package com.hch.utils.rsa;


/**
 * RSA加密
 * 
 * @author 易茂剑
 */
public class RSACoderTest {

	public static void test() throws Exception {
		System.err.println("公钥加密——私钥解密");
		String inputStr = "password";
		byte[] data = inputStr.getBytes();
//
//		byte[] encodedData = RSACoder.encryptByPublicKey(data, VR_KeyUtils.publicKey);
//		String mw = Base64.encode(encodedData);
//		System.out.println("密文：");
//		System.out.println(mw);
//		byte[] decodedData = RSACoder.decryptByPrivateKey(Base64.decode(mw), VR_KeyUtils.privateKey);
//		String outputStr = new String(decodedData);
//		System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);

	}

	public static void main(String[] args) {
		try {
			test();
			//testSign();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

//	public static void testSign() throws Exception {
//		System.err.println("私钥加密——公钥解密");
//		String inputStr = "delcom";
//		byte[] data = inputStr.getBytes();
//
//		byte[] encodedData = RSACoder.encryptByPublicKey(data, VR_KeyUtils.publicKey);
//
//		byte[] decodedData = RSACoder.decryptByPrivateKey(encodedData, VR_KeyUtils.privateKey);
//
//		String outputStr = new String(decodedData);
//		System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);
//
//		System.err.println("私钥签名——公钥验证签名");
//		// 产生签名
//		String sign = RSACoder.sign(encodedData, VR_KeyUtils.privateKey);
//		System.err.println("签名:\r" + sign);
//
//		// 验证签名
//		boolean status = RSACoder.verify(encodedData, VR_KeyUtils.publicKey, sign);
//		System.err.println("状态:\r" + status);
//
//	}

}
