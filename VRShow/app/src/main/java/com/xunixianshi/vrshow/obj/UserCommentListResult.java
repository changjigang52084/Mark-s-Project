package com.xunixianshi.vrshow.obj;

/**
 * Created by xnxs-ptzx04 on 2016/11/3.
 */

public class UserCommentListResult {
    String resourceTitle;
    int resourceId;
    int resourceType;
    String resourceCoverImgUrl;
    int resourcePlayerTotal;
    String resourceSmallIntro;
    String resourcesIcon;
    int uploaderId;
    String uploaderName;
    int resourcesScore;
    int isFree;
    int likesTotal;

    private String likesId; // 点赞ID，字符串，不存在时返回“-1”

    private int collectionId; // 收藏ID，整形，不存在时返回“-1”

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

    public int getResourceType() {
        return resourceType;
    }

    public void setResourceType(int resourceType) {
        this.resourceType = resourceType;
    }

    public String getResourceCoverImgUrl() {
        return resourceCoverImgUrl;
    }

    public void setResourceCoverImgUrl(String resourceCoverImgUrl) {
        this.resourceCoverImgUrl = resourceCoverImgUrl;
    }

    public int getResourcePlayerTotal() {
        return resourcePlayerTotal;
    }

    public void setResourcePlayerTotal(int resourcePlayerTotal) {
        this.resourcePlayerTotal = resourcePlayerTotal;
    }

    public String getResourceSmallIntro() {
        return resourceSmallIntro;
    }

    public void setResourceSmallIntro(String resourceSmallIntro) {
        this.resourceSmallIntro = resourceSmallIntro;
    }

    public String getResourcesIcon() {
        return resourcesIcon;
    }

    public void setResourcesIcon(String resourcesIcon) {
        this.resourcesIcon = resourcesIcon;
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

    public int getResourcesScore() {
        return resourcesScore;
    }

    public void setResourcesScore(int resourcesScore) {
        this.resourcesScore = resourcesScore;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public int getLikesTotal() {
        return likesTotal;
    }

    public void setLikesTotal(int likesTotal) {
        this.likesTotal = likesTotal;
    }

    public String getLikesId() {
        return likesId;
    }

    public void setLikesId(String likesId) {
        this.likesId = likesId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }
}
