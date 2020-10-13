package com.xunixianshi.vrshow.obj;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-12
 * Time: 16:34
 * FIXME 根据用户ID查询用户信息实体类
 */
public class QueryUserInformationObj extends HttpObj {

    private String userId; // RSA加密后的用户ID
    private String userName; // 加密后的用户名
    private String userMobile; // RSA加密后的手机号
    private String userEmail; // null
    private String userIdCard; // null
    private String realName; // "-1"
    private int payValidateType; // 0
    private String userIcon; // "-1"
    private int userSex; // "-1"
    private String userDateBirth; // "-1"
    private String userQq; // "-1"
    private String userPersonalProfile; // "-1"
    private String registerDate; // 注册日期
    private String registerIp; // 注册IP
    private int registerClientType; // 1
    private String lastLoginTime; // null
    private String lastLoginIp; // null
    private int userNameModifyNum; // 1
    private String identity; // VRSHOW会吧
    private int userStatus; // 1
    private String residenceAreaName; // 中国广东省深圳市宝安区新安街道
    private String residenceAreaDetail; // 宝安体育馆
    private int userLevel; // 用户等级
    private int userLevelUpperLimit; // 用户等级上限

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

    public String getUserMobile() {
        return userMobile;
    }

    public void setUserMobile(String userMobile) {
        this.userMobile = userMobile;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserIdCard() {
        return userIdCard;
    }

    public void setUserIdCard(String userIdCard) {
        this.userIdCard = userIdCard;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public int getPayValidateType() {
        return payValidateType;
    }

    public void setPayValidateType(int payValidateType) {
        this.payValidateType = payValidateType;
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

    public String getUserDateBirth() {
        return userDateBirth;
    }

    public void setUserDateBirth(String userDateBirth) {
        this.userDateBirth = userDateBirth;
    }

    public String getUserQq() {
        return userQq;
    }

    public void setUserQq(String userQq) {
        this.userQq = userQq;
    }

    public String getUserPersonalProfile() {
        return userPersonalProfile;
    }

    public void setUserPersonalProfile(String userPersonalProfile) {
        this.userPersonalProfile = userPersonalProfile;
    }

    public String getRegisterDate() {
        return registerDate;
    }

    public void setRegisterDate(String registerDate) {
        this.registerDate = registerDate;
    }

    public String getRegisterIp() {
        return registerIp;
    }

    public void setRegisterIp(String registerIp) {
        this.registerIp = registerIp;
    }

    public int getRegisterClientType() {
        return registerClientType;
    }

    public void setRegisterClientType(int registerClientType) {
        this.registerClientType = registerClientType;
    }

    public String getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(String lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public String getLastLoginIp() {
        return lastLoginIp;
    }

    public void setLastLoginIp(String lastLoginIp) {
        this.lastLoginIp = lastLoginIp;
    }

    public int getUserNameModifyNum() {
        return userNameModifyNum;
    }

    public void setUserNameModifyNum(int userNameModifyNum) {
        this.userNameModifyNum = userNameModifyNum;
    }

    public String getIdentity() {
        return identity;
    }

    public void setIdentity(String identity) {
        this.identity = identity;
    }

    public int getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(int userStatus) {
        this.userStatus = userStatus;
    }

    public String getResidenceAreaName() {
        return residenceAreaName;
    }

    public void setResidenceAreaName(String residenceAreaName) {
        this.residenceAreaName = residenceAreaName;
    }

    public String getResidenceAreaDetail() {
        return residenceAreaDetail;
    }

    public void setResidenceAreaDetail(String residenceAreaDetail) {
        this.residenceAreaDetail = residenceAreaDetail;
    }

    public int getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(int userLevel) {
        this.userLevel = userLevel;
    }

    public int getUserLevelUpperLimit() {
        return userLevelUpperLimit;
    }

    public void setUserLevelUpperLimit(int userLevelUpperLimit) {
        this.userLevelUpperLimit = userLevelUpperLimit;
    }
}