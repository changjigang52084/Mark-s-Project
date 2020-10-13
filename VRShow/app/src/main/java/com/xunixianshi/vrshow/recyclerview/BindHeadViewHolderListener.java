package com.xunixianshi.vrshow.recyclerview;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by duan on 2016/10/20.
 */

public interface BindHeadViewHolderListener<VH extends RecyclerView.ViewHolder>{
    @NonNull
    VH onCreateHeadViewHolder(ViewGroup parent, final int viewType);
    void onBindHeadViewHolder(RecyclerView.ViewHolder viewHolder);
}
