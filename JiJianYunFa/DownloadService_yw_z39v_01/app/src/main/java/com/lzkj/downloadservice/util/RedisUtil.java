package com.lzkj.downloadservice.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

import com.baize.lz.core.cache.redis.exception.AdpressCacheException;
import com.baize.lz.core.cache.redis.generator.bo.AdpressCacheKeyInfo;
import com.baize.lz.core.cache.redis.generator.impl.SimpleAdpressCacheKeyGenerator;
import com.baize.lz.core.cache.redis.service.impl.AdpressCacheServiceImpl;
import com.baize.lz.core.common.constant.cache.CacheKeyConstant;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月15日 下午6:58:18 
 * @version 1.0 
 * @parameter  
 */
public class RedisUtil {
	private static ExecutorService executorService = Executors.newSingleThreadExecutor();
	//private static String uuid = null;
	//private static String filename = null;
	/**
	 * 汇报下载进度给服务器
	 * @param deviceId
	 * 			设备id
	 * @param fileName
	 * 			下载文件的名称
	 * @param downloadProgress
	 * 			下载的进度
	 * @param total
	 * 			下载文件的大小
	 */
	public static synchronized void uploadDownloadProgress( final String deviceId,
															final String fileName,
															final long downloadProgress,
															final long total,
															final String uuid) {
		executorService.execute(new Runnable() {
			@Override
			public void run() {
				try {
					AdpressCacheServiceImpl adpressCacheService = new AdpressCacheServiceImpl();
					SimpleAdpressCacheKeyGenerator simpleAdpressCacheKeyGenerator = new SimpleAdpressCacheKeyGenerator();
					adpressCacheService.setAdpressCacheKeyGenerator(simpleAdpressCacheKeyGenerator);
					adpressCacheService.setDefaultCacheSeconds(60*60*48);
					Map<String, String> progress = new HashMap<String, String>();
					AdpressCacheKeyInfo cacheKeyInfo = new AdpressCacheKeyInfo(
							CacheKeyConstant.APPLICATION_EPOSTER, 
							CacheKeyConstant.MODULE_DEVICE,
							CacheKeyConstant.FUNCTION_DOWNLOAD,
							deviceId,
							fileName,
							uuid);
//					ConfigSettings.getUUID(fileName));
					String key = simpleAdpressCacheKeyGenerator.generate(cacheKeyInfo);
					progress.put(CacheKeyConstant.PROPERTY_DOWNLOAD_DOWNLOADSIZE, String.valueOf(downloadProgress));
					progress.put(CacheKeyConstant.PROPERTY_DOWNLOAD_TOTALSIZE, String.valueOf(total));
					Log.d("RedisUtil", "uploadDownloadProgress:"+downloadProgress+",total:"+total+",fileName:"+fileName+",uuid:"+uuid);
					adpressCacheService.setHashMap(key, progress);
				} catch (AdpressCacheException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
