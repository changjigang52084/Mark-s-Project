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

import android.util.Log;

/**
 * Created by duanchunlin on 12/17/15.
 */
public class FileDownloadLog {

    public static boolean NEED_LOG = true;

    private final static String TAG = "FileDownloader.";

    public static void e(Object o, Throwable e, String msg, Object... args) {
        log(Log.ERROR, o, e, msg, args);
    }

    public static void e(Object o, String msg, Object... args) {
        log(Log.ERROR, o, msg, args);
    }

    public static void i(Object o, String msg, Object... args) {
        log(Log.INFO, o, msg, args);
    }

    public static void d(Object o, String msg, Object... args) {
        log(Log.DEBUG, o, msg, args);
    }

    public static void w(Object o, String msg, Object... args) {
        log(Log.WARN, o, msg, args);
    }

    public static void v(Object o, String msg, Object... args) {
        log(Log.VERBOSE, o, msg, args);
    }

    private static void log(int priority, Object o, String message, Object... args) {
        log(priority, o, null, message, args);
    }

    private static void log(int priority, Object o, Throwable throwable, String message, Object... args) {
        final boolean force = priority >= Log.WARN;
        if (!force && !NEED_LOG) {
            return;
        }

        Log.println(priority, getTag(o), String.format(message, args));
        if (throwable != null) {
            throwable.printStackTrace();
        }
    }

    private static String getTag(final Object o) {
        return TAG + ((o instanceof Class) ? ((Class) o).getSimpleName() : o.getClass().getSimpleName());
    }
}
