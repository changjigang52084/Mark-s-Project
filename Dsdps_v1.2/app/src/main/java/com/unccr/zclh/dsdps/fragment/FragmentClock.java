package com.unccr.zclh.dsdps.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.fragment.view.DigitalClockView;
import com.unccr.zclh.dsdps.models.Area;

/**
 * 时钟控件
 */
@SuppressLint("ValidFragment")
public class FragmentClock extends BaseFragment {

    private static final String TAG = "FragmentClock";
    private View mView;
    private DigitalClockView mDigitalClockView;// 数字时钟的控件

    public FragmentClock(){}

    public FragmentClock(Area area) {
        super(area);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == mView) {
            mView = inflater.inflate(R.layout.fragment_clock, container, false);
            mDigitalClockView = mView.findViewById(R.id.digital_clock_view);
        }
        return mView;
    }

    @Override
    protected void startLoadingTask() {
        Log.d(TAG, "startLoadingTask.");
        mDigitalClockView.getDigitalClockBitmap();
    }

    @Override
    public void pausePlayback() {

    }

    @Override
    public void stopPlayback() {

    }

    @Override
    public void resumePlayback() {
        if (mDigitalClockView != null) {
            mDigitalClockView.recycler();
        }
        mDigitalClockView = null;
    }
}
