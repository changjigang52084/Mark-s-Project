package com.sunchip.adw.cloudphotoframe.set;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.sunchip.adw.cloudphotoframe.BaseActivity;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.Timer.MyCountTimer;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.set.fragment.AdvancedFragment;
import com.sunchip.adw.cloudphotoframe.set.fragment.DisplayVolumeFragment;
import com.sunchip.adw.cloudphotoframe.set.fragment.InformationFragment;
import com.sunchip.adw.cloudphotoframe.set.fragment.NetworkFragment;
import com.sunchip.adw.cloudphotoframe.set.fragment.SettingsFragment;
import com.sunchip.adw.cloudphotoframe.set.fragment.ShowHideFragment;
import com.sunchip.adw.cloudphotoframe.set.fragment.SoftwareUpdateFragment;
import com.sunchip.adw.cloudphotoframe.set.fragment.TimeZoneFragment;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;

public class SettingActivity extends BaseActivity {

    private static final String TAG = "SettingActivity";

    private TextView settings_rb;
    private TextView show_hide_rb;
    private TextView network_rb;
    private TextView display_volume_rb;
    private TextView time_zone_rb;
    private TextView software_update_rb;
    private TextView advanced_rb;
    private TextView information_rb;

    //默认选择的界面
    private int Select = 0; //默认第一个界面

    boolean ret = false;
    private SettingsFragment settingsFragment;
    private ShowHideFragment showHideFragment;
    private NetworkFragment networkFragment;
    private DisplayVolumeFragment displayVolumeFragment;
    private TimeZoneFragment timeZoneFragment;
    private SoftwareUpdateFragment softwareUpdateFragment;
    private AdvancedFragment advancedFragment;
    private InformationFragment informationFragment;
    private FragmentManager fm;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        setContentView(R.layout.activity_setting);

        initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MyCountTimer.setStartMyCount("Settingactivity_onResume");
//        Log.e("TAG","悬浮窗是否消失:onResume"+"============================");
    }

    public void onSaveInstanceState(Bundle outState) {
//         TODO Auto-generated method stub
        //super.onSaveInstanceState(outState);   //将这一行注释掉，阻止activity保存fragment的状态
//        super.onSaveInstanceState(outState);
    }

    //因为某个frament中有listView抢占onKeyevent事件
    // 那么我们就使用dispatchKeyEvent去设置按钮事件
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.e("SettingActivity", "dispatchKeyEvent:" + activityParseOnkey(event.getKeyCode())
                + " 键值是:" + event.getKeyCode());
        //直接开始倒计时
        if (event.getKeyCode() != 350)
            MyCountTimer.setStartMyCount("SettingActivity_dispatchKeyEvent");

        //防止back无效 下面返回true是防止frament中的内容出现响应按键事件
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            return super.dispatchKeyEvent(event);
        }


        int keyCode = event.getKeyCode();
        //为了防止事件触发两次 我们只在按下或者抬起事件其一的时候去触发
        if (event.getAction() == KeyEvent.ACTION_UP) {
            //防止启动两次
            if (event.getKeyCode() == 352) {
                //视频键 352  好像并没有用到
                staerActibity(1);
                return super.dispatchKeyEvent(event);
            } else if (event.getKeyCode() == 351) {
                //播放相片键351
                staerActibity(0);
                return super.dispatchKeyEvent(event);
            }
            ret = activityParseOnkey(keyCode);
            //只能在控制activity的时候使用这些方法
            if (event.getKeyCode() == 354 || event.getKeyCode() == 350) {
                return true;
            }
            if (!ret) {
                setTabSelection(Select);
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    //上
                    Select--;
                    if (Select <= 0) {
                        Select = 0;
                    }
                    setTabSelection(Select);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    //下
                    Select++;
                    if (Select >= 7) {
                        Select = 7;
                    }
                    setTabSelection(Select);
                }
                return true;
            } else {
                IntoBackground();
                //左右的时候可控制的frament
                Log.e("TAG", "Select:" + Select + " event :" + event.getKeyCode());
                if (Select == 0) {
                    settingsFragment.onKeyDown(event);
                } else if (Select == 1) {
                    showHideFragment.onKeyDown(event);
                } else if (Select == 2) {
                    networkFragment.onKeyDown(event);
                } else if (Select == 3) {
                    displayVolumeFragment.onKeyDown(event);
                } else if (Select == 4) {
                    timeZoneFragment.onKeyDown(event);
                } else if (Select == 5) {
                    softwareUpdateFragment.onKeyDown(event);
                } else if (Select == 6) {
                    advancedFragment.onKeyDown(event);
                } else if (Select == 7) {
                    informationFragment.onKeyDown(event);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void setected(int postion) {
        if (Select == 0) {
            settingsFragment.Select(postion);
        } else if (Select == 1) {
            showHideFragment.Select(postion);
        } else if (Select == 2) {
            networkFragment.Select(postion);
        } else if (Select == 3) {
            displayVolumeFragment.Select(postion);
        } else if (Select == 4) {
            timeZoneFragment.Select(postion);
        } else if (Select == 5) {
            softwareUpdateFragment.Select(postion);
        } else if (Select == 6) {
            advancedFragment.Select(postion);
        } else if (Select == 7) {
            informationFragment.Select(postion);
        }
    }

    private boolean activityParseOnkey(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                ret = true;
                setected(0);
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                ret = false;
                setected(-1);
                break;

        }
        return ret;
    }


    private void initView() {
        settings_rb = findViewById(R.id.settings_rb);
        show_hide_rb = findViewById(R.id.show_hide_rb);
        network_rb = findViewById(R.id.network_rb);
        display_volume_rb = findViewById(R.id.display_volume_rb);
        time_zone_rb = findViewById(R.id.time_zone_rb);
        software_update_rb = findViewById(R.id.software_update_rb);
        advanced_rb = findViewById(R.id.advanced_rb);
        information_rb = findViewById(R.id.information_rb);

        settings_rb.setTypeface(CloudFrameApp.typeFace);
        show_hide_rb.setTypeface(CloudFrameApp.typeFace);
        network_rb.setTypeface(CloudFrameApp.typeFace);
        display_volume_rb.setTypeface(CloudFrameApp.typeFace);
        time_zone_rb.setTypeface(CloudFrameApp.typeFace);
        software_update_rb.setTypeface(CloudFrameApp.typeFace);
        advanced_rb.setTypeface(CloudFrameApp.typeFace);
        information_rb.setTypeface(CloudFrameApp.typeFace);

        fm = getSupportFragmentManager();
        setTabSelection(0);
    }


    //初始化颜色
    private void IntoBackground() {
        settings_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        show_hide_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        network_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        display_volume_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        time_zone_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        software_update_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        advanced_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        information_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
    }


    private void setTabSelection(int index) {
        IntoBackground();
        FragmentTransaction ft = fm.beginTransaction();
        hideFragment(ft);

        //自定义半天动画没搞定 使用默认的动画模式
//        ft.setCustomAnimations(
//                R.anim.slide_right_out,
//                R.anim.slide_right_in
//        );
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);

        switch (index) {
            case 0:
                settings_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (settingsFragment == null) {
                    settingsFragment = new SettingsFragment();
                    ft.add(R.id.fl, settingsFragment);
                } else {
                    ft.show(settingsFragment);
                }
                break;
            case 1:
                show_hide_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (showHideFragment == null) {
                    showHideFragment = new ShowHideFragment();
                    ft.add(R.id.fl, showHideFragment);
                } else {
                    ft.show(showHideFragment);
                }
                break;
            case 2:
                network_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (networkFragment == null) {
                    networkFragment = new NetworkFragment();
                    ft.add(R.id.fl, networkFragment);
                } else {
                    ft.show(networkFragment);
                }
                break;
            case 3:
                display_volume_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (displayVolumeFragment == null) {
                    displayVolumeFragment = new DisplayVolumeFragment();
                    ft.add(R.id.fl, displayVolumeFragment);
                } else {
                    ft.show(displayVolumeFragment);
                }
                break;
            case 4:
                time_zone_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (timeZoneFragment == null) {
                    timeZoneFragment = new TimeZoneFragment();
                    ft.add(R.id.fl, timeZoneFragment);
                } else {
                    ft.show(timeZoneFragment);
                }
                break;
            case 5:
                software_update_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (softwareUpdateFragment == null) {
                    softwareUpdateFragment = new SoftwareUpdateFragment();
                    ft.add(R.id.fl, softwareUpdateFragment);
                } else {
                    ft.show(softwareUpdateFragment);
                }
                break;
            case 6:
                advanced_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (advancedFragment == null) {
                    advancedFragment = new AdvancedFragment();
                    ft.add(R.id.fl, advancedFragment);
                } else {
                    ft.show(advancedFragment);
                }
                break;
            case 7:
                information_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                if (informationFragment == null) {
                    informationFragment = new InformationFragment();
                    ft.add(R.id.fl, informationFragment);
                } else {
                    ft.show(informationFragment);
                }
                break;
            default:
                break;
        }
        ft.commitAllowingStateLoss();
    }

    private void hideFragment(FragmentTransaction ft) {
        if (settingsFragment != null) {
            ft.hide(settingsFragment);
        }
        if (showHideFragment != null) {
            ft.hide(showHideFragment);
        }
        if (networkFragment != null) {
            ft.hide(networkFragment);
        }
        if (displayVolumeFragment != null) {
            ft.hide(displayVolumeFragment);
        }
        if (timeZoneFragment != null) {
            ft.hide(timeZoneFragment);
        }
        if (softwareUpdateFragment != null) {
            ft.hide(softwareUpdateFragment);
        }
        if (advancedFragment != null) {
            ft.hide(advancedFragment);
        }
        if (informationFragment != null) {
            ft.hide(informationFragment);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyCountTimer.setCloseMyCount();
    }
}