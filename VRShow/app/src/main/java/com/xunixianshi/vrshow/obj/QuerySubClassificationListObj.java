package com.xunixianshi.vrshow.obj;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-17
 * Time: 16:15
 * FIXME
 */
public class QuerySubClassificationListObj {

    private int typeId;
    private String typeName;
    private String typeIcon;
    private int parentId;
    private int clickNumbers;

    public int getTypeId() {
        return typeId;
    }

    public void setTypeId(int typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeIcon() {
        return typeIcon;
    }

    public void setTypeIcon(String typeIcon) {
        this.typeIcon = typeIcon;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public int getClickNumbers() {
        return clickNumbers;
    }

    public void setClickNumbers(int clickNumbers) {
        this.clickNumbers = clickNumbers;
    }
}