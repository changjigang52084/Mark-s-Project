package com.hch.viewlib.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;

import java.util.Map;

/**
 * Util for {@link Map}
 *
 * @author wlf(Andy)
 * @datetime 2015-11-14 18:12 GMT+8
 * @email 411086563@qq.com
 */
public class MapUtil {

    /**
     * Returns true if the collection is null or 0-length.
     *
     * @param map the map to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(Map<?, ?> map) {
        if (map == null || map.isEmpty() || map.size() == 0) {
            return true;
        } else {
            return false;
        }
    }
    /**
     * 获取视频文件的截图封面
     *
     * @param videoPath
     * @param width
     *            单位：dp
     * @param height
     *            单位：dp
     * @param kind
     * @param context
     *
     * @return
     */
    public static Bitmap getVedioBitmap(String videoPath, int width,
                                        int height, int kind, Context context) {
        Bitmap bitmap = null;
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        DensityUtils.dp2px(context, width);
        try {
            bitmap = ThumbnailUtils.extractThumbnail(bitmap,
                    DensityUtils.dp2px(context, width),
                    DensityUtils.dp2px(context, height),
                    ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        } catch (Exception ex) {
            bitmap = null;
        }
        if (bitmap != null) {
            return bitmap;
        }
        return bitmap;
    }
}
