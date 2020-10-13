package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

public class CollectResultDataList implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;

	int collectId;
	int resourcesId;
	String resourcesName;
	String resourcesIcon;
	String smallIntro;
	String collectTime;
	int resourcePlayType;
	int resourcesType;
	int cumulativeNum;
	int resourcesScore;
	boolean isSelect;

	private int isCollection;
	private int isLike;

	private String likesId; // 点赞ID，字符串，不存在时返回“-1”

	private int collectionId; // 收藏ID，整形，不存在时返回“-1”

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}

	public int getCollectId() {
		return collectId;
	}

	public void setCollectId(int collectId) {
		this.collectId = collectId;
	}

	public int getResourcePlayType() {
		return resourcePlayType;
	}

	public void setResourcePlayType(int resourcePlayType) {
		this.resourcePlayType = resourcePlayType;
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

	public String getResourcesIcon() {
		return resourcesIcon;
	}

	public void setResourcesIcon(String resourcesIcon) {
		this.resourcesIcon = resourcesIcon;
	}

	public String getSmallIntro() {
		return smallIntro;
	}

	public void setSmallIntro(String smallIntro) {
		this.smallIntro = smallIntro;
	}

	public String getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(String collectTime) {
		this.collectTime = collectTime;
	}

	public int getResourcesType() {
		return resourcesType;
	}

	public void setResourcesType(int resourcesType) {
		this.resourcesType = resourcesType;
	}

	public int getCumulativeNum() {
		return cumulativeNum;
	}

	public void setCumulativeNum(int cumulativeNum) {
		this.cumulativeNum = cumulativeNum;
	}

	public int getResourcesScore() {
		return resourcesScore;
	}

	public void setResourcesScore(int resourcesScore) {
		this.resourcesScore = resourcesScore;
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

	public Integer getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(int collectionId) {
		this.collectionId = collectionId;
	}

}
