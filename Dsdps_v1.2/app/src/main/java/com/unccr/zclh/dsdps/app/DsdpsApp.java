package com.unccr.zclh.dsdps.app;

import android.app.Application;
import android.content.ComponentCallbacks2;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.receiver.TimeChangeReceive;
import com.unccr.zclh.dsdps.sip.Sip;
import com.unccr.zclh.dsdps.sip.SipEngine;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.StorageList;
import com.unccr.zclh.dsdps.util.ThreadPoolManager;
import com.unccr.zclh.dsdps.util.WorkTimeUtil;

/**
 * @author  jigangchang Email:changjigang@sunchip.com
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午4:35:59
 * @parameter DsdpsApp
 */
public class DsdpsApp extends Application {

    private static final String TAG = "DsdpsApp";

    /**图片加载次数**/
    private int picLoadIndex = 0;
    /**图片加载次数最大值10 ，到了就清内存**/
    private int maxPicLoadIndex = 10;

    private PlayActivity playActivity = null;

    public PlayActivity getPlayActivity() {
        return playActivity;
    }

    public void setPlayActivity(PlayActivity activity) {
        playActivity = activity;
    }

    private static DsdpsApp dsdpsApp;

    public Handler mHandler = new Handler();

    public static DsdpsApp getDsdpsApp(){
        return dsdpsApp;
    }

    private TimeChangeReceive timeChangeReceive;


    @Override
    public void onCreate() {
        super.onCreate();
        dsdpsApp = this;
        Sip.loginSip();
        initSdcardPath();
        initGilde();
        initConfig();
        registerTimeChangeRecieve();
    }



    // 初始化sd卡路径
    private void initSdcardPath() {
        StorageList storageList = new StorageList(this);
        FileStore.getInstance().setSdCard(storageList.getSdPath());
    }

    private void initGilde() {
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                Glide.get(DsdpsApp.this).clearDiskCache();
            }
        });
    }

    private void initConfig(){
        if(ConfigSettings.isClientValid()){
            WorkTimeUtil.get().startLocalWorkTime();
        }
    }

    /**
     * 清除缓存
     */
    public void clearGlideMemory() {
        picLoadIndex++;
        if (picLoadIndex >= maxPicLoadIndex) {
            Log.d(TAG, "clearGlideMemory picLoadIndex: " + picLoadIndex);
            picLoadIndex = 0;
            Glide.get(this).clearMemory();//清除内存缓存,防止OOM
        }
    }

    @Override
    public void onLowMemory() {
        Glide.get(this).clearMemory();
        Log.d(TAG, "onLowMemory.");
        super.onLowMemory();
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        switch (level) {
            case ComponentCallbacks2.TRIM_MEMORY_COMPLETE:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_MODERATE:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_BACKGROUND:
                break;
            case ComponentCallbacks2.TRIM_MEMORY_UI_HIDDEN:
                break;
            default:
                break;
        }
    }

    /**
     * 动态组成时间改变receive
     */
    private void registerTimeChangeRecieve(){
        if(null == timeChangeReceive){
            IntentFilter filter = new IntentFilter(Intent.ACTION_TIME_TICK);
            timeChangeReceive = new TimeChangeReceive();
            registerReceiver(timeChangeReceive,filter);
        }
    }

  }
