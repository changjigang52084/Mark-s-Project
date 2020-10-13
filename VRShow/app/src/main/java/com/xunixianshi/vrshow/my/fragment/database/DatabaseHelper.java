package com.xunixianshi.vrshow.my.fragment.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2016/10/11.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 5;  //数据库版本号

    private static final String DATABASE_NAME = "upload_video";  //数据库名称
    public static final String TABLE_NAME = "upload_tab";  //数据库表 名称

    private static final String _ID = "_id";//主键id
    public static final String U_ID = "uid";//资源id
    public static final String RESOURCE_ID = "resourceId";//资源id
    public static final String IMAGE_PATH = "imagePath";//封面路径
    public static final String RESOURCE_NAME = "resourceName";//资源名称
    public static final String MD5_RESOURCE_NAME = "md5ResourceName";//七牛上传资源加密名称
    public static final String RESOURCE_INTRODUCE = "resourceIntroduce";//资源简介
    public static final String UPLOAD_PROGRESS = "uploadProgress";//上传进度
    public static final String UPLOAD_RESOURCE_PATH = "uploadResourcePath";//七牛上传缓存文件路径
    public static final String VIDEO_PATH = "videoPath";//上传文件路径
    public static final String VIDEO_TYPE_ID = "videoTypeId";//上传文件类型
    public static final String VIDEO_SIZE = "videoSize";//上传文件大小
    public static final String VIDEO_FORMAT = "videoFormat";//上传文件格式
    public static final String UPLOAD_STATE = "uploadState";//上传状态 1 开始 2 停止 3失败
    public static final String QINIU_PATH = "qiniuPath";//上传状态 1 开始 2 停止 3失败


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        // 创建资源上传表
        String sqldept = "Create table upload_tab (" + _ID + " integer primary key autoincrement, "
                + U_ID + " varchar,"
                + RESOURCE_ID + " varchar,"
                + IMAGE_PATH + " varchar,"
                + RESOURCE_NAME + " varchar,"
                + MD5_RESOURCE_NAME + " varchar,"
                + RESOURCE_INTRODUCE + " varchar,"
                + UPLOAD_PROGRESS + " varchar,"
                + VIDEO_TYPE_ID + " varchar,"
                + VIDEO_SIZE + " varchar,"
                + UPLOAD_RESOURCE_PATH + " varchar,"
                + UPLOAD_STATE + " varchar,"
                + VIDEO_FORMAT + " varchar,"
                + QINIU_PATH + " varchar,"
                + VIDEO_PATH + " varchar)";
        db.execSQL(sqldept);
    }

    // 更新数据库
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 这个数据库只是缓存在线数据，所以升级方法是丢弃以前的数据然后重新开始
        db.beginTransaction();

        //改名数据库表
        db.execSQL("alter table upload_tab rename to upload_tab2");

        //新建表单
        onCreate(db);
        //插入原有的数据
        db.execSQL("insert into upload_tab select * ,''from upload_tab2");
        //如果增加了列属性，则使用双引号”” 来补充原来不存在的数据
        //删除临时表单
        db.execSQL("drop table upload_tab2");

        db.setTransactionSuccessful();
        db.endTransaction();
    }


}
