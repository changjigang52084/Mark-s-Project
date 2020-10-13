package com.lzkj.ui.fragment;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/**
 * 根据不同类型的创建不同的fragment
 *
 * @author changkai
 */
public class FactoryFragment {
    private static final LogTag TAG = LogUtils.getLogTag(FactoryFragment.class.getSimpleName(), true);
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
            LogUtils.e(TAG, "creatFragment", "areaBean is null");
            return null;
        }
        LogUtils.d(TAG, "creatFragment", "areaBean is type: " + area.getT());
        BaseFragment baseFragment = null;
        switch (area.getT()) {
            case Constants.DATE_FRAGMENT: // 时钟碎片
                baseFragment = new FragmentClock(area);
                break;
            case Constants.WEATHER_FRAGMENT: // 天气碎片
                baseFragment = new FragmentWeather(area);
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
