package com.lzkj.launcher.util;

import android.content.Intent;

import com.lzkj.baize_android.utils.AppUtils;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.launcher.app.LauncherApp;

/**
 * @author longyihuang
 * @ClassName: TestAppManager
 * @Description: TODO
 * @date 2016年6月16日 下午4:05:44
 */
public class TestAppManager {

    private static TestAppManager instance = null;

    private TestAppManager() {
    }

    public static TestAppManager getInstance() {
        if (null == instance) {
            instance = new TestAppManager();
        }
        return instance;

    }

    public void UploadTestLog() {
        if (AppUtils.isInstallApp(LauncherApp.getApplication(), Constant.DEBUG_PKG)) {
            Intent intent = new Intent();
            intent.setAction(Constant.TEST_DEVICE_ACTION);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            LauncherApp.getApplication().startService(intent);
            LogUtils.d(LogUtils.getStackTraceElement(), "Test device status and upload test report");
        } else {
            LogUtils.d(LogUtils.getStackTraceElement(), "Debug app not uninstall");
        }
    }
}
