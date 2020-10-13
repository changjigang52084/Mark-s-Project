package com.xunixianshi.vrshow.my.fragment;

import android.Manifest;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.permissions.RxPermissions;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * TODO 创建页面
 *
 * @author MarkChang
 * @ClassName CreateVideoContentActivity
 * @time 2016/11/1 15:52
 */

public class CreateVideoContentActivity extends BaseAct {

    @Bind(R.id.my_back_rl)
    RelativeLayout my_back_rl;
    @Bind(R.id.my_content_tv)
    TextView my_content_tv;
    @Bind(R.id.my_fragment_create_video_content_rl)
    RelativeLayout my_fragment_create_video_content_rl;


    private boolean isCanRefreshLocalVideo;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_create_video_content);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        my_content_tv.setText("创建内容");
    }

    @Override
    protected void initData() {
        super.initData();

        RxPermissions rxPermissions = RxPermissions.getInstance(this);
        rxPermissions.setLogging(true);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                            isCanRefreshLocalVideo = true;
                        } else {
                            // 未获取权限
                            isCanRefreshLocalVideo = false;
                        }
                    }
                });
    }

    @OnClick({R.id.my_back_rl, R.id.my_fragment_create_video_content_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_back_rl:
                CreateVideoContentActivity.this.finish();
                break;
            case R.id.my_fragment_create_video_content_rl:
                if(isCanRefreshLocalVideo){
                    Bundle bundle1 = new Bundle();
                    bundle1.putString("videoName", "");
                    bundle1.putString("videoIntroduce", "");
                    showActivity(CreateVideoContentActivity.this, ChoiceVideoActivity.class, bundle1);
                    CreateVideoContentActivity.this.finish();
                }
                else{
                    showToastMsg("没有获取本地视频权限");
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
