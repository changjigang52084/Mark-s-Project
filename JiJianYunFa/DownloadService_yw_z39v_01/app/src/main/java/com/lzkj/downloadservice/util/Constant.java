package com.lzkj.downloadservice.util;

import android.net.Uri;

import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.baize.adpress.core.common.constant.protocol.DownloadStateConstant;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年6月5日 下午2:50:16 
 * @version 1.0 
 * @parameter  常量类
 */
public class Constant {
	//"192.168.0.124:8080" //需要替换掉124服务器
	public final static String TEST_SERVER = "192.168.0.124:7070";
	/**定时上传日志的间隔时间*/
	public static long UPLOAD_LOG_TIME = 60 * 60 * 1000;
	/**连接请求时间*/
	protected static final int CONNECT_TIME_OUT = 30000;
	/**延迟重试时间*/
	protected static final long RETRY_SLEEP_TIME = 2000;
	/**获取七牛上传 token*/
	public static String GET_UPLOAD_TOKEN_URL = "http://%s//mallappss-message/synchronizations/devices/token/upload";
//	public static String GET_UPLOAD_TOKEN_URL = "http://%s/folder/returnUploadToken.action";
	/**服务器地址*/
//	public static String SERVER_IP =  "192.168.0.108:8080";
	/**获取七牛下载token*/
	public static String  GET_DOWNLOAD_TOKEN_URL = "http://%s/adpress-message/management/syncQueryDownloadToken.do";
//	public static String  GET_DOWNLOAD_TOKEN_URL = "http://%s/folder/returnDownloadToken.action";
	/***
	 * ResponseDownloadService
	 */
	/**UI apk NotifyLayoutService的详细类名*/
	public static final String UI_NOTIFYLAYOUTSERVICE_CLS = "com.lzkj.ui.service.NotifyLayoutService";
//	public static final String UI_NOTIFYLAYOUTSERVICE_CLS = "com.lzmr.mallposter.service.NotifyLayoutService";
	/**更新节目的action*/
	public static final String UI_NOTIFY_ACTION = "com.lzkj.ui.NOTIFY_PRM_ACTION";
	/**UI apk的包名*/
	public static final String UI_PKG = "com.lzkj.ui";
//	public static final String UI_PKG = "com.lzmr.mallposter";
	
	/**控制 apk的包名*/
	public static final String CTRL_PKG = "com.lz.ds.ctrl";
	/**通讯 apk的包名*/
	public static final String COMMUNIACTION_PKG = "com.lzkj.aidlservice";
	
	/**阿里云储存bucket*/
	public static final String OSS_BUCKET_NAME = "com-kchang";
	/**阿里云存储bucket host id*/
	public static final String OSS_BUCKET_HOST_ID = "oss-cn-shenzhen.aliyuncs.com";
	
	/**截图阿里云存储根目录**/
	public static final String SCREENSHOT_CLOUD_FOLDER = "screenshot";
	/**接收截图失败或者成功的action**/
	public static final String SCREENSHOT_HANDLER_ACTION = "com.lzkj.aidlservice.SCREENSHOT_HANDLER_ACTION";
	/**上传类型**/
	public static final int UPLOAD_TYPE = 1;
	/**上传空间为截图空间类型**/
	public static final int BUCKET_TYPE = 2;
	/**七牛下载类型*/
	public static final int QINIU_DOWNLOAD_TYPE = 2;
	/**下载链接失效时间(单位秒)*/
	public static final int QINIU_DOWNLOAD_OUTTIME = 7200;
	/**是否使用七牛云*/
	public static final boolean IS_QINIU = true;
	/**开始下载*/
	public static final int START_DOWNLOAD = DownloadStateConstant.DOWNLOAD_STATE_START;
	/**下载成功*/
	public static final int SUCCESS_DOWNLOAD = DownloadStateConstant.DOWNLOAD_STATE_SUCCESS;
	/**下载失败*/
	public static final int FAIL_DOWNLOAD = DownloadStateConstant.DOWNLOAD_STATE_ERROR;
	/**命令执行完成**/
	public static final int COMMAND_EXECUTED_SUCCESS = CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS;
	/**buckey type apressdev**/
	public static final int BUCKEY_TYPE_APRESSDEV           					    = 1;
	/**buckey_type_screenshot**/
	public static final int BUCKEY_TYPE_SCREENSHOT           					    = 2;
	/**buckey_type_sample**/
	public static final int BUCKEY_TYPE_SAMPLE           					    	= 3;
	/**buckey_type_appinfo**/
	public static final int BUCKEY_TYPE_APPINFO           					    	= 4;
	
	/*******ResposeDownloadService class********/
	/**下载节目列表的地址*/
	public static final int DOWNLOAD_PRMLIST = 1;
	/**下载素材和节目文件**/
	public static final int DOWNLOAD_PRMFILE = 2;
	/**下载文件 不进行md5校验**/
	public static final int DOWNLOAD_FILE = 4;
	/**下载APP**/
	public static final int DOWNLOAD_APP = 3;
	
	
	/**未下载状态*/
	public static final Integer NOT_DOWNLOAD_STATUE = 1;
	/**下载类型 app*/
	public static final long DOWNLOAD_TYPE_APP = 1;
	/**下载类型 节目*/
	public static final long DOWNLOAD_TYPE_PAROGRAM = 2;
	
	/**下载类型 1.添加下载，2.恢复下载，3.取消下载**/
	public static final String DOWNLOAD_TYPE_KEY = "downloadType";
	/**恢复下载**/
	public static final int RECOVERY_DOWNLOAD = 0;
	/**追加下载**/
	public static final int APPEND_DOWNLOAD = 1;
	/**取消下载**/
	public static final int CANCEL_DOWNLOAD = 2;
	/**取消下载文件**/
	public static final int CANCEL_DOWNLOAD_FILE = 3;
	
	/**网络断开**/
	public static final int NETWORK_DISCONNECT = 4;
	/**网络正常**/
	public static final int NETWORK_CONNECT = 5;
	
	
	
	/**静默安装APP命令*/
	public final static int SILENCE_INSTALL = 5;
	
	/**响应命令状态的action*/
	public final static String REPORT_STATE_ACTION = "com.lzkj.cmd.REPORT_STATE_ACTION";
	
	/**命令执行成功*/
	public final static int SUCCESS_CODE = 1;
	/**命令执行失败*/
	public final static int FAIL_CODE = -1;
	
	public static final String AUTHORITY = "com.lzkj.aidlservice.provider.MallContentProvider";
	public static final Uri CONTENT_URI_WEIBO = Uri.parse("content://"
			+ AUTHORITY + "/tbl_weibo");
	public static final Uri CONTENT_URI_COMMENT = Uri.parse("content://"
			+ AUTHORITY + "/tbl_comment");
	/**app路径和md5 分隔符*/
	public static final String SPLIT_APP_PATH = "##@@##";
	/**下载回执key*/
	public static final String DOWNLOAD_REPORT_KEY = "download_report";
	/***
	 * 错误日志开头分割线
	 */
	public static final String ERROR_SPLIT = "#&#";
	/***
	 * 分割线
	 */
	public static final String SPLIT = "|";
	
	/**下载路径（文件夹）**/
	public static final String DOWNLOAD_FOLDER = "downloadFolder";
	/**logPath**/
	public static final String LOG_PATH = "logPath";
	
	public static final String DOWNLOAD_TASK_PATH = "DOWNLOAD_TASK_TYPE_";
	
	/**取消下载素材文件列表**/
	public static final String CANCEL_DOWNLOAD_FILE_LIST = "cancelDownloadFileList";
	/**恢复素材文件url列表**/
	public static final String RECOVERY_DAMAGE_FILE_LIST = "fileUrlList";
	/**
	 * 节目key
	 */
	public static final String PROGRAM_KEY = "programKey";
	/**
	 * 素材类型
	 */
	public static final String MATERIAL_TYPE = "materialType";
	/**
	 * 素材名称
	 */
	public static final String MATERIAL_NAME = "materialName";
	/**
	 * 素材MD5值
	 */
	public static final String MATERIAL_MD5 = "materialMD5";
	
	/**server key**/
	public final static String KEY_SERVER = "server";
	/***
	 * 素材下载成功以后发生的广播
	 */
	public static final String MATERIAL_REPAIR_SUCCESS_ACTION = "com.lzkj.ui.MATERIAL_REPAIR_SUCCESS_ACTION";

}
