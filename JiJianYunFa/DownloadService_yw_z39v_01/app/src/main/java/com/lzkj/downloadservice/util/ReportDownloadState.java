package com.lzkj.downloadservice.util;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.common.constant.protocol.AdpressProtocolType;
import com.baize.adpress.core.common.constant.protocol.DownloadStateConstant;
import com.baize.adpress.core.protocol.assembler.protocol.ProtocolPackageAssembler;
import com.baize.adpress.core.protocol.bo.AdpressDataPackage;
import com.baize.adpress.core.protocol.bo.AdpressProtocolPackage;
import com.baize.adpress.core.protocol.dto.AdpressCommandReceiptPackage;
import com.baize.adpress.core.protocol.dto.AdpressProgramReceiptErrorPackage;
import com.baize.adpress.core.protocol.dto.AdpressProgramReceiptPackage;
import com.baize.adpress.core.protocol.factory.AdpressProtocolPackageAssemblerFactory;
import com.lzkj.downloadservice.bean.HttpRequestBean;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.interfaces.IRequestCallback;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月17日 上午11:11:29 
 * @version 1.0 
 * @parameter  汇报下载的状态给服务器
 */
public class ReportDownloadState implements IRequestCallback {
	private static final LogTag TAG = LogUtils.getLogTag(ReportDownloadState.class.getSimpleName(), true);

	private ReportDownloadState() {}
	private static volatile ReportDownloadState sReportDownloadState;
	public static ReportDownloadState getInstance() {
		if (null == sReportDownloadState) {
			synchronized (ReportDownloadState.class) {
				if (null == sReportDownloadState) {
					sReportDownloadState = new ReportDownloadState();
				}
			}
		}
		return sReportDownloadState;
	}

	/**
	 * 汇报下载进度
	 * @param downloadState
	 * 			下载的状态(0 = 开始,1= 成功,2=失败)
	 * @param progress
	 * 			下载进度(下载了多少kb)
	 * @param totalSize
	 * 			下载文件的总大小(kb)
	 * @param fileName
	 * 			下载的文件名
	 */
	public void reportDownloadState(int downloadState,int progress, int totalSize, String fileName) {
		try {
			ProtocolPackageAssembler<AdpressProtocolPackage> assembler = AdpressProtocolPackageAssemblerFactory
																		.create(AdpressProtocolType.SPC_DOWNLOAD_RPT);
			Map<String, Object> materials = new HashMap<String, Object>();
			materials.put("deviceId", Long.parseLong(ConfigSettings.getDeviceId()));
			materials.put("fileName", fileName);
			materials.put("status", (long)downloadState);
			materials.put("uuid",ConfigSettings.getUUID(fileName));
			if (downloadState == DownloadStateConstant.DOWNLOAD_STATE_START) {
				materials.put("size", (long)totalSize);
				materials.put("startTime",new Date());
			} else if (downloadState == DownloadStateConstant.DOWNLOAD_STATE_SUCCESS){
				materials.put("endTime",new Date());
				ConfigSettings.removeUUID(fileName);
			} else {
				materials.put("endTime",new Date());
			}
			AdpressProtocolPackage assembleRes = assembler.assemble(materials);
			LogUtils.d(TAG, "reportDownloadState","assembleRes："+assembleRes.toJson());
			AdpressDataPackage responseData = new AdpressDataPackage(
																	AdpressDataPackage.VER_APP_EPOSTER, 
																	AdpressDataPackage.VER_OS_LINUX, 
																	"0.0.1", 
																	AdpressProtocolType.SPC_DOWNLOAD_RPT, 
																	assembleRes.toByte());
			String param = "data="+responseData.toString();
			LogUtils.d(TAG, "reportDownloadState","param:"+param);
			HttpRequestBean httpRequestBean = new HttpRequestBean();
			httpRequestBean.setRequestUrl(HttpUtil.getReportServer());
			httpRequestBean.setRequestRestry(5);
			httpRequestBean.setRequestCallback(this);
			httpRequestBean.setRequestParm(param);
			httpRequestBean.setRequestTag("responseReceiver");
			HttpUtil.newInstance().postRequest(httpRequestBean);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 汇报下载计划
	 * @param downloadTokenUrl
	 * 		   	下载任务列表
	 */
	public void uploadDownloadPlan(final List<String> downloadTokenUrl) {
//		new Thread(
//					new Runnable() {
//						@Override
//						public void run() {
//							try {
//								String packageContent = CommutShardUtil.newInstance().getString(CommutShardUtil.DWONLOADPLAN_ADPRESSDATAPACKAGE);
//								AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(packageContent);
//								DownloadPlanDto downloadPlanDto = new DownloadPlanDto();
//								long devieId = Long.parseLong(ConfigSettings.getDeviceId());
//								downloadPlanDto.setDeviceId(devieId);
//								long downloadProlistId = ShreadUtil.newInstance().getInt(CommutShardUtil.PROGRAM_LIST_ID);
////								long downloadProlistId = ConfigSettings.getPrmId();
//								int size = downloadTokenUrl.size();
//								LogUtils.d(TAG, "uploadDownloadPlan","downloadTokenUrl.size:"+size + ",devieId : "+ devieId + ",downloadProlistId:" +downloadProlistId);
//								List<FileDownloadDto> tblFileDownloadList = new ArrayList<FileDownloadDto>();
//								for (int i = 0 ;i < size;i++) {
//									String downloadUrl = downloadTokenUrl.get(i);
//									FileDownloadDto fileDownloadDto = new FileDownloadDto();
//									fileDownloadDto.setDeviceId(Long.parseLong(ConfigSettings.getDeviceId()));
//									fileDownloadDto.setDownloadStartTime(new Date());
//									fileDownloadDto.setDownloadStatus(Constant.NOT_DOWNLOAD_STATUE);
//									fileDownloadDto.setDownloadType(Constant.DOWNLOAD_TYPE_PAROGRAM);
//									String uuid = Helper.getUUID();
//									String fileName = FileUtil.getFileName(downloadUrl);
//									ShreadUtil.newInstance().putString(fileName, uuid);
//									fileDownloadDto.setUuId(uuid);
//									fileDownloadDto.setDownloadFileName(fileName);
//									long contentLength = HttpUtil.newInstance().getContentLength(downloadUrl);
//									LogUtils.d(TAG, "uploadDownloadPlan","contentLength:"+contentLength
//																		  +",uuid:"+uuid+",fileName:"+fileName);
//									fileDownloadDto.setDownloadFileTotal(contentLength);
//									fileDownloadDto.setDownloadProlistId(downloadProlistId);
//									tblFileDownloadList.add(fileDownloadDto);
//								}
//								downloadPlanDto.setFileDownloadList(tblFileDownloadList);
//							
//								responseReceiver(adpressDataPackage, 
//												 CommandStateConstant.COMMAND_STATE_EXECUTING,
//												 downloadPlanDto);
//								
//							} catch (Exception e) {
//								e.printStackTrace();
//							}
//						}
//					}
//				).start();
	}
	
	/**
	 * 命令接收成功返回给服务器下载计划
	 * @param adpressDataPackage
	 * 			
	 * @param responseCode
	 * 			返回服务器端的返回码
	 * @param downloadPlanData
	 * 			下载计划的json数据
	 * 			
	 */
//	private static void responseReceiver(AdpressDataPackage adpressDataPackage, int responseCode, DownloadPlanDto downloadPlanDto) {
//		try {
//			ProtocolPackageAssembler<AdpressProtocolPackage> assembler = AdpressProtocolPackageAssemblerFactory.create(
//																				AdpressProtocolType.SPC_DEVICE_CLIENT_RECEIPT_PROGRAM_DOWNLOAD);
//			Map<String, Object> materials = new HashMap<String, Object>();
//			materials.put("deviceId", Long.parseLong(ConfigSettings.getDeviceId()));
//			materials.put("state", responseCode);
//			materials.put("downloadPlan", downloadPlanDto);
//			long prmListId = ShreadUtil.newInstance().getInt(CommutShardUtil.PROGRAM_LIST_ID);
//			materials.put("programListId", prmListId);
//			LogUtils.d(TAG, "uploadDownloadPlan","downloadPlanDto:"+JSON.toJSONString(materials));
//			AdpressProtocolPackage assembleRes = assembler.assemble(materials);
//			responsePost(adpressDataPackage, assembleRes);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}
	/**
	 * 响应命令执行状态
	 * @param responseCode
	 * 			命令值
	 * @param errMessage
	 * 			错误信息
	 * @param downloadSuccessList
	 * 			下载成功的节目列表id
	 * @param downloadFiledList
	 * 			下载失败的节目列表id
	 */
	public void responseDownloadState(int responseCode, String errMessage,
										     List<String> downloadSuccessList,
										     List<String> downloadFiledList) {
		try {
			if (null == downloadFiledList) {
				downloadFiledList = new ArrayList<String>();
			}
			downloadSuccessList.clear();
			//读取本地的所有的节目文件名 传入到downloadSuccessList
			File layoutFolder      = new File(FileUtil.getInstance().getPrmFolder());
			File[] layoutFileArray = layoutFolder.listFiles();
			Set<String> layoutSet  = new HashSet<String>();
			for (File layoutFile : layoutFileArray) {
				if (layoutFile.getName().endsWith(FileUtil.PRM_SUFFIX_NAME)) {
					String prmId = FileUtil.getFileNameWithoutSuffix(layoutFile.getName());
					layoutSet.add(prmId);
				}
			}
			downloadSuccessList.addAll(layoutSet);
			downloadFiledList.addAll(getWaitPrmList());

			AdpressCommandReceiptPackage commandReceiptPagckage = new AdpressCommandReceiptPackage();
			commandReceiptPagckage.setDeviceId(Long.parseLong(ConfigSettings.getDeviceId()));
			commandReceiptPagckage.setState(responseCode);

			String packageContent = CommutShardUtil.newInstance().getString(
					CommutShardUtil.DWONLOADPLAN_ADPRESSDATAPACKAGE);
			if (!StringUtil.isNullStr(packageContent)) {
				AdpressDataPackage adpressDataPackage = AdpressDataPackage.parser(packageContent);
				if (null == adpressDataPackage || null == adpressDataPackage.getData()) {
					LogUtils.d(TAG, "responseDownloadState", "adpressDataPackage is null.");
					commandReceiptPagckage.setOperatorId(8L);
					commandReceiptPagckage.setType("03301e");
				} else {
					Long operationId = adpressDataPackage.getData().getOperationId();
					if (null == operationId) {
						operationId = 8L;
					}
					commandReceiptPagckage.setOperatorId(operationId);
					String type = adpressDataPackage.getTypeStr();
					if (StringUtil.isNullStr(type)) {
						type = "03301e";
					}
					commandReceiptPagckage.setType(type);
				}
			} else {
				commandReceiptPagckage.setOperatorId(8L);
				commandReceiptPagckage.setType("03301e");
			}
//			String prmListId = ShreadUtil.newInstance().getString(CommutShardUtil.PROGRAM_LIST_ID);
			AdpressProgramReceiptPackage adpressProgramReceiptPackage = new AdpressProgramReceiptPackage();
			adpressProgramReceiptPackage.setErrorList(getErrorProgarmList(downloadFiledList, errMessage));
			adpressProgramReceiptPackage.setSuccessList(downloadSuccessList);
			commandReceiptPagckage.setData(JSON.toJSONString(adpressProgramReceiptPackage));
			requetPost(JSON.toJSONString(commandReceiptPagckage));
			LogUtils.d(TAG, "uploadDownloadPlan", "downloadSuccessList:" + JSON.toJSONString(downloadSuccessList));
			downloadFiledList.clear();
			downloadSuccessList.clear();
//			ProtocolPackageAssembler<AdpressProtocolPackage> assembler = AdpressProtocolPackageAssemblerFactory.create(
//			Map<String, Object> materials = new HashMap<String, Object>();
//			materials.put("state", responseCode);
//			materials.put("deviceId", Long.parseLong(ConfigSettings.getDeviceId()));
//			if (null != errMessage) {
//				materials.put("message", errMessage);
//			}
//			materials.put("programListId", prmListId);
//			AdpressProtocolPackage assembleRes = assembler.assemble(materials);
//			responsePost(adpressDataPackage, assembleRes);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	private static List<AdpressProgramReceiptErrorPackage> getErrorProgarmList(
			List<String> downloadFiledList, String errMessage) {
		List<AdpressProgramReceiptErrorPackage> programReceiptErrorPackages = new ArrayList<>();
		if (downloadFiledList.isEmpty()) {
			LogUtils.d(TAG, "getErrorProgarmList", "errMessage:" + errMessage
					+ ",downloadFiledList:" + JSON.toJSONString(downloadFiledList));
			return programReceiptErrorPackages;
		}
		if (StringUtil.isNullStr(errMessage)) {
			errMessage = "节目下载失败";
		}

		for (String prmKey: downloadFiledList) {
			LogUtils.d(TAG, "getErrorProgarmList", "prmKey:" + prmKey);
			AdpressProgramReceiptErrorPackage adpressProgramReceiptErrorPackage
					= new AdpressProgramReceiptErrorPackage();
			adpressProgramReceiptErrorPackage.setKey(prmKey);
			adpressProgramReceiptErrorPackage.setRemark(errMessage);
			programReceiptErrorPackages.add(adpressProgramReceiptErrorPackage);
		}
		return programReceiptErrorPackages;
	}
	/**
	 * 获取等待下载的节目列表
	 * @return
	 */
	public List<String> getWaitPrmList() {
		List<String> waitPrmList = new ArrayList<String>();
		File waitPrmFolder = new File(FileUtil.getInstance().getWaitDownloadPrmFolder());
		String[] fileNames = waitPrmFolder.list();
		for (String fileName : fileNames) {
			StringTokenizer stringTokenizer = new StringTokenizer(fileName, FileUtil.SPLIT_PATH);
			String prmKey = stringTokenizer.nextToken();
			LogUtils.d(TAG, "getWaitPrmList", "prmKey : " + prmKey);
			waitPrmList.add(prmKey);
		}
		return waitPrmList;
	}
	
	/**
	 * 发送回执
	 * @param param
	 */
	public void requetPost(final String param) {
		HttpRequestBean httpRequestBean = new HttpRequestBean();
		httpRequestBean.setRequestUrl(HttpUtil.getReportServer());
		httpRequestBean.setRequestRestry(15);
		httpRequestBean.setRequestCallback(new IRequestCallback() {
			@Override
			public void onSuccess(String result, String httpUrl, String requestTag) {
				SQLiteManager.getInstance().delHttpBo(httpUrl, requestTag);
				LogUtils.d(TAG, "responsePost","onSuccess is param: " + param);
//				ShreadUtil.newInstance().removeKey(Constant.DOWNLOAD_REPORT_KEY);
			}
			@Override
			public void onFaile(String errMsg, String httpUrl, String requestTag) {
				LogUtils.d(TAG, "responsePost","onFaile is param: " + param);
//				ShreadUtil.newInstance().putString(Constant.DOWNLOAD_REPORT_KEY, param);
			}
		});
		httpRequestBean.setRequestParm(param);
		httpRequestBean.setRequestTag("responseReceiver" + System.currentTimeMillis());
		HttpUtil.newInstance().postRequest(httpRequestBean);

		SQLiteManager.getInstance().insterHttpBo(httpRequestBean, "POST");
	}
	/**
	 * 响应命令执行回执
	 * @param adpressDataPackage
	 * @param assembleRes
	 */
	private void responsePost(AdpressDataPackage adpressDataPackage , AdpressProtocolPackage assembleRes) {
		if(adpressDataPackage == null){
			LogUtils.d(TAG, "responsePost","adpressDataPackage is null");
			return;
		}
		try {
			AdpressDataPackage responseData = new AdpressDataPackage(
					adpressDataPackage.getPre(),
					adpressDataPackage.getVerApp(), 
					adpressDataPackage.getVerOs(), 
					adpressDataPackage.getVerMain(),
					adpressDataPackage.getVerSub(), 
					adpressDataPackage.getVerIdx(), 
					0, 0, adpressDataPackage.getTypeFunc(),
					assembleRes.toByte(), 
					adpressDataPackage.getId(), 
					new Date());
			String param = "data="+responseData.toString();
			LogUtils.d(TAG, "uploadDownloadPlan","assembleRes:"+assembleRes.toJson());
			HttpRequestBean httpRequestBean = new HttpRequestBean();
			httpRequestBean.setRequestUrl(HttpUtil.getDownloadPlanReportServer());
			httpRequestBean.setRequestRestry(5);
			httpRequestBean.setRequestCallback(this);
			httpRequestBean.setRequestParm(param);
			httpRequestBean.setRequestTag("responseReceiver" + System.currentTimeMillis());
			HttpUtil.newInstance().postRequest(httpRequestBean);
		} catch  (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onSuccess(String result, String httpUrl, String requestTag) {
		SQLiteManager.getInstance().delHttpBo(httpUrl, requestTag);
	}

	@Override
	public void onFaile(String errMsg, String httpUrl, String requestTag) {
	}
}
