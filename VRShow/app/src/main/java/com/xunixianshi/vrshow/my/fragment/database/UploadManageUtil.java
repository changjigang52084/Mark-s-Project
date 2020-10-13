package com.xunixianshi.vrshow.my.fragment.database;

import android.content.Context;

import com.hch.utils.MD5Encryption;
import com.hch.utils.rsa.Base64;
import com.hch.viewlib.util.MLog;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.Configuration;
import com.qiniu.android.storage.KeyGenerator;
import com.qiniu.android.storage.Recorder;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;
import com.qiniu.android.storage.persistent.FileRecorder;
import com.qiniu.android.utils.UrlSafeBase64;
import com.sina.weibo.sdk.utils.MD5;
import com.xunixianshi.vrshow.interfaces.UploadInterface;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Administrator on 2016/10/11.
 */

public class UploadManageUtil {

    private UploadManager uploadManager;
    private static UploadManageUtil uploadManagerUtil;
    public static boolean isCancelled = true;
    private Map<String, UploadInterface> mUploadInterfaces;
    private boolean noRepeat = true;
    private String MD5VideoTitle;

    public UploadManageUtil() {
        mUploadInterfaces = new HashMap<String, UploadInterface>();
    }

    public static UploadManageUtil getInstance() {
        if (uploadManagerUtil == null) {
            uploadManagerUtil = new UploadManageUtil();
        }
        return uploadManagerUtil;
    }

    /**
     * 注册上传监听
     *
     * @author HeChuang
     * @time 2016/10/11 16:32
     */
    public void registUploadListener(String videoTitle, UploadInterface uploadInterface) {
        removeRepeatListener(videoTitle, uploadInterface, true);
    }

    /**
     * 移除上传监听
     *
     * @author HeChuang
     * @time 2016/10/11 16:32
     */
    public void removeUploadListener(String videoTitle) {
        mUploadInterfaces.remove(videoTitle);
    }

    /**
     * 上传视频资源到七牛服务器
     *
     * @param token
     */
    public void uploadVideoToQiniuServer(String token, final String videoTitle, String videoPath, final UploadInterface uploadInterface, String videoFormat, boolean isNew) {
        initUpload(videoTitle);
        removeRepeatListener(videoTitle, uploadInterface, true);
        isCancelled = false;
        MD5VideoTitle = videoTitle + videoFormat;
        uploadManager.put(videoPath, MD5VideoTitle, token,
                new UpCompletionHandler() {
                    public void complete(String key,
                                         ResponseInfo info, JSONObject res) {
                        MLog.d(" uploadManager.put~~key:" + key);
                        MLog.d(" uploadManager.put~~key:" + info);
                        Iterator<Map.Entry<String, UploadInterface>> it = mUploadInterfaces.entrySet().iterator();
                        if(info.statusCode != 200){
                            cancelUpload();
                            while (it.hasNext()) {
                                Map.Entry<String, UploadInterface> entry = it.next();
                                if (entry.getKey().equals(videoTitle)) {
                                    entry.getValue().uploadPause();
                                    break;
                                }
                            }
                        }
                        while (it.hasNext()) {
                            Map.Entry<String, UploadInterface> entry = it.next();
                            if (entry.getKey().equals(videoTitle)) {
                                entry.getValue().uploadComplete(MD5VideoTitle, info, res);
                                break;
                            }
                        }
                    }
                }, new UploadOptions(null, null, false,
                        new UpProgressHandler() {
                            public void progress(String key, double percent) {
                                MLog.e("progress::" + percent);
                                Iterator<Map.Entry<String, UploadInterface>> it = mUploadInterfaces.entrySet().iterator();
                                while (it.hasNext()) {
                                    Map.Entry<String, UploadInterface> entry = it.next();
                                    if (entry.getKey().equals(videoTitle)) {
                                        entry.getValue().uploadProgress(key, percent);
                                        break;
                                    }
                                }
                            }
                        }, new UpCancellationSignal() {
                    @Override
                    public boolean isCancelled() {
                        if (isCancelled == true) {
                            removeRepeatListener(videoTitle, uploadInterface, false);
                        }
                        return isCancelled;
                    }
                }));
    }

    /**
     * 取消上传
     *
     * @author HeChuang
     * @time 2016/10/11 16:31
     */
    public void cancelUpload() {
        isCancelled = true;
    }

    public void initUpload(String videoTitle) {
        //断点记录文件保存的文件夹位置
        String dirPath = "/storage/emulated/0/Download";
        Recorder recorder = null;// 分片上传中，可将各个已上传的块记录下来，再次上传时，已上传的部分不用再次上传。断点记录类需要实现Recorder接口。
        try {
            File f = File.createTempFile(videoTitle, ".tmp");
            MLog.e("qiniu：：：：" + f.getAbsolutePath().toString());
            dirPath = f.getParent();
            recorder = new FileRecorder(dirPath);
        } catch (Exception e) {
            e.printStackTrace();
        }

        final String dirPath1 = dirPath;
        //默认使用 key 的url_safe_base64编码字符串作为断点记录文件的文件名。
        //避免记录文件冲突（特别是key指定为null时），也可自定义文件名(下方为默认实现)：
        KeyGenerator keyGen = new KeyGenerator() {
            public String gen(String videoTitle, File file) {
                // 不必使用url_safe_base64转换，uploadManager内部会处理
                // 该返回值可替换为基于key、文件内容、上下文的其它信息生成的文件名
                String path = videoTitle + "_._" + new StringBuffer(file.getAbsolutePath()).reverse();
                MLog.e("qiniu：" + path);
                File f = new File(dirPath1, UrlSafeBase64.encodeToString(path));
                BufferedReader reader = null;
                try {
                    reader = new BufferedReader(new FileReader(f));
                    String tempString = null;
                    int line = 1;
                    try {
                        while ((tempString = reader.readLine()) != null) {
                            MLog.e("qiniu  line " + line + ": " + tempString);
                            line++;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        try {
                            reader.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                return path;
            }
        };
        // 初始化 一般情况下可以直接使用默认设置，不用单独配置。可以配置超时时长，分片上传阀值等。
        Configuration config = new Configuration.Builder()
                .recorder(recorder, keyGen) // keyGen 分片上传时，生成标识符，用于片记录器区分是那个文件的上传记录
                .build();
        // 重用uploadManager。一般地，只需要创建一个uploadManager实例
        uploadManager = new UploadManager(config);
    }

    /**
     * @author HeChuang
     * @time 2016/10/11 16:54
     */
    public void removeRepeatListener(String videoTitle, UploadInterface uploadInterface, boolean isRegist) {
        Map<String, UploadInterface> mUploadInterfacesTemp = new HashMap<String, UploadInterface>();
        if (mUploadInterfaces != null) {
            mUploadInterfacesTemp.putAll(mUploadInterfaces);
            Iterator<Map.Entry<String, UploadInterface>> it = mUploadInterfacesTemp.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, UploadInterface> entry = it.next();
                MLog.d("videoTitle::"+videoTitle);
                MLog.d("entry.getKey()::"+entry.getKey());
                if (entry.getKey().equals(videoTitle)) {
                    mUploadInterfaces.remove(videoTitle);
                    uploadInterface.uploadPause();
                    break;
                }
            }
        }
        if (isRegist) {
            mUploadInterfaces.put(videoTitle, uploadInterface);
        }

    }
}
