package com.unccr.zclh.dsdps.fragment.view;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.format.DateFormat;

import com.unccr.zclh.dsdps.fragment.FactoryFragment;

import java.util.Calendar;

public class DigitalClockBitmap {

    private static final String TAG = "DigitalClockBitmap";

    private Calendar mCalendar;
    private String mFormatTime;
    private String mFormatDate;

    private final Bitmap mBitmap = Bitmap.createBitmap(600, 305, Bitmap.Config.ARGB_4444);
    private final Canvas mCanvas = new Canvas(mBitmap);
    private final Paint mPaint = new Paint();
    private final String m24 = "kk : mm"; // 显示时间的格式：09 : 10
    private final String d24 = "MM-dd|  E"; // 显示日期的格式：04-10|  周三


    private static volatile DigitalClockBitmap digitalClockBitmap = null;

    public static DigitalClockBitmap newInstance() {
        if (null == digitalClockBitmap) {
            synchronized (FactoryFragment.class) {
                if (null == digitalClockBitmap) {
                    digitalClockBitmap = new DigitalClockBitmap();
                }
            }
        }
        return digitalClockBitmap;
    }

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
        mPaint.setStrokeJoin(Paint.Join.ROUND); // 优化文字边角的弧度

        Typeface mTypeface = Typeface.create(Typeface.SERIF, Typeface.BOLD);
        mPaint.setTypeface(mTypeface); // 设置文字的样式

        mPaint.setTextSize(100.0f); // 设置时间的字体大小
        mPaint.setColor(Color.BLACK);
        mPaint.setFakeBoldText(true);
        mCanvas.drawText(mFormatTime, 10.0f, 100.0f, mPaint);
        mPaint.setTextSize(100.0f); // 设置时间的字体大小
        mPaint.setColor(Color.WHITE);
        mPaint.setFakeBoldText(false);
        mCanvas.drawText(mFormatTime, 10.0f, 100.0f, mPaint);

        mPaint.setTextSize(50.0f); // 设置日期的字体大小
        mPaint.setColor(Color.BLACK);
        mPaint.setFakeBoldText(true);
        mCanvas.drawText(mFormatDate, 10.0f, 200.0f, mPaint);
        mPaint.setColor(Color.WHITE);
        mPaint.setFakeBoldText(false);
        mCanvas.drawText(mFormatDate, 10.0f, 200.0f, mPaint);
    }
}
