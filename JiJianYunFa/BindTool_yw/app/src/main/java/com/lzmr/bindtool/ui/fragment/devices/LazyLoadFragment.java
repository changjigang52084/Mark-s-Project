package com.lzmr.bindtool.ui.fragment.devices;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.lzkj.baize_android.utils.LogUtils;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.ui.fragment.BaseFragment;


/**
 * 项目名称：BindTool
 * 类描述：
 * 创建人：longyihuang
 * 创建时间：16/11/21 10:50
 * 邮箱：huanglongyi@17-tech.com
 */

public abstract class LazyLoadFragment extends BaseFragment {
    /**
     * 控件是否初始化完成
     */
    private boolean isViewCreated;
    /**
     * 数据是否已加载完毕
     */
    private boolean isLoadDataCompleted;


    public LazyLoadFragment() {
    }

    public LazyLoadFragment(ControlFragmentListener switchFragmentListener) {
        super(switchFragmentListener);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (null == view) {
            view = inflater.inflate(getLayout(), container, false);
        }
        initViews(view);
        isViewCreated = true;
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        LogUtils.e(LogUtils.getStackTraceElement(),"onActivityCreated");
        if (getUserVisibleHint()) {
            isLoadDataCompleted = true;
            loadData();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isViewCreated && !isLoadDataCompleted) {
            isLoadDataCompleted = true;
            loadData();
        }
    }

    public abstract int getLayout();
    public abstract void initViews(View view);
    /**
     * 子类实现加载数据的方法
     */
    public abstract  void loadData();



}
