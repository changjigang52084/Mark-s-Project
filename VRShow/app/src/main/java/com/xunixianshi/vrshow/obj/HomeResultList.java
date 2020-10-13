package com.xunixianshi.vrshow.obj;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeResultList implements Serializable {

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = 1L;

    String resourceTitle;
    int resourceId;
    String resourceSmallIntro;
    String coverImgUrl;
    int uploaderId;
    String uploaderName;
    String uploaderIcon;
    int resourceScore;
    int downloadTotal;
    int playerTotal;
    int commentTotal;
    int likesTotal;
    int isFree;
    String createTime;

    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceSmallIntro() {
        return resourceSmallIntro;
    }

    public void setResourceSmallIntro(String resourceSmallIntro) {
        this.resourceSmallIntro = resourceSmallIntro;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public String getUploaderIcon() {
        return uploaderIcon;
    }

    public void setUploaderIcon(String uploaderIcon) {
        this.uploaderIcon = uploaderIcon;
    }

    public int getResourceScore() {
        return resourceScore;
    }

    public void setResourceScore(int resourceScore) {
        this.resourceScore = resourceScore;
    }

    public int getDownloadTotal() {
        return downloadTotal;
    }

    public void setDownloadTotal(int downloadTotal) {
        this.downloadTotal = downloadTotal;
    }

    public int getPlayerTotal() {
        return playerTotal;
    }

    public void setPlayerTotal(int playerTotal) {
        this.playerTotal = playerTotal;
    }

    public int getCommentTotal() {
        return commentTotal;
    }

    public void setCommentTotal(int commentTotal) {
        this.commentTotal = commentTotal;
    }

    public int getLikesTotal() {
        return likesTotal;
    }

    public void setLikesTotal(int likesTotal) {
        this.likesTotal = likesTotal;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
