package com.lzkj.aidlservice.adapter.board.smdt;

import com.lzkj.aidlservice.adapter.board.ly.LYPowerOnOffManager;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.xboot.stdcall.posix;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年7月26日 上午9:51:28 
 * @version 1.0 
 * @parameter  视美泰喂狗
 */
public class SMDTFeedDogManager {

	private static final LogTag TAG = LogUtils.getLogTag(SMDTFeedDogManager.class, true);
	/***喂狗的间隔时间**/
//	private static final long FEED_DOG_TIME = 20 * 1000;
	
//	private static Handler mHandler = new Handler();
	
	private static volatile SMDTFeedDogManager mSMDTFeedDogManager;

	private SMDTFeedDogManager() {}

	public static SMDTFeedDogManager getInstance() {
		if (null == mSMDTFeedDogManager) {
			synchronized (SMDTFeedDogManager.class) {
				if (null == mSMDTFeedDogManager) {
					mSMDTFeedDogManager = new SMDTFeedDogManager();
				}
			}
		}
		return mSMDTFeedDogManager;
	}
	
	/**
	 * 打开国微A10看门狗
	 */
	public final void enableWatchDog() {
		LYPowerOnOffManager.get().enableWatchDog();
		LogUtils.w(TAG, "enableWatchDog", "----------------------");
		int fd, ret;
		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
		if (fd < 0) {
			LogUtils.w(TAG, "enableWatchDog", "--- failed ---");
		}
		ret = posix.watchdogenable((byte) 1, fd);
		if (ret != 0) {
			LogUtils.w(TAG, "enableWatchDog", "--- failed ---");
		}
		posix.close(fd);
	}

	/**
	 * A10喂狗
	 */
	public final void feedDog() {
		LogUtils.w(TAG, "feedDog", "----------------------");
		int fd, ret;
		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
		if (fd < 0) {
			LogUtils.w(TAG, "feedDog", "--- failed ---");
		} else {
			LogUtils.w(TAG, "feedDog", "--- fd success ---");
		}

		ret = posix.watchdogfeed(fd);
		if (ret != 0) {
			LogUtils.w(TAG, "feedDog", "--- failed ---");
		} else {
			LogUtils.w(TAG, "feedDog", "--- ret success ---");
		}
		posix.close(fd);
	}

	/**
	 * 关闭国微A10看门狗
	 */
	public final void disableWatchDog() {
		LogUtils.w(TAG, "disableWatchDog", "----------------------");
		int fd, ret;
		fd = posix.open("/dev/McuCom", posix.O_RDWR, 0666);
		if (fd < 0) {
			LogUtils.w(TAG, "disableWatchDog", "--- fd failed ---");
		} else {
			LogUtils.w(TAG, "disableWatchDog", "--- fd success ---");
		}
		
		ret = posix.watchdogenable((byte) 0, fd);
		if (ret != 0) {
			LogUtils.w(TAG, "disableWatchDog", "--- ret failed ---");
		} else {
			LogUtils.w(TAG, "disableWatchDog", "--- ret success ---");
		}
		posix.close(fd);
	}
}
