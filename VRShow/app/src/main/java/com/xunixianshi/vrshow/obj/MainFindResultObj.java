package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */

public class MainFindResultObj extends HttpObj {
    String total;
    int pageSize;
    ArrayList<MainFindListObj> list;

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public ArrayList<MainFindListObj> getList() {
        return list;
    }

    public void setList(ArrayList<MainFindListObj> list) {
        this.list = list;
    }
}
