package com.unccr.zclh.dsdps.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.models.Area;

@SuppressLint("ValidFragment")
public class WebViewFragment extends BaseFragment {

    private static final String TAG = "WebViewFragment";

    /**
     * 加载网页的webview
     **/
    private WebView loadWebpageWv;
    /**
     * 显示网页加载进度
     **/
    private ProgressBar webpageLoadPbar;
    /**
     * 布局文件
     */
    private View layoutView;
    /**
     * 网页加载完成
     **/
    private int webpageLoadComplete = 100;

    private String webpage;

    public WebViewFragment(Area area) {
        super(area);
        webpage = area.getU();
        Log.d(TAG, "WebViewFragment webpage: " + webpage);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == layoutView) {
            layoutView = inflater.inflate(R.layout.fragment_webview, container, false);
            loadWebpageWv = (WebView) layoutView.findViewById(R.id.wv_load_webpage);
            webpageLoadPbar = (ProgressBar) layoutView.findViewById(R.id.pbar_webpage_progress);
            webpageLoadPbar.setMax(webpageLoadComplete);
            initWebViewConfig();
        }
        return layoutView;
    }

    /**
     * 初始化webview 设置
     */
    private void initWebViewConfig() {
        loadWebpageWv.setWebViewClient(new WebViewClientLinstener());
        loadWebpageWv.setWebChromeClient(new WebChromeClientProgress());
        WebSettings webSettings = loadWebpageWv.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setUseWideViewPort(true);
        webSettings.setLoadsImagesAutomatically(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
    }

    @Override
    protected void startLoadingTask() {
        Log.d(TAG, "startLoadingTask 开始加载网页。materialList is null.");
        loadWebpageWv.loadUrl(webpage);
    }

    @Override
    public void resumePlayback() {

    }

    /**
     *
     * @Description:网页加载错误
     * @time:2019年10月31日 14:51:29
     */
    private class WebViewClientLinstener extends WebViewClient {
        @Override
        public void onReceivedError(WebView view, int errorCode,
                                    String description, String failingUrl) {
            webpageLoadPbar.setVisibility(View.GONE);
            super.onReceivedError(view, errorCode, description, failingUrl);
        }
    }

    /**
     *
     * @Description:监听网页加载进度
     * @time:2019年10月31日 14:52:21
     */
    private class WebChromeClientProgress extends WebChromeClient {
        @Override
        public void onProgressChanged(WebView view, int newProgress) {
            if (webpageLoadComplete == newProgress) {
                webpageLoadPbar.setVisibility(View.GONE);
            } else {
                if (webpageLoadPbar.getVisibility() == View.GONE) {
                    webpageLoadPbar.setVisibility(View.VISIBLE);
                }
                webpageLoadPbar.setProgress(newProgress);
            }
            super.onProgressChanged(view, newProgress);
        }
    }

    @Override
    public void pausePlayback() {
    }

    @Override
    public void stopPlayback() {
    }
}
