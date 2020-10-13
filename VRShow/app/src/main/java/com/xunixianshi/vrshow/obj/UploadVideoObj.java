package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-10
 * Time: 18:59
 * FIXME
 */
public class UploadVideoObj implements Serializable {

    /**
     * 资源Id
     */
    private String resourceId;

    /**
     * 图片路径
     */
    private String imagePath;

    /**
     * 资源名称
     */
    private String resourceName;

    /**
     * 资源简介
     */
    private String resourceIntroduce;

    /**
     * 已上传文件资源路径
     */
    private String uploadResourcePath;

    public String getResourceId() {
        return resourceId;
    }

    public void setResourceId(String resourceId) {
        this.resourceId = resourceId;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public String getResourceName() {
        return resourceName;
    }

    public void setResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceIntroduce() {
        return resourceIntroduce;
    }

    public void setResourceIntroduce(String resourceIntroduce) {
        this.resourceIntroduce = resourceIntroduce;
    }

    public String getUploadResourcePath() {
        return uploadResourcePath;
    }

    public void setUploadResourcePath(String uploadResourcePath) {
        this.uploadResourcePath = uploadResourcePath;
    }
}