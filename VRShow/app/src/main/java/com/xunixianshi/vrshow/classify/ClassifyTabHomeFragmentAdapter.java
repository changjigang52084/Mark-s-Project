/**  
 * @Title: ClassifyTabHomeFragmentAdapter.java
 * @Package com.xunixianshi.vrshow.home
 * @Description: TODO(用一句话描述该文件做什么)
 * @author hechuang 
 * @date 2016年3月12日 下午12:32:09
 * @version V1.0  
 */
package com.xunixianshi.vrshow.classify;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.obj.HomeResultList;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.utils.PicassoUtil;

/**
 *  分类首页adapter
 * @ClassName ClassifyTabHomeFragmentAdapter
 *@author HeChuang
 *@time 2016/11/1 15:22
 */
public class ClassifyTabHomeFragmentAdapter extends BaseAda<HomeResultList> {
	
	private Context mContext;
	public ClassifyTabHomeFragmentAdapter(Context context) {
		super(context);
		this.mContext = context;
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
		final HomeResultList item = getItem(position);
		int screenWith = ScreenUtils.getScreenWidth(mContext);
		LayoutParams lp;
		lp = viewHolder.home_customer_icon.getLayoutParams();
		lp.width = screenWith/2;
		lp.height = (int) (screenWith/2*0.56);
		viewHolder.home_customer_icon.setLayoutParams(lp);
		PicassoUtil.loadImage(mContext,item.getCoverImgUrl() + "?imageView2/2/w/" + (lp.width + 120),
				viewHolder.home_customer_icon);
		viewHolder.home_customer_tv.setText(item.getResourceTitle());
		viewHolder.home_video_describe_tv.setText("播放次数："+item.getPlayerTotal());
		viewHolder.home_customer_icon.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(mContext, ClassifyVideoDetialActivity.class);
				Bundle bundle = new Bundle();
				bundle.putInt("videoTypeId",
						item.getResourceId());
				intent.putExtras(bundle);
				mContext.startActivity(intent);
			}
		});
		return convertView;
	}

	class ViewHolder {
		ImageView home_customer_icon;
		TextView home_customer_tv;
		TextView home_video_describe_tv;

		public ViewHolder(View convertView) {
			home_customer_icon = (ImageView) convertView
					.findViewById(R.id.home_customer_icon);
			home_customer_tv = (TextView) convertView
					.findViewById(R.id.home_customer_tv);
			home_video_describe_tv = (TextView) convertView
					.findViewById(R.id.home_video_describe_tv);
		}
	}
}
