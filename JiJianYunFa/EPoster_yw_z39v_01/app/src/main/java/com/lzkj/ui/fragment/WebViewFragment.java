package com.lzkj.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.lzkj.ui.R;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月20日 上午10:52:47 
 * @version 1.0 
 * @parameter  显示网页的区域
 */
@SuppressLint("ValidFragment")
public class WebViewFragment extends BaseFragment {
	/**加载网页的webview**/
	private WebView loadWebpageWv;
	/**显示网页加载进度**/
	private ProgressBar webpageLoadPbar;
	/**布局文件*/
	private View layoutView;
	/**网页加载完成**/
	private int webpageLoadComplete = 100;
	public WebViewFragment(Area area) {
		super(area);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null == layoutView) {
			layoutView      = inflater.inflate(R.layout.fragment_webview, container, false);
			loadWebpageWv   = (WebView) layoutView.findViewById(R.id.wv_load_webpage);
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
//		loadWebpageWv.loadUrl(url);
	}
	
	@Override
	public void resumePlayback() {

	}
	
	/**
	 *@author kchang changkai@lz-mr.com
	 *@Description:网页加载错误
	 *@time:2016年4月22日 下午12:51:29
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
	 *@author kchang changkai@lz-mr.com
	 *@Description:监听网页加载进度
	 *@time:2016年4月22日 上午10:52:21
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
