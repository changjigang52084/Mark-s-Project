package com.unccr.zclh.dsdps.download.interfaces;

import java.util.List;

public interface QiNiuDownloadTokenCallback {

    /**
     * 根据token url下载文件
     * @param list
     * 			一组下载文件的带token的url
     * @param type
     * 			下载文件的类型
     * @param downloadFolder
     * 			文件下载路径(文件夹)
     */
    void downloadTokenList(List<String> list, int type, String downloadFolder);
}
