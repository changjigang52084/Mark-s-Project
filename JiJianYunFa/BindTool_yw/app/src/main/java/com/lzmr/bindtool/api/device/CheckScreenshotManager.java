package com.lzmr.bindtool.api.device;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.DeviceScreenShotViewDto;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.listener.CheckScreenshotListener;
import com.lzmr.bindtool.api.util.AuthorityCodeConstants;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.bean.ResponseContent;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.http.MyHttpClient;

import org.apache.http.Header;

/**
 * 项目名称：BindTool
 * 类描述：
 * 创建人：longyihuang
 * 创建时间：16/11/7 11:56
 * 邮箱：huanglongyi@17-tech.com
 */

public class CheckScreenshotManager extends BaseDeviceSettingManager {
    private CheckScreenshotListener mListener;
    public CheckScreenshotManager(String deviceId, CheckScreenshotListener listener) {
        super(deviceId, listener);
        this.mListener = listener;
    }

    @Override
    public void deviceControl() {
        checkScreenshot();
    }

    private void checkScreenshot(){
        String checkScreenshotUrl = HttpUtil.getCheckScreenshotServer(mDeviceId);
        if (StringUtils.isEmpty(checkScreenshotUrl)) {
            LogUtils.e(LogUtils.getStackTraceElement(), "Check screenshot url is null.");
            return;
        }

        MyHttpClient httpClient = new MyHttpClient();
//        if (!ConfigSettings.getBindMode()) {
            httpClient.getClient().addHeader("Authorization-Type", AuthorityCodeConstants.AUTHORITY_CODE_CHECK_SCREENSHOT);
//        }

        httpClient.get(checkScreenshotUrl,null,this);
    }

    @Override
    public void onRequestFailure(int statusCode, Header[] headers, Throwable throwable,
                                 String response, Object arg4) {
        String errorMsg = HttpUtil.getResponseErrorMessage(statusCode);
        mListener.onFailure(errorMsg);
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
                    DeviceScreenShotViewDto deviceScreenShotViewDto = JSON.parseObject(data.toString(),
                            DeviceScreenShotViewDto.class);
                    if (deviceScreenShotViewDto != null) {
                        mListener.onSuccess(BindToolApp.getApplication().getString(R.string.check_shots_success));
                        mListener.onCheckScreenshot(deviceScreenShotViewDto);
                    } else {
                        mListener.onFailure(BindToolApp.getApplication().getString(R.string.check_shots_fail));
                    }

                } catch (Exception e) {
                    LogUtils.e(LogUtils.getStackTraceElement(), e);
                }
            } else {
                mListener.onFailure(BindToolApp.getApplication().getString(R.string.check_shots_fail)
                        + ":" + responseContent.getMessage());
            }
        }
    }
}
