package com.xunixianshi.vrshow.classify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.search.SearchResultListObj;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * 搜索结果返回结果列表适配器
 * @ClassName SearchResultAdapter
 *@author HeChuang
 *@time 2016/11/1 15:24
 */
public class SearchResultAdapter extends BaseAda<SearchResultListObj> {

    private Context mContext;

    public SearchResultAdapter(Context context) {
        super(context);
        mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_search_result, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final SearchResultListObj item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.item_search_result_image_iv.getLayoutParams();
        lp.width = screenWith / 3;
        lp.height = (int) (lp.width * 0.56);
        viewHolder.item_search_result_image_iv.setLayoutParams(lp);
        PicassoUtil.loadImage(mContext,item.getCoverImgUrl()+"?imageView2/2/w/" + (lp.width + 120), viewHolder.item_search_result_image_iv);
        viewHolder.item_search_result_title_iv.setText(item.getResourceName());
        viewHolder.item_search_result_subtitle_tv.setText("播放次数:" + item.getCumulativeNum());
        viewHolder.item_search_result_date_tv.setText(item.getCreateTime());
        viewHolder.item_search_result_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 搜索到的視頻點擊跳轉到播放頁面，把視頻ID傳遞過去
                Intent intent = new Intent();
                intent.setClass(mContext, ClassifyVideoDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("videoTypeId",
                        item.getResourceId());
                intent.putExtras(bundle);
                mContext.startActivity(intent);
            }
        });
//        viewHolder.item_search_result_more_operation_iv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 以后可能还会有其他操作，先写个按钮到这里
//                showToastMsg("以后可能还会有其他操作，先写个按钮到这里");
//            }
//        });
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.item_search_result_rl)
        RelativeLayout item_search_result_rl;
        @Bind(R.id.item_search_result_image_iv)
        ImageView item_search_result_image_iv;
        @Bind(R.id.item_search_result_title_iv)
        TextView item_search_result_title_iv;
        @Bind(R.id.item_search_result_subtitle_tv)
        TextView item_search_result_subtitle_tv;
        @Bind(R.id.item_search_result_date_tv)
        TextView item_search_result_date_tv;
        @Bind(R.id.item_search_result_more_operation_iv)
        ImageView item_search_result_more_operation_iv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}