package com.unccr.zclh.dsdps.download;


import android.annotation.SuppressLint;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.graphics.drawable.AnimationDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.db.SQLiteManager;
import com.unccr.zclh.dsdps.download.bean.DownloadBean;
import com.unccr.zclh.dsdps.download.bean.DownloadBo;
import com.unccr.zclh.dsdps.download.bean.DownloadInfo;
import com.unccr.zclh.dsdps.download.bean.RecoveryDownloadBean;
import com.unccr.zclh.dsdps.download.interfaces.IDownloadStateCallback;
import com.unccr.zclh.dsdps.download.interfaces.QiNiuDownloadTokenCallback;
import com.unccr.zclh.dsdps.service.heart.RequestHelper;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.Constants;
import com.unccr.zclh.dsdps.util.DownloadStateReportTools;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.FileUtils;
import com.unccr.zclh.dsdps.util.Helper;
import com.unccr.zclh.dsdps.util.PrmFileHashUtil;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.ThreadPoolManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 接受下通讯apk发过来的下载任务列表
 *
 * @author changjigang
 */
public class ResposeDownloadService extends Service implements IDownloadStateCallback, QiNiuDownloadTokenCallback {

    private static final String TAG = "ResponseDownload";

    /**
     * 下载管理类
     */
//	private DownloadManager downloadManager;

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

    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;
    private RelativeLayout debugView;
    private ProgressBar progressBar;

    private static RequestHelper requestHelper = new RequestHelper();

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
    private String downloadUrl;

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
                    clearRepeat(downloadList);
                    if (!downloadList.isEmpty()) {
                        allDownloadTaskList.addAll(downloadList);
                        downloadSize += downloadList.size();
                        Log.d(TAG, "onDownloadList download task..downloadSize: " + downloadSize + " ,prmId: " + prmId + " ,prmMap.size: " + prmMap.size());
                        cachePrmMap.put(prmId, downloadList);
                        ConfigSettings.savePrmKey(cachePrmMap);
                        if (prmMap.isEmpty()) {//判断map里面是否为空，如果为空则进行添加下载素材
                            cachePrmId = prmId;//保存当前正在下载的节目id
                            ConfigSettings.saveCurrentPrmKey(cachePrmId);
                            download(downloadList, type, true, null);
                        } else {
                            prmMap.put(prmId, downloadList);//保存节目列表id和素材下载列表
                            waitDownloadPrm.add(prmId);//保存所有要下载的节目列表id
                        }
                    } else {
                        Log.d(TAG, "onDownloadList downloadList is null,prmId: " + prmId);
                    }
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

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, "onCreate.............");
        registerReceivers();
        init();
        permission();
    }

    private void permission() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return;
            } else {
                initWindow();
            }
        } else {
            initWindow();
        }
    }

    private void registerReceivers() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.DOWNLOAD_BROADCAST);
        registerReceiver(downloadReceiver, filter);
    }

    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String key = intent.getStringExtra("key");
            List<String> downloadList = intent.getStringArrayListExtra("materialList");
            Log.d(TAG, "key: " + key + " ,downloadList size: " + downloadList.size());
            addDownloadPrmFile(downloadList, 2, key);
        }
    };

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
        layoutParams.gravity = Gravity.TOP;
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
                int typeKey = intent.getIntExtra(Constants.DOWNLOAD_TYPE_KEY, -1);
                Log.d(TAG, "onStartCommand typeKey: " + typeKey);
                switch (typeKey) {
                    case Constants.RECOVERY_DOWNLOAD:
                        recoverDownload(intent);
                        break;
                    case Constants.APPEND_DOWNLOAD:
                        appendDownloadToIntent(intent);
                        break;
                    case Constants.CANCEL_DOWNLOAD:
//				cancelDownloadToIntent(intent);
                        break;
                    case Constants.CANCEL_DOWNLOAD_FILE:
//				cancelDownloadFile(intent);
                        break;
                    case Constants.NETWORK_DISCONNECT:
                        mHandler.obtainMessage(Constants.NETWORK_DISCONNECT).sendToTarget();
                        break;
                    case Constants.NETWORK_CONNECT:
                        mHandler.obtainMessage(Constants.NETWORK_CONNECT).sendToTarget();
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
                    if (Constants.DOWNLOAD_FILE == type) {
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
                                download(downloadList, Constants.DOWNLOAD_FILE, true, downloadBo.getSaveLocalFoldePath());
                            }
                        } else {
                            addDownloadPrmFile(downloadList, downloadBo.getType(), downloadBo.getPrmId());
                        }
                    }
                }
            }
        }
    }

    /**
     * 去除重复的下载文件
     *
     * @param downloadList
     */
    private ArrayList<String> removeRepeatDownloadUrl(ArrayList<String> downloadList) {
        Log.d(TAG, "removeRepeatDownloadUrl start downloadList.size :" + downloadList.size());
        Log.d(TAG, "removeRepeatDownloadUrl start downloadList:" + downloadList);
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
            Log.d(TAG, "removeRepeatDownloadUrl add download Urls: " + notParamList);
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
            Log.d(TAG, "removeRepeatDownloadUrl to download Urls: " + newDownloadUrls);
            Log.d(TAG, "removeRepeatDownloadUrl not Download List: " + notDownloadListNotParam);
            Log.d(TAG, "removeRepeatDownloadUrl download Over List: " + downloadOverListNotParam);
            downloadList = newDownloadUrls;


            notParamKeyMap.clear();
            notParamKeyMap = null;
            notDownloadListNotParam = null;
            downloadOverListNotParam = null;
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.d(TAG, "removeRepeatDownloadUrl end downloadList.size: " + downloadList.size());
        return downloadList;
    }

    /**
     * 是否下载完
     */
    private void isDownloadOver() {
        downloadFileIsOver();
        if (downloadNum == downloadSize || downloadNum > downloadSize) {//隐藏进度条,并且更新节目列表
            downloadNum = 0;
            downloadSize = 0;
            renamePrmName(null);
        }
    }

    /**
     * 是否显示debug窗口
     */
    private void ifShowProg() {
        mHandler.obtainMessage(DOWNLOADING).sendToTarget();
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
        if (ifDownloading && !downloadBeanList.isEmpty()) {
            Log.d(TAG, "download addToDownloadTaskList.");
            addToDownloadTaskList(type, ifAddTask, downloadList, locleFolder);
            return;
        }
        ifDownloading = true;
        ifShowProg();
        ifAddTask(downloadList, type, ifAddTask, locleFolder);
        if (Constants.IS_QINIU) {
            Log.d(TAG, "download addQiNiuTask.");
            addQiNiuTask(downloadList, type, locleFolder);
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
     * 添加七牛云储存下载任务
     *
     * @param downloadList
     * @param type
     */
    private void addQiNiuTask(List<String> downloadList, int type, String locleFolder) {
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
        httpDownloadFile.addDownloadListTask(downloadList, this, locleFolder, type);
        httpDownloadFile.execu();
        updateDownloadSize(downloadNum);
        DownloadManager.newInstance().addHttpDownload(httpDownloadFile);
    }

    /**
     * 下载成功以后调用的方法
     */
    @Override
    public void onSuccess(String httpUrl, int totalSize, int downloadType) {
        Log.d(TAG, "onSuccess httpUrl: " + httpUrl + " ,totalSize: " + totalSize + " ,downloadType: " + downloadType);
        downloadProgress = 0;
        delDownloadTask(httpUrl);
        if (Constants.DOWNLOAD_FILE == downloadType) {//下载单个文件
            downloadFileTypeIndex++;
            downloadFileTypeHttpList.add(httpUrl);
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
                Log.e(TAG, "onSuccess currentPrmNum <= currentPrmDownloadNum currentPrmNum: " + currentPrmNum + " ,cachePrmId: " + cachePrmId);
            }
        }
        updateDownloadSize(downloadNum);
        if (downloadSize == downloadNum) {//表示一个节目素材下载完成
            Log.e(TAG, "onSuccess httpUrl: " + httpUrl + " ,downloadNum: " + downloadNum);
            downloadNum = 0;
            if (null != cachePrmId) {
                doneDownloadPrm.add(cachePrmId);//添加素材下载完以后的节目id
            }
            prmFileDoneHandler(httpUrl, true);//判断保存的节目列表里面是否有未下载完成的节目素材列表
        }
    }

    @Override
    public void onFail(String httpUrl, String errMsg, int downloadType) {
        Log.e(TAG, "onFail errMsg: " + errMsg + " ,httpUrl: " + FileUtil.getFileName(httpUrl));
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

        if (Constants.DOWNLOAD_FILE == downloadType) {
            downloadFileTypeIndex--;
            if (downloadFileTypeIndex < 0) {
                downloadFileTypeIndex = 0;
            }
            downloadFileIsOver();
        }

    }

    /**
     * 下载类型为Constant.DOWNLOAD_FILE 是否全部下载完成
     */
    private void downloadFileIsOver() {
        Log.d(TAG, "downloadFileIsOver downloadFileTypeSize: " + downloadFileTypeSize + " ,downloadFileTypeIndex: " + downloadFileTypeIndex);
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
        Log.d(TAG, "prmFileDoneHandler httpUrl: " + httpUrl + " ,isDownload: " + (isDownload ? "success" : "failed"));
        if (!prmMap.isEmpty()) {
            prmMap.remove(cachePrmId);//删除掉已经下载完成的节目列表
            //获取未下载完成的节目id
            String prmId = waitDownloadPrm.remove(0);
            cachePrmId = prmId;
            ConfigSettings.saveCurrentPrmKey(cachePrmId);
            Log.d(TAG, "prmFileDoneHandler httpUrl: " + httpUrl + " ,prmMap is new prmId: " + prmId + " ,old prmId: " + cachePrmId);
            List<String> downloadFileList = prmMap.get(prmId);//获取需要下载的节目素材列表
            download(downloadFileList, Constants.DOWNLOAD_PRMFILE, true, null);//添加到下载列表中进行下载
        } else {
            Log.d(TAG, "prmFileDoneHandler httpUrl: " + httpUrl + " ,prmMap is empty download over, done size is: " + doneDownloadPrm.size()
                    + " ,download failed size is: " + failedDownloadPrm.size());
            requestHelper.downloadCompletionApi();
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
        renamePrmName(httpUrl);
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
    private void renamePrmName(String httpUrl) {
        String suffix = FileUtil.getSuffix(FileUtil.getFileName(httpUrl));
        if (null != cachePrmMap && !cachePrmMap.isEmpty()) {
            for (String prmId : cachePrmMap.keySet()) {
                if (!doneDownloadPrm.contains(prmId)) {
                    doneDownloadPrm.add(prmId);
                }
                FileUtil.getInstance().renamePl(prmId);
            }
        }
        if (SharedUtil.newInstance().getString(SharedUtil.PROGRAM_SOURCE_TYPE).equals("programme")) {
            if (suffix != null) {
                String programmeSuffix = suffix.trim().toLowerCase();
                if (!FileUtil.getInstance().suffixToProgrammeFolder.containsKey(programmeSuffix)) {
                    FileUtil.getInstance().renameVideoName(httpUrl);
                }
            }
        }
        try {
            SharedUtil.newInstance().removeKey(SharedUtil.CURRENT_PRM_KEY);
            SharedUtil.newInstance().removeKey(SharedUtil.PRM_ID_KEY);
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
            Log.d(TAG, "notifyUpdateProgram. cachePrmMap is not null.");
            Intent notifyIntent = new Intent(Constants.UI_NOTIFY_ACTION);
            sendBroadcast(notifyIntent);
        }
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
        if (downloadType == Constants.DOWNLOAD_TYPE_PAROGRAM) {
//			uploadDownloadProgress(fileName,downloadProgress,totalSize);
        }
        //更新进度
        updateDownloadInfoProgress(fileName);
        //计算下载速度
        calculSpeed(fileName, progress);
        //更新调试界面
        updateView(fileName, "updateProgreass");
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
        if (Constants.DOWNLOAD_FILE == downloadType) {
            DownloadStateReportTools.sendDownloadFileStateReceive(STATE_DOWNLOADING, httpUrl, null);
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return onBind(intent);
    }

    /***添加浮动窗口的handler*/
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.NETWORK_CONNECT:
                    mNetworkStateImg.setVisibility(View.INVISIBLE);
                    break;
                case Constants.NETWORK_DISCONNECT:
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
        Log.d(TAG, "onDestroy.");
        try {
            removeWindowsView();
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
    }

    private void sendCancel(ArrayList<String> cancelDownloadList) {
        DownloadStateReportTools.sendDownloadFileStateReceive(
                ResposeDownloadService.STATE_DOWNLOAD_CANCEL,
                null, cancelDownloadList);
    }
}