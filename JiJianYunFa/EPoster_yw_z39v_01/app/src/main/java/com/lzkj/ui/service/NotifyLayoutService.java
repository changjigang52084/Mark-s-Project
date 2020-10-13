package com.lzkj.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;

import com.lzkj.aidl.NotifyLayoutAIDL;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/**
 * 接收更新节目的服务
 *
 * @author changkai
 */
public class NotifyLayoutService extends Service {
    private static final LogTag TAG = LogUtils.getLogTag(NotifyLayoutService.class.getSimpleName(), true);
    private Handler mHandler = new Handler();
    /**
     * 更新节目的AIDL
     */
    private NotifyLayoutAIDL.Stub notifyLayoutAIDL = new NotifyLayoutAIDL.Stub() {
        @Override
        public void notifyLayout() throws RemoteException {
            LogUtils.i(TAG, "notifyLayout", "update program list");
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    ProgramPlayManager.getInstance().updateProgramList();
                }
            });
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return notifyLayoutAIDL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
