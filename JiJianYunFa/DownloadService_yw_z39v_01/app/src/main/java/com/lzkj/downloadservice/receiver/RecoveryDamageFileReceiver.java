package com.lzkj.downloadservice.receiver;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lzkj.downloadservice.impl.RecoveryDamageFileImpl;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.PrmFileHashUtil;
import com.lzkj.downloadservice.util.ProgramParseTools;
import com.lzkj.downloadservice.util.StringUtil;
import com.lzkj.downloadservice.util.ThreadPoolManager;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2016年2月23日 上午10:13:42 
 * @version 1.0 
 * @parameter  恢复 下载损坏的文件
 */
public class RecoveryDamageFileReceiver extends BroadcastReceiver {
	
	@Override
	public void onReceive(Context context, final Intent intent) {
		if (null != intent) { 
//			String hashMd = intent.getStringExtra(Constant.MATERIAL_MD5);
//			String fileName = intent.getStringExtra(Constant.MATERIAL_NAME);
			ThreadPoolManager.get().addRunnable(new Runnable() {
				@Override
				public void run() {
					PrmFileHashUtil.get().addAllPrmHash();
					ArrayList<String> downloadUrlList = intent.getStringArrayListExtra(Constant.RECOVERY_DAMAGE_FILE_LIST);
					String prmKey = intent.getStringExtra(Constant.PROGRAM_KEY);
					if (!StringUtil.isNullStr(prmKey) && null != downloadUrlList) {
						ProgramParseTools.getCanDownloadList(downloadUrlList, prmKey, Constant.DOWNLOAD_PRMFILE);
						RecoveryDamageFileImpl damageFile = new RecoveryDamageFileImpl();
						damageFile.extecuDownload(downloadUrlList);
					}
				}
			});
		}
	}
}
