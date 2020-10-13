package com.lzkj.aidlservice.push.control;

import android.content.Intent;
import android.util.Log;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandDeviceNetworkSetup;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandDeviceScreenAudioSetup;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandDeviceWorkTimeSetup;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.NetworkTrafficLimit;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.WorkTimesInfo;
import com.lzkj.aidlservice.adapter.board.gw.GWPowerOnOffManager;
import com.lzkj.aidlservice.api.sync.RequestSyncProgramRunnable;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.log.LogBuilder;
import com.lzkj.aidlservice.log.LogBuilder.LogType;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.manager.ReportDeviceStatusManager;
import com.lzkj.aidlservice.manager.ScreentshotManager;
import com.lzkj.aidlservice.service.FeedDogService;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.DeviceFlowUtle;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.Util;
import com.lzkj.aidlservice.util.VolumeUtil;
import com.lzkj.aidlservice.util.WorkTimeUtil;

import java.io.File;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年4月14日 下午7:30:36
 * @parameter 广告机的控制命令类
 */
public class DeviceCommandControl {

    private static final String TAG = "DeviceCommandControl";

    DeviceCommandControl() {
    }

    /**
     * 关机
     *
     * @param adpressDataPackage
     */
    void shutdown(AdpressDataPackage adpressDataPackage) {
        Log.d(TAG, "shutdown device.");
        disableWatchDog();
        SharedUtil.newInstance().setString(SharedUtil.SHUTDOWN_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
        responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
        new GWPowerOnOffManager().powerOff();
    }

    /**
     * 设置终端流量阀值
     *
     * @param adpressDataPackage
     */
    void setFlowMax(AdpressDataPackage adpressDataPackage) {
        try {
            CommandDeviceNetworkSetup networkTrafficLimit = adpressDataPackage.getData();
            if (null != networkTrafficLimit) {
                NetworkTrafficLimit trafficLimit = networkTrafficLimit.getTl();
                if (null != trafficLimit) {
                    Integer maxFlow = trafficLimit.getD();
                    Log.d(TAG, "maxFlow: " + maxFlow);
                    if (null != maxFlow) {
                        DeviceFlowUtle.getInstance().updateFolw(maxFlow.longValue());
                        responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
                    }
                }
            }
        } catch (Exception e) {
            responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR, null);
            e.printStackTrace();
        }

    }


    /**
     * 重启机器
     *
     * @param adpressDataPackage
     */
    void reboot(AdpressDataPackage adpressDataPackage) {
        Log.d(TAG, "reboot device.");
        //发送给服务器开机状态
        ReportDeviceStatusManager.get().reportDeviceState(ReportDeviceStatusManager.DEVICE_STATE_STARTUP);
        SharedUtil.newInstance().setString(SharedUtil.REBOOT_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
        responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
        new GWPowerOnOffManager().reboot();
    }

    private void disableWatchDog() {
        Intent intent = new Intent(CommunicationApp.get().getApplicationContext(), FeedDogService.class);
        CommunicationApp.get().stopService(intent);
    }

    /**
     * 更新终端的声音大小
     *
     * @param adpressDataPackage
     */
    void updateDeivceVolume(AdpressDataPackage adpressDataPackage) {
        try {
            CommandDeviceScreenAudioSetup obj = adpressDataPackage.getData();
            String volumeData = obj.getSa().getVolume();
            if (StringUtil.isNullStr(volumeData)) {
                Log.e(TAG, "Volume data is null.");
                responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR, null);
                return;
            }

            Log.d(TAG, "volumeData: " + volumeData);
            VolumeUtil.setDeviceVolume(volumeData);

            /**
             *  修改者cjg
             *  修改日间 2018年12月3日
             */
            Intent updateVolumeIntent = new Intent();
            updateVolumeIntent.setAction(Constant.LAUNCHER_UPDATE_VOLUME_ACTION);// UI APK 更新天气的action
            updateVolumeIntent.putExtra(SharedUtil.CACHE_VOLUME, volumeData); // cache_weather
            CommunicationApp.get().sendBroadcast(updateVolumeIntent); //  发送广播

            responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
        } catch (Exception e) {
            responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR, null);
            e.printStackTrace();
        }
    }


    /**
     * 更新的终端工作时间
     *
     * @param adpressDataPackage
     */
    void updateWorkTiming(AdpressDataPackage adpressDataPackage) {
        try {
            SharedUtil.newInstance().setString(SharedUtil.WORKTIME_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
            CommandDeviceWorkTimeSetup deviceWorkTimerSetup = adpressDataPackage.getData();
            WorkTimesInfo workTimesInfo = deviceWorkTimerSetup.getWorktimes();
            WorkTimeUtil.get().updateWorkTime(workTimesInfo);
        } catch (Exception e) {
            responseReceiver(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR, null);
            e.printStackTrace();
        }
    }

    /**
     * 执行上传截图的命令
     *
     * @param adpressDataPackage
     */
    void uploadScreenshot(AdpressDataPackage adpressDataPackage) {
        SharedUtil.newInstance().setString(SharedUtil.SCREENSHOT_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
        Log.d(TAG, "device screentshot.");
        ScreentshotManager screentshotManager = new ScreentshotManager();
        screentshotManager.executShotCommand(0, 1);
    }

    /**
     * 上传日志到七牛云
     */
    void uploadLog() {
        Intent uploadLogIntent = new Intent(Constant.UPDATE_LOG_ACTION);
        uploadLogIntent.putExtra(Constant.LOG_PATH, getLogFileName());//上传最近一个小时的http运行日志
        CommunicationApp.get().sendBroadcast(uploadLogIntent);
    }

    /**
     * 获取日志文件名
     *
     * @return logFileName 日志文件名
     */
    private String getLogFileName() {
        String logFileName = FileUtile.getInstance().getLogFolder()
                + File.separator + LogBuilder.LOG_PREFIX + "_"
                + LogType.OPERATION_LOG + "_" + Util.getStringTimeToFormat("yyyy_MM_dd_HH")
                + "_ .log";
        return logFileName;
    }


    /**
     * @param adpressDataPackage
     * @Title: testDevicePush
     * @Description: TODO 测试终端推送，发送广播返回推送结果
     */
    void testDevicePush(AdpressDataPackage adpressDataPackage) {
        Intent intent = new Intent();
        intent.setAction(Constant.RECEIVER_PUSH_ACTION);
        CommunicationApp.get().sendBroadcast(intent);
    }

    /**
     * 同步终端节目
     */
    void syncProgram(){
        new RequestSyncProgramRunnable();
        Intent intent = new Intent();
        intent.setAction(Constant.SYNC_DEVICE_PROGRAM);
        CommunicationApp.get().sendBroadcast(intent);
    }


    /**
     * 命令接收成功返回给服务器
     *
     * @param adpressDataPackage AdpressDataPackage对象
     * @param responseCode       响应码
     * @param jsonData           说明内容
     */
    private void responseReceiver(AdpressDataPackage adpressDataPackage, int responseCode, String jsonData) {
        CommandReceiptManager.commandReceipt(adpressDataPackage, responseCode, jsonData);
    }
}
