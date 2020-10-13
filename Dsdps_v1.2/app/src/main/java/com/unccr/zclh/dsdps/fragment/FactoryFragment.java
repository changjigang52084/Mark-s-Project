package com.unccr.zclh.dsdps.fragment;

import android.util.Log;

import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.util.Constants;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午7:12:24
 * @parameter FactoryFragment
 */
public class FactoryFragment {

    private static final String TAG = "FactoryFragment";

    private DefaultFragment defaultFragment = new DefaultFragment(null);
    private static volatile FactoryFragment factoryFragment = null;

    public static FactoryFragment newInstance() {
        if (null == factoryFragment) {
            synchronized (FactoryFragment.class) {
                if (null == factoryFragment) {
                    factoryFragment = new FactoryFragment();
                }
            }
        }
        return factoryFragment;
    }

    public DefaultFragment creatDefaultFragment() {
        return defaultFragment;
    }

    public synchronized BaseFragment creatFragment(Area area, boolean isPreLoad) {
        if (null == area || null == area.getT()) {
            return null;
        }
        BaseFragment baseFragment = null;
        Log.d(TAG,"creatFragment type: " + area.getT());
        switch (area.getT()) {
            case Constants.DATE_FRAGMENT: // 时钟碎片
                baseFragment = new FragmentClock(area);
                break;
            case Constants.WEIBO_FRAGMENT: // 网页碎片
                baseFragment = new WebViewFragment(area);
                break;
            case Constants.TEXT_FRAGMENT: // 文本碎片
                baseFragment = new FragmentText(area, isPreLoad);
                break;
            default:
                baseFragment = new VideoFragment(area, isPreLoad); // 图片碎片或者视频碎片
                break;
        }
        return baseFragment;
    }
}
