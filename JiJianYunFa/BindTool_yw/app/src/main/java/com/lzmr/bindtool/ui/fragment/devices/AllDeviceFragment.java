package com.lzmr.bindtool.ui.fragment.devices;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baize.adpress.core.protocol.dto.DeviceListDto;
import com.baize.adpress.core.protocol.dto.DeviceListItemDto;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.lzkj.baize_android.utils.ListUtils;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.adapter.DeviceListAdapter;
import com.lzmr.bindtool.api.DevicesInfoManager;
import com.lzmr.bindtool.api.listener.GetDevicesInfoListener;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.ui.fragment.BaseFragment;
import com.lzmr.bindtool.util.Constants;
import com.lzmr.bindtool.util.LogoutUtil;

/**
 * 所有终端列表界面
 *
 * @author longyihuang
 * @date 2016-3-12 下午1:49:26
 */
@SuppressLint("ValidFragment")
public class AllDeviceFragment extends LazyLoadFragment
	implements  OnItemClickListener, OnRefreshListener2<ListView>, GetDevicesInfoListener {
	private static final LogTag TAG = LogUtils.getLogTag(AllDeviceFragment.class.getSimpleName(), true);
	/**
	 * 刷新数据标签
	 */
	public static final int UPDATE_FLAG_REFRESH = 0;
	/**
	 * 加载数据标签
	 */
	public static final int UPDATE_FLAG_LOAD = 1;
	
	/**终端ListView**/
	protected PullToRefreshListView pullToRefreshListView;
    
	protected List<DeviceListItemDto> deviceList;
    
	protected DeviceListAdapter adapter;
	
	/**
	 * 当前页码
	 */
	protected int currentPage = 1;
	public AllDeviceFragment(ControlFragmentListener switchFragmentListener) {
		super(switchFragmentListener);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		deviceList = new ArrayList<DeviceListItemDto>();
	}

//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		if(view == null){
//			view = inflater.inflate(R.layout.fragment_all_devices, null);
//			pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_list_view);
//			pullToRefreshListView.setOnRefreshListener(this);
//			pullToRefreshListView.setOnItemClickListener(this);
//		}
//		isViewCreated = true;
//		loadDeviceData(UPDATE_FLAG_REFRESH,currentPage,0);
////		startProgressDialog(StringUtils.getString(getActivity(), R.string.wait), false);
//		return view;
//	}

	@Override
	public int getLayout() {
		return R.layout.fragment_all_devices;
	}

	@Override
	public void initViews(View view) {
		pullToRefreshListView = (PullToRefreshListView) view.findViewById(R.id.pull_list_view);
		pullToRefreshListView.setOnRefreshListener(this);
		pullToRefreshListView.setOnItemClickListener(this);
	}

	@Override
	public void loadData() {
		loadDeviceData(UPDATE_FLAG_REFRESH,currentPage,Constants.DEVICE_STATE_ALL,"AllDeviceFragment");
	}



	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		currentPage = 1;
	}
	
	/** 
	* @Title: updateListView 
	* @Description: 更新listview
	*/ 
	protected void updateListView(List<DeviceListItemDto> deviceList) {
		LogUtils.d(TAG, "updateListView", "deviceList:"+deviceList.size());
		if(adapter == null){
			adapter = new DeviceListAdapter(this,getActivity(), deviceList);
			pullToRefreshListView.setAdapter(adapter);
		}else{
			adapter.updateAdapter(deviceList);
		}
	}
	
	/** 
	* @Title: loadDeviceData 
	* @Description: 加载终端列表信息
	* @param  updateFlag 加载标签
	* @param  pageNumber 页码
	* @param  state 终端状态 0-所有 1-工作中  2-离线 3-待机
	* @return void    返回类型 
	*/ 
	protected void loadDeviceData(int updateFlag,int pageNumber,int state,String className) {
		LogUtils.i(LogUtils.getStackTraceElement(),"class:"+className);
		//刷新数据，把当前页重置
		if(UPDATE_FLAG_REFRESH == updateFlag){
			currentPage = 1;
		}
		DevicesInfoManager devicesInfoManager = new DevicesInfoManager(
				this, updateFlag, pageNumber, Constants.PAGE_DEVICES_SIZE, state);
		devicesInfoManager.loadDevices();
	}

	public void switchDeviceSettingFragment(Bundle bundle){
		this.switchDeviceSetting(bundle,true);
	}


	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (null!=adapter) {
			adapter.showDeviceByItemId((int)id);
		}
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		LogUtils.d(TAG, "onPullUpToRefresh", "Refresh data...");
		loadDeviceData(UPDATE_FLAG_REFRESH, 1,Constants.DEVICE_STATE_ALL,"AllDeviceFragment");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadDeviceData(UPDATE_FLAG_LOAD, ++currentPage,Constants.DEVICE_STATE_ALL,"AllDeviceFragment");
		LogUtils.d(TAG, "onPullUpToRefresh", "Load data...page number:"+currentPage);
	}

	@Override
	public void onGetDevicesInfo(int updateFlag,DeviceListDto devices) {
//		AllDeviceFragment.this.dismissProgressDialog();
		pullToRefreshListView.onRefreshComplete();
		if(null == devices){
			LogUtils.e(TAG, "onGetDevicesInfo", "Load device data failed.");
			return;
		}
		List<DeviceListItemDto> rows = devices.getRows();
		switch (updateFlag) {
		case UPDATE_FLAG_REFRESH:
			deviceList.clear();
			deviceList.addAll(rows);
			updateListView(deviceList);
			break;
		case UPDATE_FLAG_LOAD:
			if(ListUtils.isEmpty(rows)){
				return;
			}
			deviceList.addAll(rows);
			updateListView(deviceList);
			break;
		default:
			break;
		}
	}

	@Override
	public void onSuccess(String msg) {
	}


	@Override
	public void onFailure(String msg) {
		pullToRefreshListView.onRefreshComplete();
		showDialog(msg, null);
	}

	@Override
	public void onSessionInvalid() {
		switchLoginAndCloseOther(true);
	}
}
