package com.xunixianshi.vrshow.webView;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.webkit.JavascriptInterface;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hch.viewlib.util.MLog;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.obj.HomeResultVRHot;

import java.net.URLDecoder;

/**
 * web页面展示页
 * @ClassName ShowWebViewActivity
 *@author HeChuang
 *@time 2016/11/1 15:49
 */
public class ShowWebViewActivity extends BaseAct {

    private ImageView back_icon_iv;
    private WebView home_label_webview;
    private RelativeLayout webview_loading_rl;
//    private ImageView webview_loading_round_iv;
    private String loadUrl;
    private boolean showProgress = true;
    private Animation animation;
    private ProgressBar progressBar;

    private LoadingAnimationDialog mLoadingAnimationDialog;

    private TextView web_view_title;

    private boolean isJumpPage;

  /*  打开本地相册*/
    public ValueCallback<Uri> mUploadMessage;
    public ValueCallback<Uri[]> mUploadMessageForAndroid5;

    public final static int FILECHOOSER_RESULTCODE = 1;
    public final static int FILECHOOSER_RESULTCODE_FOR_ANDROID_5 = 2;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_home_webview);
    }

    @Override
    protected void initView() {
        // TODO Auto-generated method stub
        super.initView();
        back_icon_iv = (ImageView) findViewById(R.id.back_icon_iv);
        webview_loading_rl = (RelativeLayout) findViewById(R.id.webview_loading_rl);
//        webview_loading_round_iv = (ImageView) findViewById(R.id.webview_loading_round_iv);
        home_label_webview = (WebView) findViewById(R.id.home_label_webview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        web_view_title = (TextView) findViewById(R.id.web_view_title);

        mLoadingAnimationDialog = new LoadingAnimationDialog(this);
        initListener();
    }

    private void initListener() {
        back_icon_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ShowWebViewActivity.this.finish();
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        loadUrl = (String) getIntent().getExtras().get("loadUrl");
        showProgress = (boolean) getIntent().getExtras().get("showProgress");
        String title = (String) getIntent().getExtras().get("title");
        if(title!=null && !title.equals("")){
            web_view_title.setText(title);
        }
        if (loadUrl.equals("")) {
            showToastMsg("访问地址为空");
            ShowWebViewActivity.this.finish();
            return;
        }
        MLog.d("loadUrl:" + loadUrl);
        home_label_webview.getSettings().setJavaScriptEnabled(true);
        home_label_webview.getSettings().setAppCacheEnabled(false);
        home_label_webview.getSettings().setAllowFileAccess(true);
        home_label_webview.getSettings().setAllowFileAccessFromFileURLs(true);
        home_label_webview.getSettings().setAllowUniversalAccessFromFileURLs(true);

        home_label_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        home_label_webview.addJavascriptInterface(new JavaScriptinterface(),
                "android");
        home_label_webview.loadUrl(loadUrl);
//        home_label_webview.loadUrl("http://jssdk.demo.qiniu.io/");
        home_label_webview.setWebViewClient(new myWebViewClient());
        home_label_webview.setWebChromeClient(new WebChromeClient(){
            public void onProgressChanged(WebView view, int progress) {// 载入进度改变而触发
                MLog.d("progress:::" + progress);
                progressBar.setProgress(progress);
                if (progress >= 100) {
//                    webview_loading_round_iv.clearAnimation();
//                //模拟触屏点击屏幕事件
//                int x = 0;
//                int y = 0;
//                final long downTime = SystemClock.uptimeMillis();
//                final MotionEvent downEvent = MotionEvent.obtain(
//                        downTime, downTime, MotionEvent.ACTION_DOWN, 1000, 500, 0);
//                final MotionEvent upEvent = MotionEvent.obtain(
//                        downTime, SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 1000, 500, 0);
//                //添加到webview_loading_round_iv上
//                home_label_webview.onTouchEvent(downEvent);
//                home_label_webview.onTouchEvent(upEvent);
//                downEvent.recycle();
//                upEvent.recycle();
                    webview_loading_rl.setVisibility(View.GONE);
                    progressBar.setVisibility(View.GONE);
                    mLoadingAnimationDialog.dismiss();
                }
            }
            //扩展支持alert事件
            @Override
            public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("提示").setMessage(message).setPositiveButton("确定", null);
                builder.setCancelable(false);
                AlertDialog dialog = builder.create();
                dialog.show();
                result.confirm();
                return true;
            }
            //扩展浏览器上传文件
            //3.0++版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
                openFileChooserImpl(uploadMsg);
            }

            //3.0--版本
            public void openFileChooser(ValueCallback<Uri> uploadMsg) {
                openFileChooserImpl(uploadMsg);
            }

            public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
                openFileChooserImpl(uploadMsg);
            }

            // For Android > 5.0
            public boolean onShowFileChooser (WebView webView, ValueCallback<Uri[]> uploadMsg, WebChromeClient.FileChooserParams fileChooserParams) {
                openFileChooserImplForAndroid5(uploadMsg);
                return true;
            }
        });
        if (showProgress) {
//            animation = AnimationUtils.loadAnimation(ShowWebViewActivity.this, R.anim.rotate_animation);
//            LinearInterpolator interpolator = new LinearInterpolator();
//            animation.setInterpolator(interpolator);// 表示设置旋转速率，LinearInterpolator为匀速效果，AccelerateInterpolator为加速效果，DecelerateInterpolator为减速效果。
            if (animation != null) {
//                webview_loading_round_iv.startAnimation(animation);
            }
        } else {
            webview_loading_rl.setVisibility(View.GONE);
        }

        mLoadingAnimationDialog.show();

        isJumpPage = false;
    }

    private void openFileChooserImpl(ValueCallback<Uri> uploadMsg) {
        mUploadMessage = uploadMsg;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "File Chooser"), FILECHOOSER_RESULTCODE);
    }

    private void openFileChooserImplForAndroid5(ValueCallback<Uri[]> uploadMsg) {
        mUploadMessageForAndroid5 = uploadMsg;
        Intent contentSelectionIntent = new Intent(Intent.ACTION_GET_CONTENT);
        contentSelectionIntent.addCategory(Intent.CATEGORY_OPENABLE);
        contentSelectionIntent.setType("image/*");

        Intent chooserIntent = new Intent(Intent.ACTION_CHOOSER);
        chooserIntent.putExtra(Intent.EXTRA_INTENT, contentSelectionIntent);
        chooserIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser");

        startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE_FOR_ANDROID_5);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent intent) {
        if (requestCode == FILECHOOSER_RESULTCODE) {
            if (null == mUploadMessage)
                return;
            Uri result = intent == null || resultCode != RESULT_OK ? null: intent.getData();
            mUploadMessage.onReceiveValue(result);
            mUploadMessage = null;

        } else if (requestCode == FILECHOOSER_RESULTCODE_FOR_ANDROID_5){
            if (null == mUploadMessageForAndroid5)
                return;
            Uri result = (intent == null || resultCode != RESULT_OK) ? null: intent.getData();
            if (result != null) {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{result});
            } else {
                mUploadMessageForAndroid5.onReceiveValue(new Uri[]{});
            }
            mUploadMessageForAndroid5 = null;
        }
    }



    // 设置回退
    // 覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && home_label_webview.canGoBack()) {
            home_label_webview.goBack(); // goBack()表示返回WebView的上一页面
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (isJumpPage && home_label_webview.canGoBack()) {
            home_label_webview.goBack();
        }
        isJumpPage = false;
    }

    class myWebViewClient extends WebViewClient {

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            view.loadUrl(url);
            progressBar.setVisibility(View.VISIBLE);
            return true;
        }

    }


    class JavaScriptinterface {

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void transferParameter(String parameterJson) {
            MLog.d("parameterJson:" + parameterJson);
            HomeResultVRHot result = null;
            try {

                result = new Gson().fromJson(parameterJson,
                        HomeResultVRHot.class);
            } catch (Exception e) {
                // TODO: handle exception
            }
            if (result == null) {
                showToastMsg("播放地址为空");
                return;
            }
            if (result.getResourcesUrl().equals("")) {
                showToastMsg("播放地址为空");
                return;
            } else {
                String videoUrl = "";
                String videoName = "";
                int videoType = 0;
                try {
                    videoUrl = URLDecoder.decode(result.getResourcesUrl(), "utf-8");
                    videoName = URLDecoder.decode(result.getResourcesTitle(), "utf-8");
                    videoType = result.getResourcesType();
                } catch (Exception e) {
                }
                isJumpPage = true;
                MLog.d("videoName:" + videoName);
                MLog.d("videoUrl:" + videoUrl);
                MLog.d("videoType:" + videoType);
//                点播资源类型, 1表示普通视频，2表示左右格式3D，3表示单画面全景，4表示上下格式3D，5表示上下格式全景 ,100 表示图片资源 ， 101表示图片资源网页连接
                switch (videoType) {
                    default:
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
//                        VRPlayerActivity.intentTo(ShowWebViewActivity.this, videoName, videoUrl, videoType, 0, 0, true); //web页面无视id
                        //ShowWebViewActivity.this.finish();
                        break;
                    case 100:
                        videoUrl = "http://imgsrc.baidu.com/forum/w%3D580/sign=c49c299a17ce36d3a20483380af23a24/213fb80e7bec54e7a9f8cf5ebb389b504fc26a5e.jpg";
//                        PicturePlayerActivity.intentTo(ShowWebViewActivity.this,videoName,videoUrl);
                        break;
                    case 101:
                        Bundle bundle = new Bundle();
                        bundle.putString("loadUrl", videoUrl);
                        bundle.putBoolean("showProgress", false);
                        showActivity(ShowWebViewActivity.this, ShowWebViewActivity.class, bundle);
                        break;
                }
            }
        }
    }
}
