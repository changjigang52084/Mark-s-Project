package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.db.SQLiterManager;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpConstants;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.SharedUtil;

import org.json.JSONException;
import org.json.JSONObject;

import static android.R.attr.tag;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月9日 上午11:11:11
 * @parameter 截图成功或者失败调用的receive
 */
public class ScreenshotHandlerReceiver extends BroadcastReceiver implements IRequestCallback {

    private static final String TAG = "ScreenshotHandler";

    /**
     * 截图的状态
     **/
    private static final String KEY_SCREENSHOT_STATE = "screenshot_state";
    /**
     * 截图的名称
     **/
    private static final String KEY_FILE_NAME = "fileName";
    /**
     * 截图的节目名称
     **/
    private static final String KEY_PROGRAM_NAME = "programName";
    /**
     * 截图的节目key
     **/
    private static final String KEY_PROGRAM_KEY = "programKey";

    /**
     * 设备id
     **/
    private static final String KEY_DEVICE_ID = "deviceId";
    /**
     * 截图的url
     **/
    private static final String KEY_SCREENSHOT_URL = "screenShotUrl";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            boolean screentshotState = intent.getBooleanExtra(KEY_SCREENSHOT_STATE, false);
            if (screentshotState) {//判断截图是否成功
                screentshotReceipt();
                responseScreentshotInfo(intent);
            }
            Log.d(TAG, "ScreenshotHandlerReceiver onReceive Screentshot state is: " + screentshotState);
        }
    }

    /**
     * 截图信息汇报到服务器
     *
     * @param intent
     */
    private void responseScreentshotInfo(Intent intent) {
        String fileName = intent.getStringExtra(KEY_FILE_NAME);
        String programName = intent.getStringExtra(KEY_PROGRAM_NAME);
        String programKey = intent.getStringExtra(KEY_PROGRAM_KEY);

        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestParm(getRequestParm(fileName, programName));
        httpRequestBean.setRequestUrl(getRequestUrl());
        httpRequestBean.setRequestTag(ScreenshotHandlerReceiver.class.getSimpleName());
        HttpUtil.newInstance().postRequest(httpRequestBean);

        Log.d(TAG, "ScreenshotHandlerReceiver responseScreenshotInfo fileName: " + fileName + " ,programName: " + programName + " ,programKey: " + programKey);
    }

    /**
     * 截图回执
     */
    private void screentshotReceipt() {
        String adpresData = SharedUtil.newInstance().getString(SharedUtil.SCREENSHOT_ADPRESSDATAPACKAGE);
        AdpressDataPackage adpressDataPackage = null;
        try {
            adpressDataPackage = AdpressDataPackage.parser(adpresData);
            CommandReceiptManager
                    .commandReceipt(adpressDataPackage,
                            CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getRequestParm(String fileName, String programName) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put(KEY_DEVICE_ID, Long.parseLong(ConfigSettings.getDid()));
            jsonObject.put(KEY_SCREENSHOT_URL, fileName);
            if (null != programName) {
                jsonObject.put(KEY_PROGRAM_NAME, programName);
            }
            jsonObject.put("createTime", System.currentTimeMillis());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private String getRequestUrl() {
        String messageServer = HttpConfigSetting.getMessageServer();
        return String.format(HttpConstants.REPORT_DEVICE_SCREENSHOT_URL, messageServer, ConfigSettings.getDid());
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        Log.d(TAG, "ScreenshotHandlerReceiver onSuccess result: " + result + " ,httpTag: " + httpTag + " ,requestUrl: " + requestUrl);
        //SQLiterManager.getInstance().delHttpBo(requestUrl, httpTag);
    }

    @Override
    public void onFaile(String errMsg, String tag, String requestUrl) {
        Log.d(TAG, "ScreenshotHandlerReceiver onFaile errMsg: " + errMsg + " ,tag: " + tag + " ,requestUrl: " + requestUrl);
    }
}
