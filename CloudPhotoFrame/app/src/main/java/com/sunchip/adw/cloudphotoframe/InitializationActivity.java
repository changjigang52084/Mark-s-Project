package com.sunchip.adw.cloudphotoframe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sunchip.adw.cloudphotoframe.Timer.MyCountTimer;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.HttpURLUtils;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.manager.AudioManger;
import com.sunchip.adw.cloudphotoframe.manager.MusicPlayManger;
import com.sunchip.adw.cloudphotoframe.util.AlertDialogUtils;
import com.sunchip.adw.cloudphotoframe.util.InitializationUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.NotDialogSystemUI;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.PlayPhotoVideo;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.StopPlayPhotoVideo;


public class InitializationActivity extends AppCompatActivity {

    //判断锁屏还是时钟界面
    int IsTurnColck = 0;//默认锁屏界面

//    public static boolean Ismedia = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void InitializationEvent(BaseErrarEvent event) {
        switch (event.getCode()) {
            case InterFaceCode.isAllow_Binding:
                SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, false);
                //解除绑定直接设置成默认值
                InitializationUtils.getInstance().setInitialization(this);
                //解除绑定 删除所有照片
                HttpURLUtils.deleteDirWihtFile(new File(CloudFrameApp.BASE));
                //相框解除绑定以后要重启  特么的系统没有关机弹窗还得自己做 因为弹窗要依赖activity
                // 那么就使用eventbus跳到activity
                AlertDialogUtils.getmAlertDialogUtils().AleraDialog(InitializationActivity.this);
                break;
            case NotDialogSystemUI:
                StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
                break;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        IsTurnColck = SharedUtil.newInstance().getInt("whenFrameGoesToSleep");
        Log.e("InitializationActivity", "獲取的鍵值:" + event.getKeyCode() + "" +
                "鎖屏狀態:" + (IsTurnColck == 0) + "  " + IsTurnColck);
        if (event.getKeyCode() == 354) {
            MyCountTimer.setCloseMyCount();
            MusicPlayManger.getInstance().Stop();
            SharedUtil.newInstance().setInt("vMensor", 0);
            if (IsTurnColck == 0)
            //息屏
            {
                //自动息屏
                SystemInterfaceUtils.getInstance().setScreen(false);
            } else {
                // 跳转到时钟设置界面
                Intent intent = new Intent(this, ClockActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        } else if (event.getKeyCode() == 350) {
//            MyCountTimer.setStartMyCount();
            //开屏  在自动息屏的时候
//            Ismedia = true;
            SystemInterfaceUtils.getInstance().setScreen(true);

        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == 349) {
            //人工息屏
            SharedUtil.newInstance().setInt("vMensor", 1);
            EventBus.getDefault().post(new BaseErrarEvent("", StopPlayPhotoVideo));
            if (CloudFrameApp.IsScreen) {
//                Ismedia = false;
                SystemInterfaceUtils.getInstance().setScreen(false);
            } else {
//                Ismedia = true;
                SystemInterfaceUtils.getInstance().setScreen(true);
            }
        }
        return super.onKeyDown(keyCode, event);
    }
}
