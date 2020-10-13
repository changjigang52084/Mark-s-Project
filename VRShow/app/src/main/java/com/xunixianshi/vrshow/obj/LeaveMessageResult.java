package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by markIron on 2016/9/26.
 */

public class LeaveMessageResult extends HttpObj {

    private int total;
    private int pageSize;
    private ArrayList<LeaveMessageResultList> list;

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

    public ArrayList<LeaveMessageResultList> getList() {
        return list;
    }

    public void setList(ArrayList<LeaveMessageResultList> list) {
        this.list = list;
    }
}
