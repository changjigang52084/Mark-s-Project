package com.xunixianshi.vrshow.obj;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by markIron on 2016/9/19.
 */
public class UserUploadResourcesObj implements Serializable {

    private String coverImg;
    private int currencyClient;
    private String developers;
    private ArrayList<String> imgList;
    private String resourcesContent;
    private String resourcesDefinition;
    private String resourcesDriveVersion;
    private String resourcesLanguage;
    private String resourcesOriginal;
    private String resourcesPath;
    private String resourcesSize;
    private String resourcesSubtitle;
    private String resourcesTags;
    private String resourcesTitle;
    private int resourcesColumn;
    private int resourcesType;
    private String resourcesVersion;
    private String userId;
    private int resourcesLinkType;

    public String getCoverImg() {
        return coverImg;
    }

    public void setCoverImg(String coverImg) {
        this.coverImg = coverImg;
    }

    public int getCurrencyClient() {
        return currencyClient;
    }

    public void setCurrencyClient(int currencyClient) {
        this.currencyClient = currencyClient;
    }

    public String getDevelopers() {
        return developers;
    }

    public void setDevelopers(String developers) {
        this.developers = developers;
    }

    public ArrayList<String> getImgList() {
        return imgList;
    }

    public void setImgList(ArrayList<String> imgList) {
        this.imgList = imgList;
    }

    public String getResourcesContent() {
        return resourcesContent;
    }

    public void setResourcesContent(String resourcesContent) {
        this.resourcesContent = resourcesContent;
    }

    public String getResourcesDefinition() {
        return resourcesDefinition;
    }

    public void setResourcesDefinition(String resourcesDefinition) {
        this.resourcesDefinition = resourcesDefinition;
    }

    public String getResourcesDriveVersion() {
        return resourcesDriveVersion;
    }

    public void setResourcesDriveVersion(String resourcesDriveVersion) {
        this.resourcesDriveVersion = resourcesDriveVersion;
    }

    public String getResourcesLanguage() {
        return resourcesLanguage;
    }

    public void setResourcesLanguage(String resourcesLanguage) {
        this.resourcesLanguage = resourcesLanguage;
    }

    public String getResourcesOriginal() {
        return resourcesOriginal;
    }

    public void setResourcesOriginal(String resourcesOriginal) {
        this.resourcesOriginal = resourcesOriginal;
    }

    public String getResourcesPath() {
        return resourcesPath;
    }

    public void setResourcesPath(String resourcesPath) {
        this.resourcesPath = resourcesPath;
    }

    public String getResourcesSize() {
        return resourcesSize;
    }

    public void setResourcesSize(String resourcesSize) {
        this.resourcesSize = resourcesSize;
    }

    public String getResourcesSubtitle() {
        return resourcesSubtitle;
    }

    public void setResourcesSubtitle(String resourcesSubtitle) {
        this.resourcesSubtitle = resourcesSubtitle;
    }

    public String getResourcesTags() {
        return resourcesTags;
    }

    public void setResourcesTags(String resourcesTags) {
        this.resourcesTags = resourcesTags;
    }

    public String getResourcesTitle() {
        return resourcesTitle;
    }

    public void setResourcesTitle(String resourcesTitle) {
        this.resourcesTitle = resourcesTitle;
    }

    public int getResourcesColumn() {
        return resourcesColumn;
    }

    public void setResourcesColumn(int resourcesColumn) {
        this.resourcesColumn = resourcesColumn;
    }

    public int getResourcesType() {
        return resourcesType;
    }

    public void setResourcesType(int resourcesType) {
        this.resourcesType = resourcesType;
    }

    public String getResourcesVersion() {
        return resourcesVersion;
    }

    public void setResourcesVersion(String resourcesVersion) {
        this.resourcesVersion = resourcesVersion;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getResourcesLinkType() {
        return resourcesLinkType;
    }

    public void setResourcesLinkType(int resourcesLinkType) {
        this.resourcesLinkType = resourcesLinkType;
    }
}
