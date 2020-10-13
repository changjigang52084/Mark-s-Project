package com.hch.viewlib.util;

import android.content.Context;

/**
 * MathUtil
 *
 * @author wlf(Andy)
 * @datetime 2015-12-11 21:40 GMT+8
 * @email 411086563@qq.com
 */
public class MathUtil {

    public static double formatNumber(double number) {
        return (Math.round(number * 100.00)) / 100.00;
    }

    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
