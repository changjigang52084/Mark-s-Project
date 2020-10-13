package com.lzkj.downloadservice.interfaces;

import java.util.ArrayList;

public interface IBucketFileListCallback {
        /**
         * 设置文件列表
         * @param fileList
         */
        void setupFileList(ArrayList<String> fileList);
    }