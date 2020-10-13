package com.unccr.zclh.dsdps.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @author  jigangchang Email:changjigang@sunchip.com
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午5:47:56
 * @parameter ThreadPoolManager
 */
public class ThreadPoolManager {

	private ExecutorService executorService;
	private int maxPoolSize = 5;

	private ThreadPoolManager() {
		executorService = Executors.newFixedThreadPool(maxPoolSize);
	}

	private static volatile ThreadPoolManager mThreadPoolManager;

	public static ThreadPoolManager get() {
		if (null == mThreadPoolManager) {
			synchronized (ThreadPoolManager.class) {
				if (null == mThreadPoolManager) {
					mThreadPoolManager = new ThreadPoolManager();
				}
			}
		}
		return mThreadPoolManager;
	}

	public boolean addRunnable(Runnable runnable) {
		executorService.execute(runnable);
		return true;
	}

	public void shutDownThreadPool() {
		executorService.shutdown();
		executorService = null;
		mThreadPoolManager = null;
	}
}
