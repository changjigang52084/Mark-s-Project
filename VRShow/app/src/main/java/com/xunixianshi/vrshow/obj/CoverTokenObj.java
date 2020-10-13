package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by markIron on 2016/9/19.
 */
public class CoverTokenObj implements Serializable {

    private static final long serialVersionUID = 1L;
    String resCode;
    String resDesc;
    String token;
    String fileName;

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

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
}
