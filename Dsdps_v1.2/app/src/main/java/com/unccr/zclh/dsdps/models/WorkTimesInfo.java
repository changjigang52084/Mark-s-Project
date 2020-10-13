package com.unccr.zclh.dsdps.models;

import java.util.List;

public class WorkTimesInfo {

    private Integer sts;
    private List<WorkTimeInfo> wts;

    public WorkTimesInfo() {
    }

    public Integer getSts() {
        return sts;
    }

    public void setSts(Integer sts) {
        this.sts = sts;
    }

    public List<WorkTimeInfo> getWts() {
        return wts;
    }

    public void setWts(List<WorkTimeInfo> wts) {
        this.wts = wts;
    }
}
