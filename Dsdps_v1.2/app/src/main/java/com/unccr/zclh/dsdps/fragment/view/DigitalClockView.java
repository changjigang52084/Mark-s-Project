package com.unccr.zclh.dsdps.fragment.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.RectF;
import android.os.Handler;
import android.os.SystemClock;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

public class DigitalClockView extends View {

    private static final String TAG = "DigitalClockView";

    private RectF src;
    private RectF dest;
    private Bitmap mBitmap;

    private Handler mHandler;
    private Runnable mTicker;
    private boolean mTickerStopped = false;

    public DigitalClockView(Context context) {
        super(context);
    }

    public DigitalClockView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public DigitalClockView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void getDigitalClockBitmap() {
        if (src != null) {
            measureScale();
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d(TAG, "onSizeChanged w: " + w + " ,h: " + h + " ,oldw: " + oldw + " ,oldh: " + oldh);
        src = new RectF(0, 0, w, h);
        measureScale();
    }

    ;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        DigitalClockBitmap.newInstance().drawDigitalClockBitmap();
        mBitmap = DigitalClockBitmap.newInstance().getDigitalClock();
        dest = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
        if (mBitmap != null) {
            canvas.drawBitmap(mBitmap, null, dest, null);
        }
    }

    @Override
    protected void onAttachedToWindow() {
        mTickerStopped = false;
        super.onAttachedToWindow();
        mHandler = new Handler();

        /**
         * requests a tick on the next hard-second boundary
         */
        mTicker = new Runnable() {
            public void run() {
                if (mTickerStopped)
                    return;
                DigitalClockBitmap.newInstance().prepareDigitalClockCalender(); // 加载所需的Calender
                invalidate();
                long now = SystemClock.uptimeMillis();
                long next = now + (1000 - now % 1000);
                mHandler.postAtTime(mTicker, next);
            }
        };

        mTicker.run();
    }

    public void recycler() {
        if (mBitmap != null && !mBitmap.isRecycled()) {
            mBitmap.isRecycled();
            mBitmap = null;
        }
        if (null != mHandler) {
            mHandler.removeCallbacksAndMessages(null);
        }
        mTickerStopped = true;
    }

    /**
     * 等比例显示Bitmap
     */
    private void measureScale() {

        if (mBitmap != null && !mBitmap.isRecycled()) {

            dest = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
            float dx = src.width() / dest.width();
            float dy = src.height() / dest.height();

            if (dest.height() * dx <= src.height()) {
                Log.d(TAG, "measureScale dx: " + dx);
                dest.set(0, 0, src.width(), dest.height() * dx);
                float destHHalf = dest.height() / 2;
                float srcHHalf = src.height() / 2;
                float difHHalf = srcHHalf - destHHalf;
                dest.set(0, difHHalf, src.width(), dest.height() + difHHalf);
                Log.d(TAG, "measureScale dest: " + dest);
            } else {
                Log.d(TAG, "measureScale dy: " + dy);
                dest.set(0, 0, dest.width() * dy, src.height());
                float destWHalf = dest.width() / 2;
                float srcWHalf = src.width() / 2;
                float difWHalf = srcWHalf - destWHalf;
                dest.set(difWHalf, 0, dest.width() + difWHalf, src.height());
                Log.d(TAG, "measureScale dest: " + dest);
            }
        }
    }
}
