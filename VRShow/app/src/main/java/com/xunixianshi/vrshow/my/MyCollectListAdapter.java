package com.xunixianshi.vrshow.my;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.interfaces.DeleteItemInterface;
import com.xunixianshi.vrshow.obj.CollectResultDataList;
import com.xunixianshi.vrshow.obj.PlayHistoryResultDataList;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2016/9/21.
 */
public class MyCollectListAdapter extends BaseAda<CollectResultDataList> {
    private DeleteItemInterface mDeleteItemInterface;

    public MyCollectListAdapter(Context context) {
        super(context);
    }

    public void setDeleteInterface(DeleteItemInterface deleteItemInterface) {
        this.mDeleteItemInterface = deleteItemInterface;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_public_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final CollectResultDataList item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.public_video_item_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        viewHolder.public_video_item_icon_iv.setLayoutParams(lp);
        viewHolder.public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewHolder.public_video_item_more_operation_ll.setVisibility(View.VISIBLE);
            }
        });
        viewHolder.public_video_item_delete_iv.setVisibility(View.VISIBLE);
        viewHolder.public_video_item_bottom_text_right_ll.setVisibility(View.GONE);
        viewHolder.public_video_item_delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDeleteItemInterface.showDeleteDialog(item.getCollectId(), position);
            }
        });
        PicassoUtil.loadImage(mContext, item.getResourcesIcon() + "?imageView2/2/w/" + (lp.width + 120), viewHolder.public_video_item_icon_iv);
        viewHolder.public_video_item_bottom_text_one_tv.setText(item.getResourcesName());
        viewHolder.public_video_item_bottom_text_two_tv.setText("收藏时间：" + item.getCollectTime());
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
        @Bind(R.id.public_video_item_more_operation_ll)
        LinearLayout public_video_item_more_operation_ll;
        @Bind(R.id.public_video_item_bottom_text_right_ll)
        LinearLayout public_video_item_bottom_text_right_ll;
        @Bind(R.id.public_video_item_delete_iv)
        ImageView public_video_item_delete_iv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
