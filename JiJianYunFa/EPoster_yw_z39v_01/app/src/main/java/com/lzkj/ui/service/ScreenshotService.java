package com.lzkj.ui.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;

import com.lzkj.aidl.ScreentshotAIDL;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.ScreenshotUtil;

/**
 * 截图的服务
 *
 * @author changkai
 */
public class ScreenshotService extends Service {
    private static final LogTag TAG = LogUtils.getLogTag(ScreenshotService.class.getSimpleName(), true);

    /**
     * 执行截图的aidl服务端
     */
    private ScreentshotAIDL.Stub screentshotAIDL = new ScreentshotAIDL.Stub() {
        @Override
        public void executeShot(int shotNumber, int intervalTime)
                throws RemoteException {
            LogUtils.d(TAG, "ScreenshotService", "executeShot");
            ScreenshotUtil.newInstance().excSreenshot(shotNumber, intervalTime);
            stopSelf();
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return screentshotAIDL;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        screentshotAIDL = null;
        super.onDestroy();
    }
}
