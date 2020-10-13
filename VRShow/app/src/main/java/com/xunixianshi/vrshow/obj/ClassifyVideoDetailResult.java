package com.xunixianshi.vrshow.obj;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassifyVideoDetailResult implements Serializable {

    /**
     * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
     */

    private static final long serialVersionUID = 1L;

    String resCode;
    String resDesc;
    int resourcesId;
    String resourcesName;
    int resourcesType;
    //	int resourcePlayType;
    String resourcesIcon;
    int resourcesNumbers;
    String createTime;
    //	String resourcesUrl;
    int resourcesScore;
    String smallIntroduce;
    String longIntroduce;
    int isCollection;
    String shareUrl;
    ArrayList<ClassifyVideoDetailResultSimilarList> similarList;
    String fileSize;
    int isComment;
    int isLikes;
    int likesTotal;
    int isCopyright;
    int copyrightTagId;
    String copyrightTagName;
    int upStatus;
    String attachmentUserId;
    String attachmentUserName;
    String attachmentUserIcon;
    int attachmentUserLvl;


    public String getShareUrl() {
        return shareUrl;
    }

    public void setShareUrl(String shareUrl) {
        this.shareUrl = shareUrl;
    }

    public String getFileSize() {
        return fileSize;
    }

    public void setFileSize(String fileSize) {
        this.fileSize = fileSize;
    }

    public String getResCode() {
        return resCode;
    }

    public void setResCode(String resCode) {
        this.resCode = resCode;
    }

    public String getResDesc() {
        return resDesc;
    }

    public void setResDesc(String resDesc) {
        this.resDesc = resDesc;
    }

    public int getResourcesId() {
        return resourcesId;
    }

    public void setResourcesId(int resourcesId) {
        this.resourcesId = resourcesId;
    }

    public String getResourcesName() {
        return resourcesName;
    }

    public void setResourcesName(String resourcesName) {
        this.resourcesName = resourcesName;
    }

    public int getResourcesType() {
        return resourcesType;
    }

    public void setResourcesType(int resourcesType) {
        this.resourcesType = resourcesType;
    }

//	public int getResourcePlayType() {
//		return resourcePlayType;
//	}
//
//	public void setResourcePlayType(int resourcePlayType) {
//		this.resourcePlayType = resourcePlayType;
//	}

    public String getResourcesIcon() {
        return resourcesIcon;
    }

    public void setResourcesIcon(String resourcesIcon) {
        this.resourcesIcon = resourcesIcon;
    }

    public int getResourcesNumbers() {
        return resourcesNumbers;
    }

    public void setResourcesNumbers(int resourcesNumbers) {
        this.resourcesNumbers = resourcesNumbers;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

//	public String getResourcesUrl() {
//		return resourcesUrl;
//	}
//
//	public void setResourcesUrl(String resourcesUrl) {
//		this.resourcesUrl = resourcesUrl;
//	}

    public int getResourcesScore() {
        return resourcesScore;
    }

    public void setResourcesScore(int resourcesScore) {
        this.resourcesScore = resourcesScore;
    }

    public String getSmallIntroduce() {
        return smallIntroduce;
    }

    public void setSmallIntroduce(String smallIntroduce) {
        this.smallIntroduce = smallIntroduce;
    }

    public int getIsCollection() {
        return isCollection;
    }

    public void setIsCollection(int isCollection) {
        this.isCollection = isCollection;
    }

    public String getLongIntroduce() {
        return longIntroduce;
    }

    public void setLongIntroduce(String longIntroduce) {
        this.longIntroduce = longIntroduce;
    }

    public ArrayList<ClassifyVideoDetailResultSimilarList> getSimilarList() {
        return similarList;
    }

    public void setSimilarList(
            ArrayList<ClassifyVideoDetailResultSimilarList> similarList) {
        this.similarList = similarList;
    }

    public int getIsLikes() {
        return isLikes;
    }

    public void setIsLikes(int isLikes) {
        this.isLikes = isLikes;
    }

    public int getLikesTotal() {
        return likesTotal;
    }

    public void setLikesTotal(int likesTotal) {
        this.likesTotal = likesTotal;
    }

    public int getIsComment() {
        return isComment;
    }

    public void setIsComment(int isComment) {
        this.isComment = isComment;
    }

    public String getAttachmentUserName() {
        return attachmentUserName;
    }

    public void setAttachmentUserName(String attachmentUserName) {
        this.attachmentUserName = attachmentUserName;
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

    public String getCopyrightTagName() {
        return copyrightTagName;
    }

    public void setCopyrightTagName(String copyrightTagName) {
        this.copyrightTagName = copyrightTagName;
    }

    public int getUpStatus() {
        return upStatus;
    }

    public void setUpStatus(int upStatus) {
        this.upStatus = upStatus;
    }

    public String getAttachmentUserId() {
        return attachmentUserId;
    }

    public void setAttachmentUserId(String attachmentUserId) {
        this.attachmentUserId = attachmentUserId;
    }

    public int getAttachmentUserLvl() {
        return attachmentUserLvl;
    }

    public void setAttachmentUserLvl(int attachmentUserLvl) {
        this.attachmentUserLvl = attachmentUserLvl;
    }

    public String getAttachmentUserIcon() {
        return attachmentUserIcon;
    }

    public void setAttachmentUserIcon(String attachmentUserIcon) {
        this.attachmentUserIcon = attachmentUserIcon;
    }
}
