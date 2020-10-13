package com.lzkj.aidlservice.manager;

import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.AdpressDeviceValidatePackage;
import com.baize.adpress.core.protocol.dto.AdpressDeviceValidateResultPackage;
import com.baize.adpress.core.protocol.dto.Server;
import com.baize.adpress.core.utils.NextationCoder;
import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.api.sync.RequestSyncWeather;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.bo.ResponseContent;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;

/**
 * 终端绑定验证
 *
 * @author lyhuang
 * @date 2016-2-18 下午4:28:47
 */
public class ValidationDeviceManager implements IRequestCallback {
    private static final LogTag TAG = LogUtils.getLogTag(ValidationDeviceManager.class.getSimpleName(), true);

    /**
     * 分割符
     */
    private static final String SPLIT = "#";

    /**
     * 单例的对象
     */
    private static volatile ValidationDeviceManager instance;

    /**
     * 请求的接口回调
     **/
    private IRequestCallback mRequestCallback;
    /**
     * 请求恢复密钥
     */
    private RecoverySecretKeyManager mSecretKeyManager;

    private ValidationDeviceManager() {
        mSecretKeyManager = new RecoverySecretKeyManager();
    }

    public static ValidationDeviceManager get() {
        if (null == instance) {
            synchronized (ValidationDeviceManager.class) {
                if (null == instance) {
                    instance = new ValidationDeviceManager();
                }
            }
        }
        return instance;
    }

    /**
     * 验证终端是否绑定
     *
     * @param
     * @return
     */
    public void validationDevice(String macAddress, IRequestCallback callback) {
        mRequestCallback = callback;
        //判断本地存不存在解通讯密文的key，不存在key的情况下，告知服务端，本地丢失解通讯密文的key。
        //服务端会判断该终端是否绑定，如果验证是已绑定的终端则会推送key。否则告知未绑定。
        handlerValidation(macAddress, callback);
    }

    private void handlerValidation(String macAddress, IRequestCallback callback) {
        if (null == ConfigSettings.getCommunicationKey()) {
            mSecretKeyManager.getSecretKey(macAddress, callback);
        } else {
            validationDevice(macAddress);
        }
    }


    public void validationDevice(String macAddress) {
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestUrl(HttpConfigSetting.getRecoveryRegisterUrl());
        httpRequestBean.setRequestRestry(10);
        httpRequestBean.setRequestCallback(this);
        httpRequestBean.setRequestParm(getValidationData(macAddress));
        httpRequestBean.setRequestTag(this.getClass().getSimpleName());
        HttpUtil.newInstance().postRequest(httpRequestBean);
    }

    /**
     * 获取要发送验证绑定的数据
     *
     * @param macAddress mac地址
     * @return
     */
    private String getValidationData(String macAddress) {
        AdpressDeviceValidatePackage deviceValidatePackage = new AdpressDeviceValidatePackage();
        deviceValidatePackage.setMac(macAddress);
        deviceValidatePackage.setJpid(ConfigSettings.getRegistrationId());
        deviceValidatePackage.setGpid(ConfigSettings.getClientId());
        return JSON.toJSONString(deviceValidatePackage);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        try {
            //在这里判断是否注册成功，成功了以后就打开推送
            //SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
            LogUtils.d(TAG, "onSuccess", "result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
            parseResponse(result, httpTag, requestUrl);
        } catch (Exception e) {
            if (null != mRequestCallback) {
                mRequestCallback.onFaile(StringUtil.getString(R.string.response_data_is_fail), "", requestUrl);
            }
            e.printStackTrace();
        }
    }

    /**
     * 解析验证终端授权的回复
     *
     * @param result 回复结果
     * @throws Exception
     */
    private void parseResponse(String result, String httpTag, String requestUrl) throws Exception {
        if (StringUtil.isNullStr(result)) {
            if (null != mRequestCallback) {
                mRequestCallback.onFaile(StringUtil.getString(R.string.response_data_is_fail), "", requestUrl);
            }
            return;
        }

        ResponseContent responseContent = JSON.parseObject(result, ResponseContent.class);
        if (responseContent.isSuccess()) {
            String data = responseContent.getData().toString();
            if (StringUtil.isNullStr(data)) {
                LogUtils.e(TAG, "parseResponse", "data is null");
                return;
            }

            LogUtils.d(TAG, "parseResponse", "decode: " + data + " ,httpTag: " + httpTag);
            //解密
            data = NextationCoder.decryptByPublicKey(data, getPublicKey());
            LogUtils.d(TAG, "parseResponse", "encode: " + data + " ,httpTag: " + httpTag);
            AdpressDeviceValidateResultPackage deviceValidateResultPackage = JSON.parseObject(data, AdpressDeviceValidateResultPackage.class);
            if (null != deviceValidateResultPackage) {
                String deviceKey = deviceValidateResultPackage.getDeviceKey();
                Long deviceId = deviceValidateResultPackage.getDeviceId();
                Server server = deviceValidateResultPackage.getServer();
                saveDeviceInfo(deviceKey, deviceId, server);
                if (null != mRequestCallback) {
                    mRequestCallback.onSuccess(StringUtil.getString(R.string.bind_success), httpTag, requestUrl);
                }
                requestSyncWeather();
            } else {
                if (null != mRequestCallback) {
                    mRequestCallback.onFaile("", httpTag, requestUrl);
                }
            }

        } else {
            String message = responseContent.getMessage();
//			if(!StringUtil.isNullStr(message) &&
//					message.contains(StringUtil.getString(R.string.device_not_auther))){
//			}
            UnbindDeviceManager.newInstance().unbindDevice();//设备没有授权，终端做解绑操作
            if (null != mRequestCallback) {
                mRequestCallback.onFaile(message, "", requestUrl);
            }
        }
    }

    /**
     * 绑定成功以后请求同步天气
     */
    private void requestSyncWeather() {
        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                new RequestSyncWeather().syncWeather();
            }
        });
    }

    private String getPublicKey() {
        return ConfigSettings.getCommunicationKey();
    }

    /**
     * 保存终端绑定信息
     */
    private void saveDeviceInfo(String deviceKey, Long deviceId, Server server) {
        if (null == deviceKey || null == deviceId || null == server) {
            LogUtils.e(TAG, "saveDeviceInfo", "Error : device bind info");
            return;
        }
        saveServerAndDeviceId(deviceKey, deviceId, server);
    }


    private void saveServerAndDeviceId(String deviceKey, Long deviceId, Server server) {
        LogUtils.d(TAG, "saveEposterDefaultServer","deviceKey: " + deviceKey + " ,deviceId: " + deviceId);
        //这里要获取2套不同的server
        sendUpdateDidReceive(String.valueOf(deviceId), JSON.toJSONString(server));
        sendUpdateDeviceKeyReceive(deviceKey);
        ConfigSettings.saveDeviceId(String.valueOf(deviceId));
        ConfigSettings.saveDeviceKey(deviceKey);
        HttpConfigSetting.saveHeartServer(server.getH());
        HttpConfigSetting.saveMessageServer(server.getM());
        HttpConfigSetting.saveRegisterServer(server.getR());
    }


    /**
     * 发送更新did的广播
     */
    private void sendUpdateDidReceive(String did, String server) {
        Intent intent = new Intent(Constant.UPDATE_DID_ACTION);
        intent.putExtra(Constant.DEVICEID_KEY, did);
        CommunicationApp.get().sendBroadcast(intent);
    }

    private void sendUpdateDeviceKeyReceive(String deviceKey) {
        Intent intent = new Intent(Constant.UPDATE_DEVICE_KEY_ACTION);
        intent.putExtra(Constant.DEVICE_KEY_KEY, deviceKey);
        CommunicationApp.get().sendBroadcast(intent);
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        LogUtils.d(TAG, "onFaile", "errMsg: " + errMsg + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        if (mRequestCallback != null) {
            mRequestCallback.onFaile(errMsg, httpTag, requestUrl);
        }
    }
}
