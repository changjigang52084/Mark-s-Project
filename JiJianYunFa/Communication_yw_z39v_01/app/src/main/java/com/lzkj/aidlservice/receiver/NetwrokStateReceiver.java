package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.aidlservice.api.impl.RetryReportMsgImpl;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.manager.BaiduLocationManager;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.manager.ReportDeviceStatusManager;
import com.lzkj.aidlservice.manager.ValidationDeviceManager;
import com.lzkj.aidlservice.service.StorageSpaceService;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.PushUitl;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;
import com.lzkj.aidlservice.util.Util;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月24日 下午3:47:31
 * @parameter 监听网络变化的receive
 */
public class NetwrokStateReceiver extends BroadcastReceiver implements IRequestCallback {

    private static final String TAG = "NetwrokStateReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = connectivityManager.getActiveNetworkInfo();
        if (info != null && info.isAvailable()) {
            Log.d(TAG, "NetwrokStateReceiver onReceive network is available.");
            //updateState(context);
        }else {
            Log.d(TAG,"NetwrokStateReceiver onReceive network not available.");
        }
    }

    private void updateState(Context context) {
        new BaiduLocationManager().startLocation();//启动定位
        PushUitl.initGeTuiPush();
        PushUitl.initJPush();
        updateDevicePrmState();
        validationDeviceAuther();//校验终端是否绑定
        updateDeviceFunctionState();
        reportStorageSpace(context);
        retryUnfinishedReport();
    }

    /**
     * 重新发送回执到服务器
     */
    private void retryUnfinishedReport() {
        RetryReportMsgImpl retryReportMsg = new RetryReportMsgImpl();
        retryReportMsg.retrySendReport();
    }

    /**
     * 校验授权
     */
    private void validationDeviceAuther() {
        ValidationDeviceManager.get().validationDevice(ConfigSettings.MAC_ADDRESS, this);
    }

    /**
     * 汇报磁盘信息
     *
     * @param context
     */
    private void reportStorageSpace(Context context) {
        Intent storageIntentService = new Intent(context, StorageSpaceService.class);
        storageIntentService.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(storageIntentService);
    }

    /**
     * 汇报终端运行状态(待机or工作中)
     */
    private void updateDeviceFunctionState() {
        if (ConfigSettings.isClientValid()) {
            //发送给服务器工作中状态
            ReportDeviceStatusManager.get().reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_STARTUP);
        }
    }

    /**
     * 汇报终端节目的状态
     */
    private void updateDevicePrmState() {
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                if (ConfigSettings.isClientValid()) {
                    String downloadAdpress = SharedUtil.newInstance()
                            .getString(SharedUtil.DWONLOADPLAN_ADPRESSDATAPACKAGE);
                    //首先读取节目文件目录里面所有的节目
                    try {
                        List<Program> localProgramList = Util.getLocalProgramList(false);//读取本地所有的节目
                        List<String> localSuccesList = new ArrayList<String>();
                        for (Program program : localProgramList) {
                            if (null != program) {
                                localSuccesList.add(program.getKey());
                            }
                        }
                        //此处转Json对象报错。修改如下
//						AdpressDataPackage adpressDataPackage = JSON.parseObject(downloadAdpress,
//								AdpressDataPackage.class);
                        AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(downloadAdpress);
                        sendReceipt(adpressDataPackage, localSuccesList, new ArrayList<String>());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void sendReceipt(AdpressDataPackage adpressDataPackage,
                             List<String> remoteProgramList, List<String> downloadFiledList) {
        CommandReceiptManager.responseDownloadState(adpressDataPackage,
                CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null,
                remoteProgramList, downloadFiledList);

    }

    @Override
    public void onSuccess(String result, String httpTag, String httpUrl) {
        Log.d(TAG, "NetwrokStateReceiver onSuccess result: " + result + " ,httpTag: " + httpTag + " ,httpUrl: " + httpUrl);
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String httpUrl) {
        Log.e(TAG, "NetwrokStateReceiver onFaile errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,httpUrl: " + httpUrl);
    }

}
