package com.unccr.zclh.dsdps.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;

import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.play.ProgramPlayManager;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @Description:截图工具类
 */
public class ScreenShotUtil {

    private static final String TAG = "ScreenShotUtil";

    private static final String SCREEN_SHOT_NAME = "screenshot";

    private static final String KEY_FILE_NAME    = "fileName";
    private static final String KEY_PROGRAM_KEY  = "programKey";

    private int shot_count = 1;

    private long mDelaymillis = 5000;

    private String mSrennshotFileName = null;

    private volatile static ScreenShotUtil sScreenshotUtil = null;

    private ScreenShotUtil() {}

    public static ScreenShotUtil newInstance() {
        if (null == sScreenshotUtil) {
            synchronized (ScreenShotUtil.class) {
                if (null == sScreenshotUtil) {
                    sScreenshotUtil = new ScreenShotUtil();
                }
            }
        }
        return sScreenshotUtil;
    }

    /**
     * 执行截图命令,并且设置截图次数和截图间隔时间
     * @param shotNumber 截图次数
     * @param shot_time 截图间隔时间
     */
    public void excScreenShot(int shotNumber,int shot_time) {
        setShotCount(shotNumber);
        setShotTime(shot_time);
        timerScreenshot();
    }

    /**
     * 定时截图
     */
    private void timerScreenshot() {
        Log.d(TAG, "timerScreenshot timer screentshot 5s");
        ThreadPoolManager.get().addRunnable(screenshotRun);
    }

    /**
     * 截图的线程
     */
    private Runnable screenshotRun = new Runnable() {
        @Override
        public void run() {
            if (shellCaptureScreen()) {
                Log.d(TAG, "screenshotRun captureScreen success");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                scaleBitmap();//对图片进行压缩 fileName
            } else {
                screentshot(PlayActivity.getActivity());
            }
            sendScreentshotAction();
        }
    };

    /**
     * 设置截图次数
     * @param shotNumber 截图数
     */
    public void setShotCount(int shotNumber) {
        if (0 == shotNumber) {
            return;
        }
        shot_count = shotNumber;
    }

    /**
     * 设置截图时间
     * @param shot_time 单位秒
     */
    public void setShotTime(int shot_time) {
        if (0 == shot_time) {
            return;
        }
        mDelaymillis = shot_time * 1000;
    }

    /**
     * 压缩图片
     */
    private void scaleBitmap() {
        File shotFolder = new File(FileStore.getInstance().getShotFolder(), mSrennshotFileName);
        Bitmap bitmap = BitmapUtil.getBitmapWithScale(shotFolder.getAbsolutePath(), 700, 700, false);
        if (shotFolder.exists()) {
            shotFolder.delete();
        }
        BitmapUtil.saveBitmapToSdCard(bitmap, shotFolder.getAbsolutePath());
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 使用shell截图
     * @return true表示执行成功
     */
    private boolean shellCaptureScreen() {
        File shotFolder = new File(FileStore.getInstance().getShotFolder());
        if (!shotFolder.exists()) {
            shotFolder.mkdir();
        }
        mSrennshotFileName = getFileName();
        Log.d(TAG, "shellCaptureScreen mSrennshotFileName: " + mSrennshotFileName);
        File shotFile = new File(shotFolder, mSrennshotFileName);
        Log.d(TAG,"absolutePath: " + shotFile.getAbsolutePath());
//        Intent cmdIntent = new Intent("com.zclhsd.setting.syscmd");
//        cmdIntent.putExtra("cmd","screencap");
//        cmdIntent.putExtra("fullscreen",true);
//        cmdIntent.putExtra("scale",1.0f);
//        cmdIntent.putExtra("filepath",shotFile.getAbsolutePath());
////        cmdIntent.putExtra("filepath","sdcard/screenshot_1.jpeg");
//        DsdpsApp.getDsdpsApp().sendBroadcast(cmdIntent);

        String[] commands = new String[]{"/system/bin/screencap -p " + shotFile.getAbsolutePath()};

        return null == ShellUtil.execCommand(commands, true, true) ? false : true;
//        return true;
    }

    /**
     * @return 获取截图的名称
     */
    private String getFileName() {
        StringBuffer buffer = new StringBuffer(6);
        buffer.append(SCREEN_SHOT_NAME);
        buffer.append("_");
        buffer.append(ConfigSettings.getProgramId());
        buffer.append("_");
        buffer.append(System.currentTimeMillis());
        buffer.append(".jpeg");
        return buffer.toString();
    }

    /**
     * 截图
     * @param activity
     */
    private void screentshot(Activity activity) {
        //View是你需要截图的View
        View view = activity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        File shotFolder = new File(FileStore.getInstance().getShotFolder());
        if (!shotFolder.exists()) {
            shotFolder.mkdir();
        }
        mSrennshotFileName = getFileName();
        Log.d(TAG, "screentshot fileName: " + mSrennshotFileName);
        File shotFile = new File(shotFolder, mSrennshotFileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(shotFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 60, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            view.destroyDrawingCache();
            scaleBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ;
    }

    /**
     * 发送截图上传的action
     */
    private void sendScreentshotAction() {
        Intent screentshotIntent = new Intent(Constants.SCREENTSHOT_UPLOAD_ACTION);
        if (null != mSrennshotFileName) {
            screentshotIntent.putExtra(KEY_FILE_NAME, mSrennshotFileName);
        }
        Program programParmBean = ProgramPlayManager.getInstance().getCurrentProgram();
        if (null != programParmBean) {
            screentshotIntent.putExtra(KEY_PROGRAM_KEY, programParmBean.getKey());//当前播放节目的id
        }
        DsdpsApp.getDsdpsApp().sendBroadcast(screentshotIntent);
    }
}
