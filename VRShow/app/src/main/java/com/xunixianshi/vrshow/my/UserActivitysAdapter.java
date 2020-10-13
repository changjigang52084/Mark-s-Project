package com.xunixianshi.vrshow.my;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.ActivitysListResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResultDataList;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/21.
 */
public class UserActivitysAdapter extends BaseAda<ActivitysListResult> {
    public UserActivitysAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_activitys_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ActivitysListResult item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.public_video_item_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        viewHolder.public_video_item_icon_iv.setLayoutParams(lp);
        PicassoUtil.loadImage(mContext, item.getActiveCoverUrl() + "?imageView2/2/w/" + (lp.width + 120), viewHolder.public_video_item_icon_iv);
        viewHolder.item_activitys_name_tv.setText(item.getActiveTitle());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.public_video_item_icon_iv)
        ImageView public_video_item_icon_iv;
        @Bind(R.id.item_activitys_name_tv)
        TextView item_activitys_name_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
