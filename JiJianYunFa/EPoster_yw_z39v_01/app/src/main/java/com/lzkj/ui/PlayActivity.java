package com.lzkj.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.ui.manager.FragmentsManager;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.play.interfaces.ISwitchLayoutListener;
import com.lzkj.ui.postern.PosternManager;
//import com.lzkj.ui.service.MyServer;
import com.lzkj.ui.util.ConfigSettings;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.FileStore;
//import com.lzkj.ui.util.HttpUrl;
import com.lzkj.ui.util.LayoutUtil;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.PreLoadImgTool;
import com.lzkj.ui.util.StorageList;
import com.lzkj.ui.util.StringUtil;
import com.lzkj.ui.util.VolumeUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.Date;

/**
 * 主界面
 *
 * @author changkai
 */
public class PlayActivity extends Activity implements ISwitchLayoutListener {

    private static final LogTag TAG = LogUtils.getLogTag(PlayActivity.class.getSimpleName(), true);
    public static final String TIP_MSG_KEY = "tipMsg";
    public static final String TIP_MSG_IS_SHOW_KEY = "isShow";
    public static final String IS_COPY_PROGRAM_KEY = "isCopyProgram";
    public static final String UPDATE_MSG_TIP_MSG_ACTION = "com.lzkj.ui.UPDATE_TIP_MSG_ACTION";
    public static PlayActivity mainActivity;
//    private File playCountFile = null;
//    private File playFile = new File("mnt/internal_sd/mallposter/playcount");

//    private Intent tanZhenIntent;

    /**
     * 管理fragment的类
     */
    private FragmentsManager mFragmentsManager;
    /**
     * 屏幕内容主布局控件
     **/
    private FrameLayout mainFrameLayoutView;
    /**
     * 后门管理器
     **/
    private PosternManager posternManager = null;
    private FrameLayout mianFlayout;
    private PlayHandler mHandler = new PlayHandler(this);
    private TextView mTipMsgTv;

    /**
     * 获取当前activity对象
     *
     * @return 返回当前的activity对象
     */
    public static PlayActivity getActivity() {
        return mainActivity;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupFullScreen();
        mainActivity = this;
        setContentView(R.layout.activity_main);
        initSdcardPath();
//        registerCountReceiver();
        registerTipReceiver();
        initView();
//        tanZhenIntent = new Intent(this, MyServer.class);
//        startService(tanZhenIntent);
    }

    private void setupFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void registerTipReceiver() {
        IntentFilter intentFilter = new IntentFilter(UPDATE_MSG_TIP_MSG_ACTION);
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(mUpdateTipMsgReceiver, intentFilter);
    }

    // 注册记录广告播放次数广告
//    private void registerCountReceiver() {
//        IntentFilter intentFilter = new IntentFilter("com.lzkj.aidlservice.AD_TRACKING");
//        registerReceiver(mCountAdReceiver, intentFilter);
//    }

    // 初始化sd卡路径
    private void initSdcardPath() {
        StorageList storageList = new StorageList(this);
        FileStore.getInstance().setSdcrad(storageList.getSDPath());
    }

    /**
     * 初始化view
     */
    private void initView() {
        mainFrameLayoutView = (FrameLayout) findViewById(R.id.main_container);
        mianFlayout = (FrameLayout) findViewById(R.id.mian_flayout);
        mTipMsgTv = (TextView) findViewById(R.id.tv_tip_msg);
        ProgramPlayManager.getInstance().setSwitchLayoutImpl(this);
        mFragmentsManager = new FragmentsManager(this, getFragmentManager(), mainFrameLayoutView);
        initAttrbute();
    }

    /**
     * 初始化对象
     */
    private void initAttrbute() {
        startRecoveryDownload();
        posternManager = PosternManager.get(new Runnable() {
            @Override
            public void run() {
                PlayActivity.this.finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        int width = LayoutUtil.getInstance().getRealDisplayWidth();
        int height = LayoutUtil.getInstance().getRealDisplayHeight();
        LogUtils.d(TAG, "onStart", "width: " + width + " ,height: " + height);
        mainFrameLayoutView.getLayoutParams().width = width;
        mianFlayout.getLayoutParams().height = height;
    }

    @Override
    protected void onResume() {
        super.onResume();
        VolumeUtil.setDeviceVolume("16");
        // 当前返回到EPoster播放器的时候把系统音量设置为默认音量
        LogUtils.d(TAG, "onResume", "onResume.");
//        SharedPreferences preferences = getSharedPreferences("play_count", MODE_PRIVATE);
//        ConfigSettings.countCompletion = preferences.getInt("countCompletion", 0);
//        LogUtils.d(TAG, "onResume", "ConfigSettings.countCompletion: " + ConfigSettings.countCompletion);
        checkDeviceIsAuther();
        // 播放节目列表
        ProgramPlayManager.getInstance().playProgramList("onResume");

    }

    private void checkDeviceIsAuther() {
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handlerNotDid();
            }
        }, 2000);
    }

    private void handlerNotDid() {
        if (StringUtil.isNullStr(ConfigSettings.getDid())) {
            Intent updateDidIntent = new Intent(Constants.UPDATE_DID_ACTION);
            sendBroadcast(updateDidIntent);
            finish();
        }
    }

    /**
     * 切换到垫片界面
     */
    @Override
    public void switchDefaultLayout(String alertMsg) {
        switchFragment(alertMsg, null);
    }

    /**
     * 切换播放指定的节目
     */
    @Override
    public void switchProgramLayout(final Program program) {
        switchFragment(null, program);
    }

    /**
     * 预加载节目
     */
    @Override
    public void preLoadProgram(Program program) {
        mFragmentsManager.preLoadProgram(program);
    }

    /**
     * 切换fragment
     *
     * @param alertMsg 提示信息
     * @param program  要播放的节目对象
     */
    private void switchFragment(final String alertMsg, final Program program) {

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                if (program != null) {
//                    String key = program.getKey();

//                    LogUtils.d(TAG, "switchFragment", "ConfigSettings.COUNT_URL_03:" + ConfigSettings.COUNT_URL_03 + "\r\n" + "ConfigSettings.countCompletion:" + ConfigSettings.countCompletion + "\r\n" + "key:" + key);

//                    if (ConfigSettings.COUNT_URL_03 != null && !"".equals(ConfigSettings.COUNT_URL_03) && key.equals(ConfigSettings.AD_KEY)) {
//
//                        new Handler().postDelayed(new Runnable() {
//                            public void run() {
//                                HttpUrl.playProgramByGet(ConfigSettings.COUNT_URL_03);
//
//                                Date date = new Date();
//                                if (!playFile.exists()) {
//                                    playFile.mkdirs();
//                                }
//                                playCountFile = new File("mnt/internal_sd/mallposter/playcount/" + ConfigSettings.AD_KEY + "-" + (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + ".txt");
//                                try {
//                                    if (!playCountFile.exists()) {
//                                        playCountFile = new File("mnt/internal_sd/mallposter/playcount/" + ConfigSettings.AD_KEY + "-" + (date.getYear() + 1900) + "-" + (date.getMonth() + 1) + "-" + date.getDate() + ".txt");
//                                        playCountFile.createNewFile();
//                                    }
//                                    SharedPreferences preferences = getSharedPreferences("play_count", MODE_PRIVATE);
//                                    SharedPreferences.Editor editor = preferences.edit();
//                                    editor.putInt("countCompletion", ConfigSettings.countCompletion);
//                                    editor.commit();
//                                    LogUtils.d(TAG, "switchFragment", "count : " + ConfigSettings.countCompletion);
//                                    FileStore.writePrmContent(playCountFile, String.valueOf(ConfigSettings.countCompletion));
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//                        }, (ConfigSettings.SHOW_TIME + 1) * 1000);
//                    }
                    if (null == alertMsg) {
                        mFragmentsManager.switchProgramFragment(program);
                    } else {
                        mFragmentsManager.switchDefaultFragment(alertMsg);
                    }
                }
//            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            LogUtils.d(TAG, "onKeyDown", "keyCode: " + keyCode);
            finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        LogUtils.d(TAG, "onPause", "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止播放背景音乐
        mFragmentsManager.stop();
        LogUtils.d(TAG, "onStop", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        stopService(tanZhenIntent);
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mUpdateTipMsgReceiver);
//        unregisterReceiver(mCountAdReceiver);
        mHandler.recycler();
        exitApp();
        LogUtils.d(TAG, "onDestroy", "onDestroy");
        System.exit(0);
    }

    /**
     * 退出应用广播,退出通讯应用,退出下载应用
     */
    private void exitApp() {
        posternManager.destory();
        PreLoadImgTool.clearCache();
    }

    /**
     * 启动未下载完的任务
     */
    private void startRecoveryDownload() {
        ComponentName componentName = new ComponentName(Constants.DOWNLOAD_PKG
                , Constants.DOWNLOAD_RECOVERYDOWNLOADTASK_CLS);
        Intent startIntent = new Intent();
        startIntent.setComponent(componentName);
        startService(startIntent);
    }

    @Override
    public void clearPreProgram() {
        if (null != mFragmentsManager) {
            mFragmentsManager.clearPreProgram();
        }
    }

//    private BroadcastReceiver mCountAdReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (null != intent) {
//                ConfigSettings.COUNT_URL_01 = intent.getStringExtra("countUrl01");
//                ConfigSettings.COUNT_URL_02 = intent.getStringExtra("countUrl02");
//                ConfigSettings.COUNT_URL_03 = intent.getStringExtra("countUrl03");
//                ConfigSettings.AD_KEY = intent.getStringExtra("ad_key");
//                ConfigSettings.SHOW_TIME = intent.getIntExtra("show_time", 0);
//                LogUtils.d(TAG, "mCountAdReceiver", "COUNT_URL_01:" + ConfigSettings.COUNT_URL_01 + "\r\n" + "COUNT_URL_02:" + ConfigSettings.COUNT_URL_02 +
//                        "\r\n" + "COUNT_URL_03:" + ConfigSettings.COUNT_URL_03 + "\r\n" + "AD_KEY:" + ConfigSettings.AD_KEY + "\r\n" + "SHOW_TIME : " + ConfigSettings.SHOW_TIME);
//            }
//        }
//    };

    private BroadcastReceiver mUpdateTipMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LogUtils.d(TAG, "onReceive", "onReceive");
            if (null != intent) {
                boolean isCopyPrm = intent.getBooleanExtra(PlayActivity.IS_COPY_PROGRAM_KEY, false);
                if (isCopyPrm) {
                    String tipMsg = intent.getStringExtra(TIP_MSG_KEY);
                    mHandler.obtainMessage(PlayHandler.WHAT_PLAY_PRM,
                            tipMsg).sendToTarget();
                } else {
                    boolean isShow = intent.getBooleanExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, false);
                    if (isShow) {//是否拷贝完成
                        String tipMsg = intent.getStringExtra(TIP_MSG_KEY);
                        mHandler.obtainMessage(PlayHandler.WHAT_VISIBLE_TIP_TV,
                                tipMsg).sendToTarget();
                    } else {
                        LogUtils.d(TAG, "onReceive", "onReceive WHAT_GONE_TIP_TV");
                        mHandler.obtainMessage(PlayHandler.WHAT_GONE_TIP_TV,
                                null).sendToTarget();
                    }
                }
            }
        }
    };

    private static class PlayHandler extends Handler {
        private static final int WHAT_GONE_TIP_TV = 1;
        private static final int WHAT_VISIBLE_TIP_TV = 0;
        private static final int WHAT_PLAY_PRM = 2;
        private WeakReference<PlayActivity> mWeakReference;

        public PlayHandler(PlayActivity playActivity) {
            mWeakReference = new WeakReference<PlayActivity>(playActivity);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PlayActivity playActivity = mWeakReference.get();
            if (null != playActivity) {
                switch (msg.what) {
                    case WHAT_GONE_TIP_TV:
                        playActivity.mTipMsgTv.setVisibility(View.GONE);
                        break;
                    case WHAT_VISIBLE_TIP_TV:
                        playActivity.mTipMsgTv.setVisibility(View.VISIBLE);
                        playActivity.mTipMsgTv.setText((String) msg.obj);
                        break;
                    case WHAT_PLAY_PRM:
                        ProgramPlayManager.getInstance().playProgramList("WHAT_PLAY_PRM");
                        break;
                    default:
                        break;
                }
            }
        }

        public void recycler() {
            PlayActivity playActivity = mWeakReference.get();
            playActivity = null;
            mWeakReference.clear();
            mWeakReference = null;
        }
    }
}