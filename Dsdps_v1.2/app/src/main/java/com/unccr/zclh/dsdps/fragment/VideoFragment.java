package com.unccr.zclh.dsdps.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.fragment.view.VideoPlayView;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.play.ProgramPlayManager;
import com.unccr.zclh.dsdps.play.interfaces.IPlayListener;
import com.unccr.zclh.dsdps.util.BitmapUtil;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.Helper;
import com.unccr.zclh.dsdps.util.ListUtil;
import com.unccr.zclh.dsdps.util.StringUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月09日 下午3:13:28
 * @parameter VideoPicFragment 播放视频和图片
 */
@SuppressLint("ValidFragment")
public class VideoFragment extends BaseFragment implements OnErrorListener, IPlayListener, OnPreparedListener {

    private static final String TAG = "VideoFragment";
    private static final int DELAYED = 5000;
    private View rootView;
    private VideoPlayView videoView;
    private List<Material> materialList;
    private ImageView notVideoImageView;
    /*
     *预加载图片
     */
    private ImageView preImageView;
    private Material currentPlayMaterial;
    private boolean isPreLoadView;
    private BaseHandler mBaseHandler;

    @SuppressLint("ValidFragment")
    public VideoFragment(Area area, boolean isPreLoadView) {
        super(area);
        mBaseHandler = new BaseHandler(this);
        materialList = area.getMas();
        this.isPreLoadView = isPreLoadView;
        removeNotMaterial(materialList);
    }

    public VideoFragment() {
        super(null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == rootView) {
            rootView = inflater.inflate(R.layout.fragment_video, container, false);
            videoView = (VideoPlayView) rootView.findViewById(R.id.fragment_video_screen);
            videoView.setOnErrorListener(this);
            videoView.addPlayListener(this);
            notVideoImageView = (ImageView) rootView.findViewById(R.id.not_video_img);
            preImageView = (ImageView) rootView.findViewById(R.id.pre_img);
            notVideoImageView.setVisibility(View.GONE);
            preImageView.setVisibility(View.GONE);
        }
        videoView.setVisibility(View.GONE);
        return rootView;
    }

    /**
     * 删除素材列表中空素材的素材
     *
     * @param materialList 素材列表对象
     */
    private void removeNotMaterial(List<Material> materialList) {
        if (ListUtil.isEmpty(materialList)) {
            return;
        }
        List<Material> removeMaterialList = new ArrayList<Material>();
        for (Material material : materialList) {
            if (null == material) {
                removeMaterialList.add(material);
            }
        }
        materialList.removeAll(removeMaterialList);
    }

    @Override
    protected void startLoadingTask() {
        Log.d(TAG, "startLoadingTask......");
        if (ListUtil.isEmpty(materialList)) {
            return;
        }
        videoView.setMaterialList(materialList);
        showVideo();
    }

    @Override
    public void resumePlayback() {
        Log.d(TAG, "resumePlayback......");
    }

    private void showVideo() {
        if (!isPreLoadView) {
            Log.d(TAG, "showVideo isPreLoadView: " + isPreLoadView);
            videoView.playVideo();
        } else {
            if (ListUtil.isNotEmpty(materialList)) {
                Material material = materialList.get(0);
                if (material.getT() == Constants.PIC_FRAGMENT) {
                    Log.d(TAG, "showVideo material type Constants.PIC_FRAGMENT: " + material.getT());
                    videoView.setVisibility(View.GONE);
                    playing(material);
                    setPlayType(Constants.PIC_FRAGMENT);
                } else {
                    Log.d(TAG, "showVideo material type Constants.VIDEO_FRAGMENT: " + material.getT());
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoPath(FileStore.getInstance().getVideoFilePath(FileStore.getFileName(material.getU())));
                }
            }
        }
    }

    @Override
    public void startPlayPrograme() {
        if (ListUtil.isNotEmpty(materialList) && isPreLoadView && null != videoView) {
            Log.d(TAG, "startPlayProgram......");
            isPreLoadView = false;
            videoView.playVideo();
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        notVideoImageView.setImageResource(R.drawable.zclh_logo);
        notVideoImageView.setVisibility(View.VISIBLE);
        preImageView.setVisibility(View.INVISIBLE);
        notVideoImageView.setTag(null);
        preImageView.setTag(null);
        mBaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Helper.handlerMaterialError(currentPlayMaterial, ProgramPlayManager.getInstance().getCurrentProgram());
                playNextMaterial();
            }
        }, DELAYED);
        //切换到下一个素材，首先判断是否有下一个素材，如果没有则切换节目,如果没有下一个节目则切换
        return true;
    }

    @Override
    public void playing(Material material) {
        if (null != material) {
            int materialType = material.getT();
            this.currentPlayMaterial = material;
            if (Constants.VIDEO_FRAGMENT == materialType) {
                Log.d(TAG, "playing materialType: " + materialType);
                videoView.setVisibility(View.VISIBLE);
                notVideoImageView.setVisibility(View.INVISIBLE);
                preImageView.setVisibility(View.INVISIBLE);
                notVideoImageView.setTag(null);
                preImageView.setTag(null);
            }
        }
    }

    /**
     * 设置播放类型
     */
    @Override
    public void setPlayType(int materialType) {
        Log.d(TAG,"setPlayType materialType: " + materialType);
        switch (materialType) {
            case Constants.PIC_FRAGMENT:
                videoView.setVisibility(View.GONE);
                //如果是图片就切到图片展示界面
                if (!isPreLoadPic()) {
                    preLoadWhichImageView(FileStore.getFileName(currentPlayMaterial.getU()), false);
                } else {
                    //直接显示view
                    showWhichImageView(FileStore.getFileName(currentPlayMaterial.getU()));
                }
                break;
//		case Constant.WEIBO_FRAGMENT:
//			baseFragment = new FragmentText(areaBean);
//			break;
            default:
                videoView.setVisibility(View.VISIBLE);
                break;
        }
    }

    /**
     * 是否预加载了图片
     *
     * @return 返回值true表示已加载, false未加载
     */
    private boolean isPreLoadPic() {
        if (null == notVideoImageView.getTag(notVideoImageView.getId())
                && null == preImageView.getTag(preImageView.getId())) {
            return false;
        }
        return true;
    }

    /**
     * 根据图片名称显示哪一个已经加载了图片的view
     *
     * @param picName 图片名称
     */
    private void showWhichImageView(String picName) {
        String picPath = FileStore.getInstance().getImageFilePath(picName);
        String notVideoImgTag = (String) notVideoImageView.getTag(notVideoImageView.getId());
        String preImageViewTag = (String) preImageView.getTag(preImageView.getId());
        if (!StringUtil.isNullStr(notVideoImgTag) && picPath.equals(notVideoImgTag)) {
            showNotVideoImageView("showWhichImageView", true);
        } else if (!StringUtil.isNullStr(preImageViewTag) && picPath.equals(preImageViewTag)) {
            showNotVideoImageView("showWhichImageView", false);
        }
    }

    /**
     * 设置图片预加载到哪个image view上面
     *
     * @param picName      图片名称
     * @param isPreLoadPic 是否为预加载，true表示是，false表示不是 isPreLoadPic = true
     */
    private void preLoadWhichImageView(String picName, boolean isPreLoadPic) {
        // mnt/internal_sd/mallposter/pic/xxx.jpg
        String picPath = FileStore.getInstance().getImageFilePath(picName);
        if (!isPreLoadPic() && !isPreLoadPic) {
            showNotVideoImageView("preLoadWhichImageView", true);
            loadPicToImageView(notVideoImageView, picPath);
            return;
        }
        if (notVideoImageView.getVisibility() == View.VISIBLE) {
            loadPicToImageView(preImageView, picPath);
        } else {
            loadPicToImageView(notVideoImageView, picPath);
        }
    }

    /**
     * 显示notVideoImageView
     *
     * @param tagMethoed            那个方法调用的
     * @param showNotVideoImageView 是否显示notVideoImageView,true为是，false不显示
     */
    private void showNotVideoImageView(String tagMethoed, boolean showNotVideoImageView) {
        if (showNotVideoImageView) {
            notVideoImageView.setVisibility(View.VISIBLE);
            preImageView.setVisibility(View.INVISIBLE);
        } else {
            preImageView.setVisibility(View.VISIBLE);
            notVideoImageView.setVisibility(View.INVISIBLE);
        }
    }

    private void startAnims(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha",
                0f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX).setDuration(500).start();
    }

    private void startAnim(View target) {
        AlphaAnimation mShowAction = new AlphaAnimation(0, 1);
        mShowAction.setInterpolator(new AccelerateInterpolator());
        mShowAction.setDuration(500);
        target.setAnimation(mShowAction);
    }

    private void hideAnim(View target) {
        TranslateAnimation hideAnim = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
                0.0f, Animation.RELATIVE_TO_SELF, 1.0f);
        hideAnim.setInterpolator(new AccelerateInterpolator());
        hideAnim.setStartOffset(500);
        hideAnim.setDuration(500);
        target.setAnimation(hideAnim);
    }

    /**
     * 播放下一个素材
     */
    private void playNextMaterial() {
        if (ListUtil.isNotEmpty(materialList)) {
            int size = materialList.size();
            if (size > 1) {
                Log.d(TAG, "playNextMaterial......size: " + size);
                videoView.playVideo();
            }
        }
    }

    /**
     * 加载图片
     *
     * @param imageView imageview对象
     * @param picPath   图片路径
     */
    private void loadPicToImageView(ImageView imageView, String picPath) {
        if (!BitmapUtil.loadPic(imageView, picPath, area, this)) {
            imageView.setTag(imageView.getId(), "");
            imageView.setImageResource(R.drawable.no_photo);
        }
    }


    /**
     * 预加载图片
     */
    @Override
    public void prmLoadMaterial(Material material) {
        preLoadWhichImageView(FileStore.getFileName(material.getU()), true);
    }

    @Override
    public void pausePlayback() {

    }

    @Override
    public void stopPlayback() {
        videoView.pause();
        videoView.clear();
        notVideoImageView.setImageBitmap(null);
        preImageView.setImageBitmap(null);
        if (mBaseHandler != null) {
            mBaseHandler.recycle();
        }
    }

    @Override
    public void videoLoadCompletion() {
        if (isPreLoadView) {
            //视频加载完成以后 暂停开启以后播放
            videoView.start();
            videoView.pause();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
            mp.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        videoView.setBackgroundColor(Color.TRANSPARENT);
                    }
                    return false;
                }
            });
        }
    }
}
