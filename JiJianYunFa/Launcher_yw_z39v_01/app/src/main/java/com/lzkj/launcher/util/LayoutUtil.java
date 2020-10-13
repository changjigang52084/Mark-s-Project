package com.lzkj.launcher.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;

import com.lzkj.launcher.app.LauncherApp;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class LayoutUtil {

    private static final String TAG = "LayoutUtil";

    /**
     * get fullscreen layout params
     *
     * @return
     */
    public static FrameLayout.LayoutParams getFullScreenLayoutParams() {
        int width, height;
        width = ConfigSettings.SCREEN_WIDTH;
        height = ConfigSettings.SCREEN_HEIGHT;
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(width, height);
        return params;
    }

    public static int getRealDisplayWidth() {
        int widthtPixels;
        WindowManager w = ((WindowManager) LauncherApp.getApplication().getSystemService(Context.WINDOW_SERVICE));
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        widthtPixels = metrics.widthPixels;
        // includes window decorations (statusbar bar/navigation bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                widthtPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
            } catch (Exception e) {
                Log.e(TAG, "getRealDisplayWidth_1" + e.getMessage());
            }
        } else if (Build.VERSION.SDK_INT >= 17) { // includes window decorations (statusbar bar/navigation bar)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                widthtPixels = realSize.x;
            } catch (Exception e) {
                Log.e(TAG, "getRealDisplayWidth_1" + e.getMessage());
            }
        }
        Log.d(TAG, "getRealDisplayWidth_1 realWidthPixels: " + widthtPixels);
        return widthtPixels;
    }

    public static int getRealDisplayHeight() {
        int heightPixels;
        WindowManager w = ((WindowManager) LauncherApp.getApplication().getSystemService(Context.WINDOW_SERVICE));
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        d.getMetrics(metrics);
        // since SDK_INT = 1;
        heightPixels = metrics.heightPixels;
        // includes window decorations (statusbar bar/navigation bar)
        if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
            try {
                heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
            } catch (Exception e) {
                Log.e(TAG, "getRealDisplayHeight_1" + e.getMessage());
            }
        } else if (Build.VERSION.SDK_INT >= 17) { // includes window decorations (statusbar bar/navigation bar)
            try {
                Point realSize = new Point();
                Display.class.getMethod("getRealSize", Point.class).invoke(d, realSize);
                heightPixels = realSize.y;
            } catch (Exception e) {
                Log.e(TAG, "getRealDisplayHeight_1" + e.getMessage());
            }
        }
        Log.d(TAG, "getRealDisplayHeight_1 realHightPixels: " + heightPixels);
        return heightPixels;
    }
}
