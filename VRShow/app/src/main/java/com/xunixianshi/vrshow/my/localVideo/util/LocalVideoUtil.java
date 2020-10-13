package com.xunixianshi.vrshow.my.localVideo.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.provider.MediaStore;


import com.xunixianshi.vrshow.my.localVideo.entity.VideoBean;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * 获取本地手机视频的工具类
 *
 * @author lzb
 * @Description TODO
 * @date 2016-1-20
 * @Copyright: Copyright (c) 2016 Shenzhen Tentinet Technology Co., Ltd. Inc.
 * All rights reserved.
 */
public class LocalVideoUtil {

    /**
     * 获取视频缩略图
     * 2.2以前的版本不支持
     *
     * @param videoPath 视频文件的位置
     * @param width
     * @param height
     * @param kind      可以为MINI_KIND或 MICRO_KIND最终和分辨率有关
     * @return
     * @version 1.0
     * @createTime 2016-1-20,上午9:29:50
     * @updateTime 2016-1-20,上午9:29:50
     * @createAuthor lzb
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    @TargetApi(Build.VERSION_CODES.FROYO)
    @SuppressLint("NewApi")
    public static Bitmap getVideoThumbnail(String videoPath, int width, int height, int kind) {
        Bitmap bitmap = null;
        //获取视频文件的缩略图，第一个参数为视频文件的位置 第二个参数可以为MINI_KIND或 MICRO_KIND最终和分辨率有关
        bitmap = ThumbnailUtils.createVideoThumbnail(videoPath, kind);
        //直接对Bitmap进行缩略操作，最后一个参数定义为OPTIONS_RECYCLE_INPUT ，来回收资源
        bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
        return bitmap;
    }
    /**
     * 从媒体库中查询 获取视频缩略图
     * 新视频增加后需要SDCard重新扫描才能给新增加的文件添加缩略图，灵活性差，而且不是很稳定，适合简单应用
     *
     * @param context
     * @param contentResolver
     * @return 返回视频位图集合
     * @version 1.0
     * @createTime 2016-1-20,上午9:37:53
     * @updateTime 2016-1-20,上午9:37:53
     * @createAuthor lzb
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    @SuppressLint("NewApi")
    public static ArrayList<Bitmap> getVideoThumbnail(Context context, ContentResolver contentResolver) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID,};
        //查询多媒体数据库
        Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        int _id = 0;
        ArrayList<Bitmap> bitmaps = new ArrayList<Bitmap>();
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            do {
                //视频id
                _id = cursor.getInt(_idColumn);
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, _id, MediaStore.Images.Thumbnails.MINI_KIND, options);
                bitmaps.add(bitmap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bitmaps;
    }

    /**
     * 从媒体库中查询 获取视频缩略图  新视频增加后需要SDCard重新扫描才能给新增加的文件添加缩略图，灵活性差，而且不是很稳定，适合简单应用
     *
     * @param context
     * @param contentResolver
     * @return 视频集合 key : 视频路径  value ：视频压缩后的位图
     * @version 1.0
     * @createTime 2016-1-20,上午9:37:53
     * @updateTime 2016-1-20,上午9:37:53
     * @createAuthor lzb
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    @SuppressLint("NewApi")
    public static HashMap<String, Bitmap> getVideoMapThumbnail(Context context, ContentResolver contentResolver) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID,};
//         String whereClause = MediaStore.Video.Media.DATA + " = '" + Videopath + "'";、
        //查询多媒体数据库
        Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        int _id = 0;
        String videoPath = "";
        HashMap<String, Bitmap> bitmaps = new HashMap<String, Bitmap>();
        if (cursor == null || cursor.getCount() == 0) {
            return null;
        }
        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            do {
                //视频id
                _id = cursor.getInt(_idColumn);
                //视频路径
                videoPath = cursor.getString(_dataColumn);
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, _id, MediaStore.Images.Thumbnails.MINI_KIND, options);
                bitmaps.put(videoPath, bitmap);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bitmaps;
    }

    /**
     * 从媒体库中查询 获取视频缩略图  新视频增加后需要SDCard重新扫描才能给新增加的文件添加缩略图，灵活性差，而且不是很稳定，适合简单应用
     *
     * @param context
     * @param contentResolver
     * @return 视频集合
     * @version 1.0
     * @createTime 2016-1-20,上午9:37:53
     * @updateTime 2016-1-20,上午9:37:53
     * @createAuthor lzb
     * @updateAuthor
     * @updateInfo (此处输入修改内容, 若无修改可不写.)
     */
    @SuppressLint("NewApi")
    public static ArrayList<VideoBean> getVideoBeansThumbnail(Context context, ContentResolver contentResolver) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID,MediaStore.Video.Media.TITLE};
//         String whereClause = MediaStore.Video.Media.DATA + " = '" + Videopath + "'";、
        //查询多媒体数据库
        Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        int _id = 0;
        String videoPath = "";
        String videoTitle = "";
        ArrayList<VideoBean> bitmaps = new ArrayList<VideoBean>();
        if (cursor == null || cursor.getCount() == 0) {
            return bitmaps;
        }
        if (cursor.moveToFirst()) {
            int _idColumn = cursor.getColumnIndex(MediaStore.Video.Media._ID);
            int _dataColumn = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
            int _titleColumn = cursor.getColumnIndex(MediaStore.Video.Media.TITLE);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inDither = false;
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            do {
                //视频id
                _id = cursor.getInt(_idColumn);
                //视频路径
                videoPath = cursor.getString(_dataColumn);
                // 視頻標題
                videoTitle = cursor.getString(_titleColumn);
                Bitmap bitmap = MediaStore.Video.Thumbnails.getThumbnail(contentResolver, _id, MediaStore.Images.Thumbnails.MINI_KIND, options);
                VideoBean bean = new VideoBean();
                bean.setVideoId(_id + "");
                bean.setVideoPath(videoPath);
                bean.setVideoTitle(videoTitle);
                bean.setVideoBitmap(bitmap);
                bitmaps.add(bean);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return bitmaps;
    }

    @SuppressLint("NewApi")
    public static int getLocalVideoSize(Context context, ContentResolver contentResolver) {
        ContentResolver testcr = context.getContentResolver();
        String[] projection = {MediaStore.Video.Media.DATA, MediaStore.Video.Media._ID,MediaStore.Video.Media.TITLE};
        //查询多媒体数据库
        Cursor cursor = testcr.query(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, projection, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        if (cursor == null ) {
            return 0;
        }
        return cursor.getCount();
    }


    /**
     * @Title: getLocalVideoFiles
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     * @param @return 设定文件
     * @return ArrayList<DownloadItem> 返回类型
     */
    public static ArrayList<VideoBean> getLocalVideoFiles(Context context) {
        ArrayList<VideoBean> videoBeanArrayList = new ArrayList<>();
        // Media.DURATION 时间长度
        String[] VIDEO_COLUMN = {
                MediaStore.Video.Media.DISPLAY_NAME, MediaStore.Video.Media.DATA,
                MediaStore.Video.Media.SIZE, MediaStore.Audio.Media.DURATION };
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(
                MediaStore.Video.Media.EXTERNAL_CONTENT_URI, VIDEO_COLUMN,
                null, null, null);

        if (cursor == null || cursor.getCount() == 0) {
            return videoBeanArrayList;
        }
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String name = cursor.getString(cursor
                            .getColumnIndex(VIDEO_COLUMN[0]));
                    String path = cursor.getString(cursor
                            .getColumnIndex(VIDEO_COLUMN[1]));
                    String size = cursor.getString(cursor
                            .getColumnIndex(VIDEO_COLUMN[2]));
                    String duration = cursor.getString(cursor
                            .getColumnIndex(VIDEO_COLUMN[3]));
                    VideoBean videoBean =  new VideoBean();
                    videoBean.setVideoTitle(name);
                    videoBean.setVideoPath(path);
                    videoBean.setVideoSize(Integer.valueOf(size));
                    videoBean.setTime(duration);
                    videoBeanArrayList.add(videoBean);
                }while (cursor.moveToNext());
            }
        }
        return videoBeanArrayList;
    }

}
