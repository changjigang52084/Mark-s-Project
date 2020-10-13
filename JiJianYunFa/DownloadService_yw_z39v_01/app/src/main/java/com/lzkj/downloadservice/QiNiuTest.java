package com.lzkj.downloadservice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lzkj.downloadservice.bean.HttpRequestBean;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.download.HttpDownloadTask;
import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;
import com.lzkj.downloadservice.interfaces.IRequestCallback;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.HttpUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCancellationSignal;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UpProgressHandler;
import com.qiniu.android.storage.UploadManager;
import com.qiniu.android.storage.UploadOptions;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月5日 下午3:34:56 
 * @version 1.0 
 * @parameter  
 */
public class QiNiuTest extends Activity implements OnClickListener,IDownloadStateCallback {
	private static final LogTag TAG = LogUtils.getLogTag(QiNiuTest.class.getSimpleName(), true);
	private String url = "http://192.168.0.111:8080/adpress/folder/returnUploadToken.action?uploadType=1";
	private String downloadUrl = "http://192.168.0.111:8080/adpress/folder/returnDownloadToken.action";
	private volatile boolean isCancelled;
	private Button upload_btn,download_btn;
	 protected void onCreate(Bundle savedInstanceState) {
		 super.onCreate(savedInstanceState);
		 setContentView(R.layout.qiniu_main);
		 upload_btn = (Button)findViewById(R.id.upload_btn);
		 download_btn = (Button)findViewById(R.id.download_btn);
		 upload_btn.setOnClickListener(this);
		 download_btn.setOnClickListener(this);
	 }
	 @Override
	protected void onResume() {
		super.onResume();
	}
	 @Override
	protected void onDestroy() {
		 DownloadManager.newInstance().cancelAllTask();
		super.onDestroy();
	}
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.upload_btn) {
			uploadFile();
		} else {
			downloadFile();
		}
	}

	/**取消上传的接口*/
	private UpCancellationSignal cancellationSignal = new UpCancellationSignal(){
        public boolean isCancelled(){
            return isCancelled;
        }
    };
    /**
     * 上传进度
     */
    private UpProgressHandler upProgressHandler = new UpProgressHandler(){
        public void progress(String key, double percent){
            LogUtils.d(TAG, "progress","key:"+key + ", progress:" + percent);
        }
	};
	/**
	 * 上传完成
	 */
	private UpCompletionHandler completionHandler =  new UpCompletionHandler() {
	    @Override
	    public void complete(String key, ResponseInfo info, JSONObject response) {
	    	LogUtils.d(TAG, "complete", info.toString());
	    }
	};
	private UploadOptions uploadOptions = new UploadOptions(null, null, false,upProgressHandler,null);
	
	private IRequestCallback requestCallback = new IRequestCallback() {
		@Override
		public void onSuccess(String result, String requestHttpUrl, String requestTag) {
			LogUtils.d(TAG, "onSuccess","result:"+result);

			UploadManager uploadManager = new UploadManager();
			String uploadFilePath = FileUtil.getInstance().getPicFolder() + File.separator+"meinv.jpg";
			try {
				JSONObject jsonObject = new JSONObject(result);
				String token = jsonObject.getString("uptoken");
				LogUtils.d(TAG, "onSuccess","token:"+token);
				FileInputStream fileInputStream = new FileInputStream(new File(uploadFilePath));
				int length = 0 ;
				byte[] data = new byte[1024];
				ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
				while((length = fileInputStream.read(data)) != -1) {
					arrayOutputStream.write(data, 0, length);
				}
				fileInputStream.close();
				arrayOutputStream.flush();
				byte[] fileData = arrayOutputStream.toByteArray();
				arrayOutputStream.close();
				String fileName = "meinv.jpg";
				uploadManager.put(fileData, fileName, token,completionHandler, uploadOptions);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onFaile(String errMsg, String requestHttpUrl, String requestTag) {
			LogUtils.d(TAG, "onFaile","errMsg:"+errMsg);
		}
	};
	private String downloadFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"image";
	private IRequestCallback requestDownloadCallback = new IRequestCallback() {
		@Override
		public void onSuccess(String result, String requestHttpUrl, String requestTag) {
			LogUtils.d(TAG, "onSuccess","result:"+result);
			try {
		
				JSONObject jsonObject = new JSONObject(result);
				String token = jsonObject.getString("uptoken");
//				String temp = new String(token.getBytes(),"UTF-8");
				LogUtils.d(TAG, "onSuccess","token:"+token);
//				HttpRequestBean httpRequest = new HttpRequestBean();
//				httpRequest.setRequestCallback(requestCallback);
//				httpRequest.setRequestParm(null);
//				httpRequest.setRequestRestry(5);
//				httpRequest.setRequestTag("onSuccess");
//				httpRequest.setRequestUrl(token);
//				HttpUtil.newInstance().sendGetRequest(httpRequest);
				List<String> resUrl = new ArrayList<String>();
				resUrl.add(token);
    			HttpDownloadTask httpDownload = new HttpDownloadTask();
    			httpDownload.addDownloadListTask(resUrl, QiNiuTest.this, downloadFolder,1);
    			httpDownload.execu();
    			DownloadManager.newInstance().addHttpDownload(httpDownload);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onFaile(String errMsg, String requestHttpUrl, String requestTag) {
			LogUtils.d(TAG, "onFaile","errMsg:"+errMsg);
		}
	};
	
	
	private void uploadFile() {
		HttpRequestBean httpRequest = new HttpRequestBean();
		httpRequest.setRequestCallback(requestCallback);
		httpRequest.setRequestParm(null);
		httpRequest.setRequestRestry(5);
		httpRequest.setRequestTag("uploadFile");
		httpRequest.setRequestUrl(url);
		HttpUtil.newInstance().sendGetRequest(httpRequest);
	}
	/**
	 * 获取下载
	 */
	private void downloadFile() {
			HttpRequestBean httpRequest = new HttpRequestBean();
			httpRequest.setRequestCallback(requestDownloadCallback);
			try {
				httpRequest.setRequestParm("downType=2&downloadURL="+URLEncoder.encode("二逼视频.f4v", "UTF-8")+"&expires=3600");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			httpRequest.setRequestRestry(5);
			httpRequest.setRequestTag("downloadFile");
			httpRequest.setRequestUrl(downloadUrl);
			HttpUtil.newInstance().sendGetRequest(httpRequest);
	}
	@Override
	public void onSuccess(String httpUrl,int total, int downloadType) {
		LogUtils.d(TAG, "download onSuccess","httpUrl:"+httpUrl);
	}
	@Override
	public void onFail(String httpUrl, String errMsg, int downloadType) {
		LogUtils.d(TAG, "download onFail","httpUrl:"+httpUrl+",errMsg:"+errMsg);
	}
	@Override
	public void updateProgreass(int progress, int totalSize, String httpUrl, int downloadType) {
		LogUtils.d(TAG, "download updateProgreass","httpUrl:"+httpUrl+",progress："+progress+",totalSize:"+totalSize);
	}
	@Override
	public void onStart(int progress, int totalSize, String httpUrl, int downloadType) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void onCancel(String httpUrl, int downloadType) {
		// TODO Auto-generated method stub
		
	}
}
