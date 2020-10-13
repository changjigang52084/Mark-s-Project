package com.lzmr.bindtool.ui.fragment.devices;


import android.annotation.SuppressLint;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.util.Constants;

/**
 * 待机终端列表界面
 *
 * @author longyihuang
 * @date 2016-3-12 下午2:25:03
 */
@SuppressLint("ValidFragment")
public class StandbyDeviceFragment extends AllDeviceFragment  {
	private static final LogTag TAG = LogUtils.getLogTag(StandbyDeviceFragment.class.getSimpleName(), true);
	
	
	public StandbyDeviceFragment(ControlFragmentListener switchFragmentListener) {
		super(switchFragmentListener);
	}
	
//
	@Override
	public void loadData() {
		loadDeviceData(UPDATE_FLAG_REFRESH,currentPage,Constants.DEVICE_STATE_STANDBY,"StandbyDeviceFragment");
	}


	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		currentPage = 1;
	}

	@Override
	public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
		LogUtils.d(TAG, "onPullUpToRefresh", "Refresh data...");
		currentPage = 1;
		loadDeviceData(UPDATE_FLAG_REFRESH, currentPage,Constants.DEVICE_STATE_STANDBY,"StandbyDeviceFragment");
	}

	@Override
	public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
		loadDeviceData(UPDATE_FLAG_LOAD, ++currentPage,Constants.DEVICE_STATE_STANDBY,"StandbyDeviceFragment");
		LogUtils.d(TAG, "onPullUpToRefresh", "Load data...page number:"+currentPage);
	}

}