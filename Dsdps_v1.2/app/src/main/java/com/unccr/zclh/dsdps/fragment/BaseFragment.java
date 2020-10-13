package com.unccr.zclh.dsdps.fragment;

import android.app.Fragment;
import android.graphics.Canvas;
import android.os.Build;
import android.os.Handler;
import android.os.Message;

import com.unccr.zclh.dsdps.models.Area;

import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午5:06:38
 * @parameter BaseFragment
 */
public abstract class BaseFragment extends Fragment {

    private static final String TAG = "BaseFragment";

    protected Area area;

    public BaseFragment(Area area) {
        this.area = area;
    }

    public BaseFragment() {
    }

    @Override
    public void onStart() {
        startLoadingTask();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        stopPlayback();
        super.onDestroy();
    }

    @Override
    public void onResume() {
        resumePlayback();
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    /**
     * 开始播放节目
     */
    public void startPlayPrograme() {

    }

    /**
     * Starts to load the fragment information. Here you should start any loader
     * tasks. This method is executed on Main Thread
     */
    protected abstract void startLoadingTask();

    /**
     * 获取这个fragment的截图，并画到传入的canvas上，由子类fragment重写此方法
     *
     * @param canvas 画在指定canvas上
     */
    public void getScreenshot(Canvas canvas) {
    }


    /**
     * This method will pause this module
     */
    public abstract void pausePlayback();

    /**
     * onDestroy或者节目切换时候停止当前fragment播放
     */
    public abstract void stopPlayback();

    /**
     * This method will resume this module
     */
    public abstract void resumePlayback();

    protected static class BaseHandler extends Handler {
        private WeakReference<BaseFragment> mBaseFragmentWeakReference;

        public BaseHandler(BaseFragment baseFragment) {
            mBaseFragmentWeakReference = new WeakReference<BaseFragment>(baseFragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (null != mBaseFragmentWeakReference) {
                BaseFragment baseFragment = mBaseFragmentWeakReference.get();
                if (null != baseFragment) {
                    baseFragment.handlerMessage(msg);
                }
            }
        }

        public void recycle() {
            removeCallbacksAndMessages(null);
            if (null != mBaseFragmentWeakReference) {
                BaseFragment baseFragment = mBaseFragmentWeakReference.get();
                baseFragment = null;
                mBaseFragmentWeakReference.clear();
                mBaseFragmentWeakReference = null;
            }
        }
    }

    protected void handlerMessage(Message msg) {
    }

    /**
     * 这段可以解决fragment嵌套fragment会崩溃的问题
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (16 < Build.VERSION.SDK_INT) {
            try {
                // 参数是固定写法
                Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFragmentManager.setAccessible(true);
                childFragmentManager.set(this, null);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
