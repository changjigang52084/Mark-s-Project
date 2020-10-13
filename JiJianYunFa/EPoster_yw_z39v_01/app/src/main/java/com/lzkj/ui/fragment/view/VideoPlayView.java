package com.lzkj.ui.fragment.view;

import android.content.Context;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.lzkj.ui.log.RecordPlayLogHandler;
import com.lzkj.ui.play.interfaces.IPlayListener;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 播放视频的组件
 *
 * @author changkai
 */
public class VideoPlayView extends VideoView implements OnCompletionListener, OnPreparedListener {
    private static final LogTag TAG = LogUtils.getLogTag(VideoPlayView.class.getSimpleName(), true);
    /**
     * 素材列表
     */
    private List<Material> materialList;
    /**
     * 当前播放的素材
     */
    private Material currentMaterial;
    /**
     * 素材名称
     */
    private String materialName;
    /**
     * 素材下标索引
     */
    private int materiaIndex = -1;
    /**
     * 素材个数
     */
    private int materialSize = 0;
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
            LogUtils.w(TAG, "setMaterialList", "MaterialList is null.");
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

    /**
     * 播放视频
     */
    public void playVideo() {
        LogUtils.d(TAG, "playVideo", "playVideo");
        mHandler.removeCallbacksAndMessages(null);
        if (null == materialList || materialList.isEmpty()) {
            startPlay();
            LogUtils.d(TAG, "playVideo", "materialList is empty.");
            return;
        }
        getNextVideo();
        playListener.playing(currentMaterial);
        stopPlayback();
        handlerMaterialPlay();
        mHandler.postDelayed(nextMeaterialRun, 500);
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
                RecordPlayLogHandler.addPlayLog(currentMaterial);
                startPlay();
                mHandler.postDelayed(switchVideoRun, currentMaterial.getD() * 1000);
            } else {
                materialList.remove(materiaIndex);
                materialSize = materialList.size();
                playVideo();
                LogUtils.d(TAG, "playVideo", "Play video file name is not legal: " + materialName);
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
            RecordPlayLogHandler.addPlayLog(currentMaterial);
            playListener.setPlayType(materialType);
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
        Log.v("cjg", "materialName:" + materialName);
    }

    /**
     * 下一个素材索引
     */
    private void nextMaterialIndex() {
        materiaIndex++;
        if (materialSize <= materiaIndex) {
            materiaIndex = 0;
        }
        LogUtils.d(TAG, "nextMaterial", "Video material materiaIndex：" + materiaIndex);
    }

    /**
     * 获取下一个要播放的图片素材名称
     *
     * @return 返回下一个要播放的素材名称 or null,返回null表示下一个播放的素材不是图片
     */
    private void getNextPlayMaterial() {
        int nextMaterialIndex = materiaIndex + 1;
        if (materialSize <= nextMaterialIndex) {
            nextMaterialIndex = 0;
        }
        if (null == materialList || materialList.isEmpty()) {
            LogUtils.w(TAG, "setMaterialList", "MaterialList is null.");
            return;
        }
        Material nextMaterial = materialList.get(nextMaterialIndex);
        if (Constants.PIC_FRAGMENT == nextMaterial.getT()) {//判断素材是否为图片素材
            playListener.prmLoadMaterial(nextMaterial);
        }
    }

    /**
     * 开始播放
     */
    private void startPlay() {
        setVideoPath(FileStore.getInstance().getVideoFilePath(materialName));
        start();
    }

    /***清除*/
    public void clear() {
        RecordPlayLogHandler.writPlayLog(currentMaterial);
        stopPlayback();
        mHandler.removeCallbacksAndMessages(null);
        LogUtils.d(TAG, "switchImgRun", "onDestry");
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        LogUtils.d(TAG, "onCompletion", "materialName" + materialName);
        startPlay();
    }

    /**
     * 切换素材的线程
     */
    private Runnable switchVideoRun = new Runnable() {
        @Override
        public void run() {
            RecordPlayLogHandler.writPlayLog(currentMaterial);
            playVideo();
        }
    };

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
