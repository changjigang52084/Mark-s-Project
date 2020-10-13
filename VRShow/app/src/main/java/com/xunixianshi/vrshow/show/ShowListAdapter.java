package com.xunixianshi.vrshow.show;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.ClassifyVideoTypeResultResourcesList;
import com.xunixianshi.vrshow.obj.MainShowResultListObj;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * show模块列表适配器
 * @ClassName ShowListAdapter
 *@author HeChuang
 *@time 2016/11/1 15:46
 */
public class ShowListAdapter extends BaseAda<MainShowResultListObj> {

    public ShowListAdapter(Context context) {
        super(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_public_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        MainShowResultListObj item= getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.public_video_item_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith);
        viewHolder.public_video_item_icon_iv.setLayoutParams(lp);

        viewHolder.public_video_item_more_operation_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.public_video_item_more_operation_ll.getVisibility() == View.GONE){
                    viewHolder.public_video_item_more_operation_ll.setVisibility(View.VISIBLE);
                }else{
                    viewHolder.public_video_item_more_operation_ll.setVisibility(View.GONE);
                }
            }
        });
        PicassoUtil.loadImage(mContext,item.getShowCoverImg()+ "?imageView2/2/w/" + (lp.width + 120), viewHolder.public_video_item_icon_iv);
        String levelNum;
        if(StringUtil.isEmpty(item.getShowLevel())){
            levelNum = "0";
        }else{
            levelNum = item.getShowLevel();
        }
        viewHolder.public_video_item_bottom_text_one_tv.setText(item.getShowName());
        viewHolder.public_video_item_bottom_text_one_lever_tv.setText("      LV:"+levelNum);
        viewHolder.public_video_item_bottom_text_two_tv.setText("作品数量："+item.getResourceTotal());
        viewHolder.public_video_item_bottom_text_four_tv.setText("人气："+item.getResourceTotalPlayer());
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.public_video_item_icon_iv)
        ImageView public_video_item_icon_iv;
        @Bind(R.id.public_video_item_bottom_text_one_tv)
        TextView public_video_item_bottom_text_one_tv;
        @Bind(R.id.public_video_item_bottom_text_two_tv)
        TextView public_video_item_bottom_text_two_tv;
        @Bind(R.id.public_video_item_bottom_text_one_lever_tv)
        TextView public_video_item_bottom_text_one_lever_tv;
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
        @Bind(R.id.public_video_item_bottom_text_left_ll)
        LinearLayout public_video_item_bottom_text_left_ll;
        @Bind(R.id.public_video_item_bottom_text_right_ll)
        LinearLayout public_video_item_bottom_text_right_ll;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
