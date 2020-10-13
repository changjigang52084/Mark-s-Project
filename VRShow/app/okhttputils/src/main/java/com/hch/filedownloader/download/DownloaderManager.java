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

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import okhttp3.Headers;

import android.content.ContentValues;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

import com.hch.filedownloader.BaseDownloadTask;
import com.hch.filedownloader.FileDownloadConnectListener;
import com.hch.filedownloader.FileDownloader;
import com.hch.filedownloader.model.FileDownloadStatus;
import com.hch.filedownloader.util.FileDownloadLog;
import com.hch.filedownloader.util.FileDownloadUtils;
import com.hch.filedownloader.util.ShellUtils;
import com.hch.filedownloader.util.StringUtils;

/**
 * Desction:
 * Author:duanchunlin
 * Date:2016/1/20 0020 15:39
 */
public class DownloaderManager {

    private static DownloaderManager mDownloadManager;
    private FileDownloaderDBController mDbController;
    private SparseArray<FileDownloaderModel> mAllTasks;
    private List<FileDownloadConnectListener> mConnectListenerList;
    private ListenerManager mListenerManager;

    private Queue<FileDownloaderModel> mWaitQueue;
    private List<FileDownloaderModel> mDownloadingList;

    private DownloaderManagerConfiguration mConfiguration;
    private FileDownloaderListener mGlobalDownloadCallback;

    private Map<String, String> mExtFieldMap;
    private int mAutoRetryTimes;
    private Headers mHeaders;

    /**
     * 获取DownloadManager实例
     * @return
     */
    public static DownloaderManager getInstance() {
        if (mDownloadManager == null) {
            mDownloadManager = new DownloaderManager();
        }
        return mDownloadManager;
    }

    /**
     * 初始化DownloadManager
     */
    public synchronized void init(DownloaderManagerConfiguration configuration) {
        FileDownloader.init(configuration.getApplication(), configuration.getOkHttpClientCustomMaker());
        FileDownloader.getImpl().bindService();

        ILogger.DEBUG = configuration.isDebug();
        FileDownloadLog.NEED_LOG = ILogger.DEBUG;

        this.mConfiguration = configuration;
        this.mExtFieldMap = configuration.getDbExtField();
        mDbController = new FileDownloaderDBController(configuration.getApplication(),configuration.getDbVersion(),
                mExtFieldMap, configuration.getDbUpgradeListener());
        mAllTasks = mDbController.getAllTasks();
        mConnectListenerList = new ArrayList<>();
        mListenerManager = new ListenerManager();
        mAutoRetryTimes = configuration.getAutoRetryTimes();
        mHeaders = configuration.getHeaders();

        //设置下载保存目录
        if (!StringUtils.isEmpty(configuration.getDownloadStorePath())) {
            FileDownloadUtils.setDefaultSaveRootPath(configuration.getDownloadStorePath());
        }

        mWaitQueue = new LinkedList<>();
        mDownloadingList = Collections.synchronizedList(new ArrayList<FileDownloaderModel>());
        mDownloadManager = this;

        ShellUtils.execCommand("chmod 777 " + configuration.getDownloadStorePath(), false);
    }

    private DownloaderManager() {
    }

    public static void setDownloadStorePath(String path){
        setDownloadStorePath(path,"FileDownloader");
    }

    public static void setDownloadStorePath(String path, String customDir){
        if (!StringUtils.isEmpty(path)) {
            File file  = new File(path + "/" + customDir + "/");
            boolean isCanSave = true;
            if(!file.exists()){

                if(!file.mkdir()){
                    isCanSave = false;
                }
            }
            if(isCanSave){
                FileDownloadUtils.setDefaultSaveRootPath(file.getAbsolutePath());
            }
            else{
                Log.d("TAG","setDownloadStorePath fail");
            }
        }
    }

    /**
     * 获取扩展字段map
     * @return
     */
    Map<String, String> getDbExtFieldMap() {
        return mExtFieldMap;
    }

    /**
     * 开始下载任务
     * @param downloadId
     */
    public void startTask(int downloadId) {
        startTask(downloadId, null);
    }

    public void startTask(int downloadId,String urlKey){
        startTask(downloadId,urlKey,null);
    }
    /**
     * 开始下载任务
     * @param downloadId
     * @param callback
     */
    public void startTask(int downloadId, String urlKey,FileDownloaderListener callback) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        /*
         downloadId: 66296902
        urlKey: null
        model: com.hch.filedownloader.download.FileDownloaderModel@ca3eaf
         */
        Log.d("cjg","startTask \ndownloadId: " + downloadId+"\nurlKey: " + urlKey + "\nmodel: " + model);
        if (model != null) {
            if(!StringUtils.isEmpty(urlKey)){
                updateUrlKey(model.getId(),model.getUrl(),urlKey);
            }
            BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
            bridgeListener.addDownloadListener(callback);
            Log.d("cjg","startTask \nmDownloadingList size: " + mDownloadingList.size());
            if (mDownloadingList.size() >= mConfiguration.getMaxDownloadingCount()) { //下载中队列已满
                if (!mWaitQueue.contains(model)) {
                    mWaitQueue.offer(model);
                }
                bridgeListener.wait(downloadId);
            } else {
                mDownloadingList.add(model);
                final BaseDownloadTask task = FileDownloader.getImpl().create(model.getUrl())
                        .setPath(model.getPath())
                        .setUrlKey(model.getUrlKey())
                        .setCallbackProgressTimes(100)
                        .setAutoRetryTimes(mAutoRetryTimes)
                        .setListener(bridgeListener);
                for (int i = 0; i < mHeaders.size(); i++) {
                    task.addHeader(mHeaders.name(i), mHeaders.value(i));
                }
                bridgeListener.setDownloadTask(task);
                Log.d("cjg","task id: " + task.start());
            }
        } else {
        	ILogger.e("Task does not exist!");
        }
    }

    /**
     * 删除一个任务
     * @param downloadId
     */
    public boolean deleteTask(int downloadId) {
        if (mDbController.deleteTask(downloadId)) {
            FileDownloaderModel model = getFileDownloaderModelById(downloadId);
            if (model != null) {//删除文件
                new File(model.getPath()).delete();
            }
            pauseTask(downloadId);
            removeDownloadingTask(downloadId);
            removeWaitQueueTask(downloadId);
            try {
                mAllTasks.remove(downloadId);
            } catch (Exception e) {
            	ILogger.e(e);
            }
            
            return true;
        } else {
        	ILogger.e("delete failure");
        	return false;
        }
    }

    /**
     * @param downloadId
     * @param url    匹配是否是同一个地址
     * @param urlKey
     * 更新下载密钥key
     */
    public boolean updateUrlKey(int downloadId, String url, String urlKey){
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        if (model != null && model.getUrl().equals(url)) {
            model.setUrlKey(urlKey);
            ContentValues cv = new ContentValues();
            cv.put(FileDownloaderModel.URL_KEY, urlKey);
            if(mDbController.updateTask(downloadId, cv)){
                ILogger.e("updateUrlKey success");
            }
            return true;
        }
        return false;
    }

    /**
     * @param downloadId 任务id
     * @param authentication 鉴权状态
     *                       添加鉴权状态信息，
     */
    public void setAuthentication(int downloadId, boolean authentication){
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        if(model != null){
            model.setAuthentication(authentication);
        }
    }

    /**
     * 添加下载监听
     * @param downloadId
     * @param listener
     */
    public void addFileDownloadListener(int downloadId, FileDownloaderListener listener) {
        BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
        bridgeListener.addDownloadListener(listener);
    }

    public void removeFileDownloadListener(int downloadId){
        mListenerManager.removeAllDownloadListener(downloadId);
    }

    public void removeAllDownloadListener(){
        mListenerManager.removeAllDownloadListener();
    }

    /**
     * 下一个任务
     * @return
     */
    protected synchronized FileDownloaderModel nextTask() {
        return mWaitQueue.poll();
    }

    /**
     * 将一个下载中的任务从下载中队列移除
     * @param downloadId
     */
    protected synchronized void removeDownloadingTask(int downloadId) {
        Iterator<FileDownloaderModel> iterator = mDownloadingList.iterator();
        while (iterator.hasNext()) {
            FileDownloaderModel model = iterator.next();
            if ( model != null && model.getId() == downloadId) {
                try {
                    iterator.remove();
                } catch (Exception e){
                	ILogger.e(e);
                }
                return;
            }
        }
    }

    /**
     * 将一个等待中的任务从下等待队列中移除
     * @param downloadId
     */
    protected synchronized void removeWaitQueueTask(int downloadId) {
        Iterator<FileDownloaderModel> iterator = mWaitQueue.iterator();
        while (iterator.hasNext()) {
            FileDownloaderModel model = iterator.next();
            if ( model != null && model.getId() == downloadId) {
                try {
                    iterator.remove();
                } catch (Exception e){
                	ILogger.e(e);
                }
                return;
            }
        }
    }

    /**
     * 暂停任务
     * @param downloadId
     */
    public synchronized void pauseTask(int downloadId) {
        if(isWaiting(downloadId)) {
            BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
            removeWaitQueueTask(downloadId);
            bridgeListener.stop(downloadId, getSoFar(downloadId), getTotal(downloadId));
        } else {
            FileDownloader.getImpl().pause(downloadId);
        }
    }

    /**
     * 停止所有任务
     */
    public void pauseAllTask() {
        FileDownloader.getImpl().pauseAll();
    }

    /**
     * 根据任务ID获取任务
     * @param downloadId
     * @return
     */
    public BaseDownloadTask getDownloadTaskById(int downloadId) {
        BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
        return bridgeListener.getDownloadTask();
    }

    /**
     * 添加service连接监听
     * @param listener
     */
    public void addServiceConnectListener(FileDownloadConnectListener listener) {
        FileDownloader.getImpl().addServiceConnectListener(listener);
    }

    /**
     * 移除sevice连接监听
     * @param listener
     */
    public void removeServiceConnectListener(FileDownloadConnectListener listener) {
        FileDownloader.getImpl().removeServiceConnectListener(listener);
    }

    /**
     * 释放下载管理器
     */
    public void onDestroy() {
        try {
            mConnectListenerList.clear();
            pauseAllTask();
            FileDownloader.getImpl().unBindServiceIfIdle();
        } catch (Exception e){}
    }

    /**
     * 获取service是否已连接
     * @return
     */
    public boolean isReady() {
        return FileDownloader.getImpl().isServiceConnected();
    }

    /**
     * 根据索引获取下载信息
     * @param position
     * @return
     */
    public FileDownloaderModel getFileDownloaderModelByPostion(final int position) {
        if(mAllTasks == null ) return null;
        int id = mAllTasks.keyAt(position);
        return getFileDownloaderModelById(id);
    }

    /**
     * 根据URL获取下载信息
     * @param url
     * @return
     */
    public FileDownloaderModel getFileDownloaderModelByUrl(String url) {
        if(mAllTasks == null ) return null;
        for (int i = 0; i < mAllTasks.size(); i++) {
            FileDownloaderModel model = getFileDownloaderModelByPostion(i);
            if (model != null && TextUtils.equals(model.getUrl(), url)) {
                return model;
            }
        }
        return null;
    }

    
    /**
    * @Title: getFileDownloaderModelByResourceID
    * @Description: TODO(这里用一句话描述这个方法的作用)
    * @author duanchunlin 
    * @param @param url
    * @param @return    设定文件
    * @return FileDownloaderModel    返回类型
    * 新增接口  3/27/16
    * 没做唯一插入，只是获取含有该资源id 的下载task
    */ 
    public FileDownloaderModel getFileDownloaderModelByExFeild(String key,String value){
        if(mAllTasks == null ) return null;
        for (int i = 0; i < mAllTasks.size(); i++) {
            FileDownloaderModel model = getFileDownloaderModelByPostion(i);
            if (model != null && TextUtils.equals(model.getExtFieldValue(key), value)) {
                return model;
            }
        }
        return null;    	
    }


    /**
     * 根据downloadId获取下载信息
     * @param downloadId
     * @return
     */
    public FileDownloaderModel getFileDownloaderModelById(final int downloadId) {
        if (mAllTasks != null && mAllTasks.size() > 0) {
            return mAllTasks.get(downloadId);
        }
        return null;
    }

    /**
     * 是否下载完成
     * @param downloadId
     * @return
     */
    public boolean isFinish(int downloadId) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        if (model != null) {
            if (getStatus(downloadId) == FileDownloadStatus.completed && new File(model.getPath()).exists()) {
                return true;
            }
        }

        return false;
    }

    /**
     * 判断是否在等待队列
     * @param downloadId
     * @return
     */
    public boolean isWaiting(int downloadId) {
        FileDownloaderModel model = new FileDownloaderModel();
        model.setId(downloadId);
        return mWaitQueue.contains(model);
    }

    /**
     * 判断一个任务是否在下载中
     * @param downloadId
     * @return
     */
    public boolean isDownloading(final int downloadId) {
        int status = getStatus(downloadId);
        switch (status) {
            case FileDownloadStatus.pending:
            case FileDownloadStatus.connected:
            case FileDownloadStatus.progress:
                return true;
            default:
                return false;
        }
    }

    /**
     * 判断任务是否存在
     * @param url
     * @return
     */
    public boolean exists(String url) {
        FileDownloaderModel model = getFileDownloaderModelByUrl(url);
        if (model != null) {
            return true;
        }

        return false;
    }

    /**
     * 获取FileDownloader FileDownloadStatus下载状态
     * @param downloadId
     * @return
     */
    private int getStatus(final int downloadId) {
        return FileDownloader.getImpl().getStatus(downloadId);
    }

    /**
     * 根据downloadId获取文件总大小
     * @param downloadId
     * @return
     */
    public long getTotal(final int downloadId) {
        return FileDownloader.getImpl().getTotal(downloadId);
    }

    /**
     * 根据downloadId获取文件已下载大小
     * @param downloadId
     * @return
     */
    public long getSoFar(final int downloadId) {
        return FileDownloader.getImpl().getSoFar(downloadId);
    }

    /**
     * 获取下载速度
     * @param downloadId
     * @return
     */
    public long getSpeed(final int downloadId) {
        BridgeListener bridgeListener = mListenerManager.getBridgeListener(downloadId);
        return bridgeListener.getSpeed();
    }

    /**
     * 根据downloadId获取文件下载进度
     * @param downloadId
     * @return
     */
    public int getProgress(int downloadId) {
        FileDownloaderModel model = getFileDownloaderModelById(downloadId);
        int progress = 0;
        if( model != null ) {
            if (!new File(model.getPath()).exists()) {
                return progress;
            }
        }

        long totalBytes = getTotal(downloadId);
        long soFarBytes = getSoFar(downloadId);

        if ( totalBytes != 0 ) {
            progress = (int)(soFarBytes / (float)totalBytes * 100);
        }

        return progress;
    }

    public List<FileDownloaderModel> getAllTask() {
        if(mAllTasks == null) {return null;}
        List<FileDownloaderModel> allTask = new ArrayList<>();
        for (int i = 0; i < mAllTasks.size(); i++) {
            allTask.add(mAllTasks.valueAt(i));
        }
        return allTask;
    }

    /**
     * 获取所任务数
     * @return
     */
    public int getTaskCounts() {
        if (mAllTasks == null){
            return 0;
        }
        return mAllTasks.size();
    }

    /**
     * 添加一个任务
     * 注：同样的URL，保存的目录不一样表示这两次addTask是不同的任务
     * @param url
     * @return
     */
    public FileDownloaderModel addTask(final String url) {
        FileDownloaderModel downloaderModel = new FileDownloaderModel();
        downloaderModel.setUrl(url);
        downloaderModel.setPath(createPath(url));
        return addTask(downloaderModel);
    }

    /**
     * 添加一个任务
     * 注：同样的URL，保存的目录不一样表示这两次addTask是不同的任务
     * @param url
     * @param path
     * @return
     */
    public FileDownloaderModel addTask(final String url, String path) {
        FileDownloaderModel downloaderModel = new FileDownloaderModel();
        downloaderModel.setUrl(url);
        downloaderModel.setPath(path);
        return addTask(downloaderModel);
    }


    /**
     * 添加一个任务
     * 注：同样的URL，保存的目录不一样表示这两次addTask是不同的任务
     * @param downloaderModel
     * @return
     */
    public FileDownloaderModel addTask(FileDownloaderModel downloaderModel) {
        String url = downloaderModel.getUrl();
        String path = downloaderModel.getPath();
        if (TextUtils.isEmpty(url)) {
            return null;
        }
        if (TextUtils.isEmpty(path)) {
            path = createPath(url);
            downloaderModel.setPath(path);
        }

        ShellUtils.execCommand("chmod 777 " + path, false);

        final int id = FileDownloadUtils.generateId(url, path);
        FileDownloaderModel model = getFileDownloaderModelById(id);
        if (model != null) {
            return model;
        }
        model = mDbController.addTask(downloaderModel);
        mAllTasks.put(id, model);

        return model;
    }

    /**
     * 添加任务并启动
     * @param url
     * @return
     */
    public FileDownloaderModel addTaskAndStart(String url) {
        FileDownloaderModel model = addTask(url);
        startTask(model.getId());
        return model;
    }

    /**
     * 添加任务并启动
     * @param url
     * @param path
     * @return
     */
    public FileDownloaderModel addTaskAndStart(final String url, String path) {
        FileDownloaderModel model = addTask(url, path);
        startTask(model.getId());
        return model;
    }

    /**
     * 添加任务并启动
     * @param downloaderModel
     * @return
     */
    public FileDownloaderModel addTaskAndStart(FileDownloaderModel downloaderModel) {
        FileDownloaderModel model = addTask(downloaderModel);
        startTask(model.getId());
        return model;
    }

    /**
     * 创建下载保存地址
     * @param url
     * @return
     */
    private String createPath(final String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        return FileDownloadUtils.getDefaultSaveFilePath(url);
    }

    /**
     * 设置全局下载事件监听
     * @param callback
     */
    public void setGlobalDownloadCallback(FileDownloaderListener callback) {
        this.mGlobalDownloadCallback = callback;
    }

    /**
     * 获取全局下载事件监听
     * @return
     */
    protected FileDownloaderListener getGlobalDownloadCallback() {
        return this.mGlobalDownloadCallback;
    }
}
