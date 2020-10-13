package com.sunchip.adw.cloudphotoframe.http.request.Bean;

public class BaseErrarEvent {
    private String result;
    private int code;

    public BaseErrarEvent(String result,int code){
        this.result = result;
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }
}
