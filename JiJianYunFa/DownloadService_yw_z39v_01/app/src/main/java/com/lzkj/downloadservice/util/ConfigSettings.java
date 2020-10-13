package com.lzkj.downloadservice.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.factory.AdpressProtocolPackageFactoryManager;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import java.util.List;
import java.util.Map;

public class ConfigSettings {
    private static final LogTag TAG = LogUtils.getLogTag(ConfigSettings.class.getSimpleName(), true);
    /**
     * 是否是内网测试,true表示内网,false外网
     */
    public static final boolean IFTEST = false;
    public static final String INNER_APK_VERSION = getVersion();
    public static final String DEVICE_NAME = "";

    /**
     * 存储路径
     */
//	public static String STORAGE_PATH = Environment. getExternalStorageDirectory().getAbsolutePath();
    public static int getRealDisplayHeight() {
        int heightPixels;
        WindowManager w = ((WindowManager) DownloadApp.getContext().getSystemService(Context.WINDOW_SERVICE));
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        heightPixels = metrics.heightPixels;
        return heightPixels;
    }

    /*为了看download日志 现修改为true 修改者cjg*/
    public static boolean isOpenLog() {
        return true;
    }

    public static int getRealDisplayWidth() {
        int widthPixels;
        WindowManager w = ((WindowManager) DownloadApp.getContext().getSystemService(Context.WINDOW_SERVICE));
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        widthPixels = metrics.widthPixels;
        return widthPixels;
    }

    /**
     * 获取deviceId
     *
     * @return
     */
    public static String getDeviceId() {
        return ShreadUtil.newInstance().getString(CommutShardUtil.DEVICE_ID_KEY);
//		return CommutShardUtil.newInstance().getString(CommutShardUtil.DEVICE_ID_KEY);
    }

    /**
     * 获取节目列表的id
     *
     * @return
     */
    public static Long getPrmId() {
        return CommutShardUtil.newInstance().getLong(CommutShardUtil.PROGRAM_LIST_ID);
    }

    /**
     * 获取redis ip地址
     *
     * @return
     */
    public static String getRedisIp() {
        String workServer = CommutShardUtil.newInstance().getString(CommutShardUtil.REDIS_SERVER_ID);
        if (null == workServer || "".equals(workServer)) {
            return null;
        }
        String redisIps[] = workServer.split(";");
        return redisIps[0].split(":")[0];
    }

    /**
     * 获取redis 端口号
     *
     * @return
     */
    public static int getRedisPort() {
        String workServer = CommutShardUtil.newInstance().getString(CommutShardUtil.REDIS_SERVER_ID);
        if (null == workServer || "".equals(workServer)) {
            return 0;
        }
        String redisIps[] = workServer.split(";");
        return Integer.parseInt(redisIps[0].split(":")[1]);
    }

    /**
     * 获取uuid
     *
     * @param key
     * @return
     */
    public static String getUUID(String key) {
        if (null != key) {
            return ShreadUtil.newInstance().getString(key);
        }
        return null;
    }

    /**
     * 下载成功以后移除掉
     *
     * @param key
     */
    public static void removeUUID(String key) {
        ShreadUtil.newInstance().removeKey(key);
    }

    /**
     * 获取工作服务器地址
     *
     * @return
     */
    public static String getWorkServer() {
//		String workServers = CommutShardUtil.newInstance().getString(CommutShardUtil.MESSAGE_SERVER_ID);

      String workServers = "112.74.12.254:8080"; // 正式服务器

//        String workServers = "120.79.219.145:8080"; // 极简测试服务器

//        String workServers = ShreadUtil.newInstance().getString(ShreadUtil.SERVER_KEY);

        // Method: getWorkServer, Message: workServers:112.74.12.254:8080
        LogUtils.d(TAG, "getWorkServer", "workServers:" + workServers);
        if (workServers != null) {
            return workServers;
        } else {
            return CommutShardUtil.newInstance().getString(CommutShardUtil.MESSAGE_SERVER_ID);
        }
    }

    /**
     * 初始化通讯的工具包
     */
    public static void initAdpressCore() {
        AdpressProtocolPackageFactoryManager.init();
//		String redisIp = ConfigSettings.getRedisIp();
//		int port = ConfigSettings.getRedisPort();
//		if (null == redisIp || 0 == port) {
//			return;
//		}
//		LogUtils.d(TAG, "initAdpressCore", " redisIp："+redisIp + ", port:" + port+",test："+test);
//		JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
//		jedisPoolConfig.setMaxActive(1024);
//		jedisPoolConfig.setMaxIdle(500);
//		jedisPoolConfig.setMaxWait(2000L);
//		jedisPoolConfig.setTestOnBorrow(false);
//		jedisPoolConfig.setTestOnReturn(true);
//		JedisPool jedisPool = new JedisPool(jedisPoolConfig,
//											redisIp, 
//											port,5000);
//		RedisUtils redisUtils = new RedisUtils();
//		redisUtils.setJedisPool(jedisPool);
//		if (ConfigSettings.IFTEST) {
//			redisUtils.setAuthentication("0");
//		} else {
//			redisUtils.setAuthentication("36ae75b6195711e5:01211988zhouZHI");
//		}
//		LogUtils.d(TAG, "initAdpressCore", " redisIp："+redisIp + ", port:" + port+",end,test："+test);
    }

    /**
     * 保存节目列表id
     *
     * @param prmId
     */
    public static void setPrmId(String prmId) {
        ShreadUtil.newInstance().putString(CommutShardUtil.PROGRAM_LIST_ID, prmId);
    }

    /**
     * 保存prm key
     *
     * @param prmKey
     */
    public static void savePrmKey(Map<String, List<String>> prmKey) {
        ShreadUtil.newInstance().putString(ShreadUtil.PRM_ID_KEY, JSON.toJSONString(prmKey));
    }

    /**
     * 返回prm key map对象
     *
     * @return
     */
    public static Map<String, List<String>> getPrmKey() {
        String prmKey = ShreadUtil.newInstance().getString(ShreadUtil.PRM_ID_KEY);
        if (TextUtils.isEmpty(prmKey)) {
            return null;
        }
        Map<String, List<String>> prmKeyMap = (Map<String, List<String>>) JSON.parse(prmKey);
        return prmKeyMap;
    }

    /**
     * 保存当前正在下载的节目id
     *
     * @param prmKey 节目id
     */
    public static void saveCurrentPrmKey(String prmKey) {
        ShreadUtil.newInstance().putString(ShreadUtil.CURRENT_PRM_KEY, prmKey);
    }

    /**
     * 返回之前真正下载的节目id
     *
     * @return
     */
    public static String getCurrentPrmKey() {
        return ShreadUtil.newInstance().getString(ShreadUtil.CURRENT_PRM_KEY);
    }

    /**
     * 获取版本号
     *
     * @return 当前应用的版本号
     */
    public static String getVersion() {
        try {
            PackageManager manager = DownloadApp.getContext().getPackageManager();
            PackageInfo info = manager.getPackageInfo(DownloadApp.getContext().getPackageName(), 0);
            String version = info.versionName;
            return version;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
