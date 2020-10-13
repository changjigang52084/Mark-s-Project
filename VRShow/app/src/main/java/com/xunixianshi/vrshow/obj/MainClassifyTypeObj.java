package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by xnxs-ptzx04 on 2016/12/7.
 */

public class MainClassifyTypeObj extends HttpObj {
    int total;
    int pageSize;
    ArrayList<MainClassifyTypeListObj> list;

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

    public ArrayList<MainClassifyTypeListObj> getList() {
        return list;
    }

    public void setList(ArrayList<MainClassifyTypeListObj> list) {
        this.list = list;
    }
}
