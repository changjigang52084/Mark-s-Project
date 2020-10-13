package com.xunixianshi.vrshow.obj;

public class ApkInfo {
	private String packageName;
	private int versionCode;
	private String name;

	public ApkInfo() {
		packageName = "";
		versionCode = 0;
		name = "";
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPackageName() {
		return packageName;
	}

	public void setPackageName(String packageName) {
		this.packageName = packageName;
	}

	public int getVersionCode() {
		return versionCode;
	}

	public void setVersionCode(int versionCode) {
		this.versionCode = versionCode;
	}
}
