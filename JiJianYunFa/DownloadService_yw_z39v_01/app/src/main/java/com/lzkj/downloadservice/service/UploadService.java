package com.lzkj.downloadservice.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.widget.Toast;

import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.lzkj.aidl.UploadAIDL;
import com.lzkj.downloadservice.R;
import com.lzkj.downloadservice.aliyun.upload.UploadThread;
import com.lzkj.downloadservice.interfaces.UploadStateCallback;
import com.lzkj.downloadservice.upload.UploadManager;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
/**
 * 上传到阿里云日志和截图的服务
 * @author changkai
 *
 */
public class UploadService extends Service implements UploadStateCallback{
	private static final LogTag TAG = LogUtils.getLogTag(UploadService.class.getSimpleName(), true);
	private OSSBucket sampleBucket;
	/**日志云存储根目录*/
	private String log_cloud_folder = "log";
	/**截图云存储根目录**/
	private String screenshot_cloud_folder = "screenshot";
	/**上传日志*/
	private static final int UPLOAD_LOG 	   = 1;
	/**上传截图*/
	private static final int UPLOAD_SCREENSHOT = 0x010008;
	private UploadAIDL.Stub uploadAIDL = new UploadAIDL.Stub() {
		@Override
		public void uploadCommand(int command) throws RemoteException {
			//根据命令上传日志或者截图
			switch (command) {
				case UPLOAD_LOG://上传日志
					uploadLog();
					break;
				case UPLOAD_SCREENSHOT://上传截图
					uploadScreenshot();
					break;
				default:
					break;
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent) {
		return uploadAIDL;
	}
	@Override
	public void onCreate() {
		super.onCreate();
		init();
	}
	private void init(){
        sampleBucket = new OSSBucket("com-kchang");
		sampleBucket.setBucketHostId("oss-cn-shenzhen.aliyuncs.com"); // 可以在这里设置数据中心域名或者cname域名
		sampleBucket.setBucketACL(AccessControlList.PRIVATE);
	}
	/**
	 * 上传日志到服务器
	 */
	private void uploadLog() {
		UploadThread uploadThread = new UploadThread(FileUtil.getInstance()
				.getLogFilePaths(), sampleBucket, this,log_cloud_folder);
		uploadThread.start();
		UploadManager.newInstance().addUploadThread(uploadThread);
	}
	/**
	 * 上传截图
	 */
	private void uploadScreenshot() {
		UploadThread uploadThread = new UploadThread(FileUtil.getInstance().getScreenshotFilePaths(), sampleBucket, this, screenshot_cloud_folder);
		uploadThread.start();
		UploadManager.newInstance().addUploadThread(uploadThread);	
	}
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	@Override
	public void onUploadSuccess(String uploadLocleFile) {
		LogUtils.d(TAG, "onUploadSuccess", "uploadLocleFile:"+uploadLocleFile);
		Toast.makeText(getApplicationContext(), getString(R.string.upload_success)+uploadLocleFile, Toast.LENGTH_SHORT).show();
	}
	@Override
	public void onUploadFail(String uploadLocleFile, String errMsg) {
		Toast.makeText(getApplicationContext(), getString(R.string.upload_fail)+uploadLocleFile, Toast.LENGTH_SHORT).show();
		LogUtils.d(TAG, "onUploadFail", "uploadLocleFile:"+uploadLocleFile);
	}
	@Override
	public void onUploadUpdateProgreass(int progress, long totalSize,
			String uploadLocleFile) {
		LogUtils.d(TAG, "onUploadUpdateProgreass", "uploadLocleFile:"+uploadLocleFile+",progress:"+progress+",totalSize:"+totalSize);
	}
	@Override
	public boolean onUnbind(Intent intent) {
		return true;
	}
}
