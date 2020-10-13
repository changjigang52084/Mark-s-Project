package com.lzkj.downloadservice.impl;

import com.lzkj.downloadservice.download.HttpDownloadTask;
import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;
import com.lzkj.downloadservice.qiniu.impl.QiNiuDownloadTokenCallback;
import com.lzkj.downloadservice.util.AppUtil;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.util.ShreadUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年6月17日 上午11:05:47
 * @parameter 下载app的类
 */
public class DownloadAppImpl implements IDownloadStateCallback, QiNiuDownloadTokenCallback {
    private static final LogTag TAG = LogUtils.getLogTag(DownloadAppImpl.class.getSimpleName(), true);

    /**
     * 下载一组app
     *
     * @param appUrlList
     */
    public void downloadApps(List<String> appUrlList) {

        List<String> downloadUrlList = new ArrayList<String>();
        for (String appUrl : appUrlList) {
            String downloadUrl = appUrl.split(Constant.SPLIT_APP_PATH)[0];
            String appMd5 = appUrl.split(Constant.SPLIT_APP_PATH)[1];
            String appFileName = FileUtil.getFileName(downloadUrl);
            ShreadUtil.newInstance().putString(appFileName, appMd5);
            downloadUrlList.add(downloadUrl);
            // Method: onDownloadList, Message: key : lhA51CYlU-A_8id-IQPjJ5aud9OY ,downloadUrl : http://oqsrrgfyy.bkt.clouddn.com/Launcher1.0.57.20181027.yw.beta.apk ,fileName : Launcher1.0.57.20181027.yw.beta.apk
            LogUtils.d(TAG, "onDownloadList", "key : " + appMd5 + " ,downloadUrl : " + downloadUrl + " ,fileName : " + appFileName);
        }
        excuseDownload(downloadUrlList);
    }

    /**
     * 执行下载
     *
     * @param downloadList
     */
    private void excuseDownload(List<String> downloadList) {
        HttpDownloadTask httpDownloadFile = new HttpDownloadTask();
        httpDownloadFile.addDownloadListTask(downloadList, this, FileUtil.getInstance().getAPPFolder(), Constant.DOWNLOAD_APP);
        httpDownloadFile.execu();
    }

    @Override
    public void downloadTokenList(List<String> list, int type, String downloadFolder) {
    }

    @Override
    public void onSuccess(String httpUrl, int totalSize, int downloadType) {
        String filename = FileUtil.getFileName(httpUrl);
        if (downloadType == Constant.DOWNLOAD_APP) {//下载App
            String appPath = FileUtil.getInstance().getAPPFolder() + File.separator + filename;
            File file = new File(appPath);
            LogUtils.d(TAG, "cjg", "file:" + file.getAbsolutePath());
            if (file.exists()) {
                AppUtil.installApkToAppPath(appPath);
            }
        }
    }

    @Override
    public void onFail(String httpUrl, String errMsg, int downloadType) {
        LogUtils.e(TAG,"cjg","errMsg:"+errMsg);
    }

    @Override
    public void updateProgreass(int progress, int totalSize, String httpUrl,
                                int downloadType) {
        LogUtils.e(TAG,"cjg","progress:"+progress);
    }

    @Override
    public void onStart(int progress, int totalSize, String httpUrl,
                        int downloadType) {
        LogUtils.e(TAG,"cjg","start:");
    }

    @Override
    public void onCancel(String httpUrl, int downloadType) {
        LogUtils.e(TAG,"cjg","cancel:");
    }
}
