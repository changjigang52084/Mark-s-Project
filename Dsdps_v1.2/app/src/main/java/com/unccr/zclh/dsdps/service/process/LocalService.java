package com.unccr.zclh.dsdps.service.process;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import androidx.annotation.Nullable;

import com.unccr.zclh.dsdps.IProcessService;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.play.PlayActivity;

public class LocalService extends Service {
    private static final String TAG = "LocalService";
    private static final int DELAY_MILLIS = 1 * 60 * 1000;

    private LocalBinder mLocalBinder;
    private LocalServiceConnection mLocalServiceConn;

    @Override
    public void onCreate() {
        super.onCreate();
        mLocalBinder = new LocalBinder();

        if (mLocalServiceConn == null) {
            mLocalServiceConn = new LocalServiceConnection();
        }

        Log.i(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.i(TAG, TAG + " onStartCommand");
        //  绑定远程服务
        bindService(new Intent(this, RemoteService.class), mLocalServiceConn, Context.BIND_IMPORTANT);

        return START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return mLocalBinder;
    }

    /**
     * 通过AIDL实现进程间通信
     */
    class LocalBinder extends IProcessService.Stub {
        @Override
        public String getServiceName() throws RemoteException {
            return "LocalService";
        }
    }

    /**
     * 连接远程服务
     */
    class LocalServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            try {
                // 与远程服务通信
                IProcessService process = IProcessService.Stub.asInterface(service);
                Log.i(TAG, "连接" + process.getServiceName() + "服务成功");
                if (DsdpsApp.getDsdpsApp().getPlayActivity() == null) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(LocalService.this.getBaseContext(), PlayActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getApplication().startActivity(intent);
                        }
                    }, DELAY_MILLIS);
                }
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            // RemoteException连接过程出现的异常，才会回调,unbind不会回调
            // 监测，远程服务已经死掉，则重启远程服务
            Log.w(TAG, "远程服务挂掉了,远程服务被杀死");

            // 启动远程服务
            startService(new Intent(LocalService.this, RemoteService.class));

            // 绑定远程服务
            bindService(new Intent(LocalService.this, RemoteService.class), mLocalServiceConn, Context.BIND_IMPORTANT);
        }
    }
}
