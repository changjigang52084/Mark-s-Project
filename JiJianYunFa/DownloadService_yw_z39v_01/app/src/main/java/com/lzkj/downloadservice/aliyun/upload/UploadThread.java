package com.lzkj.downloadservice.aliyun.upload;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.aliyun.mbaas.oss.callback.SaveCallback;
import com.aliyun.mbaas.oss.model.OSSException;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.aliyun.mbaas.oss.storage.TaskHandler;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.interfaces.UploadStateCallback;
import com.lzkj.downloadservice.util.FileUtil;

/**
 * 断点上传的线程
 * 
 * @author changkai
 * 
 */
public class UploadThread extends Thread {
	private static final String TAG_LOG = UploadThread.class.getSimpleName();
	private static final long oneSeconds = 1000;
	/** OSSBucket类 */
	private OSSBucket ossBucket;
	/** 上传文件的集合 **/
	private List<String> uploadPathList;
	/** 文件上传的路径 */
	// private String uploadFilePath;
	/** 上传文件的名称 */
	private String uploadFileName;
	/** 上传文件的类型 */
	private String contentType;
	/** 异步上传任务的类 */
	private TaskHandler tk;
	/**监听上传的状态*/
	private UploadStateCallback uploadStateCallback;
	/**阿里云存储的object文件夹名称*/
	private String objectKeyRoot;
	/**最多重试的次数*/
	private int retrySize = 5;
	/**重试次数*/
	private int index;
	/**是否取消上传*/
	private boolean ifCancel = false;
	/**是否上传成功*/
	private boolean ifUploadSuccess = false;
	
	public UploadThread(List<String> uploadFilePathList, OSSBucket ossBucket,UploadStateCallback uploadStateCallback,String objectKeyRoot) {
		this.uploadStateCallback = uploadStateCallback;
		this.uploadPathList = uploadFilePathList;
		this.ossBucket = ossBucket;
		this.objectKeyRoot = objectKeyRoot;
	}
	
	public UploadThread(String uploadFilePath, OSSBucket ossBucket,UploadStateCallback uploadStateCallback,String objectKeyRoot) {
		this.uploadStateCallback = uploadStateCallback;
		this.uploadPathList = new ArrayList<String>();
		this.uploadPathList.add(uploadFilePath);
		this.ossBucket = ossBucket;
		this.objectKeyRoot = objectKeyRoot;
	}
	@Override
	public void run() {
		if (null == uploadPathList || null == ossBucket) {
			return;
		}
		for (String uploadFilePath : uploadPathList) {
			if (ifCancel) {
				return;
			}
			upload(uploadFilePath);
			sleepThread();
			if (index >= retrySize) {
				tk.cancel();
			}
		}
	}
	/**
	 * 上传文件
	 * @param uploadFilePath
	 */
	private  void upload(final String uploadFilePath) {
		index = 0;
		ifUploadSuccess = false;
		String uploadFileName = FileUtil.getFileNameToFilePath(uploadFilePath);
		String suffix = FileUtil.getSuffix(uploadFileName);
		String contentType = FileUtil.getInstance().getContentTypeToSuffix(suffix);
		OSSFile ossFile = new OSSFile(ossBucket,objectKeyRoot+"/"+uploadFileName);
		ossFile.setUploadFilePath(uploadFilePath, contentType); // 指定要上传的文件，和文件内容的类型
		ossFile.enableUploadCheckMd5sum();// 开启md5校验
		Log.e(TAG_LOG, "upload uploadFilePath "+uploadFilePath);
		SQLiteManager.getInstance().insertUploadTable(uploadFilePath, objectKeyRoot);//添加上传记录
		tk = ossFile.ResumableUploadInBackground(new SaveCallback() {
			@Override
			public void onSuccess(String objectKey) {
				ifUploadSuccess = true;
				uploadStateCallback.onUploadSuccess(uploadFilePath);
				//删除上传记录
				SQLiteManager.getInstance().deleteUploadRecord(uploadFilePath);
				Log.e(TAG_LOG, "onSuccess objectKey "+objectKey);
			}
			@Override
			public void onFailure(String objectKey,
					OSSException ossException) {
				index++;
				if (ifCancel || index >= retrySize) {
					index = retrySize;
				}
				Log.e(TAG_LOG, "onFailure objectKey "+objectKey+"index:"+index+",ifCancel:"+ifCancel);
				uploadStateCallback.onUploadFail(uploadFilePath, ossException.getMessage());
			}
			@Override
			public void onProgress(String objectKey, int byteCount,
					int totalSize) {
				Log.e(TAG_LOG, "onProgress objectKey "+objectKey+",byteCount:"+byteCount+",totalSize:"+totalSize);
				uploadStateCallback.onUploadUpdateProgreass(byteCount, totalSize, objectKey);
			}
		});
	}
	/**
	 * 休眠
	 */
	private void sleepThread() {
		while (!ifUploadSuccess && index < retrySize) {
			try {
				Thread.sleep(oneSeconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	/**
	 * 取消任务
	 */
	public void cancelTast() {
		ifCancel = true;
		if (null == tk) {
			return;
		}
		tk.cancel();
	}

}
