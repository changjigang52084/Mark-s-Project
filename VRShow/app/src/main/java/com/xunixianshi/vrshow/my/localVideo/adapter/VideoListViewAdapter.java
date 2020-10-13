package com.xunixianshi.vrshow.my.localVideo.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.my.localVideo.entity.VideoBean;
import com.xunixianshi.vrshow.my.localVideo.util.LocalVideoUtil;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.util.cache.CacheCallBack;
import com.xunixianshi.vrshow.util.cache.CacheUtil;
import com.zhy.http.okhttp.utils.PicassoUtil;

import java.io.File;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.provider.MediaStore.Video.Thumbnails.MINI_KIND;

/**
 * 视频列表适配器
 * Created by markIron on 2016/9/7.
 */
public class VideoListViewAdapter extends RecyclerBaseAdapter<VideoBean, VideoListViewAdapter.LocalVideoHolder> {
    protected Context mContext;
    private Handler mHandler;
    private RecyclerItemOnClickListener mRecyclerItemOnClickListener;

    public VideoListViewAdapter(Context context) {
        this.mContext = context;
    }

    public VideoListViewAdapter(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    @Override
    public RecyclerView.ViewHolder createRecyclerHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_user_video_download, parent, false);
        return new LocalVideoHolder(v);
    }

    @Override
    public void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, VideoBean data) {
        ((LocalVideoHolder) viewHolder).refreshViewAndState(data);
    }

    public void setRecyclerItemOnClickListener(RecyclerItemOnClickListener recyclerItemOnClickListener) {
        mRecyclerItemOnClickListener = recyclerItemOnClickListener;
    }

    public class LocalVideoHolder extends RecyclerView.ViewHolder {
        @Bind(R.id.down_load_image_iv)
        ImageView down_load_image_iv;

        @Bind(R.id.down_load_image_window_rl)
        RelativeLayout down_load_image_window_rl;

        @Bind(R.id.down_load_btn_progress_rl)
        RelativeLayout down_load_btn_progress_rl;

        @Bind(R.id.down_load_more_operation_rl)
        RelativeLayout down_load_more_operation_rl;

        @Bind(R.id.down_load_bg_alpha_iv)
        ImageView down_load_bg_alpha_iv;

        @Bind(R.id.down_load_name_tv)
        TextView down_load_name_tv;

        @Bind(R.id.down_load_play_num_tv)
        TextView down_load_play_num_tv;

        @Bind(R.id.down_load_arrow_down_black)
        ImageView down_load_arrow_down_black;

        @Bind(R.id.public_video_item_delete_iv)
        ImageView public_video_item_delete_iv;
        @Bind(R.id.down_load_btn_iv)
        ImageView down_load_btn_iv;

        private int mWidth;
        private int mHeight;


        public LocalVideoHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            setImageSize();
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerItemOnClickListener != null) {
                        mRecyclerItemOnClickListener.onClick(getAdapterPosition(), 1, null);
                    }
                }
            });

            public_video_item_delete_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mRecyclerItemOnClickListener != null) {
                        mRecyclerItemOnClickListener.onClick(getAdapterPosition(), 2, null);
                    }
                }
            });
        }

        private void setImageSize() {
            int screenWith = ScreenUtils.getScreenWidth(mContext);
            ViewGroup.LayoutParams lp;
            lp = down_load_image_window_rl.getLayoutParams();
            lp.width = mWidth = screenWith;
            lp.height = mHeight = (int) (screenWith * 0.56);
            down_load_image_window_rl.setLayoutParams(lp);
        }

        private void refreshViewAndState(VideoBean videoBean) {
            down_load_btn_progress_rl.setVisibility(View.GONE);
            down_load_btn_iv.setVisibility(View.GONE);
            down_load_play_num_tv.setVisibility(View.GONE);
            down_load_bg_alpha_iv.setVisibility(View.GONE);
            down_load_arrow_down_black.setVisibility(View.GONE);
            public_video_item_delete_iv.setVisibility(View.VISIBLE);
            File cacheFile = CacheUtil.getInstance().getFromCacheImage(videoBean.getVideoPath());
            if (cacheFile != null && cacheFile.exists()) {
                PicassoUtil.loadImage(mContext, cacheFile, down_load_image_iv);
            } else {
                down_load_image_iv.setImageResource(R.drawable.image_defult_icon_small);
                new LoadCacheThread(mHandler, getAdapterPosition(), videoBean.getVideoPath(), mWidth, mHeight).start();
            }
            down_load_name_tv.setText(StringUtil.isEmpty(videoBean.getVideoTitle()) ? "未知视频" : videoBean.getVideoTitle());
        }
    }

    private static class LoadCacheThread extends Thread {
        private WeakReference<Handler> mHandler;
        private String videoPath;
        private int width;
        private int height;
        private int position;

        public LoadCacheThread(Handler handler, int position, String videoPath, int width, int height) {
            mHandler = new WeakReference<Handler>(handler);
            this.videoPath = videoPath;
            this.width = width;
            this.height = height;
            this.position = position;
        }

        @Override
        public void run() {
            Bitmap bitmap = LocalVideoUtil.getVideoThumbnail(videoPath, width, height, MINI_KIND);
            if (bitmap != null) {
                CacheUtil.getInstance().putImageToCache(bitmap, videoPath, new CacheCallBack() {
                    @Override
                    public void onBefore(Bitmap bitmap) {
                    }
                    @Override
                    public void onAfter(String path) {
                        if (mHandler != null && mHandler.get() != null && !StringUtil.isEmpty(path)) {
                            Handler handler = mHandler.get();
                            Message message = handler.obtainMessage();
                            message.what = 1;
                            message.arg1 = position;
                            handler.sendMessage(message);
                        }
                    }
                });
            }
        }
    }
}
