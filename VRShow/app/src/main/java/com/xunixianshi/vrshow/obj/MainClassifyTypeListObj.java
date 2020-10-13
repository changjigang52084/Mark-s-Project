package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by xnxs-ptzx04 on 2016/12/7.
 */

public class MainClassifyTypeListObj implements Serializable {
    String resourceTitle;
    int resourceId;
    String converImgUrl;
    String smallIntroduce;
    String longIntroduce;
    int uploaderId;
    String uploaderName;
    String uploaderIcon;
    int resourcesType;
    int resourcePlayType;
    int sumTotal;
    int resourceTotalDownload;
    int resourceTotalPlayer;
    int resourceTotalLikes;
    int isCollection;
    int isLike;
    String likesId;
    int collectionId;

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

    public String getConverImgUrl() {
        return converImgUrl;
    }

    public void setConverImgUrl(String converImgUrl) {
        this.converImgUrl = converImgUrl;
    }

    public String getSmallIntroduce() {
        return smallIntroduce;
    }

    public void setSmallIntroduce(String smallIntroduce) {
        this.smallIntroduce = smallIntroduce;
    }

    public String getLongIntroduce() {
        return longIntroduce;
    }

    public void setLongIntroduce(String longIntroduce) {
        this.longIntroduce = longIntroduce;
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

    public int getResourcesType() {
        return resourcesType;
    }

    public void setResourcesType(int resourcesType) {
        this.resourcesType = resourcesType;
    }

    public int getResourcePlayType() {
        return resourcePlayType;
    }

    public void setResourcePlayType(int resourcePlayType) {
        this.resourcePlayType = resourcePlayType;
    }

    public int getSumTotal() {
        return sumTotal;
    }

    public void setSumTotal(int sumTotal) {
        this.sumTotal = sumTotal;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }

    public int getResourceTotalPlayer() {
        return resourceTotalPlayer;
    }

    public void setResourceTotalPlayer(int resourceTotalPlayer) {
        this.resourceTotalPlayer = resourceTotalPlayer;
    }

    public int getResourceTotalLikes() {
        return resourceTotalLikes;
    }

    public void setResourceTotalLikes(int resourceTotalLikes) {
        this.resourceTotalLikes = resourceTotalLikes;
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

    public int getResourceTotalDownload() {
        return resourceTotalDownload;
    }

    public void setResourceTotalDownload(int resourceTotalDownload) {
        this.resourceTotalDownload = resourceTotalDownload;
    }
}
