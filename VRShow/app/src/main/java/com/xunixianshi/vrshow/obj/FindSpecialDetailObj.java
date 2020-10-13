package com.xunixianshi.vrshow.obj;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/9/27.
 */

public class FindSpecialDetailObj extends HttpObj {
    int total;
    int pageSize;
    String specialId;
    String specialName;
    String coverImgUrl;
    String specialIntro;
    int readTotal;
    int specialResourceTotal;
    String createTime;
    ArrayList<FindSpecialDetailListObj> list;

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

    public String getSpecialId() {
        return specialId;
    }

    public void setSpecialId(String specialId) {
        this.specialId = specialId;
    }

    public String getSpecialName() {
        return specialName;
    }

    public void setSpecialName(String specialName) {
        this.specialName = specialName;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public String getSpecialIntro() {
        return specialIntro;
    }

    public void setSpecialIntro(String specialIntro) {
        this.specialIntro = specialIntro;
    }

    public int getReadTotal() {
        return readTotal;
    }

    public void setReadTotal(int readTotal) {
        this.readTotal = readTotal;
    }

    public int getSpecialResourceTotal() {
        return specialResourceTotal;
    }

    public void setSpecialResourceTotal(int specialResourceTotal) {
        this.specialResourceTotal = specialResourceTotal;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public ArrayList<FindSpecialDetailListObj> getList() {
        return list;
    }

    public void setList(ArrayList<FindSpecialDetailListObj> list) {
        this.list = list;
    }
}
