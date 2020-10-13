package com.lzmr.bindtool.util;

import com.lzmr.bindtool.api.LogoutRequestManager;

import de.greenrobot.event.EventBus;

/**
  * @ClassName: LogoutUtil
  * @Description: TODO 登出工具类
  * @author longyihuang
  * @date 2016年6月17日 上午11:19:23
  *
  */
public class LogoutUtil {
	public static void doLogout(boolean requestLogout){
		if(requestLogout){
			LogoutRequestManager logoutRequestManager = new LogoutRequestManager(null);
			logoutRequestManager.logout();
			ShareUtil.newInstance().removeKey(ShareUtil.USER_ACCOUNT);
			ShareUtil.newInstance().removeKey(ShareUtil.USER_PASSWORD);
			ShareUtil.newInstance().setBoolean(ShareUtil.REMEMBER_ACCOUNT, false);
		}
		ShareUtil.newInstance().removeKey(ShareUtil.SESSION);
		ShareUtil.newInstance().removeKey(ShareUtil.PUBLIC_KEY);
		ShareUtil.newInstance().removeKey(ShareUtil.USER_KEY);
//		EventBus.getDefault().post(new LogoutEvent(requestLogout));
	}
	
	public static class LogoutEvent{
		boolean requestLogout ;
		public LogoutEvent(boolean requestLogout){
			this.requestLogout = requestLogout;
		}
		public boolean isRequestLogout() {
			return requestLogout;
		}
		public void setRequestLogout(boolean requestLogout) {
			this.requestLogout = requestLogout;
		}
		
	}
}
