package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.VendorWorkingStateReportPackage;
import com.lzkj.aidlservice.api.impl.ReportDeviceWorkImpl;
import com.lzkj.aidlservice.util.ConfigSettings;

/**
 * @author kchang Email:changkai@17-tech.com
 * @date   创建时间：2017/2/20 14:38
 * @version 1.0
 * @parameter  接收汇报售货机状态的广播
  */
public class ResponseVendStateReceiver extends BroadcastReceiver {
    /**售货机状态汇报action**/
    private static final String VEND_ACTION = "com.lzkj.aidlservice.RESPONES_VEND_STATE_ACTION";
    /**售货机门的状态key**/
    private static final String VEND_DOOR_STATE_KEY = "vendDoorStateKey";
    /**售货机货道状态key**/
    private static final String VEND_OUTLET_STATE_KEY = "vendOutletStateKey";

    private ReportDeviceWorkImpl mReportDeviceWorkImpl;

    public ResponseVendStateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            int doorState = intent.getIntExtra(VEND_DOOR_STATE_KEY, -1);
            int outletState = intent.getIntExtra(VEND_OUTLET_STATE_KEY, -1);
            VendorWorkingStateReportPackage vendorWorkingStateReportPackage = new VendorWorkingStateReportPackage();
            vendorWorkingStateReportPackage.setDeviceId(Long.parseLong(ConfigSettings.getDid()));
            vendorWorkingStateReportPackage.setDoorState(doorState);
            vendorWorkingStateReportPackage.setOutletState(outletState);
            String reportVendState = JSON.toJSONString(vendorWorkingStateReportPackage);
            if (mReportDeviceWorkImpl == null) {
                mReportDeviceWorkImpl = new ReportDeviceWorkImpl(reportVendState);
                mReportDeviceWorkImpl.setReportVendors(true);
            } else {
                mReportDeviceWorkImpl.setReportMsg(reportVendState);
            }
            mReportDeviceWorkImpl.reportDeviceWorkMsg();
        }
    }
}
