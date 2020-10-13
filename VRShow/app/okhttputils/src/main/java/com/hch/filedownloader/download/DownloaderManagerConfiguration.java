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

import android.app.Application;

import androidx.annotation.IntRange;

import com.hch.filedownloader.util.FileDownloadHelper;

import java.util.Map;

import okhttp3.Headers;

/**
 * Desction:
 * Author:duanchunlin
 * Date:2016/1/28 0028 15:02
 */
public class DownloaderManagerConfiguration {

    private Application mApplication;
    private String mDownloadStorePath;
    private int mMaxDownloadingCount = 3;
    private Map<String, String> mDbExtFieldMap;
    private int mDbVersion;
    private boolean mDebug;
    private DbUpgradeListener mDbUpgradeListener;
    private FileDownloadHelper.OkHttpClientCustomMaker mOkHttpClientCustomMaker;
    private int mAutoRetryTimes;
    private Headers mHeaders;

    private DownloaderManagerConfiguration(final Builder builder) {
        this.mApplication = builder.mApplication;
        this.mDownloadStorePath = builder.mDownloadStorePath;
        this.mDbExtFieldMap = builder.mDbExtFieldMap;
        this.mDbVersion = builder.mDbVersion;
        this.mDbUpgradeListener = builder.mDbUpgradeListener;
        this.mDebug = builder.mDebug;

        if (builder.mMaxDownloadingCount > 0) {
            this.mMaxDownloadingCount = builder.mMaxDownloadingCount;
        }

        this.mOkHttpClientCustomMaker = builder.mOkHttpClientCustomMaker;
        this.mAutoRetryTimes = builder.mAutoRetryTimes;
        this.mHeaders = builder.mHeaders.build();
    }

    public static class Builder {
        private Application mApplication;
        private String mDownloadStorePath;
        private int mMaxDownloadingCount = 1;
        private Map<String, String> mDbExtFieldMap;
        private int mDbVersion = 1;
        private DbUpgradeListener mDbUpgradeListener;
        private boolean mDebug;
        private FileDownloadHelper.OkHttpClientCustomMaker mOkHttpClientCustomMaker;
        private int mAutoRetryTimes = 3;
        private Headers.Builder mHeaders;

        public Builder(Application application) {
            this.mApplication = application;
            mHeaders = new Headers.Builder();
        }

        /**
         * 设置下载存储目录
         * @param path
         * @return
         */
        public Builder setDownloadStorePath(String path) {
            this.mDownloadStorePath = path;
            return this;
        }

        /**
         * 设置最大并行下载数
         * @param maxCount
         * @return
         */
        @IntRange(from = 1, to = 100)
        public Builder setMaxDownloadingCount(int maxCount) {
            this.mMaxDownloadingCount = maxCount;
            return this;
        }

        /**
         * 设置表扩展字段
         * @param extFieldMap
         * @return
         */
        public Builder setDbExtField(Map<String, String> extFieldMap) {
            this.mDbExtFieldMap = extFieldMap;
            return this;
        }

        /**
         * 数据库版本号
         * @param dbVersion
         * @return
         */
        @IntRange(from = 1, to = Integer.MAX_VALUE)
        public Builder setDbVersion(int dbVersion) {
            this.mDbVersion = dbVersion;
            return this;
        }

        /**
         * 自动重试次数
         * @param autoRetryTimes
         * @return
         */
        @IntRange(from = 1, to = Integer.MAX_VALUE)
        public Builder setAutoRetryTimes(int autoRetryTimes) {
            this.mAutoRetryTimes = autoRetryTimes;
            return this;
        }

        /**
         * 数据库更新监听
         * @param dbUpgradeListener
         * @return
         */
        public Builder setDbUpgradeListener(DbUpgradeListener dbUpgradeListener) {
            this.mDbUpgradeListener = dbUpgradeListener;
            return this;
        }

        /**
         * 添加header
         * @param line
         * @return
         */
        public Builder addHeader(String line) {
            mHeaders.add(line);
            return this;
        }

        /**
         * 添加header
         * @param name
         * @param value
         * @return
         */
        public Builder addHeader(String name, String value) {
            mHeaders.add(name, value);
            return this;
        }

        /**
         * 设置是否开启debug
         * @param debug
         * @return
         */
        public Builder setDebug(boolean debug) {
            this.mDebug = debug;
            return this;
        }


        /**
         * 设置OkHttp自定义实例
         * @param okHttpClientCustomMaker
         * @return
         */
        public Builder setOkHttpClientCustomMaker(FileDownloadHelper.OkHttpClientCustomMaker okHttpClientCustomMaker) {
            this.mOkHttpClientCustomMaker = okHttpClientCustomMaker;
            return this;
        }

        public DownloaderManagerConfiguration build() {
            return new DownloaderManagerConfiguration(this);
        }

    }

    public Application getApplication() {
        return mApplication;
    }

    public String getDownloadStorePath() {
        return mDownloadStorePath;
    }

    public int getMaxDownloadingCount() {
        return mMaxDownloadingCount;
    }

    public Map<String, String> getDbExtField() {
        return mDbExtFieldMap;
    }

    public DbUpgradeListener getDbUpgradeListener() {
        return mDbUpgradeListener;
    }

    public int getDbVersion() {
        return mDbVersion;
    }

    public boolean isDebug() {
        return mDebug;
    }

    public FileDownloadHelper.OkHttpClientCustomMaker getOkHttpClientCustomMaker() {
        return mOkHttpClientCustomMaker;
    }

    public int getAutoRetryTimes() {
        return mAutoRetryTimes;
    }

    public Headers getHeaders() {
        return mHeaders;
    }
}
