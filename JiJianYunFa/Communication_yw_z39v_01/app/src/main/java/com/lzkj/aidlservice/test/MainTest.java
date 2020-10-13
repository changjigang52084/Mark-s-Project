package com.lzkj.aidlservice.test;

import com.lzkj.aidlservice.api.manager.HttpManager;
import com.lzkj.aidlservice.bo.HttpRequestBean;

import java.io.File;

/**
 * @version 1.0
 * @Author kchang Email:changkai@17-tech.com
 * @Date Created by kchang on 2016/11/16.
 * @Parameter
 */

public class MainTest {

    public static void main(String[] args) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                System.out.println("MainTest");
                String httpUrl = "http://192.168.0.124:9090/mallappss-cc/files/upload?system=4";
                HttpRequestBean httpRequestBean = new HttpRequestBean();
                httpRequestBean.setRequestUrl(httpUrl);

                File uploadFile = new File("F:\\1463831641699.jpg");
                HttpManager httpManager = HttpManager.getInstance();
                httpManager.uploadFile(uploadFile, httpRequestBean, new HttpManager.IProgressListener() {
                    @Override
                    public void onProgress(long contentLength, long progress, boolean isDone) {
                        System.out.println("onProgress progress : " + progress + ", isDone : " + isDone);
                    }

                    @Override
                    public void onFailure(String requestUrl, String errorMsg) {
                        System.out.println("onFailure requestUrl : " + requestUrl + ", errorMsg : " + errorMsg);
                    }
                });
            }
        }).start();

    }
}
