package com.sunchip.adw.cloudphotoframe.adapter;

import android.content.Context;
import android.transition.Transition;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.util.BitmapUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by yingmuliang on 2020/7/13.
 */
public class TransitionAdpate extends BaseAdapter {

    private static final String TAG = "TransitionAdpate";
    private int selected = 0;

    private Context mContext;
    private List<String> Transition = new ArrayList<>();

    public TransitionAdpate(Context context) {
        this.mContext = context;
    }

    public TransitionAdpate(Context context, List<String> Transition) {
        this.mContext = context;
        this.Transition = Transition;
    }

    public void setList(List<String> Transition) {
        this.Transition = Transition;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return Transition.size();
    }

    @Override
    public Object getItem(int i) {
        return Transition.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.transitiontypeitem, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tvName.setText(Transition.get(i));
        holder.tvName.setTypeface(CloudFrameApp.typeFace);
        if (selected == i) {
            BitmapUtils.getInstance().setDrable(holder.tvName, R.mipmap.check_true, mContext, 1);
        }else   BitmapUtils.getInstance().setDrable(holder.tvName, R.mipmap.yuanhuan, mContext, 1);
        return convertView;
    }


    public void setselected(int Transition) {
        this.selected = Transition;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvName;

        ViewHolder(View view) {
            tvName = view.findViewById(R.id.textType);
        }
    }
}
