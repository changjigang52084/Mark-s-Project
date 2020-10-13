package com.xunixianshi.vrshow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SettingUtil;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.obj.BannerListResult;
import com.xunixianshi.vrshow.obj.BannerResult;
import com.xunixianshi.vrshow.obj.MainClassifyHomeColumnObj;
import com.xunixianshi.vrshow.obj.eventBus.uploadNoticeEvent;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.PicassoUtil;

import java.lang.reflect.Method;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 欢迎页
 *
 * @author HeChuang
 * @time 2016/11/1 15:19
 */
public class WelcomeActivity extends BaseAct {

    Boolean isFirstIn = false;
    private static final int GO_HOME = 1000;
    private static final int GO_GUIDE = 1001;
    private static final int TIMER_MESSAGE = 1002;
    private static final int WAIT_TIME = 1003;
    @Bind(R.id.welcome_bg_iv)
    ImageView welcome_bg_iv;
    @Bind(R.id.defult_welcome_bg_iv)
    ImageView defult_welcome_bg_iv;
    @Bind(R.id.welcome_skip_page_tv)
    TextView welcome_skip_page_tv;

    private int skip_num = 3;
    private BannerListResult mBannerObj;
    private Animation operatingAnim;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_GUIDE:
                    goGuide();
                    break;
                case WAIT_TIME:
                    defult_welcome_bg_iv.startAnimation(operatingAnim);
                    break;
                case TIMER_MESSAGE:
                    skip_num--;
                    if (skip_num <= 0) {
                        mHandler.sendEmptyMessage(GO_HOME);
                    } else {
                        mHandler.sendEmptyMessageDelayed(TIMER_MESSAGE,1000);
                        welcome_skip_page_tv.setText("跳过 " + skip_num);
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void setRootView() {
        // SDK在统计Fragment时 统计页面跳转时，需要关闭Activity自带的页面统计，
//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        setImmersiveSticky();
        setContentView(R.layout.activity_welcome);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        String loginUid = SimpleSharedPreferences.getString("loginUid",
                WelcomeActivity.this);
        welcome_skip_page_tv.setText("跳过 " + skip_num);
        AppContent.UID = loginUid;
        isFirstIn = SimpleSharedPreferences.getBoolean("isFirstIn", WelcomeActivity.this);//默认是false
    }

    private void setImmersiveSticky() {
        this.getWindow()
                .getDecorView()
                .setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN
                                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
    }

    @Override
    protected void initData() {
        super.initData();
        // 设置当前版本号
        OkhttpConstant.VERSION_CODE = SettingUtil.getVersionCode(WelcomeActivity.this);
        MLog.d("versionCode: " + OkhttpConstant.VERSION_CODE);
        mBannerObj = new BannerListResult();
        String device_number = SimpleSharedPreferences.getString("DEVICE_NUMBER", WelcomeActivity.this);
        MLog.d("deviceNumber: " + device_number);
        if (device_number.equals("")) {
            getSerialNumber();
            MLog.d("serialNumber: " + getSerialNumber());
        } else {
            OkhttpConstant.DEVICE_NUMBER = device_number;
        }
        AppContent.LIMIT_LOGIN = (int) SimpleSharedPreferences.getInt("limitLogin", WelcomeActivity.this);
        operatingAnim = AnimationUtils.loadAnimation(WelcomeActivity.this, R.anim.down_alpha_out);
        operatingAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }
            @Override
            public void onAnimationEnd(Animation animation) {
                defult_welcome_bg_iv.setVisibility(View.GONE);
                welcome_skip_page_tv.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessageDelayed(TIMER_MESSAGE,1000);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        welcome_skip_page_tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goHome();
            }
        });
        getBannerData();
        getClassifyTab();
    }

    /**
     * @ClassName WelcomeActivity
     * @author HeChuang
     * @time 2016/11/2 13:50
     */
    private void getBannerData() {
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("clientType", 11);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/banner/list/service", jsonData, new GenericsCallback<BannerResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                mHandler.sendEmptyMessage(GO_HOME);
            }

            @Override
            public void onResponse(BannerResult response, int id) {
                MLog.d("id: " + id + " resCode: " + response.getResCode());
                if (response.getResCode().equals("0")) {
                    if (response.getList() != null && response.getList().size() > 0) {
                        mBannerObj.setBannerContent(response.getList().get(0).getBannerContent());
                        mBannerObj.setBannerImgUrl(response.getList().get(0).getBannerImgUrl());
                        mBannerObj.setBannerName(response.getList().get(0).getBannerName());
                        mBannerObj.setBannerTargetUrl(response.getList().get(0).getBannerTargetUrl());
                        SimpleSharedPreferences.putString("BannerImage", mBannerObj.getBannerImgUrl(), WelcomeActivity.this);
                        Picasso.with(WelcomeActivity.this).load(PicassoUtil.utf8Togb2312(mBannerObj.getBannerImgUrl())).into(welcome_bg_iv, new Callback() {
                            @Override
                            public void onSuccess() {
                                MLog.d("Response onSuccess...");
                                mHandler.sendEmptyMessageDelayed(WAIT_TIME,1000);
                            }

                            @Override
                            public void onError() {
                                //加载图片失败
                                MLog.e("Response onError...");
                                mHandler.sendEmptyMessage(GO_HOME);
                            }
                        });
                      PicassoUtil.loadBannerImage(WelcomeActivity.this,mBannerObj.getBannerImgUrl(),welcome_bg_iv,R.drawable.welcome_bg);
                    } else {
                        //获取Banner列表失败
                        MLog.e("Response get banner failed...");
                        mHandler.sendEmptyMessage(GO_HOME);
                    }
                }
            }
        });
    }

    private void getClassifyTab() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("columnType", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/column/list/service", jsonData, new GenericsCallback<MainClassifyHomeColumnObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                //tab为空
            }

            @Override
            public void onResponse(MainClassifyHomeColumnObj response, int id) {
                if (response.getResCode().equals("0")) {
                    if (response.getList() != null && response.getList().size() > 0) {
                        Gson gson = new Gson();
                        String tabData = gson.toJson(response);
                        SimpleSharedPreferences.putString("tabs", tabData, WelcomeActivity.this);
                    } else {
                        //tab为空
                    }
                } else {
                    //tab为空
                }
            }
        });
    }

    /**
     * getSerialNumber
     *
     * @return result is same to getSerialNumber1()
     */
    private String getSerialNumber() {
        String serial = null;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (serial == null || serial.equals("")) {
            serial = "" + System.currentTimeMillis() + (int) (Math.random() * 1000);
        }

        String ras_serial = VR_RsaUtils.encodeByPublicKey(serial + "_Android");
        OkhttpConstant.DEVICE_NUMBER = ras_serial;
        SimpleSharedPreferences.putString("DEVICE_NUMBER", ras_serial, WelcomeActivity.this);
        return serial;

    }

    private void goHome() {
        mHandler.removeMessages(GO_HOME);
        mHandler.removeMessages(TIMER_MESSAGE);

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        intent.putExtra("assemblingString", "0");
        WelcomeActivity.this.startActivity(intent);
        overridePendingTransition(R.anim.fade, R.anim.hold);
        AppContent.fromWelcome = true;
        WelcomeActivity.this.finish();
    }

    private void goGuide() {
        Intent intent = new Intent(WelcomeActivity.this, GuideActivity.class);
        WelcomeActivity.this.startActivity(intent);
        AppContent.fromWelcome = true;
        WelcomeActivity.this.finish();
    }

    protected void onResume() {
        super.onResume();
//        JPushInterface.onResume(WelcomeActivity.this);
//        JPushInterface.setTags(111);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        JPushInterface.onPause(WelcomeActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        mHandler.removeMessages(TIMER_MESSAGE);
        mHandler.removeMessages(WAIT_TIME);
    }
}
