package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by xnxs-ptzx04 on 2016/11/3.
 */

public class UserCommentResult extends HttpObj {
    int total;
    int pageSize;
    ArrayList<UserCommentListResult> list;

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

    public ArrayList<UserCommentListResult> getList() {
        return list;
    }

    public void setList(ArrayList<UserCommentListResult> list) {
        this.list = list;
    }
}
