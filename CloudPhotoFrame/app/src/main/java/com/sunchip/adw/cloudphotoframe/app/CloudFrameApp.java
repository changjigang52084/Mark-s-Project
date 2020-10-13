package com.sunchip.adw.cloudphotoframe.app;

import android.app.Application;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.GetHolderEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.HttpErrorEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.TcpClientEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpErrarCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.receiver.ScreenStatusReceiver;
import com.sunchip.adw.cloudphotoframe.service.SocketService;
import com.sunchip.adw.cloudphotoframe.util.InitializationUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;

import cn.finalteam.okhttpfinal.OkHttpFinal;
import cn.finalteam.okhttpfinal.OkHttpFinalConfiguration;
import cn.jpush.android.api.JPushInterface;


import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.isAllow_Binding;

public class CloudFrameApp extends Application {

    //内存管理
    private RefWatcher refWatcher;

    static CloudFrameApp cloudFrameApp;

    public static String regId;

    //判断是否在时钟界面 默认不在
    public static boolean IsClock = false;

    //默认手机socker未连接
    public static boolean IsSocketPhone = false;

    public static CloudFrameApp getCloudFrameApp() {
        return cloudFrameApp;
    }

    public static final String BASE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/";

    public static GetHolderEvent mGetHolderEvent;

    private Gson gson = new Gson();

    //设置播放模式选择  主界面直接播放还是选中播放 默认主界面直接播放
    public static int IsPlayMoThod = 0;
    public static DevicePolicyManager policyManager;

    //字体
    public static Typeface typeFace;

    //息屏状态 默认未息屏
    public static boolean IsScreen = true;


    @Override
    public void onCreate() {
        super.onCreate();
        cloudFrameApp = this;
        SharedUtil.newInstance().setBoolean("Install", false);
        new Thread() {
            @Override
            public void run() {
                super.run();
                Info();
            }
        }.start();
    }

    private void Info() {
        JPushInterface.setDebugMode(true);    // 设置开启日志,发布时请关闭日志
        JPushInterface.init(this);            // 初始化 JPush
        regId = JPushInterface.getRegistrationID(this);
        OkHttpFinalConfiguration.Builder builder = new OkHttpFinalConfiguration.Builder();
        OkHttpFinal.getInstance().init(builder.build());
        policyManager = (DevicePolicyManager) this.getSystemService(Context.DEVICE_POLICY_SERVICE);
        EventBus.getDefault().register(this);
        Intent intentsevice = new Intent(this, SocketService.class);
        startService(intentsevice);
        typeFace = Typeface.createFromAsset(getAssets(), "Lato-Light.ttf");
        refWatcher = LeakCanary.install(this);
        registSreenStatusReceiver();

        boolean IsItBound = SharedUtil.newInstance().getBoolean(InterFaceCode.IsItBound);
        if (IsItBound) //绑定以后才有
            handler.sendEmptyMessage(0x123);
    }

    private ScreenStatusReceiver mScreenStatusReceiver;

    private void registSreenStatusReceiver() {
        mScreenStatusReceiver = new ScreenStatusReceiver();
        IntentFilter screenStatusIF = new IntentFilter();
        screenStatusIF.addAction(Intent.ACTION_SCREEN_ON);
        screenStatusIF.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(mScreenStatusReceiver, screenStatusIF);
    }


    public static RefWatcher getRefWatcher(Context context) {
        CloudFrameApp application = (CloudFrameApp) context.getApplicationContext();
        return application.refWatcher;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void CloudFrameAppEvent(BaseErrarEvent event) {
        switch (event.getCode()) {
            case InterFaceCode.Get_Holder_Resources:
                try {
                    JSONObject jsonObject = new JSONObject(event.getResult());
                    int code = jsonObject.optInt("err_code");
                    Log.e("TAG", "code:" + code);
                    if (code == HttpErrarCode.RESULT_CODE_FRAME_UNPAIR) {
                        SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, false);
                        EventBus.getDefault().post(new BaseErrarEvent("", isAllow_Binding));
                    } else if (code == HttpErrarCode.RESULT_CODE_SUCCESS) {
                        //成功操作 获取信息内容
                        SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, true);

                        Type listType = new TypeToken<HttpErrorEvent<GetHolderEvent>>() {
                        }.getType();
                        HttpErrorEvent<GetHolderEvent> newsInfos = gson.fromJson(event.getResult(), listType);
                        mGetHolderEvent = newsInfos.getData();
                        SharedUtil.newInstance().setString("Username", newsInfos.getData().getFrameInfo().getName());
                        SharedUtil.newInstance().setString("Location", newsInfos.getData().getFrameInfo().getLocation());

                        InitializationUtils.getInstance().SetGetHolder(newsInfos.getData(), getCloudFrameApp());
                    }
                } catch (JSONException e) {
                    Log.e("TAG", "获取数据异常");
                }
                break;

            case InterFaceCode.Setting:
                //先设置值再更新服务器的值
                HttpRequestAuxiliary.getInstance().getheartbest(InterFaceCode.Get_Holder_Resources);
                break;
            case 9999:
                ifConteng(event.getResult());
                break;
            case 9998:
                handlers.removeMessages(0x123);
                handlers.sendEmptyMessageDelayed(0x123, 6000);
                break;
            case 123:
                handler.sendEmptyMessage(0x123);
                break;
        }

    }


    Handler handlers = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            //如果六秒没有收到信息就是说断开了
            CloudFrameApp.IsSocketPhone = false;
        }
    };


    private void ifConteng(String newsInfos) {
        if (newsInfos.equals("4")) {
            Toast("Return key");
        } else if (newsInfos.equals("351")) {
            Toast("Playback key");
        } else if (newsInfos.equals("353")) {
            Toast("Setting key");
        } else if (newsInfos.equals("66")) {
            Toast("OK key");
        } else if (newsInfos.equals("19")) {
            Toast("Down key");
        } else if (newsInfos.equals("20")) {
            Toast("Lower key");
        } else if (newsInfos.equals("21")) {
            Toast("Left click");
        } else if (newsInfos.equals("22")) {
            Toast("Right click");
        }
    }

    private void Toast(String str) {
//        Toast t = Toast.makeText(this, str, Toast.LENGTH_LONG);
//        LinearLayout layout = (LinearLayout) t.getView();
//        TextView tv = (TextView) layout.getChildAt(0);
//        tv.setTextSize(25);
//        t.show();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, cloudFrameApp);
            handler.sendEmptyMessageDelayed(0x123, 60 * 1000);
        }
    };
}
