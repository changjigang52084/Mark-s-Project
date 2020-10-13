package com.xunixianshi.vrshow.obj.assembleObj;

import com.xunixianshi.vrshow.obj.HttpObj;

import java.util.List;

/**
 * Created by duan on 2016/9/29.
 * 合集列表
 *
 total：记录总数，非空，整数
 pageSize：每页查询记录数，非空，正整数
 list：合集资源数据集合
 */

public class AssembleListObj extends HttpObj {
    int total;
    int pageSize;
    List<AssembleItemObj> list;

    public List<AssembleItemObj> getList() {
        return list;
    }

    public void setList(List<AssembleItemObj> list) {
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
