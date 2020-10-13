package com.lzkj.aidlservice.api.report;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.AdpressDeviceWorkingStateReportPackage;
import com.lzkj.aidlservice.api.impl.ReportDeviceWorkImpl;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzmr.bindtool.bean.CityBo;
import com.lzmr.bindtool.util.CityDatabaseHandler;




/**
 * 项目名称：Communication_as
 * 类描述：汇报城市定位
 * 创建人：longyihuang
 * 创建时间：16/11/2 11:01
 * 邮箱：huanglongyi@17-tech.com
 */

public class UploadLocationRunnable implements Runnable {

    private static final LogUtils.LogTag TAG_LOG = LogUtils.getLogTag(UploadLocationRunnable.class.getSimpleName(), true);

    @Override
    public void run() {
        uploadLocation();
    }

    /**
      * @Description 汇报定位
      */
    private void uploadLocation() {
        ReportDeviceWorkImpl reportDeviceWorkHandler = new ReportDeviceWorkImpl(getRequestParam());
        reportDeviceWorkHandler.reportDeviceWorkMsg();
    }

    /**
      * @Description 获取请求参数
      */
    private String getRequestParam() {
        AdpressDeviceWorkingStateReportPackage reportPackage = new AdpressDeviceWorkingStateReportPackage();
        String location = SharedUtil.newInstance().getString(SharedUtil.LOCATION_KEY);
        if (location != null) {
            CityDatabaseHandler handler = new CityDatabaseHandler(CommunicationApp.get().getApplicationContext());
            CityBo cityBo = handler.findCityByCityName(location);
            if (cityBo != null) {
                reportPackage.setLocation(cityBo.getCityId().longValue());
            }
        }
        LogUtils.e(TAG_LOG,"getRequestParam", JSON.toJSONString(reportPackage));
        return JSON.toJSONString(reportPackage);
    }
}

