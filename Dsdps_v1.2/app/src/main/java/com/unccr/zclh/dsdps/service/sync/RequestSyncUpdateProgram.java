package com.unccr.zclh.dsdps.service.sync;

import android.content.Intent;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.FileUtile;
import com.unccr.zclh.dsdps.util.HttpUtil;
import com.unccr.zclh.dsdps.util.ListUtil;
import com.unccr.zclh.dsdps.util.ParsePrmUtil;
import com.unccr.zclh.dsdps.util.PrmFileHashUtil;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.StringUtil;
import com.unccr.zclh.dsdps.util.Util;

import org.apache.http.Header;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class RequestSyncUpdateProgram implements Runnable {

    private static final String TAG = "RequestSyncUpdate";
    private int programId;

    public RequestSyncUpdateProgram(int programId) {
        this.programId = programId;
    }

    @Override
    public void run() {
        requestSyncDevicePrm();
    }

    /**
     * 请求同步节目信息
     */
    private void requestSyncDevicePrm() {
        Log.d(TAG,"requestSyncDevicePrm programId: " + programId);
        if (programId != -1) {
            HttpUtil.getSyncJSON("/v1/message/synchronization/program/" + programId,
                    null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                            // 同步节目成功以后，判断本地节目是否一样
                            // 如果是一样的则不做任何处理，如果不一样则将不一样的节目进行更新
                            // 调用下载apk进行下载最新的素材
                            try {
                                isUpdatePrm(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {

                        }
                    });
        }

    }

    private boolean isUpdatePrm(JSONObject response) throws Exception {
        boolean isUpdate = false;
        if (StringUtil.isNullStr(response.toString())) {
            return isUpdate;
        }
        // 解析当前节目
        String result = response.getString("result");
        if (result.equals("success")) {
            String prmValue = response.toString();
            Log.d(TAG,"RequestSyncUpdateProgram isUpdatePrm prmValue: " + prmValue);
            if(prmValue == null){
                return isUpdate;
            }
            // 读取.temp节目，返回给服务器告诉他未下载成功
            List<Program> localUnDownloadProgramList = Util.getLocalProgramList(true);// 读取本地所有的未下载的节目
            if(ListUtil.isNotEmpty(localUnDownloadProgramList)){
                Log.d(TAG,"localUnDownloadProgramList is not null.");
                downloadTempPrm(localUnDownloadProgramList);// 下载素材
            }
            String prmJson = response.getString("data").replaceAll("\\\\","");
            Log.d(TAG, "isUpdatePrm prmJson: " + prmJson);
            Program program = JSON.parseObject(prmJson, Program.class);
            int type = program.getAs().get(0).getT();
            String key = program.getKey();
            if(type == 4){
                FileUtil.getInstance().renamePl(key);
                notifyUpdateProgram();
            }
            PrmFileHashUtil.get().init(program);//初始化保存素材hash

            List<Program> remoteProgramList = new ArrayList<Program>();
            remoteProgramList.add(program);

            File file = new File(FileUtile.getInstance().getTempFolder() + File.separator + SharedUtil.CACHE_LOCAL_PRM);
            String localPrm = FileUtile.readPrmToFile(file);
            Log.d(TAG,"RequestSyncUpdateProgram isUpdatePrm localPrm: " + localPrm);

            FileUtile.writePrmContent(file, prmValue);
            List<Program> localProgramList = Util.getLocalProgramList(false);//读取本地所有的节目
            addDownloadPrm(remoteProgramList, localProgramList);//添加下载节目
        }
        return isUpdate;
    }

    // 下载未下载完的节目
    private void downloadTempPrm(List<Program> localUnDownloadProgramList){
        ParsePrmUtil.savePrmAndDownloads(localUnDownloadProgramList);// 保存节目并且下载素材
    }

    private void addDownloadPrm(List<Program> remoteProgramList, List<Program> localProgramList) {
        //得到要下载的节目列表
        List<Program> downloadProgramList = ListUtil.getFilterProgramList(false, remoteProgramList,
                localProgramList, Program.class, "getKey", null, new Object[0]);
        if (ListUtil.isNotEmpty(downloadProgramList)) {
            //下载新节目
            ParsePrmUtil.savePrmAndDownloads(downloadProgramList);//保存节目并且下载素材
        }
    }

    /**
     * 通知更新节目
     */
    private void notifyUpdateProgram() {
            Log.d(TAG, "notifyUpdateProgram. cachePrmMap is not null.");
            Intent notifyIntent = new Intent(Constants.UI_NOTIFY_ACTION);
            DsdpsApp.getDsdpsApp().sendBroadcast(notifyIntent);
    }
}
