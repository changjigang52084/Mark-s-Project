package com.lzmr.bindtool.api.device;

import com.alibaba.fastjson.JSON;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.bean.DeviceSettingParams;
import com.lzmr.bindtool.http.handler.BaseHttpResponseHandler;

import org.apache.http.HttpEntity;
import org.apache.http.entity.StringEntity;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：BindTool
 * 类描述：
 * 创建人：longyihuang
 * 创建时间：16/11/5 12:02
 * 邮箱：huanglongyi@17-tech.com
 */

public abstract class BaseDeviceSettingManager extends BaseHttpResponseHandler {
    protected String mDeviceId;
    protected SessionRequestListener mListener;
    public BaseDeviceSettingManager(String deviceId, SessionRequestListener listener) {
        this.mDeviceId = deviceId;
        this.mListener = listener;
    }
    /**
      * @Description 终端设置
      */
    public abstract void deviceControl();

    /**
      * @Description 获取请求参数
      * @param deviceId 终端ID
      * @return HttpEntity 请求参数
      */
    protected HttpEntity getRequestParams(String deviceId) {
        List<String> deviceIds = new ArrayList<String>();
        deviceIds.add(deviceId);
        DeviceSettingParams params = new DeviceSettingParams();
        params.setDeviceIds(deviceIds);
        HttpEntity paramsEntity = null;
        try {
            paramsEntity = new StringEntity(JSON.toJSONString(params));
        } catch (UnsupportedEncodingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return paramsEntity;
    }



}
