package com.xunixianshi.vrshow.my.fragment.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.hch.utils.rsa.Base64;
import com.hch.viewlib.util.MLog;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/11.
 */

public class UploadDatabaseTools {
    private Context context;
    private DatabaseHelper dbhelper;
    private SQLiteDatabase sqlitedatabase;

    public UploadDatabaseTools(Context context) {
        this.context = context;
    }

    //打开数据库连接
    public void opendb(Context context) {
        dbhelper = new DatabaseHelper(context);
        sqlitedatabase = dbhelper.getWritableDatabase();
    }

    //关闭数据库连接
    public void closedb(Context context) {
        if (sqlitedatabase.isOpen()) {
            sqlitedatabase.close();
        }
    }

    public void saveOrUpdata(UploadBean uploadBean) {
        MLog.d("saveOrUpdata:");
        if (selectById(uploadBean.getResourceName())) {
            updata(uploadBean);
        } else {
            save(uploadBean);
        }
    }

    /**
     * 保存上传文件断点数据
     */
    private void save(UploadBean uploadBean) {
        MLog.d("save:");
        opendb(context);
        String sql = DatabaseHelper.TABLE_NAME;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.U_ID, uploadBean.getUid());
        values.put(DatabaseHelper.RESOURCE_ID, uploadBean.getResourceId());
        values.put(DatabaseHelper.IMAGE_PATH, uploadBean.getImagePath());
        values.put(DatabaseHelper.RESOURCE_NAME, Base64.encode(uploadBean.getResourceName().getBytes()));
        values.put(DatabaseHelper.MD5_RESOURCE_NAME, uploadBean.getMd5ResourceName());
        values.put(DatabaseHelper.RESOURCE_INTRODUCE, uploadBean.getResourceIntroduce());
        values.put(DatabaseHelper.UPLOAD_PROGRESS, uploadBean.getUploadProgress());
        values.put(DatabaseHelper.UPLOAD_RESOURCE_PATH, uploadBean.getUploadResourcePath());
        values.put(DatabaseHelper.VIDEO_PATH, uploadBean.getVideoPath());
        values.put(DatabaseHelper.VIDEO_TYPE_ID, uploadBean.getVideoTypeId());
        values.put(DatabaseHelper.UPLOAD_STATE, uploadBean.getUploadState());
        values.put(DatabaseHelper.VIDEO_SIZE, uploadBean.getVideoSize());
        values.put(DatabaseHelper.VIDEO_FORMAT, uploadBean.getVideoFormat());
        values.put(DatabaseHelper.QINIU_PATH, uploadBean.getQiniuPath());
        sqlitedatabase.insert(sql, null, values);
        closedb(context);
    }

    /**
     * 保存上传文件断点数据
     */
    private void updata(UploadBean uploadBean) {
        MLog.d("updata:");
        opendb(context);
        String sql = DatabaseHelper.TABLE_NAME;
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.U_ID, uploadBean.getUid());
        values.put(DatabaseHelper.RESOURCE_ID, uploadBean.getResourceId());
        values.put(DatabaseHelper.IMAGE_PATH, uploadBean.getImagePath());
        values.put(DatabaseHelper.RESOURCE_NAME, Base64.encode(uploadBean.getResourceName().getBytes()));
        values.put(DatabaseHelper.MD5_RESOURCE_NAME, uploadBean.getMd5ResourceName());
        values.put(DatabaseHelper.RESOURCE_INTRODUCE, uploadBean.getResourceIntroduce());
        values.put(DatabaseHelper.UPLOAD_PROGRESS, uploadBean.getUploadProgress());
        values.put(DatabaseHelper.UPLOAD_RESOURCE_PATH, uploadBean.getUploadResourcePath());
        values.put(DatabaseHelper.VIDEO_PATH, uploadBean.getVideoPath());
        values.put(DatabaseHelper.VIDEO_TYPE_ID, uploadBean.getVideoTypeId());
        values.put(DatabaseHelper.UPLOAD_STATE, uploadBean.getUploadState());
        values.put(DatabaseHelper.VIDEO_SIZE, uploadBean.getVideoSize());
        values.put(DatabaseHelper.VIDEO_FORMAT, uploadBean.getVideoFormat());
        values.put(DatabaseHelper.QINIU_PATH, uploadBean.getQiniuPath());
        sqlitedatabase.update(sql, values, "md5ResourceName=?", new String[]{uploadBean.getMd5ResourceName()});
        closedb(context);
    }

    /**
     * 文件上传完成，删除上传文件断点数据
     */
    public void delete(String resourceName) {
        MLog.d("delete:");
        opendb(context);
        sqlitedatabase.delete(DatabaseHelper.TABLE_NAME, "md5ResourceName=?", new String[]{resourceName});
        closedb(context);
    }

    /**
     * 查询所有上传中的文件
     *
     * @author HeChuang
     * @time 2016/10/11 13:53
     */
    public ArrayList<UploadBean> selectAllUploadData() {
        ArrayList<UploadBean> uploadBeenList = new ArrayList<UploadBean>();
        opendb(context);
        String sql = "select * from upload_tab order by _id asc";
        Cursor cursor = sqlitedatabase.rawQuery(sql, null);
        int resourceIdIndex = cursor.getColumnIndex("resourceId");
        int imagePathIndex = cursor.getColumnIndex("imagePath");
        int resourceNameIndex = cursor.getColumnIndex("resourceName");
        int md5ResourceNameIndex = cursor.getColumnIndex("md5ResourceName");
        int resourceIntroduceIndex = cursor.getColumnIndex("resourceIntroduce");
        int uploadProgressIndex = cursor.getColumnIndex("uploadProgress");
        int uploadResourcePathIndex = cursor.getColumnIndex("uploadResourcePath");
        int videoPathIndex = cursor.getColumnIndex("videoPath");
        int videoTypeIdIndex = cursor.getColumnIndex("videoTypeId");
        int uploadStateIndex = cursor.getColumnIndex("uploadState");
        int videoSizeIndex = cursor.getColumnIndex("videoSize");
        int videoSizeFormat= cursor.getColumnIndex("videoFormat");
        int qiniuPathIndex= cursor.getColumnIndex("qiniuPath");
        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            UploadBean uploadBean = new UploadBean();
            uploadBean.setResourceId(cursor.getString(resourceIdIndex));
            uploadBean.setImagePath(cursor.getString(imagePathIndex));
            uploadBean.setResourceName(new String(Base64.decode(cursor.getString(resourceNameIndex))));
            uploadBean.setMd5ResourceName(cursor.getString(md5ResourceNameIndex));
            uploadBean.setResourceIntroduce(cursor.getString(resourceIntroduceIndex));
            uploadBean.setUploadProgress(cursor.getString(uploadProgressIndex));
            uploadBean.setUploadResourcePath(cursor.getString(uploadResourcePathIndex));
            uploadBean.setVideoPath(cursor.getString(videoPathIndex));
            uploadBean.setVideoTypeId(cursor.getString(videoTypeIdIndex));
            uploadBean.setUploadState(cursor.getString(uploadStateIndex));
            uploadBean.setVideoFormat(cursor.getString(videoSizeFormat));
            uploadBean.setVideoSize(cursor.getString(videoSizeIndex));
            uploadBean.setQiniuPath(cursor.getString(qiniuPathIndex));
            uploadBeenList.add(uploadBean);
        }
        cursor.close();
        closedb(context);
        return uploadBeenList;
    }

    /**
     * 根据用户id查询数据
     * @ClassName UploadDatabaseTools
     *@author HeChuang
     *@time 2016/12/12 16:11
     */
    public ArrayList<UploadBean> selectAllByUid(String uid) {
        ArrayList<UploadBean> uploadBeenList = new ArrayList<UploadBean>();
        opendb(context);
        String sql = "select * from upload_tab where uid = "+uid;
        Cursor cursor = sqlitedatabase.rawQuery(sql, null);
        int uidIndex = cursor.getColumnIndex("uid");
        int resourceIdIndex = cursor.getColumnIndex("resourceId");
        int imagePathIndex = cursor.getColumnIndex("imagePath");
        int resourceNameIndex = cursor.getColumnIndex("resourceName");
        int md5ResourceNameIndex = cursor.getColumnIndex("md5ResourceName");
        int resourceIntroduceIndex = cursor.getColumnIndex("resourceIntroduce");
        int uploadProgressIndex = cursor.getColumnIndex("uploadProgress");
        int uploadResourcePathIndex = cursor.getColumnIndex("uploadResourcePath");
        int videoPathIndex = cursor.getColumnIndex("videoPath");
        int videoTypeIdIndex = cursor.getColumnIndex("videoTypeId");
        int uploadStateIndex = cursor.getColumnIndex("uploadState");
        int videoSizeIndex = cursor.getColumnIndex("videoSize");
        int videoSizeFormat= cursor.getColumnIndex("videoFormat");
        int qiniuPathIndex= cursor.getColumnIndex("qiniuPath");
        for (cursor.moveToFirst(); !(cursor.isAfterLast()); cursor.moveToNext()) {
            UploadBean uploadBean = new UploadBean();
            uploadBean.setUid(cursor.getString(uidIndex));
            uploadBean.setResourceId(cursor.getString(resourceIdIndex));
            uploadBean.setImagePath(cursor.getString(imagePathIndex));
            uploadBean.setResourceName(new String(Base64.decode(cursor.getString(resourceNameIndex))));
            uploadBean.setMd5ResourceName(cursor.getString(md5ResourceNameIndex));
            uploadBean.setResourceIntroduce(cursor.getString(resourceIntroduceIndex));
            uploadBean.setUploadProgress(cursor.getString(uploadProgressIndex));
            uploadBean.setUploadResourcePath(cursor.getString(uploadResourcePathIndex));
            uploadBean.setVideoPath(cursor.getString(videoPathIndex));
            uploadBean.setVideoTypeId(cursor.getString(videoTypeIdIndex));
            uploadBean.setUploadState(cursor.getString(uploadStateIndex));
            uploadBean.setVideoFormat(cursor.getString(videoSizeFormat));
            uploadBean.setVideoSize(cursor.getString(videoSizeIndex));
            uploadBean.setQiniuPath(cursor.getString(qiniuPathIndex));
            uploadBeenList.add(uploadBean);
        }
        cursor.close();
        closedb(context);
        return uploadBeenList;
    }
    
    /**
     * 根据资源名称查询
     * @ClassName UploadDatabaseTools
     *@author HeChuang
     *@time 2016/12/12 16:10
     */
    public boolean selectById(String resourceName) {
        opendb(context);
        String sql = "select * from upload_tab where resourceName = '" + Base64.encode(resourceName.getBytes())+"'";
        Cursor cursor = sqlitedatabase.rawQuery(sql, null);
        boolean res = cursor.getCount() == 0 ? false : true;
        return res;
    }

//    /**
//     * 根据文件的上传路径得到绑定的id
//     *
//     * @param uploadFile
//     * @return
//     */
//    public String getBindId(File uploadFile) {
//        SQLiteDatabase db = mUploadDB.getReadableDatabase();
//        Cursor cursor = db.rawQuery("select sourceId from upload_video where uploadFilePath=?",
//                new String[]{uploadFile.getAbsolutePath()});
//        if (cursor.moveToFirst()) {
//            return cursor.getString(0);
//        }
//        return null;
//    }
}
