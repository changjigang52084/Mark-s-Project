package com.hch.viewlib.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;

/**
 * Created by duan on 2016/10/11.
 */

public class ExtendCoordinatorLayout extends CoordinatorLayout {

    private boolean mEnableCoordinator = true;

    public ExtendCoordinatorLayout(Context context) {
        super(context);
    }

    public ExtendCoordinatorLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendCoordinatorLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public boolean isEnableCoordinator() {
        return mEnableCoordinator;
    }

    public void setEnableCoordinator(boolean enableCoordinator) {
        this.mEnableCoordinator = enableCoordinator;
    }

    @Override
    public boolean  onStartNestedScroll(View child, View target, int nestedScrollAxes) {
        if(!mEnableCoordinator){
            return false;
        }
        return super.onStartNestedScroll(child, target, nestedScrollAxes);
    }

}
