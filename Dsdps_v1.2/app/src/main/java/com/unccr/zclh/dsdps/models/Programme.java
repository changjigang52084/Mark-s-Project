package com.unccr.zclh.dsdps.models;

import java.util.List;

public class Programme {

    private String actionNum;
    private String startTime;
    private String endTime;
    private List<Resource> resources;

    public String getActionNum() {
        return actionNum;
    }

    public void setActionNum(String actionNum) {
        this.actionNum = actionNum;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public void setResources(List<Resource> resources) {
        this.resources = resources;
    }
}
