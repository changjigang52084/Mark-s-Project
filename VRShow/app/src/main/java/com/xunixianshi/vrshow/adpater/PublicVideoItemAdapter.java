package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.CommentListObj;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/14.
 */
public class PublicVideoItemAdapter extends BaseAda<CommentListObj> {

    public PublicVideoItemAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_public_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.public_video_item_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        viewHolder.public_video_item_icon_iv.setLayoutParams(lp);
        viewHolder.public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.public_video_item_icon_iv)
        ImageView public_video_item_icon_iv;
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

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
