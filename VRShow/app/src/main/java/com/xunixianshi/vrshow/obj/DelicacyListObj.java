package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * 详情页列表OBJ
 *@author DuanChunLin
 *@time 2016/10/14 11:09
 */
public class DelicacyListObj extends VideoMoreObj implements Serializable {

    int resourceId; //资源ID，非空，正整数
    String resourceTitle; //资源标题，非空，字符串
    String resourceCoverImgUrl; //资源封面图，字符串，非空
    int resourcePlayerTotal; //资源浏览量，非空，整数
    String resourceSmallIntro; //资源短描述，非空，字符串
    String resourcesIcon; //图标地址，字符串，非空，为空时返回-1
    int uploaderId; //上传者用户ID，非空，正整数
    String uploaderName; //上传者用户名，非空，字符串
    int resourcesScore; //资源评分，非空，整型，备用字段
    int isFree; //是否收费，非空，整数，0表示免费，1表示收费
    int isCopyright; //当前资源是否有版权，整数，0表示无版权，1表示有版权
    int copyrightTagId; //版权标签ID，非空，整型，无版权时返回-1
    String copyrightTagName; //版权标签名称，可为空，字符串，无版权时返回空字符串

    public String getCopyrightTagName() {
        return copyrightTagName;
    }

    public void setCopyrightTagName(String copyrightTagName) {
        this.copyrightTagName = copyrightTagName;
    }

    public String getResourceTitle() {
        return resourceTitle;
    }

    public void setResourceTitle(String resourceTitle) {
        this.resourceTitle = resourceTitle;
    }

    public int getResourceId() {
        return resourceId;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }

    public String getResourceCoverImgUrl() {
        return resourceCoverImgUrl;
    }

    public void setResourceCoverImgUrl(String resourceCoverImgUrl) {
        this.resourceCoverImgUrl = resourceCoverImgUrl;
    }

    public int getResourcePlayerTotal() {
        return resourcePlayerTotal;
    }

    public void setResourcePlayerTotal(int resourcePlayerTotal) {
        this.resourcePlayerTotal = resourcePlayerTotal;
    }

    public String getResourceSmallIntro() {
        return resourceSmallIntro;
    }

    public void setResourceSmallIntro(String resourceSmallIntro) {
        this.resourceSmallIntro = resourceSmallIntro;
    }

    public String getResourcesIcon() {
        return resourcesIcon;
    }

    public void setResourcesIcon(String resourcesIcon) {
        this.resourcesIcon = resourcesIcon;
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

    public int getResourcesScore() {
        return resourcesScore;
    }

    public void setResourcesScore(int resourcesScore) {
        this.resourcesScore = resourcesScore;
    }

    public int getIsFree() {
        return isFree;
    }

    public void setIsFree(int isFree) {
        this.isFree = isFree;
    }

    public int getIsCopyright() {
        return isCopyright;
    }

    public void setIsCopyright(int isCopyright) {
        this.isCopyright = isCopyright;
    }

    public int getCopyrightTagId() {
        return copyrightTagId;
    }

    public void setCopyrightTagId(int copyrightTagId) {
        this.copyrightTagId = copyrightTagId;
    }
}
