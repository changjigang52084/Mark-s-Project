package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/9/27.
 */

public class MainClassifyHomeColumnListObj implements Serializable {
    int columnId;
    String columnName;

    public int getColumnId() {
        return columnId;
    }

    public void setColumnId(int columnId) {
        this.columnId = columnId;
    }

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }
}
