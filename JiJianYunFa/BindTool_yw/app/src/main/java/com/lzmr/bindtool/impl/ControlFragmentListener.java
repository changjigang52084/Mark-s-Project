package com.lzmr.bindtool.impl;

import android.os.Bundle;
import android.support.v4.app.Fragment;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年8月3日 下午7:27:00 
 * @version 1.0 
 * @parameter  
 */
public interface ControlFragmentListener {
	/**
	 * 切换fragment
	 * @param fragmentId
	 * 			切换到哪一个fragment 
	 * @param bundle
	 * 			传递的数据
	 * @param addToBackStack
	 * 			是否加到栈内
	 */
	public void switchFragment(int fragmentId, Bundle bundle,boolean addToBackStack);
	/**
	 * 切换fragment 且 移除返回栈内的所有fragment
	 * @param fragmentId
	 * 			切换到哪一个fragment
	 * @param bundle
	 * 			传递的数据
	 */
	public void switchFragmentAndCloseOther(int fragmentId, Bundle bundle);
	/**
	 * 指定的fragment设为栈顶。清除其栈顶的fragment,
	 * @param fragmentName
	 */
	public void popBackStackToFragment(String fragmentName,int flag);
}
