package com.sunchip.adw.cloudphotoframe.http.request.Bean.Event;

import java.util.List;

import me.zhouzhuo.zzsecondarylinkage.bean.BaseMenuBean;

public class PhotoListEvent extends BaseMenuBean {

    private int id;
    private String name;
    private List<PhotosEvent> photos;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<PhotosEvent> getPhotos() {
        return photos;
    }

    public void setPhotos(List<PhotosEvent> photos) {
        this.photos = photos;
    }
}
