package com.hch.viewlib.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

/**
 * @description：网络相关类
 * @author samy
 * @date 2014年9月17日 下午9:10:30
 */
public class NetUtil {

	/**
	 * 检测是否有活动网络
	 */
	public static boolean hasAvailableNet(Context context) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);// 获取系统的连接服务
		if (connectivityManager == null) return false;
		NetworkInfo activeNetInfo = connectivityManager.getActiveNetworkInfo();// 获取网络的连接情况
		if (activeNetInfo != null && activeNetInfo.isConnected() && activeNetInfo.isAvailable()) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean isConnectionType(Context context, int type) {
		ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo active = cm.getActiveNetworkInfo();
		return (active != null && active.getType() == type);
	}

	public static boolean isWIFI(Context context) {
		return isConnectionType(context, ConnectivityManager.TYPE_WIFI);
	}

	public static boolean is2GNetWork(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo net = cm.getActiveNetworkInfo();
			if (net.getState() == State.CONNECTED) {
				int type = net.getType();
				int subtype = net.getSubtype();

				return !isConnectionFast(type, subtype);
			}
		}
		return false;
	}

	public static boolean isConnectionFast(int type, int subType) {
		if (type == ConnectivityManager.TYPE_WIFI) {
			return true;
		} else if (type == ConnectivityManager.TYPE_MOBILE) {
			switch (subType) {
				case TelephonyManager.NETWORK_TYPE_1xRTT:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return false; // ~ 14-64 kbps
				case TelephonyManager.NETWORK_TYPE_EDGE:
					return false; // ~ 50-100 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_0:
					return true; // ~ 400-1000 kbps
				case TelephonyManager.NETWORK_TYPE_EVDO_A:
					return true; // ~ 600-1400 kbps
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return false; // ~ 100 kbps
				case TelephonyManager.NETWORK_TYPE_HSDPA:
					return true; // ~ 2-14 Mbps
				case TelephonyManager.NETWORK_TYPE_HSPA:
					return true; // ~ 700-1700 kbps
				case TelephonyManager.NETWORK_TYPE_HSUPA:
					return true; // ~ 1-23 Mbps
				case TelephonyManager.NETWORK_TYPE_UMTS:
					return true; // ~ 400-7000 kbps
				case TelephonyManager.NETWORK_TYPE_UNKNOWN:
					return false;
				default:
					return false;
			}
		} else {
			return false;
		}
	}

	public static boolean isNetworkAvailable(Context ctx) {
		// 获得网络系统连接服务
		ConnectivityManager manager = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 获得WIFI状态
		State state = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		if (State.CONNECTED == state) { return true; }
		// 获取MOBILE状态
		state = manager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();
		if (State.CONNECTED == state) { return true; }
		return false;
	}

	public static boolean isWifiOpen(Context context) {
		ConnectivityManager conManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo wifinfo = conManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (wifinfo != null) {
			State wifi = wifinfo.getState();
			if (wifi == State.CONNECTED || wifi == State.CONNECTING) { return true; }
		}
		return false;
	}
	
	/**
	* @Title: getIpAdress
	* @Description: TODO 获取ip地址
	* @author hechuang 
	* @date 2015-12-17
	* @param @return    设定文件
	* @return String    返回类型
	*/ 
	public static String getIpAdress(Context ctx){
		String ip = "";
		//获取wifi服务  
		WifiManager wifiManager = (WifiManager) ctx.getSystemService(Context.WIFI_SERVICE);  
		//判断wifi是否开启  
		if (!wifiManager.isWifiEnabled()) {  
			wifiManager.setWifiEnabled(true);    
		}  
		WifiInfo wifiInfo = wifiManager.getConnectionInfo();       
		int ipAddress = wifiInfo.getIpAddress();   
		ip = intToIp(ipAddress);   
		return ip;
	}
    private static String intToIp(int i) {       
        
        return (i & 0xFF ) + "." +       
      ((i >> 8 ) & 0xFF) + "." +       
      ((i >> 16 ) & 0xFF) + "." +       
      ( i >> 24 & 0xFF) ;  
   }   
}
