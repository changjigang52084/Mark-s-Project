package com.xunixianshi.vrshow.my.fragment.database;

/**
 * Created by Administrator on 2016/10/11.
 */

public class UploadBean {
    String uid;
    String resourceId;
    String imagePath = "";
    String resourceName = "";
    String md5ResourceName = "";
    String resourceIntroduce = "";
    String uploadProgress;
    String uploadResourcePath = "";
    String videoPath = "";
    String videoTypeId;
    String videoSize = "0";
    String uploadState;
    String videoFormat;
    String qiniuPath = "";
    boolean isDelete = false;

    public boolean isDelete() {
        return isDelete;
    }

    public void setDelete(boolean delete) {
        isDelete = delete;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getMd5ResourceName() {
        return md5ResourceName;
    }

    public void setMd5ResourceName(String md5ResourceName) {
        this.md5ResourceName = md5ResourceName;
    }

    public String getResourceIntroduce() {
        return resourceIntroduce;
    }

    public void setResourceIntroduce(String resourceIntroduce) {
        this.resourceIntroduce = resourceIntroduce;
    }

    public String getUploadProgress() {
        return uploadProgress;
    }

    public void setUploadProgress(String uploadProgress) {
        this.uploadProgress = uploadProgress;
    }

    public String getUploadResourcePath() {
        return uploadResourcePath;
    }

    public void setUploadResourcePath(String uploadResourcePath) {
        this.uploadResourcePath = uploadResourcePath;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public String getVideoTypeId() {
        return videoTypeId;
    }

    public void setVideoTypeId(String videoTypeId) {
        this.videoTypeId = videoTypeId;
    }

    public String getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(String videoSize) {
        this.videoSize = videoSize;
    }

    public String getUploadState() {
        return uploadState;
    }

    public void setUploadState(String uploadState) {
        this.uploadState = uploadState;
    }

    public String getVideoFormat() {
        return videoFormat;
    }

    public void setVideoFormat(String videoFormat) {
        this.videoFormat = videoFormat;
    }

    public String getQiniuPath() {
        return qiniuPath;
    }

    public void setQiniuPath(String qiniuPath) {
        this.qiniuPath = qiniuPath;
    }
}
