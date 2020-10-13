package com.lzkj.ui.util;

import android.content.Context;
import android.media.AudioManager;

import com.lzkj.ui.app.EPosterApp;

/**
 * Created by Administrator on 2018/8/20.
 */

public class VolumeUtil {

    private static final LogUtils.LogTag LOG_TAG = LogUtils.getLogTag(VolumeUtil.class.getSimpleName(), true);

    public static void setDeviceVolume(String volumeRateStr) {
        try {
            if (StringUtil.isNullStr(volumeRateStr)) {
                return;
            }
            int volumeRate = Integer.parseInt(volumeRateStr);
            LogUtils.d(LOG_TAG, "volumeRate", volumeRate + "%");
            if (volumeRate >= 0 && volumeRate <= 100) {
                AudioManager audioManager = (AudioManager) EPosterApp.getApplication().getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int currentVolume = maxVolume * volumeRate / 100;
                LogUtils.d(LOG_TAG, "maxVolume and currentVolume", maxVolume + " and " + currentVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
