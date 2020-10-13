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

package com.hch.filedownloader;

import android.os.IBinder;
import android.os.RemoteException;

import com.hch.filedownloader.event.DownloadTransferEvent;
import com.hch.filedownloader.i.IFileDownloadIPCService;
import com.hch.filedownloader.model.FileDownloadHeader;
import com.hch.filedownloader.model.FileDownloadStatus;
import com.hch.filedownloader.model.FileDownloadTransferModel;
import com.hch.filedownloader.services.BaseFileServiceUIGuard;
import com.hch.filedownloader.services.FileDownloadService;
import com.hch.filedownloader.i.IFileDownloadIPCCallback;


/**
 * Created by Jacksgong on 9/23/15.
 */
class FileDownloadServiceUIGuard extends BaseFileServiceUIGuard<FileDownloadServiceUIGuard.FileDownloadServiceCallback, IFileDownloadIPCService> {

    private final static class HolderClass {
        private final static FileDownloadServiceUIGuard INSTANCE = new FileDownloadServiceUIGuard();
    }

    public static FileDownloadServiceUIGuard getImpl() {
        return HolderClass.INSTANCE;
    }

    protected FileDownloadServiceUIGuard() {
        super(FileDownloadService.class);
    }

    @Override
    protected FileDownloadServiceCallback createCallback() {
        return new FileDownloadServiceCallback();
    }

    @Override
    protected IFileDownloadIPCService asInterface(IBinder service) {
        return IFileDownloadIPCService.Stub.asInterface(service);
    }

    @Override
    protected void registerCallback(IFileDownloadIPCService service, FileDownloadServiceCallback fileDownloadServiceCallback) throws RemoteException {
        service.registerCallback(fileDownloadServiceCallback);
    }

    @Override
    protected void unregisterCallback(IFileDownloadIPCService service, FileDownloadServiceCallback fileDownloadServiceCallback) throws RemoteException {
        service.unregisterCallback(fileDownloadServiceCallback);
    }

    public static class FileDownloadServiceCallback extends IFileDownloadIPCCallback.Stub {

        @Override
        public void callback(FileDownloadTransferModel transfer) throws RemoteException {
            FileDownloadEventPool.getImpl().receiveByService(new DownloadTransferEvent(transfer));
        }
    }

    /**
     * @param url                   for download
     * @param path                  for save download file
     * @param callbackProgressTimes for callback progress times
     * @param autoRetryTimes        for auto retry times when error
     * @param header                for http header
     */
    public boolean startDownloader(final String url, final String path,
                                   final int callbackProgressTimes, final int autoRetryTimes,
                                   final FileDownloadHeader header,final String urlKey) {
        if (getService() == null) {
            return false;
        }

        try {
            getService().start(url, path, callbackProgressTimes, autoRetryTimes, header,urlKey);
        } catch (RemoteException e) {
            e.printStackTrace();

            return false;
        }

        return true;
    }

    public boolean pauseDownloader(final int downloadId) {
        if (getService() == null) {
            return false;
        }

        try {
            return getService().pause(downloadId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    public FileDownloadTransferModel checkReuse(final String url, final String path) {
        if (getService() == null) {
            return null;
        }

        try {
            return getService().checkReuse(url, path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public FileDownloadTransferModel checkReuse(final int id) {
        if (getService() == null) {
            return null;
        }

        try {
            return getService().checkReuse2(id);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return null;
    }

    public boolean checkIsDownloading(final String url, final String path) {
        if (getService() == null) {
            return false;
        }

        try {
            return getService().checkDownloading(url, path);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return false;
    }

    public long getSofar(final int downloadId) {
        long val = 0;
        if (getService() == null) {
            return val;
        }

        try {
            val = getService().getSofar(downloadId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return val;
    }

    public long getTotal(final int downloadId) {
        long val = 0;
        if (getService() == null) {
            return val;
        }

        try {
            val = getService().getTotal(downloadId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return val;
    }

    public int getStatus(final int downloadId){
        int status = FileDownloadStatus.INVALID_STATUS;
        if (getService() == null) {
            return status;
        }

        try {
            status = getService().getStatus(downloadId);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return status;
    }

    public void pauseAllTasks(){
        if (getService() == null) {
            return;
        }

        try {
            getService().pauseAllTasks();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * @return any error, will return true
     */
    public boolean isIdle(){
        if (getService() == null) {
            return true;
        }

        try {
            getService().isIdle();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        return true;
    }
}
