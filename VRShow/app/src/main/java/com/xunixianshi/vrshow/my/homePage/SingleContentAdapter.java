package com.xunixianshi.vrshow.my.homePage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.obj.ContentReviewedListObj;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.recyclerview.VideoItemExpandable;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by duan on 2016/9/20.
 */
public class SingleContentAdapter extends RecyclerBaseAdapter<ContentReviewedListObj, SingleContentAdapter.SingleProductHolder> {
    private Context mContext;

    public SingleContentAdapter(Context context) {
        mContext = context;
    }


    @Override
    public RecyclerView.ViewHolder createRecyclerHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_user_content_list_holder, parent, false);
        SingleProductHolder singleProductHolder = new SingleProductHolder(v);
        return singleProductHolder;
    }

    @Override
    public void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, ContentReviewedListObj data) {
        SingleProductHolder singleProductHolder = (SingleProductHolder) viewHolder;
        if (data != null) {
            singleProductHolder.setHolderDate(data);
        }
    }

    public class SingleProductHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.user_content_holder_rl)
        RelativeLayout user_content_holder_rl;

        @Bind(R.id.user_content_holder_image_iv)
        ImageView user_content_holder_image_iv;

        @Bind(R.id.public_video_item_bottom_text_one_tv)
        TextView public_video_item_bottom_text_one_tv;

        @Bind(R.id.public_video_item_bottom_text_two_tv)
        TextView public_video_item_bottom_text_two_tv;

        @Bind(R.id.item_expandable_child_video_ve)
        VideoItemExpandable item_expandable_child_video_ve;


        public SingleProductHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setImageSize();
            initListener();
        }

        private void initListener() {
            user_content_holder_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerBaseItemOnClickListener != null) {
                        mRecyclerBaseItemOnClickListener.onClick(getAdapterPosition());
                    }
                }
            });
        }

        private void setImageSize() {
            int screenWith = ScreenUtils.getScreenWidth(mContext);
            ViewGroup.LayoutParams lp;
            lp = user_content_holder_rl.getLayoutParams();
            lp.width = screenWith;
            lp.height = (int) (screenWith * 0.56);
            user_content_holder_rl.setLayoutParams(lp);
        }

        public void setHolderDate(ContentReviewedListObj date) {
            PicassoUtil.loadImage(mContext, date.getCoverImgUrl() + "?imageView2/2/w/" + (ScreenUtils.getScreenWidth(mContext) + 120), user_content_holder_image_iv);
            public_video_item_bottom_text_one_tv.setText(date.getResourceName());
            public_video_item_bottom_text_two_tv.setText("播放次数：" + date.getCumulativeNum());

        }
    }
}
