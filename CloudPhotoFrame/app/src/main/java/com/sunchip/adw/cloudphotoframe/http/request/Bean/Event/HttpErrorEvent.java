package com.sunchip.adw.cloudphotoframe.http.request.Bean.Event;

public  class HttpErrorEvent<T>{

    private String result;
    private int err_code;
    private T data;

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getErr_code() {
        return err_code;
    }

    public void setErr_code(int err_code) {
        this.err_code = err_code;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
