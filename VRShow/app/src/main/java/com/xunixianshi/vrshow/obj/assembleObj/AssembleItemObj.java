package com.xunixianshi.vrshow.obj.assembleObj;

import java.io.Serializable;


/**
 * 合集列表Item
 *@author DuanChunLin
 *@time 2016/10/12 18:37
 */
public class AssembleItemObj implements Serializable {
    String userCompilationId;
    String compilationName;
    String compilationIntro;
    String coverImgUrl;
    int readTotal;
    int reviewedStatus;
    String reviewedRemark;
    String createTime;
    int resourceReadTotal;

    public String getUserCompilationId() {
        return userCompilationId;
    }

    public void setUserCompilationId(String userCompilationId) {
        this.userCompilationId = userCompilationId;
    }

    public String getCompilationName() {
        return compilationName;
    }

    public void setCompilationName(String compilationName) {
        this.compilationName = compilationName;
    }

    public String getCompilationIntro() {
        return compilationIntro;
    }

    public void setCompilationIntro(String compilationIntro) {
        this.compilationIntro = compilationIntro;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public int getReadTotal() {
        return readTotal;
    }

    public void setReadTotal(int readTotal) {
        this.readTotal = readTotal;
    }

    public int getReviewedStatus() {
        return reviewedStatus;
    }

    public void setReviewedStatus(int reviewedStatus) {
        this.reviewedStatus = reviewedStatus;
    }

    public String getReviewedRemark() {
        return reviewedRemark;
    }

    public void setReviewedRemark(String reviewedRemark) {
        this.reviewedRemark = reviewedRemark;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public int getResourceReadTotal() {
        return resourceReadTotal;
    }

    public void setResourceReadTotal(int resourceReadTotal) {
        this.resourceReadTotal = resourceReadTotal;
    }
}
