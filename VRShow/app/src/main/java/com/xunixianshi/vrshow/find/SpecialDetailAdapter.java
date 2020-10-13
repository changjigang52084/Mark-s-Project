package com.xunixianshi.vrshow.find;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.FindSpecialDetailListObj;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 专题详情列表数据适配器
 * @ClassName SpecialDetailAdapter
 *@author HeChuang
 *@time 2016/11/1 15:28
 */
public class SpecialDetailAdapter extends BaseAda<FindSpecialDetailListObj> {

    public SpecialDetailAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_home_customer, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        FindSpecialDetailListObj item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.home_customer_icon.getLayoutParams();
        lp.width = screenWith/2;
        lp.height = (int) (screenWith/2*0.5);
        viewHolder.home_customer_icon.setLayoutParams(lp);
        if(position%2 == 0){
            viewHolder.home_customer_icon.setPadding(0,0,3,0);
        }else{
            viewHolder.home_customer_icon.setPadding(3,0,0,0);
        }
        PicassoUtil.loadImage(mContext,item.getResourceCoverImgUrl()+"?imageView2/2/w/" + (lp.width + 120), viewHolder.home_customer_icon);
        viewHolder.home_customer_tv.setText(item.getResourceTitle());
        viewHolder.home_video_describe_tv.setText("播放次数："+item.getResourcePlayerTotal());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.home_customer_icon)
        ImageView home_customer_icon;
        @Bind(R.id.home_customer_tv)
        TextView home_customer_tv;
        @Bind(R.id.home_video_describe_tv)
        TextView home_video_describe_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
