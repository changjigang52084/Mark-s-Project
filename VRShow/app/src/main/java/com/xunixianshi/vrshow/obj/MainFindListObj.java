package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/27.
 */

public class MainFindListObj implements Serializable {
    String specialId;
    int clientId;
    String specialName;
    String coverImgUrl;
    String specialIntro;
    int readTotal;
    int specialResourceTotal;
    String createTime;

    public String getSpecialId() {
        return specialId;
    }

    public void setSpecialId(String specialId) {
        this.specialId = specialId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public String getSpecialName() {
        return specialName;
    }

    public void setSpecialName(String specialName) {
        this.specialName = specialName;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getSpecialIntro() {
        return specialIntro;
    }

    public void setSpecialIntro(String specialIntro) {
        this.specialIntro = specialIntro;
    }

    public int getReadTotal() {
        return readTotal;
    }

    public void setReadTotal(int readTotal) {
        this.readTotal = readTotal;
    }

    public int getSpecialResourceTotal() {
        return specialResourceTotal;
    }

    public void setSpecialResourceTotal(int specialResourceTotal) {
        this.specialResourceTotal = specialResourceTotal;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
