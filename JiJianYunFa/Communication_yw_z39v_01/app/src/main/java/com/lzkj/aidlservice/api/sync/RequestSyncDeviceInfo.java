package com.lzkj.aidlservice.api.sync;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandSettingSynchronizeSetup;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Server;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Setting;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.WorkTimesInfo;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.api.interfaces.ISyncCallBack;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.bo.ResponseContent;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.util.Command;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.DeviceFlowUtle;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpConstants;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.VolumeUtil;
import com.lzkj.aidlservice.util.WorkTimeUtil;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年1月11日 上午10:58:59
 * @parameter 请求同步设备信息
 */
public class RequestSyncDeviceInfo implements Runnable, IRequestCallback {

    private static final String TAG = "RequestSyncDeviceInfo";

    /**
     * 同步epost
     **/
    public static final int TAG_EPOSTER_SYNC = 0;
    /**
     * 默认的同步
     **/
    public static final int DEFAULT_SYNC = 3;

    /**
     * 同步接口回调
     */
    private ISyncCallBack mISyncCallBack;

    /**
     * @param iSyncCallBack
     */
    public RequestSyncDeviceInfo(ISyncCallBack iSyncCallBack) {
        Log.d(TAG, "RequestSyncDeviceInfo isSyncCallBack: " + iSyncCallBack);
        mISyncCallBack = iSyncCallBack;
    }

    @Override
    public void run() {
        Log.d(TAG, "RequestSyncDeviceInfo run.");
        requestSyncDeviceInfo();
    }

    /**
     * 请求同步设备信息
     */
    private void requestSyncDeviceInfo() {
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        String requestUrl = getRequestUrl();
        Log.d(TAG, "RequestSyncDeviceInfo requestSyncDeviceInfo requestUrl: " + requestUrl);
        httpRequestBean.setRequestUrl(requestUrl);
        httpRequestBean.setRequestCallback(this);
        httpRequestBean.setRequestTag(RequestSyncDeviceInfo.class.getSimpleName());
        HttpUtil.newInstance().sendGetRequest(httpRequestBean);
    }

    private String getRequestUrl() {
        return HttpConfigSetting.getSyncDeviceUrl(ConfigSettings.getDid());
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        Log.d(TAG, "RequestSyncDeviceInfo onSuccess result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        if (null != mISyncCallBack) {
            mISyncCallBack.syncToTag(httpTag);
        }
        try {
            handlerResponseContent(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        Log.i(TAG, "RequestSyncDeviceInfo onFaile errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        if (null != mISyncCallBack) {
            mISyncCallBack.syncToTag(httpTag);
        }
    }

    /**
     * 处理响应的结果
     *
     * @param result 结果
     * @throws Exception
     */
    private void handlerResponseContent(String result) throws Exception {
        if (StringUtil.isNullStr(result)) {
            Log.d(TAG, "RequestSyncDeviceInfo handlerResponseContent result is null.");
            return;
        }
        ResponseContent responseContent = JSON.parseObject(result, ResponseContent.class);
        String packageContent = (String) responseContent.getData();
        Log.d(TAG, "RequestSyncDeviceInfo handlerResponseContent packageContent: " + packageContent);
        if (!StringUtil.isNullStr(packageContent)) {
            AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(packageContent);
            if (responseContent.isSuccess()) {
                String deviceInfo = adpressDataPackage.getData().toJson();
                Log.d(TAG, "RequestSyncDeviceInfo handlerResponseContent deviceInfo: " + deviceInfo);
                handlerDeviceInfo(deviceInfo, adpressDataPackage);
            } else {
                Log.d(TAG, "RequestSyncDeviceInfo handlerResponseContent onFailed Request sync device info failed.");
                //发送失败回执
                syncFailedReceipt(adpressDataPackage, responseContent.getMessage());
            }
        }
    }

    /**
     * 处理设备信息
     *
     * @param deviceInfo         设备信息
     * @param adpressDataPackage
     */
    private void handlerDeviceInfo(String deviceInfo, AdpressDataPackage adpressDataPackage) {
        if (!StringUtil.isNullStr(deviceInfo)) {
            CommandSettingSynchronizeSetup syncSetup = JSON.parseObject(deviceInfo, CommandSettingSynchronizeSetup.class);
            Setting setting = syncSetup.getSetting();
            Log.d(TAG, "RequestSyncDeviceInfo handlerDeviceInfo setting: " + setting);
            if (setting != null) {
                handlerSettingInfo(setting, adpressDataPackage);
            } else {
                syncFailedReceipt(adpressDataPackage, "setting is null");
            }
        }
    }

    /**
     * 处理Setting
     *
     * @param setting            包含设置信息的对象
     * @param adpressDataPackage
     */
    private void handlerSettingInfo(Setting setting, AdpressDataPackage adpressDataPackage) {
        String deviceSetting = JSON.toJSONString(setting);
        Log.d(TAG, "RequestSyncDeviceInfo handlerSettingInfo deviceSetting: " + deviceSetting);
        String cacheDeviceSetting = null;
        cacheDeviceSetting = SharedUtil.newInstance().getString(SharedUtil.CACHE_LOCAL_DEVICE_INFO);
        Log.d(TAG, "RequestSyncDeviceInfo handlerSettingInfo cacheDeviceSetting: " + cacheDeviceSetting);
        if (StringUtil.isNullStr(cacheDeviceSetting)) {
            updateDeviceInfo(adpressDataPackage, setting);
        } else {
            if (cacheDeviceSetting.equals(deviceSetting)) {
                syncSuccessReceipt(adpressDataPackage);
            } else {
                updateDeviceInfo(adpressDataPackage, setting);
            }
        }
    }

    /**
     * 更新设备设置信息
     *
     * @param adpressDataPackage
     * @param setting
     */
    private void updateDeviceInfo(AdpressDataPackage adpressDataPackage, Setting setting) {
        String deviceSetting = JSON.toJSONString(setting);
        Log.d(TAG, "RequestSyncDeviceInfo updateDeviceInfo deviceSetting: " + deviceSetting);
        SharedUtil.newInstance().setString(SharedUtil.CACHE_LOCAL_DEVICE_INFO, deviceSetting);
        if (null != setting.getSed()) {
            long heartTime = setting.getSed().intValue() * 1000;//第一次心跳的延迟值 单位秒
            /**
             * 心跳延时随机种子 （秒）
             * TODO: 2018/4/9 heartTime = 36000
             */
            Log.d(TAG, "RequestSyncDeviceInfo updateDeviceInfo heartTime: " + heartTime);
            ConfigSettings.setSycnHertTime(heartTime);
        }
        if (null != setting.getIp()) {
            int syncTime = setting.getIp().intValue();
            long syncPrmTime = ((syncTime < 20) ? 20 : syncTime) * HttpConstants.ONE_MINUTE_LONG;
            /**
             * 时间间隔-同步节目（分钟）
             * TODO: 2018/4/9 syncTime = 20
             * TODO: 2018/4/9 syncPrmTime = 1200000
             */
            Log.d(TAG, "RequestSyncDeviceInfo updateDeviceInfo syncTime: " + syncTime + " ,syncPrmTime: " + syncPrmTime);
            ConfigSettings.setSycnPrmTime(syncPrmTime);//设置 同步节目时间间隔
        }
        if (null != setting.getIs()) {
            int syncDevice = setting.getIs().intValue();
            long syncDeviceTime = ((syncDevice < 30) ? 30 : syncDevice) * HttpConstants.ONE_MINUTE_LONG;
            /**
             *  时间间隔-同步设置（分钟）
             *  TODO: 2018/4/9 syncDevice = 30
             *  TODO: 2018/4/9 syncDeviceTime = 1800000
             */
            ConfigSettings.setSycnDeviceTime(syncDeviceTime);//设置 同步设置时间间隔
        }
        if (null != setting.getVo()) {
            /**
             * 设置设备音量
             * TODO: 2018/4/9 setting.getVo().getVolume() = 10
             */
            Log.d(TAG, "RequestSyncDeviceInfo updateDeviceInfo volume: " + setting.getVo().getVolume());
            // 如果把这断代码放出来，会导致机器播放节目半小时后声音变为静音
            setDeviceVolume(setting.getVo().getVolume());
        }
        //设置工作时间（开关机）
        setWorkTime(setting.getWt());
        //设置各个服务器地址
        Server server = setting.getS();
        if (null != server) {
            saveServer(server);
        }
        //同步设备时间
        // setDeviceTime(setting.getT());
        syncSuccessReceipt(adpressDataPackage);
        if (null != setting.getTl()) {
            Integer integer = setting.getTl().getD();
            if (null == integer) {
                integer = -1;
            }
            updateFlowMaxValue(integer.longValue());
        }
    }

    /**
     * 更新最大流量阀值
     *
     * @param maxFlowValue 流量上限
     */
    private void updateFlowMaxValue(Long maxFlowValue) {
        if (null != maxFlowValue) {
            DeviceFlowUtle.getInstance().updateFolw(maxFlowValue);
        }
    }

    private void saveServer(Server server) {
        HttpConfigSetting.saveHeartServer(server.getH());//心跳服务器地址
        HttpConfigSetting.saveMessageServer(server.getM());//消息服务器地址
        HttpConfigSetting.saveRegisterServer(server.getR());//注册服务器地址
    }

    /**
     * 同步失败回执
     *
     * @param adpressDataPackage
     * @param errMsg             错误消息
     */
    private void syncFailedReceipt(AdpressDataPackage adpressDataPackage, String errMsg) {
        CommandReceiptManager.commandReceipt(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_ERROR, errMsg);
    }

    /**
     * 发送同步成功的回执
     *
     * @param adpressDataPackage
     */
    private void syncSuccessReceipt(AdpressDataPackage adpressDataPackage) {
        CommandReceiptManager.commandReceipt(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
    }

    /**
     * 设置设备时间
     *
     * @param time
     */
    private void setDeviceTime(long time) {
        Intent intent = new Intent();
        intent.putExtra("time", time);
        Command.sendCmdAction(Constant.SYCN_TIME, intent);
    }

    /**
     * 设置工作时间
     *
     * @param workTimesInfo
     */
    private void setWorkTime(WorkTimesInfo workTimesInfo) {
        if (null != workTimesInfo) {
            WorkTimeUtil.get().updateWorkTime(workTimesInfo);
        }
    }

    /**
     * @throws Exception
     * @Title: setDeviceVolume
     * @Description: TODO(设置设备音量)
     * @Param @param volumeData  音量数据
     */
    private void setDeviceVolume(String volumeData) {
        if (StringUtil.isNullStr(volumeData)) {
            Log.d(TAG, "RequestSyncDeviceInfo setDeviceVolume Volume data is null.");
            return;
        }
        VolumeUtil.setDeviceVolume(volumeData);
    }

}
