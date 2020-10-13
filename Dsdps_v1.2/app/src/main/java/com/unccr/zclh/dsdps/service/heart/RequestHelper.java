package com.unccr.zclh.dsdps.service.heart;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.unccr.zclh.dsdps.ProhibitActivity;
import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.app.DsdpsApp;
import com.unccr.zclh.dsdps.download.app.UpgradeApp;
import com.unccr.zclh.dsdps.manager.DelProgramManager;
import com.unccr.zclh.dsdps.models.Program;
import com.unccr.zclh.dsdps.models.WorkTimesInfo;
import com.unccr.zclh.dsdps.play.PlayActivity;
import com.unccr.zclh.dsdps.push.control.DeviceCommandControl;
import com.unccr.zclh.dsdps.service.sign.SignUtil;
import com.unccr.zclh.dsdps.service.sync.RequestSyncUpdateProgram;
import com.unccr.zclh.dsdps.util.ConfigSettings;
import com.unccr.zclh.dsdps.util.DeviceUtil;
import com.unccr.zclh.dsdps.util.DiskSpaceUtil;
import com.unccr.zclh.dsdps.util.FileStore;
import com.unccr.zclh.dsdps.util.FileUtil;
import com.unccr.zclh.dsdps.util.FileUtile;
import com.unccr.zclh.dsdps.util.HttpUtil;
import com.unccr.zclh.dsdps.util.IpAddress;
import com.unccr.zclh.dsdps.util.LayoutUtil;
import com.unccr.zclh.dsdps.util.PackageUtils;
import com.unccr.zclh.dsdps.util.SharedUtil;
import com.unccr.zclh.dsdps.util.ThreadPoolManager;
import com.unccr.zclh.dsdps.util.Util;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;
import static com.unccr.zclh.dsdps.util.AppUtil.installAppForSilent;

public class RequestHelper {

    private static final String TAG = "RequestHelper";
    private Map<String, Boolean> clearMap = new HashMap<>();
    private DeviceCommandControl deviceCommandControl;

    private int upgradedVersionNumber;
    private NotificationManager notificationManager;
    private Notification notification;
    private int currentVersionCode;
    private int serverVersionCode;
    private String temp = "";

    public RequestHelper() {
        deviceCommandControl = new DeviceCommandControl();
    }

    // http协议只能客户端发起通信，而不能做到服务端主动通知
    // 这里采用的是客户端不断向服务端请求，获取新数据，效率低/浪费资源
    public void heartBeatApi() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数key
        String key = DeviceUtil.getRandomString(18);
        // 参数time时间戳
        String time = new Date().getTime() + "";
        // app版本号
        String app_version = PackageUtils.getVersionName(DsdpsApp.getDsdpsApp());
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);
        int state = SharedUtil.newInstance().getInt("state");
        Log.d(TAG, "state: " + state);
        HttpUtil.postSyncJSON("/v1/message/heartbest?mac=" + mac + "&sn=" + sn + "&key=" + key + "&time=" + time + "&sign=" +
                        sign + "&app_version=" + app_version + "&state=" + state,
                params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        clearMap.clear();
                        Log.d(TAG, "heartBeatApi onSuccess response: " + response.toString());
                        try {
                            if (response.getString("result").equals("success")) {
                                int deviceId = response.getJSONObject("data").getInt("device_id");
                                if (deviceId != -1) {
                                    ConfigSettings.saveDeviceId(deviceId);
                                } else {
                                    ConfigSettings.saveDeviceId(-1);
                                }
                                JSONArray commandInfos = response.getJSONObject("data").getJSONArray("commandInfo");
                                if (commandInfos.length() > 0) {
                                    for (int i = 0; i < commandInfos.length(); i++) {
                                        JSONObject cmd = commandInfos.getJSONObject(i);
                                        int command = cmd.getInt("command");
                                        Log.d(TAG, "command: " + command);
                                        switch (command) {
                                            case 1:
                                                deviceCommandControl.reboot(); // 重启
                                                break;
                                            case 5:
                                                deviceCommandControl.uploadScreenshot(); // 截屏
                                                break;
                                            case 6:
                                                deviceCommandControl.standby(); // 待机
                                                break;
                                            case 7:
                                                deviceCommandControl.wakeUp(); // 唤醒
                                                break;
                                            case 10:
                                                deviceCommandControl.shutdown(); // 关机
                                                break;
                                        }
                                    }
                                }
                                // 调节音量
                                int volume = response.getJSONObject("data").getJSONObject("systemInfo").getInt("volume");
                                deviceCommandControl.updateDeivceVolume(volume);
                                // 更新时间
                                String updateTime = response.getJSONObject("data").getJSONObject("systemInfo").getString("workTime");
                                WorkTimesInfo workTimesInfo = JSON.parseObject(updateTime, WorkTimesInfo.class);
                                deviceCommandControl.updateWorkTiming(workTimesInfo);
                                // 节目信息
                                JSONArray programInfos = response.getJSONObject("data").getJSONArray("programInfo");
                                List<Program> localProgramList = Util.getLocalProgramList(false); // 获取本地所有的节目，包括(.prm和.temp)
                                for (Program program : localProgramList) {
                                    clearMap.put(program.getKey(), true);
                                }
                                File programFolde = new File(FileStore.getInstance().getLayoutFolderPath());//本地所有节目
                                File[] programFileList = programFolde.listFiles();
                                for (int i = 0; i < programInfos.length(); i++) {
                                    int programInfo = (int) programInfos.get(i);
                                    ConfigSettings.saveProgramId(programInfo);
                                    String prmKey = SignUtil.getMD5(String.valueOf(programInfo)); // 服务器节目清单的key
                                    if (null != clearMap.get(prmKey) && clearMap.get(prmKey)) {
                                        clearMap.put(prmKey, false);
                                    }
                                    boolean isNext = true;
                                    for (Program program : localProgramList) {
                                        String localProgramKey = program.getKey(); // 本地的节目
//                                        Log.d(TAG, "heartBeatApi onSuccess localProgramKey: " + localProgramKey);
                                        if (localProgramKey.equals(prmKey)) {
                                            for (File programFile : programFileList) {
                                                //如果服务器和本地节目key相同，并且本地的不是.temp格式，播放当前节目就行
                                                if (!programFile.getName().endsWith(FileUtile.TEMP_SUFFIX_NAME)) {
                                                    isNext = false;
                                                } else {
                                                    isNext = true;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                    if (isNext) {
                                        Log.d(TAG, "programInfo: " + programInfo + " ,isNext: " + isNext);
                                        // 请求同步更新节目线程
                                        RequestSyncUpdateProgram updateProgramRunnable = new RequestSyncUpdateProgram(programInfo);
                                        // 把线程扔进线程池运行
                                        ThreadPoolManager.get().addRunnable(updateProgramRunnable);
                                    }
                                }
                                //删除本地其它节目，mapValue为true的
                                for (Map.Entry<String, Boolean> entry : clearMap.entrySet()) {
                                    String mapKey = entry.getKey();
                                    Boolean mapValue = entry.getValue();
//                                    Log.d(TAG, "heartBeatApi onSuccess mapKey: " + mapKey + " ,mapValue: " + mapValue);
                                    if (mapValue && !ConfigSettings.UDISK_MODE) {
                                        List<String> clearPrmKeys = new ArrayList<>();
                                        clearPrmKeys.add(mapKey);
                                        DelProgramManager.getInstance().delProgramByPrmId(clearPrmKeys);
                                    }
                                }
                                //设置终端状态
                                int useState = response.getJSONObject("data").getInt("use_state");
                                SharedUtil.newInstance().setInt("useState", useState);
                                boolean isProhibit = SharedUtil.newInstance().getBoolean("isProhibit");
                                Log.d(TAG,"useState: " + useState+ " ,isProhibit: " + isProhibit);
                                if (useState == 0) {
                                    // 禁用
                                    if (isProhibit) {
                                        return;
                                    }
                                    Log.d(TAG, "终端禁用......");
                                    Intent intent = new Intent(DsdpsApp.getDsdpsApp(), ProhibitActivity.class);
                                    intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                    DsdpsApp.getDsdpsApp().startActivity(intent);
                                } else if (useState == 1) {
                                    // 启用
                                    if (isProhibit) {
                                        SharedUtil.newInstance().setBoolean("isProhibit", false);
                                        Log.d(TAG, "终端启用......");
                                        Intent intent = new Intent(DsdpsApp.getDsdpsApp(), PlayActivity.class);
                                        intent.setFlags(FLAG_ACTIVITY_NEW_TASK);
                                        DsdpsApp.getDsdpsApp().startActivity(intent);
                                    }
                                }
                                //退出密码
                                String exitPassword = response.getJSONObject("data").getString("exit_password");
                                Log.d(TAG,"exitPassword: " + exitPassword);
                                if (exitPassword.equals("null")) {
                                    SharedUtil.newInstance().setString("exitPassword", "exitPassword");
                                } else {
                                    SharedUtil.newInstance().setString("exitPassword", exitPassword);
                                }
                                String updateInfo = response.getJSONObject("data").getString("updateInfo");
                                if (updateInfo.equals("null")) {
                                    return;
                                }
                                //应用升级
                                JSONObject updateInfos = response.getJSONObject("data").getJSONObject("updateInfo");
                                if (updateInfo != null) {
                                    String targetVersion = updateInfos.getString("target_version");
                                    String updateVersion = updateInfos.getString("update_version");
                                    String url = updateInfos.getString("url");
                                    String md5 = updateInfos.getString("md5");
                                    Log.e(TAG, "heartBeatApi onSuccess targetVersion: " + targetVersion + "\r\n" + "updateVersion: " + updateVersion + "\r\n"
                                            + "url: " + url + "\r\n" + "md5: " + md5);
                                    downloadNewApp(url);
                                }
                            } else if (response.getString("result").equals("error")) {
                                if (response.getString("message").equals("设备未绑定")) {
                                    List<Program> localProgramList = Util.getLocalProgramList(false);
                                    if (localProgramList.size() > 0) {
                                        for (Program program : localProgramList) {
                                            List<String> clearPrmKeys = new ArrayList<>();
                                            clearPrmKeys.add(program.getKey());
                                            DelProgramManager.getInstance().delProgramByPrmId(clearPrmKeys);
                                        }
                                        SharedUtil.newInstance().clearAll();
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, "heartBeatApi onFailure throwable: " + throwable.getMessage());
                    }
                });
    }

    public void deviceBeatApi() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数key
        String key = DeviceUtil.getRandomString(18);
        // 参数time时间戳
        String time = new Date().getTime() + "";
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);

        String deviceId = String.valueOf(ConfigSettings.getDeviceId());
        String deviceIp = DeviceUtil.getIpAddress(DsdpsApp.getDsdpsApp());
        int width = LayoutUtil.getInstance().getRealDisplayWidth();
        int height = LayoutUtil.getInstance().getRealDisplayHeight();
        String screen = width + "x" + height;
        long memory = DeviceUtil.getTotalMemory(DsdpsApp.getDsdpsApp());
        Long diskSpaceTotal = DiskSpaceUtil.getTotalSpaceKB();
        Long diskSpaceUse = DiskSpaceUtil.getAvailableSpaceKB();
        String firmwareVersion = PackageUtils.getDeviceDisplayId();
        String appVersion = String.valueOf(PackageUtils.getVersionName(DsdpsApp.getDsdpsApp()));

        params.put("device_id", deviceId);
        params.put("device_ip", deviceIp);
        params.put("screen", screen);
        params.put("memory", memory);
        params.put("disk_space_total", diskSpaceTotal);
        params.put("disk_space_use", diskSpaceUse);
        params.put("firmware_version", firmwareVersion);
        params.put("app_version", appVersion);
        params.setContentEncoding("utf-8");
        params.setUseJsonStreamer(true);

        HttpUtil.postSyncJSON("/v1/message/report/device/info?mac=" + mac + "&sn=" + sn + "&key=" + key + "&time=" + time + "&sign=" + sign,
                params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG, "deviceBeatApi onSuccess response: " + response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, "deviceBeatApi onFailure throwable: " + throwable.getMessage());
                    }
                });
    }

    public void downloadCompletionApi() {
        RequestParams params = new RequestParams();
        // 参数mac地址
        String mac = Util.getEthernetMacAddress();
        // 参数sn号
        String sn = android.os.Build.SERIAL;
        // 参数key
        String key = DeviceUtil.getRandomString(18);
        // 参数time时间戳
        String time = new Date().getTime() + "";
        Map<String, String> map = new HashMap<>();
        map.put("mac", mac);
        map.put("sn", sn);
        map.put("time", time);
        // 参数sign签名
        String sign = SignUtil.checkSign(map);

        int deviceId = ConfigSettings.getDeviceId();
        int programId = ConfigSettings.getProgramId();

        params.put("device_id", deviceId);
        params.put("program_id", programId);
        params.setContentEncoding("utf-8");
        params.setUseJsonStreamer(true);

        Log.d(TAG, "downloadCompletionApi device_id: " + deviceId + " ,program_id: " + programId);

        HttpUtil.postSyncJSON("/v1/message/report/device/program/state?mac=" + mac + "&sn=" + sn + "&key=" + key + "&time=" + time + "&sign=" + sign,
                params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        Log.d(TAG, "downloadCompletionApi onSuccess response: " + response.toString());
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        Log.d(TAG, "downloadCompletionApi onFailure throwable: " + throwable.getMessage());
                    }
                });
    }

    /**
     * 下载apk
     *
     * @param apkUrl
     */
    private void downloadNewApp(String apkUrl) {
        notificationManager = (NotificationManager) DsdpsApp.getDsdpsApp().getSystemService(Context.NOTIFICATION_SERVICE);
        notification = new Notification();
        notification.icon = R.mipmap.ic_launcher;
        notification.contentView = new RemoteViews(DsdpsApp.getDsdpsApp().getPackageName(), R.layout.view_nofity_item);
        notificationManager.notify(100, notification);
        String appName = FileUtil.getFileName(apkUrl);
        Log.d(TAG, "downloadNewApp appName: " + appName);
        File appFile = new File(FileUtil.getInstance().getAPPFolder() + File.separator + appName);
//        if (!appFile.exists()) {
//            try {
//                // 在指定的文件夹中创建文件
//                appFile.createNewFile();
//                downloadSchedule(apkUrl, completeHandler, appFile, appName);
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        } else {
//            downloadSchedule(apkUrl, completeHandler, appFile, appName);
//        }
        if (!appFile.exists()) {
            try {
                // 在指定的文件夹中创建文件
                appFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        downloadSchedule(apkUrl, completeHandler, appFile, appName);
    }

    /**
     * 正式下载app
     *
     * @param downloadUrl     app下载地址
     * @param completeHandler 下载完成的Handler
     * @param appFile         app下载到机器里面的路径
     */
    private void downloadSchedule(String downloadUrl, Handler completeHandler, File appFile, String appName) {
        if (!appFile.exists()) {
            completeHandler.sendEmptyMessage(-1);
            return;
        }
        if (!temp.equals(appName)) {
            temp = appName;
            UpgradeApp upgradeApp = new UpgradeApp(downloadUrl, completeHandler, appFile, appName);
            ThreadPoolManager.get().addRunnable(upgradeApp);
        }
    }

    @SuppressLint("HandlerLeak")
    private Handler completeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // 更新通知栏
            Bundle bundle = msg.getData();
            int schedule = bundle.getInt("schedule");
            String appName = bundle.getString("appName");
            Log.d(TAG,"schedule: " + schedule);
            if (schedule < 100) {
                Log.e(TAG, "completeHandler percent: " + schedule + "%");
                notification.contentView.setTextViewText(R.id.notify_updata_values_tv, schedule + "%");
                notification.contentView.setProgressBar(R.id.notify_updata_progress, 100, schedule, false);
            } else {
                Log.d(TAG,"completeHandler 下载完成");
                notification.contentView.setTextViewText(R.id.notify_updata_values_tv, "下载完成");
                notification.contentView.setProgressBar(R.id.notify_updata_progress, 100, msg.what, false);
                notificationManager.cancel(100);
                // 静默安装
                installAppForSilent(appName);
                //installApkForSilent(appName);
//                DsdpsApp.getDsdpsApp().sendBroadcast(new Intent("android.intent.action.PACKAGE_REPLACED"));
            }
        }
    };

    /**
     * 清除节目
     */
    public void clearProgramApi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                // 参数mac地址
                String mac = Util.getEthernetMacAddress();
                // 参数sn号
                String sn = android.os.Build.SERIAL;
                // 参数key
                String key = DeviceUtil.getRandomString(18);
                // 参数time时间戳
                String time = new Date().getTime() + "";
                // app版本号
                String app_version = PackageUtils.getVersionName(DsdpsApp.getDsdpsApp());
                Map<String, String> map = new HashMap<>();
                map.put("mac", mac);
                map.put("sn", sn);
                map.put("time", time);
                // 参数sign签名
                String sign = SignUtil.checkSign(map);
                Log.e(TAG, "clearProgramApi mac: " + mac + " ,sn: " + sn + " ,key: " + key + " ,time: " + time + " ,sign: " + sign + " ,app_version: " + app_version);
                HttpUtil.postSyncJSON("/v1/message/clearProgram?mac=" + mac + "&sn=" + sn + "&key=" + key + "&time=" + time + "&sign=" + sign + "&app_version=" + app_version,
                        params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.d(TAG, "clearProgramApi onSuccess statusCode: " + statusCode);
                                Looper.prepare();
                                showMsg("已成功清空节目，请返回主界面。");
                                Looper.loop();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.d(TAG, "clearProgramApi onFailure throwable: " + throwable.getMessage());
                                Looper.prepare();
                                showMsg("节目清空失败，请确认网络是否正常。");
                                Looper.loop();
                            }
                        });
            }
        }).start();
    }

    /**
     * 服务器解绑
     */
    public void unbindServerApi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                RequestParams params = new RequestParams();
                // 参数mac地址
                String mac = Util.getEthernetMacAddress();
                // 参数sn号
                String sn = android.os.Build.SERIAL;
                // 参数key
                String key = DeviceUtil.getRandomString(18);
                // 参数time时间戳
                String time = new Date().getTime() + "";
                // app版本号
                String app_version = PackageUtils.getVersionName(DsdpsApp.getDsdpsApp());
                Map<String, String> map = new HashMap<>();
                map.put("mac", mac);
                map.put("sn", sn);
                map.put("time", time);
                // 参数sign签名
                String sign = SignUtil.checkSign(map);
                Log.e(TAG, "unbindServerApi mac: " + mac + " ,sn: " + sn + " ,key: " + key + " ,time: " + time + " ,sign: "
                        + sign + " ,app_version: " + app_version);
                HttpUtil.postSyncJSON("/v1/message/unbind?mac=" + mac + "&sn=" + sn + "&key=" + key + "&time="
                                + time + "&sign=" + sign + "&app_version=" + app_version,
                        params, new JsonHttpResponseHandler() {
                            @Override
                            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                                Log.d(TAG, "unbindServerApi onSuccess statusCode: " + statusCode);
                                Looper.prepare();
                                showMsg("已成功解绑服务器，请返回主界面");
                                Looper.loop();
                            }

                            @Override
                            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                                Log.d(TAG, "unbindServerApi onFailure throwable: " + throwable.getMessage());
                                Looper.prepare();
                                showMsg("服务器解绑失败，请确认网络是否正常");
                                Looper.loop();
                            }
                        });
            }
        }).start();
    }

    /**
     * 网络检测
     */
    public void checkNetApi() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String str = "http://" + IpAddress.getIpAddress() + ":18889/v1/systemInfo/network";
//                    URL url = new URL("http://119.23.249.140:18889/v1/systemInfo/network");
                    URL url = new URL(str);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    int responseCode = urlConnection.getResponseCode();
                    Log.d(TAG, "checkNetApi responseCode: " + responseCode);
                    if (responseCode == 200) {
                        Looper.prepare();
                        showMsg("广告机和服务器之间是通的");
                        Looper.loop();
                    } else {
                        Looper.prepare();
                        showMsg("连接失败，网络未到达");
                        Looper.loop();
                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    showMsg("连接失败，网络未到达");
                    Looper.loop();
                } catch (IOException e) {
                    e.printStackTrace();
                    Looper.prepare();
                    showMsg("连接失败，网络未到达");
                    Looper.loop();
                }

            }
        }).start();
    }

    private void showMsg(String msg) {
        Toast.makeText(DsdpsApp.getDsdpsApp(), msg, Toast.LENGTH_SHORT).show();
    }
}
