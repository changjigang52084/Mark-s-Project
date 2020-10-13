package com.lzkj.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.ui.util.Constants;
import com.lzkj.ui.util.Helper;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.StringUtil;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月23日 下午2:54:13 
 * @version 1.0 
 * @parameter  素材修复完成以后广播
 */
public class MaterialRepairSuccessReceiver extends BroadcastReceiver {
	private static final LogTag TAG = LogUtils.getLogTag(MaterialRepairSuccessReceiver.class.getSimpleName(), true);
	@Override
	public void onReceive(Context context, Intent intent) {
		if (null != intent) {
			String materialName = intent.getStringExtra(Constants.MATERIAL_NAME);
			if (StringUtil.isNullStr(materialName)) {
				return;
			}
			Helper.delRecoveryMaterials(materialName);
			LogUtils.d(TAG, "onReceive", "materialName:" + materialName);
//			ProgramPlayManager.getInstance().updateProgramList();
		}
	}
}
