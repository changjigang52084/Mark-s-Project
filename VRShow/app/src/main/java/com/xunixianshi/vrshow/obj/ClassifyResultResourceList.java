/**  
 * @Title: ClassifyResultResourceList.java
 * @Package com.xunixianshi.vrshow.obj
 * @Description: TODO(用一句话描述该文件做什么)
 * @author hechuang 
 * @date 2016年3月12日 下午4:14:37
 * @version V1.0  
 */
package com.xunixianshi.vrshow.obj;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @ClassName: ClassifyResultResourceList
 * @Description: TODO(这里用一句话描述这个类的作用)
 * @author hechuang
 * @date 2016年3月12日 下午4:14:37
 * 
 */

public class ClassifyResultResourceList implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = -2723924961724254891L;

	int tagId;
	String tagName;
	String tagIcon;
	int clickNumbers;
	int tagResourcesTotal;
	ArrayList<ClassifyResultListResourceList> resourcesList;

	/**
	 * @return the tagId
	 */

	public int getTagId() {
		return tagId;
	}

	/**
	 * @param tagId
	 */
	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	/**
	 * @return the tagName
	 */

	public String getTagName() {
		return tagName;
	}

	/**
	 * @param tagName
	 */
	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	/**
	 * @return the tagIcon
	 */

	public String getTagIcon() {
		return tagIcon;
	}

	/**
	 * @param tagIcon
	 */
	public void setTagIcon(String tagIcon) {
		this.tagIcon = tagIcon;
	}

	/**
	 * @return the clickNumbers
	 */

	public int getClickNumbers() {
		return clickNumbers;
	}

	/**
	 * @param clickNumbers
	 */
	public void setClickNumbers(int clickNumbers) {
		this.clickNumbers = clickNumbers;
	}

	public int getTagResourcesTotal() {
		return tagResourcesTotal;
	}

	public void setTagResourcesTotal(int tagResourcesTotal) {
		this.tagResourcesTotal = tagResourcesTotal;
	}

	/**
	 * @return the resourcesList
	 */

	public ArrayList<ClassifyResultListResourceList> getResourcesList() {
		return resourcesList;
	}

	/**
	 * @param resourcesList
	 */
	public void setResourcesList(
			ArrayList<ClassifyResultListResourceList> resourcesList) {
		this.resourcesList = resourcesList;
	}

}
