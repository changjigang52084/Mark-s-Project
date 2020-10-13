package com.xunixianshi.vrshow.my.fragment.database;

import android.provider.BaseColumns;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-09
 * Time: 18:48
 * FIXME
 */
public final class FeedReaderContract {

    public FeedReaderContract(){}

    // 为一个表定义了表明和列名
    public static abstract class FeedEntry implements BaseColumns {

        public static final String TABLE_NAME = "upload_video";
        public static final String COLUMN_VIDEO_ID = "video_id";
        public static final String COLUMN_VIDEO_TITLE = "video_title";
        public static final String COLUMN_VIDEO_PATH = "video_path";
        public static final String COLUMN_VIDEO_SIZE = "video_size";
        public static final String COLUMN_VIDEO_IMAGE = "video_image";
        public static final String COLUMN_VIDEO_PROGRESS = "video_progress";
        public static final String COLUMN_VIDEO_HAS_UPLOAD = "video_has_upload";
        public static final String COLUMN_VIDEO_PERCENT = "video_percent";
    }
}  