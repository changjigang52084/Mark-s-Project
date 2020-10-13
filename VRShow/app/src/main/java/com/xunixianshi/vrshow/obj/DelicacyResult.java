package com.xunixianshi.vrshow.obj;


import java.util.ArrayList;

/**
 * 精品对象
 *@author DuanChunLin
 *@time 2016/10/14 11:07
 */
public class DelicacyResult {
    String resCode;
    String resDesc;
    int total;
    int pageSize;
    ArrayList<DelicacyListObj> list;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
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

    public ArrayList<DelicacyListObj> getList() {
        return list;
    }

    public void setList(ArrayList<DelicacyListObj> list) {
        this.list = list;
    }
}
