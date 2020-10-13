package com.xunixianshi.vrshow.obj;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassifyVideoTypeResult implements Serializable {

	/**
	 * @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么)
	 */

	private static final long serialVersionUID = 1L;
	String resCode;
	String resDesc;
	ClassifyVideoTypeResultTag tag;
	ArrayList<ClassifyVideoTypeResultResourcesList> resourcesList;
	int total;
	int queryCount;

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

	public ClassifyVideoTypeResultTag getTag() {
		return tag;
	}

	public void setTag(ClassifyVideoTypeResultTag tag) {
		this.tag = tag;
	}

	public ArrayList<ClassifyVideoTypeResultResourcesList> getResourcesList() {
		return resourcesList;
	}

	public void setResourcesList(
			ArrayList<ClassifyVideoTypeResultResourcesList> resourcesList) {
		this.resourcesList = resourcesList;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getQueryCount() {
		return queryCount;
	}

	public void setQueryCount(int queryCount) {
		this.queryCount = queryCount;
	}

}
