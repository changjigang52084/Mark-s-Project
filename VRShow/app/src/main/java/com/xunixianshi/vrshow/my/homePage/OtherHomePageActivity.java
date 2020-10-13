package com.xunixianshi.vrshow.my.homePage;

import android.os.Bundle;
import android.widget.FrameLayout;

import androidx.fragment.app.FragmentTransaction;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.actmanager.BaseFra;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by duan on 2016/9/19.
 */
public class OtherHomePageActivity extends BaseAct {

    BaseFra mFragment;
    private String showId = "";

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_empty_attach_fragment);
        ButterKnife.bind(this);
        showId = getIntent().getStringExtra("showId");
        initFragment();
    }
    /**
     * @author 段春林
     * @time 2016/8/30 18:23
     * 播放器添加横竖屏代码
     */
    @Bind(R.id.empty_attach_fragment)
    FrameLayout empty_attach_fragment;
    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(mFragment!=null){
            transaction.remove(mFragment);
        }
        //创建一个fragment
        mFragment = new OtherHomePageFragment();
        //实例化fragment事务管理器
        //用新创建的fragment来代替fragment_container

        Bundle bundle = new Bundle();
        bundle.putString("showId",showId);
        mFragment.setArguments(bundle);
        transaction.replace(R.id.empty_attach_fragment, mFragment)
                .setCustomAnimations(R.anim.activity_left_in,R.anim.activity_right_out);
        //提交事务
        transaction.commit();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
