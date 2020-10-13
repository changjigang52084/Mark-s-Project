/**  
 * @Title: ClassifyResultListResourceList.java
 * @Package com.xunixianshi.vrshow.obj
 * @Description: TODO(用一句话描述该文件做什么)
 * @author hechuang 
 * @date 2016年3月12日 下午4:20:10
 * @version V1.0  
 */
package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

/**
 * @ClassName: ClassifyResultListResourceList
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author hechuang
 * @date 2016年3月12日 下午4:20:10
 *
 */

public class ClassifyResultListResourceList implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = -5874197229533283941L;

	int resourceId;
	String resourceName;
	String smallIntroduce;
	String coverImgUrl;
	int resourcesScore;
	int cumulativeNum;
	String createTime;

	/**
	 * @return the resourceId
	 */

	public int getResourceId() {
		return resourceId;
	}

	/**
	 * @param resourceId
	 */
	public void setResourceId(int resourceId) {
		this.resourceId = resourceId;
	}

	/**
	 * @return the resourceName
	 */

	public String getResourceName() {
		return resourceName;
	}

	/**
	 * @param resourceName
	 */
	public void setResourceName(String resourceName) {
		this.resourceName = resourceName;
	}

	/**
	 * @return the smallIntroduce
	 */

	public String getSmallIntroduce() {
		return smallIntroduce;
	}

	/**
	 * @param smallIntroduce
	 */
	public void setSmallIntroduce(String smallIntroduce) {
		this.smallIntroduce = smallIntroduce;
	}

	/**
	 * @return the coverImgUrl
	 */

	public String getCoverImgUrl() {
		return coverImgUrl;
	}

	/**
	 * @param coverImgUrl
	 */
	public void setCoverImgUrl(String coverImgUrl) {
		this.coverImgUrl = coverImgUrl;
	}

	/**
	 * @return the resourcesScore
	 */

	public int getResourcesScore() {
		return resourcesScore;
	}

	/**
	 * @param resourcesScore
	 */
	public void setResourcesScore(int resourcesScore) {
		this.resourcesScore = resourcesScore;
	}

	/**
	 * @return the cumulativeNum
	 */

	public int getCumulativeNum() {
		return cumulativeNum;
	}

	/**
	 * @param cumulativeNum
	 */
	public void setCumulativeNum(int cumulativeNum) {
		this.cumulativeNum = cumulativeNum;
	}

	/**
	 * @return the createTime
	 */

	public String getCreateTime() {
		return createTime;
	}

	/**
	 * @param createTime
	 */
	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

}
