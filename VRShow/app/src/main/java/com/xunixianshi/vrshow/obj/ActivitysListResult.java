package com.xunixianshi.vrshow.obj;

/**
 * Created by xnxs-ptzx04 on 2016/10/31.
 */

public class ActivitysListResult {
    int activeId;
    String activeTitle;
    String activeSubTitle;
    String activeCoverUrl;
    String activeTargetUrl;
    int activeCurrStatus;
    String activeStartTime;
    String activeEndTime;
    int joinUserTotal;
    int isUserLogin;
    String createTime;

    public int getActiveId() {
        return activeId;
    }

    public void setActiveId(int activeId) {
        this.activeId = activeId;
    }

    public String getActiveTitle() {
        return activeTitle;
    }

    public void setActiveTitle(String activeTitle) {
        this.activeTitle = activeTitle;
    }

    public String getActiveSubTitle() {
        return activeSubTitle;
    }

    public void setActiveSubTitle(String activeSubTitle) {
        this.activeSubTitle = activeSubTitle;
    }

    public String getActiveCoverUrl() {
        return activeCoverUrl;
    }

    public void setActiveCoverUrl(String activeCoverUrl) {
        this.activeCoverUrl = activeCoverUrl;
    }

    public String getActiveTargetUrl() {
        return activeTargetUrl;
    }

    public void setActiveTargetUrl(String activeTargetUrl) {
        this.activeTargetUrl = activeTargetUrl;
    }

    public int getActiveCurrStatus() {
        return activeCurrStatus;
    }

    public void setActiveCurrStatus(int activeCurrStatus) {
        this.activeCurrStatus = activeCurrStatus;
    }

    public String getActiveStartTime() {
        return activeStartTime;
    }

    public void setActiveStartTime(String activeStartTime) {
        this.activeStartTime = activeStartTime;
    }

    public int getJoinUserTotal() {
        return joinUserTotal;
    }

    public void setJoinUserTotal(int joinUserTotal) {
        this.joinUserTotal = joinUserTotal;
    }

    public String getActiveEndTime() {
        return activeEndTime;
    }

    public void setActiveEndTime(String activeEndTime) {
        this.activeEndTime = activeEndTime;
    }

    public int getIsUserLogin() {
        return isUserLogin;
    }

    public void setIsUserLogin(int isUserLogin) {
        this.isUserLogin = isUserLogin;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
