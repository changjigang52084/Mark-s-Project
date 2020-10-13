package com.xunixianshi.vrshow.my.assemble.detail;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleContentItemObj;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by duan on 2016/9/24.
 */

public class AssembleDetailsContentAdapter extends RecyclerBaseAdapter<AssembleContentItemObj, AssembleDetailsContentAdapter.AssembleContentHolder> {

    private Context mContext;
    private RecyclerItemOnClickListener mRecyclerItemOnClickListener;

    public AssembleDetailsContentAdapter(Context context) {
        mContext = context;
    }

    @Override
    public AssembleContentHolder createRecyclerHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_assemble_detail_list_content, parent, false);
        return new AssembleContentHolder(v, mContext);
    }

    public void setRecyclerItemOnClickListener(RecyclerItemOnClickListener recyclerItemOnClickListener) {
        mRecyclerItemOnClickListener = recyclerItemOnClickListener;
    }

    @Override
    public void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, AssembleContentItemObj data) {
        ((AssembleContentHolder) viewHolder).setHolderData(data);
    }

    public class AssembleContentHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.assemble_detail_list_item_icon_iv)
        ImageView assemble_detail_list_item_icon_iv;

        @Bind(R.id.assemble_detail_list_bottom_text_one_tv)
        TextView assemble_detail_list_bottom_text_one_tv;

        @Bind(R.id.assemble_detail_list_bottom_text_two_tv)
        TextView assemble_detail_list_bottom_text_two_tv;

        private Context mContext;

        public AssembleContentHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            setImageSize();
        }

        private void setImageSize() {
            int screenWith = ScreenUtils.getScreenWidth(mContext);
            ViewGroup.LayoutParams lp;
            lp = assemble_detail_list_item_icon_iv.getLayoutParams();
            lp.width = screenWith;
            lp.height = (int) (screenWith * 0.56);
            assemble_detail_list_item_icon_iv.setLayoutParams(lp);
        }

        public void setHolderData(AssembleContentItemObj item) {
            PicassoUtil.loadImage(mContext, item.getCoverImgUrl() + "?imageView2/2/w/" + (ScreenUtils.getScreenWidth(mContext) / 2 + 120), assemble_detail_list_item_icon_iv);
            assemble_detail_list_bottom_text_one_tv.setText(item.getResourceTitle());
            assemble_detail_list_bottom_text_two_tv.setText("播放次数：" + item.getResourceReadTotal());
        }

        @OnClick({R.id.assemble_detail_list_item_icon_iv})
        void onClick(View view) {
            switch (view.getId()) {
                case R.id.assemble_detail_list_item_icon_iv:
                    if (mRecyclerItemOnClickListener != null) {
                        mRecyclerItemOnClickListener.onClick(getAdapterPosition(), 1, null);
                    }
                    break;
            }
        }
    }
}