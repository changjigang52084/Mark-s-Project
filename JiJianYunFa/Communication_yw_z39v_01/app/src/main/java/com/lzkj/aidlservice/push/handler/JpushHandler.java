package com.lzkj.aidlservice.push.handler;

import android.content.Intent;
import android.os.Bundle;
import cn.jpush.android.api.JPushInterface;

import com.lzkj.aidlservice.push.control.CommandControl;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月14日 下午6:47:59 
 * @version 1.0 
 * @parameter  极光推送消息处理类
 */
public class JpushHandler extends PushHandler{
	private static final LogTag LOG_TAG = LogUtils.getLogTag(JpushHandler.class.getSimpleName(), true);
	/**处理控制命令的类**/
	private CommandControl mCommandControl;
	
	public JpushHandler() {
		mCommandControl = new CommandControl();
	}
	
	@Override
	public void handlerPerspective(Intent msgIntent) {
		if (null != msgIntent) {
			Bundle bundle = msgIntent.getExtras();
			if (null != bundle) {
		      if (JPushInterface.ACTION_REGISTRATION_ID.equals(msgIntent.getAction())) {
		    	  updateRegisterId(bundle);
		        } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(msgIntent.getAction())) {
		        	handlerExtraMessage(bundle);
		        }	
			}
		}
	}
	
	/**
	 * 更新register id
	 * @param bundle 包含消息的bundle对象
	 */
	private void updateRegisterId(Bundle bundle) {
	     String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
         SharedUtil.newInstance().setString(SharedUtil.REGISTRATION_ID, regId);
         LogUtils.d(LOG_TAG, "onReceive", "Registration Id : " + regId);
	}
	
	/**
	 * 处理推送的穿透消息
	 * @param bundle 包含消息的bundle对象
	 */
	private void handlerExtraMessage(Bundle bundle) {
		String extraMessage = bundle.getString(JPushInterface.EXTRA_MESSAGE);
		mCommandControl.handlerPayloadMsg(extraMessage);
	}
}
