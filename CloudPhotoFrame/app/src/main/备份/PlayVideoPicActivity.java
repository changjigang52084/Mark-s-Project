package com.sunchip.adw.cloudphotoframe.play;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.example.SunchipFile.File_Message;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.leakcanary.RefWatcher;
import com.sunchip.adw.cloudphotoframe.BaseActivity;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.Timer.PlayTimer;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.HttpURLUtils;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.HttpErrorEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.PhotoListEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.PhotosEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpErrarCode;
import com.sunchip.adw.cloudphotoframe.manager.AudioManger;
import com.sunchip.adw.cloudphotoframe.manager.MusicPlayManger;
import com.sunchip.adw.cloudphotoframe.show.ShowVideoPicActivity;
import com.sunchip.adw.cloudphotoframe.util.AnimUtils;
import com.sunchip.adw.cloudphotoframe.util.BitmapUtils;
import com.sunchip.adw.cloudphotoframe.util.FileUtils;
import com.sunchip.adw.cloudphotoframe.util.MediaFileUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;


import jp.wasabeef.glide.transformations.internal.FastBlur;

import static com.sunchip.adw.cloudphotoframe.app.CloudFrameApp.BASE;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.PlayPhotoVideo;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.ValumeUI;
import static com.sunchip.adw.cloudphotoframe.show.ShowVideoPicActivity.Selection;

public class PlayVideoPicActivity extends BaseActivity implements MediaPlayer.OnCompletionListener, MediaPlayer.OnVideoSizeChangedListener {

    private static final String TAG = "PlayVideoPicActivity";
    private File materialFile = null;
    private boolean isSuccess = false;
    private ImageView imageView;

    private SurfaceView surfaceView;
    private MediaPlayer mediaPlayer;
    private int index = 0;

    private long SLEEP_TIME = 8000; //图片播放时长
    private int playNum; // 指定播放的数
    private int totalNum; // 获取要播放的图片总数
    private String animationType;

    private boolean IsPause = true;

    private TextClock textClock;
    private LinearLayout PlayClockLinear;

    //本地保存的图片列表
    private String mData = "";
    List<PhotosEvent> mPhotosEvent = new ArrayList<>();

    private PlayHandler playHandler = new PlayHandler();
    List<File> fileList = new ArrayList<>();

    private ImageView mRenderScriptimage;

    private DonutProgress mDonutProgress;

    private TextView caption;

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.e("TAG", "息屏状态==================:" + mp.isPlaying() /*Ismedia*//**/);
//        if (Ismedia)
        initData();
    }

    @Override
    public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
//        Log.e("TAG",width+"==========="+height);
        surfaceView.getHolder().setFixedSize(width, height);
    }


    public class PlayHandler extends Handler {

    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_play_video_pic);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        initView();
        //检测内存
        RefWatcher refWatcher = CloudFrameApp.getRefWatcher(this);
        refWatcher.watch(this);

        String path = SharedUtil.newInstance().getString("MusicSetting");
        if (TextUtils.isEmpty(path))
            return;
        Log.e("TAG", "path===" + path);
        if (path.equals(getResources().getString(R.string.off))) {
            //关闭音乐  不播放
            MusicPlayManger.getInstance().Stop();
        } else {
            //开启音乐了
            //那么音乐地址就是/data/pre_sel_del/+path
            MusicPlayManger.getInstance().play();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        getPlay();
        EventBus.getDefault().post(new BaseErrarEvent("", PlayPhotoVideo));
    }

    private void getPlay() {
        try {
            mData = File_Message.read("PhotoList", this);
        } catch (Exception e) {
            Log.e("TAG", "找不到文件需要你去设置");
        }
        if (mData != null) {
            TypeTokenPlaylist(mData);
        }
    }

    private Gson gson = new Gson();

    private void TypeTokenPlaylist(String event) {
        Type listType = new TypeToken<HttpErrorEvent<List<PhotoListEvent>>>() {
        }.getType();
        HttpErrorEvent<List<PhotoListEvent>> newsInfos = gson.fromJson(event, listType);

//        if (newsInfos != null) {
        if (newsInfos.getErr_code() == HttpErrarCode.RESULT_CODE_SUCCESS) {
            //内容获取成功
            if (newsInfos.getData().size() == 0) {
                //没有数据
            } else {
                //如果有数据的话 那么我们就统计所有的数据放在list里面
                for (int i = 0; i < newsInfos.getData().size(); i++) {
                    for (int i1 = 0; i1 < newsInfos.getData().get(i).getPhotos().size(); i1++) {
                        mPhotosEvent.add(newsInfos.getData().get(i).getPhotos().get(i1));
                    }
                }
            }
        }
    }


    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("TAG", "我是播放列表的时候的键值:" + event.getKeyCode());
        if (event.getKeyCode() == 354) {
            if (mediaPlayer != null) {
                if (mediaPlayer.isPlaying()) {
                    mediaPlayer.pause();
                }
            }
            stopThread();
        }
        return super.dispatchKeyEvent(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void PlayVideoEvent(BaseErrarEvent event) {
        switch (event.getCode()) {
            case PlayPhotoVideo:
                //解析数据  到时候再去判断
                initData(materialFile);
                break;
            case ValumeUI:
                SetValume();
                break;
            case InterFaceCode.Get_Playback_Resources:
                //可以获取到内容
                UpdateTypeTokenPlaylist(event.getResult());
                TypeTokenPlaylist(event.getResult());
                break;
            case InterFaceCode.DownLoadDone:
                Log.e("PLAY", "更新播放列表============================================" + CloudFrameApp.IsPlayMoThod);
                if (CloudFrameApp.IsPlayMoThod == 0) {
                    fileList.clear();
                    fileList = FileUtils.getInstance().getFile(materialFile);
                } else {
                    //从播放列表进来
                    ShowVideoPicActivity.PhotoPhth.add(path);
                    playHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            right();
                        }
                    }, 1000);
                }
                break;
            case InterFaceCode.playtimer:
                if (IsPause) {
                    if (SharedUtil.newInstance().getInt("shuffle") == 1) {
                        Random ra = new Random();
                        index = index + ra.nextInt(5);
                    } else
                        index++;
                    Log.d(TAG, "playRunnable index: " + index);
                    if (materialFile == null || !materialFile.exists()) {
                        return;
                    }
                    EventBus.getDefault().post(new BaseErrarEvent("", PlayPhotoVideo));
                } else {
                    return;
                }
                break;
        }
    }

    String path = "";

    public void UpdateTypeTokenPlaylist(String event) {
        Type listType = new TypeToken<HttpErrorEvent<List<PhotoListEvent>>>() {
        }.getType();
        HttpErrorEvent<List<PhotoListEvent>> newsInfos = gson.fromJson(event, listType);
        if (newsInfos.getErr_code() == HttpErrarCode.RESULT_CODE_SUCCESS) {
            //内容获取成功
            if (newsInfos.getData().size() == 0) {
                //没有数据可以不用管
            } else {
                List<PhotosEvent> mlist = newsInfos.getData().get(ShowVideoPicActivity.Selection).getPhotos();
                //获取到的图片大小 如果大于本地的播放图片大小 那么就直接下载最后一个图片 然后添加到数组
                if (mlist.size() > ShowVideoPicActivity.PhotoPhth.size()) {
                    //下载最后一个资源
                    String Key = mlist.get(mlist.size() - 1).getKey();
                    int Sm = Key.lastIndexOf(".");
                    String Suffix = Key.substring(Sm);
                    String PhotoPathName = mlist.get(mlist.size() - 1).getMd5() + Suffix;
                    final String Path = CloudFrameApp.BASE + PhotoPathName;
                    path = Path;
                    HttpURLUtils.Download(mlist.get(mlist.size() - 1).getUrl(), new File(Path), 1);
                }
            }
        }
    }


    private void initView() {

        imageView = findViewById(R.id.imageView);
        surfaceView = findViewById(R.id.vdeoView);
        caption = findViewById(R.id.Playcaption);
        mDonutProgress = findViewById(R.id.donut_progress);
        mDonutProgress.bringToFront();

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setLooping(true);
//        onVideoSizeChanged
        mediaPlayer.setOnVideoSizeChangedListener(this);

        textClock = findViewById(R.id.PlayTextClock);
        PlayClockLinear = findViewById(R.id.PlayClockLinear);
        mRenderScriptimage = findViewById(R.id.RenderScriptimage);
        PlayClockLinear.setVisibility(!(SharedUtil.newInstance().getInt("showClock") == 0) ? View.VISIBLE : View.GONE);

        if (CloudFrameApp.IsPlayMoThod == 0) {
            index = 0;
        } else {
            index = ShowVideoPicActivity.PhotoSelection;
        }
        EventBus.getDefault().post(new BaseErrarEvent("", ValumeUI));

        if (FileUtils.getInstance().fileIsExists(BASE)) {
            materialFile = new File(BASE);
            fileList = FileUtils.getInstance().getFile(materialFile);
        }

        int select = SharedUtil.newInstance().getInt("transitionType1");
        if (select == 0) {
            mRenderScriptimage.setVisibility(View.VISIBLE);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageView.setAdjustViewBounds(true);
        } else if (select == 1) {
            mRenderScriptimage.setVisibility(View.GONE);
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//            imageView.setAdjustViewBounds(true);
        } else if (select == 2) {
            mRenderScriptimage.setVisibility(View.GONE);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        } else if (select == 3) {
            mRenderScriptimage.setVisibility(View.GONE);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        }
    }

    private void SetValume() {
        mDonutProgress.setDonut_progress(AudioManger.getInstance().getmusicVolume() + "");
        mDonutProgress.setText(AudioManger.getInstance().getmusicVolume() + "");
        mDonutProgress.setMax(AudioManger.getInstance().getmusicMaxVolume());
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            //上  在此界面当做音量键
            AudioManger.getInstance().SetMusicVolume(AudioManger.getInstance().getmusicVolume() + 3, true);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            //下 在此界面当做音量键
            AudioManger.getInstance().SetMusicVolume(AudioManger.getInstance().getmusicVolume() - 3, true);
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            //左
            lift();
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            //右
            right();
        } else if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //OK键
            paustPlaylist();
        } else if (event.getKeyCode() == 352) {
            staerActibity(1);
        } else if (event.getKeyCode() == 353) {
            staerActibity(2);
        }
        return super.onKeyDown(keyCode, event);
    }


    private void paustPlaylist() {
        stopThread();
        IsPause = !IsPause;
        if (IsPause) {
            //缩短点时间呗
            PlayTimer.setStartCount(SLEEP_TIME / 2);
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
            }
        } else {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
            }
        }
    }

    private void right() {
        initData();
    }


    private void lift() {
        stopThread();
        if (SharedUtil.newInstance().getInt("shuffle") == 1) {
            Random ra = new Random();
            index = index + ra.nextInt(5);
        } else
            index--;
        if (index <= 0) {
            index = 0;
        }
        EventBus.getDefault().post(new BaseErrarEvent("", PlayPhotoVideo));
    }

    /**
     * 获取指定文件夹里所有文件
     */
    private void initData(File materialFile) {
        if (materialFile == null || !materialFile.exists()) {
            imageView.setVisibility(View.VISIBLE);
            imageView.setBackgroundResource(R.drawable.no_photo);
            return;
        }
        try {
            if (fileList == null) {
                imageView.setVisibility(View.VISIBLE);
                imageView.setBackgroundResource(R.drawable.no_photo);
                return;
            }
            totalNum = fileList.size();

            int playbackMode = SharedUtil.newInstance().getInt("playbackMode");
            Log.d(TAG, "playbackMode: " + playbackMode);
            if (0 == playbackMode) {
                playNum = totalNum;
            } else if (1 == playbackMode) {
                playNum = 100;
            } else if (2 == playbackMode) {
                playNum = 200;
            } else if (3 == playbackMode) {
                playNum = 500;
            } else if (4 == playbackMode) {
                playNum = 1000;
            }

            SLEEP_TIME = getResources().getIntArray(R.array.time_int)[SharedUtil.newInstance().getInt("transitionTime")];
            animationType = getResources().getStringArray(R.array.AlphaAnimation_type)[SharedUtil.newInstance().getInt("transitionType")];
            caption.setVisibility(SharedUtil.newInstance().getInt("showCaption", 0) == 0 ? View.GONE : View.VISIBLE);

            String materialPath;
            if (CloudFrameApp.IsPlayMoThod == 0) {
                if (playNum > 0) {
                    if (playNum <= totalNum) {
                        if (index >= playNum) {
                            index = 0;
                        }
                    } else {
                        if (index >= totalNum) {
                            index = 0;
                        }
                    }
                }
                materialPath = fileList.get(index).getPath();
            } else {
                if (ShowVideoPicActivity.PhotoPhth.size() > 0) {
                    if (playNum <= totalNum) {
                        if (index > ShowVideoPicActivity.PhotoPhth.size() - 1) {
                            index = 0;
                        }
                    } else if (index > totalNum) {
                        index = 0;
                    }
                }
                materialPath = ShowVideoPicActivity.PhotoPhth.get(index);
            }
            //此处去判断文件是否大小合适 不合适直接下一个
            IsPlay(materialPath);
        } catch (Exception e) {
            Log.e(TAG, "initData e: " + e.getMessage());
        }
    }


    private void setBackground(String materialPath, boolean isplay) {
        surfaceView.setVisibility(View.GONE);
        imageView.setVisibility(View.VISIBLE);
//        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        if (isplay) {
            Glide.with(this).load(materialPath).diskCacheStrategy(DiskCacheStrategy.SOURCE)
                    .override(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .error(R.drawable.no_photo).signature(new StringSignature(UUID.randomUUID().toString())).into(imageView);
            //毛玻璃特效
            try {
                Bitmap bitmap = BitmapUtils.getInstance().openImage(materialPath);
                int scaleRatio = 40;
                int blurRadius = 8;
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap,
                        bitmap.getWidth() / scaleRatio,
                        bitmap.getHeight() / scaleRatio,
                        false);
                Bitmap blurBitmap = FastBlur.blur(scaledBitmap, blurRadius, true);
                mRenderScriptimage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                mRenderScriptimage.setImageBitmap(blurBitmap);
            } catch (Exception e) {
                Log.e("TAG", "地址异常: 不播放");
                PlayTimer.setStartCount(SLEEP_TIME);
            }
        } else Glide.with(this).load(R.drawable.no_photo).into(imageView);
        AnimUtils.setAnimations(imageView, animationType);
        PlayTimer.setStartCount(SLEEP_TIME);
    }

    private void IsPlay(String materialPath) throws IOException {

        if (mPhotosEvent != null && mPhotosEvent.size() > 0) {
            for (int i = 0; i < mPhotosEvent.size(); i++) {
//                Log.e("TAG","需要显示的数据是for:"+mPhotosEvent.get(i).getCaption());
                //先判断要播放的地址是都能匹配到Md5
                if (materialPath.contains(mPhotosEvent.get(i).getMd5())) {
//                    Log.e("TAG","需要显示的数据是:"+mPhotosEvent.get(i).getCaption());
                    caption.setText(mPhotosEvent.get(i).getCaption());
                    if (FileUtils.getInstance().getFileOrFilesSize(materialPath) == mPhotosEvent.get(i).getSize()) {
                        if (MediaFileUtils.isImageFileType(materialPath)) {
                            MusicPlayManger.getInstance().PlayMusic();
                            setBackground(materialPath, true);
                        } else if (MediaFileUtils.isVideoFileType(materialPath)) {
                            MusicPlayManger.getInstance().PausePlay();
                            imageView.setVisibility(View.GONE);
                            surfaceView.setVisibility(View.VISIBLE);
//                            if (isSuccess) {
//                                replay(materialPath);
//                            } else {
//                                isSuccess = true;
                            playVideo(materialPath);
//                            }
                        }
                        return;
                    } else {
                        //此资源异常 直接显示一张
                        setBackground(materialPath, false);
                        return;
                    }
                } /*else {
                    Log.e("TAG", "IsPlay============MD5不匹配================" + materialPath + "====" + CloudFrameApp.IsPlayMoThod);
                    setBackground(materialPath, true);
                    return;
                }*/
            }
        } else setBackground(materialPath, false);
    }

    /**
     * 播放视频
     *
     * @param url
     */
    private void playVideo(final String url) {

        surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.setZOrderOnTop(true);
        surfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder surfaceHolder) {
                play(url);
            }

            @Override
            public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

            }

            @Override
            public void surfaceDestroyed(SurfaceHolder surfaceHolder) {

            }
        });
    }

    /**
     * 封装播放方法
     *
     * @param urlPath 播放路径
     */
    private void play(String urlPath) {
        try {
            if (urlPath == null || "".equals(urlPath) || mediaPlayer == null) {
                return;
            }
            mediaPlayer.reset();
            mediaPlayer.setDataSource(urlPath);
            mediaPlayer.setDisplay(surfaceView.getHolder());
            mediaPlayer.setOnCompletionListener(this);
            mediaPlayer.prepare();
            mediaPlayer.setOnVideoSizeChangedListener(this);
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mediaPlayer.start();
                    mediaPlayer.seekTo(0);
                }
            });

        } catch (Exception e) {
            initData();
        }
    }

    /**
     * 封装重复播放方法
     *
     * @param urlPath 播放路径
     */
    private void replay(String urlPath) {
        if (urlPath == null || "".equals(urlPath) || mediaPlayer == null) {
            return;
        }
        mediaPlayer.reset();
        mediaPlayer.release();
        mediaPlayer = null;
        surfaceView.setVisibility(View.GONE);
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.setOnVideoSizeChangedListener(this);
        surfaceView.setVisibility(View.VISIBLE);
        try {
            if (mediaPlayer != null) {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(urlPath);
                surfaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
                surfaceView.getHolder().setKeepScreenOn(true);
                mediaPlayer.setDisplay(surfaceView.getHolder());
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        mediaPlayer.start();
                        mediaPlayer.seekTo(0);
                    }
                });

                mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                    @Override
                    public boolean onError(MediaPlayer mp, int what, int extra) {
                        Log.e("TAG", "MediaPlayer:" + what + "  extra:" + extra);
                        return true;
                    }
                });
                mediaPlayer.prepareAsync();
            }
        } catch (Exception e) {
            initData();
        }
    }

    private void initData() {
        stopThread();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        if (SharedUtil.newInstance().getInt("shuffle") == 1) {
            Random ra = new Random();
            index = index + ra.nextInt(5);
        } else
            index++;
        EventBus.getDefault().post(new BaseErrarEvent("", PlayPhotoVideo));
    }


    private void releaseMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause.");
        playPause();
        MusicPlayManger.getInstance().PausePlay();
    }


    private void playPause() {
        if (mediaPlayer != null && mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop.");
        stopThread();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }


    private void stopThread() {
        PlayTimer.setCloseMyCount();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy.");
        stopThread();
        releaseMediaPlayer();
        System.runFinalization();
        System.gc();
    }
}
