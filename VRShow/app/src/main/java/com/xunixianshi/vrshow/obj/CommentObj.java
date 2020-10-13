package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/8/25.
 */
public class CommentObj extends HttpObj{

    int total;
    int pageSize;
    ArrayList<CommentListObj> resourcesCommentList;

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public ArrayList<CommentListObj> getResourcesCommentList() {
        return resourcesCommentList;
    }

    public void setResourcesCommentList(ArrayList<CommentListObj> resourcesCommentList) {
        this.resourcesCommentList = resourcesCommentList;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }
}
