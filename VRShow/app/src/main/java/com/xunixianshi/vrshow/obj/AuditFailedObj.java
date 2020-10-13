package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by markIron on 2016/9/18.
 */
public class AuditFailedObj extends HttpObj{

    private ArrayList<AuditFailedResult> dataList;
    private int total;
    private int pageSize;

    public ArrayList<AuditFailedResult> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<AuditFailedResult> dataList) {
        this.dataList = dataList;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
