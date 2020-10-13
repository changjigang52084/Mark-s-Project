package com.lzkj.downloadservice;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.lzkj.downloadservice.aliyun.upload.UploadThread;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.bean.DownloadInfo;
import com.lzkj.downloadservice.bean.UploadInfo;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.download.DownloadInfoManager;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.download.HttpDownloadTask;
import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;
import com.lzkj.downloadservice.interfaces.UploadStateCallback;
import com.lzkj.downloadservice.upload.UploadManager;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

public class MainActivity extends Activity implements IDownloadStateCallback,UploadStateCallback{
	private static final LogTag TAG = LogUtils.getLogTag(MainActivity.class.getSimpleName(), true);
	/**下载单个文件的进度*/
	private int downloadProgress = 0;
	/**临时保存下载进度，用于计算下载速度*/
	private int tempProgress;
	/**临时保存上次的时间，用于计算下载速度*/
	private long tempTime;
//	private DownloadManager downloadManager;
	private String  resourceURL;
	public OSSBucket sampleBucket;
	private ProgressBar progressBar;
	private TextView fileName_tv,speed_tv,per_tv,download_size_tv;
//	private String downloadFolder = Environment.getDataDirectory().getAbsolutePath()+File.separator+"image";
	private String downloadFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"image";
	private String uploadFolder = Environment.getExternalStorageDirectory().getAbsolutePath();
//	private String uploadFolder = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"UCDownloads";
	private ArrayList<String> list = new ArrayList<String>();
//	private UploadManager uploadManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_main);
//        uploadManager   = new UploadManager();
//        downloadManager = new DownloadManager();
        sampleBucket = new OSSBucket("com-kchang");
		sampleBucket.setBucketHostId("oss-cn-shenzhen.aliyuncs.com"); // 可以在这里设置数据中心域名或者cname域名
		sampleBucket.setBucketACL(AccessControlList.PRIVATE);
//		list.add("image/sogou_pinyin_75a.exe");
		list.add("image/1-Swift概述.mp4");
		list.add("image/灏景机器的资料.rar");
//		list.add("image/e-poster.doc");
        if (savedInstanceState == null) {
//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();
        }
        initView();
    }
    
    private void initView() {
    	progressBar = (ProgressBar)findViewById(R.id.progressBar);
    	fileName_tv = (TextView)findViewById(R.id.file_name_tv);
    	speed_tv = (TextView)findViewById(R.id.speed_tv);
    	per_tv = (TextView)findViewById(R.id.progresss_per_tv);
    	download_size_tv = (TextView)findViewById(R.id.download_size_tv);
    }
    
    public void main_downloadListener(View view) {	//测试下载
    	switch(view.getId()) {
    		case R.id.download: 
    			LogUtils.d(TAG, "main_downloadListener"," download");
    			ArrayList<String> resUrl = new ArrayList<String>();
    			for (String path : list) {
    				OSSFile ossFile = new OSSFile(sampleBucket,path);
        		    String resourceURL = ossFile.getResourceURL(DownloadApp.accessKey, 1800);
        		    resUrl.add(resourceURL);
    			}
    			LogUtils.d(TAG, "main_downloadListener ","download resourceURL size:"+resUrl.size());
    			HttpDownloadTask httpDownload = new HttpDownloadTask();
    			httpDownload.addDownloadListTask(resUrl, this, downloadFolder,1);
    			httpDownload.execu();
    			DownloadManager.newInstance().addHttpDownload(httpDownload);
    			break;
    		case R.id.upload:
    			List<String> uploadFilePathList = new ArrayList<String>();
    			List<UploadInfo> uploadRecordList = SQLiteManager.getInstance().queryUploadTable();
    			if (null != uploadRecordList && uploadRecordList.size() > 0) {
    				LogUtils.d(TAG, "main_downloadListener"," upload uploadRecordList not null");
    				for (UploadInfo uploadInfo : uploadRecordList) {
    					uploadFilePathList.add(uploadInfo.getUploadFilePath());
    				}
    			} else {
    				LogUtils.d(TAG, "main_downloadListener"," upload uploadRecordList isS null");
    				uploadFilePathList.add(uploadFolder+File.separator+"jianli.doc");
    			}
    			UploadThread uploadThread = new UploadThread(uploadFilePathList, sampleBucket, this, "upload");
    			uploadThread.start();
    			UploadManager.newInstance().addUploadThread(uploadThread);
//    			SQLiteManager.getInstance().queryAllTable();
//    			SQLiteManager.getInstance().deleteDownloadTableData();
    			break;
    		case R.id.cancel:
//    			uploadManager.cancelUploadThreads();
    			DownloadManager.newInstance().cancelAllTask();
//    			downloadManager.cancelTaskToUrl(resourceURL);
    			break;
    	}
    }
    @Override
    protected void onDestroy() {
    	DownloadManager.newInstance().cancelAllTask();
    	super.onDestroy();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {}
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
    private int downloadNum;
	@Override
	public void onSuccess(String httpUrl,int total, int downloadType) {
//		downloadNum++;
//		if (3 == downloadNum) {
			downloadProgress = 0;
			downloadNum = 0;
			DownloadInfo downloadInfo = DownloadInfoManager.get().getDownloadInfoToFileName(getFileName(httpUrl));
			if (null == downloadInfo) {
				return;
			}
			LogUtils.d(TAG, "onSuccess","+getFileName(httpUrl)+downloadInfo Percentage:"+downloadInfo.getPercentage());
			updateDownloadInfoSpeed(httpUrl, 0);
			updateView(httpUrl,"onSuccess");
//		}
	}
	@Override
	public void onFail(String httpUrl, String errMsg, int downloadType) {
		downloadProgress = 0;
		LogUtils.d(TAG, "onFail ","errMsg:"+errMsg+",httpUrl:"+getFileName(httpUrl));
		updateDownloadInfoSpeed(httpUrl, 0);
		updateView(httpUrl,"onFail");
	}

//	private int tempProgress = 0;
	@Override
	public void updateProgreass(int progress, int totalSize, String httpUrl, int downloadType) {
		downloadProgress += progress;
//		tempProgress += progress;
		//更新进度
		DownloadInfoManager.get().updateDownloadInfoProgress(FileUtil.getFileName(httpUrl), downloadProgress);
		tempProgress += progress;//临时保存进度
		if (tempTime == 0) {
			tempTime = System.currentTimeMillis();
		} else {
			long currentTime = System.currentTimeMillis();//获取当的时间
			if ((currentTime - tempTime) > 1000) {
//				String tempSpeed = null;
				int speed = tempProgress/1024;//计算下载速度kb/s
//				tempSpeed = speed+"kb/s";
//				if (1024 < speed) {//MB/s
//					tempSpeed = (speed/1024)+"MB/s";
//				}
				updateDownloadInfoSpeed(httpUrl, speed);
//				Log.d(TAG, "updateProgreass speed:" +tempSpeed+",fileName:"+FileUtil.getFileName(httpUrl));
				tempProgress = 0;
				tempTime = 0;
			}
		} 
		updateView(httpUrl,"updateProgreass");
//		Log.d(TAG, "updateProgreass downloadProgress:"+downloadProgress+",totalSize:"+totalSize+",httpUrl:"+getFileName(httpUrl));
	}
	/**
	 * 更新下载速度
	 * @param httpUrl
	 * 			下载地址
	 * @param speed
	 * 			下载速度
	 */
	private void updateDownloadInfoSpeed(String httpUrl,int speed) {
		DownloadInfoManager.get().updateDownloadInfoSpeed(FileUtil.getFileName(httpUrl), speed);
	}
	/**
	 * 更新界面UI
	 * @param httpUrl
	 */
	private void updateView(final String httpUrl,String methodName) {
//		Log.d(TAG, "methodName:"+methodName);
		fileName_tv.post(new Runnable() {
			@Override
			public void run() {
				DownloadInfo downloadInfo = DownloadInfoManager.get().getDownloadInfoToFileName(getFileName(httpUrl));
				if (null == downloadInfo) {
					return;
				}
				DecimalFormat decimalFormat=new DecimalFormat("0.00");
				String tempSpeed = null;
				int speed = downloadInfo.getSpeed();//计算下载速度kb/s
				if (1024 < speed) {//MB/s
					tempSpeed = decimalFormat.format(speed/1024f)+"MB/s";
				} else {
					tempSpeed = speed+"kb/s";
				}
				progressBar.setMax(downloadInfo.getDownloadFileSize());
				progressBar.setProgress(downloadInfo.getDownloadProgress());
				fileName_tv.setText(downloadInfo.getDownloadUrl());
				speed_tv.setText(tempSpeed);
				per_tv.setText(downloadInfo.getPercentage()+"%");
				float fileSize = ((downloadInfo.getDownloadFileSize()/1024f)/1024f);
//				String contentLength = decimalFormat.format(fileSize);
				float downloadSize = ((downloadInfo.getDownloadProgress()/1024f)/1024f);
				download_size_tv.setText(decimalFormat.format(downloadSize)+"M/"+decimalFormat.format(fileSize)+"M");
			}
		});
	}
	
	/**
	 * 根据http地址获取文件名
	 * @param httpUrl
	 * 			下载地址
	 * @return
	 * 		文件名
	 */
	private String getFileName(String httpUrl) {
		return httpUrl.substring(httpUrl.lastIndexOf("/")+1, httpUrl.lastIndexOf("?"));
	}

	@Override
	public void onUploadSuccess(String httpUrl) {
		
	}

	@Override
	public void onUploadFail(String httpUrl, String errMsg) {
		
	}

	@Override
	public void onUploadUpdateProgreass(int progress, long totalSize,
			String httpUrl) {
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
