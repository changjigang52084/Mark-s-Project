package com.lzkj.downloadservice.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.aliyun.mbaas.oss.model.AccessControlList;
import com.aliyun.mbaas.oss.storage.OSSBucket;
import com.aliyun.mbaas.oss.storage.OSSFile;
import com.baize.adpress.core.common.constant.protocol.CommandStateConstant;
import com.lzkj.aidl.DownloadAIDL;
import com.lzkj.aidl.NotifyLayoutAIDL;
import com.lzkj.downloadservice.R;
import com.lzkj.downloadservice.app.DownloadApp;
import com.lzkj.downloadservice.bean.DownloadBean;
import com.lzkj.downloadservice.bean.DownloadBo;
import com.lzkj.downloadservice.bean.DownloadInfo;
import com.lzkj.downloadservice.bean.RecoveryDownloadBean;
import com.lzkj.downloadservice.db.SQLiteManager;
import com.lzkj.downloadservice.download.DownloadInfoManager;
import com.lzkj.downloadservice.download.DownloadManager;
import com.lzkj.downloadservice.download.HttpDownloadTask;
import com.lzkj.downloadservice.impl.DownloadAppImpl;
import com.lzkj.downloadservice.interfaces.IDownloadStateCallback;
import com.lzkj.downloadservice.qiniu.impl.QiNiuDownloadTokenCallback;
import com.lzkj.downloadservice.util.AppUtil;
import com.lzkj.downloadservice.util.ConfigSettings;
import com.lzkj.downloadservice.util.Constant;
import com.lzkj.downloadservice.util.DownloadStateReportTools;
import com.lzkj.downloadservice.util.FileUtil;
import com.lzkj.downloadservice.util.FlowManager;
import com.lzkj.downloadservice.util.Helper;
import com.lzkj.downloadservice.util.LogUtils;
import com.lzkj.downloadservice.util.LogUtils.LogTag;
import com.lzkj.downloadservice.util.PrmFileHashUtil;
import com.lzkj.downloadservice.util.ProgramParseTools;
import com.lzkj.downloadservice.util.ReportDownloadState;
import com.lzkj.downloadservice.util.ShreadUtil;
import com.lzkj.downloadservice.util.ThreadPoolManager;

import java.io.File;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 接受下通讯apk发过来的下载任务列表
 *
 * @author changkai
 */
public class ResposeDownloadService extends Service implements IDownloadStateCallback, QiNiuDownloadTokenCallback {
    /**
     * 下载管理类
     */
//	private DownloadManager downloadManager;
    private static final LogTag TAG = LogUtils.getLogTag(ResposeDownloadService.class.getSimpleName(), true);

    private static final int SLEEP_TIME = 2000;

    private static final int SLEEP_FIVE_TIME = 5000;

    /**
     * 下载中
     ***/
    public static final int STATE_DOWNLOADING = 1;
    /**
     * 下载失败
     ***/
    public static final int STATE_DOWNLOAD_FAILED = -1;
    /**
     * 下载成功
     ***/
    public static final int STATE_DOWNLOAD_SUCCESS = 2;
    /**
     * 全部下载成功
     ***/
    public static final int STATE_DOWNLOAD_ALL_SUCCESS = 3;
    /**
     * 取消下载
     ***/
    public static final int STATE_DOWNLOAD_CANCEL = -2;

    /**
     * 正在下载
     ***/
    private static final int DOWNLOADING = 0;

    /**
     * 下载完成
     ***/
    private static final int DOWNLOAD_OVER = -1;

    /**
     * 下载类型为 Constant.DOWNLOAD_FILE 的文件总数
     */
    private int downloadFileTypeSize = 0;
    /**
     * 已下载类型为 Constant.DOWNLOAD_FILE个数
     */
    private int downloadFileTypeIndex = 0;
    /**
     * 下载成功的文件类型为Constant.DOWNLOAD_FILE http列表
     **/
    private ArrayList<String> downloadFileTypeHttpList;
    /**
     * 下载的http任务
     **/
    private HttpDownloadTask httpDownloadFile = null;
    /**
     * 下载单个文件的进度
     */
    private int downloadProgress = 0;
    /**
     * 临时保存下载进度，用于计算下载速度
     */
    private int tempProgress;
    /**
     * 临时保存上次的时间，用于计算下载速度
     */
    private long tempTime;
    /***资源连接有效时间2个小时*/
    private static final int RES_LINK_VALID_TIME = 1800 * 4;
    /**
     * 阿里云存储对象
     */
    public OSSBucket sampleBucket;
    /**
     * 下载任务的总数
     */
    private int downloadSize = 0;
    /**
     * 下载的次数
     */
    private int downloadNum = 0;
    /**
     * 临时保存下载文件的类型
     */
//	private int tempSaveType  = 0;
    private NotifyLayoutAIDL notifyLayoutAIDL;

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private RelativeLayout debugView;
    private ProgressBar progressBar;

    /**
     * 下载列表
     **/
    private List<DownloadBean> downloadBeanList;
    /**
     * 标志位判断当前是否在下载中
     */
    private boolean ifDownloading = false;
    /**是否显示下载进度*/
//	private boolean ifShowProg = true;
    /**
     * 记忆开始下载的开始时间
     */
    private long startTime = 0;
    /**
     * windows view if exist view,true exist,other not exist
     **/
    private boolean windowsViewIfExistView = false;
    /**
     * 判断当前的service是否为第一次启动
     */
//	private int index = 0;
    private List<String> tempSaveDownloadList;

    /**
     * 保存节目列表和节目对应的节目id
     **/
    private Map<String, List<String>> prmMap;
    /**
     * 保存节目列表和节目对应的节目id
     **/
    private Map<String, List<String>> cachePrmMap;
    private int currentPrmDownloadNum;//当前节目下载的素材个数
    /**
     * 缓存当前正在下载的节目列表id
     */
    private String cachePrmId;
    /**
     * 已经完成下载的节目列表id
     **/
    private List<String> doneDownloadPrm;
    /**
     * 下载失败的节目列表id
     **/
    private List<String> failedDownloadPrm;
    /**
     * 等待下载的节目列表id
     **/
    private List<String> waitDownloadPrm;

    /**
     * 所有下载的任务列表
     **/
    private Set<String> allDownloadTaskList;
//	
//	/**当前所有的正在下载的文件列表**/
//	private List<String> mCurrentAllDownloadFiles;

    private TextView fileName_tv, speed_tv, per_tv, download_size_tv, file_download_count;

    /**
     * 网络状态图标
     **/
    private ImageView mNetworkStateImg;
    /**
     * 下载状态图标
     **/
    private ImageView mDownloadStateImg;

    /**
     * 接受发送过来的下载任务列表
     */
    private DownloadAIDL.Stub downloadAIDL = new DownloadAIDL.Stub() {
        @Override
        public void onCancel() throws RemoteException {
            DownloadManager.newInstance().cancelAllTask();
            LogUtils.d(TAG, "onCancel", "cancel task..");
        }

        @Override
        public void onDownloadList(List<String> downloadList, int type, String prmId) throws RemoteException {
            addDownloadPrmFile(downloadList, type, prmId);
        }
    };

    /**
     * 添加下载任务
     *
     * @param downloadList
     * @param type         任务类型
     * @param prmId        节目id
     */
    private void addDownloadPrmFile(final List<String> downloadList, final int type, final String prmId) {

        ThreadPoolManager.get().addRunnable(new Runnable() {
            @Override
            public void run() {
                if (!downloadList.isEmpty()) {
                    if (type == Constant.DOWNLOAD_APP) {
                        new DownloadAppImpl().downloadApps(downloadList);
                        return;
                    }
                    clearRepeat(downloadList);
                    ProgramParseTools.getCanDownloadList(downloadList, prmId, type);
                    if (!downloadList.isEmpty()) {
                        allDownloadTaskList.addAll(downloadList);
                        FileUtil.getInstance().writerDownloadTask("addDownloadPrmFile_" + JSON.toJSONString(downloadList), true);
                        checkIsDownloadApp(type, downloadList);
                        downloadSize += downloadList.size();
                        LogUtils.d(TAG, "onDownloadList", "download task..downloadSize: " + downloadSize + " ,prmId: " + prmId + " ,prmMap.size: " + prmMap.size());
                        cachePrmMap.put(prmId, downloadList);
                        ConfigSettings.savePrmKey(cachePrmMap);
                        PrmFileHashUtil.get().init(prmId);//初始化保存素材hash
                        if (prmMap.isEmpty()) {//判断map里面是否为空，如果为空则进行添加下载素材
                            cachePrmId = prmId;//保存当前正在下载的节目id
                            ConfigSettings.saveCurrentPrmKey(cachePrmId);
                            download(downloadList, type, true, null);
                        } else {
                            prmMap.put(prmId, downloadList);//保存节目列表id和素材下载列表
                            waitDownloadPrm.add(prmId);//保存所有要下载的节目列表id
                        }
                    }
                } else {
                    LogUtils.d(TAG, "onDownloadList", "downloadList is null,prmId:" + prmId);
                }
            }
        });
    }

    /**
     * 去除重复的下载列表
     *
     * @param downloadList
     */
    private void clearRepeat(List<String> downloadList) {
        downloadList.removeAll(allDownloadTaskList);
    }

    /**
     * 判断是否为下载app
     *
     * @param type         下载类型
     * @param downloadList 下载列表
     */
    private void checkIsDownloadApp(int type, List<String> downloadList) {
        if (type == Constant.DOWNLOAD_APP) {
//			ifShowProg = false;
            List<String> tempList = new ArrayList<String>();
            for (String appPath : downloadList) {
                String md5 = appPath.split(Constant.SPLIT_APP_PATH)[1];
                String downloadPath = appPath.split(Constant.SPLIT_APP_PATH)[0];
                String fileName = FileUtil.getFileName(downloadPath);
                ShreadUtil.newInstance().putString(fileName, md5);
                tempList.add(downloadPath);
                LogUtils.d(TAG, "onDownloadList", "key:" + md5 + ",downloadPath:" + downloadPath + ",fileName:" + fileName);
            }
            downloadList.clear();
            downloadList.addAll(tempList);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        init();
        initWindow();
    }

    /**
     * 初始化
     */
    private void init() {
        prmMap = new HashMap<String, List<String>>();
        cachePrmMap = new HashMap<String, List<String>>();
        doneDownloadPrm = new ArrayList<String>();
        waitDownloadPrm = new ArrayList<String>();
        downloadBeanList = new ArrayList<DownloadBean>();
        failedDownloadPrm = new ArrayList<String>();
        tempSaveDownloadList = new ArrayList<String>();
        downloadFileTypeHttpList = new ArrayList<String>();
        allDownloadTaskList = new HashSet<String>();
        bindNotifyService();
        sampleBucket = new OSSBucket(Constant.OSS_BUCKET_NAME);
        sampleBucket.setBucketHostId(Constant.OSS_BUCKET_HOST_ID); //可以在这里设置数据中心域名或者cname域名
        sampleBucket.setBucketACL(AccessControlList.PRIVATE);
    }

    /**
     * 绑定更新界面的service
     */
    private void bindNotifyService() {
        notifyLayoutAIDL = null;
        if (Helper.isInstallApp(DownloadApp.getContext(), Constant.UI_PKG)) {
            Intent service = new Intent();
            service.setComponent(new ComponentName(Constant.UI_PKG,
                    Constant.UI_NOTIFYLAYOUTSERVICE_CLS));
            boolean flag = DownloadApp.getContext()
                    .bindService(service, notifyConnection, Context.BIND_AUTO_CREATE);
            if (!flag) {
                bindNotifyService();
            }
        }
    }

    /**
     * 初始化调试窗口
     */
    private void initWindow() {
        windowManager = (WindowManager) getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.gravity = Gravity.BOTTOM;
        layoutParams.x = 0;
        layoutParams.y = 0;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = 29;

        debugView = (RelativeLayout) LayoutInflater.from(getApplicationContext())
                .inflate(R.layout.debug_view_layout, null, false);
        progressBar = (ProgressBar) debugView.findViewById(R.id.progressBar);
        fileName_tv = (TextView) debugView.findViewById(R.id.file_name_tv);
        speed_tv = (TextView) debugView.findViewById(R.id.speed_tv);
        per_tv = (TextView) debugView.findViewById(R.id.progresss_per_tv);
        download_size_tv = (TextView) debugView.findViewById(R.id.download_size_tv);
        file_download_count = (TextView) debugView.findViewById(R.id.file_size_tv);

        mNetworkStateImg = (ImageView) debugView.findViewById(R.id.img_network_state_icon);
        mDownloadStateImg = (ImageView) debugView.findViewById(R.id.img_download_state_icon);
//    	mDownloadStateImg.setBackgroundResource(R.drawable.download_state_anim);
//    	IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//    	registerReceiver(mNetworkStateReceiver, intentFilter);

        windowManager.addView(debugView, layoutParams);
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        try {
            if (null != intent) {
                int typeKey = intent.getIntExtra(Constant.DOWNLOAD_TYPE_KEY, -1);
                LogUtils.d(TAG, "onStartCommand", "typeKey: " + typeKey);
                switch (typeKey) {
                    case Constant.RECOVERY_DOWNLOAD:
                        recoverDownload(intent);
                        break;
                    case Constant.APPEND_DOWNLOAD:
                        appendDownloadToIntent(intent);
                        break;
                    case Constant.CANCEL_DOWNLOAD:
//				cancelDownloadToIntent(intent);
                        break;
                    case Constant.CANCEL_DOWNLOAD_FILE:
//				cancelDownloadFile(intent);
                        break;
                    case Constant.NETWORK_DISCONNECT:
                        mHandler.obtainMessage(Constant.NETWORK_DISCONNECT).sendToTarget();
                        break;
                    case Constant.NETWORK_CONNECT:
                        mHandler.obtainMessage(Constant.NETWORK_CONNECT).sendToTarget();
                        break;
                    default:
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 恢复下载
     *
     * @param intent
     */
    private void recoverDownload(Intent intent) {
        ArrayList<RecoveryDownloadBean> downloadBeans = intent.getParcelableArrayListExtra(RecoveryDownloadTaskService.DOWNLOAD_LIST);
        for (RecoveryDownloadBean recoveryDownloadBean : downloadBeans) {
            int type = recoveryDownloadBean.getType();
            if (type != -1) {
                ArrayList<String> downloadList = recoveryDownloadBean.getList();
                if (null != downloadList && !downloadList.isEmpty()) {
                    downloadSize += downloadList.size();
                    if (Constant.DOWNLOAD_FILE == type) {
//						ifShowProg = false;
                        downloadFileTypeSize += downloadList.size();
                    }
                    recoveryPrmKey();
                    PrmFileHashUtil.get().addAllPrmHash();
                    download(downloadList, type, false, recoveryDownloadBean.getSaveFoldePath());
                }
            }
        }
    }

    /**
     * 恢复prm key
     */
    private void recoveryPrmKey() {
        Map<String, List<String>> prmKeyMap = ConfigSettings.getPrmKey();
        if (null != cachePrmMap && cachePrmMap.isEmpty()) {
            cachePrmMap = prmKeyMap;
        } else {
            if (null != prmKeyMap && !prmKeyMap.isEmpty()) {
                cachePrmMap.putAll(prmKeyMap);
            }
        }
        if (TextUtils.isEmpty(cachePrmId)) {
            cachePrmId = ConfigSettings.getCurrentPrmKey();
        }
    }

    /**
     * 追加下载到下载列表
     *
     * @param intent
     */
    private void appendDownloadToIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        if (null != bundle) {
            ArrayList<DownloadBo> downloadBos = bundle.getParcelableArrayList(RecoveryDownloadTaskService.DOWNLOAD_LIST);
            if (null != downloadBos && !downloadBos.isEmpty()) {
                for (DownloadBo downloadBo : downloadBos) {
                    if (null != downloadBo) {
                        ArrayList<String> downloadList = downloadBo.getHttpUrls();
                        if (null == downloadBo.getPrmId()) {
                            downloadList = removeRepeatDownloadUrl(downloadList);
                            if (null != downloadList && !downloadList.isEmpty()) {
                                downloadSize += downloadList.size();
                                downloadFileTypeSize += downloadList.size();
                                FileUtil.getInstance()
                                        .writerDownloadTask("appendDownloadToIntent_" + JSON.toJSONString(downloadList), true);
                                download(downloadList, Constant.DOWNLOAD_FILE, true, downloadBo.getSaveLocalFoldePath());
                            }
                        } else {
                            addDownloadPrmFile(downloadList, downloadBo.getType(), downloadBo.getPrmId());
                        }
                    }
                }
//				 ifShowProg = false;
            } else {
                //下载列表为空提示下载失败。。
                DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOAD_FAILED, "download filed", null);
            }
        } else {
            //下载列表为空提示下载失败。。
            DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOAD_FAILED, "download filed", null);
        }
    }

    /**
     * 去除重复的下载文件
     *
     * @param downloadList
     */
    private ArrayList<String> removeRepeatDownloadUrl(ArrayList<String> downloadList) {
        LogUtils.d(TAG, "removeRepeatDownloadUrl", "start downloadList.size :" + downloadList.size());
        LogUtils.d(TAG, "removeRepeatDownloadUrl", "start downloadList:" + downloadList);
        ArrayList<RecoveryDownloadBean> recoveryAllDownloadFiles = SQLiteManager.getInstance().getDownloadFiles();
        ArrayList<String> notDownloadList = new ArrayList<String>();//未下载的列表
        for (RecoveryDownloadBean recoveryDownloadBean : recoveryAllDownloadFiles) {
            notDownloadList.addAll(recoveryDownloadBean.getList());
        }
        //key保存无参数的url, value是带参数的url
        Map<String, String> notParamKeyMap = new HashMap<String, String>();
        try {
            List<String> notParamList = Helper.getDownloadHttpUrls(downloadList);
            int size = notParamList.size();
            for (int i = 0; i < size; i++) {
                notParamKeyMap.put(notParamList.get(i), downloadList.get(i));
            }
            LogUtils.d(TAG, "removeRepeatDownloadUrl", "add download Urls :" + notParamList);
            //未下载的列表
            List<String> notDownloadListNotParam = Helper.getDownloadHttpUrls(notDownloadList);
            //以下完成的列表
            List<String> downloadOverListNotParam = Helper.getDownloadHttpUrls(downloadFileTypeHttpList);

            notParamList.removeAll(notDownloadListNotParam);//去除要下载 已在队列中的未下载的任务
            notParamList.removeAll(downloadOverListNotParam);//去除要下载 已下载的任务
            //剩下的就是重新要下载的新任务
            ArrayList<String> newDownloadUrls = new ArrayList<String>();
            for (String notParamUrl : notParamList) {
                String srcUrl = notParamKeyMap.get(notParamUrl);
                if (!TextUtils.isEmpty(srcUrl)) {
                    newDownloadUrls.add(srcUrl);
                }
            }

//			LogUtils.d(TAG, "removeRepeatDownloadUrl", "end downloadList:" + downloadList);
            LogUtils.d(TAG, "removeRepeatDownloadUrl", "to download Urls :" + newDownloadUrls);
            LogUtils.d(TAG, "removeRepeatDownloadUrl", "not Download List :" + notDownloadListNotParam);
            LogUtils.d(TAG, "removeRepeatDownloadUrl", "download Over List :" + downloadOverListNotParam);
            sendRetryDownloadFileList(downloadOverListNotParam);
            downloadList = newDownloadUrls;

            notParamKeyMap.clear();
            notParamKeyMap = null;
            notDownloadListNotParam = null;
            downloadOverListNotParam = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

//		 downloadList.removeAll(notDownloadList);
//		 downloadList.removeAll(downloadFileTypeHttpList);

        LogUtils.d(TAG, "removeRepeatDownloadUrl", "end downloadList.size :" + downloadList.size());
        return downloadList;
    }

    /**
     * 将重复下载的成功的列表发送给mallposter
     *
     * @param downloadOverListNotParam
     */
    private void sendRetryDownloadFileList(List<String> downloadOverListNotParam) {
        if (null != downloadOverListNotParam && !downloadOverListNotParam.isEmpty()) {
            DownloadStateReportTools
                    .sendDownloadFileStateReceive(STATE_DOWNLOAD_ALL_SUCCESS,
                            "", new ArrayList<String>(downloadOverListNotParam));
        }
    }

    /**
     * 是否下载完
     */
    private void isDownloadOver() {
        downloadFileIsOver();
        if (downloadNum == downloadSize || downloadNum > downloadSize) {//隐藏进度条,并且更新节目列表
            downloadNum = 0;
            downloadSize = 0;
            renamePrmName();
        }
    }

    /**
     * 是否显示debug窗口
     */
    private void ifShowProg() {
//		if (ifShowProg) {
//			ifShowProg = false;
        mHandler.obtainMessage(DOWNLOADING).sendToTarget();
//			LogUtils.d(TAG, "ifShowProg", "ifShowProg:"+ifShowProg);
//		}
    }

    /**
     * 是否添加到数据库中保存
     *
     * @param downloadList url下载列表
     * @param type         下载类型
     * @param ifAddTask    是否添加到数据库保持记录 ifAddTask = true
     */
    private void ifAddTask(List<String> downloadList, int type, boolean ifAddTask, String folder) {
        if (ifAddTask) {
            // Method: ifAddTask, Message: downloadList : [http://dsplug-source.jjyunfa.com/1520056284360.jpg?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:MskxKkxkB4-aW6enDV-UolCXVoE=, http://dsplug-source.jjyunfa.com/1520056284360.jpg?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:MskxKkxkB4-aW6enDV-UolCXVoE=, http://dsplug-source.jjyunfa.com/1475748353349.mp4?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:SH7aiwyGRsq1jVC6jGut6xxnPGM=, http://dsplug-source.jjyunfa.com/1490800629848.mp4?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:p1Ql3DpAPvJJGxdQ20C08D3hkwU=, http://dsplug-source.jjyunfa.com/1472046742609.jpg?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:qGE9XsoZWPNhr-9sSAzds5rZLZM=, http://dsplug-source.jjyunfa.com/1472046707949.jpg?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:SouJZVsKMG0y9wnWLUPB5OBQFkc=, http://dsplug-source.jjyunfa.com/1474722606626.jpg?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:Wy_7hYccF4TuU3acdyU8UyZIQoo=, http://dsplug-source.jjyunfa.com/1491292500706.jpg?e=1534662322&token=zmt_xSEULDGgKmIuBbeSeRDqMsPMKjbs22qgGDqR:eeqoQ6UZznHnB4MPC_RReVGBujA=]
            LogUtils.d(TAG, "ifAddTask", "downloadList : " + downloadList);
            SQLiteManager.getInstance().addDownloadTask(downloadList, type, folder);
        }
    }

    /**
     * 添加下载
     *
     * @param downloadList 下载路径
     * @param type         下载类型
     * @param ifAddTask    是否添加到task任务列表中
     */
    private synchronized void download(List<String> downloadList, int type, boolean ifAddTask, String locleFolder) {
        LogUtils.d(TAG, "download", "downloadList: " + downloadList.size() + " ,type: "
                + type + " ,ifAddTask: " + ifAddTask + " ,downloadBeanSize: "
                + downloadBeanList.size());
        if (ifDownloading && !downloadBeanList.isEmpty()) {
            addToDownloadTaskList(type, ifAddTask, downloadList, locleFolder);
            LogUtils.d(TAG, "download", "ifDownloading:" + ifDownloading + "type:" + type + ",index:" + tempSaveDownloadList.size());
            return;
        }
        ifDownloading = true;
        ifShowProg();
        ifAddTask(downloadList, type, ifAddTask, locleFolder);

//		tempSaveType = type;
        if (Constant.IS_QINIU) {
            addQiNiuTask(downloadList, type, locleFolder);
        } else {
            addAliYunTask(downloadList, type);
        }
    }

    /**
     * 添加到临时队列中
     *
     * @param type         文件类型
     * @param falg         是否保存到数据库中
     * @param downloadList 素材云存储路径
     */
    private void addToDownloadTaskList(int type, boolean falg, List<String> downloadList, String locleFolder) {
        DownloadBean downloadBean = new DownloadBean();
        downloadBean.setDownload_type(type);
        downloadBean.setDownloadList(downloadList);
        downloadBean.setFlag(falg);
        downloadBean.setDownloadFolder(locleFolder);
        downloadBeanList.add(downloadBean);
    }

    /**
     * 添加阿里云储存下载任务
     *
     * @param downloadList
     * @param type
     */
    private void addAliYunTask(List<String> downloadList, int type) {
        HttpDownloadTask httpDownloadFile = new HttpDownloadTask();
        String locleFolder = null;
        if (Constant.DOWNLOAD_PRMLIST == type) {
            locleFolder = FileUtil.getInstance().getTempFolder();
        } else if (Constant.DOWNLOAD_APP == type) {
            locleFolder = FileUtil.getInstance().getAPPFolder();
        }
        httpDownloadFile.addDownloadListTask(convertPathToHttp(downloadList), this, locleFolder, type);
        httpDownloadFile.execu();
        downloadSize = downloadList.size();
        updateDownloadSize(downloadNum);
        DownloadManager.newInstance().addHttpDownload(httpDownloadFile);
    }

    /***
     * 将阿里云储存的路径转换成http地址
     * @param downloadList
     * @return
     */
    private List<String> convertPathToHttp(List<String> downloadList) {
        ArrayList<String> resUrls = new ArrayList<String>();
        for (String path : downloadList) {
            OSSFile ossFile = new OSSFile(sampleBucket, path);
            String resourceURL = ossFile.getResourceURL(DownloadApp.accessKey, RES_LINK_VALID_TIME);
            LogUtils.e(TAG, "convertPathToHttp", "resourceURL:" + resourceURL);
            resUrls.add(FileUtil.encodeUrl(resourceURL));
        }
        return resUrls;
    }

    /**
     * 添加七牛云储存下载任务
     *
     * @param downloadList
     * @param type
     */
    private void addQiNiuTask(List<String> downloadList, int type, String locleFolder) {
        // 下载app
        if (Constant.DOWNLOAD_APP == type) {
            HttpDownloadTask httpDownloadFile = new HttpDownloadTask();
            httpDownloadFile.addDownloadListTask(downloadList, this, FileUtil.getInstance().getAPPFolder(), type);
            httpDownloadFile.execu();
            downloadSize = downloadList.size();
            updateDownloadSize(downloadNum);
            DownloadManager.newInstance().addHttpDownload(httpDownloadFile);
            return;
        }
        //先注释方便调试
//		QiNiuGetDownloadToken downloadToken = new QiNiuGetDownloadToken(this);
//		downloadToken.downloadFile(downloadList, type);
        //直接下载素材
        downloadTokenList(downloadList, type, locleFolder);
    }

    /**
     * 根据七牛云url下载
     *
     * @param downloadList 七牛下载文件集合
     * @param type         文件下载类型
     * @param locleFolder  下载的本地路径(文件夹)
     */
    @Override
    public void downloadTokenList(List<String> downloadList, int type, String locleFolder) {
        if (null == httpDownloadFile) {
            httpDownloadFile = new HttpDownloadTask();
        }
        if (Constant.DOWNLOAD_PRMLIST == type) {
            locleFolder = FileUtil.getInstance().getTempFolder();
        } else if (Constant.DOWNLOAD_APP == type) {
            locleFolder = FileUtil.getInstance().getAPPFolder();
        }
        httpDownloadFile.addDownloadListTask(downloadList, this, locleFolder, type);
        httpDownloadFile.execu();
        LogUtils.d(TAG, "downloadTokenList", "downloadSize: " + downloadSize);
        updateDownloadSize(downloadNum);
        DownloadManager.newInstance().addHttpDownload(httpDownloadFile);
    }

    /**
     * 下载成功以后调用的方法
     */
    @Override
    public void onSuccess(String httpUrl, int totalSize, int downloadType) {
        LogUtils.d(TAG, "onSuccess", "httpUrl: " + httpUrl + " ,totalSize: " + totalSize + " ,downloadType: " + downloadType);
        downloadProgress = 0;
        String filename = FileUtil.getFileName(httpUrl);
        delDownloadTask(httpUrl);
        if (downloadType == Constant.DOWNLOAD_APP) {//下载App
            String appPath = FileUtil.getInstance().getAPPFolder() + File.separator + filename;
            AppUtil.installApkToAppPath(appPath);
        }
        if (Constant.DOWNLOAD_FILE == downloadType) {//下载单个文件
            downloadFileTypeIndex++;
            downloadFileTypeHttpList.add(httpUrl);
            DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOAD_SUCCESS, httpUrl, null);
            downloadFileIsOver();
        }
        downloadNum++;
        currentPrmDownloadNum++;
        if (null != cachePrmMap && !cachePrmMap.isEmpty() && null != cachePrmId) {
            int currentPrmNum = cachePrmMap.get(cachePrmId).size();
            if (currentPrmNum <= currentPrmDownloadNum) {
                currentPrmDownloadNum = 0;
                doneDownloadPrm.add(cachePrmId);//添加素材下载完以后的节目id
                FileUtil.getInstance().renamePl(cachePrmId);
                LogUtils.e(TAG, "onSuccess", "currentPrmNum <= currentPrmDownloadNum currentPrmNum: " + currentPrmNum + " ,cachePrmId: " + cachePrmId);
            }
        }
        updateDownloadSize(downloadNum);
        if (downloadSize == downloadNum) {//表示一个节目素材下载完成
            LogUtils.e(TAG, "onSuccess", "httpUrl: " + httpUrl + " ,downloadNum: " + downloadNum);
            downloadNum = 0;
            if (null != cachePrmId) {
                doneDownloadPrm.add(cachePrmId);//添加素材下载完以后的节目id
            }
            prmFileDoneHandler(httpUrl, true);//判断保存的节目列表里面是否有未下载完成的节目素材列表
        }
    }

    @Override
    public void onFail(String httpUrl, String errMsg, int downloadType) {
        LogUtils.d(TAG, "onFail ", "errMsg: " + errMsg + " ,httpUrl: " + FileUtil.getFileName(httpUrl));
        updateDownloadInfoSpeed(httpUrl, 0);
        updateSpeed();
        ifDownloading = false;
        failedDownloadPrm.add(cachePrmId);//添加下载失败的节目id到列表中
        downloadNum++;
        if (downloadSize == downloadNum) {//表示一个节目素材下载完成
            downloadNum = 0;
            //判断保存的节目列表里面是否有未下载完成的节目素材列表
            prmFileDoneHandler(httpUrl, false);
        }

        if (Constant.DOWNLOAD_FILE == downloadType) {
            downloadFileTypeIndex--;
            if (downloadFileTypeIndex < 0) {
                downloadFileTypeIndex = 0;
            }
            DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOAD_FAILED, httpUrl, null);
            downloadFileIsOver();
        }

        if (downloadType == Constant.DOWNLOAD_APP) {//下载App
            //告诉CommunicationApp下载失败
            responseExcuteCmdState(Constant.SILENCE_INSTALL,
                    Constant.FAIL_CODE,
                    DownloadApp.getContext(),
                    FileUtil.getFileName(httpUrl));
        }
    }

    /**
     * 下载类型为Constant.DOWNLOAD_FILE 是否全部下载完成
     */
    private void downloadFileIsOver() {
        LogUtils.d(TAG, "downloadFileIsOver", " ,downloadFileTypeSize: "
                + downloadFileTypeSize + " ,downloadFileTypeIndex: " + downloadFileTypeIndex);
        if (downloadFileTypeSize > 0) {
            if (downloadFileTypeIndex == downloadFileTypeSize
                    || downloadFileTypeIndex > downloadFileTypeSize) {
                downloadFileTypeSize = 0;
                downloadFileTypeIndex = 0;
                //提示所有下载完成
//				DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOAD_ALL_SUCCESS, "", downloadFileTypeHttpList);
            }
        }
    }

    /**
     * 节目素材下载完成
     *
     * @param httpUrl    当前下载http地址
     * @param isDownload
     */
    private void prmFileDoneHandler(String httpUrl, boolean isDownload) {
        LogUtils.e(TAG, "prmFileDoneHandler", "httpUrl: " + httpUrl + " ,isDownload: " + (isDownload ? "success" : "failed") + " ,prmMap: " + prmMap.toString());
        if (!prmMap.isEmpty()) {
            prmMap.remove(cachePrmId);//删除掉已经下载完成的节目列表
            //获取未下载完成的节目id
            String prmId = waitDownloadPrm.remove(0);
            cachePrmId = prmId;
            ConfigSettings.saveCurrentPrmKey(cachePrmId);
            LogUtils.d(TAG, "prmFileDoneHandler", "httpUrl: " + httpUrl + " ,prmMap is new prmId: " + prmId + " ,old prmId: " + cachePrmId);
            List<String> downloadFileList = prmMap.get(prmId);//获取需要下载的节目素材列表
            download(downloadFileList, Constant.DOWNLOAD_PRMFILE, true, null);//添加到下载列表中进行下载
        } else {
            LogUtils.d(TAG, "prmFileDoneHandler", "httpUrl: " + httpUrl + " ,prmMap is empty download over, done size is: " + doneDownloadPrm.size() + " ,download failed size is: " + failedDownloadPrm.size());
            DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOAD_ALL_SUCCESS, "", downloadFileTypeHttpList);
            notifyProgram(httpUrl);
            clearData();
        }
        ifDownloading = false;
    }

    /**
     * 清除数据
     */
    private void clearData() {
        allDownloadTaskList.clear();
        downloadFileTypeHttpList.clear();
        doneDownloadPrm.clear();
        failedDownloadPrm.clear();
        waitDownloadPrm.clear();
        prmMap.clear();
        PrmFileHashUtil.clearCache();
    }

    /**
     * 返回下载回执给服务器
     */
    private void reportDownloadState() {
        ReportDownloadState.getInstance().responseDownloadState(CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null, doneDownloadPrm, failedDownloadPrm);
        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                LogUtils.d(TAG, "reportDownloadState", "timerTask");
                ReportDownloadState.getInstance().responseDownloadState(CommandStateConstant.COMMAND_STATE_EXECUTED_SUCCESS, null, doneDownloadPrm, failedDownloadPrm);
            }
        };
        timer.schedule(timerTask, 2000);
    }

    /**
     * 删除数据库中保存的任务记录
     *
     * @param httpUrl
     */
    private void delDownloadTask(String httpUrl) {
        SQLiteManager.getInstance().delDownloadTask(FileUtil.getFileName(httpUrl));
    }

    /**
     * 下载完成以后提示Eposter更新节目
     *
     * @param httpUrl
     */
    private void notifyProgram(String httpUrl) {
        downloadNum = 0;
        downloadSize = 0;
        updateDownloadInfoSpeed(httpUrl, 0);
        updateView(httpUrl, "onSuccess");
        checkIfExitsTask();
        renamePrmName();
    }

    /**
     * 判断是否还有未下载完的任务,提示显示的apk更新节目
     */
    private void checkIfExitsTask() {
        if (downloadBeanList.size() > 0) {
            ifDownloading = false;
            DownloadBean downloadBean = downloadBeanList.remove(0);
            download(downloadBean.getDownloadList(), downloadBean.getDownload_type(), downloadBean.isFlag(), downloadBean.getDownloadFolder());
        } else {
            SQLiteManager.getInstance().delDownloadTaskTable();
        }
    }

    /**
     * 重命名节目文件
     */
    private void renamePrmName() {
        if (null != cachePrmMap && !cachePrmMap.isEmpty()) {
            for (String prmId : cachePrmMap.keySet()) {
                LogUtils.d(TAG, "renamePrmName", "prmId: " + prmId + " ,doneDownloadPrm.size: " + doneDownloadPrm.size());
                if (!doneDownloadPrm.contains(prmId)) {
                    doneDownloadPrm.add(prmId);
                }
                FileUtil.getInstance().renamePl(prmId);
            }
        }
        try {
            ShreadUtil.newInstance().removeKey(ShreadUtil.CURRENT_PRM_KEY);
            ShreadUtil.newInstance().removeKey(ShreadUtil.PRM_ID_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            notifyUpdateProgram();
            removeWindowsView();
        }
    }

    /**
     * 通知更新节目
     */
    private void notifyUpdateProgram() {
        if (null != cachePrmMap && !cachePrmMap.isEmpty()) {
            LogUtils.d(TAG, "notifyUpdateProgram", "notifyUpdateProgram.");
            Intent notifyIntent = new Intent(Constant.UI_NOTIFY_ACTION);
            sendBroadcast(notifyIntent);
        }
        reportDownloadState();
    }

    /**
     * 移除掉显示的view
     */
    private void removeWindowsView() {
//		if (windowsViewIfExistView) {
        if (null != windowManager) {
            windowsViewIfExistView = false;
            try {
//					windowManager.removeView(debugView);
//					if (View.VISIBLE == mDownloadStateImg.getVisibility()) {
//						file_download_count.setVisibility(View.GONE);
//						mDownloadStateImg.setVisibility(View.GONE);?
//					}
                mHandler.obtainMessage(DOWNLOAD_OVER).sendToTarget();
                LogUtils.d(TAG, "removeWindowsView", "removeWindowsView");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//		}
    }

    @Override
    public void updateProgreass(int progress, int totalSize, String httpUrl, int downloadType) {
        downloadProgress += progress;
        String fileName = FileUtil.getFileName(httpUrl);
        //对下载节目的文件汇报下载进度
        if (downloadType == Constant.DOWNLOAD_TYPE_PAROGRAM) {
//			uploadDownloadProgress(fileName,downloadProgress,totalSize);
        }
        //更新进度
        updateDownloadInfoProgress(fileName);
        //计算下载速度
        calculSpeed(fileName, progress);
        //更新调试界面
        updateView(fileName, "updateProgreass");
        FlowManager.getInstance().addDownloadFlow(progress);
    }

    /**
     * 计算下载速度
     *
     * @param fileName
     * @param progress
     */
    private void calculSpeed(String fileName, int progress) {
        tempProgress += progress;//临时保存进度
        if (tempTime == 0) {
            tempTime = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();//获取当的时间
            if ((currentTime - tempTime) > 1000) {//1秒钟更新一次进度
                int speed = tempProgress / 1024;//计算下载速度kb/s
                updateDownloadInfoSpeed(fileName, speed);
                tempProgress = 0;
                tempTime = 0;
            }
        }
    }

    /**
     * 更新下载进度
     *
     * @param fileName
     */
    private void updateDownloadInfoProgress(String fileName) {
        DownloadInfoManager.get().updateDownloadInfoProgress(fileName, downloadProgress);
    }

    /**
     * 更新界面UI
     *
     * @param fileName
     */
    private void updateView(final String fileName, String methodName) {
        fileName_tv.post(new Runnable() {
            @Override
            public void run() {
                DownloadInfo downloadInfo = DownloadInfoManager.get().getDownloadInfoToFileName(fileName);
                if (null == downloadInfo) {
                    return;
                }
                DecimalFormat decimalFormat = new DecimalFormat("0.00");
                String tempSpeed = null;
                int speed = downloadInfo.getSpeed();//计算下载速度kb/s
                if (1024 < speed) {//MB/s
                    tempSpeed = decimalFormat.format(speed / 1024f) + "MB/s";
                } else {
                    tempSpeed = speed + "kb/s";
                }
                speed_tv.setText(tempSpeed);
                progressBar.setMax(downloadInfo.getDownloadFileSize());
                progressBar.setProgress(downloadInfo.getDownloadProgress());
                fileName_tv.setText(downloadInfo.getDownloadUrl());
                per_tv.setText(downloadInfo.getPercentage() + "%");
                float fileSize = ((downloadInfo.getDownloadFileSize() / 1024f) / 1024f);
                float downloadSize = ((downloadInfo.getDownloadProgress() / 1024f) / 1024f);
                download_size_tv.setText(decimalFormat.format(downloadSize) + "M/" + decimalFormat.format(fileSize) + "M");
            }
        });
    }

    /***
     * 更新下载总数
     * @param index
     */
    public void updateDownloadSize(final int index) {
        file_download_count.post(new Runnable() {
            @Override
            public void run() {
                file_download_count.setText(index + "/" + downloadSize);
            }
        });
    }

    /**
     * 更新下载速度
     *
     * @param fileName 下载地址
     * @param speed    下载速度
     */
    private void updateDownloadInfoSpeed(String fileName, int speed) {
        DownloadInfoManager.get().updateDownloadInfoSpeed(fileName, speed);
    }

    private void updateSpeed() {
        speed_tv.post(new Runnable() {
            @Override
            public void run() {
                speed_tv.setText("0kb/s");
            }
        });
    }

    /**
     * 开始下载
     */
    @Override
    public void onStart(int progress, int totalSize, String httpUrl, int downloadType) {
//		uploadDownloadState(Constant.START_DOWNLOAD, progress, totalSize, httpUrl);
        downloadProgress = 0;
        String fileName = FileUtil.getFileName(httpUrl);
        //更新进度
        updateDownloadInfoProgress(fileName);
        if (Constant.DOWNLOAD_FILE == downloadType) {
            DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOADING, httpUrl, null);
        }
    }

    /**
     * 返回执行命令的状态
     *
     * @param cmd       执行的命令
     * @param state     状态(1 or -1)
     * @param cxt       Context对象
     * @param extraData 附带的返回数据(文件名,包名,app路径)
     */
    private void responseExcuteCmdState(int cmd, int state, Context cxt, String extraData) {
        /**响应命令状态的action*/
        Intent stateIntent = new Intent();
        stateIntent.setAction(Constant.REPORT_STATE_ACTION);
        stateIntent.putExtra("cmd", cmd);
        stateIntent.putExtra("state", state);
        if (null != extraData && !"".equals(extraData)) {
            stateIntent.putExtra("extraData", extraData);
        }
        cxt.sendBroadcast(stateIntent);
    }

    /***更新节目的serviceConnection*/
    private ServiceConnection notifyConnection = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            LogUtils.e(TAG, "onServiceDisconnected", name.getPackageName() + "|" + name.getClassName());
            notifyLayoutAIDL = null;
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LogUtils.e(TAG, "onServiceConnected", name.getPackageName() + "|" + name.getClassName());
            notifyLayoutAIDL = NotifyLayoutAIDL.Stub.asInterface(service);
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return downloadAIDL;
    }

    /***添加浮动窗口的handler*/
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constant.NETWORK_CONNECT:
                    mNetworkStateImg.setVisibility(View.INVISIBLE);
                    break;
                case Constant.NETWORK_DISCONNECT:
                    mNetworkStateImg.setVisibility(View.VISIBLE);
                    break;
                case DOWNLOADING:
                    if (View.INVISIBLE == mDownloadStateImg.getVisibility()) {
                        ((AnimationDrawable) mDownloadStateImg.getBackground()).start();
                        mDownloadStateImg.setVisibility(View.VISIBLE);
                        file_download_count.setVisibility(View.VISIBLE);
                    }
                    break;
                case DOWNLOAD_OVER:
                    if (View.VISIBLE == mDownloadStateImg.getVisibility()) {
                        ((AnimationDrawable) mDownloadStateImg.getBackground()).stop();
                        mDownloadStateImg.setVisibility(View.INVISIBLE);
                        file_download_count.setVisibility(View.INVISIBLE);
                    }
                    break;

                default:
                    break;
            }
//			windowsViewIfExistView = true;
//			if (null != windowManager) {
//				try {
////					windowManager.addView(debugView,layoutParams);
//					if (View.GONE == mDownloadStateImg.getVisibility()) {
//						mDownloadStateImg.setVisibility(View.VISIBLE);
//						file_download_count.setVisibility(View.VISIBLE);
//					} 
//					LogUtils.d(TAG, "handleMessage", "handleMessage");
//				} catch (Exception e) {
//					e.printStackTrace();
//					try {
//						LogUtils.e(TAG, "mHandler error", "removeView");
////						windowManager.removeView(debugView);
//					} catch (Exception e1) {
//						e1.printStackTrace();
//					}
//				}
//			}
            super.handleMessage(msg);
        }
    };

    @Override
    public void onDestroy() {
        try {
            removeWindowsView();
            unbindService(notifyConnection);
            LogUtils.d(TAG, "onDestroy", "respose download servicce stop");
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    /***
     * 上传下载进度到服务器
     */
    private void uploadDownloadProgress(String fileName, long progress, long total) {
//		if(tempSaveType == Constant.DOWNLOAD_APP){
//			return;
//		}
//		if (startTime == 0) {
//			startTime = System.currentTimeMillis();
//		} else {
//			long temp = System.currentTimeMillis() - startTime;
//			LogUtils.d(TAG, "updateView", "startTime："+startTime+",temp:"+temp);
//			if (temp >= 1000) {
//				startTime = System.currentTimeMillis();
//				RedisUtil.uploadDownloadProgress(
//												ConfigSettings.getDeviceId(), 
//												fileName, 
//												progress,
//												total,
//												ConfigSettings.getUUID(fileName));
//			}
//		}
    }

    /**
     * 上传下载成功的信息
     *
     * @param total    下载文件的大小
     * @param fileName 文件名
     */
    private void uploadDownloadSuccess(int total, String fileName, String uuid) {
//		if(tempSaveType == Constant.DOWNLOAD_APP){
//			return;
//		}
//		RedisUtil.uploadDownloadProgress(
//										ConfigSettings.getDeviceId(), 
//										fileName, 
//										total,
//										total,uuid);
    }


    /**
     * 汇报开始下载状态
     */
    private void uploadDownloadState(int downloadState, int progress, int totalSize, String fileName) {
//		if(tempSaveType == Constant.DOWNLOAD_APP){
//			return;
//		}
//		ReportDownloadState.reportDownloadState(downloadState, progress, totalSize, fileName);
    }

    @Override
    public void onCancel(String httpUrl, int downloadType) {
//		downloadSize --;
//		if (downloadSize <= 0) {
//			downloadSize = 0;
//			downloadNum = 0 ;
//		} 
        downloadNum++;

//		if (downloadNum > 1) {
//			downloadNum -= 1;
//		}
        downloadFileTypeSize--;
        if (downloadFileTypeSize <= 0) {
            downloadFileTypeSize = 0;
            downloadFileTypeIndex = 0;
        }
        if (downloadFileTypeIndex > 1) {
            downloadFileTypeIndex -= 1;
        }
        if (downloadFileTypeHttpList.contains(httpUrl)) {
            downloadFileTypeHttpList.remove(httpUrl);
        }
        ArrayList<String> cancelArrayList = new ArrayList<String>();
        cancelArrayList.add(httpUrl);
        sendCancel(cancelArrayList);
        updateDownloadSize(downloadNum);
        isDownloadOver();

        LogUtils.d(TAG, "onCancel", " httpUrl : " + httpUrl + ", downloadType : " + downloadType);
        LogUtils.d(TAG, "onCancel", " downloadSize :" + downloadSize + ",downloadNum :" + downloadNum);
        LogUtils.d(TAG, "onCancel", " downloadFileTypeSize :" + downloadFileTypeSize + ",downloadFileTypeIndex :" + downloadFileTypeIndex);
    }

    private void sendCancel(ArrayList<String> cancelDownloadList) {
        DownloadStateReportTools.sendDownloadFileStateReceive(
                ResposeDownloadService.STATE_DOWNLOAD_CANCEL,
                null, cancelDownloadList);
    }
}
