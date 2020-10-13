package com.unccr.zclh.dsdps.util;

public class InCallUtils {

    private String callNumber;
    private boolean isHolder;
    private boolean isVideo;
    private int rPort;
    private int lPort;
    private String timeThreadId;
    private String callState;
    private boolean isCurrentCall;
    private int payload;
    private int callId;


    public InCallUtils(String callNumber, boolean isHolder, boolean isVideo,
                       int rPort, int lPort, String timeThreadId, String callState,
                       boolean isCurrentCall, int payload,int callId) {
        super();
        this.callNumber = callNumber;
        this.isHolder = isHolder;
        this.isVideo = isVideo;
        this.rPort = rPort;
        this.lPort = lPort;
        this.timeThreadId = timeThreadId;
        this.callState = callState;
        this.isCurrentCall = isCurrentCall;
        this.payload = payload;
        this.callId = callId;
    }


    public int getCallId() {
        return callId;
    }


    public void setCallId(int callId) {
        this.callId = callId;
    }


    public int getPayload() {
        return payload;
    }
    public void setPayload(int payload) {
        this.payload = payload;
    }
    public boolean isCurrentCall() {
        return isCurrentCall;
    }
    public void setCurrentCall(boolean isCurrentCall) {
        this.isCurrentCall = isCurrentCall;
    }
    public String getCallState() {
        return callState;
    }
    public void setCallState(String callState) {
        this.callState = callState;
    }
    public String getCallNumber() {
        return callNumber;
    }
    public void setCallNumber(String callNumber) {
        this.callNumber = callNumber;
    }
    public boolean isHolder() {
        return isHolder;
    }
    public void setHolder(boolean isHolder) {
        this.isHolder = isHolder;
    }
    public boolean isVideo() {
        return isVideo;
    }
    public void setVideo(boolean isVideo) {
        this.isVideo = isVideo;
    }
    public int getrPort() {
        return rPort;
    }
    public void setrPort(int rPort) {
        this.rPort = rPort;
    }
    public int getlPort() {
        return lPort;
    }
    public void setlPort(int lPort) {
        this.lPort = lPort;
    }
    public String getTimeThreadId() {
        return timeThreadId;
    }
    public void setTimeThreadId(String timeThreadId) {
        this.timeThreadId = timeThreadId;
    }
}
