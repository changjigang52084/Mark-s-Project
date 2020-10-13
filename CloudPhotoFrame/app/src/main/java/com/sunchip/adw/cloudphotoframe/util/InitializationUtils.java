package com.sunchip.adw.cloudphotoframe.util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.example.SunchipFile.File_Message;
import com.google.gson.Gson;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.GetHolderEvent;
import com.sunchip.adw.cloudphotoframe.manager.AudioManger;
import com.sunchip.adw.cloudphotoframe.model.FrameSettingsModel;
import com.sunchip.adw.cloudphotoframe.model.FrameSlideshowModel;
import com.sunchip.adw.cloudphotoframe.model.SaveFrameSettings;

import java.util.Locale;

/**
 * Created by yingmuliang on 2020/1/10.
 * <p>
 * 用作初始化设置的各种属性内容
 */
public class InitializationUtils {
    public static InitializationUtils mInitializationUtils = new InitializationUtils();

    public InitializationUtils() {
    }

    public static InitializationUtils getInstance() {
        if (mInitializationUtils == null) {
            mInitializationUtils = new InitializationUtils();
        }
        return mInitializationUtils;
    }

    //初始化所有的设置值
    public void setInitialization(Context context) {
        //设置默认值 默认音量为6
        AudioManger.getInstance().SetMusicVolume(8, false);
        //设置亮度 默认亮度为255
        AudioManger.getInstance().setScreenBrightness(255, (Activity) context);
        //默認英語
        SharedUtil.newInstance().setInt("isLanguage", 0);
        SystemInterfaceUtils.getInstance().setConfiguration((Activity) context, Locale.ENGLISH, false);
        //SettingsFragment getResources().getStringArray(R.array.AlphaAnimation)[1]
        SharedUtil.newInstance().setInt("playbackMode", 0);//默认全部照片
        SharedUtil.newInstance().setInt("shuffle", 0); //默认关闭随机播放
        SharedUtil.newInstance().setInt("transitionType", 0);//播放动画默认第一个
        SharedUtil.newInstance().setInt("transitionTime", 1);//动画时间默认5秒

        SharedUtil.newInstance().setInt("motionSensor", 1);//人体感应设置
        SystemInterfaceUtils.getInstance().setPIR(true, 10);//人体感应默认开启
        SharedUtil.newInstance().setInt("whenFrameGoesToSleep", 0);//睡眠状态

        //ShowHideFragment
        SystemInterfaceUtils.getInstance().setTime24(false); //默认12小时制
        SharedUtil.newInstance().setInt("showClock", 1);
        SharedUtil.newInstance().setInt("showCaption", 0);//默认不显示标题

        SharedUtil.newInstance().setString("MusicSetting",context.getResources().getString(R.string.on));
    }

    //设置值 从服务器获取的值设置
    public void SetGetHolder(GetHolderEvent getHolderEvent, Context context) {

        //设置默认值 默认音量为8
        AudioManger.getInstance().SetMusicVolume(getHolderEvent.getFrameConfig().getVolume(), false);
        //设置亮度 默认亮度为176
        AudioManger.getInstance().setScreenBrightness(getHolderEvent.getFrameConfig().getBrightness(), context);

        //SettingsFragment getResources().getStringArray(R.array.AlphaAnimation)[1]
        SharedUtil.newInstance().setInt("playbackMode", getHolderEvent.getFrameConfig().getAlwaysShowRecent());//默认全部照片
        SharedUtil.newInstance().setInt("shuffle", getHolderEvent.getPlaylistConfig().getShuffle()); //默认关闭随机播放
        SharedUtil.newInstance().setInt("transitionType", getHolderEvent.getPlaylistConfig().getTransitionType());//播放动画默认第一个
        SharedUtil.newInstance().setInt("transitionTime", getHolderEvent.getPlaylistConfig().getDisplayDuration());//动画时间默认5秒

        Log.e("SetGetHolder", "设置的状态是:" + getHolderEvent.getFrameConfig().getMotion());
        SharedUtil.newInstance().setInt("motionSensor", getHolderEvent.getFrameConfig().getMotion());//人体感应设置

        //息屏状态不更改值 只保存状态
        if (CloudFrameApp.IsScreen) {
            if (getHolderEvent.getFrameConfig().getMotion() == 1)
                SystemInterfaceUtils.getInstance().setPIR(true, 10);
            else SystemInterfaceUtils.getInstance().setPIR(false, 10);
        }


        if (getHolderEvent.getFrameConfig().getSleepClock() >= 1)
            SharedUtil.newInstance().setInt("whenFrameGoesToSleep", 1);//睡眠状态
        else SharedUtil.newInstance().setInt("whenFrameGoesToSleep", 0);//睡眠状态

        //ShowHideFragment
        int clockSlideshow = getHolderEvent.getPlaylistConfig().getClockSlideshow();
        SharedUtil.newInstance().setInt("showClock", clockSlideshow);
        if (clockSlideshow == 2) {
            SystemInterfaceUtils.getInstance().setTime24(true);
        } else if (clockSlideshow == 1) {
            SystemInterfaceUtils.getInstance().setTime24(false); //默认12小时制
        }
        SharedUtil.newInstance().setInt("showCaption", getHolderEvent.getPlaylistConfig().getCaptions());//默认不显示标题


        //时区
        SharedUtil.newInstance().setInt("TimeZone", getHolderEvent.getFrameConfig().getTimezone());
        //直接设置时区
        SystemInterfaceUtils.getInstance().SetTimeZone(CloudFrameApp.getCloudFrameApp().getResources().
                getStringArray(R.array.timezones)
                [getHolderEvent.getFrameConfig().getTimezone()]);
    }


    //设置值 上传到服务器更新值 并且使用文件保存 有网的就更新
    public String setGetHolder(Context context) {

        SaveFrameSettings mSaveFrameSettings = new SaveFrameSettings();
        FrameSettingsModel mFrameSettingsModel = new FrameSettingsModel();
        mFrameSettingsModel.setAlwaysShowRecent(SharedUtil.newInstance().getInt("playbackMode"));
        mFrameSettingsModel.setMotion(SharedUtil.newInstance().getInt("motionSensor"));
        mFrameSettingsModel.setSleepClock(SharedUtil.newInstance().getInt("whenFrameGoesToSleep"));
        mFrameSettingsModel.setTimezone(SharedUtil.newInstance().getInt("TimeZone"));

        mFrameSettingsModel.setVolume(SharedUtil.newInstance().getInt("Music"));
        mFrameSettingsModel.setBrightness(AudioManger.getInstance().getSystemBrightness());

        mSaveFrameSettings.setFrameSettings(mFrameSettingsModel);

        FrameSlideshowModel mFrameSlideshowModel = new FrameSlideshowModel();

        mFrameSlideshowModel.setShuffle(SharedUtil.newInstance().getInt("shuffle"));
        mFrameSlideshowModel.setClockSlideshow(SharedUtil.newInstance().getInt("showClock"));
        mFrameSlideshowModel.setCaptions(SharedUtil.newInstance().getInt("showCaption"));
        mFrameSlideshowModel.setTransitionType(SharedUtil.newInstance().getInt("transitionType"));
        mFrameSlideshowModel.setDisplayDuration(SharedUtil.newInstance().getInt("transitionTime"));

        mSaveFrameSettings.setFrameSlideshow(mFrameSlideshowModel);

        if (CloudFrameApp.getCloudFrameApp().mGetHolderEvent != null)
            mSaveFrameSettings.setFrameId(CloudFrameApp.getCloudFrameApp().mGetHolderEvent.getFrameInfo().getId());
        Log.e("TAG", "直接设置的json值是:" + new Gson().toJson(mSaveFrameSettings));
        File_Message.write(new Gson().toJson(mSaveFrameSettings), "SaveFrameSetting.txt", context);
        String Json = File_Message.read("SaveFrameSetting.txt", context);
        if (Json == null) {
            Json = new Gson().toJson(mSaveFrameSettings);
        }
        Log.e("TAG", "Json:" + Json);
        return Json;
    }
}
