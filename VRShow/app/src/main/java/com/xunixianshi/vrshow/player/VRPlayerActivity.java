package com.xunixianshi.vrshow.player;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.player.object.VideoInfo;

/**
 * Created by duanchunlin on 2016/8/4.
 */
public class VRPlayerActivity extends BaseAct {
    private VRPlayerFragment mVRPlayerFragment;
    /**
     * By Lin Date:2016/8/5  10:50
     *
     * @注释： 参数较多时，用类来表示
     */
    public static Intent newIntent(Context context, VideoInfo videoInfo) {
        Intent intent = new Intent(context, VRPlayerActivity.class);
        intent.putExtra("videoInfo", videoInfo);
        return intent;
    }

    public static void intentTo(Context context, VideoInfo videoInfo) {
        context.startActivity(newIntent(context, videoInfo));
    }

    public static void intentTo(Context context, String videoName, String videoUrl, int videoType, int sourceId, int lastProgress, boolean isNeedNet) {
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setVideoName(videoName);
        videoInfo.setVideoUrl(videoUrl);
        videoInfo.setVideoType(videoType);
        videoInfo.setSourceId(sourceId);
        videoInfo.setLastProgress(lastProgress);
        videoInfo.setNeedNet(isNeedNet);
        context.startActivity(newIntent(context, videoInfo));
    }

    public static void intentTo(Context context, String videoName,int sourceId){
        VideoInfo videoInfo = new VideoInfo();
        videoInfo.setSourceId(sourceId);
        videoInfo.setVideoName(videoName);
        videoInfo.setNeedNet(true);
        context.startActivity(newIntent(context, videoInfo));
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_vr_player);
    }

    @Override
    protected void initData() {
//        mHandler.sendEmptyMessageDelayed(0,30);

        initPlayerFragment();
    }

//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            initPlayerFragment();
//        }
//    };

    /**
     * @author 段春林
     * @time 2016/8/30 18:23
     * 播放器添加横竖屏代码
     */
    private void initPlayerFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mVRPlayerFragment!=null){
            transaction.remove(mVRPlayerFragment);
        }
        //创建一个fragment
        mVRPlayerFragment = VRPlayerFragment.build().setCanConfigurationChanged(false);
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if(intent!=null && intent.getExtras()!=null){
            VideoInfo videoInfo = (VideoInfo) getIntent().getSerializableExtra("videoInfo");
            bundle.putSerializable("videoInfo",videoInfo);
        }
        mVRPlayerFragment.setArguments(bundle);
        //实例化fragment事务管理器
        //用新创建的fragment来代替fragment_container
        transaction.add(R.id.vr_player_fragment_rl, mVRPlayerFragment);
        //提交事务
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if (mVRPlayerFragment != null) {
            mVRPlayerFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mHandler.removeMessages(0);
    }
}
