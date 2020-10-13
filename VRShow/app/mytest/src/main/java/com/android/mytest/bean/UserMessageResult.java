package com.android.mytest.bean;

import java.io.Serializable;

public class UserMessageResult implements Serializable {

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     * 以实际返回参数为准
    userName：用户名，非空，字符串
    userIconUrl：用户头像地址，非空，字符串
    userPersonalProfile：用户简介/修改签名，可为空，字符串
    userPopularity：用户人气，非空，整数
    levelTotal：等级上限，非空，正整数，暂时返回50
    userLevel：用户等级，非空，正整数，暂时返回1
    userLevelTitle：用户等级名称(用于显示)，非空，字符串
    userResourceTotal：用户资源数，非空，整数
    userFollowTotal：用户关注数，非空，整数
    userFansTotal：用户粉丝数，非空，整数
    userMessageTotal：用户留言数，非空，整数
    userMessageOtherTotal：他人留言数，非空，整数
    userStruggleTotal：用户合集数，非空，整数
    userActivityTotal：用户参加活动数，非空，整数
    usercollectionsTotal：用户资源收藏量，非空，整数
    userPlayerTotal：用户播放历史数，非空，整数
    userShareTotal：用户分享内容数，非空，整数
    userCommentTotal：用户评论数，非空，整数
    userDownloadTotal：用户下载数，非空，整数，客户端禁用当前字段值，自行统计本地数据值
     */

    /**
     * UserMessageResult
     * {resCode='0',
     * resDesc='查询成功',
     * userName='梅川擂酷',
     * userIconUrl='https://xnxs.img.vrshow.com/f18e3ffafe53fee2.png',
     * userPersonalProfile='还没有写签名，赶紧去写一个威武霸气的签名吧！',
     * userPopularity=212,
     * levelTotal=50,
     * userLevel=1,
     * userLevelTitle='LV1',
     * isFollow=0,
     * userResourceTotal=28,
     * userFollowTotal=1,
     * userFansTotal=3,
     * userMessageTotal=4,
     * userMessageOtherTotal=3,
     * userStruggleTotal=0,
     * userActivityTotal=2,
     * usercollectionsTotal=12,
     * userPlayerTotal=133,
     * userShareTotal=16,
     * userCommentTotal=19,
     * userDownloadTotal=0}
     */

    private static final long serialVersionUID = 1L;
    String resCode;
    String resDesc;
    String userName;
    String userIconUrl;
    String userPersonalProfile;
    int userPopularity;
    int levelTotal;
    int userLevel;
    String userLevelTitle;

    int isFollow;

    int userResourceTotal;
    int userFollowTotal;
    int userFansTotal;
    int userMessageTotal;
    int userMessageOtherTotal;
    int userStruggleTotal;
    int userActivityTotal;
    int usercollectionsTotal;
    int userPlayerTotal;
    int userShareTotal;
    int userCommentTotal;
    int userDownloadTotal;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIconUrl() {
        return userIconUrl;
    }

    public void setUserIconUrl(String userIconUrl) {
        this.userIconUrl = userIconUrl;
    }

    public String getUserPersonalProfile() {
        return userPersonalProfile;
    }

    public void setUserPersonalProfile(String userPersonalProfile) {
        this.userPersonalProfile = userPersonalProfile;
    }
    public int getIsFollow() {
        return isFollow;
    }

    public void setIsFollow(int isFollow) {
        this.isFollow = isFollow;
    }


    public int getUserPopularity() {
        return userPopularity;
    }

    public void setUserPopularity(int userPopularity) {
        this.userPopularity = userPopularity;
    }

    public int getLevelTotal() {
        return levelTotal;
    }

    public void setLevelTotal(int levelTotal) {
        this.levelTotal = levelTotal;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public String getUserLevelTitle() {
        return userLevelTitle;
    }

    public void setUserLevelTitle(String userLevelTitle) {
        this.userLevelTitle = userLevelTitle;
    }


    public int getUserResourceTotal() {
        return userResourceTotal;
    }

    public void setUserResourceTotal(int userResourceTotal) {
        this.userResourceTotal = userResourceTotal;
    }

    public int getUserFollowTotal() {
        return userFollowTotal;
    }

    public void setUserFollowTotal(int userFollowTotal) {
        this.userFollowTotal = userFollowTotal;
    }

    public int getUserFansTotal() {
        return userFansTotal;
    }

    public void setUserFansTotal(int userFansTotal) {
        this.userFansTotal = userFansTotal;
    }

    public int getUserMessageTotal() {
        return userMessageTotal;
    }

    public void setUserMessageTotal(int userMessageTotal) {
        this.userMessageTotal = userMessageTotal;
    }

    public int getUserMessageOtherTotal() {
        return userMessageOtherTotal;
    }

    public void setUserMessageOtherTotal(int userMessageOtherTotal) {
        this.userMessageOtherTotal = userMessageOtherTotal;
    }

    public int getUserStruggleTotal() {
        return userStruggleTotal;
    }

    public void setUserStruggleTotal(int userStruggleTotal) {
        this.userStruggleTotal = userStruggleTotal;
    }

    public int getUserActivityTotal() {
        return userActivityTotal;
    }

    public void setUserActivityTotal(int userActivityTotal) {
        this.userActivityTotal = userActivityTotal;
    }

    public int getUsercollectionsTotal() {
        return usercollectionsTotal;
    }

    public void setUsercollectionsTotal(int usercollectionsTotal) {
        this.usercollectionsTotal = usercollectionsTotal;
    }

    public int getUserPlayerTotal() {
        return userPlayerTotal;
    }

    public void setUserPlayerTotal(int userPlayerTotal) {
        this.userPlayerTotal = userPlayerTotal;
    }

    public int getUserShareTotal() {
        return userShareTotal;
    }

    public void setUserShareTotal(int userShareTotal) {
        this.userShareTotal = userShareTotal;
    }

    public int getUserCommentTotal() {
        return userCommentTotal;
    }

    public void setUserCommentTotal(int userCommentTotal) {
        this.userCommentTotal = userCommentTotal;
    }

    public int getUserDownloadTotal() {
        return userDownloadTotal;
    }

    public void setUserDownloadTotal(int userDownloadTotal) {
        this.userDownloadTotal = userDownloadTotal;
    }

    @Override
    public String toString() {
        return "UserMessageResult{" +
                "resCode='" + resCode + '\'' +
                ", resDesc='" + resDesc + '\'' +
                ", userName='" + userName + '\'' +
                ", userIconUrl='" + userIconUrl + '\'' +
                ", userPersonalProfile='" + userPersonalProfile + '\'' +
                ", userPopularity=" + userPopularity +
                ", levelTotal=" + levelTotal +
                ", userLevel=" + userLevel +
                ", userLevelTitle='" + userLevelTitle + '\'' +
                ", isFollow=" + isFollow +
                ", userResourceTotal=" + userResourceTotal +
                ", userFollowTotal=" + userFollowTotal +
                ", userFansTotal=" + userFansTotal +
                ", userMessageTotal=" + userMessageTotal +
                ", userMessageOtherTotal=" + userMessageOtherTotal +
                ", userStruggleTotal=" + userStruggleTotal +
                ", userActivityTotal=" + userActivityTotal +
                ", usercollectionsTotal=" + usercollectionsTotal +
                ", userPlayerTotal=" + userPlayerTotal +
                ", userShareTotal=" + userShareTotal +
                ", userCommentTotal=" + userCommentTotal +
                ", userDownloadTotal=" + userDownloadTotal +
                '}';
    }
}
