package com.lzkj.launcher.util;

import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;

/**
 * 项目名称：MallPoster
 * 类名称：FileStore
 * 类描述：   文件管理类
 * 创建人："lyhuang"
 * 创建时间：2015年8月15日 下午6:49:30
 */
public class FileStore {

    private static final String TAG = "FileStore";

    public static final String ROOT_FOLDER = ConfigSettings.STORAGE_PATH + File.separator + "mallposter" + File.separator;
    public static final String QRCODE_ROOT_FOLDER = ConfigSettings.STORAGE_PATH + File.separator;
    private static final String PATH_QRCODE = QRCODE_ROOT_FOLDER + "qrcode";
    public static final String APP_FOLDER = ROOT_FOLDER + "app";

    public static final String APP_NAME = "app.zip";

    /**
     * 日志目录
     */
    public static final String PATH_LOG = ROOT_FOLDER + "log";

    public static final String PATH_TEMP = ROOT_FOLDER + "temp";

    /**
     * 存放mac地址的文件夹目录
     */
    public static final String PATH_SYSTEM = ROOT_FOLDER + ".systems";

    /**
     * EPosterUI apk路径
     */
    public static final String EPOST_PATH = APP_FOLDER + File.separator + Constant.EPOSTER_FILE_NAME;

    /**
     * DownloadService apk路径
     */
    public static final String DOWNLOAD_PATH = APP_FOLDER + File.separator + Constant.DOWNLOAD_FILE_NAME;

    /**
     * Communication apk路径
     */
    public static final String COMMUNICATION_PATH = APP_FOLDER + File.separator + Constant.COMM_FILE_NAME;

    /**
     * DS-Debug apk路径
     **/
    public static final String DEBUG_PATH = APP_FOLDER + File.separator + "DS-Debug.apk";

    /**
     * 获取日志文件目录路径
     *
     * @return 日志文件目录路径
     */
    public static final String getLogFolderPath() {
        return PATH_LOG;
    }

    /**
     * @return String    返回类型
     * @Title: getQrcodeFolderPath
     * @Param @return    设定文件
     */
    public static final String getQrcodeFolderPath() {
        String path = PATH_QRCODE;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 获取systems文件夹路径
     */
    private static final String getSystemsFilePath() {
        String path = PATH_SYSTEM;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 获取设备WIFI mac地址
     *
     * @return
     */
    public static String getMac() {
        String mac = null;
        File[] file = new File(getSystemsFilePath()).listFiles();
        if (null != file && file.length > 0) {
            for (int i = 0; i < file.length; i++) {
                mac = file[i].getName();
                if (mac.length() == 12) {
                    Log.d(TAG, "FileStore getMac mac1: " + mac);
                    return mac;
                }
            }
        }
        if (StringUtil.isNullStr(mac)) {
            mac = Helper.getMacAddress().toUpperCase(); // 将WIFI mac地址字符串转换成大写
            Log.d(TAG, "FileStore getMac mac2: " + mac);
            try {
                File macFile = new File(getSystemsFilePath(), mac);
                macFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mac;
    }

    public static boolean saveStringToFile(String filePath, String content) {
        boolean isSuccess = false;
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(filePath);
            fileOutputStream.write(content.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
            isSuccess = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return isSuccess;
    }

    public static String readStringToFile(String filePath) {
        if (null != filePath || !new File(filePath).exists()) {
            return null;
        }
        StringBuffer sb = new StringBuffer(10);
        try {
            FileInputStream fileInputStream = new FileInputStream(filePath);
            int length = -1;
            byte[] buffer = new byte[1024 * 1024];
            while ((length = fileInputStream.read(buffer)) != -1) {
                sb.append(new String(buffer, 0, length));
            }
            fileInputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * @return boolean    返回类型
     * @Title: fileExists
     * @Description: TODO(判断文件是否存在)
     * @Param @param filePath
     * @Param @return    设定文件
     */
    public static boolean fileExists(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            return true;
        }
        return false;
    }

    /**
     * 获取epost app安装包路径
     *
     * @return
     */
    public static String getEpostAppPath() {
        return EPOST_PATH;
    }

    /**
     * 获取Download app安装包路径
     *
     * @return
     */
    public static String getDownloadAppPath() {
        return DOWNLOAD_PATH;
    }

    /**
     * 获取Communication app安装包路径
     *
     * @return
     */
    public static String getCommunicationAppPath() {
        return COMMUNICATION_PATH;
    }

    /**
     * 获取Communication app安装包路径
     *
     * @return
     */
    public static String getDebugAppPath() {
        return DEBUG_PATH;
    }

    /**
     * 创建app folder
     */
    public static void createAppFolder() {
        File appFolder = new File(APP_FOLDER);
        appFolder.mkdirs();
    }

    /**
     * 创建app folder
     */
    public static String createTempFolder() {
        File appFolder = new File(PATH_TEMP);
        appFolder.mkdirs();
        return appFolder.getAbsolutePath();
    }

    /**
     * 写入文件到本地
     *
     * @param content
     * @param file
     * @return 返回写入到本地的路径
     */
    public static String writerContentToFile(String content, File file) {
        try {
            FileWriter fileWriter = new FileWriter(file, false);
            fileWriter.write(content);
            fileWriter.flush();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file.getAbsolutePath();
    }
}
