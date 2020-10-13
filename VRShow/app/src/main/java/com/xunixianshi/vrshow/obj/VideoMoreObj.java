package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * Created by duan on 2016/10/24.
 */

public class VideoMoreObj implements Serializable {

    private int likesTotal; //资源点赞总数量，非空，整数

    private int isCollection;
    private int isLike;

    private String likesId; // 点赞ID，字符串，不存在时返回“-1”

    private int collectionId; // 收藏ID，整形，不存在时返回“-1”

    public int getLikesTotal() {
        return likesTotal;
    }

    public void setLikesTotal(int likesTotal) {
        this.likesTotal = likesTotal;
    }

    public int getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(int isCollection) {
        this.isCollection = isCollection;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }

    public String getLikesId() {
        return likesId;
    }

    public void setLikesId(String likesId) {
        this.likesId = likesId;
    }

    public int getCollectionId() {
        return collectionId;
    }

    public void setCollectionId(Integer collectionId) {
        this.collectionId = collectionId;
    }
}
