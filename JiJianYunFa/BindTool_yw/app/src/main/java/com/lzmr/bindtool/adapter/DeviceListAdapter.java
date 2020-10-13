package com.lzmr.bindtool.adapter;

import java.util.HashMap;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.dto.DeviceListItemDto;
import com.lzkj.baize_android.utils.FileUtils;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.MapUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.ui.fragment.BaseFragment;
import com.lzmr.bindtool.ui.fragment.devices.AllDeviceFragment;
import com.lzmr.bindtool.ui.fragment.devices.DevicesManagerFragment;
import com.yqhd.baizelocationlib.CityDatabaseHandler;
import com.yqhd.baizelocationlib.entity.CityBo;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import static android.R.attr.id;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.V;
import static com.lzmr.bindtool.R.drawable.online_state_icon;

/**
 * 终端列表适配器
 *
 * @author longyihuang
 * @date 2016-3-11 下午3:03:05
 */
public class DeviceListAdapter extends BaseAdapter {
	private static final LogTag TAG = LogUtils.getLogTag(DeviceListAdapter.class.getSimpleName(), true);
	private LayoutInflater inflater;
	private Context context;
	private List<DeviceListItemDto> deviceList;
	private AllDeviceFragment mFragment;
	private HashMap<Integer,Boolean> selectMap;
	private CityDatabaseHandler cityHandler;
	public DeviceListAdapter(AllDeviceFragment fragment,Context context,List<DeviceListItemDto> deviceList) {
		this.mFragment = fragment;
		this.context = context;
		this.inflater = LayoutInflater.from(context);
		this.deviceList = deviceList;
		cityHandler = new CityDatabaseHandler(context);
		initSelectMap();
	}

	private void initSelectMap() {
		selectMap = new HashMap<Integer,Boolean>();
		for (int i = 0; i < deviceList.size(); i++) {
			selectMap.put(i,false);
		}
	}

	public void showDeviceByItemId(int id){
		LogUtils.i(LogUtils.getStackTraceElement(),"id:"+id);
		LogUtils.i(LogUtils.getStackTraceElement(),"selectMap.size"+selectMap.size());
		if (!MapUtils.isEmpty(selectMap)) {
			for (int i = 0; i < deviceList.size(); i++) {
				if (i == id && !selectMap.get(i)) {
					selectMap.put(i,true);
				}else{
					selectMap.put(i,false);
				}
			}

		}
		this.notifyDataSetChanged();
	}

	/**
	* @Title: updateAdapter
	* @Description: 更新数据
	* @return void    返回类型
	*/
	public void updateAdapter(List<DeviceListItemDto> deviceList){
		this.deviceList = deviceList;
		initSelectMap();
		this.notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = inflater.inflate(R.layout.device_item, null);
			holder.deviceName = (TextView) convertView.findViewById(R.id.txt_device_name);
			holder.deviceState = (ImageView) convertView.findViewById(R.id.img_device_state);
			holder.deviceManager = (ImageButton) convertView.findViewById(R.id.btn_device_manager);

			holder.mTxtDeviceName = (TextView) convertView.findViewById(R.id.txt_device_name2);
			holder.mTxtWorkTime = (TextView) convertView.findViewById(R.id.txt_work_time);
			holder.mTxtScreenDirection = (TextView) convertView.findViewById(R.id.txt_screen_direction);
			holder.mTxtDeviceLocation = (TextView) convertView.findViewById(R.id.txt_device_location);
			holder.mTxtStorageTotal = (TextView) convertView.findViewById(R.id.txt_storageTotal);
			holder.mTxtStorageUse = (TextView) convertView.findViewById(R.id.txt_storageUse);
			holder.mDeviceInfoLayout = (RelativeLayout) convertView.findViewById(R.id.rl_device_info);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final DeviceListItemDto deviecInfo = deviceList.get(position);
		boolean isShowDeviceInfo = selectMap.get(position);
		holder.deviceName.setText(deviecInfo.getName());

		holder.deviceManager.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				String deviceInfoStr = JSON.toJSONString(deviecInfo);
				bundle.putString("deviceInfo",deviceInfoStr);
				LogUtils.d(LogUtils.getStackTraceElement(),"deviceInfoStr:"+deviceInfoStr);
				if (mFragment!=null) {
					mFragment.switchDeviceSettingFragment(bundle);
				}

			}
		});
		holder.deviceState.setVisibility(View.VISIBLE);
		int deviceState = deviecInfo.getState();
		switch (deviceState) {
		case 1://在线
			holder.deviceState.setImageResource(R.drawable.icon_device_online);
			holder.deviceManager.setVisibility(View.VISIBLE);
			break;
		case 2://离线
			holder.deviceState.setImageResource(R.drawable.icon_device_online);
			holder.deviceState.setVisibility(View.INVISIBLE);
			holder.deviceManager.setVisibility(View.GONE);
			break;
		case 3://待机
			holder.deviceState.setImageResource(R.drawable.icon_device_standby);
			holder.deviceManager.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
		//设备信息
		if (isShowDeviceInfo) {
			holder.mDeviceInfoLayout.setVisibility(View.VISIBLE);
			//终端名称
			String deviceName = String.format(context.getString(R.string.device_name_value)
					, deviecInfo.getName());
			holder.mTxtDeviceName.setText(deviceName);
			//终端定位
			Long cityId = deviecInfo.getLocation();
			String cityName = null;
			if(null != cityId){
				CityBo cityBo = cityHandler.findCityByCityId(cityId.intValue());
				if (null != cityBo) {
					cityName = cityBo.getCityName();
				}
			}
			String location = String.format(context.getString(R.string.device_location_value)
					, cityName);
			holder.mTxtDeviceLocation.setText(location);
			//屏幕方向
			String screenDirection = String.format(context.getString(R.string.screen_direction_value)
					, deviecInfo.getDirectionName());
			holder.mTxtScreenDirection.setText(screenDirection);
			//工作时间
			String workTime = String.format(context.getString(R.string.work_time_value)
					, deviecInfo.getWorkTimeStart() + "-" + deviecInfo.getWorkTimeEnd());
			holder.mTxtWorkTime.setText(workTime);
			//总容量
			long storageTotalValue = 0L;
			Long storageTotal = deviecInfo.getStorageTotal();
			if (storageTotal!=null) {
				storageTotalValue = storageTotal.longValue() * 1024L;
			}
			String storaTotalStr = String.format(context.getString(R.string.storage_total_value)
					, FileUtils.formatByte(storageTotalValue));
			holder.mTxtStorageTotal.setText(storaTotalStr);

			//可用空间
			long storageUseValue = 0L;
			Long storageUse = deviecInfo.getStorageUse();
			if (storageUse != null) {
				storageUseValue = storageUse.longValue() * 1024L;
			}
			long storageAvailableUse = storageTotalValue - storageUseValue;
			String storaUseStr = String.format(context.getString(R.string.storage_use_value)
					, FileUtils.formatByte(storageAvailableUse));
			holder.mTxtStorageUse.setText(storaUseStr);
		}else{
			holder.mDeviceInfoLayout.setVisibility(View.GONE);
		}

		return convertView;
	}

	public final int getCount() {
		return deviceList == null ? 0 : deviceList.size();
	}

	public final Object getItem(int position) {
		return deviceList == null ? null : deviceList.get(position);
	}

	public final long getItemId(int position) {
		//去掉HeadView
		return position;
	}

	public final class ViewHolder {
		public TextView deviceName;
		public ImageView deviceState;
		public ImageButton deviceManager;
		public TextView mTxtDeviceName;
		public TextView mTxtWorkTime;
		public TextView mTxtScreenDirection;
		public TextView mTxtDeviceLocation;
		public TextView mTxtStorageTotal;
		public TextView mTxtStorageUse;
		public RelativeLayout mDeviceInfoLayout;
	}
}
