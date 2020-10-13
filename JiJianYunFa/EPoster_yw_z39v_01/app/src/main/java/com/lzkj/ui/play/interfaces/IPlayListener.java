package com.lzkj.ui.play.interfaces;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
/**
 *@author kchang changkai@lz-mr.com
 *@Description:播放监听接口
 *@time:2016年3月23日 下午3:46:20
 */
public interface IPlayListener {
	/**
	 * 当前播放的素材对象
	 * @param material
	 * 			要播放的素材对象
	 */
	void playing(Material material);
	/**
	 * 设置播放的素材类型
	 * @param type
	 * 		素材类型
	 */
	void setPlayType(int type);
	/**
	 *	预加载素材
	 * @param material
	 * 			被预加载的素材
	 */
	void prmLoadMaterial(Material material);
	/**
	 * 视频加载完成
	 */
	void videoLoadCompletion();
}