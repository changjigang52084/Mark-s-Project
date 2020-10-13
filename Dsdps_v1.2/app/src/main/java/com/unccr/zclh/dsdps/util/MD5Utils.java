package com.unccr.zclh.dsdps.util;


import android.os.Build;

import androidx.annotation.RequiresApi;

import org.apache.commons.codec.binary.Base64;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Utils {
    private static final int CHUNK_SIZE = 4194304;

    public MD5Utils() {
    }

    public static String string2MD5(String inStr) {
        MessageDigest md5 = null;

        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception var8) {
            System.out.println(var8.toString());
            var8.printStackTrace();
            return "";
        }

        char[] charArray = inStr.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for(int md5Bytes = 0; md5Bytes < charArray.length; ++md5Bytes) {
            byteArray[md5Bytes] = (byte)charArray[md5Bytes];
        }

        byte[] var9 = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();

        for(int i = 0; i < var9.length; ++i) {
            int val = var9[i] & 255;
            if(val < 16) {
                hexValue.append("0");
            }

            hexValue.append(Integer.toHexString(val));
        }

        return hexValue.toString();
    }

    public static String convertMD5(String inStr) {
        char[] a = inStr.toCharArray();

        for(int i = 0; i < a.length; ++i) {
            a[i] = (char)(a[i] ^ 116);
        }

        return new String(a);
    }

    private static byte[] sha1(byte[] data) throws NoSuchAlgorithmException {
        MessageDigest mDigest = MessageDigest.getInstance("sha1");
        return mDigest.digest(data);
    }

    private static String urlSafeBase64Encode(byte[] data) {
        byte[] encodedData = Base64.encodeBase64(data);
        String encodedString = new String(encodedData);
        encodedString = encodedString.replace('+', '-').replace('/', '_');
        return encodedString;
    }

    public static String getHashToFilePath(String fileNamePath) throws IOException, NoSuchAlgorithmException {
        String etag = "";
        File file = new File(fileNamePath);
        if(file.exists() && file.isFile() && file.canRead()) {
            long fileLength = file.length();
            FileInputStream inputStream = new FileInputStream(file);
            byte[] allSha1Data;
            int allSha1DataSha1;
            byte[] hashData;
            if(fileLength <= 4194304L) {
                byte[] chunkCount = new byte[(int)fileLength];
                inputStream.read(chunkCount, 0, (int)fileLength);
                allSha1Data = sha1(chunkCount);
                allSha1DataSha1 = allSha1Data.length;
                hashData = new byte[allSha1DataSha1 + 1];
                System.arraycopy(allSha1Data, 0, hashData, 1, allSha1DataSha1);
                hashData[0] = 22;
                etag = urlSafeBase64Encode(hashData);
            } else {
                int var14 = (int)(fileLength / 4194304L);
                if(fileLength % 4194304L != 0L) {
                    ++var14;
                }

                allSha1Data = new byte[0];

                for(allSha1DataSha1 = 0; allSha1DataSha1 < var14; ++allSha1DataSha1) {
                    hashData = new byte[4194304];
                    int bytesReadLen = inputStream.read(hashData, 0, 4194304);
                    byte[] bytesRead = new byte[bytesReadLen];
                    System.arraycopy(hashData, 0, bytesRead, 0, bytesReadLen);
                    byte[] chunkDataSha1 = sha1(bytesRead);
                    byte[] newAllSha1Data = new byte[chunkDataSha1.length + allSha1Data.length];
                    System.arraycopy(allSha1Data, 0, newAllSha1Data, 0, allSha1Data.length);
                    System.arraycopy(chunkDataSha1, 0, newAllSha1Data, allSha1Data.length, chunkDataSha1.length);
                    allSha1Data = newAllSha1Data;
                }

                byte[] var15 = sha1(allSha1Data);
                hashData = new byte[var15.length + 1];
                System.arraycopy(var15, 0, hashData, 1, var15.length);
                hashData[0] = -106;
                etag = urlSafeBase64Encode(hashData);
            }

            inputStream.close();
            return etag;
        } else {
            System.err.println("Error: File not found or not readable");
            return etag;
        }
    }

    /**
     * 计算文件 MD5
     * @param file
     * @return 返回文件的md5字符串，如果计算过程中任务的状态变为取消或暂停，返回null， 如果有其他异常，返回空字符串
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String createCheckSum(File file) {
        try {
//            InputStream stream = Files.newInputStream(file.toPath(),StandardOpenOption.READ);
            FileInputStream stream = new FileInputStream(file);
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] buf = new byte[8192];
            int len;
            while ((len = stream.read(buf)) > 0) {
                digest.update(buf, 0, len);
            }
            return bytesToHexString(digest.digest());
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return "";
        }
    }

    public static String bytesToHexString(byte[] bytes){
        StringBuffer sb = new StringBuffer(bytes.length);
        String sTemp;

        for(int i=0; i<bytes.length;i++){
            sTemp = Integer.toHexString(0XFF & bytes[i]);
            if(sTemp.length() < 2){
                sb.append(0);
            }
            sb.append(sTemp.toLowerCase());
        }
        return sb.toString();
    }
}