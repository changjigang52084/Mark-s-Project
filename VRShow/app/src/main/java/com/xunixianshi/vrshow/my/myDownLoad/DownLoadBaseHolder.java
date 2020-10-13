package com.xunixianshi.vrshow.my.myDownLoad;

import android.content.Context;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.FileDownloaderListener;
import com.hch.filedownloader.download.FileDownloaderModel;
import com.hch.utils.OkhttpConstant;
import com.hch.viewlib.util.ViewlibConstant;
import com.hch.viewlib.util.StringUtils;

import java.io.File;

/**
 * @author duanchunlin
 * @ClassName: DownLoadHolder
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @date 2016-3-22 下午3:12:00 统一下载管理类
 */

public abstract class DownLoadBaseHolder extends RecyclerView.ViewHolder implements FileDownloaderListener {

    public static final int DOWNLOAD_STATE_NORMAL = 0; // 默认无状态
    public static final int DOWNLOAD_STATE_WAIT = 1; // 等待下载
    public static final int DOWNLOAD_STATE_DOWNING = 2; // 正在下载
    public static final int DOWNLOAD_STATE_PAUSE = 3; // 暂停
    public static final int DOWNLOAD_STATE_COMPLETE = 4; // 下载完成

    public Context mContext; // 上下文
    private String mDownLoadType; // 下载类型
    private String mDownLoadIconUrl; // 下载图标连接地址
    private String mDownLoadUrl; // 下载地址
    private String mDownLoadName; // 下载名称
    public String mDownLoadResourcesID; // 资源id

    private int mDownloadId = -1; // 下载ID 一般是数据库的序列号
    private long mSoFarBytes; // 当前下载的字节数
    private long mTotalBytes; // 总共字节数
    private long mSpeed; // 下载速度 字节数
    private int mProgress; // 下载进度

    private int mDownLoadState; // 下载状态
    private FileDownloaderModel mFileDownloaderModel; // 下载任务对象，里面包含保存地址
    public String filePath;

    public DownLoadBaseHolder(Context context, View itemView) {
        super(itemView);
        this.mContext = context;
        this.mDownLoadState = DOWNLOAD_STATE_NORMAL;
    }

    public abstract void notifyProductProgress(int state); // 下载状态

    public abstract void notifyDataSetChanged(); // 数据更改

    public abstract void onProgress(long soFarBytes, long totalBytes,
                                    long speed, int progress); // 下载进度

    public FileDownloaderModel createFileDownloaderModel() {
        FileDownloaderModel model = new FileDownloaderModel();
        model.setUrl(mDownLoadUrl);
        model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_TYPE, mDownLoadType);
        model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_MARK_ID, mDownLoadResourcesID);
        model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_NAME, mDownLoadName);
        model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_ICON_URL, mDownLoadIconUrl);
        return model;
    }

    /**
     * @param @return 设定文件
     * @return boolean 返回类型
     * 添加下载任务
     * @Title: addDownloadTask
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     */
    public boolean addDownloadTask(FileDownloaderModel model) {
        if (StringUtils.isBlank(model.getUrl())) {
            return false;
        }
        this.mFileDownloaderModel = DownloaderManager.getInstance().addTask(
                model);
        this.mDownloadId = mFileDownloaderModel.getId(); // 获取返回的 ID
        // DownloaderManager.getInstance().removeFileDownloadListener(mDownloadId);
        // //清除监听
        DownloaderManager.getInstance().addFileDownloadListener(mDownloadId,
                this); // 重新注册监听
        DownloaderManager.getInstance().startTask(mDownloadId);
        return true;
    }

    public void pauseDownLoad() {
        if (this.mDownloadId != -1) {
            DownloaderManager.getInstance().pauseTask(mDownloadId);
            this.setDownLoadState(DOWNLOAD_STATE_PAUSE);
            this.notifyProductProgress(DOWNLOAD_STATE_PAUSE);
        }
    }

    public void startDownLoad() {
        if (this.mDownloadId != -1) {
            DownloaderManager.getInstance().startTask(mDownloadId);
            this.setDownLoadState(DOWNLOAD_STATE_DOWNING);
            this.notifyProductProgress(DOWNLOAD_STATE_DOWNING);
        }
    }

    /**
     * @param @param downType
     * @param @param downName
     * @param @param iconUrl
     * @param @param filePath 设定文件
     * @return void 返回类型
     * ，只是初始化数据
     * @Title: refreshDate
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     */
    public void refreshDate(String downType, String resourcesId,
                            String downName, String iconUrl, String filePath, long fileSize) {

        this.mDownLoadResourcesID = resourcesId;
        this.filePath = filePath;
        this.mDownLoadName = StringUtils.isBlank(downName) ? OkhttpConstant.DOWN_LOAD_UNKNOWN_TYPE_NAME
                : downName;
        this.mDownLoadType = StringUtils.isBlank(downType) ? OkhttpConstant.DOWN_LOAD_UNKNOWN_TYPE_NAME
                : downType;
        this.mDownLoadIconUrl = iconUrl;
        this.mTotalBytes = fileSize;
    }

    /**
     * @param @param downType
     * @param @param downUrl
     * @param @param downName
     * @param @param iconUrl 设定文件
     * @return void 返回类型
     * 初始化下载状态    一定要调用这个方法
     * @Title: refreshState
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     */
    public void refreshState(String downType, String resourcesId,
                             String downName, String iconUrl, String downUrl, long downSize) {

        this.mDownloadId = -1;
        this.mDownLoadUrl = downUrl;
        this.refreshDate(downType, resourcesId, downName, iconUrl, "", downSize); // 没有路径

        this.notifyDataSetChanged();
        this.initDownState(this.mDownLoadUrl);
        this.notifyProductProgress(getDownLoadState());
    }

    private void initDownState(String downUrl) {
        FileDownloaderModel model = null;
        if (!StringUtils.isBlank(downUrl)) {
            model = DownloaderManager.getInstance()
                    .getFileDownloaderModelByUrl(downUrl);
        }
        if (model == null && !StringUtils.isBlank(mDownLoadResourcesID)) {
            model = DownloaderManager.getInstance()
                    .getFileDownloaderModelByExFeild(
                            OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_MARK_ID,
                            mDownLoadResourcesID);
        }

        final FileDownloaderModel finalmodel = model;
        if (finalmodel != null) {
            DownloaderManager.getInstance().removeFileDownloadListener(
                    finalmodel.getId()); // 清除监听
            DownloaderManager.getInstance().addFileDownloadListener(
                    finalmodel.getId(), this); // 重新注册监听

            if (DownloaderManager.getInstance().isReady()) {
                if (DownloaderManager.getInstance().isFinish(
                        finalmodel.getId())) {
                    // 下载完成
                    mDownLoadState = DownLoadBaseHolder.DOWNLOAD_STATE_COMPLETE;
                    this.filePath = finalmodel.getPath();
                } else if (DownloaderManager.getInstance().isDownloading(
                        finalmodel.getId()) || finalmodel.isAuthentication()) {
                    // 正在下载
                    mDownLoadState = DownLoadBaseHolder.DOWNLOAD_STATE_DOWNING;
                } else if (DownloaderManager.getInstance().isWaiting(
                        finalmodel.getId())) {
                    // 队列已满，等待下载
                    mDownLoadState = DownLoadBaseHolder.DOWNLOAD_STATE_WAIT;
                } else if (!new File(finalmodel.getPath()).exists()) {
                    // 在下载管理，下载没有开始过
                    mDownLoadState = DownLoadBaseHolder.DOWNLOAD_STATE_PAUSE;
                } else {
                    // 已经在下载列表，可能因为某种原有导致下载停止
                    mDownLoadState = DownLoadBaseHolder.DOWNLOAD_STATE_PAUSE;
                }
            }
            this.mProgress = DownloaderManager.getInstance().getProgress(
                    finalmodel.getId());
            this.mSpeed = DownloaderManager.getInstance().getSpeed(
                    finalmodel.getId());
            this.mTotalBytes = DownloaderManager.getInstance().getTotal(
                    finalmodel.getId());
            this.mSoFarBytes = DownloaderManager.getInstance().getSoFar(
                    finalmodel.getId());
            this.mDownloadId = finalmodel.getId();
            if (StringUtils.isBlank(mDownLoadUrl)) {
                this.mDownLoadUrl = finalmodel.getUrl();
            }
            this.mFileDownloaderModel = finalmodel;
            onProgress(mSoFarBytes, mTotalBytes, mSpeed, mProgress);
        }
    }

    public String getDownLoadName() {
        return mDownLoadName;
    }

    public String getDownLoadIconUrl() {
        return mDownLoadIconUrl;
    }

    public String getDownLoadResourcesID() {
        return mDownLoadResourcesID;
    }

    public void setDownLoadState(int state) {
        this.mDownLoadState = state;
    }

    public int getDownloadId() {
        return this.mDownloadId;
    }

    public int getDownLoadState() {
        return this.mDownLoadState;
    }

    public String getDownLoadType() {
        return this.mDownLoadType;
    }

    public long getTotalBytes() {
        return this.mTotalBytes;
    }

    public long getSoFarBytes() {
        return this.mSoFarBytes;
    }

    @Override
    public void onStart(int downloadId, long soFarBytes, long totalBytes,
                        int preProgress) {
        // TODO Auto-generated method stub
        this.mSoFarBytes = soFarBytes;
        this.mTotalBytes = totalBytes;
        this.mProgress = preProgress;

        this.setDownLoadState(DownLoadBaseHolder.DOWNLOAD_STATE_DOWNING);
        this.notifyProductProgress(getDownLoadState());
        this.onProgress(soFarBytes, totalBytes, preProgress, totalBytes!=0? ((int)(100*soFarBytes/totalBytes)):0);
    }

    @Override
    public void onProgress(int downloadId, long soFarBytes, long totalBytes,
                           long speed, int progress) {
        // TODO Auto-generated method stub

        this.mSoFarBytes = soFarBytes;
        this.mTotalBytes = totalBytes;
        this.mProgress = progress;
        this.mSpeed = speed;
        if (getDownLoadState() != DownLoadBaseHolder.DOWNLOAD_STATE_DOWNING) {
            this.setDownLoadState(DownLoadBaseHolder.DOWNLOAD_STATE_DOWNING);
            this.notifyProductProgress(getDownLoadState());
        }
        this.onProgress(soFarBytes, totalBytes, speed, progress);
    }

    @Override
    public void onWait(int downloadId) {
        // TODO Auto-generated method stub
        this.setDownLoadState(DownLoadBaseHolder.DOWNLOAD_STATE_WAIT);
        this.notifyProductProgress(getDownLoadState());
    }

    @Override
    public void onStop(int downloadId, long soFarBytes, long totalBytes,
                       int progress) {
        // TODO Auto-generated method stub
        this.mSoFarBytes = soFarBytes;
        this.mTotalBytes = totalBytes;
        this.mProgress = progress;
        this.setDownLoadState(DownLoadBaseHolder.DOWNLOAD_STATE_PAUSE);
        this.notifyProductProgress(getDownLoadState());
        this.onProgress(soFarBytes, totalBytes, 0, progress);
    }

    @Override
    public void onFinish(int downloadId, String path) {
        // TODO Auto-generated method stub
        this.mProgress = 100;
        this.filePath = path;
        this.setDownLoadState(DownLoadBaseHolder.DOWNLOAD_STATE_COMPLETE);
        this.notifyProductProgress(getDownLoadState());
    }

    @Override
    public void onError(int downloadId, Throwable t) {
        if (!DownloaderManager.getInstance().getFileDownloaderModelById(downloadId).isAuthentication()) {
            this.setDownLoadState(DownLoadBaseHolder.DOWNLOAD_STATE_PAUSE);
            this.notifyProductProgress(getDownLoadState());
        }
        //下载失败
    }
}
