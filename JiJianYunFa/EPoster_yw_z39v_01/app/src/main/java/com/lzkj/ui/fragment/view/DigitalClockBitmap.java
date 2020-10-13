package com.lzkj.ui.fragment.view;

import java.util.Calendar;

import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.Bitmap.Config;
import android.graphics.Paint.Join;
import android.text.format.DateFormat;

public class DigitalClockBitmap {
	private static final LogTag TAG = LogUtils.getLogTag(DigitalClockBitmap.class.getSimpleName(), true);

	private  Calendar mCalendar;
	private  String mFormatTime;
	private  String mFormatDate;
	
	private  final Bitmap mBitmap = Bitmap.createBitmap(600, 305, Config.ARGB_4444);
	private  final Canvas mCanvas = new Canvas(mBitmap);
	private  final Paint mPaint = new Paint();
	private  final String m24 = "kk : mm"; // 显示时间的格式：09 : 10
	private  final String d24 = "MM-dd|  E"; // 显示日期的格式：04-10|  周三
//	
//	public static DigitalClockBitmap ins = new DigitalClockBitmap();
//	
//	public static DigitalClockBitmap get() {
//		if (ins == null) {
//			ins = new DigitalClockBitmap();
//		}
//		
//		return ins;
//	}
	
	public Bitmap getDigitalClock() {
		prepareDigitalClockCalender();
		drawDigitalClockBitmap();
		return mBitmap;
	}
	
	/**
	 * 初始化Calendar
	 */
	public void prepareDigitalClockCalender() {
		if (mCalendar == null) {
			mCalendar = Calendar.getInstance();
		}		
		
		mCalendar.setTimeInMillis(System.currentTimeMillis());
		setDigitalClockFormat();
	}
	
	/**
	 * 格式化Calendar
	 */
	public void setDigitalClockFormat() {
		mFormatTime = DateFormat.format(m24, mCalendar).toString(); // 获取时间
		mFormatDate = DateFormat.format(d24, mCalendar).toString(); // 获取日期
	}
	
	/**
	 * 绘制Bitmap的方法
	 */
	public void drawDigitalClockBitmap() {
		mBitmap.eraseColor(Color.TRANSPARENT);	
		
		mPaint.setAntiAlias(true); // 实现抗锯齿的效果
		mPaint.setDither(true);
		mPaint.setStrokeJoin(Join.ROUND); // 优化文字边角的弧度
		
		Typeface mTypeface = Typeface.create(Typeface.SERIF, Typeface.BOLD);
		mPaint.setTypeface(mTypeface); // 设置文字的样式
		
		mPaint.setTextSize(190.0f); // 设置时间的字体大小
		mPaint.setColor(Color.BLACK);
		mPaint.setFakeBoldText(true);
		mCanvas.drawText(mFormatTime, 10.0f, 160.0f, mPaint);
		mPaint.setTextSize(190.0f); // 设置时间的字体大小
		mPaint.setColor(Color.WHITE);
		mPaint.setFakeBoldText(false);
		mCanvas.drawText(mFormatTime, 10.0f, 160.0f, mPaint);
		
		mPaint.setTextSize(102.0f); // 设置日期的字体大小
		mPaint.setColor(Color.BLACK);
		mPaint.setFakeBoldText(true);
		mCanvas.drawText(mFormatDate, 10.0f, 265.0f, mPaint);
		mPaint.setColor(Color.WHITE);
		mPaint.setFakeBoldText(false);
		mCanvas.drawText(mFormatDate, 10.0f, 265.0f, mPaint);	
	}
}