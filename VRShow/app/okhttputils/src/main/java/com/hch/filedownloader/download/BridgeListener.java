/*
 * Copyright (C) 2015 彭建波(duanchunlin@finalteam.cn), Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hch.filedownloader.download;

import com.hch.filedownloader.BaseDownloadTask;
import com.hch.filedownloader.FileDownloadListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Desction:
 * Author:duanchunlin
 * Date:16/1/3 下午11:14
 */
class BridgeListener extends FileDownloadListener {

    private List<FileDownloaderListener> mListenerList;

    private BaseDownloadTask mDownloadTask;
    private FileDownloaderListener mGlobleDownloadCallback;
    //开始下载时间，用户计算加载速度
    private long mPreviousTime;
    private long mSpeed;//下载速度
    private long mOldSoFarBytes;

    public BridgeListener(){
        mGlobleDownloadCallback = DownloaderManager.getInstance().getGlobalDownloadCallback();
        mListenerList = Collections.synchronizedList(new ArrayList<FileDownloaderListener>());
    }

    public void setDownloadTask(BaseDownloadTask task) {
        this.mDownloadTask = task;
    }

    public BaseDownloadTask getDownloadTask() {
        return this.mDownloadTask;
    }

    @Override
    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        int preProgress = 0;
        if ( totalBytes != 0 ) {
            preProgress = (int)(soFarBytes / (float)totalBytes * 100);
        }

        for(FileDownloaderListener listener: mListenerList) {
            if (listener != null) {
                listener.onStart(task.getDownloadId(), soFarBytes, totalBytes, preProgress);
            }
        }

        if (mGlobleDownloadCallback != null) {
            mGlobleDownloadCallback.onStart(task.getDownloadId(), soFarBytes, totalBytes, preProgress);
        }
    }

    @Override
    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
        int preProgress = 0;
        if ( totalBytes != 0 ) {
            preProgress = (int)(soFarBytes / (float)totalBytes * 100);
        }
        for(FileDownloaderListener listener: mListenerList) {
            if (listener != null) {
                listener.onStart(task.getDownloadId(), soFarBytes, totalBytes, preProgress);
            }
        }

        if (mGlobleDownloadCallback != null) {
            mGlobleDownloadCallback.onStart(task.getDownloadId(), soFarBytes, totalBytes, preProgress);
        }
        mOldSoFarBytes = soFarBytes;
        mPreviousTime = System.currentTimeMillis();
    }

    @Override
    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        int progress = 0;
        if ( totalBytes != 0 ) {
            progress = (int)(soFarBytes / (float)totalBytes * 100);
        }

        //计算下载速度
        long totalTime = (System.currentTimeMillis() - mPreviousTime)/1000;
        if ( totalTime == 0 ) {
            totalTime += 1;
        }
        long speed = (soFarBytes - mOldSoFarBytes) / totalTime;

        for(FileDownloaderListener listener: mListenerList) {
            if (listener != null) {
                listener.onProgress(task.getDownloadId(), soFarBytes, totalBytes, speed, progress);
            }
        }

        mSpeed = speed;

        if (mGlobleDownloadCallback != null) {
            mGlobleDownloadCallback.onProgress(task.getDownloadId(), soFarBytes, totalBytes, speed, progress);
        }
    }

    @Override
    protected void blockComplete(BaseDownloadTask task) {
    }

    @Override
    protected void completed(BaseDownloadTask task) {
        for(FileDownloaderListener listener: mListenerList) {
            if (listener != null) {
                listener.onFinish(task.getDownloadId(), task.getPath());
            }
        }
        if (mGlobleDownloadCallback != null) {
            mGlobleDownloadCallback.onFinish(task.getDownloadId(), task.getPath());
        }
        nextTask(task.getDownloadId(),true);
    }

    @Override
    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
        stop(task.getDownloadId(), soFarBytes, totalBytes);
    }

    @Override
    protected void error(BaseDownloadTask task, Throwable e) {
        long totalBytes = task.getLargeFileTotalBytes();
        long soFarBytes = task.getLargeFileSoFarBytes();
//        stop(task.getDownloadId(), soFarBytes, totalBytes);
        error(task.getDownloadId(), soFarBytes, totalBytes,e);
   }

    @Override
    protected void warn(BaseDownloadTask task) {
    }

    protected void error(int downloadId, long soFarBytes, long totalBytes, Throwable e){
        DownloaderManager.getInstance().removeDownloadingTask(downloadId);
        if (mGlobleDownloadCallback != null) { //优先处理全局监听
            mGlobleDownloadCallback.onError(downloadId, e);
        }
        for(FileDownloaderListener listener: mListenerList) {
            if (listener != null) {
                listener.onError(downloadId, e);
            }
        }
    }

    protected void stop(int downloadId, long soFarBytes, long totalBytes) {
        int progress = 0;
        if ( totalBytes != 0 ) {
            progress = (int)(soFarBytes / (float)totalBytes * 100);
        }
        for(FileDownloaderListener listener: mListenerList) {
            if (listener != null) {
                listener.onStop(downloadId, soFarBytes, totalBytes, progress);
            }
        }
        if (mGlobleDownloadCallback != null) {
            mGlobleDownloadCallback.onStop(downloadId, soFarBytes, totalBytes, progress);
        }
        nextTask(downloadId,true);
    }

    public void wait(int downloadId) {
        for(FileDownloaderListener listener: mListenerList) {
            if (listener != null) {
                listener.onWait(downloadId);
            }
        }
    }

    public void addDownloadListener(FileDownloaderListener listener) {
        if ( listener != null && !mListenerList.contains(listener)) {
            mListenerList.add(listener);
        }
    }

    public void removeDownloadListener(FileDownloaderListener listener) {
        if ( listener != null && !mListenerList.contains(listener)) {
            mListenerList.remove(listener);
        }
    }

    public void removeAllDownloadListener() {
        mListenerList.clear();
    }

    private void nextTask(int curDownloadId,boolean isRemove) {
        DownloaderManager.getInstance().removeDownloadingTask(curDownloadId);
        FileDownloaderModel model = DownloaderManager.getInstance().nextTask();
        if (model != null) {
            DownloaderManager.getInstance().startTask(model.getId());
        }
    }

    protected long getSpeed() {
        return mSpeed;
    }

}