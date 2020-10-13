package com.lzkj.aidlservice.util;

import com.baize.lz.core.cache.redis.exception.AdpressCacheException;
import com.baize.lz.core.cache.redis.generator.bo.AdpressCacheKeyInfo;
import com.baize.lz.core.cache.redis.generator.impl.SimpleAdpressCacheKeyGenerator;
import com.baize.lz.core.cache.redis.service.impl.AdpressCacheServiceImpl;
import com.baize.lz.core.common.constant.cache.CacheKeyConstant;
import com.lzkj.aidlservice.api.interfaces.IRedisCallback;
import com.lzkj.aidlservice.util.LogUtils.LogTag;


/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月19日 上午11:34:41 
 * @version 1.0 
 * @parameter  redis进行读写
 */
public class RedisUtil {
	private static final LogTag TAG = LogUtils.getLogTag(RedisUtil.class.getSimpleName(), true);
	/**
	 * 根据redis 的key获取值
	 * @param key
	 * 			redis的key
	 * @param iRedisCallback
	 * 			得到结果以后调用的接口
	 */
	public static void getValueToKey(final String key,final IRedisCallback iRedisCallback ) {
		if (StringUtil.isNullStr(key) || null == iRedisCallback) {
			LogUtils.d(TAG, "getValueToKey", "key:"+key);
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AdpressCacheServiceImpl adpressCacheService = new AdpressCacheServiceImpl();
					SimpleAdpressCacheKeyGenerator simpleAdpressCacheKeyGenerator = new SimpleAdpressCacheKeyGenerator();
					adpressCacheService.setAdpressCacheKeyGenerator(simpleAdpressCacheKeyGenerator);
					adpressCacheService.setDefaultCacheSeconds(60*60*48);
					String value = adpressCacheService.getString(key);
					LogUtils.d(TAG, "getValueToKey", "value:"+value);
					iRedisCallback.setValue(key,value);
				} catch (AdpressCacheException e) {
					iRedisCallback.error(key);
					e.printStackTrace();
				}
			}
		}).start();

	}
	
	/**
	 * 写到redis里面
	 * @param fileName
	 */
	public static void writerRedis(final String fileName) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					AdpressCacheServiceImpl adpressCacheService = new AdpressCacheServiceImpl();
					SimpleAdpressCacheKeyGenerator simpleAdpressCacheKeyGenerator = new SimpleAdpressCacheKeyGenerator();
					adpressCacheService.setAdpressCacheKeyGenerator(simpleAdpressCacheKeyGenerator);
					adpressCacheService.setDefaultCacheSeconds(60*60*48);
					String device_id = SharedUtil.newInstance().getString(SharedUtil.DEVICE_ID);
					AdpressCacheKeyInfo adpressCacheKeyInfo = new AdpressCacheKeyInfo(
							CacheKeyConstant.APPLICATION_EPOSTER,
							CacheKeyConstant.MODULE_DEVICE, 
							CacheKeyConstant.FUNCTION_SCREENSHOT,
							device_id);
					String key = simpleAdpressCacheKeyGenerator.generate(adpressCacheKeyInfo);
					adpressCacheService.setString(key, fileName);
					String value = adpressCacheService.getString(key);
					LogUtils.d(TAG, "onReceive", "fileName:"+fileName+",key:"+key+",value:"+value);
				} catch (AdpressCacheException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
