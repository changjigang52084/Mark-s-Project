package com.unccr.zclh.dsdps.util;

import android.content.Context;
import android.media.AudioManager;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;

/**
 * Created by Administrator on 2018/8/20.
 */

public class VolumeUtil {

	private static final String TAG = "VolumeUtil";

	public static void setDeviceVolume(int volumeRateStr) {
		try {
			if(volumeRateStr == -1){
				return;
			}
			Log.d(TAG,"setDeviceVolume volumeRateStr: " + volumeRateStr + "%");
			if (volumeRateStr >= 0 && volumeRateStr <= 100) {
				AudioManager audioManager = (AudioManager) DsdpsApp.getDsdpsApp().getSystemService(Context.AUDIO_SERVICE);
				int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
				int currentVolume = maxVolume * volumeRateStr / 100;
				Log.d(TAG,"setDeviceVolume maxVolume: " + maxVolume + " ,currentVolume: " + currentVolume);
				audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume, 0);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
