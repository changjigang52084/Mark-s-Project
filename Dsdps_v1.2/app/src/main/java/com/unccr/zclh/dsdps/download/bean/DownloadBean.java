package com.unccr.zclh.dsdps.download.bean;

import java.util.List;

/**
 * 下载对象的bean
 *
 * @author changjigang
 */
public class DownloadBean {
    private int download_type;
    private List<String> downloadList;
    private String downloadFolder;
    private boolean flag;

    public int getDownload_type() {
        return download_type;
    }

    public void setDownload_type(int download_type) {
        this.download_type = download_type;
    }

    public List<String> getDownloadList() {
        return downloadList;
    }

    public void setDownloadList(List<String> downloadList) {
        this.downloadList = downloadList;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getDownloadFolder() {
        return downloadFolder;
    }

    public void setDownloadFolder(String downloadFolder) {
        this.downloadFolder = downloadFolder;
    }


}

