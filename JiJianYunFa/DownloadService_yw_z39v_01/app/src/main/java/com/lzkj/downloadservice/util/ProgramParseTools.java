package com.lzkj.downloadservice.util;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Material;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.bean.DownloadBo;
import com.lzkj.downloadservice.bean.WaitDownloadBo;
import com.lzkj.downloadservice.service.RecoveryDownloadTaskService;
import com.lzkj.downloadservice.service.ResposeDownloadService;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *@author kchang changkai@lz-mr.com
 *@Description:节目解析工具类
 *@time:2016年3月21日 下午5:15:21
 */
public class ProgramParseTools {
	private static final LogTag TAG = LogUtils.getLogTag(ProgramParseTools.class.getSimpleName(), true);
	/**
	 * 获取本地所有素材名称列表
	 * @return
	 * 		返回本地所有节目素材名称列表
	 */
	public static List<String> getAllMaterialName() {
		List<String> materialNames = new ArrayList<String>();
		List<Program> programList = getLocalProgramList();
		for (Program program : programList) {
			List<Area> areas = program.getAs();
			if (null != areas && !areas.isEmpty()) {
				for (Area area : areas) {
					 List<Material> materials = area.getMas();
					 if (null != materials && !materials.isEmpty()) {
						 for (Material material : materials) {
							 if (null != material) {
								 String fileName = FileUtil.getFileName(material.getU());
//								 String fileName = material.getN();
								 if (!TextUtils.isEmpty(fileName)) {
									 materialNames.add(fileName);
								 }
							 }
						 }
					 }
				}
			}
		}
		return materialNames;
	}
	/**
	 * 筛选出可以下载的节目文件 流量控制
	 * @param downloadList 下载列表
	 * @param prmId 节目id
	 * @param type 下载类型
	 * @return 可下载的任务列表
	 */
	public static List<String> getCanDownloadList(List<String> downloadList, String prmId, int type) {
		if (null == downloadList || null == prmId) {
			return new ArrayList<String>();
		}
		ArrayList<WaitDownloadBo> waitDownloadBos = new ArrayList<WaitDownloadBo>();
		String prmPath = checkPrmIsExits(prmId);
		Log.d("getCanDownloadList","prmPath: " + prmPath);
		if (StringUtil.isNullStr(prmPath)) {
			downloadList.clear();
			return downloadList;
		}
		
		long availableFlow = FlowManager.getInstance().getAvailableFlow();
		Program program = getProgramToPath(prmPath);
		
		Material materialBgm = program.getBgm();
		addWaitDownloadList(downloadList, prmId, waitDownloadBos, materialBgm, availableFlow, type);
		Material materialBi = program.getBi();
		addWaitDownloadList(downloadList, prmId, waitDownloadBos, materialBi, availableFlow, type);
		List<Area> areaList = program.getAs();
		if (null != areaList && !areaList.isEmpty()) {
			for (Area area : areaList) {
				if (null != area) {
					List<Material> materials = area.getMas();
					if (null != materials && !materials.isEmpty()) {
						for (Material material : materials) {
							addWaitDownloadList(downloadList, prmId, waitDownloadBos,
									material, availableFlow, type);
						}
					}
				}
			}
		}
		//将等待下载的任务写入到本地进行缓存，等待更新流量上限进行重新筛选下载
		writerWaitDownload(waitDownloadBos);
		return downloadList;
	}
	
	private static String checkPrmIsExits(String prmId) {
		Log.d("checkPrmIsExits","prmId: " + prmId);
		String tempPrmFile = prmId + FileUtil.TEMP_SUFFIX_NAME;
		String prmFile = prmId + FileUtil.PRM_SUFFIX_NAME;
		File prmFolder = new File(FileUtil.getInstance().getPrmFolder(), tempPrmFile);
		if (!prmFolder.exists()) {//
			prmFolder = new File(FileUtil.getInstance().getPrmFolder(), prmFile);
			LogUtils.d(TAG, "getCanDownloadList", "tempPrmFile:" + prmFolder.getAbsolutePath());
			if (!prmFolder.exists()) {
				LogUtils.d(TAG, "getCanDownloadList", "prmFile:" + prmFolder.getAbsolutePath());
				return null;
			}
		}
		return prmFolder.getAbsolutePath();
	}
	
	
	private static void writerWaitDownload(final ArrayList<WaitDownloadBo> waitDownloadBos) {
		LogUtils.d(TAG, "writerWaitDownload", "waitDownloadBos size :" + waitDownloadBos.size());
		if (null != waitDownloadBos && !waitDownloadBos.isEmpty()) {
			ThreadPoolManager.get().addRunnable(new Runnable() {
				@Override
				public void run() {
					String cacheWaitDownloadBos = FileUtil
							.readStringToPath(FileUtil.getInstance().getWaitFilePath());
					Set<WaitDownloadBo> cacheWaitDownloadSet = new HashSet<WaitDownloadBo>();
					if (null != cacheWaitDownloadBos) {
						List<WaitDownloadBo> cacheList = JSON.parseArray(cacheWaitDownloadBos, WaitDownloadBo.class);
						waitDownloadBos.addAll(cacheList);
					}
					cacheWaitDownloadSet.addAll(waitDownloadBos);
					
					waitDownloadBos.clear();
					waitDownloadBos.addAll(cacheWaitDownloadSet);
					LogUtils.d(TAG, "writerWaitDownload", "cacheWaitDownloadBos :" + cacheWaitDownloadBos + ", waitDownloadBos : " + JSON.toJSONString(waitDownloadBos));
					String cacheWaitList = JSON.toJSONString(waitDownloadBos);
					try {
						FileUtil.writerContentToFile(FileUtil.getInstance()
								.getWaitFilePath(), cacheWaitList, false);
					} catch (IOException e) {
						e.printStackTrace();
					}
					
				}
			});
		}
	}
	
	private static void addWaitDownloadList(List<String> downloadList, String prmId,
			ArrayList<WaitDownloadBo> waitDownloadBos, Material material, 
			long availableFlow, int type) {
		LogUtils.d(TAG, "addWaitDownloadList", "availableFlow :" + availableFlow + ", prmId : " + prmId 
				+ ", downloadList : " + JSON.toJSONString(downloadList));

		if (null != material && null != material.getS() && downloadList.contains(material.getU())) {
			if (availableFlow < material.getS()) {
				LogUtils.d(TAG, "addWaitDownloadList", "availableFlow : " + availableFlow 
						+ ", httpUrl: "+ material.getU());
				downloadList.remove(material.getU());//移除下载任务
				WaitDownloadBo waitDownloadBo = new WaitDownloadBo();
				waitDownloadBo.downloadUrl = material.getU();
				waitDownloadBo.fileSize = material.getS();
				waitDownloadBo.prmKey = prmId;
				waitDownloadBo.type = type;
				waitDownloadBos.add(waitDownloadBo);
			} else {
				availableFlow -= material.getS();
			}
		}
	}
	
	/**
	 * 读取等待下载的素材列表进行下载
	 */
	public static void updateWaitDownloadListToDownload() {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				String cacheWaitDownloadBos = FileUtil.readStringToPath(FileUtil.getInstance()
						.getWaitFilePath());
				if (!StringUtil.isNullStr(cacheWaitDownloadBos)) {
					//获取可以使用的流量值
					long maxFlowSize = FlowManager.getInstance().getAvailableFlow();
					List<WaitDownloadBo> removeList = new ArrayList<WaitDownloadBo>();
					List<WaitDownloadBo> cacheList = JSON.parseArray(cacheWaitDownloadBos, WaitDownloadBo.class);
					ArrayList<DownloadBo> downloadBos = new ArrayList<DownloadBo>();
					//节目id集合
					Set<String> prmKeyList = new HashSet<String>();
					for (WaitDownloadBo waitDownloadBo : cacheList) {
						if (null != waitDownloadBo) {
							prmKeyList.add(waitDownloadBo.prmKey);
						}
					}
					
					for (String prmKey : prmKeyList) {
						//判断节目本地还有没有，没有节目的话直接删除
						String prmPath = checkPrmIsExits(prmKey);
						ArrayList<String> httpUrlList = new ArrayList<String>();
						DownloadBo bo = new DownloadBo();
						for (WaitDownloadBo waitDownloadBo : cacheList) {
							if (StringUtil.isNullStr(prmPath) && prmKey.equals(waitDownloadBo.prmKey)) {
								removeList.add(waitDownloadBo);
								continue;
							}
							if (null != waitDownloadBo 
									&& prmKey.equals(waitDownloadBo.prmKey)
									&& maxFlowSize > waitDownloadBo.fileSize) {
								removeList.add(waitDownloadBo);
								maxFlowSize -= waitDownloadBo.fileSize;
								bo.setType(waitDownloadBo.type);
								httpUrlList.add(waitDownloadBo.downloadUrl);
							}
						}	
						
						if (!httpUrlList.isEmpty()) {
							bo.setHttpUrls(httpUrlList);
							bo.setPrmId(prmKey);
							downloadBos.add(bo);
						}
					}
					LogUtils.d(TAG, "updateWaitDownloadListToDownload", "maxFlowSize : " + maxFlowSize + ", cacheList：" + JSON.toJSONString(cacheList));
					
					cacheList.removeAll(removeList);//删除已经添加下载任务列表中的文件
					try {
						Set<String> downloadUrlList = new HashSet<String>();
						for (WaitDownloadBo waitDownloadBo : cacheList) {
							downloadUrlList.add(waitDownloadBo.downloadUrl);
						}
						Set<String> downloadList = new HashSet<String>();
						List<WaitDownloadBo> newWaitDownloadList = new ArrayList<WaitDownloadBo>();
						for (String url : downloadUrlList) {
							for (WaitDownloadBo waitDownloadBo : cacheList) {
								if (url.equals(waitDownloadBo.downloadUrl) && !downloadList.contains(url)) {
									downloadList.add(waitDownloadBo.downloadUrl);
									newWaitDownloadList.add(waitDownloadBo);
								}
							}
						}
						
						cacheList.clear();
						cacheList.addAll(newWaitDownloadList);
						
						downloadUrlList.clear();
						downloadList.clear();
						
						if (!cacheList.isEmpty()) {
							String cacheWaitList = JSON.toJSONString(cacheList);
							FileUtil.writerContentToFile(FileUtil.getInstance()
									.getWaitFilePath(), cacheWaitList, false);
						} else {
							FileUtil.writerContentToFile(FileUtil.getInstance()
									.getWaitFilePath(), "", false);
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					
					startResposeDownloadService(downloadBos);
				}	
			}
		});
	}
	
	/**
	 * 启动下载
	 * @param downloadBos 需要下载的任务列表
	 */
	private static void startResposeDownloadService(ArrayList<DownloadBo> downloadBos) {
		if (null == downloadBos || downloadBos.isEmpty()) {
			return;
		}
		Intent intent = new Intent(DownloadApp.getContext(), ResposeDownloadService.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.putExtra(Constant.DOWNLOAD_TYPE_KEY, Constant.APPEND_DOWNLOAD);
		
		Bundle bundle = new Bundle();
		bundle.putParcelableArrayList(RecoveryDownloadTaskService.DOWNLOAD_LIST, downloadBos);
		intent.putExtras(bundle);
		
		DownloadApp.getContext().startService(intent);
	}
	
	/**
	 * 传入节目路径返回节目对象
	 * @param programFilePath
	 * 			节目的本地路径
	 * @return	
	 * 		节目对象
	 */
	public static Program getProgramToPath(String programFilePath) {
		Program program = null;
		if (!StringUtil.isNullStr(programFilePath)) {
			File programFile = new File(programFilePath);
			if (programFile.exists()) {
				String programJson = FileUtil.readStringToPath(programFilePath);
				program = JSON.parseObject(programJson, Program.class);
			}
		}
		return program;
	}
	
	
	/**
	 * 获取本地所有节目
	 * @return 
	 *  	返回所有节目对象列表
	 */
	private static List<Program> getLocalProgramList() {
		List<Program> programList = new ArrayList<Program>();
		File prmFolder = new File(FileUtil.getInstance().getPrmFolder());
		File[] files = prmFolder.listFiles();
		if (null != files && files.length > 0) {
			for (File file : files) {
				String prmJson = FileUtil.readStringToPath(file.getAbsolutePath());
				try {
					Program program = JSON.parseObject(prmJson, Program.class);
					if (program == null) {
						LogUtils.w(TAG, "getLocalProgramList", "Program parse error: program is null." + file.getName());
						continue;
					}
					if (checkProgramIsNotMaterial(program)) {
						programList.add(program);
					}
				} catch (Exception e) {
					LogUtils.w(TAG, "getLocalProgramList", "Program parse error:" + e.getMessage());
					continue;
				}
			}
			return programList;
		} else {
			LogUtils.w(TAG, "getLocalProgramList", "No local program files.");
		}
		return programList;
	}
	
	/**
	 * 验证节目是否有素材
	 * @return
	 * 		true表示有素材，false表示没有素材
	 */
	private static boolean checkProgramIsNotMaterial(Program program) {
		boolean isNotMaterial = false;
		List<Area> areas = program.getAs();
		if (null == areas || areas.isEmpty()) {
			return isNotMaterial;
		}
		if (1 == areas.size()) {
			Area area = areas.get(0);
			if (null == area) {
				isNotMaterial = false;
			} else {
				List<Material> materials = area.getMas();
				isNotMaterial = !(null == materials || materials.isEmpty());
			}
		} else {
			isNotMaterial = true;
		}
		return isNotMaterial;
	}
	

	
}
