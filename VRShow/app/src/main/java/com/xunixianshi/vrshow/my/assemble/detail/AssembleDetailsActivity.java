package com.xunixianshi.vrshow.my.assemble.detail;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.my.assemble.AssembleListActivity;

import butterknife.ButterKnife;


/**
 * 合集内容列表
 *@author DuanChunLin
 *@time 2016/10/11 16:25
 */
public class AssembleDetailsActivity extends BaseAct {

    private String mCompilationId;
    private String mCompilationName;
    private int mAssembleUserType;
    AssembleDetailsFragment mFragment;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_empty_attach_fragment);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        mCompilationId = getIntent().getStringExtra("assembleId");
        mCompilationName = getIntent().getStringExtra("assembleName");
        mAssembleUserType = getIntent().getIntExtra(AssembleListActivity.ASSEMBLE_USER_TYPE,1);
        initFragment();
    }

    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mFragment != null) {
            transaction.remove(mFragment);
        }
        //创建一个fragment
        mFragment = new AssembleDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString("assembleId",mCompilationId);
        bundle.putString("assembleName",mCompilationName);
        bundle.putInt(AssembleListActivity.ASSEMBLE_USER_TYPE,mAssembleUserType);

        mFragment.setArguments(bundle);
        //实例化fragment事务管理器
        //用新创建的fragment来代替fragment_container
        transaction.replace(R.id.empty_attach_fragment, mFragment);

        //提交事务
        transaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(mFragment != null){
            mFragment.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
