package com.unccr.zclh.dsdps.adapter;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.download.bean.LiftRatingBean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LiftRatingAdapter extends RecyclerView.Adapter<LiftRatingAdapter.ViewHolder> {

    private Context mContext;
    private List<LiftRatingBean> mList;
    private LayoutInflater inflater;
    private HashMap<String, Integer> map = new HashMap<>();
    private HashMap<Integer, Boolean> checkedMap = new HashMap<>();

    public LiftRatingAdapter(Context context, List<LiftRatingBean> list) {
        mContext = context;
        mList = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.item_lift_rating, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.cb_rate.setText(mList.get(position).getName());
        holder.cb_rate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    map.put(mList.get(position).getKey(), 1);
                    checkedMap.put(position, true);
                }else{
                    map.put(mList.get(position).getKey(), 0);
                }
            }
        });
        if (checkedMap != null && map.containsKey(position)) {
            holder.cb_rate.setChecked(true);
        }else{
            holder.cb_rate.setChecked(false);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private CheckBox cb_rate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            cb_rate = itemView.findViewById(R.id.cb_rate);
        }
    }

    public HashMap<String, Integer> getMap() {
        HashMap<String, Integer> selectedMap = new HashMap<>();
        for(Map.Entry<String, Integer> entry : map.entrySet()){
            if (entry.getValue() == 1) {
                selectedMap.put(entry.getKey(), entry.getValue());
            }
        }
        return selectedMap;
    }

    public void clear() {
        if (checkedMap != null && checkedMap.size() > 0) {
            checkedMap.clear();
        }
        map.clear();
        notifyDataSetChanged();
    }

//    public void updateDate(List<LiftRatingBean> list) {
//        mList.clear();
//        mList.addAll(list);
//        notifyDataSetChanged();
//    }
}
