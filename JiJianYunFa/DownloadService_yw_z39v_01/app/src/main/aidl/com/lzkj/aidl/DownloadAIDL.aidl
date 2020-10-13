// DownloadAIDL.aidl
package com.lzkj.aidl;

// Declare any non-default types here with import statements

interface DownloadAIDL {
   	void onDownloadList(in List<String> downloadList,int type, String prmId);
   	void onCancel();
}
