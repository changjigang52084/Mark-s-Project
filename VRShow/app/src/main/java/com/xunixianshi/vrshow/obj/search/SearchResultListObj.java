package com.xunixianshi.vrshow.obj.search;

/**
 * Created by Administrator on 2016/9/29.
 */

public class SearchResultListObj {

    int resourceId;
    String resourceName;
    String smallIntroduce;
    String longIntroduce;
    String coverImgUrl;
    int resourcesScore;
    int cumulativeNum;
    String createTime;
    int parentTypeId;
    String parentTypeName;

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

    public String getLongIntroduce() {
        return longIntroduce;
    }

    public void setLongIntroduce(String longIntroduce) {
        this.longIntroduce = longIntroduce;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
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

    public int getParentTypeId() {
        return parentTypeId;
    }

    public void setParentTypeId(int parentTypeId) {
        this.parentTypeId = parentTypeId;
    }

    public String getParentTypeName() {
        return parentTypeName;
    }

    public void setParentTypeName(String parentTypeName) {
        this.parentTypeName = parentTypeName;
    }
}
