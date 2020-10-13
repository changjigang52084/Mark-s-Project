package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by markIron on 2016/9/26.
 */

public class UserConcernResult extends HttpObj {

    private int total;
    private int pageSize;
    private ArrayList<UserConcernResultList> list;

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

    public ArrayList<UserConcernResultList> getList() {
        return list;
    }

    public void setList(ArrayList<UserConcernResultList> list) {
        this.list = list;
    }
}
