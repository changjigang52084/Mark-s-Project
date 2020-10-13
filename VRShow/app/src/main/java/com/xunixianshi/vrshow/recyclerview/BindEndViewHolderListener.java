package com.xunixianshi.vrshow.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * 尾部布局
 *@author DuanChunLin
 *@time 2016/10/26 18:53
 */
public interface BindEndViewHolderListener<VH extends RecyclerView.ViewHolder>{
    @NonNull
    VH onCreateEndViewHolder(ViewGroup parent, final int viewType);
    void onBindEndViewHolder(RecyclerView.ViewHolder viewHolder);
}