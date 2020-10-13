package com.unccr.zclh.dsdps.download.bean;

import com.unccr.zclh.dsdps.download.interfaces.IDownloadStateCallback;

import java.util.List;

public class DownloadListBo {

    public List<String> downloadUrlList;
    public IDownloadStateCallback progressListener;
    public String downloadFolder;
    public int type;

    public DownloadListBo(List<String> downloadUrlList, IDownloadStateCallback progressListener, String downloadFolder, int type) {
        this.downloadUrlList = downloadUrlList;
        this.progressListener = progressListener;
        this.downloadFolder = downloadFolder;
        this.type = type;
    }
}
