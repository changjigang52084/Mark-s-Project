package com.xunixianshi.vrshow.obj.search;

import com.xunixianshi.vrshow.obj.HttpObj;

import java.util.ArrayList;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-17
 * Time: 14:46
 * FIXME
 */
public class SearchHotWordObj extends HttpObj {

    private int total;
    private int pageSize;
    private ArrayList<SearchHotWordListObj> list;

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

    public ArrayList<SearchHotWordListObj> getList() {
        return list;
    }

    public void setList(ArrayList<SearchHotWordListObj> list) {
        this.list = list;
    }
}