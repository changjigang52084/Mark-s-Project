package com.unccr.zclh.dsdps.fragment.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.play.interfaces.IPlayListener;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileStore;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午3:53:51
 * @parameter VideoPlayView 播放视频组件
 */
public class VideoPlayView extends VideoView implements MediaPlayer.OnCompletionListener, MediaPlayer.OnPreparedListener {

    private static final String TAG = "VideoPlayView";

    /**
     * 素材列表
     */
    private List<Material> materialList;
    private ArrayList<String> videoList;
    /**
     * 当前播放的素材
     */
    private Material currentMaterial;
    private String videoPrmPath;
    /**
     * 素材名称
     */
    private String materialName;
    /**
     * 素材下标索引
     */
    private int materiaIndex = -1;
    private int videoIndex = -1;
    /**
     * 素材个数
     */
    private int materialSize = 0;
    private int videoListSize = 0;

    private IPlayListener playListener;
    private VideoPlayHandler mHandler = new VideoPlayHandler();

    public VideoPlayView(Context context) {
        super(context);
        init();
    }

    public VideoPlayView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public VideoPlayView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    private static class VideoPlayHandler extends Handler {
    }

    private void init() {
        setOnCompletionListener(this);
        setOnPreparedListener(this);
    }

    /**
     * 添加播放监听
     *
     * @param playListener
     */
    public void addPlayListener(IPlayListener playListener) {
        this.playListener = playListener;
    }

    /**
     * 设置播放列表
     *
     * @param materialList
     */
    public void setMaterialList(List<Material> materialList) {
        if (null == materialList || materialList.isEmpty()) {
            return;
        }
        //根据素材播放顺序进行排序
        Collections.sort(materialList, new Comparator<Material>() {
            @Override
            public int compare(Material object1, Material object2) {
                return object1.getI().compareTo(object2.getI());
            }
        });
        this.materialList = materialList;
        materialSize = materialList.size();
    }

//    public void setVideoList(ArrayList<String> videoList) {
//        if (null == videoList) {
//            return;
//        }
//        this.videoList = videoList;
//        videoListSize = videoList.size();
//    }

    /**
     * 播放视频
     */
    public void playVideo() {
        Log.d(TAG,"playVideo......");
        mHandler.removeCallbacksAndMessages(null);
        if (null == materialList || materialList.isEmpty()) {
            Log.d(TAG,"playVideo materialList is null......");
            startPlay();
            return;
        }
        getNextVideo();
        playListener.playing(currentMaterial);
        stopPlayback();
        handlerMaterialPlay();
        mHandler.postDelayed(nextMeaterialRun, 500);
    }

    /**
     * 播放视频(电梯）
     */
    public void playPrmVideo() {
        Log.d(TAG,"playPrmVideo......");
        mHandler.removeCallbacksAndMessages(null);
//        nextVideoIndex();
        startPlayVideo();
//        mHandler.postDelayed(switchVideoPlay, getVideoDuration(videoPrmPath));
    }

    private void startPlayVideo() {
        videoPrmPath = videoList.get(videoIndex);
        setVideoPath(videoPrmPath);
        start();
    }

    /*
     * 获取视频时长
     */
    private long getVideoDuration(String videoPrmPath) {
        if (TextUtils.isEmpty(videoPrmPath)) {
            return -1;
        }
        File file = new File(videoPrmPath);
        MediaPlayer mediaPlayer = null;
        try {
            mediaPlayer = MediaPlayer.create(getContext(), Uri.fromFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mediaPlayer.getDuration();
    }

    /**
     * 播放下一个素材
     */
    public void playNextMaterial(long delayedMills) {
        materiaIndex++;
        mHandler.postDelayed(nextMeaterialRun, 500);
        mHandler.postDelayed(switchVideoRun, delayedMills);
    }

    /**
     * 校验素材类型，素材是否合法，播放素材
     */
    private void handlerMaterialPlay() {
        if (checkPlayType()) {
            if (FileStore.getInstance()
                    .checkFileLegal(FileStore.getInstance()
                            .getVideoFilePath(materialName), currentMaterial)) {
                Log.d(TAG,"handlerMaterialPlay 视频素材合法，开始播放。");
                startPlay();
                mHandler.postDelayed(switchVideoRun, currentMaterial.getD() * 1000);
            } else {
                materialList.remove(materiaIndex);
                materialSize = materialList.size();
                playVideo();
            }
        } else {
            mHandler.postDelayed(switchVideoRun, currentMaterial.getD() * 1000);
        }
    }

    /**
     * 验证播放类型，根据不同类型展示不同素材
     */
    private boolean checkPlayType() {
        int materialType = currentMaterial.getT();
        if (Constants.PIC_FRAGMENT == materialType) {
            playListener.setPlayType(materialType);
            Log.d(TAG,"checkPlayType materialType: " + materialType);
            return false;
        } else {
            return true;
        }
    }

    /**
     * 获取下一个视频
     */
    private void getNextVideo() {
        nextMaterialIndex();
        if (null == materialList || materialList.isEmpty()) {
            materialName = "";
            startPlay();
            return;
        }
        currentMaterial = materialList.get(materiaIndex);
        materialName = FileStore.getFileName(currentMaterial.getU());
        Log.d(TAG, "getNextVideo materialName: " + materialName);
    }

    /**
     * 下一个素材索引
     */
    private void nextMaterialIndex() {
        materiaIndex++;
        if (materialSize <= materiaIndex) {
            materiaIndex = 0;
        }
    }
//
//    private void nextVideoIndex() {
//        videoIndex++;
//        if (videoListSize <= videoIndex) {
//            videoIndex = 0;
//        }
//    }
    /**
     * 获取下一个要播放的图片素材名称
     *
     * @return 返回下一个要播放的素材名称 or null,返回null表示下一个播放的素材不是图片
     */
    private void getNextPlayMaterial() {
        try {
            int nextMaterialIndex = materiaIndex + 1;
            if (materialSize <= nextMaterialIndex) {
                nextMaterialIndex = 0;
            }
            if (null == materialList || materialList.isEmpty()) {
                return;
            }
            Material nextMaterial = materialList.get(nextMaterialIndex);
            if (Constants.PIC_FRAGMENT == nextMaterial.getT()) {//判断素材是否为图片素材
                playListener.prmLoadMaterial(nextMaterial);
            }
        }catch (Exception e){
            Log.e(TAG,"getNextPlayMaterial e: " + e.getMessage());
        }
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        String playMaterialName = FileStore.getInstance().getVideoFilePath(materialName);
        Log.d(TAG,"startPlay 运行到这里说明节目已经开始播放了-> " + playMaterialName + " 是要播放的素材名称。");
        setVideoPath(playMaterialName);
        start();
    }

    /***清除*/
    public void clear() {
        stopPlayback();
        mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        Log.d(TAG,"onCompletion......");
        startPlay();
    }

    /**
     * 切换素材的线程
     */
    private Runnable switchVideoRun = new Runnable() {
        @Override
        public void run() {
            playVideo();
        }
    };

//    private Runnable switchVideoPlay = new Runnable() {
//        @Override
//        public void run() {
//            playPrmVideo();
//        }
//    };

    private Runnable nextMeaterialRun = new Runnable() {
        @Override
        public void run() {
            getNextPlayMaterial();
        }
    };

    @Override
    public void onPrepared(MediaPlayer mp) {
        playListener.videoLoadCompletion();
    }
}
