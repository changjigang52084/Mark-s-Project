package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.ResourcesCommentResultList;
import com.xunixianshi.vrshow.obj.UserCommentListResult;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by markIron on 2016/9/18.
 */
public class MyContentItemAdapter extends BaseAda<UserCommentListResult> {

    public MyContentItemAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_my_content, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserCommentListResult item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.public_video_item_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        viewHolder.public_video_item_icon_iv.setLayoutParams(lp);
        PicassoUtil.loadImage(mContext,item.getResourceCoverImgUrl() + "?imageView2/2/w/" + (lp.width + 100), viewHolder.public_video_item_icon_iv);
        viewHolder.public_video_item_bottom_text_one_tv.setText(
                item.getResourceTitle());
        viewHolder.public_video_item_bottom_text_two_tv.setText(
                "播放次数：" + item.getResourcePlayerTotal());
        return convertView;
    }


    static class ViewHolder {
        @Bind(R.id.public_video_item_icon_iv)
        ImageView public_video_item_icon_iv;
        @Bind(R.id.public_video_item_bottom_text_one_tv)
        TextView public_video_item_bottom_text_one_tv;
        @Bind(R.id.public_video_item_bottom_text_two_tv)
        TextView public_video_item_bottom_text_two_tv;
        @Bind(R.id.public_video_item_center_content_rl)
        RelativeLayout public_video_item_center_content_rl;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
