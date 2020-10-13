package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.customview.CircleImageView;
import com.xunixianshi.vrshow.obj.LeaveMessageResultList;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO 我的留言适配器
 *
 * @author MarkChang
 * @ClassName MyMessageAdapter
 * @time 2016/11/1 16:03
 */
public class MyMessageAdapter extends BaseAda<LeaveMessageResultList> {

    public MyMessageAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_message_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        LeaveMessageResultList item = getItem(position);
        PicassoUtil.loadImage(mContext, item.getCommenterIcon()+"?imageView2/2/w/" + ScreenUtils.dip2px(mContext,60)+100, viewHolder.my_message_item_icon_iv);
        viewHolder.my_message_item_name_tv.setText(item.getCommenterName());
        viewHolder.my_message_item_content_tv.setText(item.getMessageContent());
        viewHolder.my_message_item_time_tv.setText(item.getCreateTime());
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.my_message_item_icon_iv)
        CircleImageView my_message_item_icon_iv;
        @Bind(R.id.my_message_item_name_tv)
        TextView my_message_item_name_tv;
        @Bind(R.id.my_message_item_content_tv)
        TextView my_message_item_content_tv;
        @Bind(R.id.my_message_item_time_tv)
        TextView my_message_item_time_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}