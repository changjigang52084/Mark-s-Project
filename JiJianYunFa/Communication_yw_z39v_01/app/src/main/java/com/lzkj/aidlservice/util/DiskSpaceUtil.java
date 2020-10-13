package com.lzkj.aidlservice.util;

import android.os.Environment;
import android.os.StatFs;
import android.util.Log;

import java.io.File;
import java.text.DecimalFormat;

/**
 * 项目名称：DebugInfo
 * 类名称：DiskSpaceUtil
 * 类描述：   sdcard存储信息工具类
 * 创建人："lyhuang"
 * 创建时间：2015年8月10日 上午11:53:48
 */
public class DiskSpaceUtil {

    private static final String TAG = "DiskSpaceUtil";
    public final static double DEFAULT_ERROR_VALUE = 0d;
    private final static long SIZE_KB = 1024L;
    private final static long SIZE_MB = SIZE_KB * SIZE_KB;
    private final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    /**
     * @return float    可用空间大小
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取可用空间，以KB为单位)
     * @Param @return    设定文件
     */
    public static double getAvailableSpaceKB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getAvailableSpaceBytes() / (double) SIZE_KB;
        }
        return space;
    }

    /**
     * 获取TF卡可用空间，以KB为单位
     *
     * @return
     */
    public static double getAvailableTFSpaceKB() {
        double space = DEFAULT_ERROR_VALUE;
        space = (double) getAvailableTFSpaceBytes() / (double) SIZE_KB;
        return space;
    }

    /**
     * @return float   可用空间大小
     * @Title: getAvailableSpaceMB
     * @Description: TODO(获取可用空间，以MB为单位)
     * @Param @return    设定文件
     */
    public static double getAvailableSpaceMB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getAvailableSpaceBytes() / (double) SIZE_MB;
        }
        return space;
    }

    /**
     * @return float    可用空间大小
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取可用空间，以GB为单位)
     * @Param @return    设定文件
     */
    public static double getAvailableSpaceGB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getAvailableSpaceBytes() / (double) SIZE_GB;
        }
        return space;
    }

    /**
     * @return float    可用空间大小
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取存储空间总量，以KB为单位)
     * @Param @return    设定文件
     */
    public static double getTotalSpaceKB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getTotalSpaceBytes() / (double) SIZE_KB;
        }
        return space;
    }

    /**
     * 获取TF卡存储空间总量，以KB为单位
     *
     * @return
     */
    public static double getTotalTFSpaceKB() {
        double space = DEFAULT_ERROR_VALUE;
        space = getTotalTFSpaceBytes() / SIZE_KB;
        return space;
    }

    /**
     * @return float    可用空间大小
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取存储空间总量，以MB为单位)
     * @Param @return    设定文件
     */
    public static double getTotalSpaceMB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getTotalSpaceBytes() / (double) SIZE_MB;
        }
        return space;
    }

    /**
     * @return float    可用空间大小
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取存储空间总量，以GB为单位)
     * @Param @return    设定文件
     */
    public static double getTotalSpaceGB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getTotalSpaceBytes() / (double) SIZE_GB;
        }
        return space;
    }


    /**
     * @return boolean    返回类型
     * @Title: hasExternalSpace
     * @Description: TODO(判断SD卡是否存在，并且可以进行写操作)
     * @Param @return    设定文件
     */
    private static boolean hasExternalSpace() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * @return float    返回类型
     * @Title: getFreeSpacePercentage
     * @Description: TODO(获取存储空间的可用率)
     * @Param @return    设定文件
     */
    public static double getFreeSpacePercentage() {
        return getTotalSpaceBytes() == 0 ? DEFAULT_ERROR_VALUE : 100 * getAvailableSpaceBytes() / getTotalSpaceBytes();
    }

    /**
     * @return float    返回类型
     * @Title: getFreeSpacePercentage
     * @Description: TODO(获取存储空间的使用率)
     * @Param @return    设定文件
     */
    public static double getUsedSpacePercentage() {
        return getTotalSpaceBytes() == 0 ? DEFAULT_ERROR_VALUE : 100 - 100 * getAvailableSpaceBytes() / getTotalSpaceBytes();
    }


    /**
     * @return long    返回类型
     * @Title: getTotalSpaceBytes
     * @Description: TODO(获取存储空间总字节数)
     * @Param @return    设定文件
     */
    private static long getTotalSpaceBytes() {
//		String path = ConfigSettings.STORAGE_PATH;
        String path = FileUtile.getInstance().getSDCard();
        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }

        StatFs stat = new StatFs(path);
        long totalBlocks = stat.getBlockCount();
        long blockSize = stat.getBlockSize();
        long availableBytes = totalBlocks * blockSize;
        return availableBytes;
    }

    /**
     * 获取TF卡存储空间总字节数
     *
     * @return
     */
    private static long getTotalTFSpaceBytes() {
        if (FileUtile.getInstance().getExtSDCardPathList().size() == 2) {
            String path = FileUtile.getInstance().getExtSDCardPathList().get(1);
            Log.d(TAG, "getTotalTFSpaceBytes tf_path: " + path);
            File file = new File(path);
            if (!file.exists()) {
                return 0;
            }
            StatFs stat = new StatFs(path);
            long totalBlocks = stat.getBlockCount();
            long blockSize = stat.getBlockSize();
            long availableBytes = totalBlocks * blockSize;
            return availableBytes;
        }
        return 0;
    }

    /**
     * @return long    返回类型
     * @Title: getTotalSpaceBytes
     * @Description: TODO(获取存储空间可用字节数)
     * @Param @return    设定文件
     */
    private static long getAvailableSpaceBytes() {
        String path = FileUtile.getInstance().getSDCard();
//		String path = ConfigSettings.STORAGE_PATH;

        File file = new File(path);
        if (!file.exists()) {
            return 0;
        }

        StatFs stat = new StatFs(path);
        long availableBlocks = stat.getAvailableBlocks();
        long blockSize = stat.getBlockSize();
        long availableBytes = availableBlocks * blockSize;
        return availableBytes;
    }

    /**
     * 获取TF卡可用字节数
     *
     * @return
     */
    private static long getAvailableTFSpaceBytes() {
        if (FileUtile.getInstance().getExtSDCardPathList().size() == 2) {
            String path = FileUtile.getInstance().getExtSDCardPathList().get(1);
            Log.d(TAG, "getAvailableTFSpaceBytes tf_path: " + path);
            File file = new File(path);
            if (!file.exists()) {
                return 0;
            }
            StatFs stat = new StatFs(path);
            long availableBlocks = stat.getAvailableBlocks();
            long blockSize = stat.getBlockSize();
            long availableBytes = availableBlocks * blockSize;
            return availableBytes;
        }
        return 0;
    }

    /**
     * @return String    返回类型
     * @Title: getFreeDiskSpace
     * @Description: TODO(获取可用存储空间)
     * @Param @return    设定文件
     */
    public static String getFreeDiskSpace() {
        double space = getAvailableSpaceGB();
        String value = null;

        if (space < 1.0) {
            space = getAvailableSpaceMB();
            if (space < 1.0) {
                space = getAvailableSpaceKB();
                value = "MB";
            } else {
                value = "MB";
            }
        } else {
            value = "GB";
        }

        return FORMAT.format(space) + value;
    }

    /**
     * @return String    返回类型
     * @Title: getFreeDiskSpace
     * @Description: TODO(获取存储空间总量)
     * @Param @return    设定文件
     */
    public static String getTotalDiskSpace() {
        double space = getTotalSpaceGB();
        String value = null;

        if (space < 1.0) {
            space = getTotalSpaceMB();
            if (space < 1.0) {
                space = getTotalSpaceKB();
                value = "MB";
            } else {
                value = "MB";
            }
        } else {
            value = "GB";
        }
        return FORMAT.format(space) + value;
    }
}
