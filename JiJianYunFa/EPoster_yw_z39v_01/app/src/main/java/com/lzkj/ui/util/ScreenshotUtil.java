package com.lzkj.ui.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.view.View;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.ui.PlayActivity;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author kchang changkai@lz-mr.com
 * @Description:截图工具类
 * @time:2016年10月11日 下午7:31:09
 */
public class ScreenshotUtil {
    private static final LogTag TAG = LogUtils.getLogTag(ScreenshotUtil.class.getSimpleName(), true);

    private static final String SCREEN_SHOT_NAME = "screenshot";

    private static final String KEY_FILE_NAME = "fileName";
    private static final String KEY_PROGRAM_NAME = "programName";
    private static final String KEY_PROGRAM_KEY = "programKey";

    private long mDelaymillis = 5000;

    private int shot_count = 1;

    private String mSrennshotFileName = null;

    private volatile static ScreenshotUtil sScreenshotUtil = null;

    private ScreenshotUtil() {
    }

    public static ScreenshotUtil newInstance() {
        if (null == sScreenshotUtil) {
            synchronized (ScreenshotUtil.class) {
                if (null == sScreenshotUtil) {
                    sScreenshotUtil = new ScreenshotUtil();
                }
            }
        }
        return sScreenshotUtil;
    }

    /**
     * 你可以先调用setShotCount(int shotNumber)设置截图的次数,
     * 调用setShotTime(int shot_time)设置截图的间隔时间单位秒.如果不设置上面的参数则默认截图一次,间隔时间5秒钟
     * 执行截图命令
     */
    public void excSreenshot() {
        timerScreenshot();
    }

    /**
     * 执行截图命令,并且设置截图次数和截图间隔时间
     *
     * @param shotNumber 截图次数
     * @param shot_time  截图间隔时间
     */
    public void excSreenshot(int shotNumber, int shot_time) {
        setShotCount(shotNumber);
        setShotTime(shot_time);
        timerScreenshot();
    }

    /**
     * 定时截图
     */
    private void timerScreenshot() {
        LogUtils.d(TAG, "timerScreenshot", "timer screentshot 5s");
        ThreadPoolManager.get().addRunnable(screenshotRun);
    }

    /**
     * 截图的线程
     */
    private Runnable screenshotRun = new Runnable() {
        @Override
        public void run() {
            if (shellCaptureScreen()) {
                LogUtils.d(TAG, "screentshot", "captureScreen success");
                scaleBitmap();//对图片进行压缩 fileName
            } else {
                screentshot(PlayActivity.getActivity());
            }
            sendScreentshotAction();
        }
    };

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
            screentshotIntent.putExtra(KEY_PROGRAM_NAME, programParmBean.getN());//节目名称
        }
        EPosterApp.getApplication().sendBroadcast(screentshotIntent);
    }

    /**
     * 设置截图次数
     *
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
     *
     * @param shot_time 单位秒
     */
    public void setShotTime(int shot_time) {
        if (0 == shot_time) {
            return;
        }
        mDelaymillis = shot_time * 1000;
    }

    /**
     * 截图
     *
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
        LogUtils.d(TAG, "screentshot", "fileName: " + mSrennshotFileName);
        File shotFile = new File(shotFolder, mSrennshotFileName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(shotFile);
            bitmap.compress(CompressFormat.JPEG, 60, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
            view.destroyDrawingCache();
            scaleBitmap();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return;
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
     *
     * @return true表示执行成功
     */
    private boolean shellCaptureScreen() {
        File shotFolder = new File(FileStore.getInstance().getShotFolder());
        if (!shotFolder.exists()) {
            shotFolder.mkdir();
        }
        mSrennshotFileName = getFileName();
        LogUtils.d(TAG, "screentshot", "fileName:" + mSrennshotFileName);
        File shotFile = new File(shotFolder, mSrennshotFileName);
        String[] commands = new String[]{"/system/bin/screencap -p " + shotFile.getAbsolutePath()};
        return null == ShellUtil.execCommand(commands, true, true) ? false : true;
    }

    /**
     * @return 获取截图的名称
     */
    private String getFileName() {
        StringBuffer buffer = new StringBuffer(6);
        buffer.append(SCREEN_SHOT_NAME);
        buffer.append("_");
        buffer.append(ConfigSettings.getDid());
        buffer.append("_");
        buffer.append(System.currentTimeMillis());
        buffer.append(".jpeg");
        return buffer.toString();
    }
}
