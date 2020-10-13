package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.view.KeyEvent;

import androidx.fragment.app.Fragment;


public abstract class BaseFragment extends Fragment {

    //这里的是我们的重点
    public abstract boolean onKeyDown(KeyEvent event);

    //初始化选中
    public abstract void Select(int postion);

}
