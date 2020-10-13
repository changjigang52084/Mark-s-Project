package com.lzkj.ui.fragment.view;


import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import android.content.Context;
import android.graphics.Canvas;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

/**
 * 自定义AnalogClock
 * 
 * @author kchang
 * 
 */
public class AnalogClockView extends View {

	private static final LogTag TAG = LogUtils.getLogTag(AnalogClockView.class.getSimpleName(), true);
	
    private Handler mHandler;
    private Runnable mTicker;
    private boolean mTickerStopped = false;

    private int mWidth;
    private int mHeight;
	
    public AnalogClockView(Context context, int width, int height) {
		super(context);
		mWidth = width;
		mHeight = height;
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		AnalogClockBitmap.get().drawAnalogClockView(canvas);
	}
	
	@Override
	protected void onAttachedToWindow() {
		mTickerStopped = false;
		super.onAttachedToWindow();
		AnalogClockBitmap.get().getAnalogClock(mWidth, mHeight); // 执行初始化加载Bitmap的操作
		mHandler = new Handler();
		
		/**
		 * requests a tick on the next hard-second boundary
		 */
		mTicker = new Runnable() {
			@Override
			public void run() {
				if (mTickerStopped)
					return;
				AnalogClockBitmap.get().prepareAnalogClockCalendar(); // 加载所需的Calendar
				invalidate();
				long now = SystemClock.uptimeMillis();
				long next = now + (1000 - now % 1000);
				mHandler.postAtTime(mTicker, next);
			}
		};
		
		mTicker.run();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		mTickerStopped = true;
	}
}
