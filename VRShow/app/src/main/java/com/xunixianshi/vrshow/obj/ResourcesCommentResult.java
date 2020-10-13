package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by markIron on 2016/9/27.
 */

public class ResourcesCommentResult extends HttpObj {

    private int total;
    private int pageSize;
    private ArrayList<ResourcesCommentResultList> resourcesCommentList;

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

    public ArrayList<ResourcesCommentResultList> getResourcesCommentList() {
        return resourcesCommentList;
    }

    public void setResourcesCommentList(ArrayList<ResourcesCommentResultList> resourcesCommentList) {
        this.resourcesCommentList = resourcesCommentList;
    }
}
