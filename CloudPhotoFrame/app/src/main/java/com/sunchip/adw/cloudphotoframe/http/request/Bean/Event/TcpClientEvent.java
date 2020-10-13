package com.sunchip.adw.cloudphotoframe.http.request.Bean.Event;

/**
 * Created by yingmuliang on 2019/12/27.
 */
public class TcpClientEvent {
    private String type;
    private String content;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
