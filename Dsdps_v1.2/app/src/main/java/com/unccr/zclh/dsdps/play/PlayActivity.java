package com.unccr.zclh.dsdps.play;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.hedgehog.ratingbar.RatingBar;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.unccr.zclh.dsdps.ProhibitActivity;
import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.adapter.LiftContentAdapter;
import com.unccr.zclh.dsdps.adapter.LiftInfoAdapter;
import com.unccr.zclh.dsdps.adapter.LiftRatingAdapter;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.download.ResposeDownloadService;
import com.unccr.zclh.dsdps.download.bean.LiftBean;
import com.unccr.zclh.dsdps.download.bean.LiftContentBean;
import com.unccr.zclh.dsdps.download.bean.LiftRatingBean;
import com.unccr.zclh.dsdps.fragment.PlayFragment;
import com.unccr.zclh.dsdps.fragment.view.AutoPollRecyclerView;
import com.unccr.zclh.dsdps.fragment.view.CourstomerLinearLayouts;
import com.unccr.zclh.dsdps.manager.FragmentsManager;
import com.unccr.zclh.dsdps.models.MessageEvent;
import com.unccr.zclh.dsdps.models.PositionEvent;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.models.Programme;
import com.unccr.zclh.dsdps.models.Resource;
import com.unccr.zclh.dsdps.models.WeatherInfo;
import com.unccr.zclh.dsdps.play.interfaces.ISwitchLayoutListener;
import com.unccr.zclh.dsdps.push.control.DeviceCommandControl;
import com.unccr.zclh.dsdps.service.SecondScreenService;
import com.unccr.zclh.dsdps.service.heart.HeartService;
import com.unccr.zclh.dsdps.service.process.LocalService;
import com.unccr.zclh.dsdps.service.process.RemoteService;
import com.unccr.zclh.dsdps.service.sign.SignUtil;
import com.unccr.zclh.dsdps.service.time.TimerSleepScreenService;
import com.unccr.zclh.dsdps.service.websocket.JWebSocketService;
import com.unccr.zclh.dsdps.setting.SettingsActivity;
import com.unccr.zclh.dsdps.sip.CallBean;
import com.unccr.zclh.dsdps.sip.CallMediaUtils;
import com.unccr.zclh.dsdps.sip.Sip;
import com.unccr.zclh.dsdps.sip.SipEngine;
import com.unccr.zclh.dsdps.sip.common.MaintainBean;
import com.unccr.zclh.dsdps.util.CONS;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.DialogUtil;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.HttpUtil;
import com.unccr.zclh.dsdps.util.LayoutUtil;
import com.unccr.zclh.dsdps.util.ListUtil;
import com.unccr.zclh.dsdps.util.LogcatHelper;
import com.unccr.zclh.dsdps.util.NetworkUtil;
import com.unccr.zclh.dsdps.util.PreLoadImgTool;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.Util;
import com.unccr.zclh.dsdps.util.WorkTimeUtil;

import org.apache.http.Header;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import blue.view.EngineServer;
import blue.view.SMPercentFrameLayout;
import blue.view.SMSurfaceViewRenderer;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.unccr.zclh.dsdps.util.DateTimeUtil.getTimeToStrDate;

/**
 * @version 1.0
 * @data 创建时间：2019年10月09日 上午9:43:12
 * @parameter PlayActivity
 */
public class PlayActivity extends Activity implements ISwitchLayoutListener, Handler.Callback {

    private static final String TAG = "DsdpsPlayActivity";
    public static final String TIP_MSG_KEY = "tipMsg";
    public static final String TIP_MSG_IS_SHOW_KEY = "isShow";
    public static final String IS_COPY_PROGRAM_KEY = "isCopyProgram";
    public static final String UPDATE_MSG_TIP_MSG_ACTION = "com.lzkj.ui.UPDATE_TIP_MSG_ACTION";
    private static final String DEVICE_ID = android.os.Build.SERIAL;
    public static final int RESTART_PERIOD_TIME = 1 * 1000;//1分钟
    public static final int REFLUSH_PERIOD_TIME = 10 * 60 * 1000;//10分钟
    public static final int PICTURE_PERIOD_TIME = 20 * 1000;//20秒
    public static final int LOG_PERIOD_TIME = 3 * 60 * 60 * 1000;//3小时
    public static final int CONSERVATION_OF_RESOURCES_TIME = 3 * 60 * 60 * 1000;//测试暂且5分钟一次
    public static PlayActivity mainActivity;
    public static Handler handler_PlayActivity;
    private Handler handler = new Handler(this);
    private int callid = -1;//当前通话的id
    private String callnumbers = "未知";
    private String callstates = "未知";
    private int calltype = 0; //0音频1视频
    private boolean VIDEOSTATE = false;
    private int count = 0;
    private long callTime;

    /**
     * 管理fragment的类
     */
    private FragmentsManager mFragmentsManager;
    /**
     * 屏幕内容主布局控件
     **/
    private FrameLayout mainFrameLayoutView;
    private FrameLayout mianFlayout;
    private PlayHandler mHandler = new PlayHandler(this);
    private TextView mTipMsgTv;
    private LinearLayout show_pic_ll, ll_blueTel;

    private Intent secondScreenService;
    private Intent heartService;
    private Intent responseDownloadService;
    private Intent timerSleepScreenService;
    private Intent localService;
    private Intent remoteService;
    private static String callnumber;

    private static final int MY_PERMISSION_REQUEST_CODE = 10001;//权限请求码
    // 所需的全部权限
    private String[] permissions = new String[]{
            Manifest.permission.NFC,
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.CAMERA,
            Manifest.permission.MODIFY_AUDIO_SETTINGS,
            Manifest.permission.RESTART_PACKAGES,
            Manifest.permission.READ_CALL_LOG,
            Manifest.permission.BROADCAST_STICKY,
            Manifest.permission.GET_ACCOUNTS,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.CHANGE_WIFI_MULTICAST_STATE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CHANGE_WIFI_STATE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.VIBRATE,
            Manifest.permission.DISABLE_KEYGUARD,
            Manifest.permission.WAKE_LOCK,
            Manifest.permission.CALL_PHONE,
            Manifest.permission.WRITE_CONTACTS,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.RECEIVE_BOOT_COMPLETED,
            Manifest.permission.PROCESS_OUTGOING_CALLS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.KILL_BACKGROUND_PROCESSES,
            Manifest.permission.READ_EXTERNAL_STORAGE
    };
    private Intent jWebsocketService;
    //private RecyclerView mRvLift, mRvRate, mRvCategory;
    private AutoPollRecyclerView mRecyclerView;
    private RatingBar ratingBar;
    private double ratingStar = 5.0;
    private List<LiftBean> liftInfoList = new ArrayList<>();
    private List<LiftRatingBean> liftRatingList = new ArrayList<>();
    private List<LiftContentBean> liftContentList = new ArrayList<>();
    private ArrayList<String> moduleOneUrlList = new ArrayList<>();
    private ArrayList<String> moduleFourUrlList = new ArrayList<>();
    private ArrayList<String> moduleFourNameList = new ArrayList<>();
    private List<Resource> resourceList = new ArrayList<>();
    private LiftRatingAdapter liftRatingAdapter;
    private LiftContentAdapter liftContentAdapter;
    private TextView date;
    private TextView week;
    private ImageView feedback;
    private ImageView iv_weather;
    private TextView tv_temperature;
    private TextView tv_weatherInfo;
    private HashMap<String, Integer> weatherIconMap;
    private String cityName = "烟台";
    private RecyclerView rv_liftInfo;
    private RecyclerView rv_rate;
    private Button btn_confirm, btn_cancel;
    private AlertDialog dialog;
    private LinearLayoutManager linearLayoutManager;
    private ImageView bigImage;
    private ImageView bgFrame;
    private ArrayList<String> images;
    private TextView tv_title;
    private TextView tv_content;
    private int currentPosition = 0;
    private int clickPosition;
    private int timerPosition = 1;
    private int flagCount = 0;
    private boolean flag = false;
    private boolean reStartFlag = true;
    private Timer picTimer;
    private Timer picTimerTwo;
    private Timer timer;
    private PicTaskOne picTaskOne;
    private PicTaskTwo picTaskTwo;
    private RestartTimerTask restartTimerTask;
    private LogCaptureTimerTask logCaptureTimerTask;
    private ConservationOfResourcesTask conservationOfResourcesTask;
    private WeatherReflushTimerTask weatherReflushTimerTask;
    private ImageView iv_blueTel;
    private TextView tv_network;
    private boolean shortPress = false;
    private SMPercentFrameLayout remoteRenderLayout;
    private SMSurfaceViewRenderer remoteRender;
    private EngineServer engineServer;
    private SMSurfaceViewRenderer localRender;
    private SMPercentFrameLayout localRenderLayout;
    private TextView calling_tv;
    private ImageView head_iv;
    private Chronometer chronometer;

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
        DsdpsApp.getDsdpsApp().setPlayActivity(this);
        setContentView(R.layout.activity_play);
        EventBus.getDefault().register(this);
        SharedUtil.newInstance().setInt("state", 1);
        initView();
        initListener();
        handler_PlayActivity = handler;
        secondScreenService = new Intent(PlayActivity.this, SecondScreenService.class);
        heartService = new Intent(PlayActivity.this, HeartService.class);
        responseDownloadService = new Intent(PlayActivity.this, ResposeDownloadService.class);
        timerSleepScreenService = new Intent(PlayActivity.this, TimerSleepScreenService.class);
        jWebsocketService = new Intent(PlayActivity.this, JWebSocketService.class);
        //双进程守护，应用保活
        localService = new Intent(PlayActivity.this, LocalService.class);
        remoteService = new Intent(PlayActivity.this, RemoteService.class);
        if (checkPermissions()) {
            startService(heartService);
            startService(responseDownloadService);
            startService(secondScreenService);
            startService(timerSleepScreenService);
            startService(jWebsocketService);
            startService(localService);
            startService(remoteService);
        } else {
            ActivityCompat.requestPermissions(PlayActivity.this, permissions, MY_PERMISSION_REQUEST_CODE);
        }
        int useState = SharedUtil.newInstance().getInt("useState");
        Log.d(TAG, "useState: " + useState);
        if (useState == 0) {
            // 禁用
            if (SharedUtil.isProhibit) {
                return;
            }
            Log.d(TAG, "终端禁用......");
            Intent intent = new Intent(DsdpsApp.getDsdpsApp(), ProhibitActivity.class);
            intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
            DsdpsApp.getDsdpsApp().startActivity(intent);
        } else if (useState == 1) {
            // 启用
            if (SharedUtil.isProhibit) {
                // chexiao
                Log.d(TAG, "终端启用......");
                SharedUtil.isProhibit = false;
                Intent intent = new Intent(DsdpsApp.getDsdpsApp(), PlayActivity.class);
                intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                DsdpsApp.getDsdpsApp().startActivity(intent);
            }
        }
    }

    private boolean checkPermissions() {
        for (String permission : permissions) {
            if (Build.VERSION.SDK_INT >= 23) {
                if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSION_REQUEST_CODE) {
            boolean isAllGranted = true;
            // 判断是否所有的权限都已授予
            for (int grant : grantResults) {
                if (grant != PackageManager.PERMISSION_GRANTED) {
                    isAllGranted = false;
                    break;
                }
            }
            if (isAllGranted) {
                startService(heartService);
                startService(responseDownloadService);
                startService(secondScreenService);
                startService(timerSleepScreenService);
                startService(jWebsocketService);
                startService(localService);
                startService(remoteService);
            } else {
                Toast.makeText(getApplicationContext(), "授权失败", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void setupFullScreen() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 隐藏状态栏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    /**
     * 初始化view
     */
    private void initView() {
        mainFrameLayoutView = findViewById(R.id.main_container);
        mianFlayout = findViewById(R.id.mian_flayout);
        date = findViewById(R.id.tv_date);
        week = findViewById(R.id.tv_week);
        feedback = findViewById(R.id.iv_feedback);
        iv_weather = findViewById(R.id.iv_weather);
        tv_temperature = findViewById(R.id.tv_temperature);
        tv_weatherInfo = findViewById(R.id.tv_weatherInfo);
        //mRvCategory = findViewById(R.id.rv_category);
        chronometer = findViewById(R.id.chronometer);
        mRecyclerView = findViewById(R.id.rv_category);
        bigImage = findViewById(R.id.iv_big_category);
        tv_title = findViewById(R.id.tv_title);
        tv_content = findViewById(R.id.tv_content);
        iv_blueTel = findViewById(R.id.iv_tonghua_finish);
        ll_blueTel = findViewById(R.id.ll_blueTel);
        tv_network = findViewById(R.id.tv_network);
        chronometer = findViewById(R.id.chronometer);
        show_pic_ll = findViewById(R.id.show_pic_ll);
        localRender = (SMSurfaceViewRenderer) findViewById(R.id.local_video_view);
        remoteRender = (SMSurfaceViewRenderer) findViewById(R.id.remote_video_view);
        localRenderLayout = (SMPercentFrameLayout) findViewById(R.id.local_video_layout);
        remoteRenderLayout = (SMPercentFrameLayout) findViewById(R.id.remote_video_layout);
        ProgramPlayManager.getInstance().setSwitchLayoutImpl(this);
        mFragmentsManager = new FragmentsManager(this, getFragmentManager(), mainFrameLayoutView);
        engineServer = new EngineServer(localRender, remoteRender, localRenderLayout, remoteRenderLayout, true);
        /**
         * LAYOUT_PRIMARY 0
         * LAYOUT_AVERAGE 1
         * LAYOUT_REMOTEFULL 2
         * LAYOUT_LOCALFULL 3
         */
        engineServer.adjustVideoView(2);
        calling_tv = findViewById(R.id.calling_tv);
        head_iv = findViewById(R.id.head_iv);
        //模块四
        linearLayoutManager = new LinearLayoutManager(PlayActivity.this, LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(new CourstomerLinearLayouts(PlayActivity.this, LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        liftContentAdapter = new LiftContentAdapter(PlayActivity.this);
        mRecyclerView.setAdapter(liftContentAdapter);

        initWeatherIcons();
    }

    /**
     * 停止视频
     *
     * @param isExit 是否完全退出视频
     */
    public synchronized void stopVideoStream(boolean isExit) {
        Log.d(TAG, "stopVideoStream isExit: " + isExit);
        if (VIDEOSTATE && engineServer != null) {
            Log.d(TAG, "stopVideoStream VIDEOSTATE: " + VIDEOSTATE + " ,engineServer: " + engineServer);
            engineServer.stopVideoStream(isExit);
            VIDEOSTATE = false;
            engineServer = null;
        }
    }

    private void initListener() {
        feedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback.setVisibility(View.INVISIBLE);
                dialog = new AlertDialog.Builder(PlayActivity.this, R.style.dialog).create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
                Window window = dialog.getWindow();
                window.setGravity(Gravity.BOTTOM | Gravity.RIGHT);//设置弹框在屏幕的下方
                window.setContentView(R.layout.lift_dialog_layout);
                rv_liftInfo = (RecyclerView) window.findViewById(R.id.rv_liftInfo);
                ratingBar = (RatingBar) window.findViewById(R.id.ratingbar);
                rv_rate = (RecyclerView) window.findViewById(R.id.rv_rate);
                btn_confirm = (Button) window.findViewById(R.id.btn_confirm);
                btn_cancel = (Button) window.findViewById(R.id.btn_back);

                window.setWindowAnimations(R.style.AnimBottom);//设置从屏幕下方弹框动画
                //设置弹框的高为屏幕的一半宽是屏幕的宽
                WindowManager windowManager = getWindowManager();
                Display display = windowManager.getDefaultDisplay();
                WindowManager.LayoutParams lp = window.getAttributes();
                lp.width = (int) (display.getWidth() * 0.6); //设置宽度
                lp.height = (int) (display.getHeight() * 0.51); //设置宽度
                lp.y = 45;
                window.setAttributes(lp);

                getDialogData();
            }
        });
        iv_blueTel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                stopVideoStream(true);
                Log.d(TAG, "iv_blueTel callid: " + callid);
                SipEngine.getInstance().hangup(callid);
//                if (SipEngine.callPagesConfig.size() == 0) {

//                    SipEngine.getInstance().hangup(callid);
//                }
            }
        });
    }

    private void initWeatherIcons() {
        weatherIconMap = new HashMap<>();
        weatherIconMap.put("00", R.drawable.qing);
        weatherIconMap.put("01", R.drawable.duoyun);
        weatherIconMap.put("02", R.drawable.yin);
        weatherIconMap.put("03", R.drawable.zhenyu);
        weatherIconMap.put("04", R.drawable.leizhenyu);
        weatherIconMap.put("05", R.drawable.leizhenyubanyoubingbao);
        weatherIconMap.put("06", R.drawable.yujiaxue);
        weatherIconMap.put("07", R.drawable.xiaoyu);
        weatherIconMap.put("08", R.drawable.zhongyu);
        weatherIconMap.put("09", R.drawable.dayu);
        weatherIconMap.put("10", R.drawable.baoyu);
        weatherIconMap.put("11", R.drawable.dabaoyu);
        weatherIconMap.put("12", R.drawable.tedabaoyu);
        weatherIconMap.put("13", R.drawable.zhenxue);
        weatherIconMap.put("14", R.drawable.xiaoxue);
        weatherIconMap.put("15", R.drawable.zhongxue);
        weatherIconMap.put("16", R.drawable.daxue);
        weatherIconMap.put("17", R.drawable.baoxue);
        weatherIconMap.put("18", R.drawable.wu);
        weatherIconMap.put("19", R.drawable.dongyu);
        weatherIconMap.put("20", R.drawable.shachenbao);
        weatherIconMap.put("21", R.drawable.xiaodaozhongyu);
        weatherIconMap.put("22", R.drawable.zhongdaodayu);
        weatherIconMap.put("23", R.drawable.dadaobaoyu);
        weatherIconMap.put("24", R.drawable.baoyudaodabaoyu);
        weatherIconMap.put("25", R.drawable.dabaoyudaotedabaoyu);
        weatherIconMap.put("26", R.drawable.xiaodaozhongxue);
        weatherIconMap.put("27", R.drawable.zhongdaodaxue);
        weatherIconMap.put("28", R.drawable.dadaobaoxue);
        weatherIconMap.put("29", R.drawable.fuchen);
        weatherIconMap.put("30", R.drawable.yangsha);
        weatherIconMap.put("31", R.drawable.qiangshachenbao);
        weatherIconMap.put("53", R.drawable.mai);
    }

    /**
     * 获取天气和日期
     */
    private void getWeatherAndDate() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Date time = new Date(System.currentTimeMillis());
        date.setText(simpleDateFormat.format(time));
        week.setText(Util.getDayOfWeek());
//      getWeatherWithIpAddress();
        getWeather();
    }

    private void getModuleDatas() {
        getModuleOneData();
        getModuleTwoData();
        getModuleThreeData();
        getModuleFourData();
    }

    private void getWeather() {
        RequestParams params = new RequestParams();
        params.put("key", "cc386a8c8af3b908f158cc93d15db6c3");
        params.put("city", "烟台");

        HttpUtil.postLiftInfoJSON("http://apis.juhe.cn/simpleWeather/query", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.e("WeatherInfos:", response.toString());
                try {
                    String temperature = response.getJSONObject("result").getJSONObject("realtime").getString("temperature");
                    String weatherInfo = response.getJSONObject("result").getJSONObject("realtime").getString("info");
                    String wid = response.getJSONObject("result").getJSONObject("realtime").getString("wid");
                    WeatherInfo weather = new WeatherInfo(temperature, weatherInfo, wid);
                    mHandler.obtainMessage(PlayHandler.WHAT_WEATHER, weather).sendToTarget();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "getWeather onFailure throwable: " + throwable.getMessage());
            }
        });
    }

    /**
     * 模块一
     */
    private void getModuleOneData() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数time时间戳
        String time = System.currentTimeMillis() + "";
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);
        params.put("timestamp", System.currentTimeMillis());
        params.put("deviceID", DEVICE_ID);
        HttpUtil.postLiftInfoJSON("http://120.220.248.250:30006/moduleOne", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                //{"code":"200",
                // "message":"操作成功",
                // "data":{"actionNum":"e334f6df12e745c5b534a67aaab77343","resources":[{"name":"b23232d4f51d4d8abed7e040af23a2c2.mp4","type":1,"sort":1,"downPath":"http:\/\/120.220.248.250:30009\/web\/lift\/common\/download\/b23232d4f51d4d8abed7e040af23a2c2.mp4"}],"startTime":"2020-08-11 11:26:10","endTime":"2999-05-11 14:42:35"}}
                Log.d(TAG, "getModuleOneData LiftUrls:" + response.toString());
                //setPlayFlags();
                List<String> playNameList = new ArrayList<>();
                moduleOneUrlList.clear();
                resourceList.clear();

                try {
                    if (response.getString("message").equals("操作成功")) {
                        Programme programme = new Programme();
                        JSONArray jsonArray = response.getJSONObject("data").getJSONArray("resources");
                        if (jsonArray.length() > 0) {
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Resource resource = new Resource();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                resource.setName(jsonObject.getString("name"));// b23232d4f51d4d8abed7e040af23a2c2.mp4
                                resource.setType(jsonObject.getInt("type"));// 1
                                resource.setSort(jsonObject.getInt("sort"));// 1
                                resource.setDownPath(jsonObject.getString("downPath"));// http:\/\/120.220.248.250:30009\/web\/lift\/common\/download\/b23232d4f51d4d8abed7e040af23a2c2.mp4
                                moduleOneUrlList.add(resource.getDownPath());
                                resourceList.add(resource);
                            }
                        }
                        // 下载
                        String startTime = response.getJSONObject("data").getString("startTime");
                        String endTime = response.getJSONObject("data").getString("endTime");
                        String actionNum = response.getJSONObject("data").getString("actionNum");
                        programme.setResources(resourceList);
                        programme.setStartTime(startTime);
                        programme.setEndTime(endTime);
                        programme.setActionNum(actionNum);

                        SharedUtil.newInstance().setString(SharedUtil.PROGRAM_SOURCE_TYPE, "programme");
                        ArrayList<String> downloadProgrammeList = ListUtil.getFilterProgrammeList(moduleOneUrlList);
                        if (downloadProgrammeList.size() > 0) {
                            new Timer().schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Intent intent = new Intent();
                                    intent.setAction(Constants.DOWNLOAD_BROADCAST);
//                              intent.putExtra("key", actionNum);
                                    intent.putExtra("key", "moduleOne");
                                    intent.putStringArrayListExtra("materialList", downloadProgrammeList);
                                    Log.e(TAG, "getModuleOneData LiftDownload1: " + downloadProgrammeList.toString());
                                    PlayActivity.this.sendBroadcast(intent);
                                }
                            }, 5000);
                        }

                        QuartzScheduler.getInstance().addStartPlayProgrammeTask(programme);
                        QuartzScheduler.getInstance().addDelProgrammeTask(programme);

                        for (String url : moduleOneUrlList) {
                            String fileName = FileStore.getFileName(url);
                            playNameList.add(fileName);
                        }
                        String playNamesJson = JSON.toJSONString(playNameList);
                        SharedUtil.newInstance().setString(SharedUtil.MODULEONE_PLAY_NAMES, playNamesJson);

                        // 播放
                        String programmeJson = JSON.toJSONString(programme);
                        Log.e(TAG, "getModuleOneData programmeJson: " + programmeJson);
                        SharedUtil.newInstance().setString(SharedUtil.PROGRAMME, programmeJson);
                        if (downloadProgrammeList.isEmpty()) {
                            playModuleOneProgram();
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "getModuleOneData moduleOne onFailure throwable: " + throwable.getMessage());
            }
        });
    }

    private void setPlayFlags() {
        SharedUtil.newInstance().setString(SharedUtil.PROGRAMME_PLAY_OVER, "play");
        if (reStartFlag) {
            reStartFlag = false;
            SharedUtil.newInstance().setBoolean("canPlay", true);
        } else {
            SharedUtil.newInstance().setBoolean("canPlay", false);
        }
    }

    /**
     * 模块二
     */
    private void getModuleTwoData() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数time时间戳
        String time = System.currentTimeMillis() + "";
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);
        params.put("timestamp", System.currentTimeMillis());
        params.put("deviceID", DEVICE_ID);

        HttpUtil.postLiftInfoJSON("http://120.220.248.250:30006/moduleTwo", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "getModuleTwoData LiftInfos:" + response.toString());
                liftInfoList.clear();
                try {
                    if (response.get("message").equals("操作成功")) {
                        JSONArray data = response.getJSONArray("data");
                        if (data != null && data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                LiftBean lift = new LiftBean();
                                lift.setFieldName(data.getJSONObject(i).getString("fieldName"));
                                lift.setFieldContent(data.getJSONObject(i).getString("fieldContent"));
                                liftInfoList.add(lift);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "getModuleTwoData moduleTwo onFailure throwable: " + throwable.getMessage());
            }
        });
    }

    /**
     * 模块三
     */
    private void getModuleThreeData() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数time时间戳
        String time = System.currentTimeMillis() + "";
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);
        params.put("timestamp", System.currentTimeMillis());
        params.put("deviceID", DEVICE_ID);

        HttpUtil.postLiftInfoJSON("http://120.220.248.250:30006/moduleThree", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "getModuleThreeData LiftRating:" + response.toString());
                liftRatingList.clear();
                try {
                    if (response.get("message").equals("操作成功")) {
                        JSONArray data = response.getJSONArray("data");
                        if (data != null && data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                LiftRatingBean ratingBean = new LiftRatingBean();
                                ratingBean.setName(data.getJSONObject(i).getString("name"));
                                ratingBean.setKey(data.getJSONObject(i).getString("key"));
                                liftRatingList.add(ratingBean);
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "getModuleThreeData moduleThree onFailure throwable: " + throwable.getMessage());
            }
        });
    }

    /**
     * 模块四
     */
    private void getModuleFourData() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数time时间戳
        String time = System.currentTimeMillis() + "";
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);
        params.put("timestamp", System.currentTimeMillis());
        params.put("deviceID", DEVICE_ID);
        HttpUtil.postLiftInfoJSON("http://120.220.248.250:30006/moduleFour", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "getModuleFourData LiftContents:" + response.toString());
                List<String> playNameList = new ArrayList<>();
                moduleFourNameList.clear();
                moduleFourUrlList.clear();
                liftContentList.clear();
                try {
                    if (response.get("message").equals("操作成功")) {
                        JSONArray data = response.getJSONArray("data");
                        if (data != null && data.length() > 0) {
                            for (int i = 0; i < data.length(); i++) {
                                LiftContentBean liftContentBean = new LiftContentBean();
                                liftContentBean.setName(data.getJSONObject(i).getString("name"));
                                liftContentBean.setTitle(data.getJSONObject(i).getString("title"));
                                liftContentBean.setContent(data.getJSONObject(i).getString("content"));
                                liftContentBean.setDownPath(data.getJSONObject(i).getString("downPath"));
                                moduleFourNameList.add(FileStore.getInstance().getImagePrmFolderPath() + File.separator + liftContentBean.getName());
                                moduleFourUrlList.add(liftContentBean.getDownPath());
                                liftContentList.add(liftContentBean);
                            }
                        }

                        ArrayList<String> downloadProgrammeList = ListUtil.getFilterProgrammeList(moduleFourUrlList);
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                Intent intent = new Intent();
                                intent.setAction(Constants.DOWNLOAD_BROADCAST);
                                intent.putExtra("key", "moduleFour");
                                intent.putStringArrayListExtra("materialList", downloadProgrammeList);
                                Log.e("LiftDownload4:", downloadProgrammeList.toString());
                                PlayActivity.this.sendBroadcast(intent);
                            }
                        }, 5000);

                        for (String url : moduleFourUrlList) {
                            String fileName = FileStore.getFileName(url);
                            playNameList.add(fileName);
                        }
                        String playNamesJson = JSON.toJSONString(playNameList);
                        SharedUtil.newInstance().setString(SharedUtil.MODULEFOUR_PLAY_NAMES, playNamesJson);
                        SharedUtil.newInstance().setString(SharedUtil.PROGRAM_SOURCE_TYPE, "programme");
                        SharedUtil.newInstance().setString(SharedUtil.PROGRAMME_FOUR, JSON.toJSONString(moduleFourNameList));
                        //图片轮播
                        recyclePics(moduleFourNameList);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "getModuleFourData moduleFour onFailure throwable: " + throwable.getMessage());
            }
        });

    }

    // 资源维护
    private void getConservationResources() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数time时间戳
        String time = System.currentTimeMillis() + "";
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);
        Log.d(TAG, "getConservationResources sign: " + sign);
//        params.put("sign", sign);
        params.put("timestamp", System.currentTimeMillis());
        params.put("deviceID", DEVICE_ID);
        HttpUtil.postLiftInfoJSON("http://120.220.248.250:30006/delete", params, new JsonHttpResponseHandler() {
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.d(TAG, "getConservationResources response:" + response.toString());
                try {
                    if (response.get("message").equals("操作成功")) {
                        MaintainBean maintainBean = JSON.parseObject(response.toString(), MaintainBean.class);
                        List<String> data = maintainBean.getData().getData();
                        if (data.size() > 0) {
                            for (int i = 0; i < data.size(); i++) {
                                String programPath = data.get(i);
                                String suffix = FileUtil.getSuffix(programPath).trim().toLowerCase();
                                if (FileUtil.getInstance().suffixToProgrammeFolder.containsKey(suffix)) {
                                    Log.d(TAG,"getConservationResources datas: " + Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zcdt" + File.separator + "pic" + File.separator + programPath);
                                    FileStore.delete(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zcdt" + File.separator + "pic" + File.separator + programPath);
                                } else {
                                    Log.d(TAG,"getConservationResources datas: " + Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zcdt" + File.separator + "video" + File.separator + programPath);
                                    FileStore.delete(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "zcdt" + File.separator + "video" + File.separator + programPath);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.d(TAG, "getConservationResources onFailure throwable: " + throwable.getMessage());
            }
        });
    }

    private void recyclePics(ArrayList<String> list) {
        if (list == null | list.isEmpty()) {
            return;
        }
        flagCount = 0;
        liftContentAdapter.setList(list);
        cancelRotationTasks();
        picTaskOne = new PicTaskOne();
        picTimer = new Timer();
        picTimer.schedule(picTaskOne, 0, PICTURE_PERIOD_TIME);
//      mHandler.obtainMessage(PlayHandler.WHAT_MODULEFOUR, list.get(0)).sendToTarget();
        liftContentAdapter.setItemClickListener(position -> {
            flag = true;
            clickPosition = position;
            Log.d(TAG, "clickPosition: " + clickPosition);
            cancelRotationTasks();
            picTaskTwo = new PicTaskTwo();
            picTimerTwo = new Timer();
            picTimerTwo.schedule(picTaskTwo, 0, PICTURE_PERIOD_TIME);
        });
    }

    /**
     * 取消所有轮播定时任务
     */
    private void cancelRotationTasks() {
        if (picTaskTwo != null) {
            picTaskTwo.cancel();
            picTaskTwo = null;
        }
        if (picTimerTwo != null) {
            picTimerTwo.cancel();
            picTimerTwo.purge();
            picTimerTwo = null;
        }
        if (picTaskOne != null) {
            picTaskOne.cancel();
            picTaskOne = null;
        }
        if (picTimer != null) {
            picTimer.cancel();
            picTimer.purge();
            picTimer = null;
        }
    }

    @Override
    public boolean handleMessage(@NonNull Message msg) {
        Log.d(TAG, "handleMessage msg.what: " + msg.obj);
        switch (msg.what) {
            case CONS.LOGIN:
//                callnumber = SharedUtil.newInstance().getString(Constants.CALL_NUMBER);
//                if (TextUtils.isEmpty(callnumber)) {
//                    callnumber = Constants.defaultCallNumber;
//                }
//                Log.d(TAG, "handleMessage callnumber: " + callnumber);
//                SipEngine.getInstance().CallNumber(callnumber, true);
                break;
            case CONS.CALLING:
                Log.d(TAG, "handleMessage CONS.CALLING: " + CONS.CALLING);
                CallBean callBean = (CallBean) msg.obj;
                show_pic_ll.setVisibility(View.GONE);
                ll_blueTel.setVisibility(View.VISIBLE);
                callid = callBean.getCallid();
                callnumbers = callBean.getCallnumber();
                callstates = callBean.getCallstate();
                calltype = callBean.getCalltype();
                Log.e(TAG, "handleMessage callid: " + callid + " ,callnumbers: " + callnumbers + " ,callstates: " + callstates + " ,calltype: " + calltype);
                break;
            case CONS.CALLSTATE:
                if (msg.obj.equals("呼叫中")) {
                    calling();
                } else if (msg.obj.equals("振铃中")) {

                } else if (msg.obj.equals("通话中")) {
                    ProgramPlayManager.getInstance().playProgrammeList("onResume", null, true);
                    head_iv.setVisibility(View.GONE);
                    calling_tv.setVisibility(View.VISIBLE);
                    calling_tv.setText("通话中...");
                    chronometer.setVisibility(View.VISIBLE);
                    chronometer.setBase(SystemClock.elapsedRealtime() - callTime);//路过已经记录的时间
                    chronometer.start();
                } else if (msg.obj.equals("挂断")) {
                    chronometer.stop();
                    callTime = 0;//重置时间
                    chronometer.setBase(SystemClock.elapsedRealtime());
                    calling_tv.setVisibility(View.GONE);
                    chronometer.setVisibility(View.GONE);
                    remoteRenderLayout.setVisibility(View.GONE);
                    ll_blueTel.setVisibility(View.GONE);
                    show_pic_ll.setVisibility(View.VISIBLE);
                    stopVideoStream(true);
                    SipEngine.getInstance().hangup(callid);
                }
                break;
            case CONS.MEDIASTATE:
                Log.d(TAG, "handleMessage CONS.MEDIASTATE: " + CONS.MEDIASTATE);
                CallMediaUtils utils = (CallMediaUtils) msg.obj;
                int r = utils.getR();
                int l = utils.getL();
                int payload = utils.getPayload();
                if (r != 0 || l != 0) {
                    //开始视频
                    ShowVideoView(true);
                    startVideoStream(SipEngine.getInstance().getip(), r, l, payload);
                } else {
                    //开始音频
                    stopVideoStream(false);
                    ShowVideoView(false);
                }
                break;
            case CONS.CALLDOWN:
                Log.d(TAG, "handleMessage CONS.CALLDOWN: " + CONS.CALLDOWN);
                calling_tv.setVisibility(View.GONE);
                show_pic_ll.setVisibility(View.VISIBLE);
                remoteRenderLayout.setVisibility(View.GONE);
                ll_blueTel.setVisibility(View.GONE);
                stopVideoStream(true);
                SipEngine.getInstance().hangup(callid);
                break;
        }
        return true;
    }

    private void calling() {
        head_iv.setVisibility(View.VISIBLE);
        calling_tv.setVisibility(View.VISIBLE);
        chronometer.setVisibility(View.GONE);
        calling_tv.setText("呼叫中...");
    }

    private synchronized void startVideoStream(String server, int rport, int lport, final int payload) {
        int BitRate = 768;
        int FrameRate = 10;
        int w = 480;
        int h = 270;
        String camerafb = "Front";
        int c = 1;
        switch (camerafb) {
            case "Front":
                c = 1;
                break;
            case "Post":
                c = 0;
                break;
            case "Usb":
                c = 2;
                break;
        }
        c = 0;
        Log.e(TAG, "startVideoStream: " + callnumber + "--" + server + "--" + rport + "--" + lport);
        if (engineServer == null) {
            engineServer = new EngineServer(localRender, remoteRender, localRenderLayout, remoteRenderLayout, true);
            /**
             * LAYOUT_PRIMARY 0
             * LAYOUT_AVERAGE 1
             * LAYOUT_REMOTEFULL 2
             * LAYOUT_LOCALFULL 3
             */
            engineServer.adjustVideoView(2);
        }
        if (engineServer != null) {
            ShowVideoView(true);
            /*
             * 开始视频
             * @param context
             * @param server 服务器地址
             * @param rport 远程端口
             * @param lport 本地端口
             * @param payload payload
             * @param BitRate 带宽
             * @param FrameRate 帧率
             * @param w 宽
             * @param h 高
             * @param isHard true硬编 false软编
             */
            Log.d(TAG, "startVideoStream \n" + "server: " + server + "\nrport: " + rport + "\npayload: " + payload + "\nBitRate: " + BitRate + "\nFrameRate: " + FrameRate + "\nw: " + w
                    + "\nh: " + h + "\nc: " + c);
            engineServer.startVideoStream(this, server, rport, lport, payload, BitRate, FrameRate, w, h, c, true);
            VIDEOSTATE = true;
        }
    }

    int s = 0;

    public void ShowVideoView(boolean show) {
        if (show) {
            localRenderLayout.setVisibility(View.GONE);
            remoteRenderLayout.setVisibility(View.VISIBLE);
        } else {
            localRenderLayout.setVisibility(View.GONE);
            remoteRenderLayout.setVisibility(View.GONE);
        }
    }

    private class PicTaskOne extends TimerTask {
        @Override
        public void run() {
            flagCount++;
            images = liftContentAdapter.updateList(timerPosition);
            if (images != null && !images.isEmpty()) {
                mHandler.obtainMessage(PlayHandler.WHAT_MODULEFOUR, images.get(0)).sendToTarget();
            }
        }
    }

    private class PicTaskTwo extends TimerTask {
        @Override
        public void run() {
            if (flag) {
                Message msg = new Message();
                images = liftContentAdapter.updateList(clickPosition);
                Bundle bundle = new Bundle();
                bundle.putInt("position", clickPosition);
                bundle.putString("imagePath", images.get(0));
                msg.what = PlayHandler.WHAT_MODULEFOUR;
                msg.setData(bundle);
                mHandler.sendMessage(msg);
            } else {
                images = liftContentAdapter.updateList(currentPosition);
                mHandler.obtainMessage(PlayHandler.WHAT_MODULEFOUR, images.get(0)).sendToTarget();
            }
        }
    }

    private class RestartTimerTask extends TimerTask {
        @Override
        public void run() {
            if (getTimeToStrDate().equals("02:00:00")) {
                new DeviceCommandControl().reboot();
            }
        }
    }

    private class LogCaptureTimerTask extends TimerTask {
        @Override
        public void run() {
            LogcatHelper.delOldLogs(PlayActivity.this);//删除目录下前天的日志
            LogcatHelper.getInstance(PlayActivity.this).start(); //获取并保持日志
        }
    }

    // 资源维护
    private class ConservationOfResourcesTask extends TimerTask {

        @Override
        public void run() {
            try {
                new Thread(() -> mHandler.obtainMessage(PlayHandler.WHAT_CONSERVATION_OF_RESOURCES).sendToTarget()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class WeatherReflushTimerTask extends TimerTask {
        @Override
        public void run() {
            try {
                new Thread(() -> mHandler.obtainMessage(PlayHandler.WHAT_REFLUSH_WEATHER).sendToTarget()).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 弹框模块二/三数据
     */
    private void getDialogData() {
        mHandler.obtainMessage(PlayHandler.WHAT_MODULETWO).sendToTarget();
        mHandler.obtainMessage(PlayHandler.WHAT_MODULETHREE).sendToTarget();

        ratingBar.setStar(5);
        ratingBar.setOnRatingChangeListener(RatingCount -> {
            BigDecimal b = new BigDecimal(String.valueOf(RatingCount));
            ratingStar = b.doubleValue();
        });

        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                feedback.setVisibility(View.VISIBLE);
                dialog.dismiss();
            }
        });

        btn_confirm.setOnClickListener(v -> {
            HashMap<String, Integer> liftRatingAdapterMap = liftRatingAdapter.getMap();
            if (liftRatingAdapterMap == null || liftRatingAdapterMap.size() == 0) {
                showToast("请至少勾选一项投诉原因～");
                return;
            }
            JSONObject jsonObject = new JSONObject(liftRatingAdapterMap);
            String data = jsonObject.toString();
            RequestParams params = new RequestParams();
            params.put("sign", "dddd");
            params.put("timestamp", System.currentTimeMillis());
            params.put("deviceID", DEVICE_ID);
            params.put("data", data);
            params.put("value", ratingStar);
            HttpUtil.postLiftInfoJSON("http://120.220.248.250:30006/interactive", params, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                    commitToast("提交成功");
                    Log.d(TAG, "report liftRating onSuccess response: " + response.toString());
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                    commitToast("提交失败");
                    Log.d(TAG, "report liftRating onFailure response: " + throwable.getMessage());
                }
            });
            feedback.setVisibility(View.VISIBLE);
            dialog.dismiss();
        });
    }

    private void commitToast(String msg) {
        showToast(msg);
        ratingBar.setStar(0.0f);
        ratingStar = 0.0;
        liftRatingAdapter.clear();
    }

    private void showToast(String msg) {
        Toast toast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        View view = View.inflate(this, R.layout.custom_toast, null);
        toast.setView(view);
        TextView text = view.findViewById(R.id.text);
        ImageView image = view.findViewById(R.id.iv_toast);
        text.setText(msg);
        if ("请进行评分～".equals(msg) || "请至少勾选一项投诉原因～".equals(msg)) {
            text.setTextColor(getResources().getColor(R.color.toastSelected));
            image.setVisibility(View.GONE);
        } else if ("提交成功".equals(msg)) {
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.ok);
            text.setTextColor(getResources().getColor(R.color.toastSucceed));
        } else {
            text.setTextColor(getResources().getColor(R.color.toastFailed));
            image.setVisibility(View.VISIBLE);
            image.setImageResource(R.drawable.fail);
        }
        toast.setGravity(Gravity.BOTTOM, 30, 485);
        toast.show();
    }

    @Override
    protected void onStart() {
        super.onStart();
        int width = LayoutUtil.getInstance().getRealDisplayWidth();
        int height = LayoutUtil.getInstance().getRealDisplayHeight();
        mainFrameLayoutView.getLayoutParams().width = width;
        mianFlayout.getLayoutParams().height = height;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        reStartFlag = true;
        currentPosition = 0;
        flagCount = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
//      VolumeUtil.setDeviceVolume(20);
        timer = new Timer();
        restartTimerTask = new RestartTimerTask();
        weatherReflushTimerTask = new WeatherReflushTimerTask();
        logCaptureTimerTask = new LogCaptureTimerTask();
        conservationOfResourcesTask = new ConservationOfResourcesTask();
        timer.schedule(restartTimerTask, 0, RESTART_PERIOD_TIME);
        timer.schedule(weatherReflushTimerTask, REFLUSH_PERIOD_TIME, REFLUSH_PERIOD_TIME);//10分钟刷新一次天气
        timer.schedule(logCaptureTimerTask, 0, LOG_PERIOD_TIME);
        timer.schedule(conservationOfResourcesTask, 0, CONSERVATION_OF_RESOURCES_TIME);
        WorkTimeUtil.get().setWorkTime();
        getWeatherAndDate();
        getModuleDatas();
        playModuleOneProgram(); //播放模块一节目
        if (!NetworkUtil.isNetworkConnected(this)) {
            playModuleFourProgram(); //播放模块四节目
        }
        ProgramPlayManager.getInstance().playProgramList("onResume");//播放节目
        if (true) {
            mRecyclerView.start();
        }
    }

    private void playModuleOneProgram() {
        Programme programme = null;
        try {
            programme = JSON.parseObject(SharedUtil.newInstance().getString(SharedUtil.PROGRAMME), Programme.class);
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "playProgrammeList1 Exception: ");
        }
//        Log.d(TAG, "playProgrammeList1 programme: "+programme.getResources().get(0).getName());
        ProgramPlayManager.getInstance().playProgrammeList("onResume", programme, false);
    }

    private void playModuleFourProgram() {
        ArrayList<String> pictureNames = (ArrayList<String>) JSON.parseObject(SharedUtil.newInstance().getString(SharedUtil.PROGRAMME_FOUR), new TypeReference<List<String>>() {
        });
        recyclePics(pictureNames);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageEvent messageEvent) {
        Log.d(TAG, "onEvent: " + "message: " + messageEvent.getMsg() + " ,code: " + messageEvent.getCode() + " ,command: " + messageEvent.getCommand());
        if (!TextUtils.isEmpty(messageEvent.getCode())) {
            playModuleOneProgram();
        } else if ("节目结束".equals(messageEvent.getMsg())) {
            playModuleOneProgram();
        } else if ("节目开始".equals(messageEvent.getMsg())) {
            playModuleOneProgram();
        }
        if (DEVICE_ID.equals(messageEvent.getCode())) {
            if ("moduleOne".equals(messageEvent.getCommand())) {
                getModuleOneData();
            } else if ("moduleTwo".equals(messageEvent.getCommand())) {
                getModuleTwoData();
            } else if ("moduleThree".equals(messageEvent.getCommand())) {
                getModuleThreeData();
            } else if ("moduleFour".equals(messageEvent.getCommand())) {
                getModuleFourData();
            } else if ("pacificationStop".equals(messageEvent.getCommand())) {
                ProgramPlayManager.getInstance().playProgrammeList("onResume", null, false);
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(PositionEvent positionEvent) {
        currentPosition = positionEvent.getPosition();
        Log.d(TAG, "currentPosition: " + currentPosition);
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

    @Override
    public void switchPlayLayout(ArrayList<String> pathList, boolean isPicProgramme, boolean isAnfu) {
        runOnUiThread(() -> {
            PlayFragment playFragment = PlayFragment.newInstance(pathList, isPicProgramme, isAnfu);
            FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.main_container, playFragment);
            fragmentTransaction.commitAllowingStateLoss();
        });
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
                if (program != null) {
                    mFragmentsManager.switchProgramFragment(program);
                } else {
                    mFragmentsManager.switchDefaultFragment(alertMsg);
                }
            }
        });
    }


    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event) {
        if (keyCode == 134) {
            shortPress = false;
            if (ConfigSettings.SIP_IS_LOGIN == false) {
                Sip.loginSip();
            } else {
                callnumber = SharedUtil.newInstance().getString(Constants.CALL_NUMBER);
                if (TextUtils.isEmpty(callnumber)) {
                    callnumber = Constants.defaultCallNumber;
                }
                Log.d(TAG, "handleMessage callnumber: " + callnumber);
                SipEngine.getInstance().CallNumber(callnumber, true);
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown keyCode: " + keyCode + " ,event: " + event.getKeyCode());
        if (keyCode == 134) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                event.startTracking();
                if (event.getRepeatCount() == 0) {
                    shortPress = true;
                }
                return true;
            }
        } else if ((keyCode == KeyEvent.KEYCODE_BACK) && (event.getRepeatCount() == 0)) {
            DialogUtil.showAlertDialog(PlayActivity.this, R.mipmap.ic_launcher_round,
                    "退出提示", "", "确定", "取消",
                    true, new DialogUtil.AlertDialogBtnClickListener() {
                        @Override
                        public void clickPositive() {
                            Intent intent = new Intent(PlayActivity.this, SettingsActivity.class);
                            startActivity(intent);
                        }

                        @Override
                        public void clickNegative() {

                        }

                        @Override
                        public void clickSet() {

                        }

                    });
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyUp keyCode: " + keyCode);
        if (keyCode == 134) {
            if (shortPress) {
                //短按要执行的操作
                DialogUtil.showAlertDialog(PlayActivity.this, R.mipmap.ic_launcher_round,
                        "退出提示", "", "确定", "取消",
                        true, new DialogUtil.AlertDialogBtnClickListener() {
                            @Override
                            public void clickPositive() {
                                Intent intent = new Intent(PlayActivity.this, SettingsActivity.class);
                                startActivity(intent);
                            }

                            @Override
                            public void clickNegative() {

                            }

                            @Override
                            public void clickSet() {

                            }

                        });
            }
            shortPress = false;
            return true;
        }
        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //停止播放背景音乐
        mFragmentsManager.stop();
        cancelRotationTasks();
    }

    @Override
    protected void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer = null;
            restartTimerTask = null;
            conservationOfResourcesTask = null;
            weatherReflushTimerTask = null;
            logCaptureTimerTask = null;
        }
        LocalBroadcastManager.getInstance(getApplicationContext()).unregisterReceiver(mUpdateTipMsgReceiver);
        EventBus.getDefault().unregister(this);
        mHandler.recycler();
        stopVideoStream(true);
        SipEngine.getInstance().hangup(callid);
        SipEngine.getInstance().Unregister();
        handler_PlayActivity = null;
        exitApp();
        Log.d(TAG, "onDestroy.");
        super.onDestroy();
        System.exit(0);
    }

    /**
     * 退出应用广播,退出通讯应用,退出下载应用
     */
    private void exitApp() {
        PreLoadImgTool.clearCache();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "FLAGS: " + intent.getFlags());
        if ((Intent.FLAG_ACTIVITY_CLEAR_TOP & intent.getFlags()) != 0) {
            finish();
        }
    }

    @Override
    public void clearPreProgram() {
        if (null != mFragmentsManager) {
            mFragmentsManager.clearPreProgram();
        }
    }

    private BroadcastReceiver mUpdateTipMsgReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                boolean isCopyPrm = intent.getBooleanExtra(PlayActivity.IS_COPY_PROGRAM_KEY, false);
                Log.d(TAG, "mUpdateTipMsgReceiver isCopyPrm: " + isCopyPrm);
                if (isCopyPrm) {
                    String tipMsg = intent.getStringExtra(TIP_MSG_KEY);
                    mHandler.obtainMessage(PlayHandler.WHAT_PLAY_PRM, tipMsg).sendToTarget();
                    Log.d(TAG, "mUpdateTipMsgReceiver tipMsg: " + tipMsg);
                } else {
                    boolean isShow = intent.getBooleanExtra(PlayActivity.TIP_MSG_IS_SHOW_KEY, false);
                    if (isShow) {//是否拷贝完成
                        String tipMsg = intent.getStringExtra(TIP_MSG_KEY);
                        mHandler.obtainMessage(PlayHandler.WHAT_VISIBLE_TIP_TV, tipMsg).sendToTarget();
                        Log.d(TAG, "mUpdateTipMsgReceiver tipMsg: " + tipMsg + " ,isShow: " + isShow);
                    } else {
                        mHandler.obtainMessage(PlayHandler.WHAT_GONE_TIP_TV, null).sendToTarget();
                        Log.d(TAG, "mUpdateTipMsgReceiver isShow: " + isShow);
                    }
                }
            }
        }
    };

    private static class PlayHandler extends Handler {
        private static final int WHAT_VISIBLE_TIP_TV = 0;
        private static final int WHAT_GONE_TIP_TV = 1;
        private static final int WHAT_PLAY_PRM = 2;
        private static final int WHAT_WEATHER = 3;
        private static final int WHAT_MODULETWO = 6;
        private static final int WHAT_MODULETHREE = 7;
        private static final int WHAT_MODULEFOUR = 8;
        private static final int WHAT_REFLUSH_WEATHER = 20;
        private static final int WHAT_CONSERVATION_OF_RESOURCES = 21;

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
                    case WHAT_VISIBLE_TIP_TV:
                        Log.d(TAG, "WHAT_VISIBLE_TIP_TV");
                        playActivity.mTipMsgTv.setVisibility(View.VISIBLE);
                        playActivity.mTipMsgTv.setText((String) msg.obj);
                        break;
                    case WHAT_GONE_TIP_TV:
                        Log.d(TAG, "WHAT_GONE_TIP_TV");
                        playActivity.mTipMsgTv.setVisibility(View.GONE);
                        break;
                    case WHAT_PLAY_PRM:
                        Log.d(TAG, "WHAT_PLAY_PRM");
                        ProgramPlayManager.getInstance().playProgramList("WHAT_PLAY_PRM");
                        break;
                    case WHAT_WEATHER:
                        Log.d(TAG, "WHAT_WEATHER");
                        WeatherInfo weatherInfo = (WeatherInfo) msg.obj;
                        playActivity.tv_temperature.setText(weatherInfo.getTemperature() + "℃");
                        playActivity.tv_weatherInfo.setText(weatherInfo.getWeatherInfo());
                        if (!TextUtils.isEmpty(weatherInfo.getWid())) {
                            if (playActivity.weatherIconMap.containsKey(weatherInfo.getWid())) {
                                playActivity.iv_weather.setImageResource(playActivity.weatherIconMap.get(weatherInfo.getWid()));
                            }
                        }
                        break;
                    case WHAT_MODULETWO:
                        Log.d(TAG, "WHAT_MODULETWO");
                        playActivity.rv_liftInfo.setLayoutManager(new LinearLayoutManager(playActivity));
                        playActivity.rv_liftInfo.addItemDecoration(new RecyclerView.ItemDecoration() {
                            @Override
                            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                                outRect.bottom = 10;
                            }
                        });
                        playActivity.rv_liftInfo.setAdapter(new LiftInfoAdapter(playActivity, playActivity.liftInfoList));
                        break;
                    case WHAT_MODULETHREE:
                        Log.d(TAG, "WHAT_MODULETHREE");
                        playActivity.rv_rate.setLayoutManager(new GridLayoutManager(playActivity, 3));
                        playActivity.rv_rate.addItemDecoration(new RecyclerView.ItemDecoration() {
                            @Override
                            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                                outRect.bottom = 20;
                            }
                        });
                        playActivity.liftRatingAdapter = new LiftRatingAdapter(playActivity, playActivity.liftRatingList);
                        playActivity.rv_rate.setAdapter(playActivity.liftRatingAdapter);
                        break;
                    case WHAT_MODULEFOUR:
                        Log.d(TAG, "WHAT_MODULEFOUR");
                        playActivity.flag = false;
                        if (!TextUtils.isEmpty(msg.getData().getString("imagePath"))) {
                            String imagePath1 = msg.getData().getString("imagePath");
                            int position = msg.getData().getInt("position");
                            Log.e("imagePath---->", "msg" + msg.getData().toString() + "imagePath1------>"
                                    + imagePath1 + "position------>" + position);
                            Glide.with(DsdpsApp.getDsdpsApp())
                                    .load(new File(imagePath1))
                                    .asBitmap()//取消GIF播放
                                    .dontAnimate()//取消动画
                                    .thumbnail(0.1f)
//                                  .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存
                                    .skipMemoryCache(true) // 不使用内存缓存
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 不使用磁盘缓存
                                    .into(playActivity.bigImage);
//                            if (playActivity.mRecyclerView.getChildAt(position) != null) {
//                                playActivity.mRecyclerView.getChildAt(position).setBackgroundResource(R.drawable.bg_image_selected); //关键
//                            }
                            String imageName = imagePath1.substring(imagePath1.lastIndexOf("/") + 1, imagePath1.length());
                            for (LiftContentBean liftContentBean : playActivity.liftContentList) {
                                if (imageName.equals(liftContentBean.getName())) {
                                    playActivity.tv_title.setText(liftContentBean.getTitle());
                                    playActivity.tv_content.setText("\u3000\u3000" + liftContentBean.getContent());
                                    playActivity.tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
                                    break;
                                }
                            }
                        } else {
                            String imagePath = (String) msg.obj;
                            Glide.with(DsdpsApp.getDsdpsApp())
                                    .load(new File(imagePath))
                                    .asBitmap()//取消GIF播放
                                    .dontAnimate()//取消动画
                                    .thumbnail(0.1f)
//                                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存
                                    .skipMemoryCache(true) // 不使用内存缓存
                                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 不使用磁盘缓存
                                    .into(playActivity.bigImage);
//                            if (playActivity.flagCount != 1 && playActivity.mRecyclerView.getChildAt(playActivity.currentPosition) != null) {
//                                playActivity.mRecyclerView.getChildAt(playActivity.currentPosition).setBackgroundResource(R.drawable.bg_image_selected); //关键
//                            }
                            String imageName = imagePath.substring(imagePath.lastIndexOf("/") + 1, imagePath.length());
                            for (LiftContentBean liftContentBean : playActivity.liftContentList) {
                                if (imageName.equals(liftContentBean.getName())) {
                                    playActivity.tv_title.setText(liftContentBean.getTitle());
                                    playActivity.tv_content.setText("\u3000\u3000" + liftContentBean.getContent());
                                    playActivity.tv_content.setMovementMethod(ScrollingMovementMethod.getInstance());
                                    break;
                                }
                            }
                        }
                        break;
                    case WHAT_REFLUSH_WEATHER:
                        playActivity.getWeatherAndDate();
                        playActivity.getModuleOneData();
                        playActivity.getModuleTwoData();
                        playActivity.getModuleThreeData();
                        playActivity.getModuleFourData();
                        break;
                    case WHAT_CONSERVATION_OF_RESOURCES:
                        playActivity.getConservationResources();
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
