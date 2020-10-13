package com.xunixianshi.vrshow.util.cache;

import android.graphics.Bitmap;

/**
 * Created by duan on 2016/10/25.
 */

public class Cache {
    private CacheType cacheType;
    private Bitmap bitmap;
    private String json;

    private CacheCallBack cacheCallBack;

    public Cache(Bitmap bitmap){
        this.cacheType = CacheType.IMAGE;
        this.bitmap = bitmap;
    }

    public Cache(Bitmap bitmap,CacheCallBack cacheCallBack){
        this.cacheType = CacheType.IMAGE;
        this.bitmap = bitmap;
        this.cacheCallBack = cacheCallBack;
    }

    public Cache(String json){
        this.cacheType = CacheType.JSON;
        this.json = json;
    }

    public CacheType getCacheType() {
        return cacheType;
    }

    public CacheCallBack getCacheCallBack() {
        return cacheCallBack;
    }

    public void setCacheType(CacheType cacheType) {
        this.cacheType = cacheType;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getJson() {
        return json;
    }

    public void setJson(String json) {
        this.json = json;
    }
}
