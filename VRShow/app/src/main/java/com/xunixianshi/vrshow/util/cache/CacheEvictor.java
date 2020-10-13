package com.xunixianshi.vrshow.util.cache;

import java.io.File;

/**
 * Created by duan on 2016/10/25.
 */

public class CacheEvictor implements Runnable {
    private final FileManager fileManager;
    private final File cacheDir;

    CacheEvictor(FileManager fileManager, File cacheDir) {
        this.fileManager = fileManager;
        this.cacheDir = cacheDir;
    }

    @Override public void run() {
        this.fileManager.clearDirectory(this.cacheDir);
    }
}
