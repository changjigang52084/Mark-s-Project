package com.xunixianshi.vrshow.my.localVideo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.StringUtils;
import com.xnxs.mediaplayer.widget.media.IMediaPlayerVRControl;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.localVideo.adapter.VideoListViewAdapter;
import com.xunixianshi.vrshow.my.localVideo.entity.VideoBean;
import com.xunixianshi.vrshow.my.localVideo.util.LocalVideoUtil;
import com.xunixianshi.vrshow.permissions.RxPermissions;
import com.xunixianshi.vrshow.player.VRPlayerActivity;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemCallBack;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.xunixianshi.vrshow.util.cache.CacheUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * Created by markIron on 2016/9/8.
 */
public class ObtainPhoneVideoActivity extends BaseAct {

    @Bind(R.id.my_down_load_recycler_view)
    RecyclerView my_down_load_recycler_view;

    @Bind(R.id.page_empty_rl)
    RelativeLayout page_empty_rl;

    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;

    @Bind(R.id.title_right_function_tv)
    TextView title_right_function_tv;

    private LoadingAnimationDialog progressDialog;
    private VideoListViewAdapter mVideoListViewAdapter;

    private HintDialog mHintDialog;

    private List<VideoBean> mSelectList;

    private boolean isCanRefreshLocalVideo;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_downloads);
        ButterKnife.bind(this);
    }


    @Override
    protected void initView() {
        super.initView();
        mHintDialog = new HintDialog(this);
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int position = msg.arg1;
                    if (mVideoListViewAdapter.getGroup().size() > position) {
                        mVideoListViewAdapter.notifyItemChanged(position);
                    }
                    break;
            }
        }
    };

    @Override
    public void initData() {
        title_center_name_tv.setText(R.string.user_label_local_video);
        mVideoListViewAdapter = new VideoListViewAdapter(this, mHandler);
        my_down_load_recycler_view.setLayoutManager(new LinearLayoutManager(this));
        my_down_load_recycler_view.setAdapter(mVideoListViewAdapter);

        mSelectList = new ArrayList<>();
        progressDialog = new LoadingAnimationDialog(this);
        my_down_load_recycler_view.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, ScreenUtils.dip2px(this, 5), ContextCompat.getColor(this, R.color.c_layout_blue_bg)));

        mVideoListViewAdapter.setRecyclerItemOnClickListener(new RecyclerItemOnClickListener() {
            @Override
            public void onClick(int position, int expandableType, RecyclerItemCallBack callBack) {
                VideoBean videoBean = mVideoListViewAdapter.getItem(position);
                switch (expandableType) {
                    case 1:
                        if (!StringUtils.isBlank(videoBean.getVideoPath())) {
                            // 打开视频
                            VRPlayerActivity.intentTo(ObtainPhoneVideoActivity.this, videoBean.getVideoTitle(), videoBean.getVideoPath(), IMediaPlayerVRControl.VIDEO_TYPE_NORMAL, 0, 0, false);
                        }
                        break;
                    case 2:
                        mSelectList.clear();
                        mSelectList.add(videoBean);
                        showDialog();
                        break;
                }
            }
        });

        RxPermissions rxPermissions = RxPermissions.getInstance(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                            isCanRefreshLocalVideo = true;
                            refreshLocalVideo();
                        } else {
                            // 未获取权限
                            isCanRefreshLocalVideo = false;
                        }
                    }
                });
    }

    @OnClick({R.id.title_left_back_iv, R.id.title_right_function_tv})
    public void clickBack(View view) {
        switch (view.getId()) {
            case R.id.title_left_back_iv:
                finish();
                break;
            case R.id.title_right_function_tv:
                mSelectList.clear();
                mSelectList.addAll(mVideoListViewAdapter.getGroup());
                showDialog();
                break;
        }
    }

    private void refreshLocalVideo() {
        if (!isCanRefreshLocalVideo) {
            return;
        }
        progressDialog.show();
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                final ArrayList<VideoBean> videoBeans = LocalVideoUtil.getLocalVideoFiles(ObtainPhoneVideoActivity.this);
                ObtainPhoneVideoActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        progressDialog.dismiss();
                        // 获取本地视频集合
                        if (videoBeans != null && videoBeans.size() > 0) {
                            mVideoListViewAdapter.setGroup(videoBeans);
                        } else {
                            title_right_function_tv.setClickable(false);
                            title_right_function_tv.setTextColor(Color.parseColor("#e6e6e6"));
                            page_empty_rl.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void showDialog() {
        mHintDialog.setContextText("是否确定删除");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelect();
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    private void deleteSelect() {
        boolean result = deleteDownLoadItems(mSelectList);
        if (result) {
            mVideoListViewAdapter.removeItems(mSelectList, true);
            showToastMsg("删除成功");
        } else {
            refreshLocalVideo();
        }
        mSelectList.clear();
        if(mVideoListViewAdapter.getGroup().size() == 0){
            title_right_function_tv.setClickable(false);
            title_right_function_tv.setTextColor(Color.parseColor("#e6e6e6"));
            page_empty_rl.setVisibility(View.VISIBLE);
        }
    }

    public boolean deleteDownLoadItems(List<VideoBean> downloads) {
        // 删除操作
        boolean isDelete;
        boolean isDeleteState = true;
        for (int j = 0; j < downloads.size(); j++) {
            VideoBean videoBean = downloads.get(j);
            File file = new File(videoBean.getVideoPath());
            isDelete = file.delete();//有问题再做异步
            if (!isDelete) {
                showToastMsg(videoBean.getVideoTitle() + " 删除失败!");
                isDeleteState = false;
            } else {
                File cacheFile = CacheUtil.getInstance().getFromCacheImage(videoBean.getVideoPath());
                if (cacheFile != null && cacheFile.exists()) {
                    cacheFile.delete();
                }
                Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(file));
                sendBroadcast(intent);
            }
        }
        return isDeleteState;
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

}




