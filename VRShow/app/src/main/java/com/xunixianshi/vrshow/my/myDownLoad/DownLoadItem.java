package com.xunixianshi.vrshow.my.myDownLoad;

import android.graphics.Bitmap;

public class DownLoadItem {
	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */
	private static final long serialVersionUID = 1L;	
	private int id;   //下载id
	private String downLoadType; // 下载类型
	private String resourcesId;

	private String downLoadIconUrl; // 下载图标连接地址
	private String downLoadUrl; // 下载地址
	private String downLoadName; // 下载名称

	private String videoType; //视频类型
	private String packageName; //游戏包名 以后再提到 子类去
	private String filePath;  //文件路径
	private String fileSize;   //文件大小
	private Bitmap videoCapture; //视频截图

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getFileSize() {
		return fileSize;
	}
	public void setFileSize(String fileSize) {
		this.fileSize = fileSize;
	}
	private boolean isSelect; //以前同事写在这里，泪崩有空就改
	
	public boolean isSelect() {
		return isSelect;
	}
	public void setSelect(boolean isSelect) {
		this.isSelect = isSelect;
	}
	public String getFilePath() {
		return filePath;
	}
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}
	public String getVideoType() {
		return videoType;
	}
	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
	public String getDownLoadType() {
		return downLoadType;
	}
	public void setDownLoadType(String downLoadType) {
		this.downLoadType = downLoadType;
	}
	public String getDownLoadIconUrl() {
		return downLoadIconUrl;
	}
	public void setDownLoadIconUrl(String downLoadIconUrl) {
		this.downLoadIconUrl = downLoadIconUrl;
	}
	public String getDownLoadUrl() {
		return downLoadUrl;
	}
	public void setDownLoadUrl(String downLoadUrl) {
		this.downLoadUrl = downLoadUrl;
	}
	public String getDownLoadName() {
		return downLoadName;
	}
	public void setDownLoadName(String downLoadName) {
		this.downLoadName = downLoadName;
	}
	
	public Bitmap getVideoCapture() {
		return videoCapture;
	}
	public void setVideoCapture(Bitmap videoCapture) {
		this.videoCapture = videoCapture;
	}
	public String getResourcesId() {
		return resourcesId;
	}
	public void setResourcesId(String resourcesId) {
		this.resourcesId = resourcesId;
	}
	
	
	public String getPackageName() {
		return packageName;
	}
	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}
}
