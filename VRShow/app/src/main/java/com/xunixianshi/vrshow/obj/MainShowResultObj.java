package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/28.
 */

public class MainShowResultObj extends HttpObj {
    int total;
    int pageSize;
    ArrayList<MainShowResultListObj> list;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public ArrayList<MainShowResultListObj> getList() {
        return list;
    }

    public void setList(ArrayList<MainShowResultListObj> list) {
        this.list = list;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
}
