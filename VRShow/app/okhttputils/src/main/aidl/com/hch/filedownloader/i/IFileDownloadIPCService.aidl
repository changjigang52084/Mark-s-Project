package com.hch.filedownloader.i;

import com.hch.filedownloader.i.IFileDownloadIPCCallback;
import com.hch.filedownloader.model.FileDownloadTransferModel;
import com.hch.filedownloader.model.FileDownloadHeader;

interface IFileDownloadIPCService {

    oneway void registerCallback(in IFileDownloadIPCCallback callback);
    oneway void unregisterCallback(in IFileDownloadIPCCallback callback);

    FileDownloadTransferModel checkReuse(String url, String path);
    FileDownloadTransferModel checkReuse2(int id);
    boolean checkDownloading(String url, String path);
    // why not use `oneway` to optimize the performance of the below `start` method? because if we
    // use `oneway` it will be very hard to decide how is the binder thread going according to the context.
    // and in this way(not is `oneway`), we can block the download before its launch only
    // by {@link FileDownloadEventPool#shutdownSendPool} according to the context, because it
    // will execute sync on the {@link FileDownloadEventPool#sendPool}
    void start(String url, String path, int callbackProgressTimes, int autoRetryTimes, in FileDownloadHeader header,String urlKey);
    boolean pause(int downloadId);
    void pauseAllTasks();

    long getSofar(int downloadId);
    long getTotal(int downloadId);
    int getStatus(int downloadId);
    boolean isIdle();
}
