package com.xunixianshi.vrshow;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.hch.utils.OkhttpConstant;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.classify.ClassifyFragment;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.find.FindFragment;
import com.xunixianshi.vrshow.fine.FineFragment;
import com.xunixianshi.vrshow.my.MyFragment;
import com.xunixianshi.vrshow.obj.MainClassifyHomeColumnObj;
import com.xunixianshi.vrshow.recyclerview.VideoExpandableActivity;
import com.xunixianshi.vrshow.show.ShowFragment;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.util.UpdateManager;
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

/**
 * app 入口 首页
 *@author HeChuang
 *@time 2016/11/1 15:20
 */
public class MainActivity extends VideoExpandableActivity implements View.OnClickListener {
    @Bind(R.id.home_tab_one_rl)
    RelativeLayout home_tab_one_rl;
    @Bind(R.id.home_tab_two_rl)
    RelativeLayout home_tab_two_rl;
    @Bind(R.id.home_tab_three_rl)
    RelativeLayout home_tab_three_rl;
    @Bind(R.id.home_tab_four_rl)
    RelativeLayout home_tab_four_rl;
    @Bind(R.id.home_tab_five_rl)
    RelativeLayout home_tab_five_rl;
    private RelativeLayout tempLayout;
    private Fragment lastFragment;
    private FragmentTransaction transaction;
    private FragmentManager fragmentManager;
    private Fragment fragment = null;
    private static final int MENU_1 = 1;
    private static final int MENU_2 = 2;
    private static final int MENU_3 = 3;
    private static final int MENU_4 = 4;
    private static final int MENU_5 = 5;
    private boolean isExit = false;
    private LoadingAnimationDialog progressDialog;
    private String assemblingString;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (OkhttpConstant.DEVICE_NUMBER.equals("")) {
            OkhttpConstant.DEVICE_NUMBER = SimpleSharedPreferences.getString("DEVICE_NUMBER", MainActivity.this);
        }
        AppContent.LIMIT_LOGIN = (int) SimpleSharedPreferences.getInt("limitLogin", MainActivity.this);
    }

    /**
     * 初始化控件
     */
    @Override
    protected void initView() {
        home_tab_three_rl.performClick();
        initListener();

    }

    /**
     * 初始化监听器
     */
    private void initListener() {
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        int width = metric.widthPixels; // 屏幕宽度（像素）
        int height = metric.heightPixels; // 屏幕高度（像素）
        float density = metric.density; // 屏幕密度（0.75 / 1.0 / 1.5）
        int densityDpi = metric.densityDpi; // 屏幕密度DPI（120 / 160 / 240）
        MLog.d("width: " + width + " ,height: " + height + " ,density: " + density
                + " ,densityDpi: " + densityDpi);
    }

    /**
     * 初始化数据
     */
    @Override
    protected void initData() {
        progressDialog = new LoadingAnimationDialog(MainActivity.this);
        assemblingString = getIntent().getExtras().get("assemblingString").toString();
        if (!assemblingString.equals("0")) {
//            syncTab();
        }
        SimpleSharedPreferences.putBoolean("isFirstIn",true, MainActivity.this);
        if (AppContent.fromWelcome) {
            // 此接口被调用时会进行版本检测，若有新版本会弹出对话框提示用户。
            UpdateManager manager = new UpdateManager(MainActivity.this);
            manager.getServerVersionCode(false);
            AppContent.fromWelcome = false;
        }
    }

    @OnClick({R.id.home_tab_two_rl, R.id.home_tab_three_rl, R.id.home_tab_one_rl, R.id.home_tab_four_rl, R.id.home_tab_five_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.home_tab_one_rl://精品
                clearChooseLayout(home_tab_one_rl);
                showTFragment(MENU_1);
                break;
            case R.id.home_tab_two_rl://分类
                clearChooseLayout(home_tab_two_rl);
                showTFragment(MENU_2);
                break;
            case R.id.home_tab_three_rl://SHOW
                clearChooseLayout(home_tab_three_rl);
                showTFragment(MENU_3);
                break;
            case R.id.home_tab_four_rl://发现
                clearChooseLayout(home_tab_four_rl);
                showTFragment(MENU_4);
                break;
            case R.id.home_tab_five_rl://我的
                clearChooseLayout(home_tab_five_rl);
                showTFragment(MENU_5);
                break;
        }
    }

    private void clearChooseLayout(RelativeLayout layout) {
        if (tempLayout != null) {
            tempLayout.setSelected(false);
        }
        tempLayout = layout;
        tempLayout.setSelected(true);
    }

    /**
     * @param @param id 设定文件
     * @return void 返回类型
     * @Title: showTFragment
     * @Description: TODO 打开响应的Fragment
     * @author hechuang
     * @date 2016-3-3
     */
    private void showTFragment(int id) {
        String tag = id + "";
        if (fragmentManager == null) {
            fragmentManager = getSupportFragmentManager();
        }
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        transaction = fragmentManager.beginTransaction();
        if (fragment == null) {
            fragment = getFragmentByTag(id);
            MLog.d("transaction:::" + transaction);
            MLog.d("fragment:::" + fragment);
            transaction.add(R.id.vrshow_main_content, fragment, tag);
        } else {
            transaction.show(fragment);
        }
        MLog.d("fragment::" + fragment);
        if (lastFragment != null && lastFragment != fragment) {
            transaction.hide(lastFragment);
        }
        lastFragment = fragment;
        transaction.commit();
    }

    /**
     * 根据id 获取对应的fragment
     *
     * @param tag
     * @return
     */
    private Fragment getFragmentByTag(int tag) {
        switch (tag) {
            case MENU_1:
                fragment = new FineFragment();
                break;
            case MENU_2:
                fragment = new ClassifyFragment();
                break;
            case MENU_3:
                fragment = new ShowFragment();
                break;
            case MENU_4:
                fragment = new FindFragment();
                break;
            case MENU_5:
                fragment = new MyFragment();
                break;
            default:
                break;
        }
        return fragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    /**
     * 重写Activity中onKeyDown()方法
     */
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            ToQuitTheApp();
            return false;
        } else {
            return super.onKeyDown(keyCode, event);
        }
    }

    /**
     * 封装ToQuitTheApp方法
     */
    private void ToQuitTheApp() {
        if (isExit) {
            // ACTION_MAIN with category CATEGORY_HOME 启动主屏幕
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            startActivity(intent);
            // 使虚拟机停止运行并退出程序
            System.exit(0);
        } else {
            isExit = true;
            showToastMsg("再按一次退出VRShow");
            // 3秒后发送消息
            mHandler.sendEmptyMessageDelayed(0, 3000);
        }
    }

    // 创建Handler对象，用来处理消息
    @SuppressLint("HandlerLeak")
    Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            isExit = false;
        }
    };

    /**
     * 同步服务器标签
     */
    private void syncTab() {
        progressDialog.show();
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        MLog.d("assemblingString:" + assemblingString);
        hashMap.put("tagIds", assemblingString);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/guide/tag/synchronize/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                MLog.d("response111111:" + result.toString());
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {

            }
        });
    }
}
