package com.unccr.zclh.dsdps.util;

import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.os.storage.StorageManager;

import java.io.File;
import java.lang.reflect.Method;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 上午9:54:15
 * @parameter StorageList
 */
public class StorageList {
    private Context mContext;
    private StorageManager mStorageManager;
    private Method mMethodGetPaths;

    public StorageList(Context context){
        if(context != null){
            mContext = context;
            mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
            try{
                mMethodGetPaths = mStorageManager.getClass().getMethod("getVolumePaths");
            }catch (NoSuchMethodException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * 有TF卡先使用TF卡，否则使用机器本身的存储空间
     * @return
     */
    public String getSdPath(){
        String sdCardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        String usb = "usb";
        String[] paths = getVolumePaths();
        try{
            for(String path : paths){
                File file = new File(path);
                if(!path.contains(usb)&&file.canWrite()&&file.canRead()){
                    StatFs sf = new StatFs(path);
                    long availCount = sf.getAvailableBlocks();
                    if(availCount > 0 && !path.equals(sdCardPath)){
                        sdCardPath = path;
                    }
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return sdCardPath;
    }

    /**
     * 获取所有的路径
     * @return
     */
    private String[] getVolumePaths(){
        String[] paths = null;
        try{
            paths = (String[]) mMethodGetPaths.invoke(mStorageManager);
        }catch (Exception e){
            e.printStackTrace();
        }
        return paths;
    }
}
