package com.lzkj.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.SharedUtil;

/**
 * Created by Administrator on 2018/12/3.
 */

public class UpdateVolumeReceive extends BroadcastReceiver {

    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(UpdateVolumeReceive.class.getSimpleName(), true);

    /**
     * 缓存最新的音量值
     */
    public static final String CACHE_VOLUME = "cache_volume";

    @Override
    public void onReceive(Context context, Intent intent) {
        String volumeResult = intent.getStringExtra(CACHE_VOLUME);
        LogUtils.d(TAG, "onReceive", "volumeResult: " + volumeResult);
        SharedUtil.newInstance().setString(CACHE_VOLUME, volumeResult);
    }
}
