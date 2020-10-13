package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

public class ClassifyVideoTypeResultTag implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;
	int tagId;
	String tagName;
	String tagIcon;
	int clickNumbers;
	int tagType;

	public int getTagId() {
		return tagId;
	}

	public void setTagId(int tagId) {
		this.tagId = tagId;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public String getTagIcon() {
		return tagIcon;
	}

	public void setTagIcon(String tagIcon) {
		this.tagIcon = tagIcon;
	}

	public int getClickNumbers() {
		return clickNumbers;
	}

	public void setClickNumbers(int clickNumbers) {
		this.clickNumbers = clickNumbers;
	}

	public int getTagType() {
		return tagType;
	}

	public void setTagType(int tagType) {
		this.tagType = tagType;
	}

}
