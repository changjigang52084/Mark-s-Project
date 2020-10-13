package com.lzkj.launcher.fragment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.FrameLayout;

import com.lzkj.launcher.util.ConfigSettings;

/**
 * 主框架布局视图
 */

public class MainFrameLayoutView extends FrameLayout{

	private static final String TAG = "MainFrameLayout";

	public MainFrameLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
		if (oldw == 0 && oldh == 0) {
			return;
		}
		ConfigSettings.SCREEN_HEIGHT = h;
		ConfigSettings.SCREEN_WIDTH = w;
		// 判断客户端是否绑定
		if (ConfigSettings.isClientValid()) {
			Log.d(TAG, "MainFrameLayoutView onSizeChanged client true.");
		} else {
			Log.d(TAG, "MainFrameLayoutView onSizeChanged client false.");
		}
	}
}