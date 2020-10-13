package com.lzkj.downloadservice.bean;
/**
 * 上传记录信息
 * @author changkai
 *
 */
public class UploadInfo {
	private String uploadFilePath;
	private String objectKey;
	public UploadInfo(String uploadFilePath,String obejctKey) {
		this.objectKey =obejctKey;
		this.uploadFilePath = uploadFilePath;
	}
	/**
	 * 需要上传的本地文件的路径
	 * @return
	 */
	public String getUploadFilePath() {
		return uploadFilePath;
	}
	public void setUploadFilePath(String uploadFilePath) {
		this.uploadFilePath = uploadFilePath;
	}
	/**
	 * 获取上传的objectKey
	 * @return
	 */
	public String getObjectKey() {
		return objectKey;
	}
	public void setObjectKey(String objectKey) {
		this.objectKey = objectKey;
	}
	
}
