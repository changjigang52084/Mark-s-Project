package com.unccr.zclh.dsdps.util;


import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月17日 下午7:49:37
 * @parameter 文件工具类
 */
public class FileUtile {

    private static final String TAG = "FileUtile";

    /**
     * 根目录
     */
    private String sdcardPath = null;

    public static final String PRM_FILE = "prm";
    public static final String PIC_FILE = "pic";
    public static final String LOG_FILE = "log";
    public static final String MALL_PIC_FILE = "mallpic";
    public static final String VIDEO_FILE = "video";
    public static final String PRM_SUFFIX_NAME = ".prm";
    public static final String TEMP_SUFFIX_NAME = ".temp";
    public static final String WAIT_PRM_SUFFIX_NAME = ".waitPrm";
    public static final String HASH_FILE = "hash";

    public Map<String, String> suffixToFolder = new HashMap<String, String>();

    private static volatile FileUtile fileUtile;

    private FileUtile() {
        init();
    }

    public static FileUtile getInstance() {
        if (null == fileUtile) {
            synchronized (FileUtile.class) {
                if (null == fileUtile) {
                    fileUtile = new FileUtile();
                }
            }
        }
        return fileUtile;
    }

    private void init() {
        suffixToFolder.clear();
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
    }

    /**
     * 存放mac地址的文件夹目录
     */
    public String getPathSystem() {
        return getRoot() + ".systems";
    }

    public String getPathScaled() {
        return getRoot() + "scaled";
    }

    public String getAppFolder() {
        return getRoot() + "app";
    }

    public String getRoot() {
        return getSDCard() + "zclh" + File.separator;
    }

    public String getSDCard() {
        if (TextUtils.isEmpty(sdcardPath)) {
            sdcardPath = Environment.getExternalStorageDirectory()
                    .getAbsolutePath() + File.separator;
        }
        return sdcardPath;
    }

    /**
     * 获取外置SD卡路径以及TF卡的路径
     *
     * @return：paths.get(0)肯定是外置sd卡的位置，因为它是primary external storage.
     */
    public static List<String> getExtSDCardPathList() {
        List<String> paths = new ArrayList<String>();
        String extFileStatus = Environment.getExternalStorageState();
        File extFile = Environment.getExternalStorageDirectory();
        //首先判断一下外置SD卡的状态，处于挂载状态才能获取的到
        if (extFileStatus.equals(Environment.MEDIA_MOUNTED)
                && extFile.exists() && extFile.isDirectory()
                && extFile.canWrite()) {
            //外置SD卡的路径
            paths.add(extFile.getAbsolutePath());
        }
        try {
            // obtain executed result of command line code of 'mount', to judge
            // whether tfCard exists by the result
            Runtime runtime = Runtime.getRuntime();
            Process process = runtime.exec("mount");
            InputStream is = process.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String line = null;
            int mountPathIndex = 1;
            while ((line = br.readLine()) != null) {
                // format of sdcard file system: vfat/fuse
                if ((!line.contains("fat") && !line.contains("fuse") && !line
                        .contains("storage"))
                        || line.contains("secure")
                        || line.contains("asec")
                        || line.contains("firmware")
                        || line.contains("shell")
                        || line.contains("obb")
                        || line.contains("legacy") || line.contains("data")) {
                    continue;
                }
                String[] parts = line.split(" ");
                int length = parts.length;
                if (mountPathIndex >= length) {
                    continue;
                }
                String mountPath = parts[mountPathIndex];
                if (!mountPath.contains("/") || mountPath.contains("data")
                        || mountPath.contains("Data")) {
                    continue;
                }
                File mountRoot = new File(mountPath);
                if (!mountRoot.exists() || !mountRoot.isDirectory()
                        || !mountRoot.canWrite()) {
                    continue;
                }
                boolean equalsToPrimarySD = mountPath.equals(extFile
                        .getAbsolutePath());
                if (equalsToPrimarySD) {
                    continue;
                }
                //扩展存储卡即TF卡或者SD卡路径
                paths.add(mountPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return paths;
    }

    public void setSDCard(String sdCard) {
        if (TextUtils.isEmpty(sdCard)) {
            sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
        } else {
            sdcardPath = sdCard + File.separator;
        }
        init();
    }

    /**
     * 获取systems文件夹路径
     */
    private final String getSystemsFilePath() {
        String path = getPathSystem();
        File folder = new File(path);
        folder.mkdirs();
        return path;
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
        String fileName = httpUrl.substring(httpUrl.lastIndexOf("/") + 1, httpUrl.length());
        try {
            if (fileName.indexOf("@") != -1) {
                fileName = fileName.substring(fileName.indexOf("@") + 1, fileName.length());
            }
            if (fileName.indexOf("?") != -1) {
                fileName = fileName.substring(0, fileName.indexOf("?"));
            }
            fileName = URLDecoder.decode(fileName, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return fileName;
    }

    /**
     * 返回log文件夹的路径
     *
     * @return
     */
    public final String getLogFolder() {
        String path = getRoot() + LOG_FILE;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    public final String getScaledImageFolderPath() {
        File folder = new File(getPathScaled());
        folder.mkdirs();
        return getPathScaled();
    }

    /**
     * 返回temp文件夹的路径
     *
     * @return
     */
    public final String getTempFolder() {
        String path = getRoot() + TEMP_SUFFIX_NAME;
        Log.d("FileUtils", "path: " + path);
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 返回等待下载的节目文件夹的路径
     *
     * @return
     */
    public final String getWaitDownloadPrmFolder() {
        String path = getRoot() + WAIT_PRM_SUFFIX_NAME;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 返回pic文件夹的路径
     *
     * @return
     */
    public final String getPicFolder() {
        String path = getRoot() + PIC_FILE;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 返回video文件夹的路径
     *
     * @return
     */
    public final String getVideoFolder() {
        String path = getRoot() + VIDEO_FILE;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 返回所有文件夹的路径
     *
     * @return
     */
    public final String getAllFolderStructure() {
        StringBuffer stringBuffer = new StringBuffer();
        loadFolderAllFile(new File(getRootFolderPath()), stringBuffer);
        return stringBuffer.toString();
    }

    private static void loadFolderAllFile(File folderFile, StringBuffer fileList) {
        if (folderFile.isDirectory()) {
            File[] files = folderFile.listFiles();
            for (File file : files) {
                if (file.isDirectory()) {
                    loadFolderAllFile(file, fileList);
                } else {
                    fileList.append("\r\n");
                    fileList.append(file.getAbsolutePath());
                }
            }
        } else {
            fileList.append("\r\n");
            fileList.append(folderFile.getAbsolutePath());
        }
    }

    /**
     * @return String    返回类型
     * @Title: getMallImageFolderPath
     * @Description: TODO(获取mall图片文件路径)
     * @Param @return    设定文件
     */
    public final String getMallImageFolderPath() {
        String path = getRoot() + MALL_PIC_FILE;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 获取一般布局文件存放路径
     *
     * @return 布局文件存放路径
     */
    public final String getLayoutFolderPath() {
        String path = getRoot() + PRM_FILE;
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 获取mallposter文件夹
     *
     * @return
     */
    public final String getRootFolderPath() {
        String path = getSDCard() + "mallposter";
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 根据节目名称获取显示的节目本地路径
     *
     * @param programName 节目名称
     * @return 节目的完整路径
     */
    public String getProgramePathToFileName(String programName) {
        String programFilePath = getLayoutFolderPath() + File.separator + programName + PRM_SUFFIX_NAME;
        if (new File(programFilePath).exists()) {
            return programFilePath;
        }
        return getLayoutFolderPath() + File.separator + programName + TEMP_SUFFIX_NAME;
    }

    /**
     * 写入节目到SD卡
     *
     * @param fileName 文件名
     *                 文件的内容
     */
    public void writePrm(final String fileName, final Program prmBean) {
        if (null == fileName || null == prmBean) {
            return;
        }
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    File floderFile = new File(getRoot() + PRM_FILE);
                    if (!floderFile.exists()) {
                        floderFile.mkdir();
                    }
                    File localPrmFile = new File(floderFile, fileName + PRM_SUFFIX_NAME);
                    if (localPrmFile.exists()) {
                        return;
                    }

                    File prmFile = new File(floderFile, fileName + TEMP_SUFFIX_NAME);
                    if (!prmFile.exists()) {
                        prmFile.createNewFile();
                    }
                    String prmStr = JSON.toJSONString(prmBean);
                    writePrmContent(prmFile, prmStr);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 根据program id来删除本地节目列表
     *
     * @param prmkey 节目id
     * @return 判断是否成功 true成功,反之不成功
     */
    public boolean delProgramToPrmId(String prmkey) {
        Log.d(TAG, "delProgramToPrmId prmId: " + prmkey);

        File floderFile = new File(getRoot() + PRM_FILE);
        if (!floderFile.exists()) {
            return true;
        }
        String fileName = prmkey + PRM_SUFFIX_NAME;
        File file = new File(floderFile, fileName);
        if (!file.exists()) {
            file = new File(floderFile, prmkey + TEMP_SUFFIX_NAME);
        }
        return delFile(file);
    }

    /**
     * 删除节目文件
     *
     * @param file
     * @return
     */
    public boolean delFile(File file) {
        if (file.exists()) {
            String prmJson = readPrmToFile(file);//读取要删除的节目
            file.delete();
            Program program = JSON.parseObject(prmJson, Program.class);
            if (null != program) {
                //获取所有要删除的素材
                List<String> delFileNameList = getAllPrmFileInfo(program);
                //获取除了要删除的节目列表之外的所有素材
                List<String> notDelFileNameList = getNotDelFileDownloadList(file.getName());
                List<String> saveFileNameList = new ArrayList<String>();
                for (String delFileName : delFileNameList) {//遍历删除的节目列表素材
                    Log.e(TAG, "delProgramToPrmId Check delete file: " + delFileName);
                    if (notDelFileNameList.contains(delFileName)) {//判断未删除的节目素材列表中,是否包含了要删除列表中的素材。
                        saveFileNameList.add(delFileName);
                        Log.w(TAG, "delProgramToPrmId save file: " + delFileName);
                    }
                }
                delFileNameList.removeAll(saveFileNameList);
                for (String del : delFileNameList) {
                    Log.e(TAG, "delProgramToPrmId Do delete file: " + del);
                }
                deleFile(delFileNameList);
                return true;
            }
        }
        return false;
    }

    /**
     * 删除素材
     *
     * @param delFileNameList
     */
    private void deleFile(List<String> delFileNameList) {
        for (String fileName : delFileNameList) {
            String suffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
            String folde = suffixToFolder.get(suffix);
            if (folde != null) {
                delPic(fileName);
            } else {
                delVideo(fileName);
            }
        }
    }

    /**
     * 删除所有文件
     *
     * @param path
     */
    public static void delete(String path) {
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

    /**
     * 删除空文件夹
     *
     * @param path 删除文件的路径
     */
    public static void deleteDirectory(String path) {
        File f1 = new File(path);
        f1.delete();
    }

    /**
     * 获取节目列表中所有的节目素材
     *
     * @return
     */
    private static List<String> getAllPrmFileInfo(Program program) {
        List<String> fileNameList = new ArrayList<String>();
        List<Area> areaList = program.getAs();
        if (null != areaList && areaList.size() > 0) {
            for (Area a : areaList) {
                List<Material> mas = a.getMas();
                if (null != mas && mas.size() > 0) {
                    for (Material material : mas) {
                        addFileName(fileNameList, material);
                    }
                }
            }
        }
        Material bgMaterial = program.getBgm();
        Material bgMusicMaterial = program.getBi();
        addFileName(fileNameList, bgMaterial);
        addFileName(fileNameList, bgMusicMaterial);
        return fileNameList;
    }

    private static void addFileName(List<String> fileNameList, Material material) {
        if (null != material) {
            String url = material.getU();
            if (!StringUtil.isNullStr(url)) {
                fileNameList.add(FileUtile.getFileName(url));
            }
        }
    }

    /**
     * 获取除要删除的节目列表之外的所有的素材名称
     */
    private List<String> getNotDelFileDownloadList(String fileName) {
        //首先要获取本地所有的节目列表中除当前要删除的节目列表除外
        List<String> fileNameList = new ArrayList<String>();
        File prmFolderFile = new File(getRoot() + PRM_FILE);
        if (!prmFolderFile.exists() || !prmFolderFile.isDirectory()) {
            return fileNameList;
        }
        File[] fileList = prmFolderFile.listFiles();
        for (File file : fileList) {
            String prmFileName = file.getName();
            if (!fileName.equals(prmFileName)) {
                String prmJson = readPrmToFile(file);
                Program program = JSON.parseObject(prmJson, Program.class);
                fileNameList.addAll(getAllPrmFileInfo(program));
            }
        }
        return fileNameList;
    }


    /**
     * 根据文件名删除 视频
     *
     * @param fileName 视频名称
     * @return 返回true表示删除成功, 反之为失败
     */
    private boolean delVideo(String fileName) {
        if (StringUtil.isNullStr(fileName)) {
            return false;
        }
        delFileToPath(getRoot() + VIDEO_FILE + File.separator + fileName);
        return false;
    }

    /**
     * 根据文件名删除图片
     *
     * @param fileName 图片名称
     * @return 返回true表示删除成功, 反之为失败
     */
    private boolean delPic(String fileName) {
        if (StringUtil.isNullStr(fileName)) {
            return false;
        }
        delFileToPath(getRoot() + PIC_FILE + File.separator + fileName);
        return false;
    }

    /**
     * 根据文件路径删除文件
     *
     * @param filePath 文件路径
     * @return 返回true表示删除成功, 反之为失败
     */
    private static boolean delFileToPath(String filePath) {
        if (StringUtil.isNullStr(filePath)) {
            return false;
        }
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }


    /**
     * 根据文件名读取文件内容返回string类型数据
     *
     * @param file 文件路径
     * @return 返回读取的数据, 如果file为空或者不存在则返回null
     */
    public static String readPrmToFile(File file) {
        if (null == file || !file.exists()) {
            return null;
        }
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));
            StringBuffer stringBuffer = new StringBuffer();
            String str = null;
            while ((str = bufferedReader.readLine()) != null) {
                stringBuffer.append(str);
            }
            bufferedReader.close();
            str = null;
            bufferedReader = null;
            return stringBuffer.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 将节目数据写到sd卡上
     *
     * @param prmFile    文件路径
     * @param prmContent 文件内容
     * @throws IOException
     */
    public static void writePrmContent(File prmFile, String prmContent) throws IOException {
        if (null != prmFile && null != prmContent) {
            FileWriter fileWriter = new FileWriter(prmFile);
            fileWriter.write(prmContent);
            fileWriter.flush();
            fileWriter.close();
        }
    }

    /**
     * 将一组节目列表写到等待下载的节目文件夹里面
     *
     * @param programList
     */
    public final void writePrmListToSd(final List<Program> programList) {
        Log.d(TAG, "writePrmListToSd...");
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                try {
                    Log.d(TAG, "writePrmListToSd run...");
                    for (Program program : programList) {
                        if (null == program) {
                            continue;
                        }
                        String programJson = JSON.toJSONString(program);
                        float promTotal = (ParsePrmUtil.getProgramFileTotal(program) / 1024f / 1024f);
                        DecimalFormat decimalFormat = new DecimalFormat("#.000");
                        String programName = program.getKey() + Constants.SPLIT_PATH + decimalFormat.format(promTotal);
                        Log.d(TAG, "writePrmListToSd programName: " + programName);
                        File file = new File(getWaitDownloadPrmFolder() + File.separator + programName);
                        Log.d(TAG, "writePrmListToSd file programName: " + file.getAbsolutePath());
                        if (!file.exists()) {
                            FileUtile.writePrmContent(file, programJson);
                        }
                        decimalFormat = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * 读取返回结果
     *
     * @param inputStream
     * @return 请求的返回值
     * @throws IOException
     */
    public static String getContentToInputStream(InputStream inputStream) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        String temp = null;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        while ((temp = bufferedReader.readLine()) != null) {
            stringBuilder.append(temp);
        }
        bufferedReader.close();
        inputStream.close();
        return stringBuilder.toString();
    }

    /**
     * copy文件到指定位置
     *
     * @param srcFile    源文件位置
     * @param targetFile 目的站位置
     * @return true表示成功
     */
    public boolean copyFileToFile(File srcFile, File targetFile) {
        boolean isSuccess = false;
        if (null == srcFile || null == targetFile) {
            return isSuccess;
        }

        if (srcFile.exists()) {
            if (!targetFile.getParentFile().exists()) {
                targetFile.getParentFile().mkdirs();
            }
            FileInputStream fileInputStream = null;
            FileOutputStream fileOutputStream = null;
            try {
                fileInputStream = new FileInputStream(srcFile);
                fileOutputStream = new FileOutputStream(targetFile);
                byte[] readData = new byte[1024 * 1024];
                int length = -1;
                while (((length = fileInputStream.read(readData)) != -1)) {
                    fileOutputStream.write(readData, 0, length);
                }
                isSuccess = true;
            } catch (IOException e) {
                if (targetFile.exists()) {
                    targetFile.delete();
                }
                e.printStackTrace();
            } finally {
                try {
                    if (null != fileInputStream) {
                        fileInputStream.close();
                    }
                    if (null != fileOutputStream) {
                        fileOutputStream.flush();
                        fileOutputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return isSuccess;
    }
}