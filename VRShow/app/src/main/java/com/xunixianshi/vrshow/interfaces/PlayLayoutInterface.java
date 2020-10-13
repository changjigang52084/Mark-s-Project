package com.xunixianshi.vrshow.interfaces;

import com.xunixianshi.vrshow.obj.ClassifyVideoTypeResultResourcesList;

import java.util.ArrayList;

/**
 * 播放窗口内部操作接口回调
 * Created by xnxs on 2016/6/27.
 */
public interface PlayLayoutInterface {

    void back();//返回

    void onFocusViewNum(int num, boolean onFocus);

    void finishActivity();//关闭activity

    void setMaxPage(int maxPage);//关闭activity

    void backOnFocus(boolean onFocus);//返回是否选中

    void volumeAdd();//声音增大

    void volumeAddOnFocus(boolean onFocus);//声音增大是否选中

    void volumeSub();//声音减小

    void volumeSubOnFocus(boolean onFocus);//声音减小是否选中

    void showGridView(boolean show); //显示九宫格

    void showClassifyList(boolean show); //显示视频分类列表

    void showClassifyOnFocus(boolean onFocus); //显示视频分类列表是否选中

    void showVideoList(boolean show); //显示视频列表

    void showVideoOnFocus(boolean onFocus); //显示视频列表是否选中

    void setVideoList(ArrayList<ClassifyVideoTypeResultResourcesList> videoLists);

    void showEmptyPage(boolean isShow, String message);

    void showVrUILoading(boolean show);

    void updateVideoPage(String page);

    void initVideoView();
}
