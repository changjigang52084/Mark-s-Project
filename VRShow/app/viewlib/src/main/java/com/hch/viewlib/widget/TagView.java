package com.hch.viewlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ToggleButton;

import com.hch.viewlib.R;

/**
 * 这个TagView就是标签视图，标签信息由他来显示。
 */

public class TagView extends androidx.appcompat.widget.AppCompatToggleButton {

    private boolean mCheckEnable = true;

    public TagView(Context paramContext) {
        super(paramContext);
        init();
    }

    public TagView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
        init();
    }

    public TagView(Context paramContext, AttributeSet paramAttributeSet,
                   int paramInt) {
        super(paramContext, paramAttributeSet, 0);
        init();
    }

    private void init() {
        setTextOn(null);
        setTextOff(null);
        setText("");
        setBackgroundResource(R.drawable.tag_bg);
    }

    public void setCheckEnable(boolean paramBoolean) {
        this.mCheckEnable = paramBoolean;
        if (!this.mCheckEnable) {
            super.setChecked(false);
        }
    }

    public void setChecked(boolean paramBoolean) {
        if (this.mCheckEnable) {
            super.setChecked(paramBoolean);
        }
    }
}