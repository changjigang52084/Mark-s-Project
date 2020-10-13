package com.unccr.zclh.dsdps.play.interfaces;

import com.unccr.zclh.dsdps.models.Program;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 上午10:41:21
 * @parameter ISwitchLayoutListener
 */
public interface ISwitchLayoutListener {

    /**
     * 切换默认界面的方法
     * @param alertMsg 提示信息
     */
    public void switchDefaultLayout(String alertMsg);

    /**
     * 切换节目界面的方法
     * @param program 节目对象
     */
    public void switchProgramLayout(Program program);

    public void switchPlayLayout(ArrayList<String> str,boolean isPicProgramme,boolean isAnfu);

    /**
     * 预加载节目
     * @param program 节目对象
     */
    public void preLoadProgram(Program program);

    /**
     * 清除预加载的节目
     */
    public void clearPreProgram();
}
