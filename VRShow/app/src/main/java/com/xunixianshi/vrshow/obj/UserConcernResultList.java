package com.xunixianshi.vrshow.obj;

/**
 * Created by markIron on 2016/9/26.
 */

public class UserConcernResultList {

    private String userId;
    private String userName;
    private String userIcon;
    private int userSex;
    private String updateRemark;
    private String affentionTime;

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

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getUpdateRemark() {
        return updateRemark;
    }

    public void setUpdateRemark(String updateRemark) {
        this.updateRemark = updateRemark;
    }

    public String getAffentionTime() {
        return affentionTime;
    }

    public void setAffentionTime(String affentionTime) {
        this.affentionTime = affentionTime;
    }
}
