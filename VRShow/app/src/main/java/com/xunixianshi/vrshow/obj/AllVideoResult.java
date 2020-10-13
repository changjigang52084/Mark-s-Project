package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * User: hch
 * Date: 2016-07-18
 * Time: 11:15
 * FIXME
 */
public class AllVideoResult {
    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = 1L;
    String resCode;
    String resDesc;
    int total;
    int pageSize;
    ArrayList<AllVideoResultDataList> resourcesList;

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

    public ArrayList<AllVideoResultDataList> getResourcesList() {
        return resourcesList;
    }

    public void setResourcesList(ArrayList<AllVideoResultDataList> resourcesList) {
        this.resourcesList = resourcesList;
    }
}
