package com.lzkj.downloadservice.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSON;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.bean.FlowBo;
import com.lzkj.downloadservice.qiniu.upload.UploadTask;
import com.lzkj.downloadservice.receiver.UploadLogReceiver;
import com.lzkj.downloadservice.util.LogUtils.LogTag;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.net.URLDecoder;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/***
 * 文件方面的工具类
 * @author changkai
 *
 */
public class FileUtil {
	private static final LogTag TAG = LogUtils.getLogTag(FileUtil.class.getSimpleName(), true);
	/**根目录*/
//	public static final String SDCARD_PATH = Environment.getExternalStorageDirectory()
//			.getAbsolutePath()+File.separator;
	
	public static final String DOWNLOAD_FLOW_INFO = "DOWNLOAD_FLOW_INFO";
	private String ROOT_FOLDER  = "mallposter" + File.separator;
	private String PATH_TEMP   = ROOT_FOLDER + "temp";
	private String PATH_PRM    = ROOT_FOLDER + "prm";
	private String PATH_VIDEO  = ROOT_FOLDER + "video";
	private String PATH_PIC    = ROOT_FOLDER + "pic";
	private String PATH_LOG    = ROOT_FOLDER + "log";
	/**截图文件目录 */
	public String PATH_SHOT = ROOT_FOLDER + "shot";
	
	private String PATH_APP     = ROOT_FOLDER + "app";
	public String UUID_FOLDER  = ROOT_FOLDER + "uuid/";
	public static final String UUID_FILE  = "uuid.json";
	public static final String TEMP_FILE  = "temp.txt";
	public static final String HASH_FILE = "hash";
	public static final String PRM_SUFFIX_NAME = ".prm";
	public static final String TEMP_SUFFIX_NAME = ".temp";
	public String PATH_QN_RECOR = ROOT_FOLDER + "qnrecor";
	public static final String WAIT_PRM_SUFFIX_NAME = ".waitPrm";
	/**分隔符 +++*/
	public static final String SPLIT_PATH = "+++";
	
	/**等待下载的列表文件名**/
	public static final String WAIT_DOWNLOAD_TMEP_NAME= "waitdownloadlist.temp";
	
	/**5 m*/
	private static final long TEMP_MAX_LENGTH = 5 * 1024 * 1024;
	/** 日志文件名前缀*/ 
	private static final String LOG_PREFIX = "Eposter";
	/** 日志文件后缀*/ 
	private static final String LOG_SUFFIX = "log";
	/**保存文件类型的map对象*/
	private Map<String,String> contentTypeMap = new HashMap<String,String>();
	
	public Map<String,String> suffixToFolder = new HashMap<String,String>();
	
	private static volatile FileUtil fileUtil;
	
	private String sdcard;
	
	private FileUtil() {
		init();
	}
	
	public static FileUtil getInstance() {
		if (null == fileUtil) {
			synchronized (FileUtil.class) {
				if (null == fileUtil) {
					fileUtil = new FileUtil();
				}
			}
		}
		return fileUtil;
	}
	
	private void init() {
		contentTypeMap.clear();
		suffixToFolder.clear();
		
		contentTypeMap.put("jpeg","image/jpeg");
		contentTypeMap.put("png", "image/png");
		contentTypeMap.put("txt", "text/plain");
		contentTypeMap.put("log", "text/plain");
		contentTypeMap.put("pdf", "application/pdf");
		contentTypeMap.put("mp4", "video/mpeg4");
		contentTypeMap.put("avi", "video/avi");
		contentTypeMap.put("doc", "application/msword");
		
		suffixToFolder.put("png", getPicFolder());
		suffixToFolder.put("jpeg", getPicFolder());
		suffixToFolder.put("jpg", getPicFolder());
		suffixToFolder.put("gif", getPicFolder());
		suffixToFolder.put("bmp", getPicFolder());
		suffixToFolder.put("PNG", getPicFolder());
		suffixToFolder.put("JPEG", getPicFolder());
		suffixToFolder.put("JPG", getPicFolder());
		suffixToFolder.put("GIF", getPicFolder());
		suffixToFolder.put("BMP", getPicFolder());
		suffixToFolder.put("prm", getPrmFolder());
	}
	
	/**
	 * @return the sdcard
	 */
	public String getSdcard() {
		if (TextUtils.isEmpty(sdcard)) {
			sdcard = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator;
		}
		return sdcard;
	}

	/**
	 * @param sdcard the sdcard to set
	 */
	public void setSdcard(String sdcard) {
		if (TextUtils.isEmpty(sdcard)) {
			this.sdcard = Environment.getExternalStorageDirectory()
			.getAbsolutePath() + File.separator;
		} else {
			this.sdcard = sdcard + File.separator;
		}
		init();
	}

	public String getRoot() {
		
		return getSdcard() + ROOT_FOLDER;
	}
	
	/**
	 * 返回prm文件夹的路径
	 * @return
	 */
	public String getPrmFolder() {
		
		return getSdcard() + PATH_PRM;
	}
	/**
	 * 返回pic文件夹的路径
	 * @return
	 */
	public String getPicFolder() {
		
		return getSdcard() + PATH_PIC;
	}
	
	/**
	 * 返回video文件夹的路径
	 * @return
	 */
	public String getVideoFolder() {
		return getSdcard() + PATH_VIDEO;
	}
	
	/**
	 * 返回temp文件夹的路径
	 * @return
	 */
	public String getTempFolder() {
		return getSdcard() + PATH_TEMP;
	}
	/**
	 * 返回App文件夹的路径
	 * @return
	 */
	public String getAPPFolder() {
		return getSdcard() + PATH_APP;
	}
	
	public String getWaitFilePath() {
		String folder = getTempFolder();
		File folderFile = new File(folder);
		folderFile.mkdirs();
		File waitFile = new File(folderFile, WAIT_DOWNLOAD_TMEP_NAME);
		return waitFile.getAbsolutePath();
	}
	

	
	/**
	 * 返回等待下载的节目文件夹的路径
	 * @return
	 */
	public String getWaitDownloadPrmFolder() {
		String path = getSdcard() + ROOT_FOLDER + WAIT_PRM_SUFFIX_NAME;
		File folder = new File(path);
		folder.mkdirs();
		return path;
	}
	
	/**
	 * 根据文件路径获取文件夹名称
	 * @param filePath 文件路径,传入null or ""返回sd卡的根目录
	 * @return 返回文件夹名称
	 */
	public String getFolderToFilePath(String filePath) {
		if (TextUtils.isEmpty(filePath)) {
			return getSdcard();
		}
		return filePath.substring(0, filePath.lastIndexOf(File.separator));
	}
	
	/**
	 * 根据http地址获取文件名
	 * @param httpUrl
	 * 			下载地址
	 * @return
	 * 		文件名
	 */
	public static String getFileName(String httpUrl) {
		if (null == httpUrl) {
			return httpUrl;
		}
		// http://7xq81q.com1.z0.glb.clouddn.com/1453285422797-test.mp4?sdfsdfasdflkajsldfjlasdjflasjdf=S>?AWekrwer
		String fileName = httpUrl.substring(httpUrl.lastIndexOf("/")+1, httpUrl.length());
//		String fileName = httpUrl.substring(httpUrl.lastIndexOf("/")+1, httpUrl.lastIndexOf("?"));
		try {
			if (fileName.indexOf("@") != -1) {
				fileName = fileName.substring(fileName.indexOf("@")+1, fileName.length());
			}
			if (fileName.indexOf("?") != -1) {
				// fileName = "1453285422797-test.mp4"
				fileName = fileName.substring(0, fileName.indexOf("?"));
			}
			// 解码成UTF-8的字符串
			fileName = URLDecoder.decode(fileName, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return fileName;
	} 
	
	/**
	 * 根据文件名获取文件后缀
	 * @param fileName
	 * 			文件名
	 * @return
	 * 		文件后缀
	 */
	public static String getSuffix(String fileName) {
		if (null == fileName) {
			return fileName;
		}
		return fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
	}
	
	/**
	 * 根据本地文件路径获取文件名
	 * @param filePath
	 * 			文件路径
	 * @return
	 * 		文件名
	 */
	public static String getFileNameToFilePath(String filePath) {
		if (null == filePath) {
			return filePath;
		}
		return filePath.substring(filePath.lastIndexOf("/")+1, filePath.length());
	}
	
	/**
	 * 根据后缀获取content-type
	 * @param suffix
	 * 			后缀名,例如"jpeg",返回的就是"image/jpeg"
	 * @return
	 */
	public String getContentTypeToSuffix(String suffix) {
		if (null == suffix) {
			return suffix;
		}
		if (contentTypeMap.containsKey(suffix)) {
			return contentTypeMap.get(suffix);
		}
		return null;
	}
	/**
	 * 返回编码过的地址
	 * @param httpUrl
	 * @return
	 */
	public static String encodeUrl(String httpUrl) {
		try {
			String http = httpUrl.substring(0, httpUrl.lastIndexOf("/") + 1);
			String httpEnd = httpUrl.substring(httpUrl.lastIndexOf("/") + 1, httpUrl.length());
			String fileName = null;
			String split = "?";
			if (httpEnd.contains(split)) {
				fileName = httpEnd.substring(0, httpEnd.indexOf(split));
				httpEnd = httpEnd.substring(httpEnd.indexOf(split), httpEnd.length());
				fileName = Uri.encode(fileName, "UTF-8");
			} else {
				fileName = Uri.encode(httpEnd, "UTF-8");
				LogUtils.d(TAG, "encodeUrl", "fileName:"+fileName);
				return http + fileName;
			}
			return http + fileName + httpEnd;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 获取文件的md5值
	 * @param filePath
	 * @return
	 */
	public static String getMD5(String filePath) {
		String md5 = null;
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			int bufferSize = 256 * 1024;
			File file = new File(filePath);
			if (file.exists()) {
				FileInputStream fis = null;
				fis = new FileInputStream(file);
				byte[] buffer = new byte[bufferSize];
				int length = 0;
				while ((length = fis.read(buffer)) != -1) {
					md.update(buffer, 0, length);
				}
				fis.close();
				BigInteger bi = new BigInteger(1, md.digest());
				md5 = bi.toString(16).toUpperCase();
			}
		} catch (NoSuchAlgorithmException e) {
			LogUtils.e(TAG, "getMD5", e);
		} catch (IOException e) {
			LogUtils.e(TAG, "getMD5", e);
		}
		return md5;
	}
	/**
	 * 获取日志的文件夹目录
	 * @return
	 */
	public String getLogFolder() {
		
		return getSdcard() + PATH_LOG;
	}
	
	/**获取截图的文件夹目录**/
//	public static String getScreenshot() {
//		return SDCARD_PATH + PATH_SCREENSHOT;
//	}
	/**
	 * 获取所有的日志文件
	 * @return
	 */
	public List<String> getLogFilePaths() {
		List<String> logList = new ArrayList<String>();
		File logFolder = new File(getLogFolder());
		File[] paths = logFolder.listFiles();
		for (File file : paths) {
			logList.add(file.getAbsolutePath());
		}
		return logList;
	}
	/**
	 * 获取所有的截图文件目录
	 * @return
	 */
	public List<String> getScreenshotFilePaths() {
		List<String> logList = new ArrayList<String>();
		File logFolder = new File(getShotFolderPath());
		File[] paths = logFolder.listFiles();
		for (File file : paths) {
			LogUtils.d(TAG, "getScreenshotFilePaths","upload filename:"+file.getAbsolutePath());
			logList.add(file.getAbsolutePath());
		}
		return logList;
	}
	
	/**
	 * 根据文件路径获取文件大小
	 * @param filePath
	 * 			本地文件的路径
	 * @return
	 * 		文件大小
	 */
	public static long getFileSizeToFilePath(String filePath) {
		File file = new File(filePath);
		if (!file.exists()) {
			return 0;
		}
		return file.length();
	}
	
	/**
	 * 读取hash文件
	 */
	public static String readHash() {
		//需要修改获取hash值的位置
//		File folder = new File(HASH_FOLDER);
//		File file = new File(folder.getAbsolutePath()+ File.separator + HASH_FILE);
//		try {
//			if (!folder.exists()) {
//				return null;
//			}
//			if (!file.exists()) {
//				return null;
//			}
//			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
//			StringBuffer buffer = new StringBuffer();
//			String temp = null;
//			while ((temp = bufferedReader.readLine()) != null) {
//				buffer.append(temp);
//			}
//			bufferedReader.close();
//			return buffer.toString();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		return "FqycZ8z00_YQbu9wxefp-TaJjCjC";
	}
	
	
	/** 
	 * 获取日志文件名
	 * @param logType 日志类型
	 * @return logFileName 日志文件名
	 */ 
	public static String getLogFileName(LogType logType) {
		String logFileName = LOG_PREFIX + "_" + logType + "_" + Helper.getStringTimeToFormat("yyyy_MM_dd") 
				+ "_" + getHour() + "_"+ ConfigSettings.getDeviceId() + "." + LOG_SUFFIX;
		return logFileName;
	} 
	
	/**
	 * 获取时间
	 * @return
	 */
	private static String getHour() {
		int hour = Helper.getCurrentHourBefore();
		if (hour < 10) {
			return "0" + hour;
		}
		return String.valueOf(hour);
	}

	/** 日志枚举类型 */
	public enum LogType {
		/** 播放日志 */ PLAY_LOG, 
		/** 错误日志 */ERROR_LOG, 
		/** 设备运行日志 */ OPERATION_LOG,
		/** 下载错误日志 */ DOWNLOADERROR_LOG
	}
	/**
	 * 读取uuid到文件
	 */
	public String readUUID() {
		File folder = new File(getSdcard() + UUID_FOLDER);
		File file = new File(folder.getAbsolutePath()+File.separator+UUID_FILE);
		try {
			if (!folder.exists()) {
				return null;
			}
			if (!file.exists()) {
				return null;
			}
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
			StringBuffer buffer = new StringBuffer();
			String temp = null;
			while ((temp = bufferedReader.readLine()) != null) {
				buffer.append(temp);
			}
			bufferedReader.close();
			return buffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * 写入uuid到文件
	 * @param jsonData
	 */
	public void writeUUID(final String jsonData) {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				File folder = new File(getSdcard() + UUID_FOLDER);
				File file = new File(folder.getAbsolutePath()+File.separator+UUID_FILE);
				try {
					if (!folder.exists()) {
						folder.mkdir();
					}
					if (!file.exists()) {
						file.createNewFile();
					}
					if (null == jsonData) {
						FileOutputStream fileOutputStream = new FileOutputStream(file);
						fileOutputStream.write("".getBytes());
						fileOutputStream.flush();
						fileOutputStream.close();
						return;
					}
					FileWriter fileWriter = new FileWriter(file);
					fileWriter.write(jsonData);
					fileWriter.flush();
					fileWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 编写上传日志
	 * @param logName
	 */
	public void writeUploadLog(final String logName) {
		if (!ConfigSettings.isOpenLog()) {
			return;
		}
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				File folder = new File(getSdcard() + PATH_TEMP);
				File file = new File(folder.getAbsolutePath()+File.separator + TEMP_FILE);
				try {
					if (!folder.exists()) {
						folder.mkdir();
					}
					if (!file.exists()) {
						file.createNewFile();
					} else {
						long length = file.length();
						if (TEMP_MAX_LENGTH < length) {
							file.delete();
							file.createNewFile();
						}
					}
					FileWriter fileWriter = new FileWriter(file, true);
					fileWriter.write(Helper.getStringTimeToFormat("yyyy-MM-dd HH:mm") + logName + "\r\n");
					fileWriter.flush();
					fileWriter.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 写入下载日志
	 * @param msg "yyyy_MM_dd_HH_mm_ss_upload or download_msg_....."
	 * @return
	 */
	public void writerDownloadTask(final String msg, final boolean isDownloadTask) {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				File downloadTaskFile = new File(getLogFolder(), getDownloadTaskFileName(true));
				StringBuffer stringBuffer = new StringBuffer(3);
				stringBuffer.append(Helper.getStringTimeToFormat("yyyy_MM_dd_HH_mm_ss"));
				stringBuffer.append("_");
				if (isDownloadTask) {
					stringBuffer.append("download");
				} else {
					stringBuffer.append("upload");
				}
				stringBuffer.append("_");
				stringBuffer.append(msg);
				stringBuffer.append("\r\n");
				try {
					writerContentToFile(downloadTaskFile.getAbsolutePath(), stringBuffer.toString(), true);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * 写入数据
	 * @param filePath  文件路径
	 * @param contentMsg 写入的内容
	 * @param isAppend 是否追加
	 * @return  返回成功的路径
	 * @throws IOException 抛出异常
	 */
	public static String writerContentToFile(String filePath, String contentMsg, boolean isAppend) throws IOException {
		FileWriter fileWriter = new FileWriter(filePath, isAppend);
		fileWriter.write(contentMsg);
		fileWriter.flush();
		fileWriter.close();
		return filePath;
	}
	
	
	/**
	 * @param filePath 文件的路径
	 * @return 路径获取String数据
	 */
	public static String readStringToPath(String filePath) {
		try {
			File programFile = new File(filePath);
			if (!programFile.exists()) {
				return null;
			}
			FileInputStream fileInputStream = new FileInputStream(programFile);
			BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(fileInputStream));
			StringBuffer stringBuffer = new StringBuffer();
			String temp = null;
			while ((temp = bufferedReader.readLine()) != null) {
				stringBuffer.append(temp);
			}
			bufferedReader.close();
			fileInputStream.close();
			return stringBuffer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getDownloadTaskFileName(boolean isWriterFile) {
		if (isWriterFile) {
			return  Constant.DOWNLOAD_TASK_PATH + Helper.getStringTimeToFormat("yyyy_MM_dd") + "_" + ConfigSettings.getDeviceId() + ".log";
		}
		return  Constant.DOWNLOAD_TASK_PATH + getCurrentFrontDate(-1) + "_" + ConfigSettings.getDeviceId() + ".log";
	}
	
	/**
	 * 获取当前时间的前n天日期
	 * @param front
	 * 			前多少天负数,后多少天正数,例如:当前日期20150928 传入-2则返回20150926,传入2则返回20150930
	 * @return
	 */
	private static String getCurrentFrontDate(int front) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd");
        Calendar c = Calendar.getInstance();  
        c.add(Calendar.DATE, front);  
        String preMonday = sdf.format(c.getTime());
        return preMonday;
	}
	
	/**
	 * 获取昨天的下载任务
	 * @return
	 */
	public String getDownloadTaskPath() {
		File downloadTaskFile = new File(getLogFolder(), getDownloadTaskFileName(false));
		return downloadTaskFile.getAbsolutePath();
	}
	
	public void uploadFlowInfo() {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				File filePath = new File(getTempFolder(), getFlowFileName());
				long downloadFlow = FlowManager.getInstance().getDownloadFlow();
				long uploadFlow = FlowManager.getInstance().getUploadFlow();
				FlowBo flowBo = new FlowBo();
				flowBo.downloadFlow = downloadFlow;
				flowBo.uploadFlow = uploadFlow;
				try {
					String flowFile = writerContentToFile(filePath.getAbsolutePath(),
							JSON.toJSONString(flowBo), false);
					//读取当前流量的使用情况 上报到服务器
					Intent uploadAction = new Intent(UploadLogReceiver.ACTION);
					uploadAction.putExtra(Constant.LOG_PATH, flowFile);
					DownloadApp.getContext().sendBroadcast(uploadAction);
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
		});
	}

	private static String getFlowFileName() {
		StringBuffer buffer = new StringBuffer(6);
		buffer.append(DOWNLOAD_FLOW_INFO);
		buffer.append("_");
		buffer.append(getCurrentFrontDate(-1));
		buffer.append("_");
		buffer.append(ConfigSettings.getDeviceId());
		buffer.append(".info");
		return buffer.toString();
	}
	
	/**
	 * 获取错误日志格式
	 * @param appName 应用名
	 * @param errorMessage 错误信息
	 * @return 日志格式
	 */
	public static String setErrorLog(String appName,String errorMessage){
		return Constant.ERROR_SPLIT + appName + Constant.SPLIT + errorMessage;
	}
	
	/**
	 * 写入日志
	 * @param errMsg
	 * 			错误信息
	 * @param logFilePath
	 * 			错误信息的file路径
	 */
	public void writerLog(final String errMsg, final String logFilePath) { 
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				try {
					File folderFile = new File(getLogFolder());
					if (!folderFile.exists()) {
						folderFile.mkdirs();
					}
					File logFile = new File(logFilePath);
					if (!logFile.exists()) {
						logFile.createNewFile();
					}
					FileOutputStream fos = new FileOutputStream(logFilePath, true);
					fos.write(errMsg.getBytes());
					fos.close();
					//主动上传错误日志到七牛
					UploadTask uploadTask = new UploadTask(null);
					uploadTask.executeUpload(logFilePath, FileConstant.BUCKET_TYPE_LOG);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	/***
	 * 获取节目列表里面的节目文件,节目的文件名是pid_pver.json
	 * 节目列表的文件名是固定的叫eposter_pl.json
	 */
	public String getProgrameListFolderPath(String prmId) {
		
		return getSdcard() + PATH_PRM + File.separator + prmId;
	}

	/**
	 * 获取视频文件存放路径
	 * 
	 * @return 视频文件存放路径
	 */
	public String getVideoFolderPath() {
		String path = getSdcard() + PATH_VIDEO;
		File folder = new File(path);
		folder.mkdirs();
		
		return path;
	}
	
	public String getShotFolderPath() {
		String path = getSdcard() + PATH_SHOT;
		File folder = new File(path);
		folder.mkdirs();
		return path;
	}
	
	/**
	 * 去掉文件名后缀
	 * 
	 * @param filename
	 *            文件名
	 * @return
	 */
	public static String getFileNameWithoutSuffix(String filename) {
		return filename.substring(0, filename.lastIndexOf("."));
	}

	public static void delete(final String path) {
		ThreadPoolManager.get().addRunnable(new Runnable() {
			@Override
			public void run() {
				File f1 = new File(path);
				if (f1.isDirectory()) {
					String[] fileList = f1.list();
					if (null == fileList) {
						return;
					}
					for (String s : fileList) {
						File f2 = new File(f1, s);
						if (f2.isDirectory()) {
							delete(f2.getAbsolutePath());
						} else {
							f2.delete();
							LogUtils.d(TAG, "delete", s);
						}
					}
					f1.delete();
				} else {
					f1.delete();
				}	
			}
		});
	
	}
	
	/**
	 * 删除空文件夹
	 * @param path 删除文件的路径
	 */
	public static void deleteDirectory(String path) {
		File f1 = new File(path);
		f1.delete();
	}
	
	public static String getFileNameToCloudPath(String cloudPath) {
		if (null == cloudPath) {
			return null;
		}
		return cloudPath.substring(cloudPath.lastIndexOf("/")+1, cloudPath.length());
	}
	/**
	 * 更改文件名
	 */
	public void renamePl(String prmId) {
		String plTempPath = getProgrameListFolderPath(prmId) + TEMP_SUFFIX_NAME;
		LogUtils.d(TAG, "renamePl", "plTempPath:" + plTempPath);
		File file = new File(plTempPath);
		if (file.exists()) {
			File prmFile = new File(file.getParent() + File.separator + prmId + PRM_SUFFIX_NAME );
			if (prmFile.exists()) {
				prmFile.delete();
			}
			file.renameTo(prmFile); 
		} else {
			LogUtils.d(TAG, "renamePl", "not file");
		}
	}
	
	/**
	 * 获取七牛断点上传的文件夹
	 * @return 文件夹的路径
	 */
	public String getQiNiuRecorderFolder() {
		return getSdcard() + PATH_QN_RECOR;
	}
	
}
