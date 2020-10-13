package com.unccr.zclh.dsdps.db;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * 数据库表
 * @author changkai
 *
 */
public class SQLiteHelper extends SQLiteOpenHelper {
    private static String databaseName = "download.db";
    /**下载进度表**/
    public static final String TABLE_DOWNLOAD_PORGRESS = "download_progress";
    /**上传记录表**/
    public static final String TABLE_UPLOAD_RECORD = "upload_record";
    /**下载任务表**/
    public static final String TABLE_DOWNLOAD_TASK = "download_task";
    /**流量表**/
    public static final String TABLE_FLOW = "flow_table";
    /**流量开始的月份**/
    static final String FLOW_MONTH = "flow_month";
    /**流量开始的日期(20161020)**/
    static final String FLOW_DATE = "flow_date";
    /**上传的流量**/
    static final String UPLOAD_FLOW = "upload_flow";
    /**下载的流量**/
    static final String DOWNLOAD_FLOW = "download_flow";

    /**http请求类的表**/
    public static final String TABLE_HTTP_REQUEST = "http_request";

    private static final String CREATE_TABLE_SQL = "CREATE TABLE IF NOT EXISTS ";
    /**http请求类的表请求的url**/
    static final String REQUEST_URL = "request_url";
    /**http请求类的表中请求的参数**/
    static final String REQUEST_PRM = "request_prm";
    /**http请求类的表请求的方法**/
    static final String REQUEST_METHOD = "request_method";
    /**http请求类的表请求的方法tag**/
    static final String REQUEST_TAG = "request_tag";

    private static final String TABLE_HTTP_REQUEST_STRUCTURE = " (id integer primary key autoincrement, " +
            "request_url varchar(100), request_prm varchar(500), request_method varchar(10), request_tag varchar(50))";

    private String getCreateFlowTableSql() {
        StringBuffer stringBuffer = new StringBuffer(12);
        stringBuffer.append(CREATE_TABLE_SQL);
        stringBuffer.append(TABLE_FLOW);
        stringBuffer.append(" (id integer primary key autoincrement, ");
        stringBuffer.append(FLOW_MONTH);
        stringBuffer.append(" varchar(2),");
        stringBuffer.append(FLOW_DATE);
        stringBuffer.append(" varchar(11),");
        stringBuffer.append(UPLOAD_FLOW);
        stringBuffer.append(" varchar(20),");
        stringBuffer.append(DOWNLOAD_FLOW);
        stringBuffer.append(" varchar(20)");
        stringBuffer.append(")");

        return stringBuffer.toString();
    }

    private String getCreateHttpRequestTableSql(){
        StringBuffer createTableHttpRequestSql = new StringBuffer(3);
        createTableHttpRequestSql.append(CREATE_TABLE_SQL);
        createTableHttpRequestSql.append(TABLE_HTTP_REQUEST);
        createTableHttpRequestSql.append(TABLE_HTTP_REQUEST_STRUCTURE);
        return createTableHttpRequestSql.toString();
    }


    public SQLiteHelper(Context context, int version) {
        super(context, databaseName, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String creatFileDownloadTableSql = "CREATE TABLE IF NOT EXISTS download_progress (id integer primary key autoincrement, download_path varchar(100), thread_id INTEGER,progress INTEGER, download_local_path varchar(100))";
        String creatFileUploadTableSql = "CREATE TABLE IF NOT EXISTS upload_record (id integer primary key autoincrement, upload_file_path varchar(100), upload_objectkey varchar(20))";
        String creatFileDownloadTaskTableSql = "CREATE TABLE IF NOT EXISTS download_task (id integer primary key autoincrement, download_path varchar(100), file_name varchar(100), type INTEGER, download_local_path varchar(100))";

        db.execSQL(creatFileDownloadTableSql);
        db.execSQL(creatFileUploadTableSql);
        db.execSQL(creatFileDownloadTaskTableSql);
        db.execSQL(getCreateFlowTableSql());
        db.execSQL(getCreateHttpRequestTableSql());


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//		String updateDownloadTaskTableSql = "ALTER TABLE download_task ADD download_local_path varchar(100)";
//		String updateFileDownloadTableSql = "ALTER TABLE download_progress ADD download_local_path varchar(100)";
//		db.execSQL(updateDownloadTaskTableSql);
//		db.execSQL(updateFileDownloadTableSql);
//		creatNewTable(db);
        dropTable(db,TABLE_FLOW);
        dropTable(db,TABLE_DOWNLOAD_PORGRESS);
        dropTable(db,TABLE_UPLOAD_RECORD);
        dropTable(db,TABLE_DOWNLOAD_TASK);
        dropTable(db,TABLE_HTTP_REQUEST);
        onCreate(db);

    }

    private void dropTable(SQLiteDatabase db,String tableName){
        db.execSQL("DROP TABLE IF EXISTS " + tableName);
    }

}