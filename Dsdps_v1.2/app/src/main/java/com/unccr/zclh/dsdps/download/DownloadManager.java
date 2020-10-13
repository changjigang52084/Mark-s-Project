package com.unccr.zclh.dsdps.download;


import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.ProgramParseTools;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 下载管理类
 * @author changjigang
 *
 */
public class DownloadManager {

    /**保存所有的下载任务*/
    private List<HttpDownloadTask> downloadList = new ArrayList<HttpDownloadTask>();
    private static volatile DownloadManager downloadManager = null;
    private int downloadCount = 0;
    private DownloadManager() {}
    public static DownloadManager newInstance() {
        if (null == downloadManager) {
            synchronized (DownloadManager.class) {
                if (null == downloadManager) {
                    downloadManager = new DownloadManager();
                }
            }
        }
        return downloadManager;
    }

    /**
     * 取消所有的下载任务
     */
    public void cancelAllTask() {
        for (HttpDownloadTask httpDownload : downloadList) {
            httpDownload.cancel();
        }
        downloadList.clear();
    }
    /**
     * 添加下载任务
     * @param httpDownload
     * 		httpd的下载对象
     */
    public void addHttpDownload(HttpDownloadTask httpDownload) {
        if (null != httpDownload) {
            downloadList.add(httpDownload);
        }
    }
    /**
     * 根据http地址文件取消下载任务
     * @param httpUrl
     * 		http地址
     */
    public void cancelTaskToUrl(String httpUrl) {
        if (!TextUtils.isEmpty(httpUrl)) {
            for (HttpDownloadTask download : downloadList) {
                if (download.ifExistToUrl(httpUrl)) {
                    download.cancel();
                    downloadList.remove(download);
                    return;
                }
            }
        }
    }

    /**
     * 取消下载素材列表
     *
     * @param cancelDownloadList 取消下载素材列表<素材名称>
     */
    public void cancelDownloadList(ArrayList<String> cancelDownloadList) {
        Intent cancelDownloadListIntent = new Intent(Constants.CANCEL_DOWNLOAD_ACTION);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.CANCEL_DOWNLOAD_FILE_LIST, cancelDownloadList);
        cancelDownloadListIntent.putExtras(bundle);
        DsdpsApp.getDsdpsApp().sendBroadcast(cancelDownloadListIntent);
    }


    /**
     * 根据文件名文件取消下载任务
     * 			文件名
     * @return
     * 		返回是否执行成功，true表示成功，false表示不成功
     */
//	public boolean cancelTaskToFileName(String fileName) {
//		boolean isSuccess = false;
//		/***被取消的下载任务**/
//		LogUtils.d(TAG, "cancelTaskToFileName", "fileName:" + fileName);
//		if (!TextUtils.isEmpty(fileName)) {
//			for (HttpDownloadTask download : downloadList) {
//				List<String> downloadList = download.getDownloadList();
//				if (null == downloadList || downloadList.isEmpty()) {
//					LogUtils.d(TAG, "cancelTaskToFileName", "downloadList is empty");
//					continue;
//				}
//				for (String httpUrl : downloadList) {
//					String downloadFileName = FileUtil.getFileName(httpUrl);
//					LogUtils.d(TAG, "cancelTaskToFileName", "downloadFileName:"+downloadFileName+",fileName:"+fileName);
//					if (downloadFileName.equals(fileName)) {
//						List<String> fileNames = new ArrayList<String>();
//						fileNames.add(fileName);
//						download.cancelListTask(fileNames);
//						isSuccess = true;
//						continue;
//					}
//				}
//				if (isSuccess) {
//					continue;
//				}
//			}
//			delFile(fileName);
//		}
//		return isSuccess;
//	}


    public boolean cancelTaskToFileNames(List<String> fileNames) {
        boolean isSuccess = false;
        /***被取消的下载任务**/
        if (null != fileNames && !fileNames.isEmpty()) {
            for (HttpDownloadTask download : downloadList) {
                download.cancelListTask(fileNames);
            }
//			for (String fileName : fileNames) {
//				delFile(fileName);
//			}
            isSuccess = true;
        }
        return isSuccess;
    }

    /**
     * 删除文件
     * @param fileName
     * 			文件名
     */
    private void delFile(String fileName) {
        if (!checkFileIsNotSureDel(fileName)) {
            String fileNameSuffix = fileName.substring(fileName.lastIndexOf(".") + 1, fileName.length()).toLowerCase();
            String filePath = FileUtil.getInstance().suffixToFolder.get(fileNameSuffix);
            if (TextUtils.isEmpty(filePath)) {
                filePath = FileUtil.getInstance().getVideoFolderPath() ;
            }
            filePath = filePath + File.separator + fileName;
            File file = new File(filePath);
            if (file.exists()) {
                file.delete();
            }
        }
    }

    /**
     * 校验传入的文件是否不可以删除
     * @param fileName
     * 			要验证的文件名
     * @return
     * 		返回true表示不可以删除，false表示能删除
     */
    private boolean checkFileIsNotSureDel(String fileName) {
        return ProgramParseTools.getAllMaterialName().contains(fileName);
    }

}

