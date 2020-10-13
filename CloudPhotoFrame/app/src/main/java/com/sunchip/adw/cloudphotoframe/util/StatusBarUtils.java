package com.sunchip.adw.cloudphotoframe.util;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.view.WindowManager;

import com.gyf.immersionbar.BarHide;
import com.gyf.immersionbar.ImmersionBar;

/**
 * Created by yingmuliang on 2020/1/8.
 */
public class StatusBarUtils {

    public static StatusBarUtils mStatusBarUtils = new StatusBarUtils();

    public StatusBarUtils() {
    }

    public static StatusBarUtils getInstance() {
        if (mStatusBarUtils == null) {
            mStatusBarUtils = new StatusBarUtils();
        }
        return mStatusBarUtils;
    }

    public void ImageStausBar(final Activity activity, int statusBarColor) {
        ImmersionBar.with(activity)
                .hideBar(BarHide.FLAG_HIDE_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
                .addTag("tag")  //给以上设置的参数打标记
                .getTag("tag")  //根据tag获得沉浸式参数
                .init();  //必须调用方可应用以上所配置的参数

        UiOptions(activity);
        activity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                UiOptions(activity);
            }
        });

    }

    //dialog使用
    public void ImageStausBarDialog(final Activity activity, final Dialog dialog) {
        ImmersionBar.with(activity, dialog)
                .hideBar(BarHide.FLAG_HIDE_BAR)  //隐藏状态栏或导航栏或两者，不写默认不隐藏
                .addTag("tag")  //给以上设置的参数打标记
                .getTag("tag")  //根据tag获得沉浸式参数
                .init();  //必须调用方可应用以上所配置的参数

        UiOptions(activity);
        activity.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                UiOptions(activity);
            }
        });

        UiOptions(dialog);
        dialog.getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                UiOptions(dialog);
            }
        });

    }

    private void UiOptions(final Dialog dialog) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        uiOptions |= 0x00001000;

        dialog.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }


    private void UiOptions(Activity activity) {
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                //布局位于状态栏下方
                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN |
                //隐藏导航栏
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
        uiOptions |= 0x00001000;
        activity.getWindow().getDecorView().setSystemUiVisibility(uiOptions);
    }


    //dialog 销毁
    public void BersionBar(Activity activity, Dialog dialog){
        ImmersionBar.with(activity, dialog).init();
    }
}
