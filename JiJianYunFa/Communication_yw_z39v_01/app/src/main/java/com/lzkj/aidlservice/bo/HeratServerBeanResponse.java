package com.lzkj.aidlservice.bo;
/**
 * 心跳服务器返回的结果bean
 * @author changkai
 *
 */
public class HeratServerBeanResponse {

	private int code;

	private String note;

	private String dtm;

	private String workservers;

	private String heartserver;
	private String messageservers;
	private String redisservers;
	private String lic;
	public HeratServerBeanResponse() {}
	/**
	 * 回传给终端的恢复绑定信息的加密内容（前提是终端使用dcd和
	 * lid=0进行通讯注册且此终端之前绑定过公司目前还没有被注销），
	 * 包含了终端MAC或者机器码、终端唯一ID、授权唯一ID、终端绑定公司唯一ID和终端名称的加密信息
	 * lid,mac,did,devicename 使用";"分割
	 */
	public String getLicense() {
		return lic;
	}
	public void setLicense(String lic) {
		this.lic = lic;
	}
	/**
	 * 需要在返回后传给终端的信息
	 */
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	/**
	 * 0 - 表示成功注册；
		>0 - 表示出错，具体根据返回代码会有对应的note
	 */
	public String getNote() {
		return note;
	}
	public void setNote(String note) {
		this.note = note;
	}
	/**
	 * 返回服务器端的当前时间，以供终端进行时间同步校准工作
	 */
	public String getDtm() {
		return dtm;
	}
	public void setDtm(String dtm) {
		this.dtm = dtm;
	}
	/**
	 * 获取工作服务器信息列表
	 */
	public String getWorkservers() {
		return workservers;
	}
	public void setWorkservers(String workservers) {
		this.workservers = workservers;
	}
	/**
	 * 获取心跳服务器地址
	 * @return
	 */
	public String getHeartserver() {
		return heartserver;
	}
	public void setHeartserver(String heartserver) {
		this.heartserver = heartserver;
	}
	/**
	 * 获取注册服务器地址
	 * @return
	 */
	public String getMessageservers() {
		return messageservers;
	}
	public void setMessageservers(String messageservers) {
		this.messageservers = messageservers;
	}
	/**
	 * 获取redis服务器地址
	 * @return
	 */
	public String getRedisservers() {
		return redisservers;
	}
	public void setRedisservers(String redisservers) {
		this.redisservers = redisservers;
	}
	public String getLic() {
		return lic;
	}
	public void setLic(String lic) {
		this.lic = lic;
	}
	
}
