package com.unccr.zclh.dsdps.fragment.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午3:23:44
 * @parameter TextViewScrollable
 */
public class TextViewScrollable extends TextView {

    private static final String TAG = "TextViewScrollable";

    // 最大字体大小
    public static final float MAX_TEXTSIZE = 220.0f;
    // 滚动字幕大小
    private static final int CHUNK_MAX_SIZE = 300;
    // 线程睡眠间隔时间
    private static final long SPAN = 5l;
    private static final long MOVE_INTERVER = 1000 / SPAN;

    private float DELTA_FACTOR = 0;

    public static enum ScrollType {
        H_TO_LEFT, H_TO_RIGHT, V_UP, V_DOWN
    };

    public static enum ScrollSpeed {
        SLOW, NORMAL, FAST
    };

    private ScrollType mScrollType = ScrollType.H_TO_LEFT;
    private ScrollSpeed mScrollSpeed = ScrollSpeed.NORMAL;

    // 1.0 slow, 1.5 normal, 2.0 fast
    private float mVelocityH;

    private float mStartPointX;
    private float mEndPointX;
    private float mStartPointY;
    private float mStartTextX;
    private float mStartTextY;

    private String[] mChunks;
    // full text
    private String mFullText;
    // 当前显示字幕
    private String mCurrentText;

    private int mCurrentlyPlayingChunk = 0;
    // 是否在滚动
    private boolean isScrolling = true;

    private TextRoundListener mScrollerListener;

    private Paint mPaint;
    // 每个字符的宽度
    private float charWidth;

    // view 宽度
    private int viewWidth;

    public void setText(String fullText) {
        mFullText = fullText;
        setFullScrollText(mFullText);
    }

    private void setFullScrollText(String fullText) {
        fullText = fullText.trim();
        int length = fullText.length();
        int nrOfChunksRequired = Math.round(length / CHUNK_MAX_SIZE);
        if (length % CHUNK_MAX_SIZE != 0) {
            nrOfChunksRequired++;
        }
        mChunks = new String[nrOfChunksRequired];
        for (int i = 0; i < nrOfChunksRequired; i++) {
            int start = i * CHUNK_MAX_SIZE;
            int end = (i + 1) * CHUNK_MAX_SIZE;
            if (end > fullText.length()) {
                end = fullText.length();
            }
            mChunks[i] = (String) fullText.subSequence(start, end);
        }
        if (nrOfChunksRequired > 0) {
            // interleaveChunks(nrOfChunksRequired);
            mCurrentlyPlayingChunk = 0;
            mCurrentText = mChunks[mCurrentlyPlayingChunk];
            setScrollText(mCurrentText);
        }
    }

    private void setScrollText(String text) {

        // INFO: if the threshold point is reached => the chunck is scrolled
        // completely
        // 1) set the text here.
        // setText(text);
        float textWidth = mPaint.measureText(text);
        // TODO 计算每个字符宽度，快-10/中-6/慢-3
        charWidth = textWidth / CHUNK_MAX_SIZE;
        viewWidth = getWidth();
        // 计算字幕滚动速度
        calculateScrollSpeed(charWidth, mScrollSpeed);

        switch (mScrollType) {
            case H_TO_LEFT:
                // 2) measure the text width
                // 3) measure the threshold point
                mStartPointX = viewWidth + textWidth;
                mEndPointX = viewWidth + textWidth * 2;
                // text height / 3 yields the best results
                // mStartPointY = (int) (heightOfView / 2 + textLineHeight / 3);
                mStartPointY = getTextSize() + getPaddingTop();

                // Set initial position of text;
                mStartTextX = mStartPointX;
                mStartTextY = mStartPointY;

                DELTA_FACTOR = textWidth;
                // 4) start scrolling.
                startScrolling();
                break;
        }

    }

    public void startScrolling() {
        this.isScrolling = false;
        invalidate();
    }

    public void stopScrolling() {
        this.isScrolling = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!isScrolling) {
            updateTextCoords();
            canvas.drawText(mCurrentText, mStartTextX, mStartTextY, mPaint);
            invalidate();
        }
    }

    private void updateTextCoords() {
        switch (mScrollType) {
            case H_TO_LEFT:
                mStartTextX = mStartPointX - DELTA_FACTOR;
                DELTA_FACTOR += mVelocityH;
                if (DELTA_FACTOR > mEndPointX) {
                    scrollFinished();
                }
                break;
        }
    }

    private void scrollFinished() {
        isScrolling = true;
        mCurrentlyPlayingChunk++;
        boolean hasMore = mChunks.length > mCurrentlyPlayingChunk;
        if (!hasMore) {
            // no more to show => announce this text was scrolled with success
            if (mScrollerListener != null) {
                mScrollerListener.textRoundFinished();
            }
        } else {
            // has some more text.
            setScrollText(mChunks[mCurrentlyPlayingChunk]);
        }
    }

    public void setTextSize(float value) {
        mPaint.setTextSize(value);
    }

    public void setTextColor(int textColor) {
        mPaint.setColor(textColor);
    }

    public void setTextSpeed(int textSpeed) {
        switch (textSpeed) {
            case 1:
                mScrollSpeed = ScrollSpeed.SLOW;
                break;
            case 2:
                mScrollSpeed = ScrollSpeed.NORMAL;
                break;
            case 3:
                mScrollSpeed = ScrollSpeed.FAST;
                break;
            default:
                mScrollSpeed = ScrollSpeed.NORMAL;
                break;
        }
    }

    /**
     * 计算字幕滚动速度
     *
     * @param charWidth
     *            每个字符平均宽度
     * @param mScrollSpeed
     *            移动速度 快/中/慢
     */
    private void calculateScrollSpeed(float charWidth, ScrollSpeed mScrollSpeed) {
        // 快-10/中-6/慢-3
        // 要可设置
        int charNum = 6;
        switch (mScrollSpeed) {
            case SLOW:
                charNum = 5;// 4f;
                break;
            case NORMAL:
                charNum = 15;// 8f;
                break;
            case FAST:
                charNum = 30;// 14f;
                break;
        }

        mVelocityH = charWidth * charNum / MOVE_INTERVER;
    }

    public void setScrollingMode(ScrollType scrollType) {
        this.mScrollType = scrollType;
    }

    public void setScrollSpeed(ScrollSpeed scrollSpeed) {
        this.mScrollSpeed = scrollSpeed;
    }

    public TextViewScrollable(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setTextRoundListener(TextRoundListener listener) {
        this.mScrollerListener = listener;
    }

    public TextViewScrollable(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = getPaint();
    }

    public TextViewScrollable(Context context) {
        super(context);
    }

    public interface TextRoundListener {
        /**
         * This method will be called when a text has completed a full cycle of
         * the text area.
         */
        public void textRoundFinished();
    }
}
