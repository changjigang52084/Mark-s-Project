package com.xunixianshi.vrshow.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.xunixianshi.vrshow.interfaces.ImageViewInterface;

import java.util.ArrayList;
import java.util.List;


/**
 * User: hch
 * Date: 2016-06-22
 * Time: 16:33
 * FIXME
 */
public class CustomImageView extends ImageView {
    private ImageViewInterface imageViewInterface;
    private List<LookObject> views;
    private List<LookObject> gridViews;
    private List<LookObject> classifyViews;
    private List<LookObject> gridControlViews;
    private LookObject bottomViewFocus;
    private LookObject gridPlayViews;
    private int onpage = 0;
    private int tempI;
    private int gridTempI;
    private int onViewNum = 0;
    private View parentView;
    private ImageViewInterface parentImageViewInterface;

    public class LookObject {
        private View view;
        private View view2;
        private ImageViewInterface imageViewInterface;

        public LookObject(View view, ImageViewInterface imageViewInterface) {
            this.view = view;
            this.imageViewInterface = imageViewInterface;
        }
        public LookObject(View view1,View view2, ImageViewInterface imageViewInterface) {
            this.view = view1;
            this.view2 = view2;
            this.imageViewInterface = imageViewInterface;
        }
    }

    public CustomImageView(Context context) {
        super(context);
        views = new ArrayList<LookObject>();
        gridViews = new ArrayList<LookObject>();
        classifyViews = new ArrayList<LookObject>();
        gridControlViews = new ArrayList<LookObject>();
    }

    public CustomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        views = new ArrayList<LookObject>();
        gridViews = new ArrayList<LookObject>();
        classifyViews = new ArrayList<LookObject>();
        gridControlViews = new ArrayList<LookObject>();
    }

    /**
     * 释放
     */
    public void releseListener(){
        views = null;
        gridViews = null;
        classifyViews = null;
        gridControlViews = null;
    }

    public void addLookObject(View view, ImageViewInterface imageViewInterface) {
        views.add(new LookObject(view, imageViewInterface));
    }
    public void setBottomViewFocus(View view, ImageViewInterface imageViewInterface){
        bottomViewFocus = new LookObject(view, imageViewInterface);
    }


    public void addGridObject(View view1,View view2, ImageViewInterface imageViewInterface) {
        gridViews.add(new LookObject(view1,view2, imageViewInterface));
    }
    public void addGridPlayObject(View view, ImageViewInterface imageViewInterface) {
        gridPlayViews = new LookObject(view, imageViewInterface);
    }

    public void addClassifyObject(View view, ImageViewInterface imageViewInterface) {
        classifyViews.add(new LookObject(view, imageViewInterface));
    }
    public void addGridControlObject(View view, ImageViewInterface imageViewInterface) {
        gridControlViews.add(new LookObject(view, imageViewInterface));
    }

    public void addGridView(View parentView, ImageViewInterface parentImageViewInterface) {
        this.parentView = parentView;
        this.parentImageViewInterface = parentImageViewInterface;
    }

    public void checkOnView(float thisX, float thisY, boolean checkGrid, boolean checkGridItem,boolean bottomView, int gridNum) {
        LookObject lookObject = null;
        if (!checkGrid) {
            if (isTouchPointInView(parentView, thisX, thisY)) {
                parentImageViewInterface.onFocus(true);
            }
        }
        if (checkGridItem && gridViews!=null) {
            switch (gridNum) {
                case 1:
//                    MLog.i("classifyViews.size()：  "+classifyViews.size());
                    for (int i = 0; i < classifyViews.size(); i++) {
                        lookObject = classifyViews.get(i);
                        boolean onView = isTouchPointInView(lookObject.view, thisX, thisY);
                        if (onView) {
                            if (gridTempI == i) {
                                onViewNum++;
                            } else {
                                onViewNum = 0;
                            }
                            gridTempI = i;
                            if (onViewNum >= 3) {
                                lookObject.imageViewInterface.calback(true);
                                onViewNum = 0;
                            }
                            lookObject.imageViewInterface.onFocus(true);
                        } else {
                            lookObject.imageViewInterface.onFocus(false);
                        }
                    }
                    break;
                case 2:
//                    MLog.d("gridViews.size()：  "+gridViews.size());
                    for (int i = 0; i < gridViews.size(); i++) {
                        lookObject = gridViews.get(i);
                        boolean onView = isTouchPointInView(lookObject.view,lookObject.view2, thisX, thisY);
                        switch (onpage){
                            case 1:
                                lookObject.imageViewInterface.calback(false);
                                if (onView) {
                                    lookObject.imageViewInterface.onFocus(true);
                                } else {
                                    lookObject.imageViewInterface.onFocus(false);
                                }
                                break;
                            case 2:
                                if (onView) {
                                    if (gridTempI == i) {
                                        onViewNum++;
                                    } else {
                                        onViewNum = 0;
                                    }
                                    gridTempI = i;
                                    if (onViewNum >= 3) {
                                        lookObject.imageViewInterface.calback(true);
                                        onViewNum = 0;
                                    }
                                    lookObject.imageViewInterface.onFocus(true);
                                } else {
                                    lookObject.imageViewInterface.onFocus(false);
                                }
                                break;
                        }
                    }
                    break;
            }
            for (int i = 0; i < gridControlViews.size(); i++) {
                lookObject = gridControlViews.get(i);
                boolean onView = isTouchPointInView(lookObject.view, thisX, thisY);
                if (onView) {
                    if (gridTempI == i) {
                        onViewNum++;
                    } else {
                        onViewNum = 0;
                    }
                    gridTempI = i;
                    if (onViewNum >= 3) {
                        lookObject.imageViewInterface.calback(true);
                        onViewNum = 0;
                    }
                    lookObject.imageViewInterface.onFocus(true);
                } else {
                    lookObject.imageViewInterface.onFocus(false);
                }
            }
        }
        if(bottomView){
            if(bottomViewFocus != null){
                boolean onBottomView = isTouchPointInView(bottomViewFocus.view, thisX, thisY);
                if(onBottomView){
                    bottomViewFocus.imageViewInterface.onFocus(true);
                }else{
                    bottomViewFocus.imageViewInterface.onFocus(false);
                }
            }
            for (int i = 0; i < views.size(); i++) {
                lookObject = views.get(i);
                boolean onView = isTouchPointInView(lookObject.view, thisX, thisY);
                if (onView) {
                    if (tempI == i) {
                        onViewNum++;
                    } else {
                        onViewNum = 0;
                    }
                    tempI = i;
                    if (onViewNum >= 3) {
                        lookObject.imageViewInterface.calback(true);
                        onViewNum = 0;
                    }
                    lookObject.imageViewInterface.onFocus(true);
                } else {
                    lookObject.imageViewInterface.onFocus(false);
                }
            }
        }
    }

    /**
     * 判断传入的坐标是否在传入的view上
     *
     * @param view
     * @param thisX2
     * @param thisY2
     * @return
     */
    private boolean isTouchPointInView(View view, float thisX2, float thisY2) {
        int[] location = new int[2];
        if (view == null) {
            return false;
        }
        if ((view.getVisibility()) != 0){
            return false;
        }
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        if (thisY2 >= top && thisY2 <= bottom && thisX2 >= left
                && thisX2 <= right) {
            return true;
        }
        return false;
    }
    private boolean isTouchPointInView(View view,View view2, float thisX2, float thisY2) {
        int[] location = new int[2];
        if (view == null) {
            return false;
        }
        if ((view.getVisibility()) != 0){
            return false;
        }
        view.getLocationOnScreen(location);
        int left = location[0];
        int top = location[1];
        int right = left + view.getMeasuredWidth();
        int bottom = top + view.getMeasuredHeight();
        view2.getLocationOnScreen(location);
        int left1 = location[0];
        int top1 = location[1];
        int right1 = left1 + view2.getMeasuredWidth();
        int bottom1 = top1 + view2.getMeasuredHeight();
        if (thisY2 >= top && thisY2 <= bottom && thisX2 >= left
                && thisX2 <= right) {
            if(thisY2 >= top1 && thisY2 <= bottom1 && thisX2 >= left1
                    && thisX2 <= right1){
                onpage = 2;
                return true;
            }
            onpage =1;
            return true;
        }
        return false;
    }
}
