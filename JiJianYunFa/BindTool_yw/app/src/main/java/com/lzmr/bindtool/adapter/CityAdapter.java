package com.lzmr.bindtool.adapter;

import java.util.List;

import com.lzmr.bindtool.R;
import com.yqhd.baizelocationlib.entity.CityBo;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class CityAdapter extends BaseAdapter {
	private List<CityBo> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public  CityAdapter(Context context,List<CityBo> citySet) {
		this.mContext = context;
		this.mList = citySet;
		this.mLayoutInflater = LayoutInflater.from(mContext);
	}
    
    public void updateData(List<CityBo> citySet) {
		this.mList = citySet;
		notifyDataSetChanged();
	}
	@Override
	public int getCount() {
		 return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		 return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.provice_item, null);
			holder.cityName = (TextView) convertView.findViewById(R.id.txt_proveice_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		CityBo city = mList.get(position);
		if(null != city){
			holder.cityName.setText(city.getCityName());
		}
        return convertView;
	}
	
	public final class ViewHolder {
		public TextView cityName;
	}

	
}
