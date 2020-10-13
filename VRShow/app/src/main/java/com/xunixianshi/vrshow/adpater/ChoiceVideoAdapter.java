package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.my.fragment.ChoiceVideoActivity;
import com.xunixianshi.vrshow.my.localVideo.entity.VideoBean;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO 本地视频选择适配器
 *
 * @author MarkChang
 * @ClassName ChoiceVideoAdapter
 * @time 2016/11/1 16:02
 */

public class ChoiceVideoAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<VideoBean> bitmaps;

    /**
     * 被点击的Item视频地址
     */
    public static final String CLICK_BEAN_VIDEO_PATH = "click_bean_video_path";

    public ChoiceVideoAdapter(Context context, ArrayList<VideoBean> bitmaps) {
        this.context = context;
        this.bitmaps = bitmaps;
    }

    @Override
    public int getCount() {
        return bitmaps == null ? 0 : bitmaps.size();
    }

    @Override
    public Object getItem(int position) {
        return bitmaps == null ? null : bitmaps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.choice_video_item, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int screenWidth = ScreenUtils.getScreenWidth(context);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.choice_video_item_iv.getLayoutParams();
        lp.width = screenWidth / 2;
        lp.height = screenWidth * 2 / 5;
        viewHolder.choice_video_item_iv.setLayoutParams(lp);
        if (bitmaps.get(position).getVideoBitmap() != null) {
            viewHolder.choice_video_item_iv.setImageBitmap(bitmaps.get(position).getVideoBitmap());
            viewHolder.choice_video_item_iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.putExtra(CLICK_BEAN_VIDEO_PATH, bitmaps.get(position).getVideoPath());
                    intent.setAction(ChoiceVideoActivity.SEND_LOAD_VIDEO_NOTICE);
                    // 发送广播
                    context.sendBroadcast(intent);
                }
            });
        }
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.choice_video_item_iv)
        ImageView choice_video_item_iv;

        public ViewHolder(View convertView) {
            ButterKnife.bind(this, convertView);
        }
    }
}
