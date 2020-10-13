package com.lzkj.aidlservice.push.handler;

import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月14日 下午4:58:07 
 * @version 1.0 
 * @parameter  推送消息处理类
 */
public abstract class PushHandler {
	/**
	 * 处理穿透消息的方法
	 * @param msgIntent 带有穿透消息的intent
	 */
	public abstract void handlerPerspective(Intent msgIntent);
}
