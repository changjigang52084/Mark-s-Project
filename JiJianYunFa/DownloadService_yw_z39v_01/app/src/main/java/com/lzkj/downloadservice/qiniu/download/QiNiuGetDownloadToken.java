package com.lzkj.downloadservice.qiniu.download;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import com.lzkj.downloadservice.bean.HttpRequestBean;
import com.lzkj.downloadservice.interfaces.IRequestCallback;
import com.lzkj.downloadservice.qiniu.impl.QiNiuDownloadTokenCallback;
import com.lzkj.downloadservice.util.ConfigSettings;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.HttpUtil;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月2日 下午3:05:28 
 * @version 1.0 
 * @parameter  获取七牛下载token
 */
public class QiNiuGetDownloadToken implements IRequestCallback {
	private static final LogTag TAG = LogUtils.getLogTag(QiNiuGetDownloadToken.class.getSimpleName(), true);
	/**下载的url*/
	private List<String> downloadTokenUrl = new ArrayList<String>();
	/**获取完所有的download token以后调用的接口*/
	private QiNiuDownloadTokenCallback qiNiuDownloadTokenCallback;
	/**下载任务总数*/
	private int downloadFileTotalSize = 0;
	/**下载文件的类型*/
	private int downloadType;
	public QiNiuGetDownloadToken(QiNiuDownloadTokenCallback qiNiuDownloadTokenCallback) {
		this.qiNiuDownloadTokenCallback = qiNiuDownloadTokenCallback;
		
	}
	/**
	 * 根据文件名获取下载路径
	 * @param downloadFileList
	 * 			一组下载文件的文件名
	 * @param type
	 * 			下载文件类型
	 */
	public void downloadFile(List<String> downloadFileList,int type) {
		try {
			downloadType = type;
			downloadFileTotalSize = downloadFileList.size();
			LogUtils.d(TAG, "downloadFile","downloadFileTotalSize:"+downloadFileTotalSize+",type:"+type);
			for (int i = 0;i < downloadFileTotalSize;i++) {
				String fileName = downloadFileList.get(i);
				LogUtils.d(TAG, "downloadFile","fileName:"+fileName);
				HttpRequestBean httpRequest = new HttpRequestBean();
				httpRequest.setRequestCallback(this);
				int buckeyType = Constant.BUCKEY_TYPE_APRESSDEV;
				if(type == Constant.DOWNLOAD_APP){
					buckeyType = Constant.BUCKEY_TYPE_APPINFO;
				}
				String pram = String.format("bucketType=%s&downType=%s&downloadURL=%s&expires=%s", 
											buckeyType,
										    Constant.QINIU_DOWNLOAD_TYPE,
										    URLEncoder.encode(fileName, "UTF-8"),
										    Constant.QINIU_DOWNLOAD_OUTTIME
										    );
/*				URLEncoder.encode(downloadFileList.get(i), "UTF-8"),
*/				LogUtils.d(TAG, "downloadFile","pram:"+pram);
				httpRequest.setRequestParm(pram);
				httpRequest.setRequestRestry(5);  
				httpRequest.setRequestTag("DownloadTask"); 
				String requestUrl = String.format(Constant.GET_DOWNLOAD_TOKEN_URL,ConfigSettings.getWorkServer());
				httpRequest.setRequestUrl(requestUrl);
				HttpUtil.newInstance().sendGetRequest(httpRequest);
//				HttpUtil.newInstance().postRequest(httpRequest); 
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onSuccess(String result, String requestHttpUrl, String requestTag) {
		try {
			LogUtils.d(TAG, "onSuccess","result:"+result);
			JSONObject jsonObject = new JSONObject(result);
			String token = jsonObject.getString("uptoken");
			LogUtils.d(TAG, "onSuccess","token:"+token);
			downloadTokenUrl.add(token);
			if (downloadFileTotalSize == downloadTokenUrl.size()) {
//				ReportDownloadState.uploadDownloadPlan(downloadTokenUrl);
				qiNiuDownloadTokenCallback.downloadTokenList(downloadTokenUrl, downloadType, null);
			}
			jsonObject = null;
		} catch (Exception e) {
			e.printStackTrace();
		}
	
	}
	

	@Override
	public void onFaile(String errMsg, String requestHttpUrl, String requestTag) {
		LogUtils.d(TAG, "onFaile","errMsg:"+errMsg);
		
	}
}
