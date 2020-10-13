package com.lzkj.ui.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.lzkj.ui.models.WeatherBo;
import com.lzkj.ui.util.DateTimeUtil;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.util.ArrayList;
import java.util.List;


/**
 * 数据库管理
 *
 * @author lyhuang
 * @date 2016-1-16 下午4:22:37
 */
public class SQLiteManager {
    private static final LogTag TAG = LogUtils.getLogTag(SQLiteManager.class.getSimpleName(), true);
    private SQLiteHelper sqLiteHelper;
    private SQLiteDatabase sqLiteDatabase;
    /**
     * 表名
     */
    private final static String WEATHER_TABLE_NAME = "tbl_weather";
    /**
     * ID
     */
    private static final String key_id = "id";
    /**
     * 城市
     */
    private static final String key_city = "city";
    /**
     * 日期
     */
    private static final String key_date = "date";
    /**
     * 星期
     */
    private static final String key_week = "week";
    /**
     * 当前温度
     */
    private static final String key_curTemp = "curTemp";
    /**
     * pm值
     */
    private static final String key_aqi = "aqi";
    /**
     * 风向
     */
    private static final String key_fengxiang = "fengxiang";
    /**
     * 风力
     */
    private static final String key_fengli = "fengli";
    /**
     * 最高温度
     */
    private static final String key_hightemp = "hightemp";
    /**
     * 最低温度
     */
    private static final String key_lowtemp = "lowtemp";
    /**
     * 天气状态
     */
    private static final String key_type = "type";

    /**
     * 数据库版本号
     */
    private int version = 1;

    private static SQLiteManager sqLiteManager;

    private SQLiteManager(Context context) {
        sqLiteHelper = new SQLiteHelper(context, version);
        sqLiteDatabase = sqLiteHelper.getWritableDatabase();
    }

    public static SQLiteManager getInstance(Context context) {
        if (null == sqLiteManager) {
            init(context);
        }
        return sqLiteManager;
    }

    private synchronized static void init(Context context) {
        if (null == sqLiteManager) {
            sqLiteManager = new SQLiteManager(context);
        }
    }

    /**
     * 关闭数据库
     */
    public synchronized void closeDatabase() {
        if (null != sqLiteHelper) {
            sqLiteHelper.close();
            sqLiteDatabase.close();
        }
    }

    /**
     * 清空表数据
     */
    public synchronized void deleteAllData() {
        if (null == sqLiteDatabase) {
            return;
        }
        sqLiteDatabase.execSQL("delete from " + WEATHER_TABLE_NAME);
    }

    /**
     * 插入一组天气
     *
     * @param
     * @return
     */
    public synchronized boolean insertWeather(WeatherBo weather) {
        if (null == sqLiteDatabase) {
            return false;
        }
        if (weather == null) {
            LogUtils.e(TAG, "insertWeather", "weather is null");
            return false;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(key_city, weather.getCity());
        contentValues.put(key_date, DateTimeUtil.getTimeToDate(weather.getDate()));
        contentValues.put(key_week, weather.getWeek());
        contentValues.put(key_aqi, weather.getAqi());
        contentValues.put(key_curTemp, weather.getCurTemp());
        contentValues.put(key_fengli, weather.getFengli());
        contentValues.put(key_fengxiang, weather.getFengxiang());
        contentValues.put(key_hightemp, weather.getHightemp());
        contentValues.put(key_lowtemp, weather.getLowtemp());
        contentValues.put(key_type, weather.getType());

        sqLiteDatabase.beginTransaction();
        try {
            sqLiteDatabase.insert(WEATHER_TABLE_NAME, null, contentValues);
            sqLiteDatabase.setTransactionSuccessful();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            sqLiteDatabase.endTransaction();
        }
    }

    /**
     * 根据城市和日期查询天气信息
     *
     * @param
     * @return
     */
    public synchronized List<WeatherBo> queryWeatherByCityAndDate(long date) {
        if (null == sqLiteDatabase) {
            return null;
        }
        Cursor cursor = sqLiteDatabase.query(WEATHER_TABLE_NAME
                , new String[]{key_city, key_date, key_week, key_curTemp, key_aqi, key_fengxiang
                        , key_fengli, key_hightemp, key_lowtemp, key_type},
                key_date + " = ?", new String[]{String.valueOf(date)}, null, null, null);
        if (null == cursor) {
            return null;
        }
        List<WeatherBo> list = new ArrayList<WeatherBo>();
        while (cursor.moveToNext()) {
            WeatherBo weather = new WeatherBo();
            weather.setCity(cursor.getString(0));
            weather.setDate(DateTimeUtil.getStringTimeToFormat("yyyy-MM-dd", cursor.getLong(1)));
            weather.setWeek(cursor.getString(2));
            weather.setCurTemp(cursor.getString(3));
            weather.setAqi(cursor.getString(4));
            weather.setFengxiang(cursor.getString(5));
            weather.setFengli(cursor.getString(6));
            weather.setHightemp(cursor.getString(7));
            weather.setLowtemp(cursor.getString(8));
            weather.setType(cursor.getString(9));
            list.add(weather);
        }
        cursor.close();
        return list;
    }


}
