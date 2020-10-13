package com.lzmr.bindtool.api.listener;

import com.baize.adpress.core.protocol.dto.DeviceListDto;

/**
 * 获取终端列表信息的回调接口
 *
 * @author longyihuang
 * @date 2016-3-11 上午11:35:05
 */
public interface GetDevicesInfoListener extends SessionRequestListener{
	/**
	 * 获取终端信息
	 * @param updateFlag 加载标记：下拉刷新或上拉加载
	 * @param devices 终端列表对象
     */
	 public void onGetDevicesInfo(int updateFlag, DeviceListDto devices);
}
