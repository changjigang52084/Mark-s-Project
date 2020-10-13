package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by xnxs-ptzx04 on 2016/10/31.
 */

public class ActivitysResult extends HttpObj {
    int total;
    int pageSize;
    int currPage;
    ArrayList<ActivitysListResult> list;

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

    public ArrayList<ActivitysListResult> getList() {
        return list;
    }

    public void setList(ArrayList<ActivitysListResult> list) {
        this.list = list;
    }

    public int getCurrPage() {
        return currPage;
    }

    public void setCurrPage(int currPage) {
        this.currPage = currPage;
    }
}
