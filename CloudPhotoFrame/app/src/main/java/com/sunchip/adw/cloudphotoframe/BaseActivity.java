package com.sunchip.adw.cloudphotoframe;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import androidx.annotation.Nullable;

import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.play.PlayVideoPicActivity;
import com.sunchip.adw.cloudphotoframe.set.SettingActivity;
import com.sunchip.adw.cloudphotoframe.show.ShowVideoPicActivity;

public class BaseActivity extends InitializationActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void staerActibity(int select) {
        if (select == 0) {
            CloudFrameApp.IsPlayMoThod = 0;
            Intent intentPlay = new Intent(BaseActivity.this, PlayVideoPicActivity.class);
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentPlay);
        } else if (select == 1) {
            Intent intentPlay = new Intent(BaseActivity.this, ShowVideoPicActivity.class);
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentPlay);
        } else if (select == 2) {
            Intent intentPlay = new Intent(BaseActivity.this, SettingActivity.class);
            intentPlay.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intentPlay);
        }
        this.finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("BaseActivity", "收到的键值是:" + keyCode);

        if (keyCode == 355 || keyCode == KeyEvent.KEYCODE_HOME) {
            //还缺个判断,如果设备解绑了的话,这个无操作 不 在绑定界面就不应该继承这个类
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            this.finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
