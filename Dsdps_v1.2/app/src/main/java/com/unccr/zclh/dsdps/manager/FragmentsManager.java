package com.unccr.zclh.dsdps.manager;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.fragment.BaseFragment;
import com.unccr.zclh.dsdps.fragment.DefaultFragment;
import com.unccr.zclh.dsdps.fragment.FactoryFragment;
import com.unccr.zclh.dsdps.fragment.StreamMediaFragment;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.models.Material;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.play.PlayAudioManager;
import com.unccr.zclh.dsdps.util.BitmapUtil;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.LayoutUtil;
import com.unccr.zclh.dsdps.util.StringUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午4:43:59
 * @parameter 管理fragment的类
 */
public class FragmentsManager {

    private static final String TAG = "FragmentsManager";

    /**
     * 上下文对象
     ***/
    private Context context;
    private Handler mHandler;
    /**
     * 播放音频文件
     **/
    private PlayAudioManager audioManager;

    private FragmentManager fragmentManager;
    /**
     * 无节目界面
     */
    private DefaultFragment defaultFragment;
    /**
     * 当前节目布局
     */
    private FrameLayout currentFrameLayout;
    /**
     * 下一个节目布局
     */
    private FrameLayout nextFrameLayout;
    /**
     * 天气fragment
     */
//	private WeatherFragment weatherFragment;
    /**
     * 天气布局控件
     */
//	private FrameLayout weatherView;
    /**
     * 屏幕内容主布局控件
     */
    private FrameLayout mainFrameLayoutView;
    private StreamMediaFragment streamMediaFragment;
    //	private MainFrameLayoutView mainFrameLayoutView;

    public FragmentsManager(Context context, FragmentManager fragmentManager,
                            FrameLayout mainFrameLayoutView) {
        this.mainFrameLayoutView = mainFrameLayoutView;
        this.fragmentManager = fragmentManager;
        this.context = context;
        mHandler = new Handler();
        audioManager = new PlayAudioManager();
    }

    /**
     * 切换到节目的界面
     *
     * @param program 节目对象
     * @return
     */
    public void switchProgramFragment(Program program) {
        stopFragment();
        if (null == program) {
            Log.d(TAG,"switchProgramFragment program is null.");
            switchDefaultFragment(null);
            return;
        }
        mainFrameLayoutView.removeAllViews();
        if (null == nextFrameLayout || !preLoadLayoutIsPlayProgram(program)) {
            currentFrameLayout = getLayoutToProgram(program, false); //没预加载就加载下一个节目
            if (null == currentFrameLayout) {
                return;
            }
        } else {
            Log.d(TAG,"switchProgramFragment program is not null.");
            playPreLoadLayout(nextFrameLayout);
            nextFrameLayout.setVisibility(View.VISIBLE); //预加载了就把预加载的节目设为可见
            currentFrameLayout = nextFrameLayout;
        }
        setBackground(program);
        playBackgroundMusic(program);
        mainFrameLayoutView.addView(currentFrameLayout, LayoutUtil.getInstance().getFullScreenLayoutParams());
    }

    /**
     * 判断当前预加载的节目是否是当前要播放的节目
     *
     * @param program 当前要播放的节目
     * @return 返回true表示是, false表示不是
     */
    private boolean preLoadLayoutIsPlayProgram(Program program) {
        String prmKey = (String) nextFrameLayout.getTag(nextFrameLayout.getId());
        if (program.getKey().equals(prmKey)) {
            return true;
        }
        return false;
    }

    /**
     * 播放背景音乐
     *
     * @param program 节目
     */
    private void playBackgroundMusic(Program program) {
        Material bgMusicMaterial = program.getBgm();
        if (null != bgMusicMaterial) {
            audioManager.stopAudio();
            audioManager.playAudio(bgMusicMaterial);
        }
    }

    /**
     * 添加背景图片和背景颜色
     *
     * @param program
     */
    private void setBackground(Program program) {
        String backgroundColor = program.getBc();//"#428bca"
        if (!TextUtils.isEmpty(backgroundColor)) {
            mainFrameLayoutView.setBackgroundColor(Color.parseColor(backgroundColor));
        } else {
            mainFrameLayoutView.setBackgroundColor(Color.BLACK);
        }
        if (isExistBackgroundPic(program)) {
            String backgroundPic = program.getBi().getU();//背景图片
            if (!TextUtils.isEmpty(backgroundPic)) {
                Bitmap backgroundBitmap = BitmapUtil.getBitmapWithScale(FileStore.getInstance()
                                .getImageFilePath(FileStore.getFileName(backgroundPic)),
                        mainFrameLayoutView.getWidth(), mainFrameLayoutView.getHeight(), true);
                mainFrameLayoutView.setBackgroundDrawable(new BitmapDrawable(backgroundBitmap));
            } else {
                mainFrameLayoutView.setBackgroundDrawable(null);
            }
        }
    }

    /**
     * 根据传入的节目判断是节目是否添加了背景图
     *
     * @param program 节目对象
     * @return true表示添加了背景图, false表示未添加
     */
    private static boolean isExistBackgroundPic(Program program) {
        Material material = program.getBi();
        if (null != material) {
            String backgroundPicUrl = material.getU();
            String backgroundPicMd5 = material.getM();
            if (!StringUtil.isNullStr(backgroundPicUrl) && !StringUtil.isNullStr(backgroundPicMd5)) {
                return true;
            }
        }
        return false;
    }

    private void startAnim(View view) {
        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat("alpha",
                0.5f, 1f);
//	        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat("scaleX", 1f,
//	                0.5f, 1f);
//	        PropertyValuesHolder pvhZ = PropertyValuesHolder.ofFloat("scaleY", 1f,
//	                0.5f, 1f);
        ObjectAnimator.ofPropertyValuesHolder(view, pvhX).setDuration(500).start();
    }

    /**
     * 开始播放预加载里面的内容
     *
     * @param frameLayout 预加载的layout
     */
    private void playPreLoadLayout(FrameLayout frameLayout) {
        List<BaseFragment> fragments = (List<BaseFragment>) frameLayout.getTag();
        for (BaseFragment fragment : fragments) {
            fragment.startPlayPrograme();
        }
    }

    /**
     * 预加载节目
     *
     * @param program 节目
     */
    public void preLoadProgram(final Program program) {
        if (null == program) {
            return;
        }
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                LayoutUtil.AREA_ID += (program.getAs().size() * 5);
                Log.d(TAG,"preLoadProgram LayoutUtil.AREA_ID: " + LayoutUtil.AREA_ID);
                nextFrameLayout = getLayoutToProgram(program, true);
                mainFrameLayoutView.addView(nextFrameLayout, LayoutUtil.getInstance().getOnePxLayoutParams());
            }
        }, 1000);
    }

    /**
     * 清除预加载
     */
    public void clearPreProgram() {
        nextFrameLayout = null;
    }

    /**
     * 根据节目和区域获取要展示的layout
     *
     * @param program 节目
     * @return
     */
    private synchronized FrameLayout getLayoutToProgram(Program program, boolean isPreLoad) {
        List<Area> areaList = program.getAs();
        if (null == areaList || areaList.isEmpty()) {
            if (!isPreLoad) {
                switchDefaultFragment(null);
            }
            return null;
        }
        orderAreaZ(areaList);
        int areaSize = areaList.size();
        LayoutUtil.AREA_ID += areaSize;
        Log.d(TAG,"getLayoutToProgram LayoutUtil.AREA_ID: " + LayoutUtil.AREA_ID);
        FrameLayout inFrame = new FrameLayout(context);
        if (isPreLoad) {
            inFrame.setVisibility(View.INVISIBLE);
        }
        LayoutUtil.getInstance().loadFrameLayoutForArea(inFrame, program, isPreLoad);
        List<BaseFragment> fragments = new ArrayList<BaseFragment>();
        // 逐一加载frame，再将view和fragment关联
        for (int i = 0; i < areaSize; i++) {
            initFragmentForArea(LayoutUtil.AREA_ID + i, areaList.get(i), fragments, isPreLoad);
        }
        inFrame.setTag(fragments);
        inFrame.setTag(inFrame.getId(), program.getKey());
        return inFrame;
    }

    /**
     * 根据区域的Z轴 安装升序排序
     *
     * @param areaList
     */
    private void orderAreaZ(List<Area> areaList) {
        Collections.sort(areaList, new Comparator<Area>() {
            @Override
            public int compare(Area lhs, Area rhs) {
                Integer newInteger = lhs.getZ();
                if (null == newInteger) {
                    newInteger = 0;
                }
                Integer oldInteger = rhs.getZ();
                if (null == oldInteger) {
                    oldInteger = 0;
                }
                return newInteger.compareTo(oldInteger);
            }
        });
    }

    /**
     * 根据区域加载fragment
     *
     * @param areaId    区域id
     * @param area      区域对象
     * @param fragments 添加fragment对象到list中
     */
    private void initFragmentForArea(int areaId, Area area, List<BaseFragment> fragments, boolean isPreLoad) {
        FragmentTransaction ft = fragmentManager.beginTransaction();
        BaseFragment areaFragment = FactoryFragment.newInstance().creatFragment(area, isPreLoad);
        if (null != areaFragment) {
            fragments.add(areaFragment);
            Log.d(TAG, "initFragmentForArea add fragment, Area<" + areaId + "> , isPreLoad : " + isPreLoad);
            ft.replace(areaId, areaFragment);
            ft.commitAllowingStateLoss();
        }
    }

    /**
     * 切换到无节目的界面
     *
     * @param alertMsg 提示信息
     */
    public void switchDefaultFragment(String alertMsg) {
        stopFragment();
        defaultFragment = new DefaultFragment(null);
        if (!StringUtil.isNullStr(alertMsg)) {
            Bundle bundle = new Bundle();
            bundle.putString("alertMsg", alertMsg);
            defaultFragment.setArguments(bundle);
        }
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, defaultFragment);
        fragmentTransaction.commitAllowingStateLoss();
//        switchStreamMediaFragment();
    }

    /**
     * 切换到流媒体播放界面
     */
    public void switchStreamMediaFragment() {
        stopFragment();
        streamMediaFragment = new StreamMediaFragment();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.main_container, streamMediaFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 将上个节目里面的fragment全部删除掉
     **/
    private void stopFragment() {
        if (defaultFragment != null) {
            fragmentManager.beginTransaction().remove(defaultFragment).commitAllowingStateLoss();
            defaultFragment = null;
            nextFrameLayout = null;
        }
        stopPlaybackCurrentFrame();
    }

    /**
     * 清空当前布局中的所有fragment
     */
    private void stopPlaybackCurrentFrame() {
        if (currentFrameLayout != null) {
            List<BaseFragment> fragments = (List<BaseFragment>) currentFrameLayout.getTag();
            if (fragments != null && !fragments.isEmpty()) {
                for (BaseFragment f : fragments) {
                    fragmentManager.beginTransaction().remove(f).commitAllowingStateLoss();
                }
                currentFrameLayout.removeAllViews();
            }
            Log.d(TAG,"stopPlaybackCurrentFrame fragments.size: " + fragments.size());
            LayoutUtil.getInstance().updateAreaId(fragments.size());
            fragments.clear();
            fragments = null;
            currentFrameLayout = null;
        }
        audioManager.stopAudio();
    }

    public void stop() {
        stopFragment();
    }

}
