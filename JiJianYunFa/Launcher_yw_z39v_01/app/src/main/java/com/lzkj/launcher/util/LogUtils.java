package com.lzkj.launcher.util;

import android.os.Build;
import android.util.Log;

import com.lzkj.launcher.R;
import com.lzkj.launcher.log.LogBuilder.LogType;

public class LogUtils {

    private static final String TAG = "LogUtils";
    private static final LogTag LOG_FLAG = new LogTag(TAG, true);

    /**
     * log状态帮助类
     *
     * @author chenercai
     */
    public static class LogTag {
        private String tagString;
        private boolean log;

        private LogTag(String tag, boolean log) {
            this.tagString = tag;
            this.log = log;
        }

        public String getTagString() {
            return tagString;
        }
    }

    /**
     * 创建LogTag
     *
     * @param tag Log tag字符串
     * @param log 是否打印此类log标志位
     * @return LogTag对象
     */
    public static final LogTag getLogTag(String tag, boolean log) {
        return new LogTag(tag, log);
    }

    public static final void v(LogTag tag, String method, Object message) {
        if (tag == null) {
            tag = LOG_FLAG;
        }
        if (ConfigSettings.LOG_TAG && tag.log) {
            Log.v(tag.tagString, "Method: " + method + ", " + "Message: " + message);
        }
    }

    public static final void timeConsume(LogTag tag, String method, long millis) {
        if (tag == null) {
            tag = LOG_FLAG;
        }
        if (ConfigSettings.LOG_TAG && tag.log) {
            Log.i(tag.tagString, "Method: " + method + ", " + "time consume: " + millis + " ms");
        }
    }

    public static final void d(LogTag tag, String method, Object message) {
        if (tag == null) {
            tag = LOG_FLAG;
        }
        if (ConfigSettings.LOG_TAG && tag.log) {
            Log.d(tag.tagString, "Method: " + method + ", " + "Message: " + message);
        }
    }

    public static final void i(LogTag tag, String method, Object message) {
        if (tag == null) {
            tag = LOG_FLAG;
        }
        if (ConfigSettings.LOG_TAG && tag.log) {
            Log.i(tag.tagString, "Method: " + method + ", " + "Message: " + message);
        }
    }

    public static final void w(LogTag tag, String method, Object message) {
        if (tag == null) {
            tag = LOG_FLAG;
        }
        if (ConfigSettings.LOG_TAG && tag.log) {
            Log.w(tag.tagString, "Method: " + method + ", " + "Message: " + message);
        }
    }

    public static final void e(LogTag tag, String method, Object message) {
        if (tag == null) {
            tag = LOG_FLAG;
        }
        String trace = "Method: " + method + ", " + "Message: " + message;
        if (ConfigSettings.LOG_TAG && tag.log) {
            Log.e(tag.tagString, trace);
        }
    }

    public static final void e(LogTag tag, String method, Throwable message) {
        if (tag == null) {
            tag = LOG_FLAG;
        }
        String trace = "Method: " + method + ", " + "Message: " + Log.getStackTraceString(message);
        if (ConfigSettings.LOG_TAG && tag.log) {
            Log.e(tag.tagString, trace);
        }
    }

    /**
     * 获取错误日志数据
     *
     * @param logType 错误类型
     * @param ex      错误信息
     * @return
     */
    public static String getStackTraceString(LogType logType, Throwable ex) {
        String msg = logType + "|" //错误类型
                + System.currentTimeMillis() + "|" //当前时间
                + Build.DISPLAY + "|"  // 系统版本
                + StringUtil.getString(R.string.version_name) + "|" //APK版本
//				+ ConfigSettings.getDid() + Constant.SPLIT //did
                + Log.getStackTraceString(ex); // 错误信息;
        return msg;
    }
}
