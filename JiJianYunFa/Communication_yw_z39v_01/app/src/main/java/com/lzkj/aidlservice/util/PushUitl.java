package com.lzkj.aidlservice.util;

import android.content.Intent;

import com.igexin.sdk.PushManager;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.service.DemoPushService;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

import cn.jpush.android.api.JPushInterface;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年3月1日 下午7:17:58
 * @parameter 推送工具类
 */
public class PushUitl {
    private static final LogTag LOG_TAG = LogUtils.getLogTag(PushUitl.class.getSimpleName(), true);

    /**
     * 初始化个推,并且保存client_id
     */
    public static void initGeTuiPush() {
        if (!StringUtil.isNullStr(ConfigSettings.getClientId())) {
            //  ui apk 更新推送id的action
            Intent updateLocationIntent = new Intent(Constant.LAUNCHER_UPDATE_PUSH_CLIENT_ACTION);
            updateLocationIntent.putExtra(SharedUtil.CLIENT_ID, ConfigSettings.getClientId());
            CommunicationApp.get().sendBroadcast(updateLocationIntent);
        }
        // SDK初始化，第三方程序启动时，都要进行SDK初始化工作
        LogUtils.d(LOG_TAG, "PushUitl", "initGeTuiPush sdk...");
        PushManager.getInstance().initialize(CommunicationApp.get().getApplicationContext(), DemoPushService.class);
        getClientId(Constant.IGETUI);
    }

    /**
     * 初始化极光推送,保存RegistrationID
     */
    public static void initJPush() {
        if (!StringUtil.isNullStr(ConfigSettings.getRegistrationId())) {
            Intent updateLocationIntent = new Intent(Constant.LAUNCHER_UPDATE_PUSH_CLIENT_ACTION);
            updateLocationIntent.putExtra(SharedUtil.REGISTRATION_ID, ConfigSettings.getRegistrationId());
            CommunicationApp.get().sendBroadcast(updateLocationIntent);
        }
        LogUtils.d(LOG_TAG, "PushUitl", "initJPush sdk...");
        JPushInterface.setDebugMode(false); // 设置开启日志,发布时请关闭日志
        JPushInterface.init(CommunicationApp.get().getApplicationContext()); // 初始化 JPush
        JPushInterface.resumePush(CommunicationApp.get().getApplicationContext());
        boolean state = JPushInterface.getConnectionState(CommunicationApp.get().getApplicationContext());
        LogUtils.d(LOG_TAG, "PushUitl", "getConnectionState: " + state);
        getClientId(Constant.JPUSH);
    }

    /**
     * 获取终端注册的推送id
     *
     * @param pushType
     */
    private static void getClientId(final int pushType) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    int push = pushType;
                    String clientId = checkPushType(push);
                    while (null == clientId) {
                        Thread.sleep(1000);
                        if (null != checkPushType(push)) {
                            break;
                        }
                    }
                    notifyUpdatePushClient(push, clientId);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    /**
     * 发送广播，通知Luancher推送id
     *
     * @param pushType 推送类型
     * @param clientId 推送id
     * @return
     */
    private static void notifyUpdatePushClient(int pushType, String clientId) {
        Intent updateLocationIntent = new Intent(Constant.LAUNCHER_UPDATE_PUSH_CLIENT_ACTION);
        if (pushType == Constant.JPUSH) {
            SharedUtil.newInstance().setString(SharedUtil.REGISTRATION_ID, clientId);
            updateLocationIntent.putExtra(SharedUtil.REGISTRATION_ID, clientId);
        } else {
            SharedUtil.newInstance().setString(SharedUtil.CLIENT_ID, clientId);
            updateLocationIntent.putExtra(SharedUtil.CLIENT_ID, clientId);
        }
        CommunicationApp.get().sendBroadcast(updateLocationIntent);
    }

    /**
     * 判断是用了那个推送 个推或者是极光
     *
     * @param pushType 1表示极光,2表示个推
     * @return 返回Getui返回client_id, JPush返回RegistrationID
     */
    private static String checkPushType(int pushType) {
        String clientId = null;
        if (pushType == Constant.JPUSH) {
            // 获取极光推送ID
            clientId = JPushInterface.getRegistrationID(CommunicationApp.get().getApplicationContext());
            LogUtils.d(LOG_TAG, "checkPushType", "getRegistrationId: " + clientId);
        } else {
            // 获取个推ID
            clientId = PushManager.getInstance().getClientid(CommunicationApp.get().getApplicationContext());
            LogUtils.d(LOG_TAG, "checkPushType", "getClientId: " + clientId);
        }
        return clientId;
    }
}
