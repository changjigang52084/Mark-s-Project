package com.xunixianshi.vrshow.my.assemble;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.classify.ClassifyTypeListActivity;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleItemObj;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by duan on 2016/9/24.
 */

public class AssembleAdapter extends RecyclerBaseAdapter<AssembleItemObj, AssembleAdapter.AssembleHolder> {

    private Context mContext;
    private RecyclerItemOnClickListener mRecyclerItemOnClickListener;
    private int mMode;

    public AssembleAdapter(Context context) {
        mContext = context;
        mMode = 0;
    }

    public void setRecyclerItemOnClickListener(RecyclerItemOnClickListener recyclerItemOnClickListener) {
        mRecyclerItemOnClickListener = recyclerItemOnClickListener;
    }

    public void setMode(int mode) {
        mMode = mode;
    }

    @Override
    public RecyclerView.ViewHolder createRecyclerHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_public_item, parent, false);
        return new AssembleHolder(v, mContext);
    }

    @Override
    public void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, AssembleItemObj data) {
        ((AssembleHolder) viewHolder).setHolderData(data, mMode);
    }

    public class AssembleHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.public_video_item_icon_iv)
        ImageView public_video_item_icon_iv;
        @Bind(R.id.public_video_item_shenhe_iv)
        ImageView public_video_item_shenhe_iv;
        @Bind(R.id.public_video_item_tab_icon_iv)
        ImageView public_video_item_tab_icon_iv;
        @Bind(R.id.public_video_item_bottom_text_one_tv)
        TextView public_video_item_bottom_text_one_tv;
        @Bind(R.id.public_video_item_bottom_text_two_tv)
        TextView public_video_item_bottom_text_two_tv;
        @Bind(R.id.public_video_item_bottom_text_three_tv)
        TextView public_video_item_bottom_text_three_tv;
        @Bind(R.id.public_video_item_bottom_text_four_tv)
        TextView public_video_item_bottom_text_four_tv;
        @Bind(R.id.public_video_item_more_operation_iv)
        ImageView public_video_item_more_operation_iv;
        @Bind(R.id.public_video_item_bottom_lv_iv)
        ImageView public_video_item_bottom_lv_iv;
        @Bind(R.id.public_video_item_bottom_people_iv)
        ImageView public_video_item_bottom_people_iv;
        @Bind(R.id.public_video_item_more_operation_ll)
        LinearLayout public_video_item_more_operation_ll;
        @Bind(R.id.public_video_item_bottom_text_left_ll)
        LinearLayout public_video_item_bottom_text_left_ll;
        @Bind(R.id.public_video_item_bottom_text_right_ll)
        LinearLayout public_video_item_bottom_text_right_ll;

        @Bind(R.id.public_video_item_delete_iv)
        ImageView public_video_item_delete_iv;
        private Context mContext;

        public AssembleHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            setImageSize();
        }

        @OnClick({R.id.public_video_item_icon_iv, R.id.public_video_item_delete_iv})
        void onClick(View view) {
            switch (view.getId()) {
                case R.id.public_video_item_icon_iv:
                    if (mRecyclerItemOnClickListener != null) {
                        mRecyclerItemOnClickListener.onClick(getAdapterPosition(), 1, null);
                    }
                    break;
                case R.id.public_video_item_delete_iv:
                    if (mRecyclerItemOnClickListener != null) {
                        mRecyclerItemOnClickListener.onClick(getAdapterPosition(), 2, null);
                    }
                    break;
            }
        }

        private void setImageSize() {
            int screenWith = ScreenUtils.getScreenWidth(mContext);
            ViewGroup.LayoutParams lp;
            lp = public_video_item_icon_iv.getLayoutParams();
            lp.width = screenWith;
            lp.height = (int) (screenWith * 0.56);
            public_video_item_icon_iv.setLayoutParams(lp);
        }

        public void setHolderData(AssembleItemObj item, int mode) {
            public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (public_video_item_more_operation_ll.getVisibility() == View.GONE) {
                        public_video_item_more_operation_ll.setVisibility(View.VISIBLE);
                    } else {
                        public_video_item_more_operation_ll.setVisibility(View.GONE);
                    }
                }
            });
            public_video_item_delete_iv.setVisibility(mode == 0 ? View.VISIBLE : View.GONE);
            PicassoUtil.loadImage(mContext, item.getCoverImgUrl() + "?imageView2/2/w/" + (120 + ScreenUtils.getScreenWidth(mContext) / 2), public_video_item_icon_iv);
            public_video_item_tab_icon_iv.setVisibility(View.VISIBLE);
            public_video_item_bottom_text_one_tv.setText(item.getCompilationName());
            public_video_item_bottom_text_two_tv.setText("播放总量：" + item.getResourceReadTotal());
            public_video_item_bottom_text_right_ll.setVisibility(View.GONE);
            if(item.getReviewedStatus() != 1){
                public_video_item_shenhe_iv.setVisibility(View.VISIBLE);
            }else{
                public_video_item_shenhe_iv.setVisibility(View.GONE);
            }
        }
    }
}
