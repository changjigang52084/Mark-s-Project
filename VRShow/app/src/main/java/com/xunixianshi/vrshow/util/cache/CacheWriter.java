package com.xunixianshi.vrshow.util.cache;

import java.io.File;

/**
 * Created by duan on 2016/10/25.
 */

public class CacheWriter implements Runnable {
    private  final FileManager fileManager;
    private final File fileToWrite;
    private final Cache cache;

    CacheWriter(FileManager fileManager, File fileToWrite, Cache cache) {
        this.fileManager = fileManager;
        this.fileToWrite = fileToWrite;
        this.cache = cache;
    }

    @Override public void run() {
        CacheCallBack cacheCallBack = cache.getCacheCallBack();
        if(cacheCallBack!=null){
            cacheCallBack.onBefore(cache.getBitmap());
        }
        this.fileManager.writeCacheToFile(fileToWrite, cache);
        if(cacheCallBack!=null){
            cacheCallBack.onAfter(fileToWrite.getAbsolutePath());
        }
    }
}
