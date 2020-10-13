package com.sunchip.adw.cloudphotoframe.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.PhotoListEvent;
import com.sunchip.adw.cloudphotoframe.util.ViewUtil;
import com.sunchip.adw.cloudphotoframe.viewholder.LeftListViewHolder;

import java.util.List;

import me.zhouzhuo.zzsecondarylinkage.adapter.LeftMenuBaseListAdapter;

/**
 * Created by zz on 2016/8/19.
 */
public class LeftMenuListAdapter extends LeftMenuBaseListAdapter<LeftListViewHolder, PhotoListEvent> {

    public LeftMenuListAdapter(Context ctx, List<PhotoListEvent> list) {
        super(ctx, list);
    }

    @Override
    public LeftListViewHolder getViewHolder() {
        return new LeftListViewHolder();
    }

    @Override
    public void bindView(LeftListViewHolder leftListViewHolder, View itemView) {
        ViewUtil.scaleContentView((ViewGroup) itemView.findViewById(R.id.root));
        leftListViewHolder.tvMacName = (TextView) itemView.findViewById(R.id.tv_menu);
        leftListViewHolder.tvMacId = (TextView) itemView.findViewById(R.id.tv_id);
    }

    @Override
    public void bindData(LeftListViewHolder leftListViewHolder, int position) {

        int Number;
        if (list.get(position).getPhotos() != null)
            Number = list.get(position).getPhotos().size();
        else
            Number = 0;
        leftListViewHolder.tvMacName.setText(list.get(position).getName() + "(" + Number + ")");
        leftListViewHolder.tvMacName.setTypeface(CloudFrameApp.typeFace);
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_menu;
    }

    //9-patch drawable
    @Override
    public int getIndicatorResId() {
        return R.drawable.list_select;
    }
}
