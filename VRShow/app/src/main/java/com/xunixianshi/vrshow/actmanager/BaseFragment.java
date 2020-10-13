package com.xunixianshi.vrshow.actmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.xunixianshi.vrshow.AndroidApplication;

/**
 * User: hch
 * Date: 2016-03-03
 * Time: 17:32
 * FIXME
 */
public abstract class BaseFragment extends Fragment {

    protected abstract View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle);

    /** initialization data */
    protected void initView(View parentView) {
    }

    /** initialization data */
    protected void initData() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflaterView(inflater, container, savedInstanceState);
        initView(view);
        initData();
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
