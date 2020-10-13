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

import com.hch.filedownloader.event.DownloadEventPoolImpl;
import com.hch.filedownloader.event.IDownloadEvent;

/**
 * Event pool for :filedownloader process
 * <p/>
 * Created by Jacksgong on 12/26/15.
 */
class FileDownloadProcessEventPool extends DownloadEventPoolImpl {

    private static class HolderClass {
        private final static FileDownloadProcessEventPool INSTANCE = new FileDownloadProcessEventPool();
    }

    private FileDownloadProcessEventPool() {
        super();
    }

    static FileDownloadProcessEventPool getImpl() {
        return HolderClass.INSTANCE;
    }

    // All status change through this pool to make sure linear schedule
    @Override
    public void asyncPublishInNewThread(IDownloadEvent event) {
        super.asyncPublishInNewThread(event);
    }
}
