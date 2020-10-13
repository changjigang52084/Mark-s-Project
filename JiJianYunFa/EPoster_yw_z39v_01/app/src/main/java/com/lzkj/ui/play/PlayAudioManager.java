package com.lzkj.ui.play;

import java.io.File;

import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.os.Handler;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年4月19日 上午11:33:54 
 * @version 1.0 
 * @parameter  播放音频文件的管理类,默认单亲循环播放
 */
public class PlayAudioManager implements OnCompletionListener, OnErrorListener{
	private static final LogTag TAG = LogUtils.getLogTag(PlayAudioManager.class.getSimpleName(), true);
	private MediaPlayer mediaPlayer;
	/**是否在播放**/
	private boolean isPlay;
	/**音频文件播放路径**/
	private Handler mHandler = new Handler();
	public PlayAudioManager() {
		mediaPlayer = new MediaPlayer();
	}
	
	/**
	 * 播放本地音频文件
	 * @param audioPath 本地音频文件路径
	 */
	public void playAudio(Material bgMusicMaterial) {
		String audioPath = FileStore.getInstance()
				.getVideoFilePath(FileStore.getFileName(bgMusicMaterial.getU()));
		Integer playTime = bgMusicMaterial.getD();
		if (null != playTime) {
			mHandler.postAtTime(stopRun, playTime.longValue() * 1000);
		}
		LogUtils.d(TAG, "playAudio", "audioPath : " + audioPath); // /mnt/internal_sd/mallposter/video/null
		if (new File(audioPath).exists()) {
			try {
				mediaPlayer.setDataSource(audioPath);
				mediaPlayer.prepare();
				mediaPlayer.start();
//				mediaPlayer.setLooping(true);
				LogUtils.d(TAG, "playAudio", "play" );
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 
	}
	
	/**
	 * 停止播放
	 */
	public void stopAudio() {
		LogUtils.d(TAG, "stopAudio", "stopAudio" );
		if (mediaPlayer.isPlaying()) {
			isPlay = false;
			mediaPlayer.stop();
			mediaPlayer.reset();
			LogUtils.d(TAG, "stopAudio", "isPlaying" );
		}
	}
	
	/**
	 * 设置是否循环播放 
	 * @param loop true表示循环播放,false表示不循环播放
	 */
	public void setLoop(boolean isLoop) {
		mediaPlayer.setLooping(isLoop);
	}
	
	/**
	 * 播放完循环播放
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		isPlay = false;
		mediaPlayer.release();
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		isPlay = false;
		mediaPlayer.reset();
		return true;
	}
	
	/**
	 * 定时停止播放背景音乐
	 */
	private Runnable stopRun = new Runnable() {
		@Override
		public void run() {
			stopAudio();
		}
	};
	
	public void suspendAudio() {
		LogUtils.d(TAG, "suspendAudio", "suspendAudio" );
		if (mediaPlayer.isPlaying()) {
			isPlay = true;
			mediaPlayer.stop();
			LogUtils.d(TAG, "suspendAudio", "isPlaying" );
		}
	}
	
	public void startAudio() {
		if (isPlay) {
			mediaPlayer.start();
		}
	}
}
