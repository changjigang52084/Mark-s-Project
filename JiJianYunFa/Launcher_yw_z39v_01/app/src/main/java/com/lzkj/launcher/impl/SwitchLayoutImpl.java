package com.lzkj.launcher.impl;

/**
 * @author kchang changkai@lz-mr.com
 * @time:2015年7月3日 上午10:20:18
 * @Description:切换界面的接口
 */
public interface SwitchLayoutImpl {
    /**
     * 切换界面的方法
     *
     * @param fragmentId  切换的界面(value =1 切换到无节目界面,2切换到播放界面,3切换到登陆界面)
     * @param prmFileInfo 要播放的内容
     * @param alertMsg    在无节目界面出现的提示信息
     */
    public void switchLayout(int fragmentId);
}
