package com.xunixianshi.vrshow.util;

import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.baidu.autoupdatesdk.AppUpdateInfo;
import com.baidu.autoupdatesdk.AppUpdateInfoForInstall;
import com.baidu.autoupdatesdk.BDAutoUpdateSDK;
import com.baidu.autoupdatesdk.CPCheckUpdateCallback;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.SDCardUtil;
import com.hch.viewlib.util.MLog;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author coolszy
 * @date 2012-4-26
 * @blog http://blog.92coding.com
 */

public class UpdateManager {

    private Context mContext;
    private Notification notify;
    private NotificationManager manager;
    private String apkUrl = "";
    private static final String sdPath = Environment.getExternalStorageDirectory() + "/download/";
    private static final String saveFileName = sdPath;
    private LoadingAnimationDialog mProgressDialog;

    public UpdateManager(Context context) {
        this.mContext = context;
        mProgressDialog = new LoadingAnimationDialog(context);
    }

    /**
     * 获取App服务端版本号和url地址
     */
    public void getServerVersionCode(final boolean isShowDesc) {
        if (OkhttpConstant.isDownloading) {
            showToastMsg("当前更新版本正在下载中...");
        } else {
            mProgressDialog.show();
            BDAutoUpdateSDK.cpUpdateCheck(mContext, new CPCheckUpdateCallback() {
                @Override
                public void onCheckUpdateCallback(AppUpdateInfo appUpdateInfo, AppUpdateInfoForInstall appUpdateInfoForInstall) {
                    MLog.d("appUpdateInfo: " + appUpdateInfo);
                    if (appUpdateInfo != null) {
                        OkhttpConstant.VERSION_CODE_SERVER = appUpdateInfo.getAppVersionCode();
                        apkUrl = appUpdateInfo.getAppUrl();
                        if (appUpdateInfoForInstall == null && OkhttpConstant.VERSION_CODE_SERVER > OkhttpConstant.VERSION_CODE) {
                            // 显示提示对话框
                            showNoticeDialog();
                        } else if (isShowDesc) {
                            Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_SHORT).show();
                            mProgressDialog.dismiss();
                        }
                    } else if (appUpdateInfoForInstall == null && isShowDesc) {
                        Toast.makeText(mContext, R.string.soft_update_no, Toast.LENGTH_SHORT).show();
                        mProgressDialog.dismiss();
                    } else {
                        mProgressDialog.dismiss();
                    }
                }
            });
        }
    }

    /**
     * 显示软件更新对话框
     */
    private void showNoticeDialog() {
        // 构造对话框
        Builder builder = new Builder(mContext);
        builder.setTitle(R.string.soft_update_title);
        builder.setMessage(R.string.soft_update_info);
        // 更新
        builder.setPositiveButton(R.string.soft_update_updatebtn, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                downLoadNewApk();
            }
        });
        // 稍后更新
        builder.setNegativeButton(R.string.soft_update_later, new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        Dialog noticeDialog = builder.create();
        noticeDialog.show();
        mProgressDialog.dismiss();
    }

    private void downLoadNewApk() {
        manager = (NotificationManager) mContext.getSystemService(mContext.NOTIFICATION_SERVICE);
        notify = new Notification();
        notify.icon = R.drawable.ic_launcher;
        notify.contentView = new RemoteViews(mContext.getPackageName(),
                R.layout.view_notify_item);
        manager.notify(100, notify);
        if (SDCardUtil.externalMemoryAvailable()) {
            File destDir = new File(sdPath);
            File ApkFile = new File(sdPath + "VRSHOW.apk");
            if (!destDir.exists()) {
                destDir.mkdirs();
            }
            if (!ApkFile.exists()) {
                try {
                    // 在指定的文件夹中创建文件
                    ApkFile.createNewFile();
                    downLoadSchedule(apkUrl, completeHandler, ApkFile);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                downLoadSchedule(apkUrl, completeHandler, ApkFile);
            }
        }
    }

    public void downLoadSchedule(final String uri, final Handler handler, final File file) {
        if (!file.exists()) {
            handler.sendEmptyMessage(-1);
            return;
        }
        OkhttpConstant.isDownloading = true;
        // 每次读取文件的长度
        final int perLength = 4096;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    URL url = new URL(uri);
                    HttpURLConnection conn = (HttpURLConnection) url
                            .openConnection();
                    conn.setDoInput(true);
                    conn.connect();
                    InputStream in = conn.getInputStream();
                    // 2865412
                    long length = conn.getContentLength();
                    // 每次读取1k
                    byte[] buffer = new byte[perLength];
                    int len = -1;
                    FileOutputStream out = new FileOutputStream(file);
                    int temp = 0;
                    while ((len = in.read(buffer)) != -1) {
                        // 写入文件
                        out.write(buffer, 0, len);
                        // 当前进度
                        int schedule = (int) ((file.length() * 100) / length);
                        // 通知更新进度（10,7,4整除才通知，没必要每次都更新进度）
                        if (temp != schedule && (schedule % 10 == 0 || schedule % 4 == 0 || schedule % 7 == 0)) {
                            // 保证同一个数据只发了一次
                            temp = schedule;
                            handler.sendEmptyMessage(schedule);
                        }
                    }
                    out.flush();
                    out.close();
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    /**
     * 更新通知栏
     */
    private Handler completeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // 更新通知栏
            if (msg.what < 100) {
                notify.contentView.setTextViewText(
                        R.id.notify_updata_values_tv, msg.what + "%");
                notify.contentView.setProgressBar(R.id.notify_updata_progress,
                        100, msg.what, false);
                manager.notify(100, notify);
            } else {
                notify.contentView.setTextViewText(
                        R.id.notify_updata_values_tv, "下载完成");
                notify.contentView.setProgressBar(R.id.notify_updata_progress,
                        100, msg.what, false);// 清除通知栏
                manager.cancel(100);
                installApk();// 安装Apk文件
            }
        }
    };

    /**
     * 安装APK文件
     */
    private void installApk() {
        OkhttpConstant.isDownloading = false;
        File apkfile = new File(saveFileName, "VRSHOW.apk");
        if (!apkfile.exists()) {
            return;
        }
        // 通过Intent安装APK文件
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkfile.toString()), "application/vnd.android.package-archive");
        mContext.startActivity(i);
    }

    /**
     * @param @param message    设定文件
     * @return void    返回类型
     * @Title: showToast
     * @Description: TODO 消息提示
     * @author hechuang
     * @date 2015-11-12
     */
    public void showToastMsg(String msg) {
        Toast.makeText(mContext, msg, Toast.LENGTH_SHORT).show();
    }
}
