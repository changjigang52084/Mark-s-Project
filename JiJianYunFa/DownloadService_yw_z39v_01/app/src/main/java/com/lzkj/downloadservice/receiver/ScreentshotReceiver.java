package com.lzkj.downloadservice.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.lzkj.downloadservice.aliyun.upload.UploadThread;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.interfaces.UploadStateCallback;
import com.lzkj.downloadservice.qiniu.impl.QiNiuUploadStateCallback;
import com.lzkj.downloadservice.qiniu.upload.UploadProgressHandler;
import com.lzkj.downloadservice.qiniu.upload.UploadTask;
import com.lzkj.downloadservice.upload.UploadManager;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileConstant;
import com.lzkj.downloadservice.util.FileUtil;

import java.io.File;
import java.util.List;

/**
 * @author kchang changkai@lz-mr.com
 * @time:2015年7月7日 上午10:15:27
 * @Description:接收截图成工的命令，并且将截取的图片上传到云端
 */
public class ScreentshotReceiver extends BroadcastReceiver implements UploadStateCallback, QiNiuUploadStateCallback {

    private static final String TAG = "ScreentshotReceiver";

    private static final String KEY_SCREENSHOT_STATE = "screenshot_state";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_PROGRAM_NAME = "programName";
    private static final String KEY_PROGRAM_KEY = "programKey";

    private String mScreentshotFileName;
    private String mProgramName;
    private String mProgramKey;


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d(TAG, "action: " + intent.getAction());
        if (null != intent) {
            mScreentshotFileName = intent.getStringExtra(KEY_FILE_NAME);
            mProgramName = intent.getStringExtra(KEY_PROGRAM_NAME);
            mProgramKey = intent.getStringExtra(KEY_PROGRAM_KEY);
            initUpload();
        }
    }

    /**
     * 初始化上传
     */
    private void initUpload() {
        if (Constant.IS_QINIU) {
            uploadScreenshotToQiniu();
        } else {
            uploadScreenshot();
        }
    }

    /**
     * 上传截图
     */
    private void uploadScreenshot() {
        Log.d(TAG, "uploadScreenshot upload fileName: " + mScreentshotFileName);
        OSSBucket sampleBucket = new OSSBucket(Constant.OSS_BUCKET_NAME);
        sampleBucket.setBucketHostId(Constant.OSS_BUCKET_HOST_ID); // 可以在这里设置数据中心域名或者cname域名
        sampleBucket.setBucketACL(AccessControlList.PRIVATE);
        UploadThread uploadThread = null;
        if (null != mScreentshotFileName) {
            uploadThread = new UploadThread(FileUtil.getInstance().getShotFolderPath()
                    + File.separator + mScreentshotFileName, sampleBucket, this, Constant.SCREENSHOT_CLOUD_FOLDER);
        } else {
            uploadThread = new UploadThread(FileUtil.getInstance().getScreenshotFilePaths(),
                    sampleBucket, this, Constant.SCREENSHOT_CLOUD_FOLDER);
        }
        uploadThread.start();
        UploadManager.newInstance().addUploadThread(uploadThread);
    }

    /**
     * 上传截图到七牛云存储
     */
    private void uploadScreenshotToQiniu() {
        Log.d(TAG, "uploadScreenshotToQiniu upload fileName: " + mScreentshotFileName);
        if (null != mScreentshotFileName) {
            UploadTask uploadTask = new UploadTask(this);
            uploadTask.executeUpload(FileUtil.getInstance().getShotFolderPath() + File.separator + mScreentshotFileName, FileConstant.BUCKET_TYPE_SCREENSHOT);
            UploadProgressHandler.newInstance().addUploadTask(uploadTask);
        } else {
            List<String> screenshotList = FileUtil.getInstance().getScreenshotFilePaths();
            int size = screenshotList.size();
            for (int i = 0; i < size; i++) {
                Log.d(TAG, "uploadScreenshotToQiniu upload filename: " + screenshotList.get(i));
                UploadTask uploadTask = new UploadTask(this);
                uploadTask.executeUpload(screenshotList.get(i), FileConstant.BUCKET_TYPE_SCREENSHOT);
                UploadProgressHandler.newInstance().addUploadTask(uploadTask);
            }
        }
    }

    @Override
    public void onUploadSuccess(String uploadLocleFile) {
        Log.d(TAG, "onUploadSuccess uploadLocleFile: " + uploadLocleFile);
        sendScreentshotStateAction(true, uploadLocleFile);
    }

    @Override
    public void onUploadFail(String uploadLocleFile, String errMsg) {
        Log.d(TAG, "onUploadFail uploadLocleFile: " + uploadLocleFile + " ,errMsg: " + errMsg);
        sendScreentshotStateAction(false, uploadLocleFile);
    }

    @Override
    public void onUploadUpdateProgreass(int progress, long totalSize,
                                        String uploadLocleFile) {
        Log.d(TAG, "aliyun onUploadUpdateProgreass uploadLocleFile: " + uploadLocleFile + " ,progress: " + progress);
    }

    @Override
    public void onUploadUpdateProgreass(int progress, String uploadLocleFile) {
        Log.d(TAG, "qiniu onUploadUpdateProgreass uploadLocleFile: " + uploadLocleFile + " ,progress: " + progress);
    }

    /**
     * 发送截图上传成功或者失败的广播
     *
     * @param state
     */
    private void sendScreentshotStateAction(boolean state, String uploadFileName) {
        File file = new File(FileUtil.getInstance().getShotFolderPath() + File.separator + uploadFileName);
        if (file.exists()) {
            file.delete();
        }

        Intent intent = new Intent(Constant.SCREENSHOT_HANDLER_ACTION);
        intent.putExtra(KEY_FILE_NAME, uploadFileName);
        intent.putExtra(KEY_PROGRAM_NAME, mProgramName);
        intent.putExtra(KEY_PROGRAM_KEY, mProgramKey);
        intent.putExtra(KEY_SCREENSHOT_STATE, state);

        DownloadApp.getContext().sendBroadcast(intent);
    }
}
