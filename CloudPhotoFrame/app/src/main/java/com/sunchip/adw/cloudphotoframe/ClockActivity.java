package com.sunchip.adw.cloudphotoframe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.manager.AudioManger;
import com.sunchip.adw.cloudphotoframe.thread.TimeThread;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;


public class ClockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        setContentView(R.layout.activity_clock);
        SharedUtil.newInstance().setInt("Music", AudioManger.getInstance().getmusicVolume());
        Log.e("TAG", "需要保存的声音是:" + AudioManger.getInstance().getmusicVolume() + " 已经保存的声音是:" +
                SharedUtil.newInstance().getInt("Music"));

        CloudFrameApp.getCloudFrameApp().IsClock = true;
        AudioManger.getInstance().setUnmute(true);
        TextView textView = findViewById(R.id.date_tx);
        textView.setText(TimeThread.getCurrentWeekDay() + "");
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("TAG", "我是时钟界面:" + event.getKeyCode());
        if (event.getKeyCode() == 350) {
            CloudFrameApp.getCloudFrameApp().IsClock = false;
            Log.e("TAG", "退出的时候当前声音:" + AudioManger.getInstance().getmusicVolume() + " 已经保存的声音是:" +
                    SharedUtil.newInstance().getInt("Music"));
            AudioManger.getInstance().SetMusicVolume(SharedUtil.newInstance().getInt("Music"), false);
            //开屏
            this.finish();
        } else {
            return true;
        }
        //默认所有按键都不能有反应
        return super.dispatchKeyEvent(event);
    }
}
