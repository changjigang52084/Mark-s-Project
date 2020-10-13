package com.lzkj.ui.util;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.log.LogBuilder;
import com.lzkj.ui.log.LogManager;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

public class FileStore {
    private static final LogTag LOG_TAG = LogUtils.getLogTag(FileStore.class.getSimpleName(), true);

    private String sdcrad;

    /**
     * app根目录
     */
    public static final String ROOT_FOLDER = "mallposter" + File.separator;
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
     * @return the sdcrad
     */
    public String getSdcrad() {
//		sdcrad = SharedUtil.newInstance().getString(SharedUtil.KEY_SD_CARD_KEY);
        if (TextUtils.isEmpty(sdcrad)) {
            sdcrad = Environment.getExternalStorageDirectory().getAbsolutePath();
        }
        return sdcrad;
    }

    /**
     * @param sdcrad the sdcrad to set
     */
    public void setSdcrad(String sdcrad) {
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
        // /mnt/internal_sd/mallposter/shot
        return getSdcrad() + File.separator + PATH_SHOT;
    }

    /**
     * 获取日志文件目录路径
     *
     * @return 日志文件目录路径
     */
    public String getLogFolderPath() {
        // /mnt/internal_sd/log
        return getSdcrad() + File.separator + PATH_LOG;
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
        } else {
            // 节目文件存放路径 /mnt/internal_sd/mallposter/prm
            path = getSdcrad() + File.separator + PATH_PRM;
        }
        File folder = new File(path);
        folder.mkdirs();
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
            // sd卡升级模式  /mnt/internal_sd/mallposter/pic
            path = getSdcrad() + File.separator + PATH_IMAGE;
        }
        File folder = new File(path); // 创建文件夹
        folder.mkdirs();
        return path;
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
            path = getSdcrad() + File.separator + PATH_VIDEO;
        }
        File folder = new File(path);
        folder.mkdirs();
        return path;
    }

    public String getScaledImageFolderPath() {
        String path;
        if (ConfigSettings.UDISK_MODE) {
            path = FileStore.UDISK_ROOT_FOLDER + File.separator + PATH_SCALED;
        } else {
            path = getSdcrad() + File.separator + PATH_SCALED;
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
        // mnt/internal_sd/mallposter/pic/filename
        LogUtils.d(LOG_TAG, "getImageFilePath", "getImageFolderPath() + File.separator + fileName ：" + getImageFolderPath() + File.separator + fileName);
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
            bitmap.compress(CompressFormat.JPEG, 70, out);
        } catch (FileNotFoundException e) {
            LogUtils.e(LOG_TAG, "storeScaledImage", e);
        } finally {
            try {
                if (out != null) {
                    out.flush();
                    out.close();
                }
            } catch (IOException e) {
                LogUtils.e(LOG_TAG, "storeScaledImage", e);
            }
        }
        long l2 = DateTimeUtil.uptimeMillis();
        LogUtils.timeConsume(LOG_TAG, "store scaled bitmap: " + filePath, l2
                - l1);
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
        LogUtils.d(LOG_TAG, "checkFileLegal", "filePath : " + filePath
                + ", fileLength: " + fileLength + ",material file name:"
                + getImageFilePath(FileStore.getFileName(material.getU())) + ", material file length:" + material.getS());
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
                LogUtils.d(LOG_TAG, "delete", "fileList is null");
                LogManager.get().insertOperationMessage(delMsg);
                if (f1.isDirectory()) {
                    String[] fileList = f1.list();
                    if (null == fileList) {
                        LogUtils.d(LOG_TAG, "delete", "fileList is null");
                        return;
                    }
                    for (String s : fileList) {
                        File f2 = new File(f1, s);
                        if (f2.isDirectory()) {
                            delete(f2.getAbsolutePath());
                        } else {
                            f2.delete();
                            LogUtils.d(LOG_TAG, "delete", s);
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
        Log.v("cjg", "fileName:" + fileName);
        return fileName;
    }

    public void ergodicFolderAndCopy(File folderFile, final String methodName) {
        final List<File> fileList = new ArrayList<File>();
        getFiles(folderFile, fileList);
        LogManager.get().insertOperationMessage("Eposter  ergodicFolderAndCopy methodName : "
                + methodName + ",folderFile : " + folderFile + "_" + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                for (File srcFile : fileList) {
                    if (srcFile.exists()) {
                        File targetFile = getTargetFileToSrcFile(srcFile);
                        if (null != targetFile) {
                            copyFileToFile(srcFile, targetFile);
                            LogManager.get().insertOperationMessage("Eposter  ergodicFolderAndCopy methodName : " + methodName
                                    + "_srcFile: " + srcFile.getAbsolutePath()
                                    + ", targetFile: " + targetFile.getAbsolutePath() + "_"
                                    + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));
                        }
                    }
//					LogUtils.d(LOG_TAG, "ergodicFolderAndCopy", "srcFile: " + srcFile.getAbsolutePath() 
//							+ ", targetFile: " + targetFile.getAbsolutePath());
                }
                Intent tipMsgIntent = new Intent(PlayActivity.UPDATE_MSG_TIP_MSG_ACTION);
                tipMsgIntent.putExtra(PlayActivity.TIP_MSG_KEY,
                        StringUtil.getString(R.string.tip_udisk_copy_over));
                tipMsgIntent.putExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, true);
                tipMsgIntent.putExtra(PlayActivity.IS_COPY_PROGRAM_KEY, true);
                LocalBroadcastManager.getInstance(EPosterApp
                        .getApplication()
                        .getApplicationContext())
                        .sendBroadcast(tipMsgIntent);
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
            targetFolder = getSdcrad() + File.separator + PATH_IMAGE;
        } else if (srcFile.getAbsolutePath().contains(PATH_PRM)) {
            targetFolder = getSdcrad() + File.separator + PATH_PRM;
        } else if (srcFile.getAbsolutePath().contains(PATH_VIDEO)) {
            targetFolder = getSdcrad() + File.separator + PATH_VIDEO;
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

    /**
     * 汇报当前连续闪退的错误日志
     *
     * @param errMsg
     * @return
     */
    public boolean createUncaughtLog(String errMsg) {
        boolean isSuccess = false;
        String uncaughtFileName = LogBuilder.LOG_PREFIX + "_" + LogBuilder.LogType.UNCAUGHT_LOG + "_"
                + DateTimeUtil.getStringTimeToFormat("yyyy_MM_dd_HH")
                + "_" + ConfigSettings.getDid() + "." + LogBuilder.LOG_SUFFIX;
        ;
        File file = new File(getLogFolderPath(), uncaughtFileName);
        try {
            FileWriter fileOutputStream = new FileWriter(file);
            fileOutputStream.write(errMsg);
            fileOutputStream.flush();
            fileOutputStream.close();
            isSuccess = true;
            LogBuilder.sendUploadLogReceiver(file.getAbsolutePath());
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

}
