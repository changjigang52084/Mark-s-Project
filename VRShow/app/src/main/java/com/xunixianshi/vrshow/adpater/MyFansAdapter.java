package com.xunixianshi.vrshow.adpater;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.view.CircleImageView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.UserConcernResultList;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * TODO 我的粉丝适配器
 *
 * @author MarkChang
 * @ClassName MyFansAdapter
 * @time 2016/11/1 16:04
 */
public class MyFansAdapter extends BaseAda<UserConcernResultList> {
    public MyFansAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.my_fans_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        UserConcernResultList item = getItem(position);
        PicassoUtil.loadImage(mContext, item.getUserIcon()+"?imageView2/2/w/" + ScreenUtils.dip2px(mContext,40)+100, viewHolder.my_fans_item_icon_civ);
        viewHolder.my_fans_item_name_tv.setText(item.getUserName());
        viewHolder.my_fans_item_content_tv.setText(item.getUpdateRemark());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.my_fans_item_icon_civ)
        CircleImageView my_fans_item_icon_civ;
        @Bind(R.id.my_fans_item_name_tv)
        TextView my_fans_item_name_tv;
        @Bind(R.id.my_fans_item_content_tv)
        TextView my_fans_item_content_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
