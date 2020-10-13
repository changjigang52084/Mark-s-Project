package com.lzkj.aidlservice.bo;

/**
 * Created by kchang on 2016/10/17.
 */
public class HttpBo {
    private String httpUrl;
    private String httpRequestParam;
    private String httpMethod;
    private String httpRequestTag;

    public String getHttpUrl() {
        return httpUrl;
    }

    public void setHttpUrl(String httpUrl) {
        this.httpUrl = httpUrl;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public void setHttpMethod(String httpMethod) {
        this.httpMethod = httpMethod;
    }

    public String getHttpRequestParam() {
        return httpRequestParam;
    }

    public void setHttpRequestParam(String httpRequestParam) {
        this.httpRequestParam = httpRequestParam;
    }

    public String getHttpRequestTag() {
        return httpRequestTag;
    }

    public void setHttpRequestTag(String httpRequestTag) {
        this.httpRequestTag = httpRequestTag;
    }
}
