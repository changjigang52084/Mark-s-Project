package com.lzmr.bindtool.ui.fragment.devices;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.DeviceDetailDto;
import com.baize.adpress.core.protocol.dto.DeviceListItemDto;
import com.baize.adpress.core.protocol.dto.DeviceScreenShotViewDto;
import com.bumptech.glide.Glide;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzkj.baize_android.utils.TimeUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.device.BaseDeviceSettingManager;
import com.lzmr.bindtool.api.device.CheckScreenshotManager;
import com.lzmr.bindtool.api.device.DeviceRebootManager;
import com.lzmr.bindtool.api.device.DeviceScreenshotManager;
import com.lzmr.bindtool.api.device.DeviceShutdownManager;
import com.lzmr.bindtool.api.device.DeviceUnbindManager;
import com.lzmr.bindtool.api.listener.BasicRequestListener;
import com.lzmr.bindtool.api.listener.CheckScreenshotListener;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.api.listener.GetDeviceDetailInfoListener;
import com.lzmr.bindtool.ui.fragment.BaseFragment;

/**
 * 项目名称：BindTool
 * 类描述：终端设置界面：重启、关机、截屏、解绑
 * 创建人：longyihuang
 * 创建时间：16/11/3 11:36
 * 邮箱：huanglongyi@17-tech.com
 */

@SuppressLint("ValidFragment")
public class DeviceSettingFragment extends BaseFragment implements  View.OnClickListener {
    private DeviceListItemDto deviceInfo; //终端信息


    private TextView mTxtDeviceName;
    private TextView mTxtShotTime;
    private Button mBtnShutdown;
    private Button mBtnReboot;
    private Button mBtnUnbind;
    private Button mBtnScreenShots;
    private Button mBtnCheckScreenshot;
    private ImageView mImgScreenshot;


    private enum DeviceSetting {
        UNBIND, REBOOT, SHUTDOWN, SCREENSHOT
    }

    public DeviceSettingFragment(ControlFragmentListener switchFragmentListener) {
        super(switchFragmentListener);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            String deviceInfoStr = bundle.getString("deviceInfo");
            if (!StringUtils.isEmpty(deviceInfoStr)) {
                try {
                    deviceInfo = JSON.parseObject(deviceInfoStr, DeviceListItemDto.class);
                    if (deviceInfo != null) {
                        String deviceId = deviceInfo.getId();
                        LogUtils.d(LogUtils.getStackTraceElement(), "deviceId:" + deviceId);
//                        DeviceDetailInfoManager manager = new DeviceDetailInfoManager(this, deviceId);
//                        manager.getDeviceDetailInfo();
//                        startProgressDialog(getString(R.string.wait), false);
                    }
                } catch (Exception e) {
                    LogUtils.e(LogUtils.getStackTraceElement(), e);
                }

            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_device_setting, container, false);
            initView();
        }
        return view;
    }

    /**
     * @Description 初始化控件
     */
    private void initView() {
        mTxtDeviceName = (TextView) view.findViewById(R.id.txt_device_name_value);
        mTxtShotTime = (TextView) view.findViewById(R.id.txt_screenshot_update_time);
        mBtnShutdown = (Button) view.findViewById(R.id.btn_shutdown);
        mBtnReboot = (Button) view.findViewById(R.id.btn_reboot);
        mBtnUnbind = (Button) view.findViewById(R.id.btn_unbind);
        mBtnScreenShots = (Button) view.findViewById(R.id.btn_screen_shots);
        mBtnCheckScreenshot = (Button) view.findViewById(R.id.btn_check_screenshot);
        mImgScreenshot = (ImageView) view.findViewById(R.id.img_screenshot);

        mBtnUnbind.setOnClickListener(this);
        mBtnShutdown.setOnClickListener(this);
        mBtnReboot.setOnClickListener(this);
        mBtnScreenShots.setOnClickListener(this);
        mBtnCheckScreenshot.setOnClickListener(this);

        if (null!=deviceInfo) {
            mTxtDeviceName.setText(deviceInfo.getName());
        }

    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_unbind:
                doDeviceSetting(DeviceSetting.UNBIND);
                break;
            case R.id.btn_shutdown:
                doDeviceSetting(DeviceSetting.SHUTDOWN);
                break;
            case R.id.btn_reboot:
                doDeviceSetting(DeviceSetting.REBOOT);
                break;
            case R.id.btn_screen_shots:
                doDeviceSetting(DeviceSetting.SCREENSHOT);
                break;
            case R.id.btn_check_screenshot:
                checkScreenshot();
                break;

        }
    }

    private void checkScreenshot() {
        if (deviceInfo != null) {
            CheckScreenshotManager manager = new CheckScreenshotManager(deviceInfo.getId(), new CheckScreenshotListener() {
                @Override
                public void onSessionInvalid() {
                    switchLoginAndCloseOther(true);
                }

                @Override
                public void onCheckScreenshot(DeviceScreenShotViewDto deviceScreenShotViewDto) {
                    if (deviceScreenShotViewDto != null) {
                        loadScreenshot(deviceScreenShotViewDto);
                    }
                }

                @Override
                public void onSuccess(String msg) {
                    dismissProgressDialog();
                }

                @Override
                public void onFailure(String msg) {
                    dismissProgressDialog();
                    showDialog(msg, null);
                }
            });
            manager.deviceControl();
            startProgressDialog(getString(R.string.check_screenshot_now), false);

        }

    }

    /**
     * @param deviceScreenShotViewDto 截屏图片
     * @Description 加载截屏图片
     */
    private void loadScreenshot(DeviceScreenShotViewDto deviceScreenShotViewDto) {
        String screenshotUrl = deviceScreenShotViewDto.getUrl();
        Long shotTime = deviceScreenShotViewDto.getCreateTime();
        if (!StringUtils.isEmpty(screenshotUrl) && mImgScreenshot != null) {
            LogUtils.d(LogUtils.getStackTraceElement(),"load screenshot:"+screenshotUrl);
            Glide
                    .with(this)
                    .load(screenshotUrl)
                    .into(mImgScreenshot);
        }
        if (shotTime!=null) {
            String shotTimeStr = TimeUtils.getTime(shotTime);
            mTxtShotTime.setText(String.format(getString(R.string.shot_time),shotTimeStr));
        }
    }

    /**
     * @param setting 操作行为（解绑、关机、重启）
     * @Description 设备操作，包含解绑、关机、重启
     */
    private void doDeviceSetting(DeviceSetting setting) {
        if (null != deviceInfo) {
            BaseDeviceSettingManager manager = null;
            String tips = null;
            String wait = null;
            switch (setting) {
                case UNBIND:
                    LogUtils.d(LogUtils.getStackTraceElement(), "unbind device:" + deviceInfo.getName());
                    manager = new DeviceUnbindManager(deviceInfo.getId(), requestListener);
                    tips = getString(R.string.unbind_confirm);
                    wait = getString(R.string.unbind_now);
                    break;
                case REBOOT:
                    LogUtils.d(LogUtils.getStackTraceElement(), "reboot device:" + deviceInfo.getName());
                    manager = new DeviceRebootManager(deviceInfo.getId(), requestListener);
                    tips = getString(R.string.reboot_confirm);
                    wait = getString(R.string.reboot_now);
                    break;
                case SHUTDOWN:
                    LogUtils.d(LogUtils.getStackTraceElement(), "shutdown device:" + deviceInfo.getName());
                    manager = new DeviceShutdownManager(deviceInfo.getId(), requestListener);
                    tips = getString(R.string.shutdown_confirm);
                    wait = getString(R.string.shutdown_now);
                    break;
                case SCREENSHOT:
                    LogUtils.d(LogUtils.getStackTraceElement(), "screenshot:" + deviceInfo.getName());
                    manager = new DeviceScreenshotManager(deviceInfo.getId(), requestListener);
                    wait = getString(R.string.screenshot_now);
                    break;
            }
            if (manager != null) {
                final BaseDeviceSettingManager m = manager;
                final String w = wait;
                if (tips != null) {
                    showDialog(tips, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            m.deviceControl();
                            startProgressDialog(w, false);
                        }
                    });
                } else {
                    m.deviceControl();
                    startProgressDialog(w, false);
                }

            }
        }
    }

    private SessionRequestListener requestListener = new SessionRequestListener() {
        @Override
        public void onSessionInvalid() {
            dismissProgressDialog();
            switchLoginAndCloseOther(true);
        }

        @Override
        public void onSuccess(String msg) {
            dismissProgressDialog();
            showDialog(msg, null);
            checkIsUnbind(msg);
        }

        @Override
        public void onFailure(String msg) {
            dismissProgressDialog();
            showDialog(msg, null);
        }
    };

    private void checkIsUnbind(String msg) {
        LogUtils.d(LogUtils.getStackTraceElement(),"msg:"+msg);
        String unbindSuccessStr = getString(R.string.unbind_success);
        if (unbindSuccessStr.equals(msg)) {
            getFragmentManager().popBackStack();
        }
    }


}
