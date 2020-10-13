package com.lzkj.aidlservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.aidlservice.manager.ServiceManager;
import com.lzkj.aidlservice.util.ConfigSettings;

/**
 * 项目名称：Communication_as
 * 类描述：接收获取devicekey的广播，并发送devicekey
 * 创建人：longyihuang
 * 创建时间：17/1/20 14:43
 * 邮箱：huanglongyi@17-tech.com
 */

public class GetDeviceKeyReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context arg0, Intent arg1) {
        ServiceManager.getInstance().sendUpdateDeviceKeyReceive(ConfigSettings.getDeviceKey());
    }
}
