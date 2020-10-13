package com.xunixianshi.vrshow;

import android.content.Context;
import android.util.Log;

import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.FileDownloaderListener;
import com.hch.filedownloader.download.FileDownloaderModel;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by xnxs on 2016/5/10.
 */
public class GlobalDownloadListener implements FileDownloaderListener {
    //    private HttpRequestQueque requestQueque;
    public GlobalDownloadListener(Context context) {
//        requestQueque = new HttpRequestQueque(context);
    }

    @Override
    public void onStart(int downloadId, long soFarBytes, long totalBytes, int preProgress) {
        Log.d("cjg", "onStart \ndownloadId: " + downloadId + "\nsoFarBytes: " + soFarBytes + "\ntotalBytes: " + totalBytes + "\npreProgress: " + preProgress);
    }

    @Override
    public void onProgress(int downloadId, long soFarBytes, long totalBytes, long speed, int progress) {
        Log.d("cjg", "onProgress \ndownloadId: " + downloadId + "\nsoFarBytes: " + soFarBytes + "\ntotalBytes: " + totalBytes + "\nspeed: " + speed + "\nprogress: " + progress);
    }

    @Override
    public void onWait(int downloadId) {
        Log.d("cjg", "onWait \ndownloadId: " + downloadId);
    }

    @Override
    public void onStop(int downloadId, long soFarBytes, long totalBytes, int progress) {
        Log.d("cjg", "onStop \ndownloadId: " + downloadId + "\nsoFarBytes: " + soFarBytes + "\ntotalBytes: " + totalBytes + "\nprogress: " + progress);
    }

    @Override
    public void onFinish(int downloadId, String path) {
        Log.d("cjg", "onFinish \ndownloadId: " + downloadId + "\npath: " + path);
    }

    /**
     * @param downloadId 任务id
     * @param e          异常信息
     *                   如果发现鉴权失败异常，重新获取一次下载密钥，并更新
     */
    @Override
    public void onError(final int downloadId, Throwable e) {
        if (e.getMessage().contains("response code error: 403")) {
            FileDownloaderModel fileDownloaderModel = DownloaderManager.getInstance().getFileDownloaderModelById(downloadId);
            if (fileDownloaderModel == null) {
                return;
            }
            DownloaderManager.getInstance().setAuthentication(downloadId, true);
            String resourceId = fileDownloaderModel.getExtFieldValue(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_MARK_ID);
            postLatestDownloadAddress(Integer.valueOf(resourceId), downloadId);
        }
    }

    private boolean postLatestDownloadAddress(int resourceId, final int downloadId) {
        String uid;
        if (AppContent.UID.equals("")) {
            return false;
        } else {
            uid = AppContent.UID;
        }

        if (AppContent.LIMIT_LOGIN == 2) {
            return false;
        }
        // 加密手机号
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourcesId", resourceId);
        hashMap.put("userId", Ras_uid);
        hashMap.put("urlType", 2); //1是播放，2是下载
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/new/url/service", jsonData, new GenericsCallback<DownLoadAddressResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                notifyDownLoadStateFail(downloadId);
                DownloaderManager.getInstance().setAuthentication(downloadId, false);
            }

            @Override
            public void onResponse(DownLoadAddressResult result, int id) {
                if (result.getResCode().equals("0")) {
                    String urlPath = result.getUrl();
                    if (urlPath.equals("") && urlPath.length() <= 0) {
                        notifyDownLoadStateFail(downloadId);
                    } else {
                        boolean success = DownloaderManager.getInstance().updateUrlKey(downloadId, StringUtil.subString(urlPath, "?", true), StringUtil.subString(urlPath, "?", false));
                        if (success) {
                            DownloaderManager.getInstance().startTask(downloadId);
                        } else {
                            notifyDownLoadStateFail(downloadId);
                        }
                    }
                } else {
                    notifyDownLoadStateFail(downloadId);
                }
                DownloaderManager.getInstance().setAuthentication(downloadId, false);
            }
        });
        return true;
    }

    private void notifyDownLoadStateFail(int downloadId) {
        DownloaderManager.getInstance().pauseTask(downloadId);
    }
}
