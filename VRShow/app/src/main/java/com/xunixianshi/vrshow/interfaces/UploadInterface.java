package com.xunixianshi.vrshow.interfaces;

import com.qiniu.android.http.ResponseInfo;

import org.json.JSONObject;

/**
 * 更新操作接口
 * @ClassName UploadInterface
 *@author HeChuang
 *@time 2016/11/1 15:38
 */
public interface UploadInterface {
    void uploadComplete(String key, ResponseInfo info, JSONObject res);
    void uploadProgress(String key, double percent);
    void uploadPause();
    void uploadFail();
}
