package com.lzkj.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.lzkj.ui.R;
import com.lzkj.ui.fragment.view.AnalogClockView;
import com.lzkj.ui.fragment.view.DigitalClockBitmap;
import com.lzkj.ui.fragment.view.DigitalClockView;
import com.lzkj.ui.util.LayoutUtil;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/**
 * 时钟控件
 * @author kchang
 *
 */
@SuppressLint("ValidFragment")
public class FragmentClock extends BaseFragment {
	@SuppressWarnings("unused")
	private static final LogTag TAG = LogUtils.getLogTag(FragmentClock.class.getSimpleName(), false);
	private static final int ID = 1006;
	private View mView; // add by yzluo
	private AnalogClockView mAnalogClockView; // 模拟时钟的控件
	private DigitalClockView mDigitalClockView; // 数字时钟的控件
	
	private LayoutParams mLayoutParams;
	private RelativeLayout mRelativeLayout;
	
	public FragmentClock(Area area) {
		super(area);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null == mView) {
			mView = inflater.inflate(R.layout.fragment_clock, container, false);
			mRelativeLayout = (RelativeLayout) mView.findViewById(R.id.clock_relative_layout);
		}
		return mView;
	}
	
	@Override
	protected void startLoadingTask() {
		mLayoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
//			switch (file.getClockType()) {
//			case AreaFile.DIGITAL: // 数字时钟
				mDigitalClockView = new DigitalClockView(getActivity().getApplicationContext(), new DigitalClockBitmap());
				mDigitalClockView.setLayoutParams(mLayoutParams);
				mRelativeLayout.addView(mDigitalClockView);
//				break;
//			case AreaFile.ANALOG: // 模拟时钟
//				mAnalogClockView = new AnalogClockView(getActivity().getApplicationContext(), area.getW(), area.getH());
//				mAnalogClockView.setLayoutParams(mLayoutParams);
//				mRelativeLayout.addView(mAnalogClockView);
//				break;
//			default:
//				break;
//			}
	}

	@Override
	public void pausePlayback() {		
	}

	@Override
	public void resumePlayback() {	
	}
	
	@Override
	public void stopPlayback() {	
		try {
			mRelativeLayout.removeAllViews();
			if (null != mDigitalClockView) {
				mDigitalClockView.recycler();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		mDigitalClockView = null;
	}
}