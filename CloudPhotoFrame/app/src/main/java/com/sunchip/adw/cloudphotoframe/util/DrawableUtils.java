package com.sunchip.adw.cloudphotoframe.util;


import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;

/*
 * 控件图片设置类
 * */
public class DrawableUtils {

    public static DrawableUtils mDrawableUtils = new DrawableUtils();

    public DrawableUtils() {
    }

    public static DrawableUtils getInstance() {
        if (mDrawableUtils == null) {
            mDrawableUtils = new DrawableUtils();
        }
        return mDrawableUtils;

    }

    //1 给TextView设置drawableLeft，drawableRight，drawableTop，drawableBottom值
    public void SetDrawable(int mDrawable, TextView view, int direction,int color) {
        Drawable drawable = CloudFrameApp.getCloudFrameApp().getResources().getDrawable(
                mDrawable);
        //这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, drawable.getMinimumWidth(),
                drawable.getMinimumHeight());
        if (direction == 1) {
            view.setCompoundDrawables(drawable, null, null, null);
        } else if (direction == 2) {
            view.setCompoundDrawables(null, drawable, null, null);
        }else if (direction == 3) {
            view.setCompoundDrawables(null, null, drawable, null);
        }else if (direction == 4) {
            view.setCompoundDrawables(null, null, null, drawable);
        }
        view.setTextColor(CloudFrameApp.getCloudFrameApp().getResources().getColor(color));
    }

}
