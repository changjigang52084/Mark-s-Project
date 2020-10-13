package com.unccr.zclh.dsdps.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.models.PositionEvent;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.ArrayList;

import static java.lang.Integer.MAX_VALUE;

public class LiftContentAdapter extends RecyclerView.Adapter<LiftContentAdapter.ViewHolder> {

    private static final String TAG = "LiftContentAdapter";
    private Context mContext;
    private ArrayList<String> imageList = new ArrayList<>();
    private ArrayList<String> tempList = new ArrayList<>();
    private String imgPath;

    public LiftContentAdapter(Context context) {
        mContext = context;
    }

    public void setList(ArrayList<String> list) {
        if (list != null && !list.isEmpty()) {
            this.imageList = list;
            notifyDataSetChanged();
        }
    }

    public ArrayList<String> updateList() {
        if (!imageList.isEmpty()) {
            imageList.add(imageList.get(0));
            imageList.remove(0);
            notifyItemRemoved(0);
//          notifyItemRangeRemoved(0, 1);
        }
        return imageList;
    }

    public ArrayList<String> updateList(int position) {
        Log.d(TAG,"position: " + position);
        if (!imageList.isEmpty() && imageList != null) {
            for (int i = 0; i < position; i++) {
                tempList.add(imageList.get(i));
            }
            imageList.removeAll(tempList);
            imageList.addAll(tempList);
            tempList.clear();

            try {
                notifyItemRangeRemoved(0, position);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EventBus.getDefault().post(new PositionEvent(position));
        return imageList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_module_four, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int i) {
        if (imageList != null && !imageList.isEmpty()) {
            imgPath = imageList.get( i % imageList.size());
        }
        try {
            Glide.with(DsdpsApp.getDsdpsApp())
                    .load(new File(imgPath))
    //               .asGif()
                    .asBitmap()//取消GIF播放
                    .dontAnimate()//取消动画
                    .thumbnail(0.1f)
    //                .diskCacheStrategy(DiskCacheStrategy.ALL)//缓存
                    .skipMemoryCache(true) // 不使用内存缓存
                    .diskCacheStrategy(DiskCacheStrategy.NONE)// 不使用磁盘缓存
                    .into(holder.imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemClickListener.onClick(holder.getAdapterPosition()%imageList.size());
            }
        });
//        if (i == 0) {
//            holder.ll_module_four.setBackgroundResource(R.drawable.bg_image_selected);
//        }else{
//            holder.ll_module_four.setBackground(null);
//        }

    }

    @Override
    public int getItemCount() {
        return MAX_VALUE;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private LinearLayout ll_module_four;

        public ViewHolder(View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.img);
            ll_module_four = itemView.findViewById(R.id.ll_module_four);
        }
    }

    private ItemClickListener itemClickListener;

    public interface ItemClickListener {
        void onClick(int position);
    }

    public void setItemClickListener(ItemClickListener listener) {
        itemClickListener = listener;
    }

}
