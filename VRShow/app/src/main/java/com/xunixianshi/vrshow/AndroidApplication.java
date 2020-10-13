package com.xunixianshi.vrshow;

import android.content.IntentFilter;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;

import androidx.multidex.MultiDexApplication;

import com.baidu.mobstat.StatService;
import com.easemob.chat.EMChat;
import com.franmontiel.persistentcookiejar.ClearableCookieJar;
import com.franmontiel.persistentcookiejar.PersistentCookieJar;
import com.franmontiel.persistentcookiejar.cache.SetCookieCache;
import com.franmontiel.persistentcookiejar.persistence.SharedPrefsCookiePersistor;
import com.hch.filedownloader.download.DbUpgradeListener;
import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.DownloaderManagerConfiguration;
import com.hch.util.io.StorageUtils;
import com.hch.utils.DefineClass;
import com.hch.utils.OkhttpConstant;
import com.hch.viewlib.util.AppCacheRootManage;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.xunixianshi.vrshow.interfaces.ReceiverNetworkInterface;
import com.xunixianshi.vrshow.my.fragment.database.UploadManageUtil;
import com.xunixianshi.vrshow.obj.MainClassifyHomeColumnListObj;
import com.xunixianshi.vrshow.receiver.ConnectionChangeReceiver;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.CrashHandler;
import com.xunixianshi.vrshow.util.cache.CacheUtil;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import okhttp3.OkHttpClient;

public class AndroidApplication extends MultiDexApplication {
    private AndroidApplication mContext;
    {
        // qq
        PlatformConfig.setQQZone("1105439134", "uUaXoSZAE48yD0fY");
        // 新浪微博
        PlatformConfig.setSinaWeibo("2835953282",
                "5c2053f74978afddea92560f05af2eb5");
        // 微信
        PlatformConfig.setWeixin("wx633b9cf100b10371",
                "50bcd5d0b66fcc0b9b5064ea9aceb169");
    }

    private ConnectionChangeReceiver connectionChangeReceiver;
    private static ArrayList<MainClassifyHomeColumnListObj> cilumnTabs = new ArrayList<MainClassifyHomeColumnListObj>();

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = this;
        EMChat.getInstance().init(getApplicationContext());
        connectionChangeReceiver = new ConnectionChangeReceiver();
        IntentFilter filter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(connectionChangeReceiver, filter);
        if(!DefineClass.debug){
            //  开启本地日志记录
            CrashHandler.getInstance().init(this);
        }
        this.initDownloaderManager();
//        JPushInterface.setDebugMode(true); 	// 设置开启日志,发布时请关闭日志
//        JPushInterface.init(this);     		// 初始化 JPush
        UMShareAPI.get(this);
//        Config.REDIRECT_URL = "http://sns.whalecloud.com/sina2/callback";//新浪回调页
        //初始化缓存路径
        CacheUtil.getInstance().initCacheUtil(new CacheUtil.Builder(this).setCacheDir(AppCacheRootManage.getInstance(mContext).getCacheSaveRootPath()));
        initOkHttp();
        StatService.setAppKey("KhtmRwsCa1Ikb0YySKHoQbLkeXmfEuzC");
        StatService.setAppChannel(this, "", false);
    }

    public ArrayList<MainClassifyHomeColumnListObj> getCilumnTabs() {
        return cilumnTabs;
    }

    private void initOkHttp() {
        ClearableCookieJar cookieJar1 = new PersistentCookieJar(new SetCookieCache(), new SharedPrefsCookiePersistor(getApplicationContext()));

//        HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);

//        CookieJarImpl cookieJar1 = new CookieJarImpl(new MemoryCookieStore());
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .addInterceptor(new LoggerInterceptor("TAG"))
                .cookieJar(cookieJar1)
                .hostnameVerifier(new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return true;
                    }
                })
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    /**
     * @return void    返回类型
     * @throws
     * @Title: initDownloaderManager
     * @Description: 初始化下载管理
     * @author duanchunlin
     */
    private void initDownloaderManager() {
        //初始化下载管理
        //数据库表扩展字段，开发者可以添加一些扩展字段来满足业务需求，注意不能包含id,path,url，因为框架内部有定义不得重复
        Map<String, String> dbExFeildMap = new HashMap<>();
        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_MARK_ID, "VARCHAR");
        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_TYPE, "VARCHAR"); //下载类型
        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_NAME, "VARCHAR"); //保存名称
        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_ICON_URL, "VARCHAR"); //保存图片地址
        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_VIDEO_TYPE, "VARCHAR"); //视频类型

        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_APP_PACKAGE_NAME, "VARCHAR");
        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_APP_VERSION_CODE, "VARCHAR");
        dbExFeildMap.put(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_FILE_SIZE, "VARCHAR"); //下载文件的大小

        //下载文件所保存的目录
        File storeFile = StorageUtils.getCacheDirectory(mContext, true, "FileDownloader");
        MLog.d("storeFile:" + storeFile);
        if (!storeFile.exists()) {
            storeFile.mkdirs();
        }

        final DownloaderManagerConfiguration.Builder dmBulder = new DownloaderManagerConfiguration.Builder(mContext)
                .setMaxDownloadingCount(3) //配置最大并行下载任务数，配置范围[1-100]
                .setDbExtField(dbExFeildMap) //配置数据库扩展字段
                .setDbVersion(1)//配置数据库版本
                .setAutoRetryTimes(5)
                .setDbUpgradeListener(dbUpgradeListener) //配置数据库更新回调
                .setDownloadStorePath(storeFile.getAbsolutePath()); //配置下载文件存储目录

        //以上都是可选配置，如果开发者想使用默认配置可以DownloaderManagerConfiguration.Builder dmBulder = new DownloaderManagerConfiguration.Builder(this);即可
        //初始化下载管理,最好放到线程去执行，因为里面有操作数据库的可能会引起卡顿
        new Thread() {
            @Override
            public void run() {
                super.run();
                DownloaderManager.getInstance().init(dmBulder.build());//必要语句
                DownloaderManager.getInstance().setGlobalDownloadCallback(new GlobalDownloadListener(mContext)); //添加全局监听，主要做下载失败后的重试
                DownloaderManager.getInstance().setDownloadStorePath(AppCacheRootManage.getInstance(mContext).getCacheSaveRootPath());  //重新设置保存路径，与app同步

                AndroidApplication.this.registerReceiverNetworkInterface(new ReceiverNetworkInterface() {
                    @Override
                    public void NetworkChanged(int state) {
                        //参考baseAc 类注释
                        if (state == ConnectionChangeReceiver.NET_STATE_CONNECTED_MOBILE && !SimpleSharedPreferences.getBoolean("isWiFi", AndroidApplication.this)) {
                            DownloaderManager.getInstance().pauseAllTask(); //暂停所有任务
                            UploadManageUtil.getInstance().cancelUpload();//暂停所有上传任务
                        }
                    }
                });

            }
        }.start();
    }

    /**
     * 更新数据库监听
     */
    private DbUpgradeListener dbUpgradeListener = new DbUpgradeListener() {
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        }
    };

    /**
     * @author duanchunlin
     * @time 2016/5/14 15:17
     * 注释： 注册网络状态改变监听接口
     */
    public void registerReceiverNetworkInterface(ReceiverNetworkInterface receiverNetworkInterface) {
        if (connectionChangeReceiver != null) {
            connectionChangeReceiver.registerReceiverNetworkInterface(receiverNetworkInterface);
        }
    }

}
