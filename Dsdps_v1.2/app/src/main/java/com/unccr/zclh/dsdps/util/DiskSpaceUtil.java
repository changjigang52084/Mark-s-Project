package com.unccr.zclh.dsdps.util;

import android.os.Environment;
import android.os.StatFs;

import java.io.File;
import java.text.DecimalFormat;

public class DiskSpaceUtil {

    private static final String TAG = "DiskSpaceUtil";

    public final static long DEFAULT_ERROR_VALUE = 0L;
    private final static long SIZE_KB = 1024L;
    private final static long SIZE_MB = SIZE_KB * SIZE_KB;
    private final static long SIZE_GB = SIZE_KB * SIZE_KB * SIZE_KB;
    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    /**
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取可用空间，以KB为单位)
     * @Param @return    设定文件
     * @return float    可用空间大小
     */
    public static long getAvailableSpaceKB() {
        long space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = getAvailableSpaceBytes() /  SIZE_KB;
        }
        return space;
    }


    /**
     * @Title: getAvailableSpaceMB
     * @Description: TODO(获取可用空间，以MB为单位)
     * @Param @return    设定文件
     * @return float   可用空间大小
     */
    public static double getAvailableSpaceMB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getAvailableSpaceBytes() / (double) SIZE_MB;
        }
        return space;
    }

    /**
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取可用空间，以GB为单位)
     * @Param @return    设定文件
     * @return float    可用空间大小
     */
    public static double getAvailableSpaceGB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getAvailableSpaceBytes() / (double) SIZE_GB;
        }
        return space;
    }

    /**
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取存储空间总量，以KB为单位)
     * @Param @return    设定文件
     * @return float    可用空间大小
     */
    public static long getTotalSpaceKB() {
        long space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = getTotalSpaceBytes() / SIZE_KB;
        }
        return space;
    }

    /**
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取存储空间总量，以MB为单位)
     * @Param @return    设定文件
     * @return float    可用空间大小
     */
    public static double getTotalSpaceMB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getTotalSpaceBytes() / (double) SIZE_MB;
        }
        return space;
    }

    /**
     * @Title: getAvailableSpaceKB
     * @Description: TODO(获取存储空间总量，以GB为单位)
     * @Param @return    设定文件
     * @return float    可用空间大小
     */
    public static double getTotalSpaceGB() {
        double space = DEFAULT_ERROR_VALUE;
        if (hasExternalSpace()) {
            space = (double) getTotalSpaceBytes() / (double) SIZE_GB;
        }
        return space;
    }


    /**
     * @Title: hasExternalSpace
     * @Description: TODO(判断SD卡是否存在，并且可以进行写操作)
     * @Param @return    设定文件
     * @return boolean    返回类型
     */
    private static boolean hasExternalSpace() {
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }


    /**
     * @Title: getFreeSpacePercentage
     * @Description: TODO(获取存储空间的可用率)
     * @Param @return    设定文件
     * @return float    返回类型
     */
    public static double getFreeSpacePercentage() {
        return getTotalSpaceBytes() == 0 ? DEFAULT_ERROR_VALUE : 100 * getAvailableSpaceBytes() / getTotalSpaceBytes();
    }

    /**
     * @Title: getFreeSpacePercentage
     * @Description: TODO(获取存储空间的使用率)
     * @Param @return    设定文件
     * @return float    返回类型
     */
    public static double getUsedSpacePercentage() {
        return getTotalSpaceBytes() == 0 ? DEFAULT_ERROR_VALUE : 100 - 100 * getAvailableSpaceBytes() / getTotalSpaceBytes();
    }


    /**
     * @Title: getTotalSpaceBytes
     * @Description: TODO(获取存储空间总字节数)
     * @Param @return    设定文件
     * @return long    返回类型
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
     * @Title: getTotalSpaceBytes
     * @Description: TODO(获取存储空间可用字节数)
     * @Param @return    设定文件
     * @return long    返回类型
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
     * @Title: getFreeDiskSpace
     * @Description: TODO(获取可用存储空间)
     * @Param @return    设定文件
     * @return String    返回类型
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
     * @Title: getFreeDiskSpace
     * @Description: TODO(获取存储空间总量)
     * @Param @return    设定文件
     * @return String    返回类型
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
