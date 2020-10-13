package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/8/31.
 */
public abstract class HttpObj implements Serializable {
    String resCode;
    String resDesc;

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
}
