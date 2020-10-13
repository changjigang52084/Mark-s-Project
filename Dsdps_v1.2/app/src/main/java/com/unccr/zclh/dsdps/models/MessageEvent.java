package com.unccr.zclh.dsdps.models;

public class MessageEvent {

    private String msg;
    private String code;
    private String command;

    public MessageEvent(String msg) {
        this.msg = msg;
    }

    public MessageEvent(String code,String command) {
        this.code = code;
        this.command = command;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }
}
