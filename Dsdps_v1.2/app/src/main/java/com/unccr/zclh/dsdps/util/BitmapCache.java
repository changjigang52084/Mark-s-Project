package com.unccr.zclh.dsdps.util;

import java.util.List;
import java.util.Map;

import android.app.ActivityManager;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.util.LruCache;

import com.unccr.zclh.dsdps.app.DsdpsApp;

public class BitmapCache {
	private LruCache<String, Bitmap> mLruCache;
	private static BitmapCache ins = new BitmapCache();
	public static BitmapCache get() {
		if (ins == null) {
			ins = new BitmapCache();
		}
		return ins;
	}

	private BitmapCache() {
		// Get max available VM memory, exceeding this amount will throw an
		// OutOfMemory exception. Stored in kilobytes as LruCache takes an
		// int in its constructor.
		final int maxMemory = ((ActivityManager) DsdpsApp.getDsdpsApp()
				.getSystemService(DsdpsApp.ACTIVITY_SERVICE)).getMemoryClass() * 1024 * 1024;
		int cacheSize = maxMemory / 8;
		mLruCache = new LruCache<String, Bitmap>(cacheSize) {
			@Override
			protected int sizeOf(String key, Bitmap value) {
				// The cache size will be measured in kilobytes rather than
				// number of items.
				return value.getByteCount();
			}
			@Override
			protected void entryRemoved(boolean evicted, String key,
										Bitmap oldValue, Bitmap newValue) {
				if (oldValue != null && !oldValue.isRecycled()) {
					oldValue.recycle();
					oldValue = null;
				}
			}
		};
	}

	public void addBitmapToMemoryCache(String key, Bitmap bitmap) {
		if (TextUtils.isEmpty(key)) {
			return;
		}
		if (bitmap == null || bitmap.isRecycled()) {
			return;
		}
		if (getBitmapFromMemCache(key) == null) {
			synchronized (mLruCache) {
				mLruCache.put(key, bitmap);
			}
		}
	}

	public Bitmap getBitmapFromMemCache(String key) {
		if (TextUtils.isEmpty(key)) {
			return null;
		}
		Bitmap bitmap;
		synchronized (mLruCache) {
			bitmap = mLruCache.get(key);
			if (bitmap != null && !bitmap.isRecycled()) {
				return bitmap;
			}
		}
		return null;
	}

	/**
	 * clear cache bitmap to key
	 * @param keyList the key list
	 */
	public void clearBitmapToKeyList(List<String> keyList) {
		if (null == keyList) {
			return;
		}
		for (String key : keyList) {
			mLruCache.remove(key).recycle();
		}
	}
	/**
	 *根据key移除掉缓存中的bitmap,并且释放掉
	 * @param key
	 */
	public void clearBitmapToKey(String key) {
		try {
			if (null != key) {
				Bitmap bitmap = mLruCache.remove(key);
				if (null != bitmap) {
					bitmap.recycle();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void clearBitmapAll() {
		if (null != mLruCache) {
			for (Map.Entry<String, Bitmap>  entry : mLruCache.snapshot().entrySet()) {
				Bitmap bitmap = entry.getValue();
				if (null != bitmap && !bitmap.isRecycled()) {
					bitmap.recycle();
				}
			}
			mLruCache.evictAll();
		}
	}
}
