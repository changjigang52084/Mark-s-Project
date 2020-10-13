package com.unccr.zclh.dsdps.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alibaba.fastjson.JSON;

import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.play.ProgramPlayManager;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午7:11:13
 * @parameter FileStore
 */
public class FileStore {

    private static final String TAG = "FileStore";

    private String sdcrad;

    /**
     * app根目录
     */
    public static final String ROOT_FOLDER = "zclh" + File.separator;
    /**
     * U盘根目录
     */
    public static String UDISK_ROOT_FOLDER = null;
    /**
     * 节目文件目录
     */
    public static final String PATH_PRM = ROOT_FOLDER + "prm";
    /**
     * 视频文件目录
     */
    public static final String PATH_VIDEO = ROOT_FOLDER + "video";
    /**
     * 图片文件目录
     */
    public static final String PATH_IMAGE = ROOT_FOLDER + "pic";
    /**
     * 截图文件目录
     */
    public static final String PATH_SHOT = ROOT_FOLDER + "shot";
    /**
     * APK文件目录
     */
    public static final String PATH_APK = ROOT_FOLDER + ".apk";
    /**
     * 临时文件目录
     */
    public static final String PATH_TEMP = ROOT_FOLDER + ".temp";
    /**
     * 日志目录
     */
    public static final String PATH_LOG = ROOT_FOLDER + "log";


    public static final String PATH_SCALED = ROOT_FOLDER + "scaled";
    /**
     * 节目文件后缀
     */
    public static final String PRM_SUFFIX_NAME = ".prm";
    private static volatile FileStore fileStore;

    public FileStore() {
    }

    public static FileStore getInstance() {
        if (null == fileStore) {
            synchronized (FileStore.class) {
                if (null == fileStore) {
                    fileStore = new FileStore();
                }
            }
        }
        return fileStore;
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
                    File floderFile = new File(PATH_PRM);
                    if (!floderFile.exists()) {
                        floderFile.mkdir();
                    }
                    File localPrmFile = new File(floderFile, fileName + ".temp");
                    if (localPrmFile.exists()) {
                        return;
                    }

                    File prmFile = new File(floderFile, fileName + ".temp");
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
     * @return the sdcrad
     */
    public String getSdCard() {
        if (TextUtils.isEmpty(sdcrad)) {
            sdcrad = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return sdcrad;
    }

    /**
     * @param sdcrad the sdcrad to set
     */
    public void setSdCard(String sdcrad) {
        if (TextUtils.isEmpty(sdcrad)) {
            this.sdcrad = Environment.getExternalStorageDirectory().getAbsolutePath();
        } else {
            this.sdcrad = sdcrad;
        }
//		SharedUtil.newInstance().setString(SharedUtil.KEY_SD_CARD_KEY, this.sdcrad);
    }

    /**
     * 获取截图文件目录路劲
     *
     * @param
     * @return 截图文件目录路劲
     */
    public String getShotFolder() {
        return getSdCard() + File.separator + PATH_SHOT;
    }

    /**
     * 获取日志文件目录路径
     *
     * @return 日志文件目录路径
     */
    public String getLogFolderPath() {
        return getSdCard() + File.separator + PATH_LOG;
    }


    /**
     * 获取一般布局文件存放路径
     *
     * @return 布局文件存放路径
     */
    public String getLayoutFolderPath() {
        String path;
        if (ConfigSettings.UDISK_MODE) {
            path = FileStore.UDISK_ROOT_FOLDER + File.separator + PATH_PRM;
            Log.d(TAG, "UDISK_MODE path: " + path);
        } else {
            path = getSdCard() + File.separator + PATH_PRM;
            Log.d(TAG, "getLayoutFolderPath path: " + path);
        }
        File folder = new File(path);
        if (!folder.exists()) {
            folder.mkdirs();
            Log.d(TAG, "getLayoutFolderPath create folder.");
        }
        return path;
    }

    /**
     * 获取图片文件存放路径
     *
     * @return 图片文件存放路径
     */
    public String getImageFolderPath() {
        String path;
        if (ConfigSettings.UDISK_MODE) { // u盘升级模式
            path = FileStore.UDISK_ROOT_FOLDER + File.separator + PATH_IMAGE;
        } else {
            path = getSdCard() + File.separator + PATH_IMAGE;
        }
        File folder = new File(path); // 创建文件夹
        folder.mkdirs();
        return path;
    }
    /**
     * 获取图片文件存放路径（电梯）
     *
     * @return 图片文件存放路径
     */
    public String getImagePrmFolderPath() {
        String prmPicFolder = FileUtil.getInstance().getPrmPicFolder();
        File folder = new File(prmPicFolder); // 创建文件夹
        folder.mkdirs();
        return prmPicFolder;
    }
    /**
     * 获取视频文件存放路径
     *
     * @return 视频文件存放路径
     */
    public String getVideoFolderPath() {
        String path;
        if (ConfigSettings.UDISK_MODE) {
            path = FileStore.UDISK_ROOT_FOLDER + File.separator + PATH_VIDEO;
        } else {
            path = getSdCard() + File.separator + PATH_VIDEO;
        }
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    public String getVideoPrmFolderPath() {
        String prmVideoFolder = FileUtil.getInstance().getVideoPrmFolder();
        File folder = new File(prmVideoFolder);
        folder.mkdirs();
        return prmVideoFolder;
    }

    public String getScaledImageFolderPath() {
        String path;
        if (ConfigSettings.UDISK_MODE) {
            path = FileStore.UDISK_ROOT_FOLDER + File.separator + PATH_SCALED;
        } else {
            path = getSdCard() + File.separator + PATH_SCALED;
        }
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    /**
     * 获取图片文件全路径
     *
     * @param fileName 图片文件名
     * @return 文件全路径
     */
    public String getImageFilePath(String fileName) {
        return getImageFolderPath() + File.separator + fileName;
    }

    /**
     * 获取视频文件全路径
     *
     * @param fileName 视频文件名
     * @return 文件全路径
     */
    public String getVideoFilePath(String fileName) {
        return getVideoFolderPath() + File.separator + fileName;
    }

    /**
     * 获取布局文件
     */
    public List<String> getLayoutFilesName() {
        File layoutfile = new File(getLayoutFolderPath());
        File[] layoutfiles = layoutfile.listFiles();
        return getLayoutFileName(layoutfiles);
    }


    /**
     * 获取节目文件目录下的节目文件名列表
     *
     * @param
     * @return
     */
    private static List<String> getLayoutFileName(File[] layoutfiles) {
        List<String> layoutfilename = new ArrayList<String>();
        if (layoutfiles != null) {
            for (File file : layoutfiles) {
                if (file.isDirectory()) {
                    getLayoutFileName(file.listFiles());
                } else {
                    String filename = file.getName();
                    layoutfilename.add(filename);
                }
            }
        } else {
            layoutfilename = null;
        }
        return layoutfilename;
    }


    /**
     * 判断节目列表文件是否存在
     *
     * @return true表示存在
     */
    @SuppressWarnings("deprecation")
    public static String checkProgramListIsExist() {
//		List<PrmBean> prmBeans = ProgramParseTools.getPrmBeans();
//		if (null == prmBeans) {
//			return null;
//		}
//		int size = prmBeans.size();
//		for (int i = 0 ;i < size; i++) {
//			PrmBean prmBean = prmBeans.get(i);
//			boolean ifOverdue = ProgramParseTools.checkProgrameIfOverdue(prmBean);
//			if (ifOverdue) {
//				return StringUtil.getString(R.string.program_expired_end_time)+
//							DateFormat.getDateInstance().format(new Date(prmBean.getLet()));
//			}
//		}
        return null;
    }


    /**
     * 判断文件是否存在
     *
     * @param fileName 文件的名称
     * @param fileType 文件的类型
     * @return 返回true表示存在，反之不存在
     */
    public static boolean fileExists(String fileName, String fileType) {
//		String filePath = null;
//		if ("v".equals(fileType)) {//视频
//			filePath = getVideoFilePath(fileName);
//		} else if ("i".equals(fileType)) {//图片
//			filePath =  getImageFilePath(fileName);
//		}
//		File file = new File(filePath);
//		if (file.exists()) {
//			return true;
//		}
//		PrmInfoBean prmInfoBean = ProgramPlayManager.newInstance().getCurrentPrmInfoBean();
//		if (null != prmInfoBean) {//获取当前正在播放的节目单
//			String prmName = prmInfoBean.getPname();
////			FileStore.wrietPlayErroLog(prmName+"_"+fileName);
//			LogManager.get().writeErrorLog(LogManager.get()
//					.setErrorLog(StringUtil.getString(R.string.app_name),
//							"prmgram file not exists, program name:"+prmName+",fileName:"+fileName));
//		}
        return false;
    }

    /**
     * return the path(temp folder) that points to the scaled picture
     *
     * @param fileName the original filename
     * @param scale    the scale of the picture
     * @return the path to the scaled picture. ex(filename.jpg.SCALED.2)
     */
    public String getPathForPictureScaled(String fileName, int scale) {
        String filePath = getScaledImageFolderPath() + File.separator + fileName
                + ".SCALED." + scale;
        return filePath;
    }

    /**
     * stores a scaled image in temp folder
     *
     * @param bitmap   the scaled bitmap
     * @param fileName the original filename
     * @param scale    the scale at which the image was scaled
     */
    public void storeScaledImage(Bitmap bitmap, String fileName,
                                 int scale) {
        long l1 = DateTimeUtil.uptimeMillis();
        String filePath = getPathForPictureScaled(fileName, scale);
        File imageFile = null;
        OutputStream out = null;
        try {
            imageFile = new File(filePath);
            out = new FileOutputStream(imageFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 70, out);
        } catch (FileNotFoundException e) {
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
            }
        }
        long l2 = DateTimeUtil.uptimeMillis();
    }

    /**
     * 检验素材的合法性
     *
     * @param filePath 素材文件的路径
     * @param material 素材对象
     * @return 合法则返回true，反之返回false
     */
    public boolean checkFileLegal(String filePath, Material material) {
        if (null == material) {
            return false;
        }
        File materialFile = new File(filePath);
        long fileLength = materialFile.length();
        if (fileLength == material.getS()) {
            return true;
        }
        materialFile.delete();//删除不合法的素材
        Helper.handlerMaterialError(material, ProgramPlayManager.getInstance().getCurrentProgram());//自动修复被删除的素材
        return false;
    }

    /**
     * 删除文件
     *
     * @param path
     */
    public static void delete(final String path) {
        ThreadPoolManager.get().addRunnable((new Runnable() {
            @Override
            public void run() {
                File f1 = new File(path);
                String delMsg = "Eposter (delete) Del file path : " + path + "_" +
                        DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss");
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
        }));
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
     * 根据http地址获取文件名
     *
     * @param httpUrl 下载地址
     * @return 文件名
     */
    public static String getFileName(String httpUrl) {
        if (null == httpUrl) {
            return httpUrl;
        }
        //http://7xq81q.com1.z0.glb.clouddn.com/1453285422797-test.mp4?sdfsdfasdflkajsldfjlasdjflasjdf=S>?AWekrwer
        String fileName = httpUrl.substring(httpUrl.lastIndexOf("/") + 1, httpUrl.length());
//		String fileName = httpUrl.substring(httpUrl.lastIndexOf("/")+1, httpUrl.lastIndexOf("?"));
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
        Log.d(TAG, "fileName:" + fileName);
        return fileName;
    }

    public void ergodicFolderAndCopy(File folderFile, final String methodName) {
        final List<File> fileList = new ArrayList<File>();
        getFiles(folderFile, fileList);
        // 拷贝节目
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                for (File srcFile : fileList) {
                    if (srcFile.exists()) {
                        File targetFile = getTargetFileToSrcFile(srcFile);
                        if (null != targetFile) {
                            copyFileToFile(srcFile, targetFile);
                        }
                        Log.d(TAG, "ergodicFolderAndCopy srcFile: " + srcFile.getAbsolutePath() + ", targetFile: " + targetFile.getAbsolutePath());
                    }
                }
                Looper.prepare();
                Toast.makeText(DsdpsApp.getDsdpsApp(), "节目拷贝完成", Toast.LENGTH_SHORT).show();
                Looper.loop();
                Intent tipMsgIntent = new Intent(PlayActivity.UPDATE_MSG_TIP_MSG_ACTION);
                tipMsgIntent.putExtra(PlayActivity.IS_COPY_PROGRAM_KEY, true);
                tipMsgIntent.putExtra(PlayActivity.TIP_MSG_KEY, StringUtil.getString(R.string.tip_udisk_copy_over));
                tipMsgIntent.putExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, true);
                LocalBroadcastManager.getInstance(DsdpsApp.getDsdpsApp().getApplicationContext()).sendBroadcast(tipMsgIntent);
            }
        });
    }

    private static void getFiles(File folderFile, List<File> fileList) {
        if (folderFile.isDirectory()) {
            File[] files = folderFile.listFiles();
            for (File file : files) {
                getFiles(file, fileList);
            }
        } else {
            fileList.add(folderFile);
        }
    }


    private File getTargetFileToSrcFile(File srcFile) {
        File targetFile = null;
        String targetFolder = null;
        if (srcFile.getAbsolutePath().contains(PATH_IMAGE)) {
            targetFolder = getSdCard() + File.separator + PATH_IMAGE;
            Log.d(TAG,"getTargetFileToSrcFile targetFolder: " + targetFolder);
        } else if (srcFile.getAbsolutePath().contains(PATH_PRM)) {
            targetFolder = getSdCard() + File.separator + PATH_PRM;
            Log.d(TAG,"getTargetFileToSrcFile targetFolder: " + targetFolder);
        } else if (srcFile.getAbsolutePath().contains(PATH_VIDEO)) {
            targetFolder = getSdCard() + File.separator + PATH_VIDEO;
            Log.d(TAG,"getTargetFileToSrcFile targetFolder: " + targetFolder);
        }
//		if (srcFile.getAbsolutePath().contains(getImageFolderPath())) {
//			targetFolder = getSdcrad() + File.separator + PATH_IMAGE;
//		} else if (srcFile.getAbsolutePath().contains(getLayoutFolderPath())) {
//			targetFolder = getSdcrad() + File.separator + PATH_PRM;
//		} else if (srcFile.getAbsolutePath().contains(getVideoFolderPath())) {
//			targetFolder = getSdcrad() + File.separator + PATH_VIDEO;
//		}
        if (!StringUtil.isNullStr(targetFolder)) {
            targetFile = new File(targetFolder + File.separator + srcFile.getName());
        }
        return targetFile;
    }

    /**
     * copy文件到指定位置
     *
     * @param srcFile    源文件位置
     * @param targetFile 目的站位置
     * @return true表示成功
     */
    public static boolean copyFileToFile(File srcFile, File targetFile) {
        boolean isSuccess = false;
        if (null == srcFile || null == targetFile) {
            return isSuccess;
        }
        Log.d(TAG, "copyFileToFile111111111111");
        if (srcFile.exists()) {
            if (!targetFile.getParentFile().exists()) {
                Log.d(TAG, "copyFileToFile22222222222");
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

    /**
     * 汇报当前连续闪退的错误日志
     *
     * @param errMsg
     * @return
     */
    public boolean createUncaughtLog(String errMsg) {
        boolean isSuccess = false;
        String uncaughtFileName = "";
        File file = new File(getLogFolderPath(), uncaughtFileName);
        try {
            FileWriter fileOutputStream = new FileWriter(file);
            fileOutputStream.write(errMsg);
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
}
