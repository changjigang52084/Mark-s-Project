package com.unccr.zclh.dsdps.util;


import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.github.mjdev.libaums.fs.UsbFile;
import com.github.mjdev.libaums.fs.UsbFileOutputStream;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.os.Environment.getExternalStorageDirectory;

/***
 * 文件方面的工具类
 * @author changkai
 *
 */
public class FileUtil {
    /**
     * 根目录
     */
//	public static final String SDCARD_PATH = Environment.getExternalStorageDirectory()
//			.getAbsolutePath()+File.separator;

    public static final String DOWNLOAD_FLOW_INFO = "DOWNLOAD_FLOW_INFO";
    private static final String TAG = "FileUtil";
    private String ROOT_FOLDER = "zclh" + File.separator;
    private String ROOT_PROGRAMME_FOLDER = "zcdt" + File.separator;
    private String PATH_PROGRAMME_TEMP = ROOT_PROGRAMME_FOLDER + "temp";
    private String PATH_PROGRAMME_PIC = ROOT_PROGRAMME_FOLDER + "pic";
    private String PATH_PROGRAMME_VIDEO = ROOT_PROGRAMME_FOLDER + "video";
    private String PATH_TEMP = ROOT_FOLDER + "temp";
    private String PATH_PRM = ROOT_FOLDER + "prm";
    private String PATH_VIDEO = ROOT_FOLDER + "video";
    private String PATH_PIC = ROOT_FOLDER + "pic";
    private String PATH_LOG = ROOT_FOLDER + "log";
    /**
     * 截图文件目录
     */
    public String PATH_SHOT = ROOT_FOLDER + "shot";

    private String PATH_APP = ROOT_FOLDER + "app";
    public String UUID_FOLDER = ROOT_FOLDER + "uuid/";
    public static final String UUID_FILE = "uuid.json";
    public static final String TEMP_FILE = "temp.txt";
    public static final String HASH_FILE = "hash";
    public static final String PRM_SUFFIX_NAME = ".prm";
    public static final String TEMP_SUFFIX_NAME = ".temp";
    public String PATH_QN_RECOR = ROOT_FOLDER + "qnrecor";
    public static final String WAIT_PRM_SUFFIX_NAME = ".waitPrm";
    /**
     * 分隔符 +++
     */
    public static final String SPLIT_PATH = "+++";

    /**
     * 等待下载的列表文件名
     **/
    public static final String WAIT_DOWNLOAD_TMEP_NAME = "waitdownloadlist.temp";

    /**
     * 5 m
     */
    private static final long TEMP_MAX_LENGTH = 5 * 1024 * 1024;
    /**
     * 日志文件名前缀
     */
    private static final String LOG_PREFIX = "Eposter";
    /**
     * 日志文件后缀
     */
    private static final String LOG_SUFFIX = "log";
    /**
     * 保存文件类型的map对象
     */
    private Map<String, String> contentTypeMap = new HashMap<String, String>();

    public Map<String, String> suffixToFolder = new HashMap<String, String>();

    public Map<String, String> suffixToProgrammeFolder = new HashMap<String, String>();

    private static volatile FileUtil fileUtil;

    private String sdcard;

    private FileUtil() {
        init();
    }

    public static FileUtil getInstance() {
        if (null == fileUtil) {
            synchronized (FileUtil.class) {
                if (null == fileUtil) {
                    fileUtil = new FileUtil();
                }
            }
        }
        return fileUtil;
    }

    private void init() {
        contentTypeMap.clear();
        suffixToFolder.clear();
        suffixToProgrammeFolder.clear();

        contentTypeMap.put("jpeg", "image/jpeg");
        contentTypeMap.put("png", "image/png");
        contentTypeMap.put("txt", "text/plain");
        contentTypeMap.put("log", "text/plain");
        contentTypeMap.put("pdf", "application/pdf");
        contentTypeMap.put("mp4", "video/mpeg4");
        contentTypeMap.put("avi", "video/avi");
        contentTypeMap.put("doc", "application/msword");

        suffixToFolder.put("png", getPicFolder());
        suffixToFolder.put("jpeg", getPicFolder());
        suffixToFolder.put("jpg", getPicFolder());
        suffixToFolder.put("gif", getPicFolder());
        suffixToFolder.put("bmp", getPicFolder());
        suffixToFolder.put("PNG", getPicFolder());
        suffixToFolder.put("JPEG", getPicFolder());
        suffixToFolder.put("JPG", getPicFolder());
        suffixToFolder.put("GIF", getPicFolder());
        suffixToFolder.put("BMP", getPicFolder());
        suffixToFolder.put("prm", getPrmFolder());

        suffixToProgrammeFolder.put("png", getPrmPicFolder());
        suffixToProgrammeFolder.put("jpeg", getPrmPicFolder());
        suffixToProgrammeFolder.put("jpg", getPrmPicFolder());
        suffixToProgrammeFolder.put("gif", getPrmPicFolder());
        suffixToProgrammeFolder.put("bmp", getPrmPicFolder());
        suffixToProgrammeFolder.put("PNG", getPrmPicFolder());
        suffixToProgrammeFolder.put("JPEG", getPrmPicFolder());
        suffixToProgrammeFolder.put("JPG", getPrmPicFolder());
        suffixToProgrammeFolder.put("GIF", getPrmPicFolder());
        suffixToProgrammeFolder.put("BMP", getPrmPicFolder());

    }

    /**
     * @return the sdcard
     */
    public String getSdcard() {
        if (TextUtils.isEmpty(sdcard)) {
            sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        }
        return sdcard;
    }

    /**
     * @param sdcard the sdcard to set
     */
    public void setSdcard(String sdcard) {
        if (TextUtils.isEmpty(sdcard)) {
            this.sdcard = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator;
        } else {
            this.sdcard = sdcard + File.separator;
        }
        init();
    }

    public String getRoot() {

        return getSdcard() + ROOT_FOLDER;
    }

    /**
     * 返回prm文件夹的路径
     *
     * @return
     */
    public String getPrmFolder() {

        return getSdcard() + PATH_PRM;
    }

    /**
     * 返回pic文件夹的路径
     *
     * @return
     */
    public String getPicFolder() {

        return getSdcard() + PATH_PIC;
    }

    /**
     * 返回pic文件夹的路径（电梯）
     *
     * @return
     */
    public String getPrmPicFolder() {

        return getSdcard() + PATH_PROGRAMME_PIC;
    }

    /**
     * 返回video文件夹的路径
     *
     * @return
     */
    public String getVideoFolder() {
        return getSdcard() + PATH_VIDEO;
    }

    /**
     * 返回video文件夹的路径（电梯）
     *
     * @return
     */
    public String getVideoPrmFolder() {
        return getSdcard() + PATH_PROGRAMME_VIDEO;
    }

    /**
     * 返回temp文件夹的路径
     *
     * @return
     */
    public String getTempFolder() {
        return getSdcard() + PATH_TEMP;
    }

    /**
     * 返回temp文件夹的路径(电梯)
     *
     * @return
     */
    public String getProgrammeTempFolder() {
        return getSdcard() + PATH_PROGRAMME_TEMP;
    }

    /**
     * 返回App文件夹的路径
     *
     * @return
     */
    public String getAPPFolder() {
        String path = getSdcard() + PATH_APP;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    public String getWaitFilePath() {
        String folder = getTempFolder();
        File folderFile = new File(folder);
        folderFile.mkdirs();
        File waitFile = new File(folderFile, WAIT_DOWNLOAD_TMEP_NAME);
        return waitFile.getAbsolutePath();
    }


    /**
     * 返回等待下载的节目文件夹的路径
     *
     * @return
     */
    public String getWaitDownloadPrmFolder() {
        String path = getSdcard() + ROOT_FOLDER + WAIT_PRM_SUFFIX_NAME;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 根据文件路径获取文件夹名称
     *
     * @param filePath 文件路径,传入null or ""返回sd卡的根目录
     * @return 返回文件夹名称
     */
    public String getFolderToFilePath(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return getSdcard();
        }
        return filePath.substring(0, filePath.lastIndexOf(File.separator));
    }

    /**
     * 根据http地址获取文件名
     *
     * @param httpUrl 下载地址
     * @return 文件名
     */
    public static String getFileName(String httpUrl) {
        if (null == httpUrl) {
            return httpUrl;
        }
        // http://7xq81q.com1.z0.glb.clouddn.com/1453285422797-test.mp4?sdfsdfasdflkajsldfjlasdjflasjdf=S>?AWekrwer
        String fileName = httpUrl.substring(httpUrl.lastIndexOf("/") + 1, httpUrl.length());
//		String fileName = httpUrl.substring(httpUrl.lastIndexOf("/")+1, httpUrl.lastIndexOf("?"));
        try {
            if (fileName.indexOf("@") != -1) {
                fileName = fileName.substring(fileName.indexOf("@") + 1, fileName.length());
            }
            if (fileName.indexOf("?") != -1) {
                // fileName = "1453285422797-test.mp4"
                fileName = fileName.substring(0, fileName.indexOf("?"));
            }
            // 解码成UTF-8的字符串
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 根据文件名获取文件后缀
     *
     * @param fileName 文件名
     * @return 文件后缀
     */
    public static String getSuffix(String fileName) {
        if (null == fileName) {
            return fileName;
        }
        return fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length());
    }

    /**
     * 根据本地文件路径获取文件名
     *
     * @param filePath 文件路径
     * @return 文件名
     */
    public static String getFileNameToFilePath(String filePath) {
        if (null == filePath) {
            return filePath;
        }
        return filePath.substring(filePath.lastIndexOf("/") + 1, filePath.length());
    }

    /**
     * 根据后缀获取content-type
     *
     * @param suffix 后缀名,例如"jpeg",返回的就是"image/jpeg"
     * @return
     */
    public String getContentTypeToSuffix(String suffix) {
        if (null == suffix) {
            return suffix;
        }
        if (contentTypeMap.containsKey(suffix)) {
            return contentTypeMap.get(suffix);
        }
        return null;
    }

    /**
     * 返回编码过的地址
     *
     * @param httpUrl
     * @return
     */
    public static String encodeUrl(String httpUrl) {
        try {
            String http = httpUrl.substring(0, httpUrl.lastIndexOf("/") + 1);
            String httpEnd = httpUrl.substring(httpUrl.lastIndexOf("/") + 1, httpUrl.length());
            String fileName = null;
            String split = "?";
            if (httpEnd.contains(split)) {
                fileName = httpEnd.substring(0, httpEnd.indexOf(split));
                httpEnd = httpEnd.substring(httpEnd.indexOf(split), httpEnd.length());
                fileName = Uri.encode(fileName, "UTF-8");
            } else {
                fileName = Uri.encode(httpEnd, "UTF-8");
                return http + fileName;
            }
            return http + fileName + httpEnd;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取文件的md5值
     *
     * @param filePath
     * @return
     */
    public static String getMD5(String filePath) {
        String md5 = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            int bufferSize = 256 * 1024;
            File file = new File(filePath);
            if (file.exists()) {
                FileInputStream fis = null;
                fis = new FileInputStream(file);
                byte[] buffer = new byte[bufferSize];
                int length = 0;
                while ((length = fis.read(buffer)) != -1) {
                    md.update(buffer, 0, length);
                }
                fis.close();
                BigInteger bi = new BigInteger(1, md.digest());
                md5 = bi.toString(16).toUpperCase();
            }
        } catch (NoSuchAlgorithmException e) {
        } catch (IOException e) {
        }
        return md5;
    }

    /**
     * 获取日志的文件夹目录
     *
     * @return
     */
    public String getLogFolder() {

        return getSdcard() + PATH_LOG;
    }

    /**获取截图的文件夹目录**/
//	public static String getScreenshot() {
//		return SDCARD_PATH + PATH_SCREENSHOT;
//	}

    /**
     * 获取所有的日志文件
     *
     * @return
     */
    public List<String> getLogFilePaths() {
        List<String> logList = new ArrayList<String>();
        File logFolder = new File(getLogFolder());
        File[] paths = logFolder.listFiles();
        for (File file : paths) {
            logList.add(file.getAbsolutePath());
        }
        return logList;
    }

    /**
     * 获取所有的截图文件目录
     *
     * @return
     */
    public List<String> getScreenshotFilePaths() {
        List<String> logList = new ArrayList<String>();
        File logFolder = new File(getShotFolderPath());
        File[] paths = logFolder.listFiles();
        for (File file : paths) {
            logList.add(file.getAbsolutePath());
        }
        return logList;
    }

    /**
     * 根据文件路径获取文件大小
     *
     * @param filePath 本地文件的路径
     * @return 文件大小
     */
    public static long getFileSizeToFilePath(String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            return 0;
        }
        return file.length();
    }

    /**
     * 读取hash文件
     */
    public static String readHash() {
        //需要修改获取hash值的位置
//		File folder = new File(HASH_FOLDER);
//		File file = new File(folder.getAbsolutePath()+ File.separator + HASH_FILE);
//		try {
//			if (!folder.exists()) {
//				return null;
//			}
//			if (!file.exists()) {
//				return null;
//			}
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//			StringBuffer buffer = new StringBuffer();
//			String temp = null;
//			while ((temp = bufferedReader.readLine()) != null) {
//				buffer.append(temp);
//			}
//			bufferedReader.close();
//			return buffer.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
        return "FqycZ8z00_YQbu9wxefp-TaJjCjC";
    }


    /**
     * 日志枚举类型
     */
    public enum LogType {
        /**
         * 播放日志
         */
        PLAY_LOG,
        /**
         * 错误日志
         */
        ERROR_LOG,
        /**
         * 设备运行日志
         */
        OPERATION_LOG,
        /**
         * 下载错误日志
         */
        DOWNLOADERROR_LOG
    }

    /**
     * 读取uuid到文件
     */
    public String readUUID() {
        File folder = new File(getSdcard() + UUID_FOLDER);
        File file = new File(folder.getAbsolutePath() + File.separator + UUID_FILE);
        try {
            if (!folder.exists()) {
                return null;
            }
            if (!file.exists()) {
                return null;
            }
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
            StringBuffer buffer = new StringBuffer();
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                buffer.append(temp);
            }
            bufferedReader.close();
            return buffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 写入uuid到文件
     *
     * @param jsonData
     */
    public void writeUUID(final String jsonData) {
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                File folder = new File(getSdcard() + UUID_FOLDER);
                File file = new File(folder.getAbsolutePath() + File.separator + UUID_FILE);
                try {
                    if (!folder.exists()) {
                        folder.mkdir();
                    }
                    if (!file.exists()) {
                        file.createNewFile();
                    }
                    if (null == jsonData) {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        fileOutputStream.write("".getBytes());
                        fileOutputStream.flush();
                        fileOutputStream.close();
                        return;
                    }
                    FileWriter fileWriter = new FileWriter(file);
                    fileWriter.write(jsonData);
                    fileWriter.flush();
                    fileWriter.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 写入数据
     *
     * @param filePath   文件路径
     * @param contentMsg 写入的内容
     * @param isAppend   是否追加
     * @return 返回成功的路径
     * @throws IOException 抛出异常
     */
    public static String writerContentToFile(String filePath, String contentMsg, boolean isAppend) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath, isAppend);
        fileWriter.write(contentMsg);
        fileWriter.flush();
        fileWriter.close();
        return filePath;
    }


    /**
     * @param filePath 文件的路径
     * @return 路径获取String数据
     */
    public static String readStringToPath(String filePath) {
        try {
            File programFile = new File(filePath);
            if (!programFile.exists()) {
                return null;
            }
            FileInputStream fileInputStream = new FileInputStream(programFile);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
            StringBuffer stringBuffer = new StringBuffer();
            String temp = null;
            while ((temp = bufferedReader.readLine()) != null) {
                stringBuffer.append(temp);
            }
            bufferedReader.close();
            fileInputStream.close();
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

//    public static String getDownloadTaskFileName(boolean isWriterFile) {
//        if (isWriterFile) {
//            return  Constant.DOWNLOAD_TASK_PATH + Helper.getStringTimeToFormat("yyyy_MM_dd") + "_" + ConfigSettings.getDeviceId() + ".log";
//        }
//        return  Constant.DOWNLOAD_TASK_PATH + getCurrentFrontDate(-1) + "_" + ConfigSettings.getDeviceId() + ".log";
//    }

    /**
     * 获取当前时间的前n天日期
     *
     * @param front 前多少天负数,后多少天正数,例如:当前日期20150928 传入-2则返回20150926,传入2则返回20150930
     * @return
     */
    private static String getCurrentFrontDate(int front) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DATE, front);
        String preMonday = sdf.format(c.getTime());
        return preMonday;
    }


    /***
     * 获取节目列表里面的节目文件,节目的文件名是pid_pver.json
     * 节目列表的文件名是固定的叫eposter_pl.json
     */
    public String getProgrameListFolderPath(String prmId) {

        return getSdcard() + PATH_PRM + File.separator + prmId;
    }

    /**
     * 获取视频文件存放路径
     *
     * @return 视频文件存放路径
     */
    public String getVideoFolderPath() {
        String path = getSdcard() + PATH_VIDEO;
        File folder = new File(path);
        folder.mkdirs();

        return path;
    }

    public String getShotFolderPath() {
        String path = getSdcard() + PATH_SHOT;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 去掉文件名后缀
     *
     * @param filename 文件名
     * @return
     */
    public static String getFileNameWithoutSuffix(String filename) {
        return filename.substring(0, filename.lastIndexOf("."));
    }

    public static void delete(final String path) {
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                File f1 = new File(path);
                if (f1.isDirectory()) {
                    String[] fileList = f1.list();
                    if (null == fileList) {
                        return;
                    }
                    for (String s : fileList) {
                        File f2 = new File(f1, s);
                        if (f2.isDirectory()) {
                            delete(f2.getAbsolutePath());
                        } else {
                            f2.delete();
                        }
                    }
                    f1.delete();
                } else {
                    f1.delete();
                }
            }
        });

    }

    /**
     * 删除空文件夹
     *
     * @param path 删除文件的路径
     */
    public static void deleteDirectory(String path) {
        File f1 = new File(path);
        f1.delete();
    }

    public static String getFileNameToCloudPath(String cloudPath) {
        if (null == cloudPath) {
            return null;
        }
        return cloudPath.substring(cloudPath.lastIndexOf("/") + 1, cloudPath.length());
    }

    /**
     * 更改文件名
     */
    public void renamePl(String prmId) {
        String plTempPath = getProgrameListFolderPath(prmId) + TEMP_SUFFIX_NAME;
        File file = new File(plTempPath);
        if (file.exists()) {
            File prmFile = new File(file.getParent() + File.separator + prmId + PRM_SUFFIX_NAME);
            if (prmFile.exists()) {
                prmFile.delete();
            }
            file.renameTo(prmFile);
        }
    }

    /**
     * 更改视频名
     */
    public void renameVideoName(String httpUrl) {
        String fileName = FileUtil.getFileName(httpUrl);
        String videoPath = FileUtil.getInstance().getVideoPrmFolder() + File.separator + fileName.substring(0,fileName.indexOf("."))+ TEMP_SUFFIX_NAME;
        File file = new File(videoPath);
        if (file.exists()) {
            File prmFile = new File(file.getParent() + File.separator + fileName);
            if (prmFile.exists()) {
                prmFile.delete();
            }
           file.renameTo(prmFile);
        }
    }


    /**
     * 获取七牛断点上传的文件夹
     *
     * @return 文件夹的路径
     */
    public String getQiNiuRecorderFolder() {
        return getSdcard() + PATH_QN_RECOR;
    }

    public static final String DEFAULT_BIN_DIR = "usb";

    /**
     * 检测SD卡是否存在
     */
    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 从指定文件夹获取文件
     *
     * @return 如果文件不存在则创建, 如果如果无法创建文件或文件名为空则返回null
     */
    public static File getSaveFile(String folderPath, String fileNmae) {
        File file = new File(getSavePath(folderPath) + File.separator
                + fileNmae);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取SD卡下指定文件夹的绝对路径
     *
     * @return 返回SD卡下的指定文件夹的绝对路径
     */
    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath();
    }

    /**
     * 获取文件夹对象
     *
     * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
     */
    public static File getSaveFolder(String folderName) {
        File file = new File(getExternalStorageDirectory()
                .getAbsoluteFile()
                + File.separator
                + folderName
                + File.separator);
        file.mkdirs();
        return file;
    }

    /**
     * 关闭流
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void redFileStream(OutputStream os, InputStream is) throws IOException {
        Log.d(TAG, "redFileStream.");
        int bytesRead = 0;
        byte[] buffer = new byte[1024 * 8];
        while ((bytesRead = is.read(buffer)) != -1) {
            os.write(buffer, 0, bytesRead);
        }
        os.flush();
        os.close();
        is.close();
    }

    /**
     * @description 把本地文件写入到U盘中
     * @author ldm
     * @time 2017/8/22 10:22
     */
    public static void saveSDFile2OTG(final File f, final UsbFile usbFile) {
        Log.d(TAG, "saveSDFile2OTG111111111111");
        UsbFile uFile = null;
        Log.d(TAG, "saveSDFile2OTG222222222222");
        FileInputStream fis = null;
        Log.d(TAG, "saveSDFile2OTG333333333333");
        try {//开始写入
            Log.d(TAG, "saveSDFile2OTG44444444444");
            fis = new FileInputStream(f);//读取选择的文件的
            Log.d(TAG, "usbFile.isDirectory(): " + usbFile.isDirectory());
            if (usbFile.isDirectory()) {//如果选择是个文件夹
                UsbFile[] usbFiles = usbFile.listFiles();
                Log.d(TAG, "usbFiles: " + usbFile + " ,usbFiles length: " + usbFiles.length);
                if (usbFiles != null && usbFiles.length > 0) {
                    for (UsbFile file : usbFiles) {
                        if (file.getName().equals(f.getName())) {
                            file.delete();
                        }
                    }
                }
                uFile = usbFile.createFile(f.getName());
                UsbFileOutputStream uos = new UsbFileOutputStream(uFile);
                try {
                    redFileStream(uos, fis);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (final Exception e) {
            e.printStackTrace();
        }
    }

}