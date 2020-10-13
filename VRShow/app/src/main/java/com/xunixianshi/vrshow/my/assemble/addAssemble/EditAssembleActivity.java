package com.xunixianshi.vrshow.my.assemble.addAssemble;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

import com.hch.utils.MLog;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.my.assemble.AssembleDetailsObject;
import com.xunixianshi.vrshow.my.assemble.detail.AssembleDetailsActivity;

import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/23.
 * 合集内容
 */

public class EditAssembleActivity extends BaseAct {
    public static final  String ASSEMBLE_DETAIL_INTENT_KEY = "assembleDetail";


    EditAssembleFragment mFragment;
    private void initFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (mFragment != null) {
            transaction.remove(mFragment);
        }
        Bundle bundle = new Bundle();
        Intent intent = getIntent();
        if(intent!=null && intent.getExtras()!=null){
            AssembleDetailsObject assembleDetailsObject = (AssembleDetailsObject)intent.getExtras().getSerializable(ASSEMBLE_DETAIL_INTENT_KEY);
            bundle.putSerializable(ASSEMBLE_DETAIL_INTENT_KEY,assembleDetailsObject);
         }
        //创建一个fragment
        mFragment = new EditAssembleFragment();
        //实例化fragment事务管理器
        //用新创建的fragment来代替fragment_container
        mFragment.setArguments(bundle);
        transaction.replace(R.id.empty_attach_fragment, mFragment);
        //提交事务
        transaction.commit();
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_empty_attach_fragment);
        ButterKnife.bind(this);
        initFragment();
    }

    @Override
    public void onBackPressed() {
        if (mFragment != null) {
            mFragment.onBackPressed();
        }
    }

    //使用时的调用:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (mFragment != null) {
            mFragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}

