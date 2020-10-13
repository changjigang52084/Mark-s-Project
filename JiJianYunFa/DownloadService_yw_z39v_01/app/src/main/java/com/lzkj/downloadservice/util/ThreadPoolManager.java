package com.lzkj.downloadservice.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月11日 下午9:31:45 
 * @version 1.0 
 * @parameter  
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

