package com.lzkj.downloadservice.bean;
/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年9月10日 上午11:11:28 
 * @version 1.0 
 * @parameter 流量控制以后等待下载的bo
 */
public class WaitDownloadBo {
	/**下载地址**/
	public String downloadUrl;
	/**节目的key**/
	public String prmKey;
	/**文件大小**/
	public long fileSize;
	/**文件类型**/
	public int type;
}
