package com.unccr.zclh.dsdps.sip;

public class CallMediaUtils {
    /**
     * 通话的id
     */
    private int callid;
    /**
     * payload
     */
    private int payload;
    /**
     * 本地端口
     */
    private int l;
    /**
     * 远程端口
     */
    private int r;


    public CallMediaUtils(int callid, int payload, int r, int l) {
        super();
        this.callid = callid;
        this.payload = payload;
        this.l = l;
        this.r = r;
    }
    public int getCallid() {
        return callid;
    }
    public void setCallid(int callid) {
        this.callid = callid;
    }
    public int getPayload() {
        return payload;
    }
    public void setPayload(int payload) {
        this.payload = payload;
    }
    public int getL() {
        return l;
    }
    public void setL(int l) {
        this.l = l;
    }
    public int getR() {
        return r;
    }
    public void setR(int r) {
        this.r = r;
    }

}
