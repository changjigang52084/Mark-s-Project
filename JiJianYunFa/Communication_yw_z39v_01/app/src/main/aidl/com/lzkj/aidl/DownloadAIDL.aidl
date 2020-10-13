package com.lzkj.aidl;
/**download aidl*/
interface DownloadAIDL {
	void onDownloadList(in List<String> downloadList,int type, String prmId);
	void onCancel();
}