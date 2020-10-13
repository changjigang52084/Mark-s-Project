package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

public class ClassifyVideoDetailResultSimilarList implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;
	int resourceId;
	String resourceName;
	String smallIntroduce;
	String longIntroduce;
	String coverImgUrl;
	int resourcesScore;
	int cumulativeNum;
	String createTime;
	String fileSize;
	String resourcesPackageName;

	public String getLongIntroduce() {
		return longIntroduce;
	}

	public void setLongIntroduce(String longIntroduce) {
		this.longIntroduce = longIntroduce;
	}

	public String getFileSize() {
		return fileSize;
	}

	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}

	public int getResourceId() {
		return resourceId;
	}

	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	public String getResourceName() {
		return resourceName;
	}

	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	public String getSmallIntroduce() {
		return smallIntroduce;
	}

	public void setSmallIntroduce(String smallIntroduce) {
		this.smallIntroduce = smallIntroduce;
	}

	public String getCoverImgUrl() {
		return coverImgUrl;
	}

	public void setCoverImgUrl(String coverImgUrl) {
		this.coverImgUrl = coverImgUrl;
	}

	public int getResourcesScore() {
		return resourcesScore;
	}

	public void setResourcesScore(int resourcesScore) {
		this.resourcesScore = resourcesScore;
	}

	public int getCumulativeNum() {
		return cumulativeNum;
	}

	public void setCumulativeNum(int cumulativeNum) {
		this.cumulativeNum = cumulativeNum;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public String getResourcesPackageName() {
		return resourcesPackageName;
	}

	public void setResourcesPackageName(String resourcesPackageName) {
		this.resourcesPackageName = resourcesPackageName;
	}

}
