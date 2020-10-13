package com.lzkj.launcher.service;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.lzkj.aidl.LoginClientAIDL;
import com.lzkj.launcher.app.LauncherApp;
import com.lzkj.launcher.util.ConfigSettings;
import com.lzkj.launcher.util.Constant;

/**
 * 使用aidl登录的管理类
 *
 * @author changkai
 */
public class LoginRequestManager {

    private static final String TAG = "LoginRequest";

    /**
     * 连接服务端的对象，用来和服务端通信的类
     */
    private LoginClientAIDL loginClientAIDL;

    /**
     * 请求类型  终端机注册请求
     */
    private static final int REGISTERED_REQUEST = 0x020005;
    /**
     * 请求类型  终端机恢复注册信息请求
     */
    private static final int REGISTERED_RECOVERY = 0x020003;

    private boolean isBind = false;
    /**
     * 绑定的服务连接
     */
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            //取消AIDL绑定
            Log.d(TAG, "LoginRequestManager serviceConnection connect login request service failed.");
            loginClientAIDL = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //绑定aidl
            Log.d(TAG, "LoginRequestManager serviceConnection connect login request service success.");
            loginClientAIDL = LoginClientAIDL.Stub.asInterface(service);
            requestCommunication();
        }
    };

    private static LoginRequestManager loginRequestManger = null;

    private LoginRequestManager() {
        bindLoginService();
    }

    public static LoginRequestManager newInstance() {
        if (null == loginRequestManger) {
            init();
        }
        return loginRequestManger;
    }

    private static synchronized void init() {
        if (null == loginRequestManger) {
            loginRequestManger = new LoginRequestManager();
        }
    }

    /**
     * 绑定服务
     */
    private void bindLoginService() {
        Log.d(TAG,"LoginRequestManager bindLoginService.");
        ComponentName component = new ComponentName(Constant.COMMUNICATION_PKG, Constant.COMMUNICATION_LOGIN_CLS);
        Intent intent = new Intent();
        intent.setComponent(component);
        isBind = LauncherApp.getApplication().bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
    }

    /**
     * 发送通讯请求恢复注册
     */
    public void requestCommunication() {
        try {
            Log.d(TAG, "LoginRequestManager requestCommunication.");
            if (null == loginClientAIDL) {
                Log.d(TAG, "LoginRequestManager requestCommunication loginClientAIDL is null.");
                return;
            }
            loginClientAIDL.onCommunication(REGISTERED_RECOVERY, ConfigSettings.MAC_ADDRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 登录请求
     *
     * @param deviceName 设备名称
     * @param userName   登录名
     * @param password   密码
     */
    public void login(String deviceName, String userName, String password) {
        try {
            Log.d(TAG, "LoginRequestManager login deviceName: " + deviceName + " ,userName: " + userName + " ,password: " + password);
            loginClientAIDL.onLogin(REGISTERED_REQUEST, deviceName,
                    userName, password, ConfigSettings.MAC_ADDRESS);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消登录
     */
    public void cancelLogin() {
        try {
            Log.d(TAG, "LoginRequestManager cancelLogin.");
            if (null == loginClientAIDL) {
                Log.d(TAG, "LoginRequestManager cancelLogin loginClientAIDL is null.");
                return;
            }
            loginClientAIDL.onCancel();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消绑定
     */
    public void unbindLoginRequestService() {
        //解除绑定
        if (isBind) {
            Log.d(TAG, "LoginRequestManager unbindLoginRequestService.");
            try {
                LauncherApp.getApplication().unbindService(serviceConnection);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        loginRequestManger = null;
    }
}
