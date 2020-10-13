package com.xunixianshi.vrshow.actmanager;

import android.os.Bundle;

import androidx.fragment.app.FragmentActivity;


/**
 * User: hch
 * Date: 2016-03-03
 * Time: 17:06
 */
public abstract class BaseFragmentActivity extends FragmentActivity implements IINITInterface {

    protected void initDataFromThread() {}

    /**
     * 初始化view
     * @ClassName BaseFragmentActivity
     *@author HeChuang
     *@time 2016/12/22 15:49
     */
    protected void initView() {}
    /**
     * 初始化数据
     * @ClassName BaseFragmentActivity
     *@author HeChuang
     *@time 2016/12/22 15:49
     */
    protected void initData() {}

    @Override
    public void initialize() {
        initView();
        initData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StackActivityManager.create().addActivity(this);
        setRootView();
        initialize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        StackActivityManager.create().finishActivity(this);
    }
}
