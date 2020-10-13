package com.sunchip.adw.cloudphotoframe.manager;

import android.content.ContentResolver;
import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;

import org.greenrobot.eventbus.EventBus;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.PlayPhotoVideo;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.ValumeUI;

/**
 * Created by yingmuliang on 2019/12/25.
 */
public class AudioManger {
    public static AudioManger mAudioManger = new AudioManger();

    public AudioManger() {
    }

    public static AudioManager mAudioManager;

    public static AudioManger getInstance() {
        if (mAudioManger == null) {
            mAudioManger = new AudioManger();
        }

        if (mAudioManager == null) {
            mAudioManager = (AudioManager) CloudFrameApp.getCloudFrameApp().getSystemService(Context.AUDIO_SERVICE);
        }
        return mAudioManger;
    }

    //调用系统的音量控件设置音量
    public void SetMusicVolume(int Music, boolean IsUi) {
        if (!CloudFrameApp.getCloudFrameApp().IsClock) {
            if (Music >= getmusicMaxVolume()) {
                Music = getmusicMaxVolume();
            } else if (Music <= 0) {
                Music = 1;
            }
            if (IsUi)
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        Music,
                        AudioManager.FLAG_SHOW_UI);
            else mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                    Music,
                    AudioManager.FLAG_PLAY_SOUND);
            EventBus.getDefault().post(new BaseErrarEvent("", ValumeUI));
        }
        Log.e("TAG", "Music==================:" + Music);
        SharedUtil.newInstance().setInt("Music", Music);
    }

    //获取当前音量
    public int getmusicVolume() {
        return mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
    }

    //获取最大音量
    public int getmusicMaxVolume() {
        return mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
    }

    //静音设置
    public void setUnmute(boolean IsAdjust) {
        if (IsAdjust)
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_MUTE, 0);
        else
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_UNMUTE, 0);
    }

    /**
     * 获得系统亮度
     *
     * @return
     */
    public int getSystemBrightness() {
        int systemBrightness = 0;
        try {
            systemBrightness = Settings.System.getInt(CloudFrameApp.getCloudFrameApp().getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return systemBrightness;
    }


    //设置亮度

    public void setScreenBrightness(int process, Context context) {
        saveBrightness(context.getContentResolver(), process);
    }

    private void saveBrightness(ContentResolver resolver, int brightness) {
        Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS,
                Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);


        Uri uri = android.provider.Settings.System.getUriFor(Settings.System.SCREEN_BRIGHTNESS);
        android.provider.Settings.System.putInt(resolver, Settings.System.SCREEN_BRIGHTNESS, brightness);
        resolver.notifyChange(uri, null);
    }
}
