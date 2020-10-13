package com.lzmr.bindtool.api.device;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.DeviceControlDto;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.api.util.AuthorityCodeConstants;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.bean.ResponseContent;
import com.lzmr.bindtool.http.HttpConstants;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;
import com.lzmr.bindtool.util.ConfigSettings;

import org.apache.http.Header;
import org.apache.http.HttpEntity;

import static com.lzmr.bindtool.R.string.reboot;

/**
 * 项目名称：BindTool
 * 类描述：终端设置重启管理器
 * 创建人：longyihuang
 * 创建时间：16/11/5 14:40
 * 邮箱：huanglongyi@17-tech.com
 */

public class DeviceRebootManager extends BaseDeviceSettingManager {

    public DeviceRebootManager(String deviceId, SessionRequestListener listener) {
        super(deviceId, listener);
    }

    @Override
    public void deviceControl() {
        reboot();
    }

    private void reboot(){
        String rebootUrl = HttpUtil.getRebootServer();
        if (StringUtils.isEmpty(rebootUrl)) {
            LogUtils.e(LogUtils.getStackTraceElement(), "Reboot url is null.");
            return;
        }

        MyHttpClient httpClient = new MyHttpClient();
//        if (!ConfigSettings.getBindMode()) {
            httpClient.getClient().addHeader("Authorization-Type", AuthorityCodeConstants.AUTHORITY_CODE_REBOOT);
//        }

        HttpEntity params = getRequestParams(mDeviceId);
        if (null == params) {
            LogUtils.e(LogUtils.getStackTraceElement(), "params is null.");
            return;
        }
        httpClient.post(rebootUrl, params, null, this);
    }

    @Override
    public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable,
                                 String response, Object arg4) {
        String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
        if(null != mListener){
            if(HttpConstants.STATUS_CODE_SESSION_INVALID == statusCode){
                mListener.onSessionInvalid();
                return;
            }
            mListener.onFailure(StringUtils.getString(BindToolApp.getApplication(), R.string.response_data_is_fail)+errorMsg);
        }
    }

    @Override
    public void onSuccess(int statusCode, Header[] headers, String response, Object arg3) {
        LogUtils.d(LogUtils.getStackTraceElement(), "statusCode:" + statusCode
                + ", response:" + response);
        ResponseContent responseContent = JSON.parseObject(response, ResponseContent.class);
        if (null != mListener) {
            if (responseContent.isSuccess()) {
                Object data = responseContent.getData();
                try {
                    DeviceControlDto deviceControlDto = JSON.parseObject(data.toString(),
                            DeviceControlDto.class);
                    if (deviceControlDto != null && deviceControlDto.getSuccess()) {
                        mListener.onSuccess(BindToolApp.getApplication().getString(R.string.reboot_success));
                    } else {
                        mListener.onFailure(BindToolApp.getApplication().getString(R.string.reboot_fail));
                    }

                } catch (Exception e) {
                    LogUtils.e(LogUtils.getStackTraceElement(), e);
                }
            } else {
                mListener.onFailure(BindToolApp.getApplication().getString(R.string.reboot_fail)
                        + ":" + responseContent.getMessage());
            }
        }
    }
}
