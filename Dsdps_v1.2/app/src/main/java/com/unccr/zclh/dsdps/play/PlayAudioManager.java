package com.unccr.zclh.dsdps.play;

import android.media.MediaPlayer;
import android.os.Handler;

import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.util.FileStore;

import java.io.File;

public class PlayAudioManager implements MediaPlayer.OnCompletionListener, MediaPlayer.OnErrorListener {
    private MediaPlayer mediaPlayer;
    /**
     * 是否在播放
     **/
    private boolean isPlay;
    /**
     * 音频文件播放路径
     **/
    private Handler mHandler = new Handler();

    public PlayAudioManager() {
        mediaPlayer = new MediaPlayer();
    }
    /**
     * 播放本地音频文件
     *
     * @param bgMusicMaterial 本地音频文件路径
     */
    public void playAudio(Material bgMusicMaterial) {
        String audioPath = FileStore.getInstance()
                .getVideoFilePath(FileStore.getFileName(bgMusicMaterial.getU()));
        Integer playTime = bgMusicMaterial.getD();
        if (null != playTime) {
            mHandler.postAtTime(stopRun, playTime.longValue() * 1000);
        }
        if (new File(audioPath).exists()) {
            try {
                mediaPlayer.setDataSource(audioPath);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 停止播放
     */
    public void stopAudio() {
        if (mediaPlayer.isPlaying()) {
            isPlay = false;
            mediaPlayer.stop();
            mediaPlayer.reset();
        }
    }

    /**
     * 设置是否循环播放
     *
     * @param isLoop true表示循环播放,false表示不循环播放
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
        if (mediaPlayer.isPlaying()) {
            isPlay = true;
            mediaPlayer.stop();
        }
    }

    public void startAudio() {
        if (isPlay) {
            mediaPlayer.start();
        }
    }
}