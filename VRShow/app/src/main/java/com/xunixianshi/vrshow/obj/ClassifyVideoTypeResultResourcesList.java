package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

public class ClassifyVideoTypeResultResourcesList implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;

	int resourceId;
	String resourceName;
	String smallIntroduce;
	String coverImgUrl;
	int resourcesScore;
	int cumulativeNum;
	String createTime;
	String resourcesPackageName;
	String longIntroduce;

	public String getLongIntroduce() {
		return longIntroduce;
	}

	public void setLongIntroduce(String longIntroduce) {
		this.longIntroduce = longIntroduce;
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
