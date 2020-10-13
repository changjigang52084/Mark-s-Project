/*
 * Copyright (c) 2015 LingoChamp Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hch.filedownloader.services;

import android.content.Context;
import android.os.RemoteException;

import com.hch.filedownloader.event.DownloadEventSampleListener;
import com.hch.filedownloader.event.DownloadTransferEvent;
import com.hch.filedownloader.event.IDownloadEvent;
import com.hch.filedownloader.i.IFileDownloadIPCCallback;
import com.hch.filedownloader.i.IFileDownloadIPCService;
import com.hch.filedownloader.model.FileDownloadHeader;
import com.hch.filedownloader.model.FileDownloadTransferModel;
import com.hch.filedownloader.util.FileDownloadHelper;
import com.hch.filedownloader.util.FileDownloadUtils;

import okhttp3.OkHttpClient;

/**
 * Created by Jacksgong on 9/23/15.
 */
public class FileDownloadService extends BaseFileService<IFileDownloadIPCCallback, FileDownloadService.FileDownloadServiceBinder> implements DownloadEventSampleListener.IEventListener {

    private DownloadEventSampleListener mListener;
    
    private static FileDownloadService service;

    @Override
    public void onCreate() {
        super.onCreate();
        
        service = this;
        mListener = new DownloadEventSampleListener(this);
        
        FileDownloadProcessEventPool.getImpl().addListener(DownloadTransferEvent.ID, mListener);
    }
    
    public static FileDownloadService getFileDownloadService(){
    	return service;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        FileDownloadProcessEventPool.getImpl().removeListener(DownloadTransferEvent.ID, mListener);
    }

    @Override
    protected FileDownloadServiceBinder createBinder(Context context) {
        return new FileDownloadServiceBinder(context,FileDownloadHelper.getOkHttpClient());
    }

    @Override
    protected boolean handleCallback(int cmd, IFileDownloadIPCCallback IFileDownloadIPCCallback, Object... objects) throws RemoteException {
        IFileDownloadIPCCallback.callback(((DownloadTransferEvent) objects[0]).getTransfer());
        return false;
    }

    @Override
    public boolean callback(IDownloadEvent event) {

        if (event instanceof DownloadTransferEvent) {
            callback(0, event);
        }
        return false;
    }

    protected class FileDownloadServiceBinder extends IFileDownloadIPCService.Stub {


        private final FileDownloadMgr downloadManager;

        private FileDownloadServiceBinder(Context context,OkHttpClient customOkhttpClient) {
            this.downloadManager = new FileDownloadMgr(context,customOkhttpClient);
        }

        @Override
        public void registerCallback(IFileDownloadIPCCallback callback) throws RemoteException {
            register(callback);
        }

        @Override
        public void unregisterCallback(IFileDownloadIPCCallback callback) throws RemoteException {
            unregister(callback);
        }

        @Override
        public FileDownloadTransferModel checkReuse(String url, String path) throws RemoteException {
            return downloadManager.checkReuse(FileDownloadUtils.generateId(url, path));
        }

        @Override
        public FileDownloadTransferModel checkReuse2(int id) throws RemoteException {
            return downloadManager.checkReuse(id);
        }

        @Override
        public boolean checkDownloading(String url, String path) throws RemoteException {
            return downloadManager.checkDownloading(url, path);
        }

        @Override
        public void start(String url, String path, int callbackProgressTimes, int autoRetryTimes,
                          FileDownloadHeader header,String urlKey) throws RemoteException {
            downloadManager.start(url, path, callbackProgressTimes, autoRetryTimes, header,urlKey);
        }

        @Override
        public boolean pause(int downloadId) throws RemoteException {
            return downloadManager.pause(downloadId);
        }

        @Override
        public void pauseAllTasks() throws RemoteException {
            downloadManager.pauseAll();
        }

        @Override
        public long getSofar(int downloadId) throws RemoteException {
            return downloadManager.getSoFar(downloadId);
        }

        @Override
        public long getTotal(int downloadId) throws RemoteException {
            return downloadManager.getTotal(downloadId);
        }

        @Override
        public int getStatus(int downloadId) throws RemoteException {
            return downloadManager.getStatus(downloadId);
        }

        @Override
        public boolean isIdle() throws RemoteException {
            return downloadManager.isIdle();
        }
    }
}
