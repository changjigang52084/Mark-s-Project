package com.unccr.zclh.dsdps.util;

public class IpAddress {

//    private static String ipAddress = "119.23.249.140";
    private static String ipAddress = "192.168.10.33";

    public static String getIpAddress() {
        return ipAddress;
    }

    public static void setIpAddress(String ipAddress) {
        IpAddress.ipAddress = ipAddress;
    }
}
