package com.lzkj.ui.fragment.view;

import java.util.Calendar;
import java.util.TimeZone;

import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.Helper;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.BitmapDrawable;

/**
 * 自定义AnalogClock
 * 
 * @author kchang
 * 
 */
public class AnalogClockBitmap {
	private static final LogTag TAG = LogUtils.getLogTag(AnalogClockBitmap.class.getSimpleName(), true);
	
	// 表盘，时针，分针，秒针的Bitmap
	private Bitmap mBmpDial;
	private Bitmap mBmpHour;
	private Bitmap mBmpMinute;
	private Bitmap mBmpSecond;
	// 表盘，时针，分针，秒针的BitmapDrawable
	private BitmapDrawable bmdHour;
	private BitmapDrawable bmdMinute;
	private BitmapDrawable bmdSecond;
	private BitmapDrawable bmdDial;

	private int mWidth; // 表盘的宽度
	private int mHeigh; // 表盘的高度
	private int mTempWidth; // 时针，分针，秒针的宽度
	private int mTempHeigh; // 时针，分针，秒针的高度
	private int centerX; // 时针，分针，秒针的圆心点（x轴）
	private int centerY; // 时针，分针，秒针的圆心点（y轴）
	private int availableWidth; // 宽度的中间变量
	private int availableHeight; // 高度的中间变量	
	private int hour;
	private int minute;
	private int second;
	
	private float circleWH; // 表心的参数
	private float hourRotate; // 时针的旋转参数
	private float minuteRotate; // 分针的旋转参数 
	private float secondRotate; // 秒针的旋转参数
	
	private Calendar mCalendar;
	private Paint mPaint; // 画表心的Paint
	
	private static AnalogClockBitmap ins = new AnalogClockBitmap();
	
	public static AnalogClockBitmap get() {
		if (ins == null) {
			ins = new AnalogClockBitmap();
		}
		
		return ins;
	}
	
	public void getAnalogClock(int width, int height) {
		initAnalogClockSize(width, height);
		initAnalogClockBitmap();
	}
	
	/**
	 * 计算相关的尺寸
	 */
	private void initAnalogClockSize(int width, int height) {
		availableWidth = width > height ? height : width; // 根据传入的Area的宽度与高度，进行View的适配
		availableHeight = availableWidth;
		
		mTempHeigh = availableHeight;
		mTempWidth = (int) (availableHeight/5.5f);
		circleWH = availableHeight / 27f;
		
		centerX = (int) (availableWidth / 2f);
		centerY = (int) (availableHeight / 2f);
	}
	
	/**
	 * 加载Bitmap的资源
	 */
	private void initAnalogClockBitmap() {
		// 时针的Bitmap与BitmapDrawable
		mBmpHour = BitmapFactory.decodeResource(EPosterApp.getApplication().getResources(),
				R.drawable.big_hour);
		bmdHour = new BitmapDrawable(EPosterApp.getApplication().getResources(), mBmpHour);
		
		// 分针的Bitmap与BitmapDrawable
		mBmpMinute = BitmapFactory.decodeResource(EPosterApp.getApplication().getResources(),
				R.drawable.big_minute);
		bmdMinute = new BitmapDrawable(EPosterApp.getApplication().getResources(), mBmpMinute);
				
		// 秒针的Bitmap与BitmapDrawable
		mBmpSecond = BitmapFactory.decodeResource(EPosterApp.getApplication().getResources(),
				R.drawable.big_second);
		bmdSecond = new BitmapDrawable(EPosterApp.getApplication().getResources(), mBmpSecond);
				
		// 表盘的Bitmap与BitmapDrawable
		mBmpDial = BitmapFactory.decodeResource(EPosterApp.getApplication().getResources(),
				R.drawable.big_dial);
		mBmpDial = Helper.zoomImage(mBmpDial, availableWidth, availableHeight);
		bmdDial = new BitmapDrawable(EPosterApp.getApplication().getResources(), mBmpDial);
		
		mWidth = mBmpDial.getWidth();
		mHeigh = mBmpDial.getHeight();
	}
	
	/**
	 * 加载Calendar
	 */
	public void prepareAnalogClockCalendar() {
		mCalendar = Calendar.getInstance(TimeZone.getDefault());
		setAnalogClockCalendar();
	}
	
	/**
	 * 格式化Calendar
	 */
	private void setAnalogClockCalendar() {
		hour = mCalendar.get(Calendar.HOUR);
		minute = mCalendar.get(Calendar.MINUTE);
		second = mCalendar.get(Calendar.SECOND);
		hourRotate = (hour * 30.0f) + (minute / 60.0f * 30.0f);
		minuteRotate = minute * 6.0f;
		secondRotate = second * 6.0f;
	}
	
	/**
	 * 通过专递的参数Canvas，绘制View
	 */
	public void drawAnalogClockView(Canvas mCanvas) {	
		boolean scaled = false;
		
		// 确定View所在中心与尺寸
		if (availableWidth < mWidth || availableHeight < mHeigh) {
			scaled = true;
			float scale = Math.min((float) availableWidth / (float) mWidth,
					(float) availableHeight / (float) mHeigh);
			mCanvas.save();
			mCanvas.scale(scale, scale, centerX, centerY);
		}
	
		// 确定表盘在View中的位置
		bmdDial.setAntiAlias(true); // 实现抗锯齿的效果
		bmdDial.setFilterBitmap(true); 
		bmdDial.setBounds(centerX - (mWidth / 2), centerY - (mHeigh / 2),
				centerX + (mWidth / 2), centerY + (mHeigh / 2));
		bmdDial.draw(mCanvas);
		
		int left = centerX - (mTempWidth / 2);
		int top = centerY - (mTempHeigh / 2);
		int right = centerX + (mTempWidth / 2);
		int bottom = centerY + (mTempHeigh / 2);
		
		// 把时针画在View上
		mCanvas.save(); // 保存时针的状态
		mCanvas.rotate(hourRotate, centerX, centerY);
		bmdHour.setBounds(left, top, right, bottom);
		bmdHour.setAntiAlias(true); // 实现抗锯齿的效果
		bmdHour.setFilterBitmap(true); 
		bmdHour.draw(mCanvas);
		mCanvas.restore(); // 恢复时针的状态

		// 把分针画在View上
		mCanvas.save(); // 保存分针的状态
		mCanvas.rotate(minuteRotate, centerX, centerY);
		bmdMinute.setBounds(left, top, right, bottom);
		bmdMinute.setAntiAlias(true); // 实现抗锯齿的效果
		bmdMinute.setFilterBitmap(true); 
		bmdMinute.draw(mCanvas);
		mCanvas.restore(); // 恢复分针的状态
		
		// 把秒针画在View上
		mCanvas.save(); // 保存秒针的状态
		mCanvas.rotate(secondRotate, centerX, centerY);
		bmdSecond.setBounds(left, top, right, bottom);
		bmdSecond.setAntiAlias(true); // 实现抗锯齿的效果
		bmdSecond.setFilterBitmap(true); 
		bmdSecond.draw(mCanvas);
		mCanvas.restore(); // 恢复秒针的状态
		
		// 绘制表心
		mPaint = new Paint();
		
		mPaint.setAntiAlias(true); // 实现抗锯齿的效果
		mPaint.setFilterBitmap(true);
		
		mPaint.setAntiAlias(true) ;
		mPaint.setColor(Color.parseColor("#282A2B"));
		mPaint.setStyle(Style.FILL);
		mCanvas.drawCircle(centerX, centerY, circleWH, mPaint);
		if (scaled) {
			mCanvas.restore();
		}
	}
	
	/**
	 * 销毁余留的Bitmap
	 */
	public void destroyAnalogClockBitmap() {
		if (mBmpHour != null && !mBmpHour.isRecycled()) {
			mBmpHour.recycle();
			mBmpHour = null;
			bmdHour.getBitmap().recycle();
			LogUtils.d(TAG, "mBmpHour has already been recycled", "mBmpHour = " + mBmpHour + " : " + "bmdHour = " + bmdHour);
		}
		if (mBmpMinute != null && !mBmpMinute.isRecycled()) {
			mBmpMinute.recycle();
			mBmpMinute = null;
			bmdMinute.getBitmap().recycle();
			LogUtils.d(TAG, "mBmpMinute has already been recycled", "mBmpMinute = " + mBmpMinute + " : " + "bmdMinute = " + bmdMinute);
		}
		if (mBmpSecond != null && !mBmpSecond.isRecycled()) {
			mBmpSecond.recycle();
			mBmpSecond = null;
			bmdSecond.getBitmap().recycle();
			LogUtils.d(TAG, "mBmpSecond has already been recycled", "mBmpSecond = " + mBmpSecond + " : " + "bmdSecond = " + bmdSecond);
		}
		if (mBmpDial != null && !mBmpDial.isRecycled()) {
			mBmpDial.recycle();
			mBmpDial = null;
			bmdDial.getBitmap().recycle();
			LogUtils.d(TAG, "mBmpDial has already been recycled", "mBmpDial = " + mBmpDial + " : " + "bmdDial = " + bmdDial);
		}
	}
}
