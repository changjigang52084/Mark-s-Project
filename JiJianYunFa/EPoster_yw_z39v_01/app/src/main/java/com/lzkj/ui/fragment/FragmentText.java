package com.lzkj.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Marquee;
import com.lzkj.ui.R;
import com.lzkj.ui.fragment.view.MarqueeTextView;
import com.lzkj.ui.fragment.view.TextViewScrollable.TextRoundListener;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.ProgramParseTools;
import com.lzkj.ui.util.StringUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年5月4日 下午12:48:43 
 * @version 1.0 
 * @parameter  播放跑马灯
 */
@SuppressLint("ValidFragment")
public class FragmentText extends BaseFragment implements TextRoundListener{
	private static final LogTag TAG = LogUtils.getLogTag(FragmentText.class.getSimpleName(), true);
	private View layoutView;
	private Marquee marquee;
//	private AlwaysMarqueeTextView textViewScrollable;
	private MarqueeTextView textViewScrollable;
    private boolean mIsPreLoadView;
	/**位移速度*/
	public static final int MARQUEE_SPEED = 5;
	/**位移速度偏移量*/
	public static final int MARQUEE_SPEED_OFFSET = 5;
	/**区域文字大小缩放率*/
	public static final float AREA_TEXTSIZE_SCALE_COEFFICIENT = 0.6f;
	/**最大字体*/
	public static final int MAX_MARQUEE_TEXTSIZE = 380;

	public FragmentText(Area area,boolean isPreLoadView) {
		super(area);
		marquee = area.getM();
        mIsPreLoadView = isPreLoadView;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView = inflater.inflate(R.layout.fragment_text, container, false);
			textViewScrollable = (MarqueeTextView)layoutView.findViewById(R.id.tv_scrollable);
		}
		return layoutView;
	}
	
	@Override
	protected void startLoadingTask() {
        LogUtils.d(TAG, "startLoadingTask", "startLoadingTask" );
        playText();
        LogUtils.d(TAG, "startLoadingTask", marquee.getC());

	}
	
	/**
	 * 播放素材
	 */
	private void playText() {
		String content = StringUtil.getString(R.string.add_text_resource);
		if (null != marquee) {
			String backgroundColor = marquee.getBc();//背景色
			String textColor = marquee.getTc();//字体颜色
			String bgAlpha = marquee.getTr();//透明度
			content = marquee.getC();//字幕内容
			int fontSize = ProgramParseTools.getTextSize(area.getH());
			setBackground(backgroundColor,bgAlpha);
			setTextColor(textColor);
			textViewScrollable.setTextSize(fontSize);
            textViewScrollable.setScrollWidth(area.getW());
            textViewScrollable.setCurrentPosition(area.getW());//设置滚动信息从滚动区域的右边出来
            textViewScrollable.setSpeed(MARQUEE_SPEED);
			textViewScrollable.setText(marquee.getC());
        }
        if (!mIsPreLoadView) {
			textViewScrollable.startScroll();
		}
	}
	/**
	 * 设置背景
	 */
	private void setBackground(String backgroundColor,String bgAlpha){
		if (null != backgroundColor) {
			int color = Color.BLACK;
			try {
				color = Color.parseColor(backgroundColor);
			} catch (Exception e1) {
				color = Color.BLACK;
				e1.printStackTrace();
			}
			if (StringUtil.isNullStr(bgAlpha)) {
				int backgAlpha = 255;
				try {
					backgAlpha = (int) (Integer.parseInt(bgAlpha) * 2.55f);
				} catch (NumberFormatException e) {
					backgAlpha = 255;
					e.printStackTrace();
				}
				if (backgAlpha != 255) {
					layoutView.getBackground().setAlpha(backgAlpha);
				}
			}
			layoutView.setBackgroundColor(color);
			LogUtils.d(TAG, "playText", "bgAlpha : "+ bgAlpha + "color : " + color );
		} else {
			textViewScrollable.setBackgroundColor(Color.BLACK);
		}
	}
	/**
	 * 设置字体颜色
	 */
	private void setTextColor(String textColor){
		if (null != textColor) {
			int txtColor = Color.WHITE;
			try {
				txtColor = Color.parseColor(textColor);
			} catch (Exception e) {
				e.printStackTrace();
			}
			textViewScrollable.setTextColor(txtColor);
		} else {
			textViewScrollable.setTextColor(Color.WHITE);
		}
	}
//
//	private Rect getWidth(TextView tv) {
//		Rect bounds = new Rect();
//		TextPaint paint;
//		paint = tv.getPaint();
//		String cityName = tv.getText().toString();
//		paint.getTextBounds(cityName, 0, cityName.length(), bounds);
//		return bounds;
//	}
//	
//	private String appendStr(int appendSize, String marquee) {
//		StringBuffer stringBuffer = new StringBuffer(10);
//		for (int i = 0; i < appendSize; i++) {
//			stringBuffer.append("　");
//		}
//		stringBuffer.append(marquee);
//		return stringBuffer.toString();
//	}
//	
	@Override
	public void pausePlayback() {
	}

	@Override
	public void stopPlayback() {
        LogUtils.d(TAG, "stopPlayback", "stopPlayback" );
	}

	@Override
	public void resumePlayback() {
	}
	
	@Override
	public void textRoundFinished() {
	}
}
