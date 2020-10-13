package com.lzkj.downloadservice.service;


import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.lzkj.downloadservice.qiniu.impl.QiNiuUploadStateCallback;
import com.lzkj.downloadservice.qiniu.upload.UploadProgressHandler;
import com.lzkj.downloadservice.qiniu.upload.UploadTask;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.FileConstant;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.FileUtil.LogType;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.guardservice.manager.GuardManager;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月18日 上午11:28:21 
 * @version 1.0 
 * @parameter  定时上传日志到服务器的service
 */
public class TimerUploadLogService extends Service implements QiNiuUploadStateCallback{
	private static final LogTag TAG = LogUtils.getLogTag(TimerUploadLogService.class.getSimpleName(), true);
	private static final int ONGOING_NOTIFICATION = 123;
	private Timer timer;
	private boolean flag;
	private long firstTimer;
	@Override
	public void onCreate() {
		super.onCreate();
		initPrm();
		LogUtils.d(TAG, "onCreate", "onCreate ");
		FileUtil.getInstance()
		.writeUploadLog(TimerUploadLogService.class.getSimpleName()+"_onCreate");
	}
	/**
	 * 初始化
	 */
	private void initPrm() {
		timer = new Timer();
		firstTimer = getNextHour();
		startTimer();
		Notification notification = new Notification();
		startForeground(ONGOING_NOTIFICATION, notification);
	}
	
	//首先得到当前时间和获取下一个时间整点
	private long getNextHour() {
		flag = true;
		SimpleDateFormat dateFormat = new SimpleDateFormat("mm");
		int currentHour = Integer.parseInt(dateFormat.format(new Date()));
		int hourInt = 60;
		int difference = 5;
		int temp = hourInt - currentHour;//得到当前时间到下一个整点还有多少分钟
		int redom = (int) Math.round(Math.random() * difference);
		long timer = (redom + temp + difference) * hourInt * 1000;
		return timer;
	}
	
	/**
	 * 启动定时器
	 */
	private void startTimer() {
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				flag = false;
				startTimer();
				UploadTask uploadTask = new UploadTask(TimerUploadLogService.this);
				uploadTask.executeUpload(FileUtil.getInstance().getLogFolder() + File.separator 
						+ FileUtil.getLogFileName(LogType.PLAY_LOG), FileConstant.BUCKET_TYPE_LOG);
				UploadProgressHandler uploadProgressHandler = UploadProgressHandler.newInstance();
				uploadProgressHandler.addUploadTask(uploadTask);
				FileUtil.getInstance()
				.writeUploadLog(TimerUploadLogService.class.getSimpleName() + "_startTimer");
			}
		}, flag ? firstTimer : Constant.UPLOAD_LOG_TIME);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		return super.onStartCommand(intent, flags, startId);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	/**
	 * 取消定时器
	 */
	private void cancelTimer() {
		timer.cancel();
	}

	@Override
	public void onUploadSuccess(String uploadLocleFile) {
		//上传完成
		LogUtils.d(TAG, "onUploadSuccess", "uploadLocleFile : " + uploadLocleFile);
		FileUtil.getInstance()
		.writeUploadLog(TimerUploadLogService.class.getSimpleName()+"_onUploadSuccess uploadLocleFile : " + uploadLocleFile);
	}

	@Override
	public void onUploadFail(String uploadLocleFile, String errMsg) {
		//上传失败
		LogUtils.d(TAG, "onUploadFail", "uploadLocleFile : " + uploadLocleFile + ",errMsg:" + errMsg);
		FileUtil.getInstance()
		.writeUploadLog(TimerUploadLogService.class.getSimpleName()+"_onUploadFail uploadLocleFile : " + uploadLocleFile);
	}

	@Override
	public void onUploadUpdateProgreass(int progress, String uploadLocleFile) {
		//上传进度
	}
}
