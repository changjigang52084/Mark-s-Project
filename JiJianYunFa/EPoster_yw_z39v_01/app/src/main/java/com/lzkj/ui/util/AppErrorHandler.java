package com.lzkj.ui.util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.R;
import com.lzkj.ui.log.LogBuilder.LogType;
import com.lzkj.ui.log.LogManager;
import com.lzkj.ui.util.LogUtils.LogTag;

/**
 * 应用崩溃捕获错误日志
 *
 * @author lyhuang
 * @date 2016-1-26 上午9:35:53
 */
public class AppErrorHandler implements Thread.UncaughtExceptionHandler {
    private static final LogTag TAG = LogUtils.getLogTag(AppErrorHandler.class.getSimpleName(), true);

    private Context cxt;
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        handlerException(ex);
        mDefaultHandler.uncaughtException(thread, ex);

    }

    public void init(Context cxt) {
        this.cxt = cxt;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
    }

    private void handlerException(Throwable ex) {
        //程序发生异常闪退的时候会调用这里的方法
        String errMsg = LogManager.get().
                setErrorLog(StringUtil.getString(R.string.app_name),
                        LogUtils.getStackTraceString(LogType.PLAY_LOG, ex));
        LogManager.get().writeErrorLog(errMsg);
        writeErroIndex(errMsg);
        if (ConfigSettings.getDid() != null) {
            Intent intent = new Intent(cxt, PlayActivity.class);
            PendingIntent restartIntent = PendingIntent.getActivity(cxt, 0, intent,
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            AlarmManager mgr = (AlarmManager) cxt.getSystemService(Context.ALARM_SERVICE);
            mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 5000, restartIntent); // 5秒钟后重启应用

        }
    }

    /**
     * 记录错误次数,2个小时内连续发生超过5次错误则删除节目和素材等待重新,同步节目
     */
    private void writeErroIndex(String errMsg) {
        LogUtils.d(TAG, "writeErrorIndex", "errMsg = " + errMsg);
        long currentDate = System.currentTimeMillis();
        //1.获取上次闪退的时间
        long appErorDate = SharedUtil.newInstance().getLong(SharedUtil.KEY_APP_ERROR_DATE);
        int appErorNum = SharedUtil.newInstance().getInt(SharedUtil.KEY_APP_ERROR_INDEX);
        if (appErorDate != -1 && appErorNum != -1) {
            long intervalTime = currentDate - appErorDate;//间隔时间
            if (intervalTime < ConfigSettings.MAX_APP_ERROR_INTERVAL_TIME) {//判断上次发送的闪退是否在2个小时内，如果是则判断appErorNum是否大于
                if (appErorNum >= ConfigSettings.MAX_APP_ERROR_COUNT) {//2个小时内连续闪退超过5次则删除本地所有节目
                    LogUtils.d(TAG, "handlerException", "appErorNum >= MAX_APP_ERROR_COUNT" + ", appErorNum : " + appErorNum);
                    SharedUtil.newInstance().setInt(SharedUtil.KEY_APP_ERROR_INDEX, 1);
                    //清除本机所有素材 回归到原始状态
//    		    	FileStore.delete(FileStore.getLayoutFolderPath());
//    		    	FileStore.delete(FileStore.getImageFolderPath());
//    		    	FileStore.delete(FileStore.getVideoFolderPath());
//    		    	FileStore.delete(FileStore.getScaledImageFolderPath());
//    		    	requestSyncPrm();
                    //上传一条log文件到七牛，提示当前终端已经连续闪退多次 "Eposter_Uncaught_DID_时间.log"
                    String currentKey = SharedUtil.newInstance().getString(SharedUtil.KEY_CURRENT_PLAY_PRM);
                    LogUtils.d(TAG, "writeErrorIndex", "currentKey : " + currentKey);
                    SharedUtil.newInstance().setString(SharedUtil.KEY_ERROR_PRM, currentKey);
                    FileStore.getInstance().createUncaughtLog(errMsg + "_error prm key_" + currentKey);
                } else {
                    appErorNum++;
                    LogUtils.d(TAG, "handlerException", "appErorNum : " + appErorNum);
                    SharedUtil.newInstance().setInt(SharedUtil.KEY_APP_ERROR_INDEX, appErorNum);
                }
            } else {//再次发生错误的时间在 2小时前则 把错误次数设置为1
                SharedUtil.newInstance().setInt(SharedUtil.KEY_APP_ERROR_INDEX, 1);
            }
        } else {
            SharedUtil.newInstance().setInt(SharedUtil.KEY_APP_ERROR_INDEX, 1);
        }
        LogUtils.d(TAG, "handlerException", "appErorNum : " + appErorNum + ", currentDate：" + currentDate);
        SharedUtil.newInstance().setLong(SharedUtil.KEY_APP_ERROR_DATE, currentDate);
    }

    /**
     * 主动请求发送同步节目
     */
    private void requestSyncPrm() {
        Intent intent = new Intent(Constants.REQUEST_SYNC_PRM_RECEIVER_ACTION);
        cxt.getApplicationContext().sendBroadcast(intent);
    }
}
