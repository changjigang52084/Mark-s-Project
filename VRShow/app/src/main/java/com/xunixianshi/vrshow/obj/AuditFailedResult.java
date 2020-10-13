package com.xunixianshi.vrshow.obj;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-17
 * Time: 10:39
 * FIXME
 */
public class AuditFailedResult {

    private int resourceId;
    private String resourceName;
    private String smallIntroduce;
    private String coverImageUrl;
    private int resourcesScore;
    private int cumulativeNum;
    private String createTime;
    private int clientId;
    private String clientName;
    private String remark;
    private int isCollection;
    private int isLike;

    private String likesId; // 点赞ID，字符串，不存在时返回“-1”

    private int collectionId; // 收藏ID，整形，不存在时返回“-1”

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getSmallIntroduce() {
        return smallIntroduce;
    }

    public void setSmallIntroduce(String smallIntroduce) {
        this.smallIntroduce = smallIntroduce;
    }

    public String getCoverImageUrl() {
        return coverImageUrl;
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
    }

    public int getResourcesScore() {
        return resourcesScore;
    }

    public void setResourcesScore(int resourcesScore) {
        this.resourcesScore = resourcesScore;
    }

    public int getCumulativeNum() {
        return cumulativeNum;
    }

    public void setCumulativeNum(int cumulativeNum) {
        this.cumulativeNum = cumulativeNum;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getClientName() {
        return clientName;
    }

    public void setClientName(String clientName) {
        this.clientName = clientName;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
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