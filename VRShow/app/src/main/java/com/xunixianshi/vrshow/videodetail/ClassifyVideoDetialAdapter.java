package com.xunixianshi.vrshow.videodetail;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.ClassifyVideoDetailResultSimilarList;
import com.zhy.http.okhttp.utils.PicassoUtil;

/**
 * 视频详情页适配器
 *
 * @author HeChuang
 * @ClassName ClassifyVideoDetialAdapter
 * @time 2016/11/1 15:49
 *
 */
public class ClassifyVideoDetialAdapter extends BaseAda<ClassifyVideoDetailResultSimilarList> {
    private Context mContext;

    public ClassifyVideoDetialAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_classify_detail, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ClassifyVideoDetailResultSimilarList itemData = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        lp = viewHolder.classify_recommend_video_icon_iv
                .getLayoutParams();
        lp.width = (screenWith / 2) - ScreenUtils.dip2px(mContext, 1);
        lp.height = (int) ((screenWith / 2 - 50) * 0.56);
        viewHolder.classify_recommend_video_icon_iv.setLayoutParams(lp);


        ViewGroup.LayoutParams lp1;
        lp1 = viewHolder.classify_recommend_video_spit_vw
                .getLayoutParams();
        lp1.height = (int) ((screenWith / 2 - 50) * 0.56);
        viewHolder.classify_recommend_video_spit_vw.setLayoutParams(lp1);

        PicassoUtil.loadImage(mContext, itemData.getCoverImgUrl() + "?imageView2/2/w/" + (lp.width + 120), viewHolder.classify_recommend_video_icon_iv);
        viewHolder.classify_recommend_video_name_tv.setText(itemData.getResourceName());
//		viewHolder.classify_recommend_video_paly_count_tv.setText("播放和下载总量:"+itemData.getCumulativeNum()+"次");
//		viewHolder.classify_recommend_video_describe_tv.setText(itemData.getSmallIntroduce());
        return convertView;
    }

    class ViewHolder {
        ImageView classify_recommend_video_icon_iv;
        TextView classify_recommend_video_name_tv;
        TextView classify_recommend_video_spit_vw;

        //		TextView classify_recommend_video_paly_count_tv;
//		TextView classify_recommend_video_describe_tv;
        public ViewHolder(View convertView) {
            this.classify_recommend_video_icon_iv = (ImageView) convertView
                    .findViewById(R.id.classify_recommend_video_icon_iv);
            this.classify_recommend_video_name_tv = (TextView) convertView
                    .findViewById(R.id.classify_recommend_video_name_tv);

            this.classify_recommend_video_spit_vw = (TextView) convertView
                    .findViewById(R.id.classify_recommend_video_spit_vw);
//			this.classify_recommend_video_paly_count_tv = (TextView) convertView
//					.findViewById(R.id.classify_recommend_video_paly_count_tv);
//			this.classify_recommend_video_describe_tv = (TextView) convertView
//					.findViewById(R.id.classify_recommend_video_describe_tv);
        }
    }
}
