package com.xunixianshi.vrshow.obj.search;

import com.xunixianshi.vrshow.obj.HttpObj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/29.
 */

public class SearchResultObj extends HttpObj {
    int total;
    int pageSize;
    ArrayList<SearchResultListObj> dataList;

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

    public ArrayList<SearchResultListObj> getDataList() {
        return dataList;
    }

    public void setDataList(ArrayList<SearchResultListObj> dataList) {
        this.dataList = dataList;
    }
}
