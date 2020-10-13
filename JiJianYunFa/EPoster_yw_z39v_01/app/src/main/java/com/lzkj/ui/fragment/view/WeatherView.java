package com.lzkj.ui.fragment.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

import com.baize.adpress.core.protocol.dto.WeatherPackage;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

public class WeatherView extends ImageView {
	
	private static final LogTag TAG = LogUtils.getLogTag(WeatherView.class.getSimpleName(), true);

	private Bitmap mBitmap;
	
	private WeatherBitmap mWeatherBitmap;
	private WeatherPackage mWeatherPackage;
	
	public WeatherView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void init() {
		mWeatherBitmap = new WeatherBitmap();
	}
	
	public void setWeather(WeatherPackage weatherPackage) {
		mWeatherPackage = weatherPackage;
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		Bitmap bitmap = mWeatherBitmap.getWeatherInfoBitmap(mWeatherPackage, w, h);
		if (null != bitmap && !bitmap.isRecycled()) {
			setImageBitmap(bitmap);
//			setBackgroundResource(R.drawable.bg_weather);
		}
		super.onSizeChanged(w, h, oldw, oldh);
	}
	
	public void recycler() {
		try {
			if (null != mWeatherBitmap) {
				mWeatherBitmap.destroyWeatherBitmap();
				mWeatherBitmap = null;
			}
			if (mBitmap != null && !mBitmap.isRecycled()) {
				mBitmap.isRecycled();
				mBitmap = null;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		setImageBitmap(null);
	}
}
