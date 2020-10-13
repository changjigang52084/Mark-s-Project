package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/28.
 */

public class ContentReviewedObj extends HttpObj {
    int total;
    int pageSize;
    ArrayList<ContentReviewedListObj> dataList;

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

    public ArrayList<ContentReviewedListObj> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<ContentReviewedListObj> dataList) {
        this.dataList = dataList;
    }
}
