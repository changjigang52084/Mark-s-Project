package com.unccr.zclh.dsdps.play;

import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.StringUtil;

import java.io.File;

/**
 * 播放u盘节目管理器
 *
 * @author jgchang
 * @date 2020-03-27 下午2:42:00
 */
public class UDiskPlayManager {

    private static final String TAG = "UDiskPlayManager";
    private static volatile UDiskPlayManager ins;
    private final int delayMillis = 5 * 1000;
    private Handler mHandler = DsdpsApp.getDsdpsApp().mHandler;

    private UDiskPlayManager() {
    }

    public static UDiskPlayManager getInstance() {
        if (ins == null) {
            synchronized (UDiskPlayManager.class) {
                if (ins == null) {
                    ins = new UDiskPlayManager();
                }
            }
        }
        return ins;
    }

    /**
     * 播放U盘节目
     *
     * @param
     * @return
     */
    public void playUDiskFile() {
        File file = new File(FileStore.getInstance().getLayoutFolderPath());
        if (file == null || !file.exists() || !file.isDirectory()) {
            Log.w(TAG, "playUDiskFile U disk program file format error!");
            ConfigSettings.UDISK_MODE = false;
            FileStore.UDISK_ROOT_FOLDER = null;
            return;
        }
        File[] fileLFiles = file.listFiles();
        if (fileLFiles == null || fileLFiles.length == 0) {
            Log.w(TAG, "playUDiskFile No program file in U disk!");
            return;
        }
        File uDiskRootFolder = new File(FileStore.UDISK_ROOT_FOLDER + File.separator + FileStore.ROOT_FOLDER);
        Log.d(TAG, "playUDiskFile U disk folder:!" + uDiskRootFolder.getAbsolutePath());
        if (!uDiskRootFolder.exists()) {
            return;
        }
        Toast.makeText(DsdpsApp.getDsdpsApp(), StringUtil.getString(R.string.tip_udisk_copying), Toast.LENGTH_SHORT).show();
        //sendProgramErrorMsg(true, StringUtil.getString(R.string.tip_udisk_copying));
        FileStore.getInstance().ergodicFolderAndCopy(uDiskRootFolder,"playUDiskFile");
    }

    private void sendProgramErrorMsg(boolean isShow, String tipMsg) {
        Log.d(TAG,"sendProgramErrorMsg isShow: " + isShow + " ,tipMsg: " + tipMsg);
        if (StringUtil.isNullStr(tipMsg)) {
            return;
        }
        Intent tipMsgIntent = new Intent(PlayActivity.UPDATE_MSG_TIP_MSG_ACTION);
        if (isShow) {
            tipMsgIntent.putExtra(PlayActivity.TIP_MSG_KEY, tipMsg);
        }
        tipMsgIntent.putExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, isShow);
        LocalBroadcastManager.getInstance(DsdpsApp.getDsdpsApp().getApplicationContext()).sendBroadcast(tipMsgIntent);
    }

    /**
     * 停播U盘节目
     *
     * @param
     * @return
     */
    public void stopUDiskFile() {
        //Toast.makeText(EPosterApp.getApplication(), "停止播放本地节目...", Toast.LENGTH_LONG).show();
        ProgramPlayManager.getInstance().playProgramList("stopUDiskFile");
    }
}
