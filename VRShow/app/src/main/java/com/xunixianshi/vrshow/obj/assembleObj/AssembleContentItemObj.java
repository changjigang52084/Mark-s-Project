package com.xunixianshi.vrshow.obj.assembleObj;

import java.io.Serializable;

/**
 * Created by duan on 2016/9/29.
 * 内容标签，列表的
 resourceId：资源ID，非空，正整数
 resourceTitle：资源标题，非空，字符串
 resourceSmallIntro：资源短描述，非空，字符串
 resourceLongIntro：资源长描述，非空，字符串
 resourceReadTotal：资源阅读量(播放量+下载量+虚拟量)，整数，非空
 coverImgUrl：资源封面图片地址，字符串，非空
 uploaderId：资源上传者(上传用户ID)，非空，整型
 uploaderName：资源上传者(上传者用户名称)，非空，字符串
 resourceCommentTotal：资源总评论数，非空，整数
 resourceLikesTotal：资源总点赞量(真实点赞量+虚拟点赞量)，非空，整型
 */

public class AssembleContentItemObj implements Serializable {
    private int resourceId;
    private String resourceTitle;
    private  String resourceSmallIntro;
    private  String resourceLongIntro;
    private  int resourceReadTotal;
    private  String coverImgUrl;

    private   int uploaderId;
    private  String uploaderName;
    private  int resourceCommentTotal;
    private  int resourceLikesTotal;
    private  boolean isSelect = false;
    private int isCollection;
    private int isLike;
    private String likesId; // 点赞ID，字符串，不存在时返回“-1”

    private int collectionId; // 收藏ID，整形，不存在时返回“-1”

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public String getResourceSmallIntro() {
        return resourceSmallIntro;
    }

    public void setResourceSmallIntro(String resourceSmallIntro) {
        this.resourceSmallIntro = resourceSmallIntro;
    }

    public String getResourceLongIntro() {
        return resourceLongIntro;
    }

    public void setResourceLongIntro(String resourceLongIntro) {
        this.resourceLongIntro = resourceLongIntro;
    }

    public int getResourceReadTotal() {
        return resourceReadTotal;
    }

    public void setResourceReadTotal(int resourceReadTotal) {
        this.resourceReadTotal = resourceReadTotal;
    }

    public String getCoverImgUrl() {
        return coverImgUrl;
    }

    public void setCoverImgUrl(String coverImgUrl) {
        this.coverImgUrl = coverImgUrl;
    }

    public int getUploaderId() {
        return uploaderId;
    }

    public void setUploaderId(int uploaderId) {
        this.uploaderId = uploaderId;
    }

    public String getUploaderName() {
        return uploaderName;
    }

    public void setUploaderName(String uploaderName) {
        this.uploaderName = uploaderName;
    }

    public int getResourceCommentTotal() {
        return resourceCommentTotal;
    }

    public void setResourceCommentTotal(int resourceCommentTotal) {
        this.resourceCommentTotal = resourceCommentTotal;
    }

    public int getResourceLikesTotal() {
        return resourceLikesTotal;
    }

    public void setResourceLikesTotal(int resourceLikesTotal) {
        this.resourceLikesTotal = resourceLikesTotal;
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

    public void setCollectionId(int collectionId) {
        this.collectionId = collectionId;
    }
}
