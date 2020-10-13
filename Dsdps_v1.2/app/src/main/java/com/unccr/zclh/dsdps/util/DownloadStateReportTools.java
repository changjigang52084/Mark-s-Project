package com.unccr.zclh.dsdps.util;


import android.content.Intent;

import com.unccr.zclh.dsdps.app.DsdpsApp;

import java.util.ArrayList;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月12日 上午10:22:07
 * @version 1.0
 * @parameter 下载状态汇报工具类
 */
public class DownloadStateReportTools {
    /**下载状态广播**/
    private static final String DOWNLOAD_STATE_ACTION = "com.yqkj.download.DOWNLOAD_STATE_ACTION";
    /***状态**/
    private static final String STATE = "state";
    /***http 地址**/
    private static final String HTTP_URL = "httpUrl";
    /***http 地址**/
    private static final String HTTP_LIST = "httpList";

    /**
     * 发送下载状态的广播
     * @param downloadState 下载状态
     * @param httpUrl 下载地址
     * @param downList 取消下载列表
     */
    public static void sendDownloadFileStateReceive(int downloadState, String httpUrl, ArrayList<String> downList) {
        Intent downloadStateIntent = new Intent(DOWNLOAD_STATE_ACTION);
        downloadStateIntent.putExtra(STATE, downloadState);
        if (null != downList && !downList.isEmpty()) {
            downloadStateIntent.putStringArrayListExtra(HTTP_LIST, downList);
        } else {
            downloadStateIntent.putExtra(HTTP_URL, httpUrl);
        }
        DsdpsApp.getDsdpsApp().sendBroadcast(downloadStateIntent);
    }
}
