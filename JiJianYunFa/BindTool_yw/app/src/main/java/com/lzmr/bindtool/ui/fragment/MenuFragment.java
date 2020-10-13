package com.lzmr.bindtool.ui.fragment;

import com.baize.adpress.core.protocol.dto.DeviceListDto;
import com.baize.adpress.core.protocol.dto.DeviceStatisticsDto;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.DevicesInfoManager;
import com.lzmr.bindtool.api.LogoutRequestManager;
import com.lzmr.bindtool.api.listener.GetDevicesInfoListener;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.ui.fragment.devices.LazyLoadFragment;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;
import com.lzmr.bindtool.util.ShareUtil;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


import static com.lzmr.bindtool.ui.fragment.devices.AllDeviceFragment.UPDATE_FLAG_REFRESH;


/**
 * 项目名称：MerchantMall
 * 类名称：MerchantCenterFragment
 * 类描述：菜单界面
 * 创建人：lyhuang
 * 创建时间：2015-8-28 上午11:35:19
 */
@SuppressLint("ValidFragment")
public class MenuFragment extends LazyLoadFragment implements OnClickListener, GetDevicesInfoListener {
    private static final LogTag TAG = LogUtils.getLogTag(MenuFragment.class.getSimpleName(), true);
    /**
     * 扫码绑定
     */
    private Button bindDevice;
    /**
     * 所有终端按钮
     */
    private Button allDeviceBtn;
    /**
     * 离线终端按钮
     */
    private Button offDeviceBtn;
    /**
     * 待机终端按钮
     */
    private Button standbyDeviceBtn;

    /**
     * 退出登录
     */
    private Button logoutBtn;

    private TextView allDeviceNumTxt, offlineDeviceNumTxt, standbyDeviceNumTxt, userNameTv;

    /**
     * 刷新按钮
     */
    private Button refreshBtn;
    /**
     * DSPLUG LOGO
     */
    private ImageView imgLogo;


    public MenuFragment(ControlFragmentListener switchFragmentListener) {
        super(switchFragmentListener);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_menu;
    }

    @Override
    public void initViews(View view) {
        bindDevice = (Button) view.findViewById(R.id.btn_new_bind_device);
        bindDevice.setOnClickListener(this);

        allDeviceBtn = (Button) view.findViewById(R.id.btn_all_device);
        allDeviceBtn.setOnClickListener(this);

        offDeviceBtn = (Button) view.findViewById(R.id.btn_offline_device);
        offDeviceBtn.setOnClickListener(this);

        standbyDeviceBtn = (Button) view.findViewById(R.id.btn_standby_device);
        standbyDeviceBtn.setOnClickListener(this);

        refreshBtn = (Button) view.findViewById(R.id.btn_refresh);
        refreshBtn.setOnClickListener(this);

        logoutBtn = (Button) view.findViewById(R.id.btn_exit);
        logoutBtn.setOnClickListener(this);

        allDeviceNumTxt = (TextView) view.findViewById(R.id.tv_device_number);
        offlineDeviceNumTxt = (TextView) view.findViewById(R.id.tv_offline_device_number);
        standbyDeviceNumTxt = (TextView) view.findViewById(R.id.tv_standby_number);
        userNameTv = (TextView) view.findViewById(R.id.tv_user_name);
        userNameTv.setText(ShareUtil.newInstance().getString(ShareUtil.USER_ACCOUNT));
        imgLogo = (ImageView) view.findViewById(R.id.img_logo);

        if(ConfigSettings.CLIENT_TYPE == Constants.YW){
            imgLogo.setImageResource(R.drawable.logo_icon_yw);
        }
    }

    @Override
    public void loadData() {
        updateDeviceInfo();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();

    }

    public void updateDeviceInfo() {
        DevicesInfoManager devicesInfoManager = new DevicesInfoManager(
                this, UPDATE_FLAG_REFRESH, 1, 1, 0);
        devicesInfoManager.loadDevices();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_new_bind_device:
                switchScanning(null,true);
                break;
            case R.id.btn_all_device:
                switchToDeviceListFragment(0);
                break;
            case R.id.btn_offline_device:
                switchToDeviceListFragment(1);
                break;
            case R.id.btn_standby_device:
                switchToDeviceListFragment(2);
                break;
            case R.id.btn_refresh:
                updateDeviceInfo();
                break;
            case R.id.btn_exit:
                doLogout(true);
                break;
            default:
                break;
        }

    }

    private void doLogout(boolean requestLogout){
        if(requestLogout){
            LogoutRequestManager logoutRequestManager = new LogoutRequestManager(null);
            logoutRequestManager.logout();
            ShareUtil.newInstance().removeKey(ShareUtil.USER_ACCOUNT);
            ShareUtil.newInstance().removeKey(ShareUtil.USER_PASSWORD);
            ShareUtil.newInstance().setBoolean(ShareUtil.REMEMBER_ACCOUNT, false);
        }
        ShareUtil.newInstance().removeKey(ShareUtil.SESSION);
        ShareUtil.newInstance().removeKey(ShareUtil.PUBLIC_KEY);
        ShareUtil.newInstance().removeKey(ShareUtil.USER_KEY);
        switchLoginAndCloseOther(false);
    }

    /**
     * @param tabsIndex tabs 下标位置
     * @return void    返回类型
     * @Title: switchToDeviceListFragment
     * @Description: 切换到终端管理界面
     */
    private void switchToDeviceListFragment(int tabsIndex) {
        Bundle bundle = new Bundle();
        bundle.putInt("tabsIndex", tabsIndex);
        bundle.putBoolean("addToBackStack",true);
        LogUtils.e(TAG, "switchToDeviceListFragment", "tabsIndex:" + tabsIndex);
        switchDevice(bundle,true);
    }

    private void updateView(DeviceStatisticsDto statistics) {
        allDeviceNumTxt.setText(String.valueOf(statistics.getTotal()));
        offlineDeviceNumTxt.setText(String.valueOf(statistics.getOffline()));
        standbyDeviceNumTxt.setText(String.valueOf(statistics.getShutdown()));
    }

    @Override
    public void onGetDevicesInfo(int updateFlag, DeviceListDto devices) {
        if (null == devices) {
            LogUtils.e(TAG, "onGetDevicesInfo", "Load device data failed.");
            return;
        }
        final DeviceStatisticsDto statistics = devices.getStatistics();
        if (null != statistics) {
            allDeviceBtn.post(new Runnable() {
                @Override
                public void run() {
                    updateView(statistics);
                }
            });
        }else{
            LogUtils.e(TAG, "onGetDevicesInfo", "statistics is null");
        }

    }

    @Override
    public void onSuccess(String msg) {
    }

    @Override
    public void onFailure(String msg) {
        showDialog(msg, null);
    }

    @Override
    public void onSessionInvalid() {
        switchLoginAndCloseOther(true);
    }
}
