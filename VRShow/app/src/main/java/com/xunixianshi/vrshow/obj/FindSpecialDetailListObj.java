package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/27.
 * 查询接口
 */

public class FindSpecialDetailListObj implements Serializable {
    String resourceTitle;
    int resourceId;
    String resourceCoverImgUrl;
    int resourcePlayerTotal;
    String resourceSmallIntro;
    String resourcesIcon;
    int uploaderId;
    String uploaderName;
    int resourcesScore;
    int isFree;
    int likesTotal;
    int isCopyright;
    int copyrightTagId;
    String copyrightTagName;
    private int isCollection;
    private int isLike;

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

    public int getIsCopyright() {
        return isCopyright;
    }

    public void setIsCopyright(int isCopyright) {
        this.isCopyright = isCopyright;
    }

    public int getCopyrightTagId() {
        return copyrightTagId;
    }

    public void setCopyrightTagId(int copyrightTagId) {
        this.copyrightTagId = copyrightTagId;
    }

    public String getCopyrightTagName() {
        return copyrightTagName;
    }

    public void setCopyrightTagName(String copyrightTagName) {
        this.copyrightTagName = copyrightTagName;
    }

    public int getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(int isCollection) {
        this.isCollection = isCollection;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
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
