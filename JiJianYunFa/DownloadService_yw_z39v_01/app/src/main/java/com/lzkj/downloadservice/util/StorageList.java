package com.lzkj.downloadservice.util;

import android.content.Context;
import android.os.Environment;
import android.os.storage.StorageManager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
/**
 * 
 *@author kchang changkai@lz-mr.com
 *@Description:获取SD卡目录
 *@time:2016年9月26日 下午6:57:25
 */
public class StorageList {
	private Context mContext;
	private StorageManager mStorageManager;
	private Method mMethodGetPaths;

	public StorageList(Context context) {
		if (context != null) {
			mContext = context;
			mStorageManager = (StorageManager) mContext
					.getSystemService(Context.STORAGE_SERVICE);
			try {
				mMethodGetPaths = mStorageManager.getClass().getMethod(
						"getVolumePaths");
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}

	public String getSDPath() {
		String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//		//获取sd卡的路径， 首先路径中包含mnt,
//		//调用String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
//		//去掉sdPath就得到外置的sd卡路径
//		//判断外置的sd卡路径是否有存储空间，如果有则使用，如果没有则使用sdPath
//		String usb = "usb";
//		String[] paths = getVolumePaths();
//		try {
//			for (String path : paths) {
//				File file = new File(path);
//				if (!path.contains(usb) && file.canWrite() && file.canRead()) {
//					StatFs sf = new StatFs(path); //sdcardDir.getPath())值为/mnt/sdcard，想取外置sd卡大小的话，直接代入/mnt/sdcard2
//					long availCount = sf.getAvailableBlocks(); //有效大小
//					if (availCount > 0 && !path.equals(sdcardPath)) {
//						sdcardPath = path;
//					}
//				}
//			}
//		} catch (Exception e) {
//			e.printStackTrace();
//		}

		return sdcardPath;
	}
	
	/**
	 * 获取所有的路径
	 * @return
	 */
	private String[] getVolumePaths() {
		String[] paths = null;
		try {
			paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return paths;
	}
}