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

package com.hch.filedownloader.model;

/**
 * Created by duanchunlin on 11/26/15.
 *
 * @see com.hch.filedownloader.IFileDownloadMessage
 */
public class FileDownloadStatus {
    // [-2^7, 2^7 -1]
    public final static byte pending = 1;
    public final static byte connected = 2;
    public final static byte progress = 3;
    public final static byte blockComplete = 4;
    public final static byte retry = 5;
    public final static byte error = -1;
    public final static byte paused = -2;
    public final static byte completed = -3;
    public final static byte warn = -4;

    public final static byte MAX_INT = 5;
    public final static byte MIN_INT = -4;
    public final static byte INVALID_STATUS = 0;

    public static boolean isOver(final int status) {
        return status < 0;
    }

    public static boolean isIng(final int status) {
        return status >= pending && status <= retry;
    }
}
