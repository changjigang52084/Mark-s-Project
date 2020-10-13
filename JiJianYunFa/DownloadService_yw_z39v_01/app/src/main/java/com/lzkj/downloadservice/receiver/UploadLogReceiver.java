package com.lzkj.downloadservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.lzkj.downloadservice.qiniu.impl.QiNiuUploadStateCallback;
import com.lzkj.downloadservice.qiniu.upload.UploadTask;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileConstant;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.FlowManager;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.ThreadPoolManager;
import com.lzkj.downloadservice.util.UploadStateReportTools;

import java.io.File;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2016年3月22日 下午3:02:49
 * @parameter 汇报日志
 */
public class UploadLogReceiver extends BroadcastReceiver {

    private static final LogUtils.LogTag TAG = LogUtils.getLogTag(UploadLogReceiver.class.getSimpleName(), true);

    public static final String ACTION = "com.lzkj.downloadservice.receiver.UPLOAD_LOG_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        LogUtils.d(TAG, "onReceive", "action: " + action);
        if (action.equals(ACTION)) {
            final String logFilePath = intent.getStringExtra(Constant.LOG_PATH);
            LogUtils.d(TAG, "onReceive", "logFilePath: " + logFilePath);
            if (!TextUtils.isEmpty(logFilePath)) {
                ThreadPoolManager.get().addRunnable(new Runnable() {
                    @Override
                    public void run() {
                        //主动上传错误日志到七牛
                        UploadTask uploadTask = new UploadTask(new QiNiuUploadStateCallback() {
                            @Override
                            public void onUploadUpdateProgreass(int progress, String uploadLocleFile) {

                            }
                            @Override
                            public void onUploadSuccess(String uploadLocleFile) {
                                LogUtils.d(TAG, "onUploadSuccess", "uploadLocleFile: " + uploadLocleFile);
                                File file = new File(logFilePath);
                                if (file.exists()) {
                                    file.delete();
                                }
                                if (uploadLocleFile.contains(FileUtil.DOWNLOAD_FLOW_INFO)) {
                                    FlowManager.getInstance().addDownloadFlow(0);
                                    FlowManager.getInstance().addUploadFlow(0);
                                }
                                UploadStateReportTools.sendUploadFileStateReceive(UploadTask.STATE_UPLOAD_SUCCESS, uploadLocleFile);
                            }
                            @Override
                            public void onUploadFail(String uploadLocleFile, String errMsg) {
                                LogUtils.d(TAG, "onUploadFail", "uploadLocleFile: " + uploadLocleFile + " ,errMsg: " + errMsg);
                                File file = new File(logFilePath);
                                if (file.exists()) {
                                    file.delete();
                                }
                                UploadStateReportTools.sendUploadFileStateReceive(UploadTask.STATE_UPLOAD_FAILED,
                                        uploadLocleFile);
                            }
                        });
                        uploadTask.executeUpload(logFilePath, FileConstant.BUCKET_TYPE_LOG);
                    }
                });
            }
        }
    }
}
