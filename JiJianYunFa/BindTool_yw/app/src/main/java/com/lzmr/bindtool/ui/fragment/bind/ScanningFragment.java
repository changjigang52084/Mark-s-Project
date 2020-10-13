package com.lzmr.bindtool.ui.fragment.bind;

import java.io.IOException;
import java.util.Vector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.camera.CameraManager;
import com.google.zxing.decoding.CaptureActivityHandler;
import com.google.zxing.decoding.InactivityTimer;
import com.google.zxing.view.ViewfinderView;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.ui.fragment.BaseFragment;
import com.lzmr.bindtool.ui.fragment.LoginFragment;
import com.lzmr.bindtool.ui.fragment.MenuFragment;
import com.lzmr.bindtool.ui.fragment.devices.DevicesManagerFragment;
import com.lzmr.bindtool.util.LogoutUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年8月3日 下午7:34:24 
 * @version 1.0 
 * @parameter  扫描界面的fragment
 */
@SuppressLint("ValidFragment")
public class ScanningFragment extends BaseFragment implements Callback ,
	View.OnClickListener{
	private static final LogTag TAG = LogUtils.getLogTag(ScanningFragment.class.getSimpleName(), true);
	private CaptureActivityHandler handler;
	private ViewfinderView viewfinderView;
	private boolean hasSurface;
	private Vector<BarcodeFormat> decodeFormats;
	private String characterSet;
	private InactivityTimer inactivityTimer;
	private MediaPlayer mediaPlayer;
	private boolean playBeep;
	private static final float BEEP_VOLUME = 0.50f;
	private boolean vibrate;
	/**
	 * 返回首页
	 */
	private RelativeLayout homePage;
	private Handler mHandler = new Handler();


	/**
	 * 缓存绑定次数，用于连续绑定区分终端名
	 */
	private int numberOfBind = 0;
	/**
	 * 缓存终端名，用于连续绑定
	 */
	private String deviceName = null;
	
	public ScanningFragment(ControlFragmentListener switchFragmentListener) {
		super(switchFragmentListener);
	}
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Bundle bundle = getArguments();
		if(null != bundle){
			deviceName = bundle.getString("deviceName");
			numberOfBind = bundle.getInt("numberOfBind",0);
		}
		view = inflater.inflate(R.layout.fragment_scanning, container, false);
		// 初始化 CameraManager
		CameraManager.init(BindToolApp.getApplication());
		viewfinderView = (ViewfinderView) view.findViewById(R.id.viewfinder_view);
		hasSurface = false;
		inactivityTimer = new InactivityTimer(getActivity());
		homePage = (RelativeLayout) view.findViewById(R.id.rl_home_page);
		homePage.setOnClickListener(this);
		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();
		
		if (hasSurface)
		{
			initCamera(surfaceHolder);
		}
		else
		{
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		decodeFormats = null;
		characterSet = null;

		playBeep = true;
		AudioManager audioService = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);
		if (audioService.getRingerMode() != AudioManager.RINGER_MODE_NORMAL)
		{
			playBeep = false;
		}
		initBeepSound();
		vibrate = true;
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		if (handler != null)
		{
			handler.quitSynchronously();
			handler = null;
		}
		CameraManager.get().closeDriver();
	}
	
	
	@Override
	public void onDestroyView() {
		LogUtils.d(TAG, "onDestroyView", "onDestroyView");
		inactivityTimer.shutdown();
		super.onDestroyView();
	}


	
	private void initCamera(SurfaceHolder surfaceHolder)
	{
		try
		{
			CameraManager.get().openDriver(surfaceHolder);
		}
		catch (IOException ioe)
		{
			return;
		}
		catch (RuntimeException e)
		{
			return;
		}
		if (handler == null)
		{
			handler = new CaptureActivityHandler(this, decodeFormats, characterSet);
		}
	}

	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		
	}
	
	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		
	}

	
	public ViewfinderView getViewfinderView()
	{
		return viewfinderView;
	}

	public Handler getHandler()
	{
		return handler;
	}

	public void drawViewfinder()
	{
		viewfinderView.drawViewfinder();

	}
	
	
	/**
	 * 处理扫描结果
	 */
	public void handleDecode(Result obj, Bitmap barcode) {
		inactivityTimer.onActivity();
		playBeepSoundAndVibrate();
		String result = obj.getText();
		if(result == null){
			LogUtils.e(TAG, "handleDecode", "scanning result is null");
			restartScanning();
			return;
		}
		LogUtils.d(TAG, "handleDecode", "qrCode result:"+result);
		reportResult(result);
	}
	
	

	/** 
	* @Title: restartScanning 
	* @Description: TODO(重新扫描) 
	* @Param     设定文件 
	* @return void    返回类型 
	*/ 
	private void restartScanning() {
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
				SurfaceHolder surfaceHolder = surfaceView.getHolder();
				initCamera(surfaceHolder);
				if (handler != null)
					handler.restartPreviewAndDecode();
			}
		}, 2000);
		
	}
	/** 
	* @Title: reportResult 
	* @Description: TODO(汇报结果，跳转界面) 
	* @Param     设定文件 
	* @return void    返回类型 
	*/ 
	private void reportResult(String result) {
		Bundle bundle = new Bundle();
		bundle.putString("scanningResult", result);
		if (!StringUtils.isEmpty(deviceName)) {
			bundle.putString("deviceName",deviceName);
			bundle.putInt("numberOfBind",numberOfBind);
		}
		switchBind(bundle,true);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.rl_home_page:
			popBackStackToFragment(MenuFragment.class.getSimpleName(),0);
			break;
		default:
			break;
		}
	}

	private void initBeepSound()
	{
		if (playBeep && mediaPlayer == null)
		{
			// The volume on STREAM_SYSTEM is not adjustable, and users found it
			// too loud,
			// so we now play on the music stream.
			getActivity().setVolumeControlStream(AudioManager.STREAM_MUSIC);
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mediaPlayer.setOnCompletionListener(beepListener);

			AssetFileDescriptor file = getResources().openRawResourceFd(R.raw.beep);
			try
			{
				mediaPlayer.setDataSource(file.getFileDescriptor(), file.getStartOffset(), file.getLength());
				file.close();
				mediaPlayer.setVolume(BEEP_VOLUME, BEEP_VOLUME);
				mediaPlayer.prepare();
			}
			catch (IOException e)
			{
				mediaPlayer = null;
			}
		}
	}
	


	private static final long VIBRATE_DURATION = 200L;

	private void playBeepSoundAndVibrate() {
		if (playBeep && mediaPlayer != null) {
			mediaPlayer.start();
		}
		if (vibrate) {
			Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
			vibrator.vibrate(VIBRATE_DURATION);
		}
	}

	/**
	 * When the beep has finished playing, rewind to queue up another one.
	 */
	private final OnCompletionListener beepListener = new OnCompletionListener() {
		public void onCompletion(MediaPlayer mediaPlayer) {
			mediaPlayer.seekTo(0);
		}
	};

	
}
