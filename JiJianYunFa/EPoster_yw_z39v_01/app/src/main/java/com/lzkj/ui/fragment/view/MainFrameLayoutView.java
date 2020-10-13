package com.lzkj.ui.fragment.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.lzkj.ui.util.LayoutUtil;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

public class MainFrameLayoutView extends FrameLayout{
	private static final LogTag TAG = LogUtils.getLogTag(MainFrameLayoutView.class.getSimpleName(), true);
	public MainFrameLayoutView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh) {
		super.onSizeChanged(w, h, oldw, oldh);
	}
}