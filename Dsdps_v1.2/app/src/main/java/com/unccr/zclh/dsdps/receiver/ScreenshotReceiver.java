package com.unccr.zclh.dsdps.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.unccr.zclh.dsdps.download.interfaces.UploadStateCallback;
import com.unccr.zclh.dsdps.qiniu.upload.UploadProgressHandler;
import com.unccr.zclh.dsdps.qiniu.upload.UploadTask;
import com.unccr.zclh.dsdps.qiniu.upload.impl.FileConstant;
import com.unccr.zclh.dsdps.qiniu.upload.impl.QiNiuUploadStateCallback;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.ScreenShotUtil;

import java.io.File;
import java.util.List;

public class ScreenshotReceiver extends BroadcastReceiver implements UploadStateCallback, QiNiuUploadStateCallback {

    private static final String TAG = "ScreenshotReceiver";

    /**
     * 截图间隔时间
     */
    private static final String KEY_INTERVAL_TIME = "intervalTime";
    /**
     * 截图数
     */
    private static final String KEY_SHOT_NUMBER = "shotNumber";

    private static final String KEY_SCREENSHOT_STATE = "screenshot_state";
    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_PROGRAM_KEY = "programKey";

    private String mScreentshotFileName;

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(TAG, "onReceive executeShot action: " + action);
        if (null != intent) {
            if (action.equals("com.sunchip.aidlservice.action.SCREEN_SHOTS_ACTION")) {
                int intervalTime = intent.getIntExtra(KEY_INTERVAL_TIME, 0); // 0
                int shotNumber = intent.getIntExtra(KEY_SHOT_NUMBER, 1); // 1
                ScreenShotUtil.newInstance().excScreenShot(shotNumber, intervalTime);
            } else if (action.equals("com.sunchip.action.UPLOAD_SCREENTSHOT_ACTION")) {
                mScreentshotFileName = intent.getStringExtra(KEY_FILE_NAME);
                initUpload();
            }
        }
    }

    /**
     * 初始化上传
     */
    private void initUpload() {
        uploadScreenshot();
    }


    /**
     * 上传截图到七牛云存储
     */
    private void uploadScreenshot() {
        Log.d(TAG, "uploadScreenshot upload fileName: " + mScreentshotFileName);
        if (null != mScreentshotFileName) {
            UploadTask uploadTask = new UploadTask((QiNiuUploadStateCallback) this);
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
    public void onUploadUpdateProgreass(int progress, String uploadLocleFile) {
        Log.d(TAG, "qiniu onUploadUpdateProgreass uploadLocleFile: " + uploadLocleFile + " ,progress: " + progress);
    }

    @Override
    public void onUploadSuccess(String uploadLocleFile) {
        Log.d(TAG, "onUploadSuccess uploadLocleFile: " + uploadLocleFile);
        sendScreenShotStateAction(true,uploadLocleFile);
    }

    @Override
    public void onUploadFail(String uploadLocleFile, String errMsg) {
        Log.d(TAG, "onUploadFail uploadLocleFile: " + uploadLocleFile + " ,errMsg: " + errMsg);
        sendScreenShotStateAction(false,uploadLocleFile);
    }

    @Override
    public void onUploadUpdateProgreass(int progress, long totalSize, String uploadLocleFile) {

    }

    private void sendScreenShotStateAction(boolean state, String uploadFileName){
        File file = new File(FileUtil.getInstance().getShotFolderPath() + File.separator + uploadFileName);
        if(file.exists()){
            file.delete();
        }
    }
}
