package com.sunchip.adw.cloudphotoframe.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingmuliang on 2020/1/13.
 * 文件操作类
 */
public class FileUtils {

    public static final int SIZETYPE_B = 1;//获取文件大小单位为B的double值
    public static final int SIZETYPE_KB = 2;//获取文件大小单位为KB的double值
    public static final int SIZETYPE_MB = 3;//获取文件大小单位为MB的double值
    public static final int SIZETYPE_GB = 4;//获取文件大小单位为GB的double值

    private List<File> mFileList = new ArrayList<>();
    public static FileUtils mFileUtils = new FileUtils();

    public FileUtils() {
    }

    public static FileUtils getInstance() {
        if (mFileUtils == null) {
            mFileUtils = new FileUtils();
        }
        return mFileUtils;
    }

    //根据文件地址获取所有的文件内容
    public List<File> getFile(File file) {
        mFileList.clear();
        File[] fileArray = file.listFiles();
        if (fileArray.length > 0) {
            for (File file1 : fileArray) {
                if (file1.isFile()) {
                    mFileList.add(file1);
                } else {
                    getFile(file1);
                }
            }
            return mFileList;
        }
        return null;
    }

    //判断文件是否存在
    public boolean fileIsExists(String path) {
        try {
            File file = new File(path);
            if (!file.exists()) {
                return false;
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    //删除某个文件
    public void deleteFile(String path) {
        File file = new File(path);
        if (file.exists()) {
            file.delete();
        }
    }


    //得到远程地址的文件大小
    public static double GetFileLength(String fileUrl) {
        HttpURLConnection urlcon = null;
        try {
            URL u = new URL(fileUrl);
            urlcon = (HttpURLConnection) u.openConnection();
        } catch (Exception e) {
            e.printStackTrace();
        }
        int fileLength = urlcon.getContentLength();
        return fileLength;
    }


    public static double getFileOrFilesSize(String filePath) {
        File file = new File(filePath);
        long blockSize = 0;
        try {
            if (file.isDirectory()) {
                blockSize = getFileSizes(file);
            } else {
                blockSize = getFileSize(file);
            }
        } catch (Exception e) {
//            e.printStackTrace();
            Log.e("获取文件大小", "获取失败!");
            return 0;
        }
        return FormetFileSize(blockSize, 1);
    }

    /**
     * 获取指定文件大小
     *
     * @param
     * @return
     * @throws Exception
     */
    private static long getFileSize(File file) throws Exception {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        } else {
            file.createNewFile();
            Log.e("获取文件大小", "文件不存在!");
        }
        return size;
    }

    /**
     * 获取指定文件夹
     *
     * @param f
     * @return
     * @throws Exception
     */
    private static long getFileSizes(File f) throws Exception {
        long size = 0;
        File flist[] = f.listFiles();
        for (int i = 0; i < flist.length; i++) {
            if (flist[i].isDirectory()) {
                size = size + getFileSizes(flist[i]);
            } else {
                size = size + getFileSize(flist[i]);
            }
        }
        return size;
    }

    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    private static String FormetFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * 转换文件大小,指定转换的类型
     *
     * @param fileS
     * @param sizeType
     * @return
     */
    private static double FormetFileSize(long fileS, int sizeType) {
        DecimalFormat df = new DecimalFormat("#.00");
        double fileSizeLong = 0;
        switch (sizeType) {
            case SIZETYPE_B:
                fileSizeLong = Double.valueOf(df.format((double) fileS));
                break;
            case SIZETYPE_KB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1024));
                break;
            case SIZETYPE_MB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1048576));
                break;
            case SIZETYPE_GB:
                fileSizeLong = Double.valueOf(df.format((double) fileS / 1073741824));
                break;
            default:
                break;
        }
        return fileSizeLong;
    }
}
