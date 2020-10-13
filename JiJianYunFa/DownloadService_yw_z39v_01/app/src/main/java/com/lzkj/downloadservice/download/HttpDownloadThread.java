package com.lzkj.downloadservice.download;

import com.baize.lz.core.utils.MD5Utils;
import com.lzkj.downloadservice.bean.DownloadInfo;
import com.lzkj.downloadservice.bean.DownloadInfo.STATE;
import com.lzkj.downloadservice.bean.FileDownloadBo;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.interfaces.IHttpDownloadStateCallback;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.FileUtil.LogType;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.util.PrmFileHashUtil;
import com.lzkj.downloadservice.util.ShreadUtil;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * 下载线程
 *
 * @author changkai
 */
public class HttpDownloadThread extends Thread implements IHttpDownloadStateCallback {
    private static final LogTag TAG = LogUtils.getLogTag(HttpDownloadThread.class.getSimpleName(), true);

    /**
     * 下载状态的接口
     */
    private IHttpDownloadStateCallback httpDownloadState;
    /**
     * 下载地址的集合
     */
    private CopyOnWriteArrayList<String> downloadHttpUrlList;
    /**
     * 本地保存的位置
     */
    private String downloadFolder;
    /**
     * 连接超时的时间
     */
    private static final int TIMEOUT = 3000;
    /**
     * 重试次数
     */
    private static final int RETRY = 20;
    /**
     * 失败
     */
    private static final int FAIL = 0;
    /**
     * 取消任务的标志位
     */
    private boolean cancelFlag = false;
    private int index = 0;
    /**
     * 保存下载线程的集合
     */
    private List<FileDownloadThread> fileDownloadThreads = null;
    /**
     * 线程数默认值3
     */
    private int threadSize = 3;
    /**
     * 是否下载成功
     */
    private boolean ifDownloadSuccess = false;
    /**
     * 下载单个文件的进度
     */
    private int downloadProgress = 0;
    /**
     * 下载次数,默认值1
     */
    private int downloadNum = 0;
    /**
     * 临时保存下载进度，用于计算下载速度
     */
    private int tempProgress;
    /**
     * 临时保存上次的时间，用于计算下载速度
     */
    private long tempTime;
    /**
     * 时间1秒钟
     */
    private int oneSeconds = 1000;
    /**
     * 下载的文件类型
     */
    private int type;
    /**
     * 是否重置下载
     **/
    private boolean isResetDownload = true;

    /**
     * @param httpDownloadState   监听下载状态的接口
     * @param downloadHttpUrlList 下载地址的集合
     * @param downloadFolder      保存到本地的文件夹路径
     * @param type                下载类型
     */
    public HttpDownloadThread(IHttpDownloadStateCallback httpDownloadState, CopyOnWriteArrayList<String> downloadHttpUrlList, String downloadFolder, int type) {
        this.httpDownloadState = httpDownloadState;
        this.downloadFolder = downloadFolder;
        this.downloadHttpUrlList = downloadHttpUrlList;
        fileDownloadThreads = new ArrayList<FileDownloadThread>();
        this.type = type;
    }

    @Override
    public void run() {
        excuteDownload("run");
    }

    public CopyOnWriteArrayList<String> getHttpUrlList() {
        return downloadHttpUrlList;
    }


    private void excuteDownload(String tag) {
        LogUtils.d(TAG, "excuteDownload", "tag: " + tag + ", downloadHttpUrlList: " + downloadHttpUrlList.size());
        if (null != downloadFolder) {
            LogUtils.d(TAG, "run", "null != downloadFolder: " + tag);
            checkFolderIsExists();
        }
        while (isResetDownload) {
            isResetDownload = false;
            LogUtils.d(TAG, "excuteDownload", "isResetDownload downloadHttpUrlList: " + downloadHttpUrlList.size());
            //遍历下载
            for (String httpUrl : downloadHttpUrlList) {
                download(httpUrl);
                LogUtils.d(TAG, "excuteDownload", "isResetDownload httpUrl: " + httpUrl);
                sleepThread();
                if (cancelFlag) {//判断是否要取消任务
                    LogUtils.d(TAG, "run", "cancelFlag: " + cancelFlag);
                    return;
                }
                if (isResetDownload) {
                    break;
                }
            }
        }
    }

    /**
     * 判断传入的保存的文件夹是否存在
     */
    private void checkFolderIsExists() {
        //判断文件夹是否存在 如果不存在就创建这个文件夹
        File floderFile = new File(downloadFolder);
        floderFile.mkdirs();
    }

    /**
     * 阻塞线程等待当前的下载文件下载完成
     */
    private void sleepThread() {
        while (!ifDownloadSuccess) {//阻塞线程，等待上一个下载任务完成
            try {
                Thread.sleep(oneSeconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        ifDownloadSuccess = false;
        index = 0;
    }

    /**
     * 判断要下载的素材的文件大小，如果小于10M则使用单线程下载
     *
     * @param contentLength 下载文件的大小
     */
    private void checkDownloadLength(int contentLength) {
        int size = 1024 * 1024 * 3;
        if (size > contentLength) {
            threadSize = 1;
        }
    }

    /**
     * 判断传入的文件夹名称是否为空，如果是为空则创建对应的文件夹
     *
     * @param fileName 文件名
     */
    private void checkFolderIsNull(String fileName) {
        if (null == downloadFolder) {
            LogUtils.i(TAG, "checkFolderIsNull", "downloadFolder: " + downloadFolder);
            downloadFolder = getDownloadFolderToFileName(fileName);
            checkFolderIsExists();
        }
    }

    /**
     * 下载
     *
     * @param httpUrl 下载的地址
     */
    public void download(String httpUrl) {
        try {
            downloadProgress = 0;
            downloadNum = 0;
            String fileName = FileUtil.getFileName(httpUrl);
            if (Constant.DOWNLOAD_APP != type && Constant.DOWNLOAD_FILE != type) {
                //获取要下载的文件的hash
                String hash = PrmFileHashUtil.get().getHash(fileName);
                LogUtils.d(TAG, "", "HttpDownloadThread-->>hash:" + hash);
                //如果要下载的文件哈希值为空则直接下载失败
                if (hash == null) {
                    LogUtils.w(TAG, "download", "File Hash is null httpUrl:" + httpUrl);
                    httpDownloadState.onSuccess(httpUrl, 0, type);
                    ifDownloadSuccess = true;
                    return;
                }
            }
            int contentLength = getContentLength(httpUrl);
            if (FAIL > contentLength) {
                ifDownloadSuccess = true;
                onFail(httpUrl, "fileName:" + fileName + ",get download file size fail.contentLength is:" + contentLength, type);
                return;
            }
            onStart(0, contentLength, fileName, type);
            checkDownloadLength(contentLength);
            addDownloadInfo(contentLength, fileName);
            // Method: download , Message: index 0,httpUrl: http://oqsrrgfyy.bkt.clouddn.com/Launcher1.0.57.20181027.yw.beta.apk,contentLength: 8803761,threadSize: 3
            LogUtils.d(TAG, "download ", "index: " + index + " ,httpUrl: " + httpUrl + " ,contentLength: " + contentLength + " ,threadSize: " + threadSize);
            checkFolderIsNull(fileName);
            String suffix = FileUtil.getSuffix(fileName);
            if ("pl".equals(suffix)) {
                fileName = fileName + ".temp";
            }
            File file = new File(downloadFolder + File.separator + fileName);
            if (createLocalFileIfExists(fileName, contentLength, httpUrl, file)) {//创建本地文件，并且判断本地是否存在相同的文件如果有则返回true
                // Method: download , Message: createLocalFileIfExists httpUrl:http://oqsrrgfyy.bkt.clouddn.com/Launcher1.0.57.20181027.yw.beta.apk, type :3
                LogUtils.d(TAG, "download ", "createLocalFileIfExists httpUrl:" + httpUrl + ", type :" + type);
                ifDownloadSuccess = true;
                if (Constant.DOWNLOAD_FILE != type) {
                    downloadFolder = null;
                }
                return;
            }
            createDownloadThread(contentLength, httpUrl, file.getAbsolutePath());
            LogUtils.d(TAG, "download", "end httpUrl: " + httpUrl);
        } catch (Exception e) {
            ifDownloadSuccess = true;
            e.printStackTrace();
        }
    }

    /**
     * 创建本地文件，并且判断本地是否存在相同的文件如果有则返回true
     *
     * @param contentLength 文件的大小
     * @param httpUrl       下载文件的地址
     * @param file          本地文件的路径
     * @return true表示本地已经存在，则不进行下载
     * @throws IOException
     */
    private boolean createLocalFileIfExists(String fileName, int contentLength,
                                            String httpUrl, File file) throws IOException {
        if (!file.exists()) {
            File folderFile = new File(file.getParent());
            folderFile.mkdirs();
            LogUtils.d(TAG, "createLocalFileIfExists ", "path: " + file.getAbsolutePath());
            file.createNewFile();
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");
            randomAccessFile.setLength(contentLength);
            randomAccessFile.close();
        } else {
            if (checkFileExists(contentLength, file, true)) {
                downloadNum = threadSize - 1;
                onSuccess(httpUrl, contentLength, type);
                return true;
            }
            downloadProgress = getDownloadProgress(fileName);//获取数据库中记录的下载总大小
        }
        return false;
    }

    /**
     * @return String    返回类型
     * @Title: getHashValue
     * @Description: TODO(获取要下载的文件的哈希值)
     * @Param @return    设定文件
     */
    private String getHashValue(File localFile) {
        String hashValue;
        if (Constant.DOWNLOAD_APP == type) {
            hashValue = ShreadUtil.newInstance().getString(localFile.getName());
            LogUtils.d(TAG, "getHashValue", "apk hashValue: " + hashValue);
        } else {
            hashValue = PrmFileHashUtil.get().getHash(localFile.getName());
        }
        return hashValue;
    }

    /**
     * 创建下载线程
     *
     * @param contentLength 文件的大小
     * @param httpUrl       文件下载地址
     * @param localPath     保存在本地的路径
     */
    private void createDownloadThread(int contentLength, String httpUrl, String localPath) {
        //计算每个线程下载的大小
        int blckSize = ((contentLength % threadSize) == 0) ? (contentLength / threadSize) : (contentLength / threadSize + 1);
        LogUtils.d(TAG, "download", "blckSize: " + blckSize);
        //计算每条线程的开始位置和结束位置
        for (int threadId = 0; threadId < threadSize; threadId++) {
            //下载的结束位置
            int endPosition = (threadId + 1) < threadSize ? ((threadId + 1) * blckSize - 1) : contentLength;
            //下载的开始位置
            int startPosition = (threadId * blckSize);
            FileDownloadBo fileDownloadBo = new FileDownloadBo(threadId, httpUrl, localPath, endPosition, startPosition, contentLength, threadSize, type);
            //添加一条下载的线程
            FileDownloadThread fileDownloadThread = new FileDownloadThread(fileDownloadBo, this);
            LogUtils.d(TAG, "createDownloadThread", "threadId: " + threadId + " ,startPosition: " + startPosition + " ,endPosition: " + endPosition);
            fileDownloadThread.start();
            fileDownloadThreads.add(fileDownloadThread);
        }
    }

    /**
     * 判断当前的文件大小是否一致
     *
     * @param downloadFileSize 下载文件的大小
     * @param localFile        本地文件的大小
     * @param isInit           是否为初始化(true表示初始化调用，false下载完成进行校验)
     * @return true表示存在，false表示不存在
     */
    private boolean checkFileExists(int downloadFileSize, File localFile, boolean isInit) {

        if (downloadFileSize == localFile.length()) {
            try {
                if (Constant.IS_QINIU) {
                    if (Constant.DOWNLOAD_FILE == type && !isInit) {
                        return true;
                    }
                    //获取要下载的文件的hash
                    String hash = getHashValue(localFile);
                    //如果要下载的文件哈希值为空则返回false
                    if (hash == null) {
                        LogUtils.w(TAG, "download", "File Hash is null");
                        return false;
                    }
                    //本地已存在或已下载的文件哈希值
                    String loclHashValue = MD5Utils.getHashToFilePath(localFile.getPath());
                    LogUtils.d(TAG, "checkFileExists", "hashValue:" + hash + ", loclHashValue:" + loclHashValue + ",isInit:" + isInit);
                    if (hash.equals(loclHashValue)) {//对比hash值
                        return true;
                    } else {
                        LogUtils.d(TAG, "checkFileExists", "file Hash not match");
                        if (!isInit) {
                            localFile.delete();
                        }
                        return false;
                    }
                } else {
                    return true;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //如果下载的类型是download file并且是下载完以后的校验 默认全部为true
        if (Constant.DOWNLOAD_FILE == type && !isInit) {
            return true;
        }
        return false;
    }

    /**
     * 添加下载记录
     *
     * @param contentLength 文件大小
     * @param fileName      文件名
     */
    private void addDownloadInfo(int contentLength, String fileName) {
        DownloadInfo downloadInfo = new DownloadInfo();
        downloadInfo.setDownloadFileSize(contentLength);
        downloadInfo.setDownloadUrl(fileName);
        downloadInfo.setDownloadState(STATE.DOWNLOADING);
        DownloadInfoManager.get().addDownloadInfo(fileName, downloadInfo);
    }

    /**
     * 获取数据库里面的下载记录
     *
     * @param fileName
     * @return
     */
    private int getDownloadProgress(String fileName) {
        //查询数据库里面的下载记录
        return SQLiteManager.getInstance().queryAllTableProgress(fileName);
    }

    /**
     * 根据http地址获取文件的大小
     *
     * @param httpUrl
     * @return
     */
    private int getContentLength(String httpUrl) {
        int index = 0;
        while (index < RETRY) {
            try {
                URL url = new URL(FileUtil.encodeUrl(httpUrl));
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("GET");
                httpURLConnection.setRequestProperty("Accept-Encoding", "identity");
                httpURLConnection.setConnectTimeout(TIMEOUT);
                httpURLConnection.setReadTimeout(TIMEOUT);
                httpURLConnection.connect();
                int responseCode = httpURLConnection.getResponseCode();
                //  Method: getContentLength , Message: httpUrl:http://oqsrrgfyy.bkt.clouddn.com/Launcher1.0.57.20181027.yw.beta.apk,responseCode：200
                LogUtils.d(TAG, "getContentLength ", "httpUrl: " + httpUrl + ",responseCode: " + responseCode);
                if (HttpURLConnection.HTTP_OK == responseCode
                        || HttpURLConnection.HTTP_PARTIAL == responseCode) {
                    index = RETRY;
                    return httpURLConnection.getContentLength();
                } else {
                    index++;
                }
            } catch (Exception e) {
//				httpDownloadState.onFail(httpUrl, e.getMessage());
                try {
                    index++;
                    Thread.sleep(TIMEOUT);
                    LogUtils.d(TAG, "getContentLength ", "error msg index:" + index);
                } catch (InterruptedException e1) {
                    e1.printStackTrace();
                }
                e.printStackTrace();
            }
        }
        ifDownloadSuccess = true;//获取连接失败
        return -1;
    }

    /**
     * 根据文件名获取保存在本地的路径,默认返回temp文件夹目录
     *
     * @param fileName
     * @return
     */
    private String getDownloadFolderToFileName(String fileName) {
        if (null == fileName) {
            return FileUtil.getInstance().getTempFolder();
        }
        String suffix = FileUtil.getSuffix(fileName).trim().toLowerCase();
        LogUtils.d(TAG, "getDownloadFolderToFileName", "suffix: " + suffix);
        if (FileUtil.getInstance().suffixToFolder.containsKey(suffix)) {
            return FileUtil.getInstance().suffixToFolder.get(suffix);
        }
        return FileUtil.getInstance().getVideoFolder();
    }

    /**
     * 取消任务
     */
    public void cancelTask() {
        cancelFlag = true;
        index = RETRY;
        ifDownloadSuccess = true;
        if (fileDownloadThreads.size() > 0) {
            for (FileDownloadThread fileDownloadThread : fileDownloadThreads) {
                fileDownloadThread.cancelTask();
            }
            fileDownloadThreads.clear();
            downloadHttpUrlList.clear();
        }
        LogUtils.d(TAG, "cancelTask", " size: " + fileDownloadThreads.size());
    }

    /**
     * 删除一组下载素材列表
     *
     * @param fileNames 一组文件名
     */
    public void cancelListTask(List<String> fileNames) {
        LogUtils.d(TAG, "cancelListTask", "fileNames size: " + fileNames.size());
        if (null == fileNames || fileNames.isEmpty()) {
            return;
        }
        boolean download = false;
        List<FileDownloadThread> removeFileDownloadThreads = new ArrayList<FileDownloadThread>();
        if (null != fileDownloadThreads && !fileDownloadThreads.isEmpty()) {
            int size = fileDownloadThreads.size();
            for (int i = 0; i < size; i++) {
                LogUtils.d(TAG, "cancelListTask", "fileDownloadThread.fileName: " + fileDownloadThreads.get(i).fileName);
                if (fileNames.contains(fileDownloadThreads.get(i).fileName)) {
//							 if (fileName.equals(fileDownloadThreads.get(i).fileName)) {
                    removeFileDownloadThreads.add(fileDownloadThreads.get(i));
                    fileDownloadThreads.get(i).cancelTask();
                    download = true;
                    LogUtils.d(TAG, "cancelListTask", "equals.fileName: " + fileDownloadThreads.get(i).fileName);
                }
            }
        }
        removeDownloadHttpUrlList(fileNames);
        fileDownloadThreads.removeAll(removeFileDownloadThreads);
        if (downloadHttpUrlList.isEmpty()) {
            cancelTask();
        } else {
            if (download) {
                ifDownloadSuccess = true;
                isResetDownload = true;
//				excuteDownload("cancelListTask");
            }
        }
        LogUtils.d(TAG, "cancelListTask", "   size:" + downloadHttpUrlList.size());
    }

    private void removeDownloadHttpUrlList(List<String> fileNames) {
        List<String> removeHttpUrl = new ArrayList<String>();
        for (String httpUrl : downloadHttpUrlList) {
            String fileName = FileUtil.getFileName(httpUrl);
            if (fileNames.contains(fileName)) {
                removeHttpUrl.add(httpUrl);
                LogUtils.d(TAG, "removeDownloadHttpUrlList", "fileName：" + fileName);
                httpDownloadState.onCancel(httpUrl, type);
            }
        }
        downloadHttpUrlList.removeAll(removeHttpUrl);
    }

    @Override
    public void onSuccess(String httpUrl, int total, int downloadType) {
        LogUtils.d(TAG, "onSuccess", " httpUrl: " + httpUrl + " ,total: " + total + " ,downloadType: " + downloadType);
        downloadNum++;
        if (threadSize == downloadNum) {
            downloadProgress = 0;
            downloadNum = 0;
            int totalSize = getContentLength(httpUrl);
            String localFilePath = downloadFolder + File.separator + FileUtil.getFileName(httpUrl);
            LogUtils.d(TAG, "onSuccess", "localFilePath: " + localFilePath + " ,downloadNum: " + downloadNum);
            File localFile = new File(downloadFolder + File.separator + FileUtil.getFileName(httpUrl));
            if (checkFileExists(totalSize, localFile, false)) {
                LogUtils.d(TAG, "onSuccess", " downloadNum: " + downloadNum + " ,threadSize: " + threadSize + " ,fileName: " + FileUtil.getFileName(httpUrl));
                httpDownloadState.onSuccess(httpUrl, totalSize, downloadType);
                ifDownloadSuccess = true;
                downloadHttpUrlList.remove(httpUrl);
                if (Constant.DOWNLOAD_FILE != type) {
                    downloadFolder = null;
                }
            } else {
                localFile.delete();
                LogUtils.d(TAG, "onSuccess", "check md5 error retry download  httpUrl: " + httpUrl);
//				download(httpUrl);//md5 校验失败不重新下载 将错误下载地址记录 并且设置为下载失败
                String errorLog = "httpUrl: " + httpUrl + " , md5 check failed!";
                httpDownloadState.onFail(httpUrl, errorLog, downloadType);
                FileUtil.getInstance().writerLog(errorLog,
                        FileUtil.getInstance().getLogFolder()
                                + File.separator +
                                FileUtil.getInstance().getLogFileName(LogType.DOWNLOADERROR_LOG));
                ifDownloadSuccess = true;
                downloadHttpUrlList.remove(httpUrl);
                if (Constant.DOWNLOAD_FILE != type) {
                    downloadFolder = null;
                }

            }
        }
    }

    @Override
    public void onFail(String httpUrl, String errMsg, int downloadType) {
        //把下载速度设置为零
        httpDownloadState.onFail(httpUrl, errMsg, downloadType);
    }

    @Override
    public void updateProgreass(int progress, int totalSize, String httpUrl, int downloadType) {
        httpDownloadState.updateProgreass(progress, totalSize, httpUrl, downloadType);
    }

    @Override
    public void onStart(int progress, int totalSize, String httpUrl, int downloadType) {
        httpDownloadState.onStart(progress, totalSize, httpUrl, downloadType);
    }

    @Override
    public void onCancel(String httpUrl, int downloadType) {
    }
}