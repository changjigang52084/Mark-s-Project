package com.xunixianshi.vrshow.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.FileDownloaderModel;
import com.hch.utils.OkhttpConstant;
import com.hch.viewlib.util.ViewlibConstant;
import com.xunixianshi.vrshow.my.myDownLoad.DownLoadItem;
import com.xunixianshi.vrshow.obj.ApkInfo;

import java.util.ArrayList;
import java.util.List;
/**
 * 搜索文件工具类
 * @ClassName SearchFileUtil
 *@author HeChuang
 *@time 2016/11/1 15:50
 */
public class SearchFileUtil {
    private static int INSTALLED = 0; // 表示已经安装，且跟现在这个apk文件是一个版本
    private static int UNINSTALLED = 1; // 表示未安装
    private static int INSTALLED_UPDATE = 2; // 表示已经安装，版本比现在这个版本要低，可以点击按钮更新

    private static SearchFileUtil searchFileUtil;

    private List<ApkInfo> allInstallApkList;
    private Context context;

    // Media.DURATION 时间长度
    private final String[] VIDEO_COLUMN = {
            MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA,
            MediaStore.Video.Media.SIZE, MediaStore.Audio.Media.DURATION};

    public static SearchFileUtil getInstance(Context context) {
        if (searchFileUtil == null) {
            searchFileUtil = new SearchFileUtil(context);
        }
        return searchFileUtil;
    }

    public SearchFileUtil(Context context) {
        this.context = context;
        // + "/VrShow/Game";
    }

    /**
     * @param @param  downType
     * @param @return 设定文件
     * @return List<DownLoadItem> 返回类型
     * @throws ， unknown 未知， game 游戏，video 视频 注意：由于加入下载列表实在非UI线程添加，不是实时
     *                           如果刚加入就需要查看，需要给 10ms等待时间
     * @Title: getDownLoadModeList
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     */
    public ArrayList<DownLoadItem> getDownLoadModeList(String downType) {

        ArrayList<DownLoadItem> downloads = new ArrayList<>();
        List<FileDownloaderModel> fileDownloaderModels = DownloaderManager
                .getInstance().getAllTask();
        if (fileDownloaderModels != null) {
            for (int i = 0; i < fileDownloaderModels.size(); i++) {
                FileDownloaderModel fileDownloaderModel = fileDownloaderModels
                        .get(i);
                String type = fileDownloaderModel
                        .getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_TYPE);
                if (type.equals(downType)) {
                    DownLoadItem downloaditem = new DownLoadItem();
                    downloaditem.setDownLoadType(downType);
                    downloaditem.setDownLoadUrl(fileDownloaderModel.getUrl());
                    downloaditem.setFilePath(fileDownloaderModel.getPath());

                    downloaditem
                            .setDownLoadName(fileDownloaderModel
                                    .getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_NAME));
                    downloaditem
                            .setDownLoadIconUrl(fileDownloaderModel
                                    .getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_ICON_URL));
                    downloaditem
                            .setResourcesId(fileDownloaderModel
                                    .getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_MARK_ID));
                    downloaditem
                            .setPackageName(fileDownloaderModel
                                    .getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_APP_PACKAGE_NAME));

                    downloaditem
                            .setFileSize(fileDownloaderModel
                                    .getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_FILE_SIZE));

                    downloaditem.setVideoType(fileDownloaderModel.getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_VIDEO_TYPE));

                    downloaditem.setId(fileDownloaderModel.getId());
                    downloads.add(downloaditem);
                }
            }
        }
        return downloads;
    }

    public int  getDownLoadModeSize(String downType){
        int size = 0;
        List<FileDownloaderModel> fileDownloaderModels = DownloaderManager
                .getInstance().getAllTask();
        if (fileDownloaderModels != null) {
            for (int i = 0; i < fileDownloaderModels.size(); i++) {
                FileDownloaderModel fileDownloaderModel = fileDownloaderModels
                        .get(i);
                String type = fileDownloaderModel
                        .getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_TYPE);
                if (type.equals(downType)) {
                    size ++;
                }
            }
        }
        return size;
    }

    /**
     * @param @param  isRefresh
     * @param @return 设定文件
     * @return List<ApkInfo> 返回类型
     * @throws 获取应用所以安装的应用
     * @Title: getMobileInstallsApps
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     */
    public List<ApkInfo> getMobileInstallsApps(boolean isRefresh) {
        if (this.allInstallApkList == null || isRefresh) {
            List<ApkInfo> installApkList = new ArrayList<>();
            final PackageManager packageManager = context.getPackageManager();
            // 获取所有已安装程序的包信息
            List<PackageInfo> packageInfos = packageManager
                    .getInstalledPackages(0);
            // 用于存储所有已安装程序的包名
            if (packageInfos != null) {
                for (int i = 0; i < packageInfos.size(); i++) {
                    ApplicationInfo applicationInfo = packageInfos.get(i).applicationInfo;
                    if (applicationInfo != null
                            && (applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                        ApkInfo installApkJson = new ApkInfo();
                        installApkJson
                                .setPackageName(applicationInfo.packageName);
                        installApkJson
                                .setVersionCode(packageInfos.get(i).versionCode);
                        installApkJson
                                .setName(packageInfos.get(i).applicationInfo.name);
                        installApkList.add(installApkJson);
                    }
                }
            }
            this.allInstallApkList = installApkList;
        }
        return this.allInstallApkList;
    }

    /**
     * @param @param  packageName
     * @param @return 设定文件
     * @return ApkInfo 返回类型
     * @throws 获取手机安装同样包名的应用 补充：后面再新建一个统一缓存数据类
     * @Title: getProductIsInstallMobile
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     */
    public ApkInfo getProductIsInstallMobile(String packageName) {
        if (StringUtil.isBlank(packageName)) {
            return null;
        }

        List<ApkInfo> installApkList = this.getMobileInstallsApps(false);
        for (int i = 0; i < installApkList.size(); i++) {
            ApkInfo installApkJson = installApkList.get(i);
            if (packageName.equals(installApkJson.getPackageName())) {
                return installApkJson;
            }
        }
        return null;
    }

    // /// 获取视频文件缩略图
    public static Bitmap getVideoThumbnail(String videoPath, int width,
                                           int height, int kind) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
                ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }

    /**
     * 091 * 判断该应用在手机中的安装情况
     * <p/>
     * 092 * @param pm PackageManager
     * <p/>
     * 093 * @param packageName 要判断应用的包名
     * <p/>
     * 094 * @param versionCode 要判断应用的版本号
     * <p/>
     * 095
     */
    public int doType(PackageManager pm, String packageName, int versionCode) {
        List<PackageInfo> pakageinfos = pm
                .getInstalledPackages(PackageManager.GET_UNINSTALLED_PACKAGES);
        for (PackageInfo pi : pakageinfos) {
            String pi_packageName = pi.packageName;
            int pi_versionCode = pi.versionCode;
            // 如果这个包名在系统已经安装过的应用中存在
            if (packageName.endsWith(pi_packageName)) {
                // Log.i("test","此应用安装过了");
                if (versionCode == pi_versionCode) {
                    return INSTALLED;
                } else if (versionCode > pi_versionCode) {
                    return INSTALLED_UPDATE;
                }
            }
        }
        return UNINSTALLED;
    }

    /**
     * 获取存储路径
     *
     * @return
     */
    public static String getSavePath() {
        if (AppContent.isInternalStorage) {
            return AppContent.InternalStorage;
        } else {
            return AppContent.SDCardStorage;
        }
    }
}
