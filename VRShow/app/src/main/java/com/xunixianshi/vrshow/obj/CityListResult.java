package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * 城市列表obj
 * Created by Administrator on 2016/10/24.
 */

public class CityListResult extends HttpObj {
    ArrayList<CityListItemResult> list;

    public ArrayList<CityListItemResult> getList() {
        return list;
    }

    public void setList(ArrayList<CityListItemResult> list) {
        this.list = list;
    }
}
