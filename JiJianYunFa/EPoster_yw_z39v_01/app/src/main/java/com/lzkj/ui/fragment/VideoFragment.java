package com.lzkj.ui.fragment;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnErrorListener;
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

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.lzkj.ui.R;
import com.lzkj.ui.fragment.view.VideoPlayView;
import com.lzkj.ui.play.ProgramPlayManager;
import com.lzkj.ui.play.interfaces.IPlayListener;
import com.lzkj.ui.util.BitmapUtil;
import com.lzkj.ui.util.ConfigSettings;
import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.FileStore;
import com.lzkj.ui.util.Helper;
//import com.lzkj.ui.util.HttpUrl;
import com.lzkj.ui.util.ListUitl;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * @author kchang changkai@lz-mr.com
 * @Description:播放fragment
 * @time:2016年3月23日 下午3:48:14
 */
public class VideoFragment extends BaseFragment implements OnErrorListener, IPlayListener, MediaPlayer.OnPreparedListener {
    private static final LogTag TAG = LogUtils.getLogTag(VideoFragment.class.getSimpleName(), true);
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

    private int countStart;

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
        if (ListUitl.isEmpty(materialList)) {
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
        LogUtils.d(TAG, "startLoadingTask", "startLoadingTask");
        if (ListUitl.isEmpty(materialList)) {
            return;
        }
//        Log.v("cjg", "ConfigSettings.COUNT_URL_01:" + ConfigSettings.COUNT_URL_01 + "\r\n" + "countStart:" + countStart);
//        if (ConfigSettings.COUNT_URL_01 != null && !"".equals(ConfigSettings.COUNT_URL_01)) {
//            HttpUrl.playProgramByGet(ConfigSettings.COUNT_URL_01);
//            countStart++;
//        }
        videoView.setMaterialList(materialList);
        showVideo();
    }

    @Override
    public void resumePlayback() {
//		LogUtils.d(TAG, "resumePlayback", "getX : " + rootView.getX() + ", getY :" + rootView.getY());
    }

    private void showVideo() {
//		LogUtils.d(TAG, "showVideo", "showVideo");
        if (!isPreLoadView) {
            videoView.playVideo();
        } else {
            if (ListUitl.isNotEmpty(materialList)) {
                Material material = materialList.get(0);
                if (material.getT() == Constants.PIC_FRAGMENT) {
                    videoView.setVisibility(View.GONE);
                    playing(material);
                    setPlayType(Constants.PIC_FRAGMENT);
                } else {
                    videoView.setVisibility(View.VISIBLE);
                    videoView.setVideoPath(FileStore
                            .getInstance().getVideoFilePath(FileStore.getFileName(material.getU())));
//					videoView.setVideoPath(FileStore.getVideoFilePath(material.getN()));
                }
            } else {
                LogUtils.d(TAG, "resumePlayback", "materialList is null or materialList size is zero");
            }
        }
    }

    @Override
    public void startPlayPrograme() {
        if (ListUitl.isNotEmpty(materialList) && isPreLoadView && null != videoView) {
            isPreLoadView = false;
            videoView.playVideo();
//			if (null != materialList && !materialList.isEmpty()) {
//				Material material = materialList.get(0);
//				if (Constant.PIC_FRAGMENT == material.getT()) {
//					videoView.playVideo();
//				} else {
//					videoView.playVideo();
////					videoView.start();
////					videoView.playNextMaterial(material.getD() * 1000);
//				}
//			} else {
//				videoView.playVideo();
//				LogUtils.d(TAG, "startPlayPrograme", "materialList is null or materialList size is zero");
//			}
        }
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        notVideoImageView.setImageResource(R.drawable.bg_b);
//        notVideoImageView.setImageResource(R.drawable.default_no_content);
//		notVideoImageView.setImageURI(Uri.parse("res://com.lzkj.ui/" + R.drawable.not_video));
        notVideoImageView.setVisibility(View.VISIBLE);
        preImageView.setVisibility(View.INVISIBLE);
        notVideoImageView.setTag(null);
        preImageView.setTag(null);
        LogUtils.d(TAG, "onError", "MediaPlayer err start what: " + what);
        mBaseHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LogUtils.d(TAG, "onError", "MediaPlayer err");
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
            this.currentPlayMaterial = material;
            if (Constants.VIDEO_FRAGMENT == material.getT()) {
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
            LogUtils.d(TAG, "isPrmPic", "all tag is null ");
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
            LogUtils.d(TAG, "showWhichImageView", "preImageView ");
            showNotVideoImageView("showWhichImageView", false);
        } else {
            LogUtils.e(TAG, "showWhichImageView", "image view not load pic... ");
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
        LogUtils.d(TAG, "showNotVideoImageView", tagMethoed);
//		startAnims(notVideoImageView);
//		startAnims(preImageView);
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
//	        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,  
//	                0, 1f);  
//	        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,  
//	                0, 1f);  
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX).setDuration(500).start();
    }

    private void startAnim(View target) {
        AlphaAnimation mShowAction = new AlphaAnimation(0, 1);
//		TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,     
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,     
//                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f); 
        mShowAction.setInterpolator(new AccelerateInterpolator());
        mShowAction.setDuration(500);
        target.setAnimation(mShowAction);
//		ObjectAnimator objectAnimator = ObjectAnimator.ofInt(target, "alpha", 0, 1);
//		objectAnimator.setDuration(500);
//		objectAnimator.start();
    }

    private void hideAnim(View target) {
//		AlphaAnimation hideAnim = new AlphaAnimation(1, 0);
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
        if (ListUitl.isNotEmpty(materialList)) {
            int size = materialList.size();
            if (size > 1) {
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
            LogUtils.d(TAG, "loadPicToImageView", "picPath not exists : " + picPath);
//			playNextMaterial();
//			Helper.handlerMaterialError(currentPlayMaterial, ProgramPlayManager.getInstance().getCurrentProgram());
        }
    }


    /**
     * 预加载图片
     */
    @Override
    public void prmLoadMaterial(Material material) {
        preLoadWhichImageView(FileStore.getFileName(material.getU()), true);
//		preLoadWhichImageView(material.getN(), true);
//		LogUtils.d(TAG, "prmLoadMaterial", "prm load picPath  : " + material.getN());
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
        mBaseHandler.recycle();
    }

    @Override
    public void videoLoadCompletion() {
        if (isPreLoadView) {
//            LogUtils.d(TAG, "videoLoadCompletion", "videoLoadCompletion");
//            if (ConfigSettings.COUNT_URL_02 != null && !"".equals(ConfigSettings.COUNT_URL_02)) {
//                HttpUrl.playProgramByGet(ConfigSettings.COUNT_URL_02);
//            }
            //视频加载完成以后 暂停开启以后播放
            videoView.start();
            videoView.pause();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
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
