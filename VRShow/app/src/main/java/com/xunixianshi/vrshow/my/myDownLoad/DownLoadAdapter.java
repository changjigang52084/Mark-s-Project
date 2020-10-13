package com.xunixianshi.vrshow.my.myDownLoad;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.StringUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.interfaces.DownLoadInterface;
import com.xunixianshi.vrshow.player.VRPlayerActivity;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by duan on 2016/9/22.
 */
public class DownLoadAdapter extends RecyclerBaseAdapter<DownLoadItem, DownLoadAdapter.DownLoadVideoHolder> {

    private Context mContext;
    private DownLoadInterface mDownLoadInterface;

    public DownLoadAdapter(Context context, DownLoadInterface downLoadInterface) {
        this.mContext = context;

        this.setDownLoadInterface(downLoadInterface);
    }

    public void setDownLoadInterface(DownLoadInterface downLoadInterface) {
        mDownLoadInterface = downLoadInterface;
    }


    @Override
    public RecyclerView.ViewHolder createRecyclerHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(mContext).inflate(R.layout.item_user_video_download, parent, false);
        return new DownLoadVideoHolder(mContext, v);
    }

    @Override
    public void bindRecyclerHolder(RecyclerView.ViewHolder viewHolder, int RealPosition, DownLoadItem data) {
        DownLoadVideoHolder videoHolder = (DownLoadVideoHolder) viewHolder;
        videoHolder.refreshViewAndState(data);
    }

    public class DownLoadVideoHolder extends DownLoadBaseHolder {

        @Bind(R.id.down_load_image_iv)
        ImageView down_load_image_iv;

        @Bind(R.id.down_load_progressBar_rl)
        RelativeLayout down_load_progressBar_rl;

        @Bind(R.id.down_load_progressBar)
        ProgressBar down_load_progressBar;

        @Bind(R.id.down_load_progress_tv)
        TextView down_load_progress_tv;

        @Bind(R.id.down_load_image_window_rl)
        RelativeLayout down_load_image_window_rl;
        @Bind(R.id.down_load_btn_progress_rl)
        RelativeLayout down_load_btn_progress_rl;

        @Bind(R.id.down_load_bg_alpha_iv)
        ImageView down_load_bg_alpha_iv;

        @Bind(R.id.down_load_name_tv)
        TextView down_load_name_tv;

        @Bind(R.id.down_load_play_num_tv)
        TextView down_load_play_num_tv;

        @Bind(R.id.down_load_btn_iv)
        ImageView down_load_btn_iv;

        @Bind(R.id.down_load_arrow_down_black)
        ImageView down_load_arrow_down_black;

        @Bind(R.id.public_video_item_delete_iv)
        ImageView public_video_item_delete_iv;

        private int mTotalProgress = 100;
        private int mCurrentProgress;

        private String fileSize = "";
        private String mVideoType = "0";


        public DownLoadVideoHolder(Context context, View itemView) {
            super(context, itemView);
            ButterKnife.bind(this, itemView);
            setImageSize();
            down_load_image_window_rl.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (getDownLoadState() == DOWNLOAD_STATE_COMPLETE) {
                        if (!StringUtils.isBlank(filePath)) {
                            // 打开视频
                            VRPlayerActivity.intentTo(mContext, getDownLoadName(), filePath, Integer.valueOf(mVideoType), Integer.valueOf(getDownLoadResourcesID()), 0, false);
                        }
                    }
                }
            });
            public_video_item_delete_iv.setOnClickListener(new View.OnClickListener() {
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
            ViewGroup.LayoutParams btn_progress_lp;
            lp = down_load_image_window_rl.getLayoutParams();
            btn_progress_lp = down_load_btn_progress_rl.getLayoutParams();
            btn_progress_lp.width = lp.width = screenWith;
            btn_progress_lp.height = lp.height = (int) (screenWith * 0.56);
            down_load_image_window_rl.setLayoutParams(lp);
            down_load_btn_progress_rl.setLayoutParams(btn_progress_lp);

            ViewGroup.LayoutParams alpha_lp;
            alpha_lp = down_load_bg_alpha_iv.getLayoutParams();
            alpha_lp.width = screenWith;
            alpha_lp.height = (int) (screenWith * 0.56) + (int) (1 * ScreenUtils.getDensity(mContext));
            down_load_bg_alpha_iv.setLayoutParams(alpha_lp);
        }


        public void refreshViewAndState(DownLoadItem downloaditem) {
            down_load_name_tv.setText(downloaditem.getDownLoadName());
            down_load_play_num_tv.setVisibility(View.GONE);
            this.fileSize = downloaditem.getFileSize();
            PicassoUtil.loadImage(mContext, downloaditem.getDownLoadIconUrl(), down_load_image_iv);

            down_load_arrow_down_black.setVisibility(View.GONE);
            public_video_item_delete_iv.setVisibility(View.VISIBLE);
            this.mVideoType = downloaditem.getVideoType();

            this.refreshState(downloaditem.getDownLoadType(),
                    downloaditem.getResourcesId(),
                    downloaditem.getDownLoadName(),
                    downloaditem.getDownLoadIconUrl(),
                    downloaditem.getDownLoadUrl(), 0);
        }


        @Override
        public void notifyProductProgress(int state) {
            switch (state) {
                case DOWNLOAD_STATE_WAIT:
                    down_load_progressBar_rl.setVisibility(View.VISIBLE);
                    down_load_btn_iv.setVisibility(View.VISIBLE);
                    down_load_btn_iv.setImageResource(R.drawable.video_upload_download_pause);
                    break;
                case DOWNLOAD_STATE_DOWNING:
                    down_load_btn_iv.setVisibility(View.VISIBLE);
                    down_load_progressBar_rl.setVisibility(View.VISIBLE);
                    down_load_btn_iv.setImageResource(R.drawable.video_upload_download_pause);
                    break;
                case DOWNLOAD_STATE_PAUSE:
                    down_load_btn_iv.setVisibility(View.VISIBLE);
                    down_load_progressBar_rl.setVisibility(View.VISIBLE);
                    down_load_btn_iv.setImageResource(R.drawable.video_download_icon);
                    break;
                case DOWNLOAD_STATE_COMPLETE:
                    down_load_btn_iv.setVisibility(View.GONE);
                    down_load_progressBar_rl.setVisibility(View.INVISIBLE);
                    down_load_bg_alpha_iv.setVisibility(View.GONE);
                    break;
            }
        }

        @OnClick(R.id.down_load_btn_iv)
        void clickDownLoadButton() {
            if (getDownLoadState() == DOWNLOAD_STATE_PAUSE) {
                if (mDownLoadInterface != null && getDownloadId() != -1) {
                    mDownLoadInterface.reDownload(getDownLoadType(), Integer.valueOf(getDownLoadResourcesID()), getDownloadId());
                }
                setDownLoadState(DOWNLOAD_STATE_DOWNING);
                notifyProductProgress(getDownLoadState());
            } else if (getDownLoadState() == DOWNLOAD_STATE_WAIT) {
                pauseDownLoad();
            } else if (getDownLoadState() == DOWNLOAD_STATE_DOWNING) {
                pauseDownLoad();
            }

        }

        /**
         * 数据改变时候 重新调整下界面
         *
         * @author DuanChunLin
         * @time 2016/10/11 12:03
         */
        @Override
        public void notifyDataSetChanged() {
            down_load_progressBar_rl.setVisibility(View.VISIBLE);
            down_load_progress_tv.setText("0%");
            down_load_progressBar.setProgress(0);
            down_load_btn_iv.setVisibility(View.GONE);
            down_load_bg_alpha_iv.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgress(long soFarBytes, long totalBytes, long speed, int progress) {
            // TODO Auto-generated method stub
            if (totalBytes > 0) {
                String downloadScale = StringUtils.generateFileSize(soFarBytes)
                        + "/" + (totalBytes >= 0 ? StringUtils.generateFileSize(totalBytes) : fileSize);
//            this.user_download_progress_state.setText(downloadScale);
//            this.user_download_progressbar.setProgress(progress);
            }
            if (down_load_progressBar.getProgress() < progress) {
                down_load_progress_tv.setText(progress + "%");
                down_load_progressBar.setProgress(progress);
            }
        }
    }

}
