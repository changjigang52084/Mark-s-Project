package com.lzkj.ui.db;


import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库类
 *
 * @author lyhuang
 * @date 2016-1-16 下午2:36:03
 */
public class SQLiteHelper extends SQLiteOpenHelper {
	private static final LogTag TAG = LogUtils.getLogTag(SQLiteHelper.class.getSimpleName(), true);
	private static String DBNAME = "EposterUI.db";

	public SQLiteHelper(Context context, int version) {
		super(context, DBNAME, null, version);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		String creatDeviceTableSql = "CREATE TABLE IF NOT EXISTS tbl_weather "
				+ "(id integer primary key autoincrement,"
				+ " city varchar,"
				+ " date INTEGER,"
				+ " week varchar,"
				+ " curTemp varchar,"
				+ " aqi varchar," 
				+ " fengxiang varchar,"
				+ " fengli varchar," 
				+ " hightemp varchar," 
				+ " lowtemp varchar," 
				+ " type varchar)"; 
		db.execSQL(creatDeviceTableSql);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		LogUtils.i(TAG,"onUpgrade","upgrade database. oldVersion:" +oldVersion +
				", newVersion:"+newVersion);
		db.execSQL("DROP TABLE IF EXISTS tbl_device");
		onCreate(db);
	}

}
