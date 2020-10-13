package com.lzkj.aidlservice.adapter.board.smdt;

import android.content.Intent;

import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;
import com.xboot.stdcall.posix;

/**
 * SMDTPowerOnOffManager
 * @Description 视美泰开关机管理类
 * @author longyihuang
 * @date 2016-5-17 下午3:09:44
 */
public class SMDTPowerOnOffManager {
	private static final LogTag TAG = LogUtils.getLogTag(SMDTFeedDogManager.class, true);
	
	public static int CLOSE_SCREEN = 0;
	public static int OPEN_SCREEN = 1;
	
	private static final String POWER_ON_OFF_ACTION = "com.lzkj.downloadservice.receiver.POWER_ON_OFF_ACTION";
	private static final String ON_OR_OFF_TYPE = "type";
	private static final int ON_TYPE = 1;
	private static final int OFF_TYPE = 2;
	
	private static final String OFF_H = "offHour";
	private static final String OFF_M = "offMin";
	
	private static final String ON_H = "onHour";
	private static final String ON_M = "onMin";
	
	/**
	 * 关闭屏幕输出
	 */
	public static void closeScreen() {
//		SmdtManager.setBackLight(CLOSE_SCREEN);
	}
	/**
	 * 开启屏幕输出
	 */
	public static void openScreen() {
//		SmdtManager.setBackLight(OPEN_SCREEN);
	}
	
	public static boolean cancelPowerOnOff() {
		byte enable = 0;
		byte onHour = (byte)SharedUtil.newInstance().getInt(SharedUtil.ON_HOUR_KEY);
		byte onMin = (byte)SharedUtil.newInstance().getInt(SharedUtil.ON_MINUTE_KEY);
		
		byte offHour = (byte)SharedUtil.newInstance().getInt(SharedUtil.OFF_HOUR_KEY);
		byte offMin = (byte)SharedUtil.newInstance().getInt(SharedUtil.OFF_MINUTE_KEY);
//		sendReceiver(onHour, onMin, offHour, offMin, false);
		LogUtils.d(TAG, "cancelPowerOnOff", "cancelPowerOnOff");
		return setPowerOnOff(offHour, offMin, onHour, onMin, enable);
	}
	/**
	 * 设置开关机
	 * @param on_h 离开机还有多少个小时
	 * @param on_m 离开机还有多少个小时
	 * @param off_h 离开机还有多少个小时
	 * @param off_m 离开机还有多少个小时
	 * @param enable  0取消,3开启定时开关机
	 * @return 返回true表示成功
	 */
	public static boolean setPowerOnOff(byte on_h, byte on_m, byte off_h, byte off_m, byte enable) {
//		sendReceiver(on_h, on_m, off_h, off_m, true);
		boolean isSuccess = false;
		int fd, ret;
//		// byte buf[] = { 0, 3, 0, 3 };
//
		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
		if (fd < 0) {
			if (3 == enable) {
				LogUtils.d(TAG, "setPowerOnOff", "开启定时开关机-打开节点失败");
			} else if (0 == enable) {
				LogUtils.d(TAG, "setPowerOnOff", "取消定时开关机-打开节点失败");
			}
		} else {
			ret = posix.poweronoff(off_h, off_m, on_h, on_m, enable, fd);
			if (ret != 0) {
				if (3 == enable) {
					LogUtils.d(TAG, "setPowerOnOff","开启定时开关机-设置命令失败！");
				} else if (0 == enable) {
					LogUtils.d(TAG, "setPowerOnOff","关闭定时开关机-设置命令失败！");
				}
			} else {
				isSuccess = true;
				if (3 == enable) {
					// mOnh, mOnm, mOffh, mOffm;
					LogUtils.d(TAG, "setPowerOnOff","定时开关机：开。"
							+ on_h + "小时"
							+ on_m + "分钟 后系统自动关机；"
							+ off_h + "小时"
							+ off_m + "分钟 后系统自动开机。");
				} else if (0 == enable) {
					
					LogUtils.d(TAG, "setPowerOnOff", "定时开关机：关。");
				}
			}
		}
		posix.close(fd);
		
		return isSuccess;
	}
	
	private static void sendReceiver(byte on_h, byte on_m, byte off_h, byte off_m, boolean isOn) {
		Intent intent = new Intent(POWER_ON_OFF_ACTION);
		if (isOn) {
			intent.putExtra(ON_OR_OFF_TYPE, ON_TYPE);
		} else {
			intent.putExtra(ON_OR_OFF_TYPE, OFF_TYPE);
		}
		intent.putExtra(ON_H, on_h); 
		intent.putExtra(ON_M, on_m); 
		
		intent.putExtra(OFF_H, off_h); 
	    intent.putExtra(OFF_M, off_m); 
	    CommunicationApp.get().sendBroadcast(intent);
	}
}
