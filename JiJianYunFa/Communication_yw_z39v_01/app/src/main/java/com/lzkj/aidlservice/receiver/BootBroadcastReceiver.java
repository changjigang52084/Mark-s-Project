package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.api.sync.RequestSyncAppVersionRunnable;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月2日 下午8:07:56
 * @parameter 拦截开机启动的广播(未使用)
 */
public class BootBroadcastReceiver extends BroadcastReceiver {

    private static final LogTag TAG_LOG = LogUtils.getLogTag(BootBroadcastReceiver.class.getSimpleName(), true);

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d(TAG_LOG, "onReceive", " action: " + intent.getAction());
        requestSyncAppVersion();
    }

    /**
     * 向服务器请求同步app版本信息
     */
    private void requestSyncAppVersion() {
        CommunicationApp.get()
                .mAppHandler
                .postDelayed(
                        new RequestSyncAppVersionRunnable("requestSyncAppVersion"),
                        1000 * 60 * 3);
    }
}
