package com.lzkj.aidlservice.bo;
public class AppInfo {
		private String appName ;
		private int appVersionCode;
		private String appPck;
		private String appVersionName;
		private boolean appIfRun;
		public String getAppName() {
			return appName;
		}
		public void setAppName(String appName) {
			this.appName = appName;
		}
		public int getAppVersionCode() {
			return appVersionCode;
		}
		public void setAppVersionCode(int appVersionCode) {
			this.appVersionCode = appVersionCode;
		}
		public String getAppPck() {
			return appPck;
		}
		public void setAppPck(String appPck) {
			this.appPck = appPck;
		}
		public String getAppVersionName() {
			return appVersionName;
		}
		public void setAppVersionName(String appVersionName) {
			this.appVersionName = appVersionName;
		}
		public boolean isAppIfRun() {
			return appIfRun;
		}
		public void setAppIfRun(boolean appIfRun) {
			this.appIfRun = appIfRun;
		}
		
	}