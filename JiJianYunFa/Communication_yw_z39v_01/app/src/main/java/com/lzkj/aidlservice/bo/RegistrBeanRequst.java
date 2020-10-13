package com.lzkj.aidlservice.bo;
/**
 * 注册的java bean
 * @author changkai
 *
 */
public class RegistrBeanRequst {
	/**
	 * 如果终端没有绑定信息，就提供该项信息。数据内容为：如果Android设备提供WIFI MAC地址信息
	 * ，对于Windows版本的客户端没有WIFI MAC就提供有线MAC或一定规则的机器码。MAC地址为12位：如50E549B7A472
	 */
	private String dcd;
	/**
	 * 终端对应的授权序列号唯一ID（LicenseID），没有的时候就传回0。
	 */
	private int lid;
	/**
	 * 客户命名的终端名称信息，客户用来识别标志这一终端的称呼
	 */
	private String name;
	/**
	 * 用户帐号ID
	 */
	private String userid;
	/**
	 * 用户帐号密码
	 */
	private String passwd;
	
	public RegistrBeanRequst() {}
	public String getDcd() {
		return dcd;
	}
	public void setDcd(String dcd) {
		this.dcd = dcd;
	}
	public int getLid() {
		return lid;
	}
	public void setLid(int lid) {
		this.lid = lid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getPasswd() {
		return passwd;
	}
	public void setPasswd(String passwd) {
		this.passwd = passwd;
	}
	
}
