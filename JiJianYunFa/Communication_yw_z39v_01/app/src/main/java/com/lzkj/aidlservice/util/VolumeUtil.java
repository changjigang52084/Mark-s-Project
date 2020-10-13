package com.lzkj.aidlservice.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.lzkj.aidlservice.app.CommunicationApp;

import static com.igexin.assist.sdk.AssistPushConsts.LOG_TAG;

/**
 * 设备音量设置类
 *
 * @author lyhuang
 * @version 1.0
 * @date 2016-1-11 下午4:55:54
 */
public class VolumeUtil {

    private static final String TAG = "VolumeUtil";

    public static void setDeviceVolume(String volumeRateStr) {
        try {
            int volumeRate = Integer.parseInt(volumeRateStr);
            Log.d(TAG,"volumeRate: " + volumeRate + "%");
            if (volumeRate >= 0 && volumeRate <= 100) {
                AudioManager audioManager = (AudioManager) CommunicationApp.get().getSystemService(Context.AUDIO_SERVICE);
                int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                int currentVolume = maxVolume * volumeRate / 100;
                Log.d(TAG,"maxVolume and currentVolume: " + maxVolume + " and " + currentVolume);
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
