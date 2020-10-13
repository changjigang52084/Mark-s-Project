package com.sunchip.adw.cloudphotoframe.manager;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.jvm.Throws;

/**
 * Created by yingmuliang on 2020/6/26.
 */
public class MusicPlayManger {

    private static MediaPlayer mediaPlayer;

    public int selected = 0;

    public static MusicPlayManger mMusicPlayManger = new MusicPlayManger();

    public MusicPlayManger() {
    }

    public static MusicPlayManger getInstance() {
        if (mMusicPlayManger == null) {
            mMusicPlayManger = new MusicPlayManger();

        }
        return mMusicPlayManger;
    }

    //    //暂停与播放
    public void stopPlay() {
        if (mediaPlayer == null) return;
        else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else mediaPlayer.start();
    }

    //是否在播放
    public boolean IsPlay() {
        if (mediaPlayer == null)
            return false;
        return mediaPlayer.isPlaying();
    }

    //暂停
    public void PausePlay() {
        if (mediaPlayer == null) return;
        else if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
        } else return;
    }

    public void PlayMusic() {
        if (mediaPlayer == null) return;
        else if (mediaPlayer.isPlaying()) {
            return;
        } else mediaPlayer.start();
    }

    //停止音乐操作
    public void Stop() {
        destoryMediaPlayer();
    }

    public List<String> getDataMusic() {
        File file = new File("/data/pre_sel_del");
        File[] files = file.listFiles();
        List<String> Music = new ArrayList<>();
        for (int i = 0; i < files.length; i++) {
            Music.add("/data/pre_sel_del/" + files[i].getName());
        }
        return Music;
    }

    public void play() {
        selected++;
        if (selected > getDataMusic().size() - 1) {
            selected = 0;
        }
        try {
            PlayMusic(getDataMusic().get(selected));
        } catch (IOException e) {
            play();
            Log.e("TAG", "Path =IOException============= "+getDataMusic().get(selected));
        }
    }


    public void PlayMusic(final String path) throws IOException {
        Log.e("TAG", "Path = " + path);
        destoryMediaPlayer();
        initPlayer();
        if (path.contains(CloudFrameApp.getCloudFrameApp().getResources().getString(R.string.off)))
            return;
        if (mediaPlayer != null) {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepareAsync();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    try {
                        Thread.sleep(50);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mp.start();
                }
            });
            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.e("TAG", "音乐播放异常！");
                    play();
                    return false;
                }
            });

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    Log.e("TAG", "播放完成==========================================" + path);
                    //循环播放
                    play();
                }
            });
        }
    }

    public void destoryMediaPlayer() {
        try {
            if (mediaPlayer != null) {
                mediaPlayer.setOnCompletionListener(null);
                mediaPlayer.setOnPreparedListener(null);
                mediaPlayer.reset();
                mediaPlayer.release();
                mediaPlayer = null;
            }
        } catch (Exception e) {
            Log.e("TAG", "destoryMediaPlayer==========================================");
        }
    }

    public void initPlayer() {
        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            }
        } catch (Exception e) {
            Log.e("TAG", "initPlayer==========================================");
        }
    }

}
