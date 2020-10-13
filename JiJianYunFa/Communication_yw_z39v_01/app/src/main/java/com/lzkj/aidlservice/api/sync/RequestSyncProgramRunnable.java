package com.lzkj.aidlservice.api.sync;

import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.baize.adpress.core.protocol.dto.AdpressDeviceSyncProgramParamPackage;
import com.lzkj.aidlservice.api.interfaces.IRequestCallback;
import com.lzkj.aidlservice.api.interfaces.ISyncCallBack;
import com.lzkj.aidlservice.app.CommunicationApp;
import com.lzkj.aidlservice.bo.HttpRequestBean;
import com.lzkj.aidlservice.bo.ResponseContentArray;
import com.lzkj.aidlservice.manager.CommandReceiptManager;
import com.lzkj.aidlservice.util.AppUtil;
import com.lzkj.aidlservice.util.ConfigSettings;
import com.lzkj.aidlservice.util.FileUtile;
import com.lzkj.aidlservice.util.HttpConfigSetting;
import com.lzkj.aidlservice.util.HttpUtil;
import com.lzkj.aidlservice.util.ListUitl;
import com.lzkj.aidlservice.util.LogUtils;
import com.lzkj.aidlservice.util.StringUtil;
import com.lzkj.aidlservice.util.ThreadPoolManager;
import com.lzkj.aidlservice.util.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/11/3.
 * @Parameter 请求同步节目的线程
 */

public class RequestSyncProgramRunnable implements IRequestCallback, Runnable {
    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(RequestSyncProgramRunnable.class.getSimpleName(), true);

    private static final String TAGS = "RequestSyncProgram";
    private ISyncCallBack mISyncCallBack;

    public RequestSyncProgramRunnable(){

    }

    public RequestSyncProgramRunnable(ISyncCallBack iSyncCallBack) {
        mISyncCallBack = iSyncCallBack;
    }

    @Override
    public void run() {
        requestSyncDevicePrm();
    }

    /**
     * 请求同步节目信息
     */
    private void requestSyncDevicePrm() {
        String requestUrl = HttpConfigSetting.getSyncDevicePrmUrl(ConfigSettings.getDid());
        Log.d(TAGS,"RequestSyncProgramRunnable requestSyncDevicePrm requestUrl = " + requestUrl);
        HttpRequestBean httpRequestBean = new HttpRequestBean();
        httpRequestBean.setRequestUrl(requestUrl);
        httpRequestBean.setRequestCallback(this);
        httpRequestBean.setRequestTag(RequestSyncUpdateProgram.class.getSimpleName());
        HttpUtil.newInstance().sendGetRequest(httpRequestBean);
    }

    @Override
    public void onSuccess(String result, String httpTag, String requestUrl) {
        if (null != mISyncCallBack) {
            mISyncCallBack.syncToTag(httpTag);
        }
        LogUtils.d(TAG, "onSuccess", "result : " + result);
        //解析当前节目
        try {
            ResponseContentArray responseContent = JSON.parseObject(result, ResponseContentArray.class);
            LogUtils.d(TAG, "onSuccess", "adptressData : " + responseContent.getData().toString() );
            if (null == responseContent.getData() || "[]".equals(responseContent.getData().toString())) {
                delLocalAllPrm();
                LogUtils.d(TAG, "onSuccess", "adptressData is null" );
                return;
            }

            if (responseContent.isSuccess()) {
                List<String> prmKeyList = responseContent.getData();
                if (ListUitl.isNotEmpty(prmKeyList)) {
                    handlerPrmKeys(prmKeyList);
                    sendReceipt(null, prmKeyList, new ArrayList<String>());
                }
                LogUtils.d(TAG, "onSuccess", "prmKeys : " + prmKeyList.size());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendReceipt(AdpressDataPackage adpressDataPackage,
                             List<String> remoteProgramList, List<String> downloadFiledList) {
        List<Program> localProgramList  = null;//读取本地所有的节目
        try {
            localProgramList = Util.getLocalProgramList(false);
            List<String> localProgramKeyList = Util.getPrmKey(localProgramList);

            CommandReceiptManager.responseDownloadState(adpressDataPackage,
                    CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null,
                    localProgramKeyList, downloadFiledList);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handlerPrmKeys(List<String> prmKeys) throws IOException {
        if (null != prmKeys) {
            List<String> localProgramKeyList = getLocalAllPrmKeyList();
            if (ListUitl.isEmpty(localProgramKeyList)) {//本地没有任何节目
                updatePrmList(prmKeys);
                LogUtils.d(TAG, "handlerPrmKeys", "localProgramKeyList is null");
                return;
            }
            List<String> remoteNewPrmList = new ArrayList<>(prmKeys);//远程最新节目key列表
            //远程的移除掉本地的剩下的就是要更新的节目keys
            remoteNewPrmList.removeAll(localProgramKeyList);
            updatePrmList(remoteNewPrmList);

            List<String> remotePrmList = new ArrayList<>(prmKeys);//远程最新节目key列表
            localProgramKeyList.removeAll(remotePrmList);//移除掉本地的所有节目key,剩下的就是要删除

            if (delProgameKeys(localProgramKeyList)) {//删除节目以后提示更新节目列表
                CommunicationApp.get().mAppHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        AppUtil.notifyProgramPlayList();
                    }
                }, 2000);
            }
        }
    }

    private List<String> getLocalAllPrmKeyList() throws IOException {
        List<Program> localUnDownloadProgramList = Util.getLocalProgramList(true);//读取本地所有的未下载的节目
        List<Program> localProgramList  = Util.getLocalProgramList(false);//读取本地所有的节目

        List<String> localProgramKeyList = new ArrayList<String>(); //本地所有的节目key
        localProgramKeyList.addAll(Util.getPrmKey(localUnDownloadProgramList));
        localProgramKeyList.addAll(Util.getPrmKey(localProgramList));
        return localProgramKeyList;
    }

    private void delLocalAllPrm() throws IOException {
        List<String> localProgramKeyList = getLocalAllPrmKeyList();
        if (delProgameKeys(localProgramKeyList)) {//删除节目以后提示更新节目列表
            CommunicationApp.get().mAppHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    AppUtil.notifyProgramPlayList();
                }
            }, 2000);
        }
    }

    /**
     * 同步新节目文件
     * @param remoteNewPrmList
     */
    private void updatePrmList(List<String> remoteNewPrmList) {
        if (ListUitl.isNotEmpty(remoteNewPrmList)) {
            AdpressDeviceSyncProgramParamPackage prmPackage
                    = new AdpressDeviceSyncProgramParamPackage();
            prmPackage.setProgramPrimaryKeyList(remoteNewPrmList);

            RequestSyncUpdateProgram updateProgramRunnable
                    = new RequestSyncUpdateProgram(JSON.toJSONString(prmPackage));
            ThreadPoolManager.get().addRunnable(updateProgramRunnable);
        }  else {
            Log.d("updatePrmList", "remoteNewPrmList is null...");
        }
    }

    /**
     * 删除掉本地的无效节目
     * @param delPrmKeyList
     * @return
     */
    private boolean delProgameKeys(List<String> delPrmKeyList) {
        if (ListUitl.isNotEmpty(delPrmKeyList)) {
            for (String delProgramKey : delPrmKeyList) {
                LogUtils.d(TAG,"delProgameKeys","delProgramKey: " + delProgramKey);
                if (null != delProgramKey && !delProgramKey.equals("video") && !delProgramKey.equals("picture")) {
                    FileUtile.getInstance().delProgramToPrmId(delProgramKey);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public void onFaile(String errMsg, String httpTag, String requestUrl) {
        if (null != mISyncCallBack) {
            mISyncCallBack.syncToTag(httpTag);
        }
        isUnBind();
    }

    /**
     * 终端是否已解绑解绑的话删除所有的节目
     * @return boolean true表示已解绑
     */
    private boolean isUnBind() {
        if (StringUtil.isNullStr(ConfigSettings.getDid())) {
            FileUtile.delete(FileUtile.getInstance().getLayoutFolderPath());
            FileUtile.delete(FileUtile.getInstance().getPicFolder());
            FileUtile.delete(FileUtile.getInstance().getVideoFolder());
            FileUtile.delete(FileUtile.getInstance().getLogFolder());
            FileUtile.delete(FileUtile.getInstance().getScaledImageFolderPath());
            LogUtils.d(TAG, "isDelAllPrm", "isDelAllPrm");
            return true;
        }
        return false;
    }

}
