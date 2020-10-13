package com.sunchip.adw.cloudphotoframe;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.example.Launcher.Downling.image.NetworkUtil;
import com.example.Launcher.Downling.image.SunchipManager;
import com.sunchip.adw.cloudphotoframe.Timer.MyCountTimer;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.manager.MusicPlayManger;
import com.sunchip.adw.cloudphotoframe.play.PlayVideoPicActivity;
import com.sunchip.adw.cloudphotoframe.receiver.NoOperationReceiver;
import com.sunchip.adw.cloudphotoframe.receiver.ScreenOffAdminReceiver;
import com.sunchip.adw.cloudphotoframe.set.SettingActivity;
import com.sunchip.adw.cloudphotoframe.show.ShowVideoPicActivity;
import com.sunchip.adw.cloudphotoframe.util.AlertDialogUtils;
import com.sunchip.adw.cloudphotoframe.util.CommandUtil;
import com.sunchip.adw.cloudphotoframe.util.DrawableUtils;
import com.sunchip.adw.cloudphotoframe.util.PermissionUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.Locale;

import dalvik.system.DexClassLoader;

import static com.sunchip.adw.cloudphotoframe.app.CloudFrameApp.policyManager;

public class MainActivity extends InitializationActivity {
    private static final String TAG = "MainActivity";
    private TextView play_video_pic_Txt;
    private TextView show_video_pic_Txt;
    private TextView setting_Txt;
    private ImageView mCloud_Download, mCloud_Wifi;

    ComponentName adminReceiver;

    //在无法触摸状态的时候 我们设置一个统一的值适得其适配键值使用  默认选择第一个
    private int Select = 0;
    private int TopBotton = 0;

    //开机有网刷新一次列表
    private boolean IsGetPlaylis = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        setContentView(R.layout.activity_main);
        initView();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //申请权限
                PermissionUtils.getInstance().Permission(MainActivity.this);
                isOpen();
            }
        });

//        NoOperationReceiver.getInstance().mregisterReceiver(this);
//        SharedUtil.newInstance().setBoolean("Install", false);
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void MainActivityEvent(BaseErrarEvent event) {
        switch (event.getCode()) {
            case InterFaceCode.languagetype:
                this.finish();
                Intent intentPlay = new Intent(this, MainActivity.class);
                intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intentPlay);
                break;
        }
    }


    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x123:
                    //此处有更新了  那么可以自己写弹窗或者使用jar内的默认弹窗 现在暂时使用默认方法：
                    AlertDialogUtils.getmAlertDialogUtils().mshowNormalDialog(MainActivity.this);
                    SharedUtil.newInstance().setBoolean("Install", true);
                    break;
                case 0x124:
                    Log.e("TAG", "apk的安装地址是:" + msg.obj);
                    CommandUtil.install((String) msg.obj);
                    break;
                case 0x125:
                    Log.e("TAG", "没有更新" + " 状态是" + SharedUtil.newInstance().getBoolean("Install"));
                    if (SharedUtil.newInstance().getBoolean("Install")) {
                        //如果有就弹窗
                        AlertDialogUtils.getmAlertDialogUtils().mshowalreadyDialog(MainActivity.this);
                        handler.sendEmptyMessageDelayed(0x127, 4000);
                    }
                    break;
                case 0x126:
//                    Log.e("TAG", "apk下载失败:");
                    AlertDialogUtils.getmAlertDialogUtils().DissDialog();
                    break;
                case 0x127:
//                    Log.e("TAG", "已经是最新版本:");
                    AlertDialogUtils.getmAlertDialogUtils().DissDialog();
                    break;
                case 0x128:
//                    Log.e("TAG", "已经是最新版本:");
                    SunchipManager.checkSunchipLCTS(MainActivity.this,
                            "https://vucatimes-update.s3.us-east-2.amazonaws.com/mFrame.xml", handler);
                    break;
                case 0x998:
                    if (NetworkUtil.isNetworkConnected(MainActivity.this)) {
                        handler.sendEmptyMessageDelayed(0x999, 5000);
                        //有网
                        mCloud_Wifi.setBackgroundResource(R.mipmap.wifi);
                        mCloud_Download.setBackgroundResource(R.mipmap.yunicon_yes);
                    } else {
                        //无网
                        mCloud_Download.setBackgroundResource(R.mipmap.yunicon_not);
                        mCloud_Wifi.setBackgroundResource(R.mipmap.wifi_not);
                    }
                    //五秒刷新一次
                    handler.sendEmptyMessageDelayed(0x998, 5000);
                    break;
                case 0x999:
                    if (!IsGetPlaylis)
                        HttpRequestAuxiliary.getInstance().getPlayList(InterFaceCode.Get_Playback_Resources);
                    IsGetPlaylis = true;
                    break;
            }
        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isOpen();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyCountTimer.setStartMyCount("MAINactivity-----onResume");
        MusicPlayManger.getInstance().Stop();
    }

    /**
     * 检测用户是否开启了超级管理员
     */
    private boolean isOpen() {
        //申请锁屏权限
        adminReceiver = new ComponentName(this, ScreenOffAdminReceiver.class);
        if (policyManager.isAdminActive(adminReceiver)) {//判断超级管理员是否激活
            return true;
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后就可以使用锁屏功能了...");
            startActivityForResult(intent, 0);
            return false;
        }
    }


    private void initView() {
        play_video_pic_Txt = findViewById(R.id.Play_videos_and_pictures);
        show_video_pic_Txt = findViewById(R.id.Show_pictures_and_videos);
        setting_Txt = findViewById(R.id.Main_interface_settings);

        play_video_pic_Txt.setTypeface(CloudFrameApp.typeFace);
        show_video_pic_Txt.setTypeface(CloudFrameApp.typeFace);
        setting_Txt.setTypeface(CloudFrameApp.typeFace);

        mCloud_Download = findViewById(R.id.Cloud_Download);
        mCloud_Wifi = findViewById(R.id.Cloud_Wifi);

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                LatiInfo();
            }
        });
    }

    private void LatiInfo() {
        int isLanguage = SharedUtil.newInstance().getInt("isLanguage", 0);
        if (isLanguage == 0)
            SystemInterfaceUtils.getInstance().setConfiguration(this, Locale.ENGLISH, false);
        else if (isLanguage == 1) {
            SystemInterfaceUtils.getInstance().setConfiguration(this, Locale.JAPANESE, false);
        } else if (isLanguage == 2) {
            //德语
            SystemInterfaceUtils.getInstance().setConfiguration(this, Locale.GERMANY, false);
        } else if (isLanguage == 3) {
            //法语
            SystemInterfaceUtils.getInstance().setConfiguration(this, Locale.FRANCE, false);
        } else if (isLanguage == 4) {
            //西班牙语
            SystemInterfaceUtils.getInstance().setConfiguration(this, new Locale("es", "ES"), false);
        }
        handler.sendEmptyMessage(0x128);
        //刷新状态
        handler.sendEmptyMessage(0x998);
    }

    private void initListener(int code) {
        //初始化操作
        DrawableUtils.getInstance().SetDrawable(R.drawable.photogray, show_video_pic_Txt, code, R.color.transparent);
        DrawableUtils.getInstance().SetDrawable(R.drawable.settinggray, setting_Txt, code, R.color.transparent);
        DrawableUtils.getInstance().SetDrawable(R.drawable.videogray, play_video_pic_Txt, code, R.color.transparent);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode != 350)
            MyCountTimer.setStartMyCount("MAINactivity_onKeyDown_keyCode != 350");
        Log.e("MainActivity", "获取到的键值是：" + keyCode);
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            //左
            if (!isScreenOriatationPortrait(this))
                KeyeventLight();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            //右
            if (!isScreenOriatationPortrait(this))
                KeyeventRight();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //OK键
            if (!isScreenOriatationPortrait(this))
                staerActibity(Select);
            else staerActibity(TopBotton);
        } else if (keyCode == 352) {
            //视频键 352  好像并没有用到
            staerActibity(1);
        } else if (keyCode == 351) {
            //播放相片键351
            staerActibity(0);
        } else if (keyCode == 353) {
            //Setting 键351
            staerActibity(2);

        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            //上
            if (isScreenOriatationPortrait(this))
                KeyeventTOP();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            //下
            if (isScreenOriatationPortrait(this))
                KeyeventBotton();
        }

        return super.onKeyDown(keyCode, event);
    }


    //主界面的显示只使用了左右键值操作
    //右键
    public void KeyeventRight() {
        initListener(2);
        Select++;
        setdrable(2, 2, R.drawable.settingblue, setting_Txt, Select > 2);
    }


    //左键
    public void KeyeventLight() {
        initListener(2);
        Select--;
        setdrable(2, 0, R.drawable.videoblue, play_video_pic_Txt, Select < 0);
    }

    private void setdrable(int i, int i2, int p, TextView play_video_pic_txt, boolean b) {
        if (Select == 1) {
            DrawableUtils.getInstance().SetDrawable(R.drawable.photoblue, show_video_pic_Txt, i, R.color.MainBlue);
        } else if (Select == i2) {
            DrawableUtils.getInstance().SetDrawable(p, play_video_pic_txt, i, R.color.MainBlue);
        } else if (b) {
            //直接无操作 或者直接返回第一个
            Select = i2; //此处设置为无操作
            DrawableUtils.getInstance().SetDrawable(p, play_video_pic_txt, i, R.color.MainBlue);
        }
    }

    //上
    public void KeyeventTOP() {
        initListener(1);
        TopBotton--;
        TopSelected();
    }

    private void TopSelected() {
        if (TopBotton == 0) {
            DrawableUtils.getInstance().SetDrawable(R.drawable.videoblue, play_video_pic_Txt, 1, R.color.MainBlue);
        } else if (TopBotton == 1) {
            DrawableUtils.getInstance().SetDrawable(R.drawable.photoblue, show_video_pic_Txt, 1, R.color.MainBlue);
        } else if (TopBotton < 0) {
            TopBotton = 0;
            DrawableUtils.getInstance().SetDrawable(R.drawable.videoblue, play_video_pic_Txt, 1, R.color.MainBlue);
        }
    }


    //下
    public void KeyeventBotton() {
        initListener(1);
        TopBotton++;
        Botton();
    }

    private void Botton() {
        if (TopBotton == 1) {
            DrawableUtils.getInstance().SetDrawable(R.drawable.photoblue, show_video_pic_Txt, 1, R.color.MainBlue);
        } else if (TopBotton == 2) {
            DrawableUtils.getInstance().SetDrawable(R.drawable.settingblue, setting_Txt, 1, R.color.MainBlue);
        } else if (TopBotton > 2) {
            TopBotton = 2;
            DrawableUtils.getInstance().SetDrawable(R.drawable.settingblue, setting_Txt, 1, R.color.MainBlue);
        }
    }

    public void staerActibity(int select) {
        if (select == 0) {
            CloudFrameApp.IsPlayMoThod = 0;
            Intent intentPlay = new Intent(MainActivity.this, PlayVideoPicActivity.class);
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentPlay);
        } else if (select == 1) {
            Intent intentPlay = new Intent(MainActivity.this, ShowVideoPicActivity.class);
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentPlay);
        } else if (select == 2) {
            Intent intentPlay = new Intent(MainActivity.this, SettingActivity.class);
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentPlay);
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
        MyCountTimer.setCloseMyCount();
    }


    /**
     * 返回当前屏幕是否为竖屏。
     *
     * @param context
     * @return 当且仅当当前屏幕为竖屏时返回true, 否则返回false。
     */
    public static boolean isScreenOriatationPortrait(Context context) {
        return context.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;
    }
}
