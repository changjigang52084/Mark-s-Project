package com.xunixianshi.vrshow.obj;

import java.io.Serializable;

public class HomeResultVRHot implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = -1007013946788826467L;

	private String clientName;
	private int playerNumbers;
	private String resourcesCoverImg;
	private String resourcesId;
	private String resourcesIntroduce;
	private String resourcesSubTitle;
	private String resourcesTitle;
	private String resourcesUrl;
	private int resourcesType;

	
	
	public HomeResultVRHot(String clientName, int playerNumbers,
						   String resourcesCoverImg, String resourcesId,
						   String resourcesIntroduce, String resourcesSubTitle,
						   String resourcesTitle, String resourcesUrl, int resourcesType) {
		super();
		this.clientName = clientName;
		this.playerNumbers = playerNumbers;
		this.resourcesCoverImg = resourcesCoverImg;
		this.resourcesId = resourcesId;
		this.resourcesIntroduce = resourcesIntroduce;
		this.resourcesSubTitle = resourcesSubTitle;
		this.resourcesTitle = resourcesTitle;
		this.resourcesUrl = resourcesUrl;
		this.resourcesType = resourcesType;
	}

	public String getClientName() {
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public int getPlayerNumbers() {
		return playerNumbers;
	}

	public void setPlayerNumbers(int playerNumbers) {
		this.playerNumbers = playerNumbers;
	}

	public String getResourcesCoverImg() {
		return resourcesCoverImg;
	}

	public void setResourcesCoverImg(String resourcesCoverImg) {
		this.resourcesCoverImg = resourcesCoverImg;
	}

	public String getResourcesId() {
		return resourcesId;
	}

	public void setResourcesId(String resourcesId) {
		this.resourcesId = resourcesId;
	}

	public String getResourcesIntroduce() {
		return resourcesIntroduce;
	}

	public void setResourcesIntroduce(String resourcesIntroduce) {
		this.resourcesIntroduce = resourcesIntroduce;
	}

	public String getResourcesSubTitle() {
		return resourcesSubTitle;
	}

	public void setResourcesSubTitle(String resourcesSubTitle) {
		this.resourcesSubTitle = resourcesSubTitle;
	}

	public String getResourcesTitle() {
		return resourcesTitle;
	}

	public void setResourcesTitle(String resourcesTitle) {
		this.resourcesTitle = resourcesTitle;
	}

	public String getResourcesUrl() {
		return resourcesUrl;
	}

	public void setResourcesUrl(String resourcesUrl) {
		this.resourcesUrl = resourcesUrl;
	}

	public int getResourcesType() {
		return resourcesType;
	}

	public void setResourcesType(int resourcesType) {
		this.resourcesType = resourcesType;
	}
}
