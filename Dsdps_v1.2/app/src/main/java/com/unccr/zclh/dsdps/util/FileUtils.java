package com.unccr.zclh.dsdps.util;

import android.os.Environment;

import java.io.File;

public class FileUtils {

    private String path = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zcdt";

    public FileUtils() {
        File file = new File(path);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    public File createFile(String FileName) {
        return new File(path, FileName);
    }
}
