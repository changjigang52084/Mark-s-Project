package com.sunchip.adw.cloudphotoframe.guide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.MediaController;
import android.widget.VideoView;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.util.DestoryActivityUtils;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;

public class FlashPlayActivity extends AppCompatActivity {
    VideoView videoView;
    Bundle msavedInstanceState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        setContentView(R.layout.activity_flash);
        DestoryActivityUtils.getInstance().addDestoryActivity(FlashPlayActivity.this,"FlashPlayActivity");
        initView();
        msavedInstanceState = savedInstanceState;
    }

    public void initView() {
        videoView = (VideoView) findViewById(R.id.Videoview);

    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
        // 记录当前播放进度
        outState.putInt("aa", videoView.getCurrentPosition());
        Log.e("FlashActivity", "aa==========================" + videoView.getCurrentPosition());
    }

    @Override
    protected void onResume() {
        super.onResume();
        playVideo();
    }

    public void playVideo() {
        String uri = "android.resource://" + getPackageName() + "/" + R.raw.flash;
        Log.e("FlashActivity", "Path==========================" + uri);
        videoView.setVideoURI(Uri.parse(uri));

        MediaController mc = new MediaController(FlashPlayActivity.this);
        //设置控制器 控制的是那一个videoview
        mc.setAnchorView(videoView);
        //设置videoview的控制器为mc
        videoView.setMediaController(mc);
        videoView.start();
        Log.e("FlashActivity", "msavedInstanceState==========================" + msavedInstanceState);
        // 当横屏时接着播放
        if (msavedInstanceState != null) {
            // 得到进度
            int ss = msavedInstanceState.getInt("aa");
            Log.e("FlashActivity", "ss==========================" + ss);
            // 接着播放
            videoView.seekTo(ss);
        }

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                String strres = "播放完成";
                //判断视频的总长度与当前长度是否在误差区间内
                if (Math.abs(videoView.getDuration() - videoView.getCurrentPosition()) > 1000) {
                    strres = "播放错误,可能无网络连接";
                }
                Log.e("TAG", strres);
                //网络可用
                startActivity(new Intent(FlashPlayActivity.this, PairYourFrameActivity.class));
            }
        });
    }
}
