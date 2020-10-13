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

package com.hch.filedownloader.util;

import android.app.Application;
import android.content.Context;

import okhttp3.OkHttpClient;

/**
 * Created by duanchunlin on 12/17/15.
 */
public class FileDownloadHelper {

    private static Context APP_CONTEXT;

    // only effect on the ':filedownloader' progress.
    private static OkHttpClient OK_HTTP_CLIENT;

    public static void initAppContext(final Application application) {
        APP_CONTEXT = application;
    }

    public static Context getAppContext() {
        return APP_CONTEXT;
    }

    public static void setOkHttpClient(OkHttpClient client) {
        OK_HTTP_CLIENT = client;
    }

    public static OkHttpClient getOkHttpClient() {
        return OK_HTTP_CLIENT;
    }

    public interface OkHttpClientCustomMaker {

        /**
         * Only be invoked by the {@link Application#onCreate()} on the ':filedownloader' progress.
         * You can customize Timeout, Proxy, etc...
         *
         * @return Nullable, Customize {@link OkHttpClient}, will be used for downloading files.
         * @see com.hch.filedownloader.FileDownloader#init(Application, OkHttpClientCustomMaker)
         * @see OkHttpClient
         */
        OkHttpClient customMake();
    }
}

