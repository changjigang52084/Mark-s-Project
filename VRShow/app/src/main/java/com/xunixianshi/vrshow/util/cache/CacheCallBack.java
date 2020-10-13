package com.xunixianshi.vrshow.util.cache;

import android.graphics.Bitmap;

public abstract class CacheCallBack {
    public abstract  void onBefore(Bitmap bitmap);
    public abstract void onAfter(String path);
}
