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

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.SparseArray;

import com.hch.filedownloader.model.FileDownloadModel;
import com.hch.filedownloader.util.FileDownloadUtils;
import com.hch.filedownloader.util.StringUtils;

import java.util.Map;



/**
 * Desction:
 * Author:duanchunlin
 * Date:2016/1/20 0020 15:40
 */
class FileDownloaderDBController {
    protected static final String TABLE_NAME = "FileDownloader"; //下载任务

    private FileDowloaderDBOpenHelper mDBHelper;

    public FileDownloaderDBController(Context context, int dbVersion, Map<String, String> dbExtFieldMap, DbUpgradeListener dbUpgradeListener) {
        mDBHelper = new FileDowloaderDBOpenHelper(context, dbVersion, dbExtFieldMap, dbUpgradeListener);
    }

    /**
     * 从数据库中读取所有下载任务
     * @return
     */
    public SparseArray<FileDownloaderModel> getAllTasks() {
        SQLiteDatabase sqliteDatabase = mDBHelper.getReadableDatabase();
        final Cursor c = sqliteDatabase.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        final SparseArray<FileDownloaderModel> tasksMap = new SparseArray<>();
        try {
            while (c.moveToNext()) {
                String url = c.getString(c.getColumnIndex(FileDownloaderModel.URL));
                int id = c.getInt(c.getColumnIndex(FileDownloaderModel.ID));
                String path = c.getString(c.getColumnIndex(FileDownloaderModel.PATH));
                String urlKey = c.getString(c.getColumnIndex(FileDownloaderModel.URL_KEY));
                FileDownloaderModel model = new FileDownloaderModel();
                model.setId(id);
                model.setUrl(url);
                model.setPath(path);
                model.setUrlKey(urlKey);
                model.parseExtField(c);
                tasksMap.put(id, model);
            }
        } catch (Exception e){
        	ILogger.e(e);
        } finally {
            if (c != null) {
                c.close();
            }

            if (sqliteDatabase.isOpen()) {
                sqliteDatabase.close();
            }
        }

        return tasksMap;
    }

    /**
     * 添加一个任务，保存到数据库
     * @param downloaderModel
     * @return
     */
    public synchronized FileDownloaderModel addTask(FileDownloaderModel downloaderModel) {
        String url = downloaderModel.getUrl();
        String path = downloaderModel.getPath();
        if (StringUtils.isEmpty(url) || StringUtils.isEmpty(path)) {
            return null;
        }

        // have to use FileDownloadUtils.generateId to associate FileDownloaderModel with FileDownloader
        final int id = FileDownloadUtils.generateId(url, path);

        downloaderModel.setId(id);
        SQLiteDatabase sqliteDatabase = mDBHelper.getWritableDatabase();
        boolean succeed = false;
        if ( sqliteDatabase.isOpen() ) {
            try {
                succeed = sqliteDatabase.insert(TABLE_NAME, null, downloaderModel.toContentValues()) != -1;
            } catch (Exception e) {
            	ILogger.e(e);
            }
        }

        try {
            sqliteDatabase.close();
        } catch (SQLException e){
        	ILogger.e(e);
        }

        return succeed ? downloaderModel : null;
    }

    /**
     * 删除数据库中的一条任务信息
     * @param downloadId
     * @return
     */
    public synchronized boolean deleteTask(final int downloadId) {
        String[] args = {String.valueOf(downloadId)};
        SQLiteDatabase sqliteDatabase = mDBHelper.getWritableDatabase();
        boolean succeed = false;
        if ( sqliteDatabase.isOpen() ) {
            try {
                succeed = sqliteDatabase.delete(TABLE_NAME, "id=?", args) != -1;
            } catch (Exception e) {
            	ILogger.e(e);
            }
        }

        try {
            sqliteDatabase.close();
        } catch (SQLException e){
        	ILogger.e(e);
        }

        return succeed;
    }

    /**
     * 更新数据库
     * @param downloadId
     * @param contentValues
     * @return
     */
    public synchronized boolean updateTask(final int downloadId,final ContentValues contentValues){
        SQLiteDatabase sqliteDatabase = mDBHelper.getWritableDatabase();
        boolean succeed = false;
        if ( sqliteDatabase.isOpen() ) {
            try {
                sqliteDatabase.update(TABLE_NAME, contentValues, FileDownloadModel.ID + " = ? ",
                        new String[] { String.valueOf(downloadId) });

            } catch (Exception e) {
                ILogger.e(e);
            }
        }
        try {
            sqliteDatabase.close();
        } catch (SQLException e){
            ILogger.e(e);
        }
        return succeed;
    }
}
