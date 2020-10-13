package com.unccr.zclh.dsdps.db;


import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.download.bean.HttpBo;
import com.unccr.zclh.dsdps.download.bean.HttpRequestBean;
import com.unccr.zclh.dsdps.download.bean.RecoveryDownloadBean;
import com.unccr.zclh.dsdps.download.bean.UploadInfo;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * sqlite管理类
 *
 * @author changkai
 */
public class SQLiteManager {
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    /**
     * 保存上传记录的表名
     */
    private static String upload_tableName = "upload_record";
    /**
     * 保存上传记录的本地文件路径
     */
    private static String upload_file_path = "upload_file_path";
    /**
     * 保存上传记录的objectKey
     */
    private static String upload_objectkey = "upload_objectkey";

    /**
     * 需要下载的素材文件表
     **/
    private static String download_task_tableName = "download_task";
    /**
     * 保存下载记录的表名
     */
    private static String download_progress_tableName = "download_progress";
    /**
     * 下载路径的字段
     */
    private String download_path = "download_path";
    /**
     * 下载文件的名称
     */
    private String file_name = "file_name";
    /**
     * 下载的线程
     */
    private String thread_id = "thread_id";
    /**
     * 下载的进度
     */
    private String progress = "progress";
    /**
     * 下载的文件保存的文件夹路径
     */
    private String download_local_path = "download_local_path";
    /**
     * 数据库版本号
     */
    private int version = 9;
    /**
     * 下载类型
     * 1.下载节目
     * 2.下载素材
     * 3.下载app
     */
    private String download_type = "type";

    private SQLiteManager(Context context) {
        sqLiteHelper = new SQLiteHelper(context, version);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
    }

    public static SQLiteManager getInstance() {
        return SQLiteManagerInstance.sqLiteManager;
    }

    private static class SQLiteManagerInstance {
        private static final SQLiteManager sqLiteManager = new SQLiteManager(DsdpsApp.getDsdpsApp());
    }

    /**
     * 创建表
     */
    private void createTable() {
//		SQLiteDatabase sqLiteDatabase = sqLiteHelper.getReadableDatabase();
//		String createTableSql = "create table download_progress "
//				+ "(id INTEGER PRIMARY KEY AUTO_INCREMENT not null,httpurl verchar(50),progress verchar(12))";
//		sqLiteDatabase.execSQL(createTableSql);
    }

    /**
     * 插入上传数据
     *
     * @param uploadFilePath
     * @param objectKey
     */
    public synchronized void insertUploadTable(String uploadFilePath, String objectKey) {
        if (null == sqLiteDatabase) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(upload_file_path, uploadFilePath);
        contentValues.put(upload_objectkey, objectKey);
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.insert(upload_tableName, null, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 删除上传记录
     *
     * @param uploadFilePath
     */
    public synchronized void deleteUploadRecord(String uploadFilePath) {
        if (null == sqLiteDatabase) {
            return;
        }
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(upload_tableName, upload_file_path + " = ?", new String[]{uploadFilePath});
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

    /**
     * 删除上传的所有记录
     */
    public synchronized void deleteUploadRecordTable() {
        deleteTableData(upload_tableName);
    }

    /**
     * 删除表数据
     *
     * @param tableName
     * @return
     */
    public synchronized boolean deleteTableData(String tableName) {
        boolean isSuccess = false;
        if (null == tableName || null == sqLiteDatabase) {
            return isSuccess;
        }
        sqLiteDatabase.beginTransaction();
        try {
            int result = sqLiteDatabase.delete(tableName, null, null);
            if (result != 0) {
                isSuccess = true;
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return isSuccess;
    }

    /**
     * 获取上传记录
     *
     * @return
     */
    public synchronized List<UploadInfo> queryUploadTable() {
        if (null == sqLiteDatabase) {
            return null;
        }
        Cursor cursor = sqLiteDatabase.query(upload_tableName, new String[]{upload_file_path, upload_objectkey}, null, null, null, null, null);
        if (null == cursor) {
            return null;
        }
        List<UploadInfo> uploadInfos = new ArrayList<UploadInfo>();
        while (cursor.moveToNext()) {
            uploadInfos.add(new UploadInfo(cursor.getString(0), cursor.getString(1)));
        }
        cursor.close();

        return uploadInfos;
    }

    /**
     * 插入数据
     *
     * @param downloadUrl       下载的http地址
     * @param download_progress 下载的进度
     * @param threadId          下载线程的id
     * @param localFolderPath   下载到本地文件夹路径
     */
    public synchronized void insertTable(String downloadUrl, int download_progress, int threadId, String localFolderPath) {
        if (null == sqLiteDatabase) {
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(download_path, downloadUrl);
        contentValues.put(thread_id, threadId);
        contentValues.put(progress, download_progress);
        contentValues.put(download_local_path, localFolderPath);
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.insert(download_progress_tableName, null, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

    /**
     * 删除下载进度
     *
     * @param downloadUrl
     * @param threadId
     */
    public synchronized void deleteTable(String downloadUrl, int threadId) {
        if (null == sqLiteDatabase) {
            return;
        }
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(download_progress_tableName, download_path + " = ? and " + thread_id + " = ?", new String[]{downloadUrl, threadId + ""});
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }

    }

    /**
     * 更新进度
     *
     * @param downloadUrl
     * @param download_progress
     * @param threadId
     */
    public synchronized void updateTable(String downloadUrl, int download_progress, int threadId) {
        if (null == sqLiteDatabase) {
            return;
        }
        ContentValues contentValues = new ContentValues();
//		contentValues.put(download_path, downloadUrl);
//		contentValues.put(thread_id, threadId);
        contentValues.put(progress, download_progress);
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.update(download_progress_tableName, contentValues, download_path + " = ? and " + thread_id + " = ?", new String[]{downloadUrl, threadId + ""});
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 获取断点的位置
     *
     * @param downloadUrl
     * @param threadId
     * @return
     */
    public synchronized int queryProgresToTable(String downloadUrl, int threadId) {
        if (null == sqLiteDatabase) {
            return 0;
        }
        Cursor cursor = sqLiteDatabase.query(download_progress_tableName, new String[]{progress},
                download_path + " = ? and " + thread_id + " = ?",
                new String[]{downloadUrl, String.valueOf(threadId)},
                null, null, null);
        if (null == cursor) {
            return 0;
        }
        if (cursor.moveToNext()) {
            int progress = cursor.getInt(0);
            cursor.close();
            return progress;
        }
        cursor.close();
        return 0;
    }

    public synchronized void queryAllTable() {
        if (null == sqLiteDatabase) {
            return;
        }
        Cursor cursor = sqLiteDatabase.query(download_progress_tableName, new String[]{download_path, thread_id, progress}, null, null, null, null, null);
        if (null == cursor) {
            return;
        }
        while (cursor.moveToNext()) {
            //System.out.println("path:"+cursor.getString(0)+",threadid:"+cursor.getInt(1)+",progress"+cursor.getInt(2));
        }
        cursor.close();
    }

    /**
     * 根据文件名获取下载的历史进度
     *
     * @param downloadUrl 文件名
     * @return 获取进度
     */
    public synchronized int queryAllTableProgress(String downloadUrl) {
        if (null == sqLiteDatabase) {
            return 0;
        }
        Cursor cursor = sqLiteDatabase.query(download_progress_tableName, new String[]{progress}, download_path + " = ?", new String[]{downloadUrl}, null, null, null);
        if (null == cursor) {
            return 0;
        }
        int downloadProgress = 0;
        while (cursor.moveToNext()) {
            downloadProgress += cursor.getInt(0);
        }
        cursor.close();
        return downloadProgress;
    }

    /**
     * 删除下载记录表中的所有的数据
     */
    public synchronized void deleteDownloadTableData() {
        if (null == sqLiteDatabase) {
            return;
        }
        sqLiteDatabase.delete(download_progress_tableName, null, null);
    }

    /**
     * 添加下载任务
     *
     * @param cloudPathList
     */
    public synchronized void addDownloadTask(List<String> cloudPathList, int type, String localFolder) {
        if (null == cloudPathList || cloudPathList.isEmpty() || null == sqLiteDatabase) {
            return;
        }
        sqLiteDatabase.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            for (String cloudPath : cloudPathList) {
                contentValues.put(download_path, cloudPath);
                contentValues.put(file_name, FileUtil.getFileName(cloudPath));
                contentValues.put(download_type, type);
                contentValues.put(download_local_path, localFolder);
                sqLiteDatabase.insert(download_task_tableName, null, contentValues);
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 下载完以后删除下载任务记录
     *
     * @param fileName
     */
    public synchronized void delDownloadTask(String fileName) {
        if (null == fileName || null == sqLiteDatabase) {
            return;
        }
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(download_task_tableName, file_name + " = ?", new String[]{fileName});
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 清除所有的任务
     */
    public synchronized void delDownloadTaskTable() {
        if (null == sqLiteDatabase) {
            return;
        }
        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.delete(download_task_tableName, null, null);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 读取未下载完成的下载任务
     *
     * @return
     */
    public synchronized ArrayList<RecoveryDownloadBean> getRecovery() {
        ArrayList<RecoveryDownloadBean> downloadBeans = new ArrayList<RecoveryDownloadBean>();
        if (null == sqLiteDatabase) {
            return downloadBeans;
        }
        sqLiteDatabase.beginTransaction();
        try {
            queryDownloadFileList(downloadBeans, Constants.DOWNLOAD_PRMLIST);
            queryDownloadFileList(downloadBeans, Constants.DOWNLOAD_PRMFILE);
//			queryDownloadFileList(downloadBeans, Constant.DOWNLOAD_FILE);
            queryHttpUrlForFolders(queryDownloadFolder(Constants.DOWNLOAD_FILE), Constants.DOWNLOAD_FILE, downloadBeans);
//			queryHttpUrlForFolders(queryDownloadFolder(Constant.DOWNLOAD_PRMFILE), Constant.DOWNLOAD_PRMFILE, downloadBeans);
//			queryHttpUrlForFolders(queryDownloadFolder(Constant.DOWNLOAD_PRMLIST), Constant.DOWNLOAD_PRMLIST, downloadBeans);
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return downloadBeans;
    }

    /**
     * 获取file下载列表
     *
     * @return
     */
    public synchronized ArrayList<RecoveryDownloadBean> getDownloadFiles() {
        ArrayList<RecoveryDownloadBean> downloadBeans = new ArrayList<RecoveryDownloadBean>();
        if (null == sqLiteDatabase) {
            return downloadBeans;
        }
        queryHttpUrlForFolders(queryDownloadFolder(Constants.DOWNLOAD_FILE), Constants.DOWNLOAD_FILE, downloadBeans);
        return downloadBeans;
    }

    /**
     * 查询未下载完的文件
     *
     * @param downloadBeans 恢复下载的列表
     */
    private void queryDownloadFileList(List<RecoveryDownloadBean> downloadBeans, int downloadType) {
        //未完成的素材下载
        Cursor downloadPrmCursor = sqLiteDatabase.query(download_task_tableName,
                new String[]{download_path, download_local_path}, download_type + " = ?",
                new String[]{String.valueOf(downloadType)},
                null, null, null);
        if (null == downloadPrmCursor) {
            return;
        }
        if (downloadPrmCursor.moveToFirst()) {
            String fileUrlPath = downloadPrmCursor.getString(0);
            String localFolder = downloadPrmCursor.getString(1);
            addRecoveryDownloadBean(downloadBeans, downloadType, fileUrlPath, localFolder);
            while (downloadPrmCursor.moveToNext()) {
                fileUrlPath = downloadPrmCursor.getString(0);
                localFolder = downloadPrmCursor.getString(1);
                addRecoveryDownloadBean(downloadBeans, downloadType, fileUrlPath, localFolder);
            }
            downloadPrmCursor.close();
        }
    }

    //根据文件夹名称 查询一组文件下载地址
    private void queryHttpUrlForFolders(ArrayList<String> folders, int downloadType, List<RecoveryDownloadBean> downloadBeans) {
        if (null == folders || folders.isEmpty() || null == downloadBeans) {
            return;
        }
        for (String folder : folders) {
            if (TextUtils.isEmpty(folder)) {
                continue;
            }
            Cursor downloadPrmCursor = sqLiteDatabase.query(download_task_tableName,
                    new String[]{download_path}, download_type + " = ? and " + download_local_path + " = ?",
                    new String[]{String.valueOf(downloadType), folder},
                    null, null, null);
            if (downloadPrmCursor.moveToFirst()) {
                ArrayList<String> httpUrls = new ArrayList<String>();
                httpUrls.add(downloadPrmCursor.getString(0));
                while (downloadPrmCursor.moveToNext()) {
                    httpUrls.add(downloadPrmCursor.getString(0));
                }
                RecoveryDownloadBean downloadBean = new RecoveryDownloadBean();
                downloadBean.setType(downloadType);
                downloadBean.setSaveFoldePath(folder);
                downloadBean.setList(httpUrls);
                downloadBeans.add(downloadBean);
            }
            downloadPrmCursor.close();
        }
    }

    /**
     * 根据下载类型查询所有的文件夹路径
     *
     * @param downloadType 下载类型
     * @return 所有文件夹路径
     */
    private ArrayList<String> queryDownloadFolder(int downloadType) {
        //查询 所有的文件夹路径
        ArrayList<String> folderList = new ArrayList<String>();
        String queryDownloadFolderSql = "select download_local_path, count(distinct download_local_path) from download_task where type = ?";
        Cursor downloadPrmCursor = sqLiteDatabase.rawQuery(queryDownloadFolderSql, new String[]{String.valueOf(downloadType)});
        if (null == downloadPrmCursor) {
            return folderList;
        }
        if (downloadPrmCursor.moveToFirst()) {
            folderList.add(downloadPrmCursor.getString(0));
            while (downloadPrmCursor.moveToNext()) {
                folderList.add(downloadPrmCursor.getString(0));
            }
        }
        downloadPrmCursor.close();
        return folderList;
    }

    /***
     * 添加恢复下载bean
     * @param downloadBeans 恢复下载列表
     * @param downloadType 下载类型
     * @param fileUrlPath 文件名
     * @param localFolder 保存在本地的文件夹
     */
    @SuppressLint("Recycle")
    private void addRecoveryDownloadBean(List<RecoveryDownloadBean> downloadBeans,
                                         int downloadType, String fileUrlPath, String localFolder) {
        ArrayList<String> list = new ArrayList<String>();
        list.add(fileUrlPath);
        RecoveryDownloadBean downloadBean = new RecoveryDownloadBean();
        downloadBean.setType(downloadType);
        downloadBean.setSaveFoldePath(localFolder);
        downloadBean.setList(list);
        downloadBeans.add(downloadBean);
    }

    /**
     * 插入请求记录
     *
     * @param httpRequestBean
     * @param requestMethod
     * @return
     */
    public synchronized boolean insterHttpBo(HttpRequestBean httpRequestBean, String requestMethod) {
        boolean isSuccess = false;
        if (null != httpRequestBean && null != requestMethod && null != sqLiteDatabase) {
            ContentValues httpRequestContentValues = new ContentValues();
            httpRequestContentValues.put(SQLiteHelper.REQUEST_URL, httpRequestBean.getRequestUrl());
            httpRequestContentValues.put(SQLiteHelper.REQUEST_PRM, httpRequestBean.getRequestParm());
            httpRequestContentValues.put(SQLiteHelper.REQUEST_METHOD, requestMethod);
            httpRequestContentValues.put(SQLiteHelper.REQUEST_TAG, httpRequestBean.getRequestTag());
            try {
                sqLiteDatabase.beginTransaction();
                long result = sqLiteDatabase.insert(SQLiteHelper.TABLE_HTTP_REQUEST, null, httpRequestContentValues);
                if (result > 0) {
                    isSuccess = true;
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
        }
        return isSuccess;
    }

    /**
     * 删除成功的记录
     *
     * @param requestHttpUrl
     * @param requestTag
     * @return
     */
    public synchronized boolean delHttpBo(String requestHttpUrl, String requestTag) {
        boolean isSuccess = false;
        if (null != requestHttpUrl && null != requestTag && null != sqLiteHelper) {
            try {
                sqLiteDatabase.beginTransaction();
                int result = sqLiteDatabase.delete(SQLiteHelper.TABLE_HTTP_REQUEST,
                        SQLiteHelper.REQUEST_URL + " = ? and " + SQLiteHelper.REQUEST_TAG + " = ?",
                        new String[]{requestHttpUrl, requestTag});
                if (result > 0) {
                    isSuccess = true;
                }
                sqLiteDatabase.setTransactionSuccessful();
            } finally {
                sqLiteDatabase.endTransaction();
            }
        }
        return isSuccess;
    }

    /**
     * 查询未成功的记录
     *
     * @return
     */
    public synchronized List<HttpBo> getHttpBoList() {
        List<HttpBo> httpRequestBeanList = new ArrayList<HttpBo>();
        if (null != sqLiteDatabase) {
            Cursor cursor = sqLiteDatabase.query(SQLiteHelper.TABLE_HTTP_REQUEST,
                    new String[]{SQLiteHelper.REQUEST_URL, SQLiteHelper.REQUEST_PRM,
                            SQLiteHelper.REQUEST_METHOD, SQLiteHelper.REQUEST_TAG}, null, null, null, null, null);
            while (cursor.moveToNext()) {
                HttpBo httpRequestBean = new HttpBo();
                httpRequestBean.setHttpUrl(cursor.getString(0));
                httpRequestBean.setHttpRequestParam(cursor.getString(1));
                httpRequestBean.setHttpMethod(cursor.getString(2));
                httpRequestBean.setHttpRequestTag(cursor.getString(3));
                httpRequestBeanList.add(httpRequestBean);
            }
            cursor.close();
        }
        return httpRequestBeanList;
    }


    /**
     * 更新流量
     *
     * @param date         日期(20161020)
     * @param downloadFlow 下载流量
     * @param uploadFlow   上传流量
     * @return true成功
     */
    public synchronized boolean updateFlowTable(String date, long downloadFlow, long uploadFlow) {
        boolean isSuccess = false;
        if (null == date || null == sqLiteDatabase) {
            return isSuccess;
        }
        ContentValues contentValues = new ContentValues();
        if (0 != uploadFlow) {
            contentValues.put(SQLiteHelper.UPLOAD_FLOW, String.valueOf(uploadFlow));
        }
        if (0 != downloadFlow) {
            contentValues.put(SQLiteHelper.DOWNLOAD_FLOW, String.valueOf(downloadFlow));
        }
//		contentValues.put(SQLiteHelper.FLOW_DATE, date);
        sqLiteDatabase.beginTransaction();
        try {
            long result = sqLiteDatabase.update(SQLiteHelper.TABLE_FLOW, contentValues,
                    SQLiteHelper.FLOW_DATE + " = ?",
                    new String[]{date});
            if (result > 0) {
                isSuccess = true;
            }
            sqLiteDatabase.setTransactionSuccessful();
        } finally {
            sqLiteDatabase.endTransaction();
        }
        return isSuccess;
    }

    /**
     * 关闭数据库
     */
    public synchronized void closeDatabase() {
        if (null != sqLiteHelper) {
            sqLiteHelper.close();
            sqLiteDatabase.close();
        }
    }

}
