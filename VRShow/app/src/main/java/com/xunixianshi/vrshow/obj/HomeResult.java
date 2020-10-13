package com.xunixianshi.vrshow.obj;

import java.io.Serializable;
import java.util.ArrayList;

public class HomeResult extends HttpObj {

    ArrayList<HomeResultList> hotList;
    ArrayList<HomeResultList> newList;
    ArrayList<HomeResultFocusImgList> bannerListOne;
    ArrayList<HomeResultFocusImgList> bannerListTow;
    ArrayList<HomeResultFocusImgList> bannerListThree;

    public ArrayList<HomeResultList> getHotList() {
        return hotList;
    }

    public void setHotList(ArrayList<HomeResultList> hotList) {
        this.hotList = hotList;
    }

    public ArrayList<HomeResultList> getNewList() {
        return newList;
    }

    public void setNewList(ArrayList<HomeResultList> newList) {
        this.newList = newList;
    }

    public ArrayList<HomeResultFocusImgList> getBannerListOne() {
        return bannerListOne;
    }

    public void setBannerListOne(ArrayList<HomeResultFocusImgList> bannerListOne) {
        this.bannerListOne = bannerListOne;
    }

    public ArrayList<HomeResultFocusImgList> getBannerListTow() {
        return bannerListTow;
    }

    public void setBannerListTow(ArrayList<HomeResultFocusImgList> bannerListTow) {
        this.bannerListTow = bannerListTow;
    }

    public ArrayList<HomeResultFocusImgList> getBannerListThree() {
        return bannerListThree;
    }

    public void setBannerListThree(ArrayList<HomeResultFocusImgList> bannerListThree) {
        this.bannerListThree = bannerListThree;
    }
}
