package com.lzkj.ui.fragment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.widget.TextView;

import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.StringUtil;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/10/24.
 * @Parameter
 */

public class MarqueeTextView extends TextView {
    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(MarqueeTextView.class.getSimpleName(), true);
    public static final int SCROLL_INTERVAL = 50;
    public static final int SCROLL_DELAY = 500;
    public static final int REPEAT_SCROLL_DELAY = 2000;
    /**
     * 是否停止滚动
     */
    private boolean mStopMarquee;
    private String mText;//文本内容
    private float mCoordinateX;//当前滚动位置
    private float mTextWidth;//文本宽度
    private int mScrollWidth;//滚动区域宽度
    private int speed = 1;//滚动速度

    public float getCurrentPosition() {
        return mCoordinateX;
    }

    public void setCurrentPosition(float mCoordinateX) {
        this.mCoordinateX = mCoordinateX;
    }

    public int getScrollWidth() {
        return mScrollWidth;
    }

    public void setScrollWidth(int mScrollWidth) {
        this.mScrollWidth = mScrollWidth;
    }

    public int getSpeed() {
        return speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public MarqueeTextView(Context context) {
        super(context);
    }

    public MarqueeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MarqueeTextView(Context context, AttributeSet attrs,
                           int defStyle) {
        super(context, attrs, defStyle);
    }

    public void startScroll() {
        if (!isEmpty(mText)) {
            LogUtils.d(TAG, "setText", "mTextWidth:" + mTextWidth);
            removeCallbacks(scrollRun);
            postDelayed(scrollRun, SCROLL_DELAY);
            LogUtils.i(TAG, "setText", "mText" + mText);
        }
    }

    public void setText(String text) {
        LogUtils.d(TAG, "setText", "mCoordinateX" + mCoordinateX);
        this.mText = text;
        if (!isEmpty(mText)) {
            mTextWidth = getPaint().measureText(mText);
            LogUtils.d(TAG, "setText", "mTextWidth:" + mTextWidth);
            LogUtils.d(TAG, "setText", "mText" + mText);
        }
    }


    @Override
    protected void onAttachedToWindow() {
        LogUtils.d(TAG, "onAttachedToWindow", "onAttachedToWindow");
        if (!StringUtil.isNullStr(mText)) {
            mStopMarquee = false;
            removeCallbacks(scrollRun);
            postDelayed(scrollRun, SCROLL_DELAY);
        }
        super.onAttachedToWindow();
    }

    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

    @Override
    protected void onDetachedFromWindow() {
        LogUtils.d(TAG, "onDetachedFromWindow", "onDetachedFromWindow");
        mStopMarquee = true;
        removeCallbacks(scrollRun);
        super.onDetachedFromWindow();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (!isEmpty(mText)) {
            float mCoordinateY = (getHeight() + getTextSize() / 2) / 2;
            canvas.drawText(mText, mCoordinateX, mCoordinateY, getPaint());
        }
    }

    private Runnable scrollRun = new Runnable() {
        @Override
        public void run() {
            if (mCoordinateX < (-mTextWidth)) {//文字滚动完了，从滚动区域的右边出来
                LogUtils.d(TAG, "handleMessage", "Text out of area");
                mCoordinateX = mScrollWidth;
                invalidate();
                if (!mStopMarquee) {
                    postDelayed(scrollRun, REPEAT_SCROLL_DELAY);
                }
            } else {
                mCoordinateX -= speed;
                invalidate();
                if (!mStopMarquee) {
                    postDelayed(scrollRun, SCROLL_INTERVAL);
                }
            }
        }
    };


}
