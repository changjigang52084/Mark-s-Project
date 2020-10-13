package com.unccr.zclh.dsdps.service;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.display.DisplayManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.play.ProgramPlayManager;
import com.unccr.zclh.dsdps.service.second.SecondScreenPresentation;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.ListUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月10日 上午9:33:13
 * @parameter SecondScreenService
 */
public class SecondScreenService extends Service implements MediaPlayer.OnCompletionListener {

    private static final String TAG = "SecondScreenService";

    private SecondScreenPresentation secondScreenPresentation;
    private SurfaceView presentSurfaceView;
    private ImageView presentImageView;
    private DisplayManager mDisplayManager;
    private int index = 0;
    private MediaPlayer mMediaPlayer;
    private int width;
    private int height;
    private boolean isSuccess = false;
    private boolean isDoubleScreen = false;
    private List<Map<String, String>> secondScreenFile = new ArrayList<>();
    private Map<String, Boolean> checkProgram = new HashMap<>();

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constants.SECOND_SCREEN_ACTION);
        registerReceiver(secondScreenReceiver, intentFilter);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        updateContents();
    }

    private void updateContents() {
        mDisplayManager = (DisplayManager) getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        Log.d(TAG, "updateContents displays: " + displays.length);
        if (displays.length >= 2) {
            width = displays[1].getWidth();
            height = displays[1].getHeight();
            isDoubleScreen = true;
            showPresentation(displays[1]);
        }
    }

    private void showPresentation(Display display) {
        if (secondScreenPresentation == null) {
            secondScreenPresentation = new SecondScreenPresentation(SecondScreenService.this, display);
        }
        secondScreenPresentation.getWindow().setType(WindowManager.LayoutParams.TYPE_PHONE);
        secondScreenPresentation.show();
        presentSurfaceView = secondScreenPresentation.getPresentSV();
        presentImageView = secondScreenPresentation.getPresentIV();
        presentImageView.setVisibility(View.VISIBLE);
        presentImageView.setBackgroundResource(R.drawable.zclh_logo);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private BroadcastReceiver secondScreenReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "secondScreenReceiver action: " + intent.getAction());
            List<Program> secondProgramList = ProgramPlayManager.getSecondProgramList();
            if (secondProgramList == null || secondProgramList.isEmpty()) {
                if (isDoubleScreen) {
                    Log.d(TAG, "secondScreenReceiver secondProgramList is null.");
                    presentSurfaceView.setVisibility(View.GONE);
                    presentImageView.setVisibility(View.VISIBLE);
                    presentImageView.setImageResource(R.drawable.zclh_logo);
                }
            }
            if (secondProgramList.size() == checkProgram.size()) {
                for (Program program : secondProgramList) {
                    if (checkProgram.get(program.getKey())) {
                        return;
                    }
                }
            }
            secondScreenFile.clear();
            checkProgram.clear();
            for (Program program : secondProgramList) {
                String prmKey = program.getKey();
                Log.d(TAG, "prmKey: " + prmKey);
                checkProgram.put(prmKey, true);
                for (int i = 0; i < program.getAs().size(); i++) {
                    List<Material> materialList = program.getAs().get(i).getMas();
                    if (ListUtil.isNotEmpty(materialList)) {
                        for (Material material : materialList) {
                            if (material != null) {
                                Integer integer = material.getT();
                                Log.d(TAG, "integer: " + integer);
                                if (integer != null) {
                                    switch (integer) {
                                        case Constants.PIC_FRAGMENT:
                                            String picPath = FileStore.getInstance().getImageFilePath(FileStore.getFileName(material.getU()));
                                            Log.d(TAG, "secondScreenReceiver picPath: " + picPath);
                                            Map<String, String> picMap = new HashMap<>();
                                            picMap.put("picPath", picPath);
                                            picMap.put("type", "pic");
                                            picMap.put("time", String.valueOf(material.getD()));
                                            secondScreenFile.add(picMap);
                                            break;
                                        case Constants.VIDEO_FRAGMENT:
                                            String videoPath = FileStore.getInstance().getVideoFilePath(FileStore.getFileName(material.getU()));
                                            Log.d(TAG, "secondScreenReceiver videoPath: " + videoPath);
                                            Map<String, String> videoMap = new HashMap<>();
                                            videoMap.put("videoPath", videoPath);
                                            videoMap.put("type", "video");
                                            secondScreenFile.add(videoMap);
                                            break;
                                        default:
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
            initData(secondScreenFile);
        }
    };

    private void initData(List<Map<String, String>> secondScreenFile) {
        if (secondScreenFile.size() > 0 && isDoubleScreen) {
            if (index >= secondScreenFile.size()) {
                index = 0;
            }
            Map<String, String> filePath = secondScreenFile.get(index);
            String type = filePath.get("type");
            Log.d(TAG, "initData type: " + type);
            switch (type) {
                case "pic":
                    String picPath = filePath.get("picPath");
                    Log.d(TAG, "initData picPath: " + picPath);
                    presentSurfaceView.setVisibility(View.GONE);
                    presentImageView.setVisibility(View.VISIBLE);
                    Bitmap bitmap = BitmapFactory.decodeFile(picPath);
                    presentImageView.setImageBitmap(bitmap);
                    int delayTime = Integer.parseInt(filePath.get("time"));
                    secondHandler.sendEmptyMessageDelayed(1, delayTime * 1000);
                    break;
                case "video":
                    String videoPath = filePath.get("videoPath");
                    Log.d(TAG, "initData videoPath: " + videoPath);
                    presentSurfaceView.setVisibility(View.VISIBLE);
                    presentImageView.setVisibility(View.GONE);
                    if (isSuccess) {
                        replay(videoPath);
                    } else {
                        playVideo(videoPath);
                        isSuccess = true;
                    }
                    break;
            }
//            Calendar cal = Calendar.getInstance();// 当前日期
//            int hour = cal.get(Calendar.HOUR_OF_DAY);// 获取小时
//            int minute = cal.get(Calendar.MINUTE);// 获取分钟
//            int minuteOfDay = hour * 60 + minute;// 从0:00分开是到目前为止的分钟数
//            final int start = 17 * 60 + 20;// 起始时间 17:20的分钟数
//            final int end = 19 * 60;// 结束时间 19:00的分钟数
//            if (minuteOfDay >= start && minuteOfDay <= end) {
//                System.out.println("在外围内");
//            } else {
//                System.out.println("在外围外");
//            }

        }
    }

    private void playVideo(final String url) {
        presentSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        presentSurfaceView.getHolder().setFixedSize(width, height);
        presentSurfaceView.getHolder().setKeepScreenOn(true);
        presentSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.d(TAG, "playVideo surfaceCreated url: " + url);
                play(url);
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
    }

    // 封装播放方法
    private void play(String urlPath) {
        Log.d(TAG, "play urlPath: " + urlPath);
        try {
            if (urlPath == null || "".equals(urlPath) || mMediaPlayer == null) {
                return;
            }
            // 播放器重置
            mMediaPlayer.reset();
            // 文件路径
            mMediaPlayer.setDataSource(urlPath);
            // 设置Video影片以SurfaceHolder播放
            mMediaPlayer.setDisplay(presentSurfaceView.getHolder());
            // 设置播放器播放数据流类型
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            // 缓冲
            mMediaPlayer.prepare();
            mMediaPlayer.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void replay(String urlPath) {
        Log.d(TAG, "replay urlPath: " + urlPath);
        if (urlPath == null || "".equals(urlPath) || mMediaPlayer == null) {
            return;
        }
        mMediaPlayer.reset();
        mMediaPlayer.release();
        mMediaPlayer = null;
        presentSurfaceView.setVisibility(View.GONE);
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnCompletionListener(this);
        presentSurfaceView.setVisibility(View.VISIBLE);
        try {
            if (mMediaPlayer != null) {
                mMediaPlayer.reset();
                mMediaPlayer.setDataSource(urlPath);
                presentSurfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                presentSurfaceView.getHolder().setFixedSize(width, height);
                presentSurfaceView.getHolder().setKeepScreenOn(true);
                mMediaPlayer.setDisplay(presentSurfaceView.getHolder());
                mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mMediaPlayer.prepare();
                mMediaPlayer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        index++;
        initData(secondScreenFile);
    }

    @SuppressLint("HandlerLeak")
    private Handler secondHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            index++;
            initData(secondScreenFile);
        }
    };

    /**
     * 释放播放器资源
     */
    private void releaseMediaPlayer() {
        if (mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy.");
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(secondScreenReceiver);
        releaseMediaPlayer();
        if (secondScreenPresentation != null) {
            secondScreenPresentation.dismiss();
        }
        System.gc();
    }
}
