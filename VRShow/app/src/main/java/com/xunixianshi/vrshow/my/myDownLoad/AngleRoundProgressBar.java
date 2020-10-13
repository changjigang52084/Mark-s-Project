package com.xunixianshi.vrshow.my.myDownLoad;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.xunixianshi.vrshow.R;

/**
 * 仿iphone带进度的进度条，线程安全的View，可直接在线程中更新进度
 *
 * @author xiaanming
 *        http://www.cnblogs.com/tianzhijiexian/p/3854346.html
 */
public class AngleRoundProgressBar extends View {
    /**
     * 画笔对象的引用
     */
    private Paint paint;

    /**
     * 圆环的颜色
     */
    private int roundColor;

    /**
     * 圆环进度的颜色
     */
    private int roundProgressColor;

    /**
     * 中间进度百分比的字符串的颜色
     */
    private int textColor;

    /**
     * 中间进度百分比的字符串的字体
     */
    private float textSize;

    /**
     * 圆环的宽度
     */
    private float roundWidth;

    /**
     * 最大进度
     */
    private int max;

    /**
     * 当前进度
     */
    private int progress;
    /**
     * 是否显示中间的进度
     */
    private boolean textIsDisplayable;

    /**
     * 进度的风格，实心或者空心
     */
    private int style;

    /**
     * 进度开始的角度数
     */
    private int startAngle;

    private int backColor;


    public static final int STROKE = 0;
    public static final int FILL = 1;

    public AngleRoundProgressBar(Context context) {
        this(context, null);
    }

    public AngleRoundProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AngleRoundProgressBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        paint = new Paint();

        TypedArray mTypedArray = context.obtainStyledAttributes(attrs,
                R.styleable.HollowRoundProgressBar);

        //获取自定义属性和默认值，第一个参数是从用户属性中得到的设置，如果用户没有设置，那么就用默认的属性，即：第二个参数
        //圆环的颜色
        roundColor = mTypedArray.getColor(R.styleable.HollowRoundProgressBar_roundColor, Color.RED);
        //圆环进度条的颜色
        roundProgressColor = mTypedArray.getColor(R.styleable.HollowRoundProgressBar_roundProgressColor, Color.GREEN);
        //文字的颜色
        textColor = mTypedArray.getColor(R.styleable.HollowRoundProgressBar_textColor, Color.GREEN);
        //文字的大小
        textSize = mTypedArray.getDimension(R.styleable.HollowRoundProgressBar_textSize, 15);
        //圆环的宽度
        roundWidth = mTypedArray.getDimension(R.styleable.HollowRoundProgressBar_roundWidth, 5);
        //最大进度
        max = mTypedArray.getInteger(R.styleable.HollowRoundProgressBar_max, 100);
        //是否显示中间的进度
        textIsDisplayable = mTypedArray.getBoolean(R.styleable.HollowRoundProgressBar_textIsDisplayable, true);
        //进度的风格，实心或者空心
        style = mTypedArray.getInt(R.styleable.HollowRoundProgressBar_style, 0);
        //进度开始的角度数
        startAngle = mTypedArray.getInt(R.styleable.HollowRoundProgressBar_startAngle, -90);
        // 圆形颜色
        backColor = mTypedArray.getColor(R.styleable.HollowRoundProgressBar_backColor, 0);
        mTypedArray.recycle();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        /**
         * 画最外层的大圆环
         */
        int centre = getWidth() / 2; //获取圆心的x坐标

        int radius = (int) (centre - roundWidth/2); //圆环的半径
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setAntiAlias(true);  //消除锯齿

        //Log.e("log", centre + "");
        if (backColor != 0) {
            paint.setAntiAlias(true);
            paint.setColor(backColor);
            paint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(centre, centre, radius, paint);
        }
        paint.setColor(roundColor); //设置圆环的颜色
        paint.setStyle(Paint.Style.STROKE); //设置空心
        canvas.drawCircle(centre, centre, radius, paint); //画出圆环

        /**
         * 画进度百分比
         */
        paint.setStrokeWidth(0);
        paint.setColor(textColor);
        paint.setTextSize(textSize);
        paint.setTypeface(Typeface.DEFAULT_BOLD); //设置字体
        int percent = (int) (((float) progress / (float) max) * 100);  //中间的进度百分比，先转换成float在进行除法运算，不然都为0
        float textWidth = paint.measureText(percent + "%");   //测量字体宽度，我们需要根据字体的宽度设置在圆环中间

        if (textIsDisplayable && percent != 0 && style == STROKE) {
            canvas.drawText(percent + "%", centre - textWidth / 2, centre + textSize / 2, paint); //画出进度百分比
        }


        /**
         * 画圆弧 ，画圆环的进度
         */
        //设置进度是实心还是空心
        paint.setStrokeWidth(roundWidth); //设置圆环的宽度
        paint.setColor(roundProgressColor);  //设置进度的颜色
//        RectF oval = new RectF(centre - radius, centre - radius, centre
//                + radius, centre + radius);  //用于定义的圆弧的形状和大小的界限
        RectF oval = new RectF();
        oval.left = centre - radius;
        oval.top = centre - radius;
        oval.right = radius + centre;
        oval.bottom = radius + centre;

        switch (style) {
            case STROKE: {
                paint.setStyle(Paint.Style.STROKE);
                /*第二个参数是进度开始的角度，-90表示从12点方向开始走进度，如果是0表示从三点钟方向走进度，依次类推
                 *public void drawArc(RectF oval, float startAngle, float sweepAngle, boolean useCenter, Paint paint)
                    oval :指定圆弧的外轮廓矩形区域。
                    startAngle: 圆弧起始角度，单位为度。
                    sweepAngle: 圆弧扫过的角度，顺时针方向，单位为度。
                    useCenter: 如果为True时，在绘制圆弧时将圆心包括在内，通常用来绘制扇形。
                    paint: 绘制圆弧的画板属性，如颜色，是否填充等
                 *
                */
                canvas.drawArc(oval, startAngle, 360 * progress / max, false, paint);  //根据进度画圆弧
                break;
            }
            case FILL: {
                paint.setStyle(Paint.Style.FILL_AND_STROKE);
                if (progress != 0)
                    canvas.drawArc(oval, startAngle, 360 * progress / max, true, paint);  //根据进度画圆弧
                break;
            }
        }


    }

    public synchronized int getMax() {
        return max;
    }

    /**
     * 设置进度的最大值
     *
     * @param max
     */
    public synchronized void setMax(int max) {
        if (max < 0) {
            throw new IllegalArgumentException("max not less than 0");
        }
        this.max = max;
    }

    /**
     * 获取进度.需要同步
     *
     * @return
     */
    public synchronized int getProgress() {
        return progress;
    }

    /**
     * 设置进度，此为线程安全控件，由于考虑多线的问题，需要同步
     * 刷新界面调用postInvalidate()能在非UI线程刷新
     *
     * @param progress
     */
    public synchronized void setProgress(int progress) {
        if (progress < 0) {
            throw new IllegalArgumentException("progress not less than 0");
        }
        if (progress > max) {
            progress = max;
        }
        if (progress <= max) {
            this.progress = progress;
            postInvalidate();
        }
    }


    public int getCircleColor() {
        return roundColor;
    }

    public void setCircleColor(int CircleColor) {
        this.roundColor = CircleColor;
    }

    public int getCircleProgressColor() {
        return roundProgressColor;
    }

    public void setCircleProgressColor(int CircleProgressColor) {
        this.roundProgressColor = CircleProgressColor;
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        this.textSize = textSize;
    }

    public float getRoundWidth() {
        return roundWidth;
    }

    public void setRoundWidth(float roundWidth) {
        this.roundWidth = roundWidth;
    }

}
