package com.yqhd.baizelocationlib.entity;

/**
  * @ClassName: ProvinceBo
  * @Description: TODO 省份
  * @author longyihuang
  * @date 2016年9月24日 上午10:49:03
  *
  */
public class ProvinceBo {
	private String proName;
	private Integer proId;
	private String proRemark;
	
	public ProvinceBo(){
	}
	
	public ProvinceBo(String proName, Integer proId, String proRemark) {
		super();
		this.proName = proName;
		this.proId = proId;
		this.proRemark = proRemark;
	}

	public String getProName() {
		return proName;
	}

	public void setProName(String proName) {
		this.proName = proName;
	}

	public Integer getProId() {
		return proId;
	}

	public void setProId(Integer proId) {
		this.proId = proId;
	}

	public String getProRemark() {
		return proRemark;
	}

	public void setProRemark(String proRemark) {
		this.proRemark = proRemark;
	}

	@Override
	public String toString() {
		return "ProvinceBo [proName=" + proName + ", proId=" + proId + ", proRemark=" + proRemark + "]";
	}
	
	
}
