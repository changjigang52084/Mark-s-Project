package com.lzkj.downloadservice.download;

import com.lzkj.downloadservice.bean.FileDownloadBo;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.interfaces.IHttpDownloadStateCallback;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * 下载文件的线程
 *
 * @author changkai
 */
class FileDownloadThread extends Thread {
    private static final LogTag TAG = LogUtils.getLogTag(FileDownloadThread.class.getSimpleName(), true);
    /**
     * 第几条线程
     */
    private int threadNum = 0;
    /**
     * 文件下载的地址
     */
    String downloadFileUrl = null;
    /**
     * 保存的路径
     */
    private String saveFilePath = null;
    /**
     * 保存的文件夹的路径
     */
    private String saveFolderPath = null;
    /**
     * 文件下载的结束位置
     */
    private int fileEndPosition = 0;
    /**
     * 重试的次数
     */
    private int retrySize = 35;
    /**
     * 重试了多少次
     */
    private int retryNum = 0;
    /**
     * 连接超时时间
     */
    private static final int TIMEOUT = 30000;
    /**
     * http连接
     */
    private HttpURLConnection httpURLConnection;
    /**
     * 是否取消任务，true表示取消
     */
    private boolean cancelTask = false;
    /**
     * 休眠时间
     */
    private static final long SLEEP_TIME = 5 * 1000;
    /**
     * 下载的开始位置
     */
    private int startPosition = 0;
    /**
     * 监听下载状态和进度的接口
     */
    private IHttpDownloadStateCallback downloadStateCallback;
    /**
     * 当前下载的进度
     */
    private int currentProgress = 0;
    /**
     * 文件的总大小
     */
    private int totalSize = 0;
    /**
     * 线程的个数
     */
    private int threadSize = 0;
    /**
     * 文件名
     */
    public String fileName = null;
    /**
     * 无记录值为0
     */
    private static final int NOTRECORD = 0;
    private int tempStartPosition = 0;
    /**
     * 下载类型
     */
    private int downloadType = -1;
    /**
     * 1个线程
     */
    private static final int ONETHREAD = 1;

    /**
     * @param fileDownloadBo            文件下载的对象
     * @param httpDownloadStateCallback 下载状态的接口
     */
    protected FileDownloadThread(FileDownloadBo fileDownloadBo, IHttpDownloadStateCallback httpDownloadStateCallback) {
        this.threadNum = fileDownloadBo.currentThreadNum;
        this.downloadFileUrl = fileDownloadBo.httpUrl;
        this.saveFilePath = fileDownloadBo.filePath;
        this.fileEndPosition = fileDownloadBo.endPosition;
        this.startPosition = fileDownloadBo.startPosition;
        this.tempStartPosition = fileDownloadBo.startPosition;
        this.totalSize = fileDownloadBo.fileSize;
        this.threadSize = fileDownloadBo.threadSize;
        this.downloadType = fileDownloadBo.downloadType;
        this.saveFolderPath = FileUtil.getInstance().getFolderToFilePath(saveFilePath);
        downloadStateCallback = httpDownloadStateCallback;
    }
//	public FileDownloadThread(int currentThreadNum,String httpUrl,String filePath,int endPosition,int startPosition
//			,HttpDownloadStateCallback httpDownloadStateCallback,int fileSize,int threadSize, int downloadType) {
//		this.threadNum       = currentThreadNum;
//		this.downloadFileUrl = httpUrl;
//		this.saveFilePath    = filePath;
//		this.fileEndPosition = endPosition;
//		this.startPosition   = startPosition;
//		this.tempStartPosition = startPosition;
//		this.totalSize       = fileSize;
//		this.threadSize		 = threadSize;
//		this.downloadType = downloadType;
//		downloadStateCallback = httpDownloadStateCallback;
//	}

    @Override
    public void run() {
        LogUtils.d(TAG, "downloadFileUrl", downloadFileUrl);
        fileName = FileUtil.getFileName(downloadFileUrl);
        if (initCurrentProgress()) {
            downloadStateCallback.onSuccess(downloadFileUrl, totalSize, downloadType);
            LogUtils.d(TAG, "run", "download end threadid: " + threadNum + " ,fileName: " + fileName + " ,onSuccess initCurrentProgres is success");
            return;
        }
        if (threadSize >= ONETHREAD) {//如果下载的线程大于1则将当前的线程数减一
            threadSize--;
        }
        downloadStateCallback.updateProgreass(currentProgress, totalSize, downloadFileUrl, downloadType);
        boolean downloadSuccess = brakePointDownload();
        LogUtils.d(TAG, "run", "download end threadid: " + threadNum + " ,fileName: " + fileName);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if (downloadSuccess) {
            downloadStateCallback.onSuccess(downloadFileUrl, totalSize, downloadType);
        } else {
            downloadStateCallback.onFail(downloadFileUrl, "download failed", downloadType);
        }
        LogUtils.d(TAG, "run", "download end threadid: " + threadNum + " ,fileName: " + fileName + " ,onSuccess");
    }

    // 初始化当前进度
    private boolean initCurrentProgress() {
        currentProgress = SQLiteManager.getInstance().queryProgresToTable(fileName, threadNum);
        LogUtils.d(TAG, "initCurrentProgress", "currentProgress: " + currentProgress + " ,fileName: " + fileName + " ,threadNum: " + threadNum);
//		if (startPosition >  0 && startPosition == currentProgress) {
//			Log.d(TAG, "initCurrentProgres true");
//			return true;
//		}
        return false;
    }

    /**
     * 断点下载
     */
    private boolean brakePointDownload() {
        while (retryNum < retrySize && !cancelTask) {
            try {
                initDownloadLog();
                LogUtils.d(TAG, "brakePointDownload", "brakePointDownload retryNum: " + retryNum + " ,downloadFileUrl: " + downloadFileUrl);
//				URL httpUrl = new URL(downloadFileUrl);
                URL httpUrl = new URL(FileUtil.encodeUrl(downloadFileUrl));
                httpURLConnection = (HttpURLConnection) httpUrl.openConnection();
                httpURLConnection.setConnectTimeout(TIMEOUT);
                httpURLConnection.setReadTimeout(TIMEOUT);
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty(
                        "Accept",
                        "image/gif, image/jpeg, image/pjpeg, "
                                + "image/pjpeg, application/x-shockwave-flash, "
                                + "application/xaml+xml, application/vnd.ms-xpsdocument, "
                                + "application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, "
                                + "application/vnd.ms-powerpoint, application/msword, */*");

                httpURLConnection.setRequestProperty("Referer", httpUrl.toString());
                //断点的位置
                String newValue = null;
                if (threadSize == threadNum) {//作用在于区分是不是最后一个线程再下载
                    newValue = "bytes=" + startPosition + "-";
                } else {
                    newValue = "bytes=" + startPosition + "-" + fileEndPosition;
                }
                httpURLConnection.setRequestProperty("Charset", "UTF-8");
                httpURLConnection.setRequestProperty("Range", newValue);
                httpURLConnection.setRequestProperty(
                        "User-Agent",
                        "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; "
                                + ".NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; "
                                + ".NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
                httpURLConnection.setRequestProperty("Connection", "keep-alive");
                LogUtils.d(TAG, "brakePointDownload", "brakePointDownload responseCode:" + httpURLConnection.getResponseCode() + ", newValue : " + newValue);
                httpURLConnection.connect();
                if (HttpURLConnection.HTTP_OK == httpURLConnection.getResponseCode() ||
                        HttpURLConnection.HTTP_PARTIAL == httpURLConnection.getResponseCode()) {
                    LogUtils.d(TAG, "brakePointDownload", "brakePointDownload newValue:" + newValue + ",contentLength:" + httpURLConnection.getContentLength()
                            + ",ResponseCode:" + httpURLConnection.getResponseCode() + ",threadId:" + threadNum + ",threadSize:" + threadSize);
                    randomWriterFile(httpURLConnection.getInputStream(), startPosition);
                }
                httpURLConnection.disconnect();
                cancelTask = true;
                retryNum = retrySize;
                return true;
            } catch (Exception e) {
                if (!cancelTask) {
                    try {
                        httpURLConnection.disconnect();
//						downloadStateCallback.onFail(downloadFileUrl, e.getMessage());
                        retryNum++;
                        Thread.sleep(SLEEP_TIME);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                }
                e.printStackTrace();
            }
        }
        //下载失败
        return false;
    }

    /**
     * 初始化下载记录，如果存在下载日志，则获取上次下载的位置
     */
    private boolean initDownloadLog() {
        //首先查询下看有木有下载记录
        int downloadProgress = currentProgress;
//		int downloadProgress = SQLiteManager.getInstance().queryProgresToTable(fileName, threadNum);
        LogUtils.d(TAG, "initDownloadLog", "threadNum: " + threadNum + " ,fileName: " + fileName);
        if (NOTRECORD == downloadProgress) {
            LogUtils.d(TAG, "initDownloadLog", "initDownloadLog downloadProgress record is zero");
            SQLiteManager.getInstance().insertTable(fileName, NOTRECORD, threadNum, saveFolderPath);
        } else {
            if (startPosition > tempStartPosition) {//将开始下载的位置复原
                startPosition = tempStartPosition;
            }
            startPosition += downloadProgress;//确定下载的开始位置,将以前下载的记录加上开始位置
            currentProgress = downloadProgress;//保存当前的下载进度
            LogUtils.d(TAG, "initDownloadLog", "initDownloadLog startPosition: " + startPosition + " ,currentProgress: " + currentProgress + " ,threadNum: " + threadNum + " ,fileName: " + fileName);
        }
//		if (startPosition >  0 && startPosition == currentProgress) {//如果当前下载的进度和当前的进度相同
//			Log.d(TAG, "initDownloadLog true");
//			return true;
//		}
        LogUtils.d(TAG, "initDownloadLog", "initDownloadLog threadid: " + threadNum + " ,downloadProgress: " + downloadProgress + " ,fileName: " + fileName);
        return false;
    }

    /**
     * 断点下载
     *
     * @param httpInputStream 输入流
     * @param brakePoint      下载的开始位置
     * @throws IOException
     */
    private void randomWriterFile(InputStream httpInputStream, int brakePoint) throws IOException {
        File file = new File(saveFilePath);
        if (!file.exists()) {
            file.createNewFile();
        }
        RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rwd");
        randomAccessFile.seek(brakePoint);
        BufferedInputStream bufferedInputStream = new BufferedInputStream(httpInputStream);
        int length = 0;
        byte buffer[] = new byte[1024 * 1024];
        byte downloadEnd = -1;
        while ((length = bufferedInputStream.read(buffer)) != downloadEnd) {
            randomAccessFile.write(buffer, 0, length);
            //显示下载进度和保存下载进度
            downloadStateCallback.updateProgreass(length, totalSize, downloadFileUrl, downloadType);
            currentProgress += length;
            LogUtils.d(TAG, "randomWriterFile", "randomWriterFile currentProgress: " + currentProgress + " ,threadId: " + threadNum);
            SQLiteManager.getInstance().updateTable(fileName, currentProgress, threadNum);
        }
        randomAccessFile.close();
        bufferedInputStream.close();
        httpInputStream.close();
        SQLiteManager.getInstance().deleteTable(fileName, threadNum);
        //显示下载完成
        LogUtils.d(TAG, "randomWriterFile", "threadId: " + threadNum + " ,download over" + ",download size:" + currentProgress + ",per downloadSize:" + (fileEndPosition - startPosition));
    }

    /**
     * 取消下载任务
     */
    public void cancelTask() {
        LogUtils.d(TAG, "cancelTask", "threadId: " + threadNum + " cancelTask");
        retryNum = retrySize;
        cancelTask = true;
        if (null != httpURLConnection) {
            LogUtils.d(TAG, "cancelTask", "httpURLConnection not null threadId: " + threadNum + " cancelTask");
            httpURLConnection.disconnect();
        }
        SQLiteManager.getInstance().deleteTable(fileName, threadNum);
    }
}