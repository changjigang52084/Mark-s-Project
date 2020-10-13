package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/12.
 */

public class UserShareObj extends HttpObj {
    int total;
    int pageSize;
    ArrayList<UserShareListObj> list;

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

    public ArrayList<UserShareListObj> getList() {
        return list;
    }

    public void setList(ArrayList<UserShareListObj> list) {
        this.list = list;
    }
}
