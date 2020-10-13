package com.unccr.zclh.dsdps.fragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.fragment.view.MarqueeTextView;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Marquee;
import com.unccr.zclh.dsdps.util.ProgramParseTools;
import com.unccr.zclh.dsdps.util.StringUtil;
import com.unccr.zclh.dsdps.fragment.view.TextViewScrollable;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午3:19:27
 * @parameter FragmentText 播放跑马灯
 */
@SuppressLint("ValidFragment")
public class FragmentText extends BaseFragment implements TextViewScrollable.TextRoundListener {

    private static final String TAG = "FragmentText";
    private View layoutView;
    private Marquee marquee;
    private MarqueeTextView textViewScrollable;
    private boolean mIsPreLoadView;
    /**
     * 位移速度
     */
    public static final int MARQUEE_SPEED = 5;
    /**
     * 位移速度偏移量
     */
    public static final int MARQUEE_SPEED_OFFSET = 5;
    /**
     * 区域文字大小缩放率
     */
    public static final float AREA_TEXTSIZE_SCALE_COEFFICIENT = 0.6f;
    /**
     * 最大字体
     */
    public static final int MAX_MARQUEE_TEXTSIZE = 380;

    public FragmentText() {
        super();
    }

    public FragmentText(Area area, boolean isPreLoadView) {
        super(area);
        marquee = area.getM();
        mIsPreLoadView = isPreLoadView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == layoutView) {
            layoutView = inflater.inflate(R.layout.fragment_text, container, false);
            textViewScrollable = (MarqueeTextView) layoutView.findViewById(R.id.tv_scrollable);
        }
        return layoutView;
    }

    @Override
    protected void startLoadingTask() {
        playText();
    }

    /**
     * 播放素材
     */
    private void playText() {
        if (null != marquee) {
            String backgroundColor = marquee.getBc();//背景色
            String textColor = marquee.getTc();//字体颜色
            String bgAlpha = marquee.getTr();//透明度
            int fontSize = ProgramParseTools.getTextSize(area.getH());
            //setBackground(backgroundColor, bgAlpha);
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
    @SuppressLint("ResourceAsColor")
    private void setBackground(String backgroundColor, String bgAlpha) {
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
            layoutView.setBackgroundColor(R.color.colorTransparent_15);
        } else {
            textViewScrollable.setBackgroundColor(Color.BLACK);
        }
    }

    /**
     * 设置字体颜色
     */
    private void setTextColor(String textColor) {
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

    @Override
    public void pausePlayback() {
    }

    @Override
    public void stopPlayback() {
    }

    @Override
    public void resumePlayback() {
    }

    @Override
    public void textRoundFinished() {
    }
}
