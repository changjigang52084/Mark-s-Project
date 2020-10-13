package com.lzkj.ui.receiver;

import com.lzkj.ui.util.SharedUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年6月27日 上午10:24:40 
 * @version 1.0 
 * @parameter  更新epost在广告机上面显示的权重比例
 */
public class UpdateWeightReceiver extends BroadcastReceiver {
	/**eposter 的显示权重*/
	private static final String EPOSTER_WEIGHT = "eposterWeight";
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			float weight = intent.getFloatExtra(EPOSTER_WEIGHT, SharedUtil.DEFAULT_WEIGHT);
			SharedUtil.newInstance().setFloat(SharedUtil.KEY_WEIGHT, weight);
		}
	}

}
