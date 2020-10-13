package com.xunixianshi.vrshow.util.cache;


/**
 *
 *@author DuanChunLin
 *@time 2016/10/25 16:07
 */
public enum CacheType {
    IMAGE("image"),
    JSON("json");
    private final String value;
    CacheType(String value){
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
