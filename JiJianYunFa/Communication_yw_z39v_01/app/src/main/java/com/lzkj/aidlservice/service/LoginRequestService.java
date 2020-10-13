package com.lzkj.aidlservice.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.IBinder.DeathRecipient;
import android.os.RemoteException;
import android.util.Log;

import com.lzkj.aidl.LoginClientAIDL;
import com.lzkj.aidl.LoginServiceAIDL;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.manager.ServiceManager;
import com.lzkj.aidlservice.manager.ValidationDeviceManager;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpUtil;

/**
 * 登录请求服务 接受登录界面发送过来的用户名,密码，终端名
 *
 * @author changkai
 */
public class LoginRequestService extends Service implements IRequestCallback {

    private static final String TAG = "LoginRequestService";

    /**
     * 客户端aidl发送请求给loginUI界面
     */
    private LoginServiceAIDL loginClientAIDL;
    /**
     * 发送请求的服务连接
     **/
    private ServiceConnection serviceConnectionLoginUI = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            Log.d(TAG, "Connect login result service failed.");
            loginClientAIDL = null;
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder obj) {
            Log.d(TAG, "Connect login result service success.");
            loginClientAIDL = LoginServiceAIDL.Stub.asInterface(obj);
        }
    };


    //断开连接的监听
    private DeathRecipient mDeathRecipient = new DeathRecipient() {
        @Override
        public void binderDied() {

        }
    };
    /**
     * 获取aidl客户端发送过来的请求
     */
    private LoginClientAIDL.Stub loginServiceAidl = new LoginClientAIDL.Stub() {
        @Override
        public void onLogin(int type, String deviceName, String userName, String password, String macAddress)
                throws RemoteException {
        }

        @Override
        public void onCancel() throws RemoteException {
            //取消请求
            Log.d(TAG, "onCancel.");
            HttpUtil.newInstance().cancleRequestToTag("LoginClientAIDL");
        }

        /***
         * 恢复注册调用的方法
         * @param type
         * 			请求的消息类型
         * @param macAddress
         * 			终端mac地址
         */
        @Override
        public void onCommunication(int type, String macAddress) throws RemoteException {
            Log.d(TAG, "type: " + type + " ,macAddress: " + macAddress);
            ValidationDeviceManager.get().validationDevice(macAddress, LoginRequestService.this);
        }
    };

    @Override
    public void onCreate() {
        Intent intent = new Intent();
        intent.setComponent(new ComponentName(Constant.LAUNCHER_PKG, Constant.LOGINRESULTSERVICE_CLS));
        bindService(intent, serviceConnectionLoginUI, BIND_AUTO_CREATE);
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return loginServiceAidl;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"onStartCommand.");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        Log.d(TAG,"onDestroy.");
        unbindService(serviceConnectionLoginUI);
        super.onDestroy();
    }

    @Override
    public void onSuccess(String result, String tag, String requestUrL) {
        Log.d(TAG,"result: " + result + " ,tag: " + tag + " ,requestUrl: " + requestUrL);
        try {
            //启动所有服务
            ServiceManager.getInstance().startAllService();
            loginClientAIDL.onSuccess(result);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaile(String errMsg, String tag, String requestUrL) {
        Log.d(TAG,"result: " + errMsg + " ,tag: " + tag + " ,requestUrl: " + requestUrL);
        try {
            loginClientAIDL.onFail(errMsg);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
