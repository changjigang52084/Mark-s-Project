package com.unccr.zclh.dsdps.manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.download.DownloadManager;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileUtile;
import com.unccr.zclh.dsdps.util.ParsePrmUtil;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

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
        Log.d(TAG, "delete programs prmKeys: " + prmKeys.toString());
        notifyDeletePrm(prmKeys);
    }

    /**
     * 发送广播提示删除节目
     *s
     * @param prmKeys
     */
    private void notifyDeletePrm(List<String> prmKeys) {
        Intent intent = new Intent();
        intent.setAction(Constants.NOTFIY_DELETE_PRM_ACTION);
        Bundle bundle = new Bundle();
        bundle.putStringArrayList("deletePrmKey", (ArrayList<String>) prmKeys);
        intent.putExtras(bundle);
        DsdpsApp.getDsdpsApp().sendBroadcast(intent);
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
            Program program = ParsePrmUtil.getProgramToPath(FileUtile
                    .getInstance().getProgramePathToFileName(programName));
            if (null != program) {
                materialFileNames.addAll(ParsePrmUtil.getPrmMaterialNameList(program));
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
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
