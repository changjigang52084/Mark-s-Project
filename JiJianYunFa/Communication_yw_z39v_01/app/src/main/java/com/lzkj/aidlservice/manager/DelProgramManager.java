package com.lzkj.aidlservice.manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.util.Constant;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.ParsePrmUitl;
import com.lzkj.aidlservice.util.SharedUtil;
import com.lzkj.aidlservice.util.StringUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年7月13日 下午4:01:31
 * @parameter 删除本地的节目列表工具类
 */
public class DelProgramManager {

    private static final String TAG = "DelProgramManager";

    private static volatile DelProgramManager delProgramManager;

    private DelProgramManager() {
    }

    public static DelProgramManager getInstance() {
        if (null == delProgramManager) {
            synchronized (DelProgramManager.class) {
                if (null == delProgramManager) {
                    delProgramManager = new DelProgramManager();
                }
            }
        }
        return delProgramManager;
    }

    /**
     * 根据program id来删除本地节目列表
     *
     * @param prmKeys 删除节目keys
     */
    public void delProgramByPrmId(List<String> prmKeys) {
        if (null == prmKeys || prmKeys.size() == 0) {
            Log.e(TAG, "prmKeys is null.");
            return;
        }
        //根据节目列表的id来删除对应的节目或者是正在下载的素材
        Log.d(TAG, "Delete programs prmKeys: " + prmKeys.toString());
        //通知Eposter从播放节目列表中移除相关节目
        notifyDeletePrm(prmKeys);
    }

    /**
     * 发生广播提示删除节目
     *
     * @param prmKeys
     */
    private void notifyDeletePrm(List<String> prmKeys) {
        Intent intent = new Intent();
        intent.setAction(Constant.NOTFIY_DELETE_PRM_ACTION);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("deletePrmKey", (ArrayList<String>) prmKeys);
        intent.putExtras(bundle);
        CommunicationApp.get().sendBroadcast(intent);
        cancelDownloadList(prmKeys);
    }

    /**
     * 取消下载素材列表
     *
     * @param prmKeys 节目名称列表
     */
    private void cancelDownloadList(List<String> prmKeys) {
        ArrayList<String> materialFileNames = new ArrayList<String>();
        for (String programName : prmKeys) {
            Program program = ParsePrmUitl.getProgramToPath(FileUtile
                    .getInstance().getProgramePathToFileName(programName));
            if (null != program) {
                materialFileNames.addAll(ParsePrmUitl.getPrmMaterialNameList(program));
            }
        }
        DownloadManager.newInstance().cancelDownloadList(materialFileNames);
    }

    /**
     * 根据节目单id删除节目
     *
     * @param prmKeys 节目id列表
     */
    public void delPrmResourceByPrmId(List<String> prmKeys) {
        List<String> delSuccessList = new ArrayList<String>();
        List<String> delFiledList = new ArrayList<String>();
        for (String prmKey : prmKeys) {
            if (FileUtile.getInstance().delProgramToPrmId(prmKey)) {
                delSuccessList.add(prmKey);
            } else {
                delFiledList.add(prmKey);
            }
        }
        try {
            String adpressData = SharedUtil.newInstance().getString(SharedUtil.DELETE_PRM_ADPRESSDATAPACKAGE);
            Log.d(TAG, "adpressData: " + adpressData);
            if (StringUtil.isNullStr(adpressData)) {
                return;
            }
            SharedUtil.newInstance().removeKey(SharedUtil.DELETE_PRM_ADPRESSDATAPACKAGE);
            AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(adpressData);
            //发送回执
            CommandReceiptManager.commandReceiptCancelPrm(adpressDataPackage, CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, delSuccessList, delFiledList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
