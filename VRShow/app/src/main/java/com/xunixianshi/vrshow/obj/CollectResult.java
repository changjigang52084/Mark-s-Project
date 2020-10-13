package com.xunixianshi.vrshow.obj;

import java.io.Serializable;
import java.util.ArrayList;

public class CollectResult implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;

	String resCode;
	String resDesc;
	int total;
	int pageSize;
	ArrayList<CollectResultDataList> dataList;

	public String getResCode() {
		return resCode;
	}

	public void setResCode(String resCode) {
		this.resCode = resCode;
	}

	public String getResDesc() {
		return resDesc;
	}

	public void setResDesc(String resDesc) {
		this.resDesc = resDesc;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getPageSize() {
		return pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}

	public ArrayList<CollectResultDataList> getDataList() {
		return dataList;
	}

	public void setDataList(ArrayList<CollectResultDataList> dataList) {
		this.dataList = dataList;
	}

}
