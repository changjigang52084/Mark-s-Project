package com.hch.viewlib.util;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.util.List;

/**
 * Created by xnxs on 2016/5/13.
 */
public class AppCacheRootManage {
    /**
     * 是否是内部存储
     */
    private static int mInternalStorage = 0;

    public final static int STORAGE_DIRECTORY_TYPE = 0; //手机内部存储

    public final static int STORAGE_SDCARD_TYPE = 1;   //SD卡存储

    private static AppCacheRootManage mAppCacheRootManage;

    // 内部存储路径
    private static String InternalStorage = null;
    // sd卡存储路径
    private static String SDCardStorage = null;

    public static AppCacheRootManage getInstance(Context context) {
        if (mAppCacheRootManage == null) {
            mAppCacheRootManage = new AppCacheRootManage();
            mAppCacheRootManage.initCacheSaveRootPath(context);
        }
        return mAppCacheRootManage;
    }

    private void initCacheSaveRootPath(Context context) {
        mInternalStorage = (int) SimpleSharedPreferences.getInt("internalStorage", context);
        String storageDir = Environment.getExternalStorageDirectory().getPath();
        List<String> sDCardPaths = SDCardUtil.getExtSDCardPath();

        InternalStorage  = getAppSaveFileDirFormRoot(storageDir,context);
        if(sDCardPaths != null && sDCardPaths.size() > 0){
            SDCardStorage = getAppSaveFileDirFormRoot(sDCardPaths.get(0),context);
        }
    }

    private String  getAppSaveFileDirFormRoot(String rootDir ,Context context){
        File dataDir = new File(new File(rootDir, "Android"), "data");
        File appCacheDir = new File(dataDir, context.getPackageName());
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                return null;
            }
        }
        return appCacheDir.getAbsolutePath();
    }

    public boolean changeCacheSaveRootPath(Context context, int saveType) {
        if(saveType != mInternalStorage){
            mInternalStorage = saveType;
            SimpleSharedPreferences.putInt("internalStorage", saveType, context);
            return true;
        }
        return false;
    }

    public int getCacheSaveRootPathType() {
        return mInternalStorage;
    }

    public String getCacheSaveRootPath() {
        if (mInternalStorage == STORAGE_SDCARD_TYPE && !StringUtils.isEmpty(SDCardStorage)) {
            return SDCardStorage;
        } else {
            return InternalStorage;
        }
    }
}
