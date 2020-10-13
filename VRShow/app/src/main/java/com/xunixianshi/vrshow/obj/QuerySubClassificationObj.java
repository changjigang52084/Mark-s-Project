package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-17
 * Time: 16:02
 * FIXME
 */
public class QuerySubClassificationObj extends HttpObj {

    private ArrayList<QuerySubClassificationListObj> typeList;

    public ArrayList<QuerySubClassificationListObj> getTypeList() {
        return typeList;
    }

    public void setTypeList(ArrayList<QuerySubClassificationListObj> typeList) {
        this.typeList = typeList;
    }
}