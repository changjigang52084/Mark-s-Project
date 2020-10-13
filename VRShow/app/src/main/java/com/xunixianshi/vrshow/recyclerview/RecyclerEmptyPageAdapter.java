package com.xunixianshi.vrshow.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.xunixianshi.vrshow.R;

/**
 * Created by duan on 2016/10/26.
 */

public abstract class RecyclerEmptyPageAdapter<T,VH extends RecyclerView.ViewHolder> extends  RecyclerBaseAdapter<T,VH>{
    private Context mContext;
    public RecyclerEmptyPageAdapter(Context context){
        mContext = context;
        setBindEndViewHolderInterface(new BindEndViewHolderListener() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateEndViewHolder(ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(mContext).inflate(R.layout.public_recycler_empty_page, parent, false);
                view.setVisibility(View.GONE);
                return new EmptyHolder(view);
            }
            @Override
            public void onBindEndViewHolder(RecyclerView.ViewHolder viewHolder) {
                if(getGroup().size()==0){
                    viewHolder.itemView.setVisibility(View.VISIBLE);
                }
                else {
                    viewHolder.itemView.setVisibility(View.GONE);
                }
            }
        });
    }

    private class EmptyHolder extends RecyclerView.ViewHolder{
        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }
}
