package com.lzkj.launcher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

import com.lzkj.launcher.fragment.AppMenuFragment;
import com.lzkj.launcher.fragment.LoginFragment;
import com.lzkj.launcher.impl.SwitchLayoutImpl;
import com.lzkj.launcher.service.LoginCallbackManager;
import com.lzkj.launcher.service.LoginRequestManager;
import com.lzkj.launcher.util.ConfigSettings;
import com.lzkj.launcher.util.Constant;

/**
 * 项目名称：Launcher 类名称：MainActivity 类描述： 创建人："lyhuang" 创建时间：2015年8月7日 下午12:02:01
 */
@SuppressLint("NewApi")
public class MainActivity extends Activity implements SwitchLayoutImpl {

    private static final String TAG = "MainActivity";

    private static MainActivity activity;

    /**
     * fragment管理类
     */
    private FragmentManager fragmentManager;

    /**
     * 登录界面
     */
    private LoginFragment loginFragment;

    /**
     * app界面
     */
    private AppMenuFragment appMenuFragment;

    public static MainActivity getActivity() {
        return activity;
    }

    private boolean showLogin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "MainActivity onCreate.");
        // 屏幕保存常亮
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activity = this;
        setContentView(R.layout.activity_main);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    /**
     * 初始化view
     */
    private void initView() {
        fragmentManager = getFragmentManager();
        if (showLogin) {
            switchLoginFragment();
        } else {
            if (ConfigSettings.isClientValid()) {
                Log.d(TAG, "MainActivity initView client has bind.");
                //进入等待广告界面
                switchAppMemuFragment();
            } else {
                Log.i(TAG, "MainActivity initView client not bind.");
                switchLoginFragment();
            }
        }
    }

    /**
     * 切换到登录界面
     */
    private void switchLoginFragment() {
        Log.d(TAG, "MainActivity switchLoginFragment play login fragment.");
        LoginRequestManager.newInstance();
        loginFragment = new LoginFragment(this);
        LoginCallbackManager.newInstance().addLoginCallback(loginFragment);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, loginFragment);
        Log.e(TAG, "MainActivity switchLoginFragment activity: " + this);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * @return void    返回类型
     * @Title: switchAppMemuFragment
     * @Description: TODO(切换app界面)
     * @Param 设定文件
     */
    private void switchAppMemuFragment() {
        Log.d(TAG, "MainActivity switchAppMemuFragment play app menu fragment.");
        appMenuFragment = new AppMenuFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, appMenuFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void switchLayout(int fragmentId) {
        switch (fragmentId) {
            case Constant.LOGIN_FRAGMENT:
                switchLoginFragment();
                break;
            case Constant.ALL_APP_FRAGMENT:
                //启动等待界面
                if (showLogin) {
                    switchLoginFragment();
                } else {
                    switchAppMemuFragment();
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "MainActivity onDestroy.");
        super.onDestroy();
    }
}
