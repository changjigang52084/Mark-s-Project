package com.lzkj.aidlservice.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.bo.HttpBo;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.bo.VendorBo;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kchang on 2016/10/17.
 */
public class SQLiterManager {

    private static final String TAG = "SQLiterManager";


    /**
     * 数据库之前版本号是2，现在修改成3 。modify by cjg 20190612
     */
    private static final int SQLIT_VERSION = 5;
    /**
     * 数据库之前版本号是2，现在修改成3 。modify by cjg 20190612
     */

    private SQLiteHelper mSQLiteHelper;

    private SQLiteDatabase mSQLiteDatabase;

    private SQLiterManager(Context context) {
        Log.d(TAG, "SQLiterManager context.");
        mSQLiteHelper = new SQLiteHelper(context, SQLIT_VERSION);
        mSQLiteDatabase = mSQLiteHelper.getWritableDatabase();
    }

    public static SQLiterManager getInstance() {
        return SQLiteManagerInstance.sqLiteManager;
    }

    private static class SQLiteManagerInstance {
        private static final SQLiterManager sqLiteManager = new SQLiterManager(CommunicationApp.get());
    }

    /**
     * 插入请求记录
     *
     * @param httpRequestBean
     * @param requestMethod
     * @return
     */
    public synchronized boolean insterHttpBo(HttpRequestBean httpRequestBean, String requestMethod) {
        boolean isSuccess = false;
        if (null != httpRequestBean && null != requestMethod && null != mSQLiteDatabase) {
            ContentValues httpRequestContentValues = new ContentValues();
            httpRequestContentValues.put(SQLiteHelper.REQUEST_URL, httpRequestBean.getRequestUrl());
            httpRequestContentValues.put(SQLiteHelper.REQUEST_PRM, httpRequestBean.getRequestParm());
            httpRequestContentValues.put(SQLiteHelper.REQUEST_METHOD, requestMethod);
            httpRequestContentValues.put(SQLiteHelper.REQUEST_TAG, httpRequestBean.getRequestTag());
            try {
                mSQLiteDatabase.beginTransaction();
                long result = mSQLiteDatabase.insert(SQLiteHelper.TABLE_HTTP_REQUEST, null, httpRequestContentValues);
                Log.d(TAG, "Insert request record result: " + result + " ,request_url: " + httpRequestBean.getRequestUrl() + " ,request_prm: " + httpRequestBean.getRequestParm() + " ,request_method: " +
                        requestMethod + " ,request_tag: " + httpRequestBean.getRequestTag());
                if (result > 0) {
                    isSuccess = true;
                }
                mSQLiteDatabase.setTransactionSuccessful();
            } finally {
                mSQLiteDatabase.endTransaction();
            }
        }
        return isSuccess;
    }

    /**
     * 删除成功的记录
     *
     * @param requestUrl
     * @return
     */
    public synchronized boolean delHttpBo(String requestUrl, String requestTag) {
        boolean isSuccess = false;
        if (null != requestUrl && null != requestTag && null != mSQLiteDatabase) {
            try {
                mSQLiteDatabase.beginTransaction();
                int result = mSQLiteDatabase.delete(SQLiteHelper.TABLE_HTTP_REQUEST,
                        SQLiteHelper.REQUEST_URL + " = ? and "
                                + SQLiteHelper.REQUEST_TAG + " = ?",

                        new String[]{requestUrl, requestTag});

                Log.d(TAG, "Delete successful records result: " + result + " ,requestUrl: " + requestUrl + " ,requestTag: " + requestTag);
                if (result > 0) {
                    isSuccess = true;
                }
                mSQLiteDatabase.setTransactionSuccessful();
            } finally {
                mSQLiteDatabase.endTransaction();
            }
        }
        return isSuccess;
    }

    /**
     * 查询未成功的记录
     *
     * @return
     */
    public synchronized List<HttpBo> getHttpBoList() {
        List<HttpBo> httpRequestBeanList = new ArrayList<HttpBo>();
        if (null != mSQLiteDatabase) {
            Cursor cursor = mSQLiteDatabase.query(SQLiteHelper.TABLE_HTTP_REQUEST,
                    new String[]{SQLiteHelper.REQUEST_URL, SQLiteHelper.REQUEST_PRM,
                            SQLiteHelper.REQUEST_METHOD, SQLiteHelper.REQUEST_TAG},
                    null, null, null, null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    HttpBo httpRequestBean = new HttpBo();
                    httpRequestBean.setHttpUrl(cursor.getString(0));
                    httpRequestBean.setHttpRequestParam(cursor.getString(1));
                    httpRequestBean.setHttpMethod(cursor.getString(2));
                    httpRequestBean.setHttpRequestTag(cursor.getString(3));
                    httpRequestBeanList.add(httpRequestBean);
                }
                cursor.close();
            }
        }
        return httpRequestBeanList;
    }

    /**
     * 插入出货记录
     *
     * @param vendorBo
     * @return
     */
    public synchronized boolean insterVendorOrder(VendorBo vendorBo) {
        boolean isSuccess = false;
        if (null != vendorBo && null != mSQLiteDatabase) {
            VendorBo oldVendorBo = queryVendorOrder(vendorBo.getVendorOrderKey());
            if (null != oldVendorBo) {
                delVendorBo(vendorBo.getVendorOrderKey());
            }
            ContentValues vendorOrderContentValues = new ContentValues();
            vendorOrderContentValues.put(SQLiteHelper.VENDOR_ORDER_KEY, vendorBo.getVendorOrderKey());
            vendorOrderContentValues.put(SQLiteHelper.VENDOR_ORDER_DATE, vendorBo.getVendorOrderDate());
            vendorOrderContentValues.put(SQLiteHelper.VENDOR_ORDER_RESULT, vendorBo.getVendorOrderResult());
            try {
                mSQLiteDatabase.beginTransaction();
                long result = mSQLiteDatabase.insert(SQLiteHelper.TABLE_VENDOR_ORDER, null
                        , vendorOrderContentValues);
                if (result > 0) {
                    isSuccess = true;
                }
                mSQLiteDatabase.setTransactionSuccessful();
            } finally {
                mSQLiteDatabase.endTransaction();
            }
        }
        return isSuccess;
    }

    /**
     * 根据订单号查询VendorBo
     *
     * @param orderKey 订单key
     * @return
     */
    public VendorBo queryVendorOrder(String orderKey) {
        VendorBo vendorBo = null;
        if (null != mSQLiteDatabase && null != orderKey) {
            Cursor cursor = mSQLiteDatabase.query(SQLiteHelper.TABLE_VENDOR_ORDER,
                    new String[]{SQLiteHelper.VENDOR_ORDER_KEY, SQLiteHelper.VENDOR_ORDER_DATE
                            , SQLiteHelper.VENDOR_ORDER_RESULT}, SQLiteHelper.VENDOR_ORDER_KEY + " = ? "
                    , new String[]{orderKey}, null, null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    vendorBo = new VendorBo();
                    vendorBo.setVendorOrderKey(cursor.getString(0));
                    vendorBo.setVendorOrderDate(cursor.getString(1));
                    vendorBo.setVendorOrderResult(cursor.getInt(2));
                }
                cursor.close();
            }
        }
        return vendorBo;
    }


    /**
     * 查询所有的订单记录
     *
     * @return
     */
    public synchronized List<VendorBo> getVendorOrderList() {
        List<VendorBo> vendorBoArrayList = new ArrayList<>();
        if (null != mSQLiteDatabase) {
            Cursor cursor = mSQLiteDatabase.query(SQLiteHelper.TABLE_VENDOR_ORDER,
                    new String[]{SQLiteHelper.VENDOR_ORDER_KEY, SQLiteHelper.VENDOR_ORDER_DATE
                            , SQLiteHelper.VENDOR_ORDER_RESULT}, null, null, null, null, null);
            if (null != cursor) {
                while (cursor.moveToNext()) {
                    VendorBo vendorBo = new VendorBo();
                    vendorBo.setVendorOrderKey(cursor.getString(0));
                    vendorBo.setVendorOrderDate(cursor.getString(1));
                    vendorBo.setVendorOrderResult(cursor.getInt(2));
                    vendorBoArrayList.add(vendorBo);
                }
                cursor.close();
            }
        }
        return vendorBoArrayList;
    }

    /**
     * 删除订单
     *
     * @param orderKey
     * @return
     */
    public boolean delVendorBo(String orderKey) {
        boolean isSuccess = false;
        if (null != orderKey && null != mSQLiteDatabase) {
            try {
                mSQLiteDatabase.beginTransaction();
                int result = mSQLiteDatabase.delete(SQLiteHelper.TABLE_VENDOR_ORDER,
                        SQLiteHelper.VENDOR_ORDER_KEY + " = ? ", new String[]{orderKey});
                if (result > 0) {
                    isSuccess = true;
                }
                mSQLiteDatabase.setTransactionSuccessful();
            } finally {
                mSQLiteDatabase.endTransaction();
            }
        }
        return isSuccess;
    }

    /**
     * 删除表数据
     *
     * @param tableName
     * @return
     */
    public boolean delTableData(String tableName) {
        if (null == mSQLiteDatabase && null != tableName) {
            return false;
        }
        boolean isSuccess = false;
        mSQLiteDatabase.beginTransaction();
        try {
            int result = mSQLiteDatabase.delete(tableName, null, null);
            isSuccess = result > 0;
            mSQLiteDatabase.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            mSQLiteDatabase.endTransaction();
        }
        return isSuccess;
    }
}
