package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.play.UDiskPlayManager;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.FileStore;

/**
 * 监听USB插拔广播
 *
 * @author jgchang
 * @date 2020-03-27 下午2:37:00
 */
public class UDiskCheckReceiver extends BroadcastReceiver {

    private static final String TAG = "UDiskCheckReceiver";
    private static final String EMULATED = "emulated";
    private static final String STORAGE = "storage";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        int deviceId = ConfigSettings.getDeviceId();
        Log.d(TAG,"deviceId: " + deviceId);
        if(deviceId == -1){
            return;
        }
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            String path = intent.getData().getPath();
            if (!ConfigSettings.UDISK_MODE && !path.contains(EMULATED)) {
                Log.d(TAG,"onReceive Insert U disk.");
                FileStore.UDISK_ROOT_FOLDER = path;
                if (FileStore.UDISK_ROOT_FOLDER != null && !"".equals(FileStore.UDISK_ROOT_FOLDER)) {
                    ConfigSettings.UDISK_MODE = true;
                    Log.d(TAG,"onReceive u disk root path: " + FileStore.UDISK_ROOT_FOLDER);
                    UDiskPlayManager.getInstance().playUDiskFile();
                }
            }
        } else if (action.equals(Intent.ACTION_MEDIA_REMOVED) || action.equals(Intent.ACTION_MEDIA_EJECT)) {
            String path = intent.getData().getPath();
            if (ConfigSettings.UDISK_MODE && path.contains(STORAGE)) {
                Log.d(TAG,"onReceive Pull out U disk.");
                ConfigSettings.UDISK_MODE = false;
                FileStore.UDISK_ROOT_FOLDER = null;
                UDiskPlayManager.getInstance().stopUDiskFile();
                sendProgramErrorMsg(false, null);
            }
        }
    }

    private void sendProgramErrorMsg(boolean isShow, String tipMsg) {
        Intent tipMsgIntent = new Intent(PlayActivity.UPDATE_MSG_TIP_MSG_ACTION);
        if (isShow) {
            tipMsgIntent.putExtra(PlayActivity.TIP_MSG_KEY, tipMsg);
        }
        tipMsgIntent.putExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, isShow);
        LocalBroadcastManager.getInstance(DsdpsApp
                .getDsdpsApp()
                .getApplicationContext()).sendBroadcast(tipMsgIntent);
    }
}