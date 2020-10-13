package com.xunixianshi.vrshow.util;


import android.content.Context;
import android.view.LayoutInflater;
import android.widget.ImageView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.zhy.http.okhttp.utils.PicassoUtil;

/**
 * ImageView创建工厂
 */
public class ViewFactory {
	/**
	 * 获取ImageView视图的同时加载显示url
	 *
	 * @return
	 */
	public static ImageView getImageView(Context context, String url) {
		ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
		PicassoUtil.loadImage(context,url+"?imageView2/2/w/"+ (100 + ScreenUtils.getScreenWidth(context)), imageView);
		return imageView;
	}

	public static ImageView getImageView(Context context,int resId){
		ImageView imageView = (ImageView) LayoutInflater.from(context).inflate(
				R.layout.view_banner, null);
		imageView.setImageResource(resId);
		return imageView;
	}

}
