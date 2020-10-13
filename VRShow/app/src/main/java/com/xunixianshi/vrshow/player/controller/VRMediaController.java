package com.xunixianshi.vrshow.player.controller;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.asha.vrlib.MDVRLibrary;
import com.xnxs.mediaplayer.widget.media.IMediaController;
import com.xnxs.mediaplayer.widget.media.MediaPlayerControl;
import com.xnxs.mediaplayer.widget.media.IMediaPlayerVRControl;
import com.xunixianshi.vrshow.R;

import java.util.Formatter;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * Created by duanchunlin on 2016/8/10.
 * 播放视频界面控制类。
 */
public class VRMediaController implements IMediaController {

    public static final int MEDIA_DISPLAY_IMMERSION_ON = 1;
    public static final int MEDIA_DISPLAY_IMMERSION_OFF = 2;

    @Bind(R.id.video_toolbar_time_tv)
    TextView video_toolbar_time_tv;

    @Bind(R.id.video_bottom_toolbar_play_pause_iv)
    ImageView mPauseButton;

    @Bind(R.id.video_toolbar_time_seekbar)
    SeekBar mProgress;

    @Bind(R.id.video_toolbar_btn_gyro_iv)
    ImageView media_gyro_btn;

    @Bind(R.id.video_toolbar_btn_screen_iv)
    ImageView media_screen_btn;

    @Bind(R.id.player_convenient_prompt_ll)
    LinearLayout player_convenient_prompt_ll;

    @Bind(R.id.player_convenient_prompt_iv)
    ImageView player_convenient_prompt_iv;

    @Bind(R.id.player_convenient_prompt_tv)
    TextView player_convenient_prompt_tv;

    @Bind(R.id.video_play_tools_controller_rl)
    RelativeLayout video_play_tools_controller_rl;

    @Bind(R.id.view_media_immersion_model)
    Button view_media_immersion_model;

    private MediaPlayerControl mPlayer;
    private IMediaPlayerVRControl mVRContorl;
    private int mPlayMode = IMediaPlayerVRControl.VIDEO_TYPE_UNKNOWN;

    private Activity mContext;
    private View mView;

    private boolean mShowing;
    private boolean mDragging;
    private static final int sDefaultTimeout = 3000;

    private int showPausePlayDelayTime = 0;
    private static final int FADE_OUT = 1;
    private static final int SHOW_PROGRESS = 2;
    private static final int SHOW_PAUSE_PLAY_STATE = 3;
    private static final int HIDE_CONVENIENT_PROMPT = 4;


//    private MDVRLibrary mMDVRLibrary;
    private boolean mGeneralEnable; //全景是否开启
    private boolean mIsImmersionModel = false; //是否是沉浸模式
    private boolean mIsCanEnableGeneral = false; //是否支持陀螺仪

    private int mProjectionModeDefault = MDVRLibrary.PROJECTION_MODE_SPHERE;
    private int mDisplayModeDefault = MDVRLibrary.DISPLAY_MODE_GLASS;

    private int mSplitScreenState; //分屏
    private OnVideoDisPlayListener onVideoDisPlayListener;

    StringBuilder mFormatBuilder;
    Formatter mFormatter;
    private long currentPlayTime = 0;

    /**
     * By Lin Date:2016/8/10  14:42
     *
     * @注释：必须具有该类实例化的view,这里会绑定到butterknife
     */
    public VRMediaController(Activity context, View view) {
        this.mContext = context;
        this.mView = view;
        ButterKnife.bind(this, view);
        initControllerView();
    }

    private void initControllerView() {
        mProgress.setOnSeekBarChangeListener(mSeekListener);
        mProgress.setMax(1000);
        mPauseButton.setOnClickListener(mPauseListener);
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        mShowing = true;
        hide();
    }


    /**
     * By Lin Date:2016/8/17  18:16
     *
     * @注释：点击全景开关按钮
     */
    @OnClick(R.id.video_toolbar_btn_gyro_iv)
    void video_toolbar_btn_gyro_iv() {
        // 全景
        if (getMDVRLibrary() != null && mIsCanEnableGeneral) {
            mGeneralEnable = !mGeneralEnable;
            getMDVRLibrary().switchInteractiveMode(mContext, mGeneralEnable ? MDVRLibrary.INTERACTIVE_MODE_MOTION : MDVRLibrary.INTERACTIVE_MODE_TOUCH);
            getMDVRLibrary().switchProjectionMode(mContext, mGeneralEnable ? mProjectionModeDefault : MDVRLibrary.PROJECTION_MODE_PLANE_FULL);
            refreshButtonView();
        }
        show();
    }


    /**
     * 测试代码，主要做视频截取功能。
     *@author DuanChunLin
     *@time 2016/11/29 18:56
     */
//    @OnClick(R.id.video_jie_ping_btn)
//    void video_jie_ping_btn(){
//        if (getMDVRLibrary() != null){
//            getMDVRLibrary().captureBitmap(new CaptureBitmapGL.BitmapReadyCallbacks() {
//                @Override
//                public void onBitmapReady(Bitmap bitmap) {
//                    CacheUtil.getInstance().putImageToCache(bitmap, "123456789", new CacheCallBack() {
//                        @Override
//                        public void onBefore(Bitmap bitmap) {
//                        }
//                        @Override
//                        public void onAfter(final String path) {
//                            Log.d("TAG","11111111path:"+path);
//                            mContext.runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                     Toast.makeText(mContext,"save path"+path,Toast.LENGTH_SHORT).show();
//                                    Log.d("TAG","11111111111111111path:"+path);
//                                }
//                            });
//                        }
//                    });
//                }
//            });
//        }
//    }

    private MDVRLibrary getMDVRLibrary(){
        if(mVRContorl!=null){
            return mVRContorl.getMDVRLibrary();
        }
        return null;
    }

    /**
     * By Lin Date:2016/8/17  18:15
     *
     * @注释：点击 单双屏开关按钮
     */
    @OnClick(R.id.video_toolbar_btn_screen_iv)
    void video_toolbar_btn_screen_iv() {
        //分屏
        if (getMDVRLibrary() != null) {
            mSplitScreenState = mSplitScreenState + 1;
            if (mSplitScreenState >= IMediaPlayerVRControl.VIDEO_SPLIT_MAX) {
                mSplitScreenState = IMediaPlayerVRControl.VIDEO_SPLIT_SCREEN_NO;
            }
            getMDVRLibrary().switchDisplayMode(mContext, getSwitchDisplayMode());
            if (onVideoDisPlayListener != null) {
                onVideoDisPlayListener.onDisPlayMode(getDisplayMode());
            }
            refreshButtonView();
        }
        show();
    }

    private int getSwitchDisplayMode() {
        return ((mSplitScreenState == IMediaPlayerVRControl.VIDEO_SPLIT_LEFT_RIGHT ) && isImmersionModel()) ? MDVRLibrary.DISPLAY_MODE_GLASS : MDVRLibrary.DISPLAY_MODE_NORMAL;
    }

    private int getDisplayMode() {
        return ((mSplitScreenState == IMediaPlayerVRControl.VIDEO_SPLIT_LEFT_RIGHT ) && isImmersionModel()) ? MEDIA_DISPLAY_IMMERSION_ON : MEDIA_DISPLAY_IMMERSION_OFF;
    }

    /**
     * By Lin Date:2016/8/17  18:15
     *
     * @注释：刷新 按钮状态
     */
    public void refreshButtonView() {
        if (mGeneralEnable) {
            media_gyro_btn.setBackgroundResource(R.drawable.gyroscope_btn_on);
        } else {
            media_gyro_btn.setBackgroundResource(R.drawable.gyroscope_btn_off);
        }

        if (mSplitScreenState == IMediaPlayerVRControl.VIDEO_SPLIT_SCREEN_NO) {
            media_screen_btn.setBackgroundResource(R.drawable.screen_btn_on);
        } else {
            media_screen_btn.setBackgroundResource(R.drawable.screen_btn_off); //需要美术出图
        }
        if(mPlayMode != IMediaPlayerVRControl.VIDEO_TYPE_L_R_3D)
        {
            media_screen_btn.setVisibility(isImmersionModel() ? View.VISIBLE : View.INVISIBLE);
            media_gyro_btn.setVisibility(isImmersionModel() ? View.VISIBLE : View.INVISIBLE);
        }
    }

    private void attachImmersionModelView(){
        if(mIsImmersionModel){
            view_media_immersion_model.setCompoundDrawablesWithIntrinsicBounds(R.drawable.media_surface_vertical_btn,0,0,0);
        }
        else{
            view_media_immersion_model.setCompoundDrawablesWithIntrinsicBounds(R.drawable.media_surface_horizontal_btn,0,0,0);
        }
    }

    public boolean isImmersionModel() {
        return mIsImmersionModel;
    }

    /**
     * @author 段春林
     * @time 2016/9/6 15:11
     * 设置沉浸模式，true 沉浸 false 非沉浸
     */
    public void setImmersionModel(boolean immersionModel) {
        mIsImmersionModel = immersionModel;
        attachVRControl();
        attachImmersionModelView();
    }

    /**
     * By Lin Date:2016/8/17  18:16
     *
     * @注释：设置播放模式
     */
    public void setPlayMode(int playMode) {
        mPlayMode = playMode;
        attachVRControl();
    }

    public View getView() {
        return mView;
    }

    @Override
    public void hide() {
        if (mShowing) {
            video_play_tools_controller_rl.setVisibility(View.GONE);
            mShowing = false;
            mHandler.removeMessages(SHOW_PROGRESS);
        }
    }

    @Override
    public boolean isShowing() {
        return mShowing;
    }

    @Override
    public void setEnabled(boolean enabled) {
    }

    public long getCurrentPlayTime() {
        return currentPlayTime;
    }

    /**
     * By Lin Date:2016/8/17  18:18
     *
     * @注释：设置播放器 控制器
     */
    @Override
    public void setMediaPlayer(MediaPlayerControl player) {
        mPlayer = player;
        updatePausePlay();
    }

    /**
     * By Lin Date:2016/8/17  18:17
     *
     * @注释：设置 VR控制器。 设置全景播放 单双屏
     */
    @Override
    public void setVRControl(IMediaPlayerVRControl vrControl) {
        if(!vrControl.isBindVRLibrary()){
            vrControl.bindVRLibrary(mContext);
        }
        mVRContorl = vrControl;
        attachVRControl();
    }

    /**
     * By Lin Date:2016/8/17  18:16
     *
     * @注释：设置视频播放模式
     */
    private void attachVRControl() {
        if (getMDVRLibrary() == null) {
            return;
        }
        switch (mPlayMode) {
            default:
            case IMediaPlayerVRControl.VIDEO_TYPE_NORMAL: //普通视频
                mGeneralEnable = true;
                mIsCanEnableGeneral = true;
                mDisplayModeDefault = MDVRLibrary.DISPLAY_MODE_GLASS;
                mProjectionModeDefault = MDVRLibrary.PROJECTION_MODE_SPHERE;
                mSplitScreenState = IMediaPlayerVRControl.VIDEO_SPLIT_LEFT_RIGHT;  //默认左右分屏
                break;
            case IMediaPlayerVRControl.VIDEO_TYPE_L_R_3D://左右3D
                mGeneralEnable = false;
                mIsCanEnableGeneral = false;
                mProjectionModeDefault = MDVRLibrary.PROJECTION_MODE_PLANE_FULL;
                mDisplayModeDefault = MDVRLibrary.DISPLAY_MODE_NORMAL;
                mSplitScreenState = IMediaPlayerVRControl.VIDEO_SPLIT_LEFT_RIGHT;
                media_gyro_btn.setVisibility(View.GONE);
                media_screen_btn.setVisibility(View.GONE);
                break;
            case IMediaPlayerVRControl.VIDEO_TYPE_ONE_GV://单屏全景
                mGeneralEnable = true;
                mIsCanEnableGeneral = true;
                mProjectionModeDefault = MDVRLibrary.PROJECTION_MODE_SPHERE;
                mDisplayModeDefault = MDVRLibrary.DISPLAY_MODE_GLASS;
                mSplitScreenState = IMediaPlayerVRControl.VIDEO_SPLIT_LEFT_RIGHT;  //默认左右分屏
                break;
            case IMediaPlayerVRControl.VIDEO_TYPE_U_D_3D://上下3D
                mGeneralEnable = true;
                mIsCanEnableGeneral = false;
                mSplitScreenState = IMediaPlayerVRControl.VIDEO_SPLIT_LEFT_RIGHT;
                mProjectionModeDefault = MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE_VERTICAL;
                mDisplayModeDefault = MDVRLibrary.DISPLAY_MODE_GLASS;
                break;
            case IMediaPlayerVRControl.VIDEO_TYPE_U_D_GV://上下3d全景
                mGeneralEnable = true;
                mIsCanEnableGeneral = true;
                mSplitScreenState = IMediaPlayerVRControl.VIDEO_SPLIT_LEFT_RIGHT;
                mProjectionModeDefault = MDVRLibrary.PROJECTION_MODE_STEREO_SPHERE_VERTICAL;
                mDisplayModeDefault = MDVRLibrary.DISPLAY_MODE_GLASS;
                break;
        }
        getMDVRLibrary().switchInteractiveMode(mContext, isImmersionModel() ? MDVRLibrary.INTERACTIVE_MODE_MOTION : MDVRLibrary.INTERACTIVE_MODE_MOTION_WITH_TOUCH);
        getMDVRLibrary().switchProjectionMode(mContext, mProjectionModeDefault);
        getMDVRLibrary().switchDisplayMode(mContext, isImmersionModel() ? mDisplayModeDefault : MDVRLibrary.DISPLAY_MODE_NORMAL); //非沉浸模式 设置单屏
        if (onVideoDisPlayListener != null) {
            onVideoDisPlayListener.onDisPlayMode(getDisplayMode());
        }
        refreshButtonView();
    }

    /**
     * By Lin Date:2016/8/17  18:18
     *
     * @注释：设置播放器 暂停延迟更新时间。本地播放器不需要延迟，第三方需要
     */
    @Override
    public void setShowPausePlayDelayTime(int delayTime) {
        this.showPausePlayDelayTime = delayTime;
    }

    /**
     * By Lin Date:2016/8/17  18:19
     *
     * @注释：展示seek状态
     */
    @Override
    public void showSeekSet(boolean forwardOrBack, int time) {
        player_convenient_prompt_ll.setVisibility(View.VISIBLE);
        if (forwardOrBack) {
            player_convenient_prompt_iv.setBackgroundResource(R.drawable.play_video_forward);
        } else {
            player_convenient_prompt_iv.setBackgroundResource(R.drawable.play_video_backoff);
        }
        player_convenient_prompt_tv.setText(stringForTime(time) + "/" + stringForTime(mPlayer.getDuration()));
        autoHideConvenientPrompt();
    }

    /**
     * By Lin Date:2016/8/17  18:19
     *
     * @注释：展示声音设置状态
     */
    @Override
    public void showVolumeSet(int vol) {
        player_convenient_prompt_ll.setVisibility(View.VISIBLE);
        player_convenient_prompt_iv.setBackgroundResource(R.drawable.player_video_sound);
        player_convenient_prompt_tv.setText(Integer.toString(vol) + "%");
        autoHideConvenientPrompt();
    }

    /**
     * By Lin Date:2016/8/17  18:20
     *
     * @注释：展示亮度设置状态
     */
    @Override
    public void showBrightnessSet(int brightness) {
        player_convenient_prompt_ll.setVisibility(View.VISIBLE);
        player_convenient_prompt_iv.setBackgroundResource(R.drawable.player_video_liang);
        player_convenient_prompt_tv.setText(Integer.toString(brightness) + "%");
        autoHideConvenientPrompt();
    }

    /**
     * 自动隐藏声音和亮度显示
     */
    private void autoHideConvenientPrompt() {
        mHandler.removeMessages(HIDE_CONVENIENT_PROMPT);
        mHandler.sendEmptyMessageDelayed(HIDE_CONVENIENT_PROMPT, 500);
    }

    private void hideConvenientPrompt() {
        player_convenient_prompt_ll.setVisibility(View.GONE);
    }

    /**
     * By Lin Date:2016/8/17  18:20
     *
     * @注释：显示播放器UI
     */
    @Override
    public void show(int timeout) {
        if (!mShowing) {
            setProgress();
            video_play_tools_controller_rl.setVisibility(View.VISIBLE);
            mShowing = true;
        }
        updatePausePlay();
        mHandler.sendEmptyMessage(SHOW_PROGRESS);
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            Message msg = mHandler.obtainMessage(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }

    /**
     * By Lin Date:2016/8/17  18:20
     *
     * @注释：显示播放器UI
     */
    @Override
    public void show() {
        show(sDefaultTimeout);
    }

    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
                case SHOW_PROGRESS:
                    currentPlayTime = setProgress();
                    if (!mDragging && mShowing && mPlayer != null && mPlayer.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (currentPlayTime % 1000));
                    }
                    break;
                case SHOW_PAUSE_PLAY_STATE:
                    updatePausePlay();
                    break;
                case HIDE_CONVENIENT_PROMPT:
                    hideConvenientPrompt();
                    break;
            }
        }
    };

    private final View.OnClickListener mPauseListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            doPauseResume();
            show(sDefaultTimeout);
        }
    };

    /**
     * By Lin Date:2016/8/17  18:21
     *
     * @注释：暂停或者播放
     */
    private void doPauseResume() {
        if (mPlayer.isPlaying()) {
            mPlayer.pause();
        } else {
            mPlayer.start();
        }
        updatePausePlay(showPausePlayDelayTime);
    }

    public void setLockPause(boolean lockPause) {
        if (mPlayer != null) {
            if (lockPause) {
                mPlayer.pause();
            } else {
                mPlayer.start();
            }
        }
        updatePausePlay(showPausePlayDelayTime);
    }


    private final SeekBar.OnSeekBarChangeListener mSeekListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onStartTrackingTouch(SeekBar bar) {
            show(3600000);
            mDragging = true;
            // By removing these pending progress messages we make sure
            // that a) we won't update the progress while the user adjusts
            // the seekbar and b) once the user is done dragging the thumb
            // we will post one of these messages to the queue again and
            // this ensures that there will be exactl
            // y one message queued up.
            mHandler.removeMessages(SHOW_PROGRESS);
        }

        @Override
        public void onProgressChanged(SeekBar bar, int progress, boolean fromuser) {
            if (!fromuser) {
                // We're not interested in programmatically generated changes to
                // the progress bar's position.
                return;
            }

            long duration = mPlayer.getDuration();
            long newPosition = (duration * progress) / 1000L;
            mPlayer.seekTo((int) newPosition);

            if (video_toolbar_time_tv != null) {
                video_toolbar_time_tv.setText(stringForTime((int) newPosition) + "/" + stringForTime((int) duration));
            }
        }

        @Override
        public void onStopTrackingTouch(SeekBar bar) {
            mDragging = false;
            setProgress();
            updatePausePlay();
            show(sDefaultTimeout);
            // Ensure that progress is properly updated in the future,
            // the call to show() does not guarantee this because it is a
            // no-op if we are already showing.
            mHandler.sendEmptyMessage(SHOW_PROGRESS);
        }
    };

    /**
     * By Lin Date:2016/8/17  18:21
     *
     * @注释：显示暂停状态
     */
    private void updatePausePlay() {
        if (mPlayer != null && (mPlayer.isPlaying()||mPlayer.canPause()) && !mPlayer.isCompletion()) {
            mPauseButton.setBackgroundResource(R.drawable.media_play_pause_btn);
        } else {
            mPauseButton.setBackgroundResource(R.drawable.media_play_play_btn);
        }
    }

    /**
     * By Lin Date:2016/8/17  18:22
     *
     * @注释：延迟显示暂停状态
     */
    private void updatePausePlay(int delayTime) {
        if (delayTime > 0) {
            mHandler.removeMessages(SHOW_PAUSE_PLAY_STATE);
            Message msg = mHandler.obtainMessage(SHOW_PAUSE_PLAY_STATE);
            mHandler.sendMessageDelayed(msg, delayTime);
        } else {
            updatePausePlay();
        }
    }

    /**
     * By Lin Date:2016/8/17  18:22
     *
     * @注释：设置进度
     */
    private int setProgress() {
        if (mPlayer == null || mDragging) {
            return 0;
        }
        int position = mPlayer.getCurrentPosition();
        int duration = mPlayer.getDuration();
        if(mPlayer.isCompletion() || position > duration ){
            position = duration;
        }
        if (mProgress != null) {
            if (duration > 0) {
                // use long to avoid overflow
                long pos = 1000L * position / duration;
                mProgress.setProgress((int) pos);
            }
            int percent = mPlayer.getBufferPercentage();
            mProgress.setSecondaryProgress(percent*10);
        }

        if (video_toolbar_time_tv != null) {
            video_toolbar_time_tv.setText(stringForTime(position) + "/" + stringForTime(duration));
        }
        return position;
    }

    /**
     * By Lin Date:2016/8/17  18:22
     *
     * @注释：时间格式化
     */
    private String stringForTime(int timeMs) {
        int totalSeconds = timeMs / 1000;
        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    /**
     * By Lin Date:2016/8/17  18:22
     *
     * @注释：监听播放模式改变状态
     */
    public void setOnVideoDisPlayListener(OnVideoDisPlayListener onVideoDisPlayListener) {
        this.onVideoDisPlayListener = onVideoDisPlayListener;
    }

    public interface OnVideoDisPlayListener {
        void onDisPlayMode(int mode);
    }
}
