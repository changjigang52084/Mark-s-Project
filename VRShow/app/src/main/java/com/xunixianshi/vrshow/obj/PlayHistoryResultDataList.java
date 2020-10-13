package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

public class PlayHistoryResultDataList implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;

	int pid;
	int resourcesId;
	String resourcesTitle;
	String smallIntro;
	String lastPlayTime;
	String resourcesCoverImg;
	int resourcePlayType;
	int downloadNumbers;
	int playerNumbers;
	int resourcesScore;
	int isPlayEnd;
	int playTimes;
	boolean isSelect;
	private int isCollection;
	private int isLike;

	private String likesId; // 点赞ID，字符串，不存在时返回“-1”

	private int collectionId; // 收藏ID，整形，不存在时返回“-1”

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(int resourcesId) {
		this.resourcesId = resourcesId;
	}

	public int getResourcePlayType() {
		return resourcePlayType;
	}

	public void setResourcePlayType(int resourcePlayType) {
		this.resourcePlayType = resourcePlayType;
	}

	public String getResourcesTitle() {
		return resourcesTitle;
	}

	public void setResourcesTitle(String resourcesTitle) {
		this.resourcesTitle = resourcesTitle;
	}

	public String getSmallIntro() {
		return smallIntro;
	}

	public void setSmallIntro(String smallIntro) {
		this.smallIntro = smallIntro;
	}

	public String getLastPlayTime() {
		return lastPlayTime;
	}

	public void setLastPlayTime(String lastPlayTime) {
		this.lastPlayTime = lastPlayTime;
	}

	public String getResourcesCoverImg() {
		return resourcesCoverImg;
	}

	public void setResourcesCoverImg(String resourcesCoverImg) {
		this.resourcesCoverImg = resourcesCoverImg;
	}

	public int getDownloadNumbers() {
		return downloadNumbers;
	}

	public void setDownloadNumbers(int downloadNumbers) {
		this.downloadNumbers = downloadNumbers;
	}

	public int getPlayerNumbers() {
		return playerNumbers;
	}

	public void setPlayerNumbers(int playerNumbers) {
		this.playerNumbers = playerNumbers;
	}

	public int getResourcesScore() {
		return resourcesScore;
	}

	public void setResourcesScore(int resourcesScore) {
		this.resourcesScore = resourcesScore;
	}

	public int getIsPlayEnd() {
		return isPlayEnd;
	}

	public void setIsPlayEnd(int isPlayEnd) {
		this.isPlayEnd = isPlayEnd;
	}

	public int getPlayTimes() {
		return playTimes;
	}

	public void setPlayTimes(int playTimes) {
		this.playTimes = playTimes;
	}

	public boolean isSelect() {
		return isSelect;
	}

	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
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
