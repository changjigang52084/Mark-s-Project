package com.xunixianshi.vrshow.obj;

/**
 * Created by markIron on 2016/9/27.
 */

public class ResourcesCommentResultList {

    private int resourcesCommentId;
    private String userId;
    private String userName;
    private String userIcon;
    private int resourceId;
    private int commentScore;
    private String commentTime;
    private String commentIp;
    private String commentContent;

    public int getResourcesCommentId() {
        return resourcesCommentId;
    }

    public void setResourcesCommentId(int resourcesCommentId) {
        this.resourcesCommentId = resourcesCommentId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserIcon() {
        return userIcon;
    }

    public void setUserIcon(String userIcon) {
        this.userIcon = userIcon;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public int getCommentScore() {
        return commentScore;
    }

    public void setCommentScore(int commentScore) {
        this.commentScore = commentScore;
    }

    public String getCommentTime() {
        return commentTime;
    }

    public void setCommentTime(String commentTime) {
        this.commentTime = commentTime;
    }

    public String getCommentIp() {
        return commentIp;
    }

    public void setCommentIp(String commentIp) {
        this.commentIp = commentIp;
    }

    public String getCommentContent() {
        return commentContent;
    }

    public void setCommentContent(String commentContent) {
        this.commentContent = commentContent;
    }
}
