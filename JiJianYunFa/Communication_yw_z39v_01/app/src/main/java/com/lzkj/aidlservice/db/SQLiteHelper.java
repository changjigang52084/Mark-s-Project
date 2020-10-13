package com.lzkj.aidlservice.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.lzkj.aidlservice.util.LogUtils;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月19日 上午11:43:08
 * @parameter sqlite管理类
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(SQLiteHelper.class.getSimpleName(), true);
    private static String DatabaseName = "connect.db";
    /**
     * http请求类的表
     **/
    public static final String TABLE_HTTP_REQUEST = "http_request";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS ";
    /**
     * http请求类的表请求的url
     **/
    static final String REQUEST_URL = "request_url";
    /**
     * http请求类的表中请求的参数
     **/
    static final String REQUEST_PRM = "request_prm";
    /**
     * http请求类的表请求的方法
     **/
    static final String REQUEST_METHOD = "request_method";
    /**
     * http请求类的表请求的方法tag
     **/
    static final String REQUEST_TAG = "request_tag";

    private static final String TABLE_HTTP_REQUEST_STRUCTURE = " (id integer primary key autoincrement, " +
            "request_url varchar(100), request_prm varchar(500), request_method varchar(10), request_tag varchar(50))";

    /**
     * 订单的表
     **/
    static final String TABLE_VENDOR_ORDER = "vendor_order";
    /**
     * 售货机订单key
     **/
    static final String VENDOR_ORDER_KEY = "vendor_order_key ";
    /**
     * 售货机订单时间
     **/
    static final String VENDOR_ORDER_DATE = "vendor_order_date ";
    /**
     * 售货机订单结果
     **/
    static final String VENDOR_ORDER_RESULT = "vendor_order_result ";

    private static final String VARCHAR_SIZE = " varchar(100)";
    private static final String INTEGER = " integer";
    private static final String TABLE_SPLIT = ",";
    private static final String TABLE_VENDOR_ORDER_SQL = " (id integer primary key autoincrement, ";
    private static final String TABLE_VENDOR_ORDER_SQL_END = ")";

    public SQLiteHelper(Context context, int version) {
        super(context, DatabaseName, null, version);
        LogUtils.i(TAG, "SQLiteHelper", "version = " + version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createHttpRequestTab(db);
        createVendorOrderTab(db);
    }

    private void createHttpRequestTab(SQLiteDatabase db) {
        StringBuffer createTableHttpRequestSql = new StringBuffer(3);
        createTableHttpRequestSql.append(CREATE_TABLE_SQL);
        createTableHttpRequestSql.append(TABLE_HTTP_REQUEST);
        createTableHttpRequestSql.append(TABLE_HTTP_REQUEST_STRUCTURE);
        db.execSQL(createTableHttpRequestSql.toString());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//            createVendorOrderTab(db);
        LogUtils.i(TAG, "onUpgrade", "oldVersion = " + oldVersion + "\r\n" + "newVersion = " + newVersion);
        if (oldVersion < newVersion) {
            db.execSQL("drop table if exists " + TABLE_HTTP_REQUEST);
            onCreate(db);
        }
    }

    private void createVendorOrderTab(SQLiteDatabase db) {
        StringBuffer createVendorOrderTabSql = new StringBuffer(3);
        createVendorOrderTabSql.append(CREATE_TABLE_SQL);
        createVendorOrderTabSql.append(TABLE_VENDOR_ORDER);
        createVendorOrderTabSql.append(TABLE_VENDOR_ORDER_SQL);
        createVendorOrderTabSql.append(VENDOR_ORDER_KEY);
        createVendorOrderTabSql.append(VARCHAR_SIZE);
        createVendorOrderTabSql.append(TABLE_SPLIT);
        createVendorOrderTabSql.append(VENDOR_ORDER_DATE);
        createVendorOrderTabSql.append(VARCHAR_SIZE);
        createVendorOrderTabSql.append(TABLE_SPLIT);
        createVendorOrderTabSql.append(VENDOR_ORDER_RESULT);
        createVendorOrderTabSql.append(INTEGER);
        createVendorOrderTabSql.append(TABLE_VENDOR_ORDER_SQL_END);
        db.execSQL(createVendorOrderTabSql.toString());
    }
}