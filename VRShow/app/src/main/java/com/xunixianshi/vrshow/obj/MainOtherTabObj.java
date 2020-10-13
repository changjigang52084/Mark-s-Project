package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */

public class MainOtherTabObj extends HttpObj {
    int total;
    int pageSize;
    ArrayList<MainOtherTabListObj> list;

    public ArrayList<MainOtherTabListObj> getList() {
        return list;
    }

    public void setList(ArrayList<MainOtherTabListObj> list) {
        this.list = list;
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
