package com.lzkj.aidlservice.push.control;

import android.content.Intent;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.AdpressProtocolPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandDeviceSocialUpdateSetup;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandProgramPublishCancelSetup;
import com.baize.adpress.core.protocol.bo.protocol.push.command.CommandProgramPublishSetup;
import com.baize.adpress.core.protocol.dto.AdpressDeviceSyncProgramParamPackage;
import com.lzkj.aidlservice.R;
import com.lzkj.aidlservice.api.sync.RequestSyncUpdateProgram;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.manager.DelProgramManager;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.LogUtils.LogTag;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;

import java.util.List;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年4月14日 下午7:39:01
 * @parameter 节目相关的控制命令
 */
public class ProgramCommanndControl {

    private static final LogTag TAG_LOG = LogUtils.getLogTag(ProgramCommanndControl.class.getSimpleName(), true);

    /**
     * 更新节目
     *
     * @param adpressDataPackage
     */
    void updateProgram(AdpressDataPackage adpressDataPackage) {
        Toast.makeText(CommunicationApp.get(), R.string.response_update_prm, Toast.LENGTH_LONG).show();
        try {
            SharedUtil.newInstance().setString(SharedUtil.DWONLOADPLAN_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
            String prmValue = adpressDataPackage.getData().toJson();
            LogUtils.d(TAG_LOG, "updateProgram", "prmValue: " + prmValue);

            CommandProgramPublishSetup prmPackage = JSON.parseObject(prmValue, CommandProgramPublishSetup.class);

            AdpressDeviceSyncProgramParamPackage deviceSyncProgramParamPackage = new AdpressDeviceSyncProgramParamPackage();
            deviceSyncProgramParamPackage.setProgramPrimaryKeyList(prmPackage.getProgramPrimaryKeyList());
            // 请求同步更新节目线程
            RequestSyncUpdateProgram updateProgramRunnable = new RequestSyncUpdateProgram(JSON.toJSONString(deviceSyncProgramParamPackage));
            // 把线程扔进线程池运行
            ThreadPoolManager.get().addRunnable(updateProgramRunnable);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除节目列表
     *
     * @param adpressDataPackage
     */
    void delProgramList(AdpressDataPackage adpressDataPackage) {
        try {
            SharedUtil.newInstance().setString(SharedUtil.DELETE_PRM_ADPRESSDATAPACKAGE, adpressDataPackage.toString());
            CommandProgramPublishCancelSetup programPublishCancelSetup = adpressDataPackage.getData();
            List<String> prmKeys = programPublishCancelSetup.getKeys();
            DelProgramManager.getInstance().delProgramByPrmId(prmKeys);
            Toast.makeText(CommunicationApp.get(), R.string.response_cancel_prm, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * @param @param adpressDataPackage    设定文件
     * @return void    返回类型
     * @Title: updateSocialData
     * @Description: TODO 更新社交媒体数据
     */
    void updateSocialData(AdpressDataPackage adpressDataPackage) {

        LogUtils.d(TAG_LOG, "updateSocialData", "updateSocialData");
        try {
            AdpressProtocolPackage data = adpressDataPackage.getData();
            if (null != data) {
                String json = data.toJson();
                if (!StringUtil.isNullStr(json)) {
                    CommandDeviceSocialUpdateSetup socialUpdate = JSON
                            .parseObject(json, CommandDeviceSocialUpdateSetup.class);
                    if (null != socialUpdate) {
                        Integer moduleType = socialUpdate.getModuleType();
                        Intent intent = new Intent(Constant.UPDATE_SOCIAL_DATA_ACTION);
                        intent.putExtra(Constant.SOCIAL_MODULE_KEY, moduleType);
                        CommunicationApp.get().sendBroadcast(intent);
                        LogUtils.d(TAG_LOG, "updateSocialData", "moduleType:" + moduleType);
                    } else {
                        LogUtils.e(TAG_LOG, "updateSocialData", "socialUpdate is null");
                    }
                } else {
                    LogUtils.e(TAG_LOG, "updateSocialData", "data.toJson() is null");
                }
            } else {
                LogUtils.e(TAG_LOG, "updateSocialData", "AdpressProtocolPackage is null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
