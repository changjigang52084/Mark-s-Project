package com.lzkj.aidlservice.util;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年8月11日 下午9:31:45 
 * @version 1.0 
 * @parameter 线程池管理类
 */
public class ThreadPoolManager {
	private static final int CPU_COUNT = Runtime.getRuntime().availableProcessors();

	private static final int MAXIMUM_POOL_SIZE = CPU_COUNT * 2 + 1;

	private ExecutorService executorService;

	private ThreadPoolManager() {
		executorService = Executors.newFixedThreadPool(MAXIMUM_POOL_SIZE);
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

