package com.lzkj.ui.fragment.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;

import com.baize.adpress.core.protocol.dto.WeatherPackage;
import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.BitmapUtil;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.StringUtil;

/**
 * 天气Fragment显示用的Bitmap，以及刷新操作
 *
 * @author chenercai
 */
public class WeatherBitmap {
    private static final LogTag TAG = LogUtils.getLogTag(WeatherBitmap.class.getSimpleName(), true);

    private static final Context mContext = EPosterApp.getApplication();

    private static int BITMAP_WIDTH = 625; // Bitmap的宽度
    private static int BITMAP_HEIGHT = 400; // Bitmap的高度

    private WeatherPackage mWeatherPackage;

    private String cityName = null; // 城市名称
    private String todayCondition = null; // 今天的天气状况
    private String todayTemperatuer = null; // 今天的温度

    private Canvas mCanvas = null;
    private Paint mPaint = null;

    private Bitmap weatherBitmap = null;
    private Bitmap enlargeBitmap = null; // 放大或者缩小操作所需要的Bitmap
    private Bitmap todayBackground = null; // 今天的天气背景

    public Bitmap getWeatherInfoBitmap(WeatherPackage weatherPackage, int areaWidth, int areaHeight) {
        if (weatherPackage == null) {
            LogUtils.w(TAG, "getWeatherInfoBitmap", "WeatherInfo cannot be null!");
            return null;
        }
        BITMAP_WIDTH = areaWidth;
        BITMAP_HEIGHT = areaHeight;
        mWeatherPackage = weatherPackage;
        init();
        return drawWeatherBitmap();

    }

    private void init() {
        try {
            initCityName();
            initTemperatuerInfo();
            initConditionInfo();
            initBitmapInfo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 加载城市名称
     */
    private void initCityName() {
        if (null != mWeatherPackage && !StringUtil.isNullStr(mWeatherPackage.getCity())) {
            cityName = mWeatherPackage.getCity();
        }
    }


    /**
     * 加载今天的温度
     */
    private void initTemperatuerInfo() {
        if (null == mWeatherPackage || StringUtil.isNullStr(mWeatherPackage.getLow())
                || StringUtil.isNullStr(mWeatherPackage.getHigh())) {
            return;
        }
        StringBuffer buffer = new StringBuffer(5);
        buffer.append(mWeatherPackage.getLow());
        buffer.append(" ~ ");
        buffer.append(mWeatherPackage.getHigh());
        buffer.append("\u00b0");
        buffer.append(StringUtil.getString(R.string.temp_unit));
        todayTemperatuer = buffer.toString();
    }

    /**
     * 加载今天的天气状况
     */
    private void initConditionInfo() {
        if (null != mWeatherPackage) {
            todayCondition = mWeatherPackage.getWeather();
            if (StringUtil.isNullStr(todayCondition)) {
                todayCondition = null;
            }
        }
    }

    /**
     * 加载所需的Bitmap（今天的天气背景，底部的天气图标）
     */
    private void initBitmapInfo() {
        if (null != mWeatherPackage) {
            String weatherCode = mWeatherPackage.getWeatherCode();
            LogUtils.d(TAG, "initBitmapInfo", "weatherCode : " + weatherCode); // 300
            if (!StringUtil.isNullStr(weatherCode)) {
                Bitmap weatherCodeBitmap = BitmapFactory.decodeResource(mContext.getResources(), getDrawableId(weatherCode));
                if (null != weatherCodeBitmap) {
                    int areaMin = Math.max(BITMAP_WIDTH, BITMAP_HEIGHT);
                    int viewWidth = areaMin / 3;
                    int viewHeight = viewWidth;
                    try {
                        todayBackground = BitmapUtil.resizeImage(weatherCodeBitmap, viewWidth, viewHeight);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    /**
     * 根据资源名获取资源id
     *
     * @param drawableName
     * @return
     */
    private int getDrawableId(String drawableName) {
        return mContext.getResources().getIdentifier(Constants.WEATHER_ICON_PREFIX + drawableName, Constants.DRAWABLE, mContext.getPackageName());
    }

    /**
     * 绘制天气的Bitmap
     */
    private Bitmap drawWeatherBitmap() {
        if (null == todayBackground || null == todayTemperatuer
                || null == cityName || null == todayCondition) {
            return null;
        }
        Bitmap bgBitmap = Bitmap.createBitmap(BITMAP_WIDTH, BITMAP_HEIGHT, Config.ARGB_4444);
        mCanvas = new Canvas(bgBitmap);
        mPaint = new Paint();

        mPaint.setAntiAlias(true); // 实现抗锯齿的效果
        mPaint.setFilterBitmap(true);

        int todayX = 10;
        int todayY = (BITMAP_HEIGHT / 2) - (todayBackground.getHeight() / 2);

        // 绘制天气图标
        mCanvas.drawBitmap(todayBackground, todayX, todayY, mPaint);
        int textSize = BITMAP_HEIGHT / 5;
        int todayTemperatuerX = todayBackground.getWidth() + (todayX * 3);

        int cityTextSize = BITMAP_HEIGHT / 6;
        int cityY = (textSize + 20);

        // 绘制城市名称
        mPaint.setTextSize(cityTextSize);
        mPaint.setColor(Color.WHITE);
        mCanvas.drawText(cityName, todayTemperatuerX, cityY, mPaint);

        int conditionTextSize = BITMAP_HEIGHT / 6;
        int conditionY = cityY + cityTextSize;

        // 绘制天气状况
        mPaint.setTextSize(conditionTextSize);
        mPaint.setColor(Color.WHITE);
        mCanvas.drawText(todayCondition, todayTemperatuerX, (conditionY + 5), mPaint);

        int todayTemperatuerY = textSize + 10 + conditionY;

        // 绘制温度
        mPaint.setTextSize(textSize);
        mPaint.setColor(Color.WHITE);
        mCanvas.drawText(todayTemperatuer, todayTemperatuerX, todayTemperatuerY, mPaint);

        return bgBitmap;
    }

    /**
     * 销毁余留的Bitmap
     */
    public void destroyWeatherBitmap() {
        if (weatherBitmap != null && !weatherBitmap.isRecycled()) {
            weatherBitmap.isRecycled();
            weatherBitmap = null;
            LogUtils.d(TAG, "weatherBitmap has already been recycled", "weatherBitmap = " + weatherBitmap);
        }

        if (enlargeBitmap != null && !enlargeBitmap.isRecycled()) {
            enlargeBitmap.isRecycled();
            enlargeBitmap = null;
            LogUtils.d(TAG, "wnlargeBitmap has already been recycled", "enlargeBitmap = " + enlargeBitmap);
        }

        if (todayBackground != null && !todayBackground.isRecycled()) {
            todayBackground.isRecycled();
            todayBackground = null;
            LogUtils.d(TAG, "todayBackground has already been recycled", "todayBackground = " + todayBackground);
        }
    }
}