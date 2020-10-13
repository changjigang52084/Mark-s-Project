package com.unccr.zclh.dsdps.receiver;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.unccr.zclh.dsdps.download.DownloadManager;
import com.unccr.zclh.dsdps.download.ResposeDownloadService;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.ThreadPoolManager;

import java.util.ArrayList;
import java.util.List;

public class CancelDownloadReceiver extends BroadcastReceiver {

    public static final String CANCEL_DOWNLOAD_SIZE_KEY = "cancelDownloadSize";

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (null != intent) {
            final Bundle bundle = intent.getExtras();
            ThreadPoolManager.get().addRunnable(new Runnable() {
                @Override
                public void run() {
                    if (null != bundle) {
                        List<String> downloadList = bundle.getStringArrayList(Constants.CANCEL_DOWNLOAD_FILE_LIST);
                        if (null == downloadList || downloadList.isEmpty()) {
                            return;
                        }
                        List<String> flieNames = new ArrayList<String>();
                        for (String fileName : downloadList) {
                            if (!TextUtils.isEmpty(fileName)) {
                                if (fileName.indexOf("http://") == 0) {
                                    fileName = FileUtil.getFileName(fileName);
                                }
                                flieNames.add(fileName);
                            }
                        }
                        DownloadManager.newInstance().cancelTaskToFileNames(flieNames);
                    }
                }
            });
        }
    }

    /**
     * 取消下载的数
     *
     * @param context    上下文
     * @param cancelSize 取消的下载数
     */
    private void cancelDownloadSize(Context context, int cancelSize) {
        Intent resposeDownloadIntent = new Intent(context, ResposeDownloadService.class);
        resposeDownloadIntent.putExtra(Constants.DOWNLOAD_TYPE_KEY, Constants.CANCEL_DOWNLOAD);
        resposeDownloadIntent.putExtra(CANCEL_DOWNLOAD_SIZE_KEY, cancelSize);
        resposeDownloadIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startService(resposeDownloadIntent);
    }
}
