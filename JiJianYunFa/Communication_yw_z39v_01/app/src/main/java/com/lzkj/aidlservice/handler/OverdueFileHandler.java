package com.lzkj.aidlservice.handler;

import com.lzkj.aidlservice.util.DeleteOverdueLogUtil;
import com.lzkj.aidlservice.util.OverdueProgramUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月15日 上午10:36:56 
 * @version 1.0 
 * @parameter 过期文件处理类(节目，素材，日志) 
 */
public class OverdueFileHandler {
	
	/**
	 * 删除过期节目，素材，日志
	 */
	public void delOverdueFile() {
		//删除过期节目和素材
		ThreadPoolManager.get().addRunnable(new OverdueProgramUtil());
		//删除过期日志
		DeleteOverdueLogUtil.deleteOverdueLog();
	}
}
