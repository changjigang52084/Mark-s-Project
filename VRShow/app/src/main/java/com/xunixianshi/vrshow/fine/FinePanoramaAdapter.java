package com.xunixianshi.vrshow.fine;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.squareup.picasso.Callback;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.obj.DelicacyListObj;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.xunixianshi.vrshow.recyclerview.VideoItemExpandable;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 精品页面列表适配器
 *
 * @author HeChuang
 * @ClassName FinePanoramaAdapter
 * @time 2016/11/1 15:33
 */
public class FinePanoramaAdapter extends RecyclerBaseAdapter<DelicacyListObj, FinePanoramaAdapter.FineVrPanoramaHolder> {

    private Context mContext;
    private RecyclerItemOnClickListener mRecyclerItemOnClickListener;

    public FinePanoramaAdapter(Context context) {
        this.mContext = context;

    }

    @Override
    public RecyclerView.ViewHolder createRecyclerHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_fine_panorama_image, parent, false);
        return new FineVrPanoramaHolder(mContext, v);
    }

    @Override
    public void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, DelicacyListObj data) {
        ((FineVrPanoramaHolder) viewHolder).setHolderDate(data);
    }

    public void setRecyclerItemOnClickListener(RecyclerItemOnClickListener recyclerItemOnClickListener) {
        mRecyclerItemOnClickListener = recyclerItemOnClickListener;
    }

    public class FineVrPanoramaHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.fine_panorama_rl)
        RelativeLayout fine_panorama_rl;

        @Bind(R.id.fine_panorama_image_iv)
        ImageView fine_panorama_image_iv;

        @Bind(R.id.fine_panorama_fl)
        FrameLayout fine_panorama_fl;

        @Bind(R.id.fine_transparent_iv)
        ImageView fine_transparent_iv;

        @Bind(R.id.public_video_item_bottom_text_one_tv)
        TextView public_video_item_bottom_text_one_tv;

        @Bind(R.id.public_video_item_bottom_text_two_tv)
        TextView public_video_item_bottom_text_two_tv;

        @Bind(R.id.public_video_item_more_operation_iv)
        ImageView public_video_item_more_operation_iv;

        @Bind(R.id.item_expandable_child_video_ve)
        VideoItemExpandable item_expandable_child_video_ve;

        private Context mContext;

        private boolean isVisible = false;
        private int mPanoramaImageLoadingState = -1;

        public FineVrPanoramaHolder(Context context, View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            mContext = context;
            setImageSize();
            fine_transparent_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerBaseItemOnClickListener != null) {
                        mRecyclerBaseItemOnClickListener.onClick(getAdapterPosition());
                    }
                }
            });
            public_video_item_more_operation_iv.setVisibility(View.VISIBLE);
            public_video_item_more_operation_iv.setImageResource(R.drawable.arrow_down);
            public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isVisible = !isVisible;
                    public_video_item_more_operation_iv.setImageResource(isVisible ? R.drawable.arrow_up : R.drawable.arrow_down);
                    item_expandable_child_video_ve.setVisibility(isVisible ? View.VISIBLE : View.GONE);
                }
            });
            item_expandable_child_video_ve.setRecyclerItemOnClickListener(mRecyclerItemOnClickListener);
        }

        private void setImageSize() {
            int screenWith = ScreenUtils.getScreenWidth(mContext);
            ViewGroup.LayoutParams lp;
            lp = fine_panorama_rl.getLayoutParams();
            lp.width = screenWith;
            lp.height = (int) (screenWith * 0.56);
            fine_panorama_rl.setLayoutParams(lp);
        }

        public void setHolderDate(DelicacyListObj date) {
            mPanoramaImageLoadingState = -1;
            PicassoUtil.loadImage(mContext, date.getResourceCoverImgUrl() + "?imageView2/2/w/" + (ScreenUtils.getScreenWidth(mContext) + 120), fine_panorama_image_iv, new Callback() {
                @Override
                public void onSuccess() {
                    mPanoramaImageLoadingState = 1;
                }

                @Override
                public void onError() {
                    mPanoramaImageLoadingState = 0;
                }
            });
            public_video_item_bottom_text_one_tv.setText(date.getResourceTitle());
            public_video_item_bottom_text_two_tv.setText("播放次数：" + date.getResourcePlayerTotal());
            item_expandable_child_video_ve.setItemDate(date, getAdapterPosition());
            fine_panorama_fl.removeAllViews();

            public_video_item_more_operation_iv.setImageResource(R.drawable.arrow_down);
            item_expandable_child_video_ve.setVisibility(View.GONE);
            isVisible = false;
        }


        public void addPanoramaView(FineVrPanoramaView panoramaImageView) {
            fine_panorama_fl.addView(panoramaImageView);
            panoramaImageView.setTag(getAdapterPosition());
        }

        public ImageView getFinePanoramaImage() {
            return fine_panorama_image_iv;
        }

        public int getPanoramaImageLoadingState() {
            return mPanoramaImageLoadingState;
        }

        public void removePanoramaView() {
            fine_panorama_fl.removeAllViews();
        }
    }
}
