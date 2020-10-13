package com.xunixianshi.vrshow.my.localVideo.entity;

import android.graphics.Bitmap;

import java.io.Serializable;

/**
 * Created by markIron on 2016/9/7.
 */
public class VideoBean implements Serializable {

    /**
     * 变量描述
     */
    private static final long serialVersionUID = 1L;
    /**
     * 视频id
     */
    public String videoId;
    /**
     * 视频路径
     */
    public String videoPath;
    /**
     * 视频位图
     */
    private Bitmap videoBitmap;
    /**
     * 视频标题
     */
    public String videoTitle;
    /**
     * 视频大小
     */
    public int videoSize;

    /**
     * 视频时间
     */
    public String time;

    /**
     * 已上传进度
     */
    public double percent;

    /**
     * 已上传大小
     */
    public int hasUploadSize;

    /**
     * 已经上传的进度
     */
    public int hasUploadProgress;

    /**
     * 视频百分比
     */

    public VideoBean() {
    }

    public VideoBean(String videoId, String videoPath, Bitmap videoBitmap, String videoTitle, int videoSize,
                     String time, double percent,int hasUploadSize,int hasUploadProgress) {
        this.videoId = videoId;
        this.videoPath = videoPath;
        this.videoBitmap = videoBitmap;
        this.videoTitle = videoTitle;
        this.videoSize = videoSize;
        this.time = time;
        this.percent = percent;
        this.hasUploadSize = hasUploadSize;
        this.hasUploadProgress = hasUploadProgress;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoPath() {
        return videoPath;
    }

    public void setVideoPath(String videoPath) {
        this.videoPath = videoPath;
    }

    public Bitmap getVideoBitmap() {
        return videoBitmap;
    }

    public void setVideoBitmap(Bitmap videoBitmap) {
        this.videoBitmap = videoBitmap;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public int getVideoSize() {
        return videoSize;
    }

    public void setVideoSize(int videoSize) {
        this.videoSize = videoSize;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public double getPercent() {
        return percent;
    }

    public void setPercent(double percent) {
        this.percent = percent;
    }

    public int getHasUploadSize() {
        return hasUploadSize;
    }

    public void setHasUploadSize(int hasUploadSize) {
        this.hasUploadSize = hasUploadSize;
    }

    public int getHasUploadProgress() {
        return hasUploadProgress;
    }

    public void setHasUploadProgress(int hasUploadProgress) {
        this.hasUploadProgress = hasUploadProgress;
    }
}
