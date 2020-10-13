package com.unccr.zclh.dsdps.setting;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.service.heart.RequestHelper;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.LogcatHelper;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.ToastUtil;

public class SettingsActivity extends Activity implements View.OnClickListener {


    private static final String TAG = "SettingsActivity";
    private ImageView settingIv;
    private Button screenSettingBtn;
    private Button clearProgramBtn;
    private Button serverUnbindBtn;
    private Button confirmBtn;
    private EditText ipAddressEt;
    private Button startCheckNetBtn;
    private Button startGetLogBtn;
    private Button stopGetLogBtn;
    private StorageManager storageManager;
    private Button exitProcedureBtn;
    private Context context;

    private TextView localAccountTv,calleeAccountTv;
    private EditText localAccountEt,localPasswordEt,localPortEt,localIpEt,calleeAccountEt;
    private Button exchangeBtn;

    private static RequestHelper requestHelper = new RequestHelper();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        initView();
        storageManager = (StorageManager) getSystemService(Context.STORAGE_SERVICE);
        context = SettingsActivity.this;
        updateAccount();
    }

    private void updateAccount(){
        String localAccount = SharedUtil.newInstance().getString(Constants.NAME);
        if(!TextUtils.isEmpty(localAccount)){
            localAccountTv.setText(localAccount);
        }else{
            localAccountTv.setText(Constants.defaultName);
        }
        String calleeAccount = SharedUtil.newInstance().getString(Constants.CALL_NUMBER);
        if(!TextUtils.isEmpty(calleeAccount)){
            calleeAccountTv.setText(calleeAccount);
        }else{
            calleeAccountTv.setText(Constants.defaultCallNumber);
        }
    }

    private void initView() {
        localAccountTv = findViewById(R.id.display_local_video_intercom_account_tv);
        calleeAccountTv = findViewById(R.id.display_callee_account_number_tv);
        localAccountEt = findViewById(R.id.account_et);
        localPasswordEt = findViewById(R.id.password_et);
        localPortEt = findViewById(R.id.port_et);
        localIpEt = findViewById(R.id.ip_et);
        calleeAccountEt = findViewById(R.id.callee_et);
        exchangeBtn = findViewById(R.id.exchange_account_btn);

        settingIv = findViewById(R.id.setting_iv);
        screenSettingBtn = findViewById(R.id.screen_setting_btn);
        clearProgramBtn = findViewById(R.id.clear_program_btn);
        serverUnbindBtn = findViewById(R.id.server_unbind_btn);
        confirmBtn = findViewById(R.id.confirm_btn);
        ipAddressEt = findViewById(R.id.ipAddress_et);
        startCheckNetBtn = findViewById(R.id.start_check_net_btn);
        startGetLogBtn = findViewById(R.id.start_get_log_btn);
        stopGetLogBtn = findViewById(R.id.stop_get_log_btn);
        exitProcedureBtn = findViewById(R.id.exit_procedure_btn);
        initListener();
    }

    private void initListener() {
        exchangeBtn.setOnClickListener(this);
        settingIv.setOnClickListener(this);
        screenSettingBtn.setOnClickListener(this);
        clearProgramBtn.setOnClickListener(this);
        serverUnbindBtn.setOnClickListener(this);
        confirmBtn.setOnClickListener(this);
        startCheckNetBtn.setOnClickListener(this);
        startGetLogBtn.setOnClickListener(this);
        stopGetLogBtn.setOnClickListener(this);
        exitProcedureBtn.setOnClickListener(this);
    }

    private void showToastMsg(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_iv: // 返回
                finish();
                break;
            case R.id.screen_setting_btn: // 横竖屏设置
                if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE) {// 如果是横屏，则改为竖屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                } else if (getRequestedOrientation() == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT) {// 如果是竖屏，则改为横屏
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
                }
                break;
            case R.id.clear_program_btn: // 清空节目
                requestHelper.clearProgramApi();
                finish();
                break;
            case R.id.confirm_btn: // 服务器绑定
                String ip = ipAddressEt.getText().toString().trim();
                if (TextUtils.isEmpty(ip)) {
                    Toast.makeText(this, "服务器地址不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                SharedUtil.newInstance().setString("ipAddress", ip);
                Toast.makeText(this, "绑定新服务器地址成功", Toast.LENGTH_SHORT).show();
                finish();
                break;
            case R.id.server_unbind_btn: // 服务器解绑
                requestHelper.unbindServerApi();
                finish();
                break;
            case R.id.start_check_net_btn: // 开始检测网络
                requestHelper.checkNetApi();
                finish();
                break;
            case R.id.start_get_log_btn: // 开始抓取日志
                showToastMsg("开始抓取日志");
                LogcatHelper.getInstance(this).start();
                break;
            case R.id.stop_get_log_btn: // 停止日志抓取
                LogcatHelper.getInstance(this).stop();
                showToastMsg("停止抓取日志，日志保存在/mnt/sdcard/zclh/log/里面");
                break;
            case R.id.exit_procedure_btn:
                Intent intent = new Intent();
                intent.setClass(SettingsActivity.this, PlayActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.exchange_account_btn:
                String localAccount = localAccountEt.getText().toString().trim();
                String localPassword = localPasswordEt.getText().toString().trim();
                String localPort = localPortEt.getText().toString().trim();
                String localIp = localIpEt.getText().toString().trim();
                String calleeAccount = calleeAccountEt.getText().toString().trim();
                if(!TextUtils.isEmpty(localAccount)){
                    SharedUtil.newInstance().setString(Constants.NAME,localAccount);
                    if(!TextUtils.isEmpty(localPassword)){
                        SharedUtil.newInstance().setString(Constants.PW,localPassword);
                        if(!TextUtils.isEmpty(localPort)){
                            SharedUtil.newInstance().setInt(Constants.PORT, Integer.parseInt(localPort));
                            if(!TextUtils.isEmpty(localIp)){
                                SharedUtil.newInstance().setString(Constants.IP,localIp);
                                if(!TextUtils.isEmpty(calleeAccount)){
                                    SharedUtil.newInstance().setString(Constants.CALL_NUMBER,calleeAccount);
                                    ConfigSettings.SIP_IS_LOGIN = false;
                                    finish();
                                }else{
                                    ToastUtil.shortShow("请输入被呼叫方账号");
                                }
                            }else{
                                ToastUtil.shortShow("请输入ip");
                            }
                        }else{
                            ToastUtil.shortShow("请输入端口号");
                        }
                    }else{
                        ToastUtil.shortShow("请输入密码");
                    }
                }else{
                    ToastUtil.shortShow("请输入账号");
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}














