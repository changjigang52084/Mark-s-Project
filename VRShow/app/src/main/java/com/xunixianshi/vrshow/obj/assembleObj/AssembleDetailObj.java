package com.xunixianshi.vrshow.obj.assembleObj;

import com.xunixianshi.vrshow.obj.HttpObj;

/**
 * Created by duan on 2016/9/29.
 * 合集详情
 *
 compilationId：合集ID，非空，字符串
 userId：合集用户ID(合集创建者)，非空，整型
 userName：合集用户名称(合集创建者)，非空，字符串
 compilationName：合集名称，非空，字符串
 compilationIntro：合集介绍内容，非空，字符串
 coverImgUrl：合集封面图URL地址，非空，字符串
 compilationReadTotal：合集阅读量/点击量，非空，整型
 compilationResourceTotal：合集包含资源总量，非空，整数
 createTime：合集创建时间，非空，字符串
 compilationReviewedRemark：合集审核备注，可为空，字符串
 compilationReviewedStatus：合集审核状态，非空，整数
                             0表示未审核
                             1表示审核成功
                             2表示审核失败
 */

public class AssembleDetailObj extends HttpObj {

    String compilationId;
    String userName;
    String compilationName;
    String compilationIntro;
    String coverImgUrl;
    int userId;
    int compilationReadTotal;
    int compilationResourceTotal;

    String createTime;
    String compilationReviewedRemark;
    int compilationReviewedStatus;


    public String getCompilationId() {
        return compilationId;
    }

    public void setCompilationId(String compilationId) {
        this.compilationId = compilationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCompilationReadTotal() {
        return compilationReadTotal;
    }

    public void setCompilationReadTotal(int compilationReadTotal) {
        this.compilationReadTotal = compilationReadTotal;
    }

    public int getCompilationResourceTotal() {
        return compilationResourceTotal;
    }

    public void setCompilationResourceTotal(int compilationResourceTotal) {
        this.compilationResourceTotal = compilationResourceTotal;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCompilationReviewedRemark() {
        return compilationReviewedRemark;
    }

    public void setCompilationReviewedRemark(String compilationReviewedRemark) {
        this.compilationReviewedRemark = compilationReviewedRemark;
    }

    public int getCompilationReviewedStatus() {
        return compilationReviewedStatus;
    }

    public void setCompilationReviewedStatus(int compilationReviewedStatus) {
        this.compilationReviewedStatus = compilationReviewedStatus;
    }
}
