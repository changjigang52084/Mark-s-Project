package com.lzkj.downloadservice.download;

import android.text.TextUtils;

import com.lzkj.downloadservice.bean.DownloadListBo;
import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;
import com.lzkj.downloadservice.interfaces.IHttpDownloadStateCallback;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * http下载的类
 *
 * @author changkai
 */
public class HttpDownloadTask implements IHttpDownloadStateCallback {
    private static final LogTag TAG = LogUtils.getLogTag(HttpDownloadTask.class.getSimpleName(), true);

    private String cacheHttpUrl = null;
    private IDownloadStateCallback downloadProgressListener;
    private CopyOnWriteArrayList<String> downloadList;
    private CopyOnWriteArrayList<DownloadListBo> downloadListBos;
    //	private static  HttpDownload httpDownload;
    private HttpDownloadThread downloadThread;
    private CopyOnWriteArrayList<HttpDownloadThread> downloadThreads;
    private String cacheCancelHttp = null;
    /***本次下载的个数**/
    private int currentDownloadSize;
    public int type;
    /**
     * 保存到本地文件夹的路径
     */
    private String downloadFloder;

    public HttpDownloadTask() {
        downloadThreads = new CopyOnWriteArrayList<HttpDownloadThread>();
        downloadList = new CopyOnWriteArrayList<String>();
        downloadListBos = new CopyOnWriteArrayList<DownloadListBo>();
    }
    /**
     * 添加一条下载任务
     * @param downloadUrl
     * 			文件下载的地址
     * @param progressListener
     * 			监听下载进度的状态和进度
     * @param downloadFolder
     * 			保存到本地的文件夹路径
     */
//	public void addDownloadTask(String downloadUrl, DownloadStateCallback progressListener, String downloadFolder) {
//		this.downloadProgressListener = progressListener;
//		this.downloadFloder = downloadFolder;
//		this.downloadList.add(downloadUrl);
//	}

    /**
     * 添加一组下载任务
     *
     * @param downloadUrlList  文件下载的地址
     * @param progressListener 监听下载进度的状态和进度
     * @param downloadFolder   保存到本地的文件夹路径
     * @param type             下载的文件类型
     */
    public void addDownloadListTask(List<String> downloadUrlList, IDownloadStateCallback progressListener, String downloadFolder, int type) {
        if (null != downloadThread) {
            DownloadListBo downloadListBo = new DownloadListBo(downloadUrlList, progressListener, downloadFolder, type);
            downloadListBos.add(downloadListBo);
            isContinueDownload("addDownloadListTask");
        } else {
            if (null == downloadThread) {
                this.downloadFloder = downloadFolder;
                this.downloadProgressListener = progressListener;
                this.downloadList = new CopyOnWriteArrayList<String>(downloadUrlList);
                this.type = type;

                currentDownloadSize = downloadList.size();
                downloadThread = new HttpDownloadThread(this, downloadList, downloadFloder, type);
                downloadThread.start();

                downloadThreads.add(downloadThread);
            } else {
                LogUtils.d(TAG, "execu", " downloadThread not null");
            }
            LogUtils.d(TAG, "addDownloadListTask", " downloadThread not null");
        }
    }

    /**
     * 执行下载
     */
    public void execu() {
        if (null != downloadFloder) {
//            if (null == downloadThread) {
//                currentDownloadSize = downloadList.size();
//                downloadThread = new HttpDownloadThread(this, downloadList, downloadFloder, type);
//                downloadThread.start();
//                downloadThreads.add(downloadThread);
//            } else {
//                LogUtils.d(TAG, "execu", " downloadThread not null");
//            }
        } else {
            LogUtils.d(TAG, "execu", " execu download");
        }
    }

    /**
     * 启动下载
     *
     * @param downloadListBo
     */
    private void startHttpDownloadThread(DownloadListBo downloadListBo) {
        CopyOnWriteArrayList<String> downloadList = new CopyOnWriteArrayList<String>(downloadListBo.downloadUrlList);
        this.currentDownloadSize = downloadList.size();
        this.downloadFloder = downloadListBo.downloadFolder;
        this.downloadProgressListener = downloadListBo.progressListener;
        this.downloadList = downloadList;
        this.type = downloadListBo.type;

        downloadThread = new HttpDownloadThread(this, this.downloadList, downloadFloder, type);
        downloadThread.start();

        downloadThreads.add(downloadThread);
//		downloadThread.excute(this, downloadList, downloadListBo.downloadFolder, downloadListBo.type);
        LogUtils.d(TAG, "startHttpDownloadThread", " currentDownloadSize : " + currentDownloadSize);
    }

    /**
     * 取消下载
     */
    public void cancel() {
        if (null != downloadThread) {
            downloadThread.cancelTask();
            downloadList.clear();
            downloadThreads.clear();
            LogUtils.e(TAG, "cancel ", "download!");
        } else {
            LogUtils.e(TAG, "cancel", " fail if downloadThread is null!");
        }
    }

    /**
     * 取消下载列表中的素材
     *
     * @param fileNames
     */
    public void cancelListTask(List<String> fileNames) {
        LogUtils.e(TAG, "cancelListTask ", "fileNames size :" + fileNames.size());
        try {
            if (null != downloadThreads && !downloadThreads.isEmpty()) {
                removeDownloadListBo(fileNames);
                for (HttpDownloadThread downloadThread : downloadThreads) {
                    downloadThread.cancelListTask(fileNames);
                }
                LogUtils.e(TAG, "cancelListTask ", "download!");
            } else {
                LogUtils.e(TAG, "cancelListTask", " fail if downloadThread is null!");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void removeDownloadListBo(List<String> fileNames) {
        for (DownloadListBo downloadListBo : downloadListBos) {
            List<String> httpUrlList = new ArrayList<String>(downloadListBo.downloadUrlList);
            for (String httpUrl : httpUrlList) {
                String fileName = FileUtil.getFileName(httpUrl);
                if (fileNames.contains(fileName)) {
                    LogUtils.d(TAG, "removeDownloadHttpUrlList", "fileName：" + fileName);
                    downloadListBo.downloadUrlList.remove(httpUrl);
                    downloadProgressListener.onCancel(httpUrl, downloadListBo.type);
                }
            }
        }
    }

    /**
     * 根据传入的http地址，判断下载集合里面是否存在当前的地址，如果存在则返回true,否则是false.
     *
     * @param httpUrl http下载地址
     * @return 返回值，true表示存在，反之不存在
     */
    public boolean ifExistToUrl(String httpUrl) {
        if (!TextUtils.isEmpty(httpUrl)) {
            return downloadList.contains(httpUrl);
        }
        return false;
    }

    /**
     * 获取下载列表
     *
     * @return
     */
    public CopyOnWriteArrayList<String> getDownloadList() {
        return downloadList;
    }

    @Override
    public void onSuccess(String httpUrl, int totalSize, int downloadType) {
        if (null != downloadThread) {
            if (downloadThread.getHttpUrlList().contains(httpUrl)) {
                currentDownloadSize--;
                isContinueDownload("onSuccess");
                downloadProgressListener.onSuccess(httpUrl, totalSize, downloadType);
            }
        }
    }

    @Override
    public void onFail(String httpUrl, String errMsg, int downloadType) {
        if (!httpUrl.equals(cacheHttpUrl)) {
            cacheHttpUrl = httpUrl;
            if (null != downloadThread) {
                if (downloadThread.getHttpUrlList().contains(httpUrl)) {
                    currentDownloadSize--;
                    isContinueDownload("onFail");
                    downloadProgressListener.onFail(httpUrl, errMsg, downloadType);
                }
            }
        }
//		if (null == cacheHttpUrl) {
//			cacheHttpUrl = httpUrl;
//		}

    }

    @Override
    public void updateProgreass(int progress, int totalSize, String httpUrl, int downloadType) {
        downloadProgressListener.updateProgreass(progress, totalSize, httpUrl, downloadType);
    }

    @Override
    public void onStart(int progress, int totalSize, String httpUrl, int downloadType) {
        downloadProgressListener.onStart(progress, totalSize, httpUrl, downloadType);
    }

    @Override
    public void onCancel(String httpUrl, int downloadType) {
        LogUtils.d(TAG, "onCancel", " httpUrl:" + httpUrl + ", downloadType : " + downloadType);
        if (!httpUrl.equals(cacheCancelHttp)) {
            if (null != downloadThread) {
                if (downloadThread.getHttpUrlList().contains(httpUrl)) {
                    currentDownloadSize--;
                }
            }
            isContinueDownload("onCancel");
            downloadList.remove(httpUrl);
            downloadProgressListener.onCancel(httpUrl, downloadType);
        }
    }

    private void isContinueDownload(String tag) {
        LogUtils.d(TAG, "isContinueDownload", " currentDownloadSize: " + currentDownloadSize + " ,tag: " + tag);
        if (currentDownloadSize <= 0 && !downloadListBos.isEmpty()) {
            cacheCancelHttp = null;
            DownloadListBo downloadListBo = downloadListBos.remove(0);
            startHttpDownloadThread(downloadListBo);
        } else {
            LogUtils.d(TAG, "isContinueDownload", " currentDownloadSize: " + currentDownloadSize + " ,tag: " + tag + ", downloadListBos is empty.");
        }
        if (currentDownloadSize <= 0 && downloadListBos.isEmpty()) {
            downloadThread = null;
            downloadThreads.clear();
            LogUtils.d(TAG, "cancelDownloadList", " currentDownloadSize is zore downloadListBos id empty.");
        }
    }

}