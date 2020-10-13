package com.unccr.zclh.dsdps.play.interfaces;

import com.unccr.zclh.dsdps.models.Material;

/**
 * @author  jigangchang Email:changjigang@sunchip.com
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午3:15:37
 * @parameter IPlayListener 播放监听接口
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
