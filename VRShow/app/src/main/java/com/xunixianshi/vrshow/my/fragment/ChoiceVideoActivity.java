package com.xunixianshi.vrshow.my.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hch.viewlib.widget.NoScrollGridView;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshScrollView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.adpater.ChoiceVideoAdapter;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.localVideo.entity.VideoBean;
import com.xunixianshi.vrshow.my.localVideo.util.LocalVideoUtil;
import com.xunixianshi.vrshow.permissions.RxPermissions;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * TODO 选择本地视频页面
 *
 * @author MarkChang
 * @ClassName ChoiceVideoActivity
 * @time 2016/11/1 15:53
 */
public class ChoiceVideoActivity extends BaseAct {

    @Bind(R.id.my_fragment_choice_video_tv)
    TextView my_fragment_choice_video_tv;
    @Bind(R.id.page_no_have_bg_head_iv)
    ImageView page_no_have_bg_head_iv;
    @Bind(R.id.my_fragment_choice_video_ptrsv)
    PullToRefreshScrollView my_fragment_choice_video_ptrsv;
    @Bind(R.id.my_fragment_choice_video_date_nsgv)
    NoScrollGridView my_fragment_choice_video_date_nsgv;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private BroadcastReceiver receiver;// 廣播接收者
    public final static String SEND_LOAD_VIDEO_NOTICE = "send_load_video_notice";
    private String clickVideoPath;
    private LoadingAnimationDialog myProgressDialog;
    private String clickVideoSize;
    private String clickVideoSubTitle;
    private Bitmap clickVideoBitmap;
    private String videoName;
    private String videoIntroduce;
    private ArrayList<VideoBean> videoBeans;
    private boolean isCanOpenCamera = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_choice_video);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        my_fragment_choice_video_tv.setText("视频");
        myProgressDialog = new LoadingAnimationDialog(ChoiceVideoActivity.this);
    }

    @Override
    protected void initData() {
        super.initData();
        videoName = getIntent().getStringExtra("videoName");
        videoIntroduce = getIntent().getStringExtra("videoIntroduce");
        videoBeans = new ArrayList<VideoBean>();
        RxPermissions rxPermissions = RxPermissions.getInstance(ChoiceVideoActivity.this);
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                            isCanOpenCamera = true;
                        } else {
                            // 未获取权限
                            isCanOpenCamera = false;
                        }
                    }
                });
        initListener();
        myProgressDialog.show();
        // 实例化广播接收者
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(SEND_LOAD_VIDEO_NOTICE)) {
                    clickVideoPath = intent.getStringExtra(ChoiceVideoAdapter.CLICK_BEAN_VIDEO_PATH);
                    if (!TextUtils.isEmpty(clickVideoPath)) {
                        Intent intent1 = new Intent(ChoiceVideoActivity.this, CreateContentActivity.class);
                        intent1.putExtra("videoPath", clickVideoPath);
                        intent1.putExtra("isNative", true);
                        intent1.putExtra("videoName", videoName);
                        intent1.putExtra("videoIntroduce", videoIntroduce);
                        ChoiceVideoActivity.this.startActivity(intent1);
                        ChoiceVideoActivity.this.finish();
                    }
                }
            }
        };
        // 添加過濾器
        IntentFilter filter = new IntentFilter();
        filter.addAction(SEND_LOAD_VIDEO_NOTICE);
        // 註冊廣播
        registerReceiver(receiver, filter);
//        getNativeVideo();
    }
    @Override
    protected void onResume() {
        super.onResume();
        getNativeVideo();
    }

    private void initListener() {
        page_no_have_bg_head_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCanOpenCamera){
                    Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                    intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0);
                    startActivityForResult(intent, 1);
                }else{
                    showToastMsg("没有相机权限！");
                }
            }
        });
        my_fragment_choice_video_ptrsv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                getNativeVideo();
            }
        });
    }

    /**
     * 获取本地视频列表
     *
     * @author HeChuang
     * @time 2016/10/26 15:10
     */
    private void getNativeVideo() {
        videoBeans.clear();
        // 获取本地视频集合
        videoBeans.addAll(LocalVideoUtil.getVideoBeansThumbnail(this, this.getContentResolver()));
        final ChoiceVideoAdapter choiceVideoAdapter = new ChoiceVideoAdapter(this, videoBeans);
        my_fragment_choice_video_date_nsgv.setAdapter(choiceVideoAdapter);
        my_fragment_choice_video_ptrsv.onRefreshComplete();
        myProgressDialog.dismiss();
        if (videoBeans.size() <= 0) {
            page_emity_rl.setVisibility(View.VISIBLE);
        } else {
            page_emity_rl.setVisibility(View.GONE);
        }
    }

    @OnClick(R.id.my_fragment_choice_cancel_rl)
    public void onClick() {
        ChoiceVideoActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 註銷廣播
        unregisterReceiver(receiver);
        ButterKnife.unbind(this);
    }
}
