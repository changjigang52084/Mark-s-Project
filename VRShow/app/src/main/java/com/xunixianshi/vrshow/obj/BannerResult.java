package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by xnxs-ptzx04 on 2016/11/2.
 */

public class BannerResult extends HttpObj {
    ArrayList<BannerListResult> list;

    public ArrayList<BannerListResult> getList() {
        return list;
    }

    public void setList(ArrayList<BannerListResult> list) {
        this.list = list;
    }
}
