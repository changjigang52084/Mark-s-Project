package com.android.mytest.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Locale;

public class MacUtil {

    /**
     * 获取Ethernet的MAC地址
     * @return
     */
    public static String getEthernetMacAddress() {
        try {
            String mac = "02:00:00:00:00:00";
            mac = loadFileAsString("/sys/class/net/eth0/address").toUpperCase(Locale.ENGLISH).substring(0, 17);
            return mac.replace(":","").toUpperCase();
        } catch (IOException e) {
            return null;
        }
    }

    public static String loadFileAsString(String filePath) throws IOException{
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024]; int numRead=0;
        while((numRead=reader.read(buf)) != -1){
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
        }
        reader.close();
        return fileData.toString();
    }
}
