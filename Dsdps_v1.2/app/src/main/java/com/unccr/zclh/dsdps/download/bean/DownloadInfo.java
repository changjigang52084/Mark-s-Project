package com.unccr.zclh.dsdps.download.bean;

/**
 * 下载信息的类
 * @author changjigang
 *
 */
public class DownloadInfo {
    /**
     * 获取下载文件的总个数
     * @return
     */
    public int getCount() {
        return count;
    }
    public void setCount(int count) {
        this.count = count;
    }
    /**
     * 获取下载了多少个文件
     * @return
     */
    public int getIndex() {
        return index;
    }
    public void setIndex(int index) {
        this.index = index;
    }
    /**下载文件的地址*/
    private String downloadUrl;
    /**文件下载的进度*/
    private int downloadProgress = 0;
    /**文件的大小*/
    private int downloadFileSize = 0;
    /**下载速度kb*/
    private int speed = 0;
    /**百分比*/
    private int percentage = 0;
    /**下载文件的总个数*/
    private int count = 0;
    /**下载了文件的个数*/
    private int index = 0;
    /**下载的状态,END,DOWNLOADING,FAIL*/
    public enum STATE{
        END,DOWNLOADING,FAIL
    }
    private STATE state;
    /**
     * 获取下载地址
     * @return
     */
    public String getDownloadUrl() {
        return downloadUrl;
    }
    /***
     * 设置下载地址
     * @param downloadUrl
     */
    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }
    /**
     * 获取下载的进度
     * @return
     */
    public int getDownloadProgress() {
        return downloadProgress;
    }
    public void setDownloadProgress(int downloadProgress) {
        this.downloadProgress = downloadProgress;
    }
    /**
     * 获取下载文件的大小
     * @return
     */
    public int getDownloadFileSize() {
        return downloadFileSize;
    }
    public void setDownloadFileSize(int downloadFileSize) {
        this.downloadFileSize = downloadFileSize;
    }
    /**
     * 获取下载速度
     * @return
     */
    public int getSpeed() {
        return speed;
    }
    public void setSpeed(int speed) {
        this.speed = speed;
    }
    /**
     * 获取下载的百分比
     * @return
     */
    public int getPercentage() {
        return percentage;
    }
    public void setPercentage(int percentage) {
        this.percentage = percentage;
    }
    /**
     * 设置下载进度的状态
     * END,DOWNLOADING,FAIL
     */
    public void setDownloadState(STATE state) {
        this.state = state;
    }
    /**
     * 获取下载状态
     * @return
     */
    public STATE getDownloadState() {
        return state;
    }

}
