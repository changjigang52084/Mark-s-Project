package com.unccr.zclh.dsdps.fragment.view;

import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSmoothScroller;
import androidx.recyclerview.widget.RecyclerView;

public class CourstomerLinearLayouts extends LinearLayoutManager {

    Context context;

    public CourstomerLinearLayouts(Context context) {
        super(context);
    }

    public CourstomerLinearLayouts(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
        this.context = context;
    }

    public CourstomerLinearLayouts(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }


    @Override
    public void smoothScrollToPosition(RecyclerView recyclerView, RecyclerView.State state, int position) {
        super.smoothScrollToPosition(recyclerView, state, position);
        MyLinearSmoothScroller linearSmoothScroller = new MyLinearSmoothScroller(context);
        linearSmoothScroller.setTargetPosition (position);
        startSmoothScroll(linearSmoothScroller);
    }



    class MyLinearSmoothScroller extends LinearSmoothScroller {

        public MyLinearSmoothScroller(Context context) {
            super(context);
        }

        @Nullable
        @Override
        public PointF computeScrollVectorForPosition(int targetPosition) {
            return super.computeScrollVectorForPosition(targetPosition);
        }


        @Override
        protected float calculateSpeedPerPixel(DisplayMetrics displayMetrics) {
            // 计算滑动每个像素需要的时间，这里应该与屏幕适配；
            return 40f / displayMetrics.density;
        }


    }


}
