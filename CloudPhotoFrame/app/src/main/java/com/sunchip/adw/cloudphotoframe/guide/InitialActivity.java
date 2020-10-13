package com.sunchip.adw.cloudphotoframe.guide;

import android.os.Bundle;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.StopPlayPhotoVideo;


public class InitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
