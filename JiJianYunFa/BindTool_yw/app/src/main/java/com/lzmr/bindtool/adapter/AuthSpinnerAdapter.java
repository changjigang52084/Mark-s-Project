package com.lzmr.bindtool.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baize.adpress.core.protocol.dto.AuthorizationListItemPackage;
import com.lzmr.bindtool.R;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年5月13日 上午10:37:28 
 * @version 1.0 
 * @parameter  
 */
public class AuthSpinnerAdapter extends BaseAdapter {
	private LayoutInflater layoutInflater;
	private List<AuthorizationListItemPackage> authorList;


	public AuthSpinnerAdapter(Context context, List<AuthorizationListItemPackage> authorList) {
		layoutInflater  = LayoutInflater.from(context);
		this.authorList = authorList;
	}
	
	@Override
	public int getCount() {
		return null == authorList ? 0 : authorList.size();
	}

	@Override
	public Object getItem(int position) {
		return null == authorList ? null : authorList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return null == authorList ? 0 : position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		AuthViewHolder authViewHolder = null;
		if (null == convertView) {
			convertView    = layoutInflater.inflate(R.layout.item_spinner_auth, parent, false);
			authViewHolder = new AuthViewHolder(convertView);
			convertView.setTag(authViewHolder);
		} else {
			authViewHolder = (AuthViewHolder)convertView.getTag();
		}
		if (null != authorList) {
			AuthorizationListItemPackage itemPackage = authorList.get(position);
			authViewHolder.authNameTv.setText(itemPackage.getCode());
			authViewHolder.authNameTv.setTag(itemPackage);
			Integer remainCapacity = itemPackage.getRemainCapacity();
			if (null == remainCapacity) {
				remainCapacity = 0;
			}
			authViewHolder.authRemainNumberTv.setText(String.valueOf(remainCapacity));
		}
		return convertView;
	}
	
	public static class AuthViewHolder {
		/***
		 * 授权名称
		 */
		public TextView authNameTv;
		/***
		 * 授权剩余数
		 */
		public TextView authRemainNumberTv;
		
		public AuthViewHolder(View convertView) {
			authRemainNumberTv = (TextView) convertView.findViewById(R.id.tv_auth_remain_capacity);
			authNameTv 		   = (TextView) convertView.findViewById(R.id.tv_auth_name);
		}
	}

}
