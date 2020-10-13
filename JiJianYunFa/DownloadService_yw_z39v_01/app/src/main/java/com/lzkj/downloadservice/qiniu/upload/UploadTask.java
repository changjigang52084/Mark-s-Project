package com.lzkj.downloadservice.qiniu.upload;

import com.alibaba.fastjson.JSON;
import com.lzkj.downloadservice.bean.HttpRequestBean;
import com.lzkj.downloadservice.interfaces.IRequestCallback;
import com.lzkj.downloadservice.qiniu.impl.QiNiuUploadStateCallback;
import com.lzkj.downloadservice.util.ConfigSettings;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.FlowManager;
import com.lzkj.downloadservice.util.HttpUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;

import org.json.JSONObject;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * @author kchang Email:changkai@lz-mr.com
 * @version 1.0
 * @date 创建时间：2015年6月2日 上午10:49:04
 * @parameter 七牛上传类
 */

public class UploadTask implements UpProgressHandler, UpCompletionHandler, IRequestCallback {
    private static final LogTag TAG = LogUtils.getLogTag(UploadTask.class.getSimpleName(), true);
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
     * 上传一组文件
     * @param uploadFilePathList
     */
//	public void executeUploadList(List<String> uploadFilePathList) {
//		initUpload(null);
//		this.uploadFilePath = uploadFilePath;
//	}
//	public void executeUploadList(List<String> uploadFilePathList,List<String> uploadFileNameList) {
//		
//	}

    /**
     * 取消上传的方法
     */
    public void cancelUpload() {
        isCancelled = true;
        LogUtils.d(TAG, "upload cancelUpload", "isCancelled:" + isCancelled);
    }

    /**
     * 获取七牛云存储上传的token
     *
     * @throws UnsupportedEncodingException
     */
    private void initUpload(String fileName, int uploadType) throws UnsupportedEncodingException {
        File uploadFile = new File(uploadFilePath);
        FileUtil.getInstance().writeUploadLog(uploadFilePath + "_start");
        LogUtils.d(TAG, "initUpload", "uploadFilePath: " + uploadFilePath);
        if (uploadFile.exists()) {
            HttpRequestBean httpRequest = new HttpRequestBean();
            httpRequest.setRequestCallback(this);
            httpRequest.setRequestParm(String.format(BUCKET_TYPE_KEY, uploadType, URLEncoder.encode(URLEncoder.encode(getUpFileName(), "UTF-8"), "UTF-8")));
            httpRequest.setRequestRestry(5);
            httpRequest.setRequestTag(UploadTask.class.getSimpleName());
            String requestUrl = String.format(Constant.GET_UPLOAD_TOKEN_URL, ConfigSettings.getWorkServer());
            httpRequest.setRequestUrl(requestUrl);
            HttpUtil.newInstance().sendGetRequest(httpRequest);
        } else {
            LogUtils.d(TAG, "initUpload", "uploadFilePath");
            FileUtil.getInstance().writeUploadLog(uploadFilePath + "_not exists");
        }
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

    /**
     * http请求返回成功的结果
     *
     * @param result
     */
    @Override
    public void onSuccess(String result, String requestHttpUrl, String requestTag) {
        upload(result);
    }

    /**
     * 上传文件
     *
     * @param uploadToken 服务器端获取到的uploadToken
     */
    private void upload(String uploadToken) {
        try {
            String uptoken = null;
            if (null != uploadToken) {
                JSONObject jsonObject = new JSONObject(uploadToken);
                uptoken = jsonObject.optString("data", null);
            }
            FileUtil.getInstance().writeUploadLog(uploadFilePath + "_upload token:" + uptoken);
            LogUtils.d(TAG, "upload", "uploadToken: " + uptoken);
            String dirPath = FileUtil.getInstance().getQiNiuRecorderFolder();
            Recorder recorder = new FileRecorder(dirPath);
            //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
            //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
            KeyGenerator keyGen = new KeyGenerator() {
                public String gen(String key, File file) {
                    // 不必使用url_safe_base64转换，uploadManager内部会处理
                    // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
                    return key + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
                }
            };
            UploadOptions uploadOptions = new UploadOptions(null, null, false, this, cancellationSignal);
            UploadManager uploadManager = new UploadManager(recorder, keyGen);
            String key = getUpFileName();
            FileUtil.getInstance().writerDownloadTask(JSON.toJSONString(key), false);
            uploadManager.put(uploadFilePath, key, uptoken, this, uploadOptions);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * http请求失败以后的结果
     */
    @Override
    public void onFaile(String errMsg, String requestHttpUrl, String requestTag) {
        LogUtils.d(TAG, "http onFaile", "errMsg:" + errMsg);
    }

    /**
     * 上传进度
     */
    @Override
    public void progress(String key, double percent) {
        int progress = (int) (percent * 100);
        LogUtils.d(TAG, "upload progress", "progress: " + percent);
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
            File file = new File(uploadFilePath);
            if (file.exists()) {
                FlowManager.getInstance().addUploadFlow(file.length());
            }
            FileUtil.getInstance().writeUploadLog(uploadFilePath + "_upload complete success ");
            if (null != uploadStateCallback) {
                uploadStateCallback.onUploadSuccess(key);
            }
            LogUtils.d(TAG, "upload complete", "upload success");
        } else {
            String errMsg = info.error;
            FileUtil.getInstance().writeUploadLog(uploadFilePath
                    + "_upload complete error: " + errMsg);
            if (null != uploadStateCallback) {
                uploadStateCallback.onUploadFail(key, errMsg);
            }
            LogUtils.d(TAG, "upload complete", "key:" + key + "errMsg:" + errMsg + ",statusCode:" + info.statusCode);
        }
    }

    public String getUploadFilePath() {
        return uploadFilePath;
    }
}