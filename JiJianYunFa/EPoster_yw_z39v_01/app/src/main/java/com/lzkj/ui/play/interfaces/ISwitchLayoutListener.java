package com.lzkj.ui.play.interfaces;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;

/**
 * 
 *@author kchang changkai@lz-mr.com
 *@time:2015年7月3日 上午10:20:18
 *@Description:切换界面的接口
 */
public interface ISwitchLayoutListener {
	/**
	 * 切换默认界面的方法
	 * @param alerMsg 提示信息
	 */
	public void switchDefaultLayout(String alerMsg);
	
	/**
	 * 切换节目界面的方法
	 * @param program 节目对象
	 */
	public void switchProgramLayout(Program program);
	/**
	 * 预加载节目
	 * @param program
	 * 			节目对象
	 */
	public void preLoadProgram(Program program);
	/**
	 * 清除预加载
	 */
	public void clearPreProgram();
}
