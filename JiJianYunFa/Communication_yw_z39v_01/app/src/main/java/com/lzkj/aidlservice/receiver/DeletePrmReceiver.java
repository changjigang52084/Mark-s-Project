package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.lzkj.aidlservice.manager.DelProgramManager;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;

import java.util.ArrayList;

/**
 * 接收撤销节目的广播
 *
 * @author lyhuang
 * @date 2016-2-15 下午3:52:35
 */
public class DeletePrmReceiver extends BroadcastReceiver {
    private static final LogTag TAG = LogUtils.getLogTag(DeletePrmReceiver.class.getSimpleName(), true);

    /**
     * 删除节目的key
     */
    private static final String DELETE_PRM_KEY = "deletePrmKey";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                ArrayList<String> prmKeys = bundle.getStringArrayList(DELETE_PRM_KEY);
                if (prmKeys != null && !prmKeys.isEmpty()) {
                    DelProgramManager.getInstance().delPrmResourceByPrmId(prmKeys);
                    LogUtils.i(TAG, "onReceive", "DeletePrmReceiver delete program: " + prmKeys.toString());
                }
            }
        }
    }
}
