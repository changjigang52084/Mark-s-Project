package com.xunixianshi.vrshow.player.object;

import java.io.Serializable;

/**
 * Created by duanchunlin on 2016/8/15.
 */
public class VideoInfo implements Serializable {

    public String getVideoName() {
        return videoName;
    }

    public void setVideoName(String videoName) {
        this.videoName = videoName;
    }

    public String getVideoUrl() {
        return videoUrl;
    }

    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }

    public int getVideoType() {
        return videoType;
    }

    public void setVideoType(int videoType) {
        this.videoType = videoType;
    }

    public int getSourceId() {
        return sourceId;
    }

    public void setSourceId(int sourceId) {
        this.sourceId = sourceId;
    }

    public int getLastProgress() {
        return lastProgress;
    }

    public void setLastProgress(int lastProgress) {
        this.lastProgress = lastProgress;
    }

    public boolean isNeedNet() {
        return isNeedNet;
    }

    public void setNeedNet(boolean needNet) {
        isNeedNet = needNet;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    private String videoName;
    private String videoUrl;
    private int videoType;
    private int sourceId;
    private int lastProgress;
    private boolean isNeedNet;
    private String errorMsg = "";
}
