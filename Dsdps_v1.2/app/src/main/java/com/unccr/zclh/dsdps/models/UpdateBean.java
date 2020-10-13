package com.unccr.zclh.dsdps.models;

public class UpdateBean {

    private String appname;
    private int serverVersionCode;
    private String serverFlag;
    private String lastForce;
    private String updateurl;
    private String upgradeinfo;

    public String getAppname() {
        return appname;
    }

    public void setAppname(String appname) {
        this.appname = appname;
    }

    public int getServerVersionCode() {
        return serverVersionCode;
    }

    public void setServerVersionCode(int serverVersionCode) {
        this.serverVersionCode = serverVersionCode;
    }

    public String getServerFlag() {
        return serverFlag;
    }

    public void setServerFlag(String serverFlag) {
        this.serverFlag = serverFlag;
    }

    public String getLastForce() {
        return lastForce;
    }

    public void setLastForce(String lastForce) {
        this.lastForce = lastForce;
    }

    public String getUpdateurl() {
        return updateurl;
    }

    public void setUpdateurl(String updateurl) {
        this.updateurl = updateurl;
    }

    public String getUpgradeinfo() {
        return upgradeinfo;
    }

    public void setUpgradeinfo(String upgradeinfo) {
        this.upgradeinfo = upgradeinfo;
    }

}
