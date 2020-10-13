package com.lzmr.bindtool.adapter;

import java.util.List;

import com.lzmr.bindtool.R;
import com.lzmr.bindtool.adapter.DeviceListAdapter.ViewHolder;
import com.yqhd.baizelocationlib.entity.ProvinceBo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ProvinceAdapter extends BaseAdapter {
	private List<ProvinceBo> mList;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    public  ProvinceAdapter(Context context,List<ProvinceBo> data) {
		this.mContext = context;
		this.mList = data;
		this.mLayoutInflater = LayoutInflater.from(mContext);
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
			holder.proviceName = (TextView) convertView.findViewById(R.id.txt_proveice_name);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ProvinceBo provinceBo = mList.get(position);
		if(null != provinceBo){
			holder.proviceName.setText(provinceBo.getProName());
		}
        return convertView;
	}
	
	public final class ViewHolder {
		public TextView proviceName;
	}
}
