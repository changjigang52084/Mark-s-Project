package com.lzkj.ui.fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.dto.WeatherPackage;
import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.fragment.view.WeatherView;
import com.lzkj.ui.util.ConfigSettings;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.StringUtil;

@SuppressLint("ValidFragment")
public class FragmentWeather extends BaseFragment {
	private static final LogTag TAG = LogUtils.getLogTag(FragmentWeather.class.getSimpleName(), true);
	private static final long DELAY_MILLIS = 60 * 1000;
	private WeatherView mImageWeather;
	private View mFrameLayout;
	
	public FragmentWeather(Area area) {
		super(area);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mFrameLayout == null) {
			mFrameLayout = inflater.inflate(R.layout.fragment_weather_a, container, false);
			mImageWeather = (WeatherView) mFrameLayout.findViewById(R.id.img_weather_view);
		}
		mImageWeather.init();
		updateWeatherView();
		return mFrameLayout;
	}

	@Override
	protected void startLoadingTask() {
	}
	
	private void updateWeatherView() {
		String weather = ConfigSettings.getWeather();
		LogUtils.d(TAG, "updateWeatherView", "weather : " + weather);
		if (!StringUtil.isNullStr(weather)) {
			stopUpdateRun();
			WeatherPackage weatherPackage = JSON.parseObject(weather, WeatherPackage.class);
			onWeatherResult(weatherPackage);
		} else {
			startUpdateRun();
		}
	}
	
	private void startUpdateRun() {
		EPosterApp
		.getApplication()
		.mHandler
		.postDelayed(updateWeatherRun, DELAY_MILLIS);
	}
	
	private void stopUpdateRun() {
		EPosterApp
		.getApplication()
		.mHandler
		.removeCallbacks(updateWeatherRun);
	}
	
	private Runnable updateWeatherRun = new Runnable() {
		@Override
		public void run() {
			startUpdateRun();
			updateWeather();
		}
	};
	
	private void updateWeather() {
		Intent intent = new Intent(Constants.UPDATE_WEATHER_ACTION);
		getActivity().getApplicationContext().sendBroadcast(intent);
	}
	/**
	 * 当刷新界面时，执行更新Bitmap的操作
	 */
	private void onWeatherResult(WeatherPackage weatherPackage) {
		if (weatherPackage != null) {
			LogUtils.d(TAG, "onWeatherResult", weatherPackage);
			if (null != mImageWeather) {
				mImageWeather.setWeather(weatherPackage);
				mImageWeather.requestLayout();
			}
		}
	}
	
	@Override
	public void pausePlayback() {
	}

	@Override
	public void resumePlayback() {
	}

	@Override
	public void stopPlayback() {
		stopUpdateRun();
		mImageWeather.recycler();
//		mImageWeather = null;
	}
}