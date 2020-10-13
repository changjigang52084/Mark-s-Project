package com.hch.viewlib.customview.viewpagercycletes;

/**
 * 描述：广告信息</br>
 */
public class ADInfo {
	int resId;
	String id = "";
	String url = "";
	String content = "";
	String type = "";

	int targetType;
	String targetValue;

	String bannerName;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}
	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public int getTargetType() {
		return targetType;
	}

	public void setTargetType(int targetType) {
		this.targetType = targetType;
	}

	public String getTargetValue() {
		return targetValue;
	}

	public void setTargetValue(String targetValue) {
		this.targetValue = targetValue;
	}
	public String getBannerName() {
		return bannerName;
	}

	public void setBannerName(String bannerName) {
		this.bannerName = bannerName;
	}
}
