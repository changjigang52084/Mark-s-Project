package com.unccr.zclh.dsdps.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.download.bean.LiftBean;

import java.util.List;

public class LiftInfoAdapter extends RecyclerView.Adapter<LiftInfoAdapter.ViewHolder> {

    private Context mContext;
    private List<LiftBean> mList;
    private LayoutInflater inflater;

    public LiftInfoAdapter(Context context, List<LiftBean> list) {
        mContext = context;
        mList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lift, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        LiftBean lift = mList.get(position);
        holder.tv_name.setText(lift.getFieldName());
        holder.tv_content.setText("null".equals(lift.getFieldContent()) ? "无" : lift.getFieldContent());
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private TextView tv_name, tv_content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_content = itemView.findViewById(R.id.tv_content);

        }
    }
}
