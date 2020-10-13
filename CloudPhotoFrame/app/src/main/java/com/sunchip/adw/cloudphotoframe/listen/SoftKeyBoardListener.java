package com.sunchip.adw.cloudphotoframe.listen;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.Rect;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;

import com.sunchip.adw.cloudphotoframe.Timer.MyCountTimer;

import static android.content.Context.WINDOW_SERVICE;

public class SoftKeyBoardListener {

    public static boolean isSoftShowing(Activity activity, boolean l) {
        //获取当屏幕内容的高度
        int screenHeight = activity.getWindow().getDecorView().getHeight();
        //获取View可见区域的bottom
        Rect rect = new Rect();
        //DecorView即为activity的顶级view
        activity.getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
        //考虑到虚拟导航栏的情况（虚拟导航栏情况下：screenHeight = rect.bottom + 虚拟导航栏高度）
        //选取screenHeight*2/3进行判断

        //横屏
//        screenHeight大小:533  ==== rect.bottom:410  getSoftButtonsBarHeight:48   原来的状态:true更改后的状态:true
//        screenHeight大小:533  ==== rect.bottom:800  getSoftButtonsBarHeight:48   原来的状态:false更改后的状态:true
        //竖屏
//        screenHeight大小:853  ==== rect.bottom:933  getSoftButtonsBarHeight:48   原来的状态:false更改后的状态:true
//       screenHeight大小:853  ==== rect.bottom:1280  getSoftButtonsBarHeight:48   原来的状态:false更改后的状态:true

        //context的方法，获取windowManager
        WindowManager windowManager = (WindowManager) activity.getSystemService(WINDOW_SERVICE);
        //获取屏幕对象
        Display defaultDisplay = windowManager.getDefaultDisplay();
        //获取屏幕的宽、高，单位是像素
        int width = defaultDisplay.getWidth();
        int height = defaultDisplay.getHeight();
        Log.e("TAG", "width:" + width + "  ==== height:" + height + "   rect.bottom:" + rect.bottom + "   " + (rect.bottom == height + getSoftButtonsBarHeight(activity)));
        if (l)
            MyCountTimer.setStartMyCount("SoftKeyBoardListener");
     /*   Configuration mConfiguration = activity.getResources().getConfiguration(); //获取设置的配置信息
        int ori = mConfiguration.orientation; //获取屏幕方向
        if (ori == mConfiguration.ORIENTATION_LANDSCAPE) {
            //横屏
            return  screenHeight * 2 / 3 > rect.bottom;
        } else if (ori == mConfiguration.ORIENTATION_PORTRAIT) {
            //竖屏*/
        return !(rect.bottom == height + getSoftButtonsBarHeight(activity));
//        }


//        Log.e("TAG", "screenHeight大小:" + screenHeight * 2 / 3 + "  ==== rect.bottom:" + rect.bottom
//                + "  getSoftButtonsBarHeight:" + getSoftButtonsBarHeight(activity)


//                + "   原来的状态:" + (screenHeight * 2 / 3 > rect.bottom) +
//                "更改后的状态:" + (screenHeight - rect.bottom - getSoftButtonsBarHeight(activity) != 0));

//        return screenHeight - rect.bottom - getSoftButtonsBarHeight(activity) != 0;
    }

    /**
     * 底部虚拟按键栏的高度
     *
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static int getSoftButtonsBarHeight(Activity mActivity) {
        DisplayMetrics metrics = new DisplayMetrics();
        //这个方法获取可能不是真实屏幕的高度
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int usableHeight = metrics.heightPixels;
        //获取当前屏幕的真实高度
        mActivity.getWindowManager().getDefaultDisplay().getRealMetrics(metrics);
        int realHeight = metrics.heightPixels;
        if (realHeight > usableHeight) {
            return realHeight - usableHeight;
        } else {
            return 0;
        }
    }

}