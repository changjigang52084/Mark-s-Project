package com.xunixianshi.vrshow.player;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.MLog;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xnxs.mediaplayer.widget.media.VRVideoView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseNetFra;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.interfaces.ReceiverNetworkInterface;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.player.controller.VRMediaController;
import com.xunixianshi.vrshow.player.object.VideoInfo;
import com.xunixianshi.vrshow.receiver.ConnectionChangeReceiver;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by duan on 2016/8/27.
 * 播放fragment碎片。依赖播放资源ID
 */
public class VRPlayerFragment extends BaseNetFra {

    private static final String VIDEO_INFO_KEY = "videoInfo";
    private static final int VIEW_MEDIA_OPEN_SHOW_LOADING = 1;
    private static final int VIEW_MEDIA_OPEN_SHOW_ERROR = 2;
    private static final int VIEW_MEDIA_OPEN_CLOSE = 3;

    @Bind(R.id.vr_video_view)
    VRVideoView mVRVideoView;

    @Bind(R.id.video_player_ui_ll)
    RelativeLayout video_player_ui_ll;

//    @Bind(R.id.include_gridView1)
//    RelativeLayout include_gridView1;
//
//    @Bind(R.id.include_gridView2)
//    RelativeLayout include_gridView2;

    @Bind(R.id.view_media_play_loading_ll)
    LinearLayout view_media_play_loading_ll;

    @Bind(R.id.player_loading_left_iv)
    ImageView player_loading_left_iv;

    @Bind(R.id.player_loading_right_iv)
    ImageView player_loading_right_iv;

    @Bind(R.id.view_media_play_open_up_one)
    RelativeLayout view_media_play_open_up_one;

    @Bind(R.id.view_media_play_open_up_two)
    RelativeLayout view_media_play_open_up_two;

    @Bind(R.id.view_media_immersion_model)
    Button view_media_immersion_model;

    private VRMediaController vrMediaController;
//    private GridViewController gridViewController;
    private VideoInfo mVideoInfo;
    private int mIsPlayEnd = -1;

    private boolean isImmersion; //是否是沉浸模式

    private boolean isShowLoading = false;
    private boolean readyMediaOver = false;
    private NetWorkDialog netWorkDialog;
    private Animation operatingAnim;
    private boolean mIsCanConfigurationChanged = false;

    private boolean mChangeScreenForBtn;

    private boolean mIsStartPlay = false;

    private OrientationSensorListener mOrientationSensorListener;

    private CancelPlayerListener mCancelPlayerListener;

    private SensorManager mSensorManager;
    private Sensor mSensor;

    private MediaPlayOpenUpHolder mMediaPlayOpenUpHolderOne;
    private MediaPlayOpenUpHolder mMediaPlayOpenUpHolderTow;

    public static VRPlayerFragment build() {
        return new VRPlayerFragment();
    }

    /**
     * @author 段春林
     * @time 2016/9/6 18:32
     * 设置是否可以旋转屏幕 ，如果可以旋转就设置为 非沉浸模式 不可旋转就设置沉浸模式
     */
    public VRPlayerFragment setCanConfigurationChanged(boolean canConfigurationChanged) {
        mIsCanConfigurationChanged = canConfigurationChanged;
        mChangeScreenForBtn = true;
        return this;
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_vr_player, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView(View parentView) {
        // init player
        IjkMediaPlayer.loadLibrariesOnce(null);
        IjkMediaPlayer.native_profileBegin("libijkplayer.so");
    }

    @Override
    protected void initData() {
        mMediaPlayOpenUpHolderOne = new MediaPlayOpenUpHolder(view_media_play_open_up_one);
        mMediaPlayOpenUpHolderTow = new MediaPlayOpenUpHolder(view_media_play_open_up_two);
        operatingAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.dialog_loading_animation);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        if (getArguments() != null) {
            setVideoInfo((VideoInfo) getArguments().getSerializable(VIDEO_INFO_KEY));
        }
		initMediaPlayer();
        if(mIsCanConfigurationChanged){
            mSensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
            mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            mOrientationSensorListener = new OrientationSensorListener(mHandler);
            mSensorManager.registerListener(mOrientationSensorListener, mSensor, SensorManager.SENSOR_DELAY_UI);
        }

        Log.d("TAG","initData end time:"+ System.currentTimeMillis());
    }



    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if(getActivity()==null ){return;}
            switch (msg.what){
                case 888:
                    if(!mIsStartPlay){ break;}
                    int orientation = msg.arg1;
                    if (orientation>45&&orientation<135 && !mChangeScreenForBtn ) {
                        if( getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE){
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE); //倒横屏
                        }
                    } else if(orientation>135&&orientation<225){ //倒竖
                        mChangeScreenForBtn = false;
//                        if(getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
//                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
//                        }
                    }
                    else if (orientation>225&&orientation<315 && !mChangeScreenForBtn ){  //横屏
                        if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE){
                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                        }
                    }
                    else if(orientation>315&&orientation<360){//竖屏
                        mChangeScreenForBtn = false;
//                        if(getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT){
//                            getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        }
                    }
                    break;
            }
        }
    };

    /**
     * @author 段春林
     * @time 2016/9/6 14:57
     * 准备播放，没有播放地址，获取播放地址后开始
     */
    public void readyToPlay(int sourceId, String videoName) {
        if (mVideoInfo != null) {
            mVRVideoView.stopPlayback();
            mVRVideoView.release(true);
        }
        mVideoInfo = new VideoInfo();
        mVideoInfo.setSourceId(sourceId);

        mVideoInfo.setVideoName(videoName);
        mVideoInfo.setNeedNet(true);
        if(vrMediaController!=null){
            readyToPlay();
        }
    }


    /**
     * @author 段春林
     * @time 2016/9/6 14:57
     * 准备播放，没有播放地址，获取播放地址后开始
     */
    public void readyToPlay(int sourceId, String videoName, CancelPlayerListener cancelPlayerListener) {
        mCancelPlayerListener = cancelPlayerListener;
        if (mVideoInfo != null) {
            mVRVideoView.stopPlayback();
            mVRVideoView.release(true);
        }
        mVideoInfo = new VideoInfo();
        mVideoInfo.setSourceId(sourceId);

        mVideoInfo.setVideoName(videoName);
        mVideoInfo.setNeedNet(true);

        if(vrMediaController!=null) {
            readyToPlay();
        }
    }

    /**
     * @author 段春林
     * @time 2016/9/6 14:56
     * 开始播放 ，不需要網絡 暂时不需要检测是否在wifi 网络状态下
     */
    public void readyToPlay() {
        if (mVideoInfo == null) {
            showToastMsg("播放错误!");
            return;
        }
        isShowLoading = false;
        mIsPlayEnd = -1;
        if (mVideoInfo.isNeedNet()) {//不是wifi
            if (getContext() != null && NetWorkDialog.getIsShowNetWorkDialog(getContext())) {
                showNetWorkDialog();
            } else {
                getPlayAddress(mVideoInfo.getSourceId());
            }
            addReceiverNetwork();
        } else {
            setMediaPlayInformation(mVideoInfo);
        }
        attachMediaOpenView(VIEW_MEDIA_OPEN_SHOW_LOADING);
        attachLoading();
    }

    public void setVideoInfo(VideoInfo videoInfo) {
        mVideoInfo = videoInfo;
    }

    private void initMediaPlayer() {
        /**
         *By Lin Date:2016/8/15  17:52
         *@注释：播放器初始化
         */
        //播放器控制类
        vrMediaController = new VRMediaController(getActivity(), video_player_ui_ll);
        vrMediaController.setOnVideoDisPlayListener(disPlayListener);
        setImmersionModel(!mIsCanConfigurationChanged);

        mVRVideoView.setOnInfoListener(mOnInfoListener);
        mVRVideoView.setOnErrorListener(onErrorListener);
        mVRVideoView.setOnPreparedListener(onPreparedListener);
        mVRVideoView.setOnCompletionListener(onCompletionListener);
        mVRVideoView.setMediaController(vrMediaController);
        mVRVideoView.enableBrightnessAudioVolume(getActivity().getWindow());
        if (mVideoInfo != null) {
            readyToPlay();
        }
//        gridViewController = new GridViewController(getActivity());
//        gridViewController.initPlayLayoutView(include_gridView1, include_gridView2);
//        gridViewController.showGridView(false);
    }

    /**
     * @author 段春林
     * @time 2016/9/6 15:12
     * 显示监听，监听沉浸开关，非沉浸不需要双屏操作界面
     */
    private VRMediaController.OnVideoDisPlayListener disPlayListener = new VRMediaController.OnVideoDisPlayListener() {
        @Override
        public void onDisPlayMode(int mode) {
            switch (mode) {
                case VRMediaController.MEDIA_DISPLAY_IMMERSION_ON:
//                    if (gridViewController != null) {
//                        include_gridView1.setVisibility(View.VISIBLE);
//                        include_gridView2.setVisibility(View.VISIBLE);
//                        gridViewController.enable();
//                    }
                    isImmersion = true;
                    break;
                case VRMediaController.MEDIA_DISPLAY_IMMERSION_OFF:
//                    if (gridViewController != null) {
//                        include_gridView1.setVisibility(View.GONE);
//                        include_gridView2.setVisibility(View.GONE);
//                        gridViewController.disable();
//                    }
                    isImmersion = false;
                    break;
            }
            attachLoading();
        }
    };

    /**
     * 播放状态监听
     *@author DuanChunLin
     *@time 2017/1/4 11:44
     */
    private IMediaPlayer.OnInfoListener mOnInfoListener = new IMediaPlayer.OnInfoListener() {
        @Override
        public boolean onInfo(IMediaPlayer mp, int what, int extra) {
            switch (what) {
                case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                    attachMediaOpenView(VIEW_MEDIA_OPEN_CLOSE);
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                    //开始缓冲
                    if(view_media_play_open_up_one.getVisibility() != View.VISIBLE){
                        isShowLoading = true;
                        attachLoading();
                    }
                    break;
                case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                    //缓冲结束
                    isShowLoading = false;
                    attachLoading();
                    break;
                case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                case IMediaPlayer.MEDIA_INFO_TIMED_TEXT_ERROR:
                    showPlayError("连接超时");
                    break;
            }
            return false;
        }
    };

    /**
     * 播放器异步初始化回掉接口
     *@author DuanChunLin
     *@time 2017/1/4 11:45
     */
    private IMediaPlayer.OnPreparedListener onPreparedListener = new IMediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(IMediaPlayer mp) {
            readyMediaOver = true;
            attachStart();
        }
    };


    /**
     * 播放错误监听，当前只显示错误信息
     *@author DuanChunLin
     *@time 2017/1/4 11:42
     */
    private IMediaPlayer.OnErrorListener onErrorListener = new IMediaPlayer.OnErrorListener() {
        @Override
        public boolean onError(IMediaPlayer mp, int what, int extra) {
            //显示错误
            showPlayError(String.valueOf(what).toUpperCase());
            return true; //拦截错误，并处理
        }
    };


    /**
     * 播放完成监听
     *@author DuanChunLin
     *@time 2017/1/4 11:43
     */
    private IMediaPlayer.OnCompletionListener onCompletionListener = new IMediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(IMediaPlayer mp) {
            mIsPlayEnd = 1;
            if(vrMediaController!=null){
                vrMediaController.show();
            }
            onBackPressed();
        }
    };

    /**
     * 注册网络切换监听
     *@author DuanChunLin
     *@time 2017/1/4 11:43
     */
    private void addReceiverNetwork() {
        registerReceiverNetworkInterface(new ReceiverNetworkInterface() {
            @Override
            public void NetworkChanged(int state) {
                if (state != ConnectionChangeReceiver.NET_STATE__CONNECTED_WIFI && mVideoInfo.isNeedNet()) {
                    showNetWorkDialog();
                }
            }
        });
    }

    /**
     * 打开视频，根据type值显示相应的界面
     *@author DuanChunLin
     *@time 2017/1/4 10:51
     */
    private void attachMediaOpenView(int type) {
        switch (type) {
            case VIEW_MEDIA_OPEN_SHOW_LOADING:
                mMediaPlayOpenUpHolderOne.startAnimation();
                mMediaPlayOpenUpHolderTow.startAnimation();
                view_media_play_open_up_one.setVisibility(View.VISIBLE);
                view_media_play_open_up_two.setVisibility(View.VISIBLE);
                mMediaPlayOpenUpHolderOne.setMediaDescTest(String.format(getActivity().getResources().getString(R.string.media_play_soon), mVideoInfo.getVideoName()));
                mMediaPlayOpenUpHolderTow.setMediaDescTest(String.format(getActivity().getResources().getString(R.string.media_play_soon), mVideoInfo.getVideoName()));
                break;
            case VIEW_MEDIA_OPEN_SHOW_ERROR:
                mMediaPlayOpenUpHolderOne.stopAnimation();
                mMediaPlayOpenUpHolderTow.stopAnimation();
                view_media_play_open_up_one.setVisibility(View.INVISIBLE);
                view_media_play_open_up_two.setVisibility(View.INVISIBLE);
                mMediaPlayOpenUpHolderOne.setMediaDescTest(mVideoInfo.getErrorMsg());
                mMediaPlayOpenUpHolderTow.setMediaDescTest(mVideoInfo.getErrorMsg());
                break;
            case VIEW_MEDIA_OPEN_CLOSE:
                view_media_play_open_up_one.setVisibility(View.GONE);
                view_media_play_open_up_two.setVisibility(View.GONE);
                break;
        }
        if(mIsCanConfigurationChanged && !vrMediaController.isImmersionModel()){
            view_media_play_open_up_two.setVisibility(View.GONE);
        }
        if(!mIsCanConfigurationChanged){
            view_media_immersion_model.setVisibility(View.GONE);
        }
    }




    /**
     * @author 段春林
     * @time 2016/9/6 11:20
     * 横竖屏监听
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mIsCanConfigurationChanged) {
            switch (newConfig.orientation) {
                case Configuration.ORIENTATION_PORTRAIT:
                    setImmersionModel(false);
                    break;
                case Configuration.ORIENTATION_LANDSCAPE:
                    setImmersionModel(true);
                    break;
            }
        }
        attachMediaOpenView(0);
    }

    /**
     * 返回按钮点击事件
     *@author DuanChunLin
     *@time 2017/1/4 10:53
     */
    @OnClick(R.id.player_back_bt)
    void player_back_bt_click() {
        if(mIsCanConfigurationChanged){
            if(getActivity().getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
                mChangeScreenForBtn = true;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
            else{
                getActivity().finish();
            }
        }
        else {
            getActivity().finish();
        }
    }

    /**
     * 提供给上层调用的返回方法（Activity）
     *@author DuanChunLin
     *@time 2017/1/4 10:54
     */
    public void onBackPressed(){
        player_back_bt_click();
    }


    /**
     * 横竖屏切换按钮 mIsCanConfigurationChanged 控制能否切换
     *@author DuanChunLin
     *@time 2017/1/4 10:54
     */
    @SuppressLint("SourceLockedOrientationActivity")
    @OnClick(R.id.view_media_immersion_model)
    void view_media_immersion_model_click() {
        if (mIsCanConfigurationChanged) {
            if (getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT || getActivity().getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT) {
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            } else {
                mChangeScreenForBtn = true;
                getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            }
        }
    }

    /**
     * @author 段春林
     * @time 2016/9/6 15:09
     * 设置沉浸模式 true 沉浸 false 非沉浸
     */
    public void setImmersionModel(boolean enable) {
        if (vrMediaController != null)
            vrMediaController.setImmersionModel(enable);
    }

    private void showNetWorkDialog() {
        if (netWorkDialog != null && netWorkDialog.isCheckOkBtn()) {
            return;
        }
        vrMediaController.setLockPause(true);
        if (netWorkDialog == null) {
            netWorkDialog = new NetWorkDialog(getActivity());
        }
        netWorkDialog.showNetWorkChangePlayerVideoWarning(new NetWorkDialogButtonListener() {
            @Override
            public void okClick() {//继续

                vrMediaController.setLockPause(false);
                if (StringUtil.isEmpty(mVideoInfo.getVideoUrl())) {
                    getPlayAddress(mVideoInfo.getSourceId());
                }
            }

            @Override
            public void cancelClick() {//取消
                if (mCancelPlayerListener != null) {
                    mCancelPlayerListener.cancel();
                }
            }
        });
    }

    private void showPlayError(String msg) {
        //显示错误信息
        mVideoInfo.setErrorMsg(String.format(getActivity().getResources().getString(R.string.media_play_error), msg));
        attachMediaOpenView(VIEW_MEDIA_OPEN_SHOW_ERROR);
    }

    @Bind(R.id.player_video_name_tv)
    TextView player_video_name_tv;

    public void setVideoTile(String videoName) {
        player_video_name_tv.setText(videoName);
    }

    @Override
    public void onStop() {
        super.onStop();
        mVRVideoView.onStop();
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void onPause() {
        super.onPause();
        mVRVideoView.onPause();
//        if (gridViewController != null) {
//            gridViewController.disable();
//        }
        if(mSensorManager!=null) {
            mSensorManager.unregisterListener(mOrientationSensorListener);
        }
        recordPlayProgress(mIsPlayEnd);
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    public void onResume() {
        super.onResume();
        mVRVideoView.onResume();
//        if (gridViewController != null) {
//            gridViewController.enable();
//        }
        attachLoading();
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if(mSensorManager!=null){
            mSensorManager.registerListener(mOrientationSensorListener,mSensor,SensorManager.SENSOR_DELAY_UI);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiverNetworkInterface();
        mVRVideoView.onDestroy();
//        if (gridViewController != null) {
//            gridViewController.onDestroy();
//            gridViewController = null;
//        }
        vrMediaController = null;
        mHandler.removeMessages(888);
        mHandler = null;
        IjkMediaPlayer.native_profileEnd();
    }

    private void attachStart() {
        if (StringUtil.isEmpty(mVideoInfo.getVideoUrl()) || !readyMediaOver) {
            return;
        }
        mIsPlayEnd = 0;
        mIsStartPlay = true;
        mVRVideoView.start();
    }

    private void setMediaPlayInformation(VideoInfo videoInfo) {
        try {
//            VRPlayerFragment.this.getActivity().getSupportFragmentManager().beginTransaction().show(VRPlayerFragment.this);
            setVideoTile(videoInfo.getVideoName());
            vrMediaController.setPlayMode(videoInfo.getVideoType());
            mVRVideoView.setVideoPath(videoInfo.getVideoUrl());
//            mVRVideoView.setVideoPath("http://xnxs.test2.vrshow.com/o_1b54acg2v1gqf11a21qqudfuugll.mov");
            mVRVideoView.setLastProgress(videoInfo.getLastProgress());
        } catch (Exception e) {
            showPlayError("未知错误");
        }
        attachStart();
    }


    private void attachLoading() {
        if (isShowLoading) {
            view_media_play_loading_ll.setVisibility(View.VISIBLE);
            player_loading_left_iv.startAnimation(operatingAnim);

            if(isImmersion){
                player_loading_right_iv.setVisibility(View.VISIBLE);
                player_loading_right_iv.startAnimation(operatingAnim);
            }
            else {
                player_loading_right_iv.setVisibility(View.GONE);
            }
        } else {
            view_media_play_loading_ll.setVisibility(View.GONE);
            player_loading_left_iv.clearAnimation();
            player_loading_right_iv.clearAnimation();
        }
    }

    /**
     * 获取播放地址
     */
    private void getPlayAddress(int sourceId) {
        String uid;
        if (AppContent.UID.equals("")) {
            // uid = "-1";
            uid = "2147483647";
        } else {
            uid = AppContent.UID;
        }
        // 加密手机号
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourcesId", sourceId);
        hashMap.put("userId", Ras_uid);
        hashMap.put("urlType",1); //1是播放，2是下载
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/new/url/service", jsonData, new GenericsCallback<DownLoadAddressResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (isDestroy()) {
                    return;
                }
                showPlayError("网络异常");
            }
            @Override
            public void onResponse(DownLoadAddressResult result, int id) {
                if (isDestroy()) {
                    return;
                }
                if (result.getResCode().equals("0")) {
                    mVideoInfo.setVideoUrl(result.getUrl());
                    mVideoInfo.setVideoType(result.getResourcePlayType());
                    mVideoInfo.setLastProgress(result.getPlayTimes());
                    setMediaPlayInformation(mVideoInfo);
                } else {
                    showPlayError("未知错误");
                }
            }
        });
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: recordPlayProgress
     * @Description: TODO 记录播放位置
     * @author hechuang
     */
    private void recordPlayProgress(int isPlayEnd) {
        if (mVideoInfo == null || vrMediaController == null || mIsPlayEnd == -1) {
            return;
        }
        MLog.d("recordPlayProgress");
        if (AppContent.UID.equals("")) {
            SimpleSharedPreferences.putInt(mVideoInfo.getVideoName(), vrMediaController.getCurrentPlayTime(), getActivity());
        } else {
            // 加密用户ID
            String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
            // 封装参数
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("isPlayEnd", isPlayEnd);
            hashMap.put("playTimes", vrMediaController.getCurrentPlayTime());
            hashMap.put("sourceId", mVideoInfo.getSourceId());
            hashMap.put("userId", Ras_uid);
            // 转参数
            String jsonData = StringUtil.MapToJson(hashMap);
            OkHttpAPI.postObjectData("/user/play/history/add/service", jsonData, new HttpSuccess<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result, int id) {
                    MLog.d("添加播放历史完成");
                }
            }, new HttpError() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    MLog.d("添加播放历史失败，请检查网络！");
                }
            });
        }
    }

    public class MediaPlayOpenUpHolder{
        @Bind(R.id.view_media_loading)
        ImageView view_media_loading;

        @Bind(R.id.view_media_desc_test)
        TextView view_media_desc_test;
        public MediaPlayOpenUpHolder(View view){
            ButterKnife.bind(this,view);
        }
        public void startAnimation(){
            if (view_media_loading != null && view_media_loading.getBackground() != null) {
                ((AnimationDrawable) view_media_loading.getBackground()).start();
            }
        }
        public void stopAnimation(){
            if (view_media_loading != null && view_media_loading.getBackground() != null) {
                ((AnimationDrawable) view_media_loading.getBackground()).stop();
            }
        }
        public void setMediaDescTest(String descTest){
            view_media_desc_test.setText(descTest);
        }
    }

    public interface CancelPlayerListener {
        void cancel();
    }
}
