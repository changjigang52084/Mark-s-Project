package com.lzkj.launcher.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.util.Log;

import com.lzkj.launcher.app.LauncherApp;
import com.lzkj.launcher.util.StorageList;
import com.lzkj.launcher.util.StringUtil;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年9月26日 上午11:33:53
 * @parameter SD卡挂在和移除的监听
 */
public class SDCardMountReceiver extends BroadcastReceiver {

    private static final String TAG = "SDCardMount";

    private static final String USB = "usb";

    private static final String ACTION = "com.lzkj.UPDATE_SDCARD_ACTION";
    private static final String IS_MOUNT = "ismount";
    private static final String SDCRAD_PATH = "sdcradPath";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            String action = intent.getAction();
            String path = intent.getData().getPath();
            Log.d(TAG, "SDCardMountReceiver onReceive action: " + intent.getAction() + " ,path: " + path);
            if (StringUtil.isNullStr(path)) {
                return;
            }
            if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {//挂着sd卡
                if (!path.contains(USB)) {
                    StorageList storageList = new StorageList(context);
                    //发送更新广播
                    String sdPath = storageList.getSDPath();
                    if (null != sdPath) {
                        sendUpdateSDPathReceiver(sdPath, true);
                    } else {
                        Log.d(TAG, "SDCardMountReceiver onReceive sdPath is null.");
                    }
                }
            } else if (action.equals(Intent.ACTION_MEDIA_EJECT) ||
                    action.equals(Intent.ACTION_MEDIA_REMOVED)) {//sd卡被移除了
                if (!path.contains(USB)) {
                    //发送更新广播
                    String sdcardPath = Environment.getExternalStorageDirectory().getAbsolutePath();
                    sendUpdateSDPathReceiver(sdcardPath, false);
                }
            }
        }
    }

    private void sendUpdateSDPathReceiver(String path, boolean isMount) {
        Log.d(TAG, "SDCardMountReceiver sendUpdateSDPathReceiver path: " + path + " ,isMount: " + isMount);
        Intent intent = new Intent(ACTION);
        intent.putExtra(IS_MOUNT, isMount);
        intent.putExtra(SDCRAD_PATH, path);
        LauncherApp.getApplication().sendBroadcast(intent);
    }
}
