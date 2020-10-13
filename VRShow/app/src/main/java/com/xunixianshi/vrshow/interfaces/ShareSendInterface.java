package com.xunixianshi.vrshow.interfaces;

/**
 * Created by Administrator on 2016/10/14.
 */

public interface ShareSendInterface {
    void share(String videoIntroduce,String videoIconUrl,String videoName,int videoId);
    void downLoad(int sourceId,String videoIconUrl,String videoName);
}
