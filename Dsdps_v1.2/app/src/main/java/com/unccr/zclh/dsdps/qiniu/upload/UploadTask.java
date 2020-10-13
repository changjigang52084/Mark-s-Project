package com.unccr.zclh.dsdps.qiniu.upload;

import android.util.Log;

import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.download.interfaces.IRequestCallback;
import com.unccr.zclh.dsdps.qiniu.upload.impl.QiNiuUploadStateCallback;
import com.unccr.zclh.dsdps.service.sign.SignUtil;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.DeviceUtil;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.PackageUtils;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.Util;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class UploadTask implements UpProgressHandler, UpCompletionHandler, IRequestCallback {

    private static final String TAG = "UploadTask";

    private static final String BUCKET_TYPE_KEY = "bucketType=%s&file=%s";
    /**
     * 上传中
     ***/
    public static final int STATE_UPLOADING = 1;
    /**
     * 下载失败
     ***/
    public static final int STATE_UPLOAD_FAILED = -1;
    /**
     * 下载成功
     ***/
    public static final int STATE_UPLOAD_SUCCESS = 2;
    /**
     * 本地文件路径
     */
    private String uploadFilePath = null;
    /**
     * 上传到云存储的文件名
     */
    private String uploadFileName = null;
    /**
     * 是否取消上传
     */
    private volatile boolean isCancelled;
    /**
     * 监听上传的状态
     */
    private QiNiuUploadStateCallback uploadStateCallback;
    /**
     * 取消上传的接口
     */
    private UpCancellationSignal cancellationSignal = new UpCancellationSignal() {
        public boolean isCancelled() {
            return isCancelled;
        }
    };

    public UploadTask(QiNiuUploadStateCallback uploadStateCallback) {
        this.uploadStateCallback = uploadStateCallback;
    }

    /**
     * 执行上传
     *
     * @param uploadFilePath 本地上传的文件路径
     */
    public void executeUpload(String uploadFilePath, int uploadType) {
        this.uploadFilePath = uploadFilePath;
        try {
            initUpload(null, uploadType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行上传
     *
     * @param uploadFilePath 本地上传的文件路径
     * @param uploadFileName 上传的文件名
     */
    public void executeUpload(String uploadFilePath, String uploadFileName, int uploadType) {
        this.uploadFilePath = uploadFilePath;
        this.uploadFileName = uploadFileName;
        try {
            initUpload(uploadFileName, uploadType);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 取消上传的方法
     */
    public void cancelUpload() {
        isCancelled = true;
        Log.d(TAG, "upload cancelUpload isCancelled: " + isCancelled);
    }

    /**
     * 上传图片
     *
     * @param fileName
     * @param uploadType
     * @throws UnsupportedEncodingException
     */
    private void initUpload(String fileName, int uploadType) throws UnsupportedEncodingException {
        File uploadFile = new File(uploadFilePath);

        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数key
        String key = DeviceUtil.getRandomString(18);
        // 参数time时间戳
        String time = new Date().getTime() + "";
        // app版本号
        String app_version = PackageUtils.getVersionName(DsdpsApp.getDsdpsApp());
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);

        Log.d(TAG, "initUpload uploadFilePath: " + uploadFilePath);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().build();
        //设置上传文件以及文件对应的MediaType类型
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/octet-stream"), uploadFile);

        // MultipartBody文件上传
        /**
         * 区别：
         *  addFormDataPart: 上传Key:value形式
         *  addPart: 只包含value数据
         */
        RequestBody body = new MultipartBody.Builder()
                .setType(MultipartBody.FORM) // 设置文件上传类型
                .addFormDataPart("key", "img") // 文件在服务器中保存的文件夹路径
                .addFormDataPart("file", uploadFile.getName(), requestBody) // 包含文件名字和内容
                .build();

        Request request = new Request.Builder()
                .url(Constants.SERVER_URL_HEAD +  SharedUtil.newInstance().getString("ipAddress",Constants.SERVER_URL_IP)+Constants.SERVER_URL_PORT  + "/v1/message/report/device/screen?mac=" + mac + "&sn=" + sn + "&key=" + key + "&time=" + time + "&sign=" + sign)
                .post(body)
                .build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "onFailure e: " + e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                Log.d(TAG, "onResponse str: " + str);
            }
        });

    }

    /**
     * 获取上传的文件名
     *
     * @return
     */
    private String getUpFileName() {
        if (null == uploadFileName) {
            return FileUtil.getFileNameToFilePath(uploadFilePath);
        }
        return uploadFileName;
    }

//    /**
//     * 上传文件
//     *
//     * @param uploadToken 服务器端获取到的uploadToken
//     */
//    private void upload(String uploadToken) {
//        try {
//            String uptoken = null;
//            if (null != uploadToken) {
//                JSONObject jsonObject = new JSONObject(uploadToken);
//                uptoken = jsonObject.optString("data", null);
//            }
//            Log.d(TAG, "upload uploadToken: " + uptoken);
//            String dirPath = FileUtil.getInstance().getQiNiuRecorderFolder();
//            Recorder recorder = new FileRecorder(dirPath);
//            //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
//            //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
//            KeyGenerator keyGen = new KeyGenerator() {
//                public String gen(String key, File file) {
//                    // 不必使用url_safe_base64转换，uploadManager内部会处理
//                    // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
//                    return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
//                }
//            };
//            UploadOptions uploadOptions = new UploadOptions(null, null, false, this, cancellationSignal);
//            UploadManager uploadManager = new UploadManager(recorder, keyGen);
//            String key = getUpFileName();
//            uploadManager.put(uploadFilePath, key, uptoken, this, uploadOptions);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }


    /**
     * 上传进度
     */
    @Override
    public void progress(String key, double percent) {
        int progress = (int) (percent * 100);
        Log.d(TAG, "upload progress progress: " + percent);
        if (null != uploadStateCallback) {
            uploadStateCallback.onUploadUpdateProgreass(progress, key);
        }
    }

    /**
     * 上传完成
     */
    @Override
    public void complete(String key, ResponseInfo info, JSONObject response) {
        if (info.isOK()) {
            if (null != uploadStateCallback) {
                uploadStateCallback.onUploadSuccess(key);
            }
            Log.d(TAG, "upload complete upload success");
        } else {
            String errMsg = info.error;
            if (null != uploadStateCallback) {
                uploadStateCallback.onUploadFail(key, errMsg);
            }
            Log.d(TAG, "upload complete key:" + key + "errMsg:" + errMsg + ",statusCode:" + info.statusCode);
        }
    }

    public String getUploadFilePath() {
        return uploadFilePath;
    }

    @Override
    public void onSuccess(String result, String httpUrl, String requestTag) {
//        upload(result);
    }

    @Override
    public void onFaile(String errMsg, String httpUrl, String requestTag) {
        Log.d(TAG, "http onFaile errMsg:" + errMsg);
    }
}