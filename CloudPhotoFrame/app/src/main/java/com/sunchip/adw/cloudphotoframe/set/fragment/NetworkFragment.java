package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.location.LocationManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.adapter.WifiListAdapter;
import com.sunchip.adw.cloudphotoframe.listen.SoftKeyBoardListener;
import com.sunchip.adw.cloudphotoframe.manager.WifiManger;
import com.sunchip.adw.cloudphotoframe.util.AlertDialogUtils;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;

import java.util.ArrayList;
import java.util.List;

import cn.finalteam.toolsfinal.StringUtils;

public class NetworkFragment extends BaseFragment {

    private static final String TAG = "NetworkFragment";

    private View view = null;

    private ListView show_wifi_lv;
    private WifiListAdapter adapter;
    private List<ScanResult> mWifiList = new ArrayList<>();
    private WifiBroadcastReceiver wifiReceiver;
    // WifiManager
    WifiManager mWifiManager;
    //当前连接的wifi
    String ConnectSSID = "NULL";
    public AlertDialog dialog;
    private int Selece = 0;
    boolean IsSoftKeyBoard = false;//判断软键盘是否存在
    private final int setScrendList = 1002;

    private ProgressBar progressBar;


    public NetworkFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.network_fragment_layout, container, false);
            show_wifi_lv = view.findViewById(R.id.show_wifi_lv);
            progressBar = view.findViewById(R.id.ProMaster);
            scanWifiInfo();
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        wifiReceiver = new WifiBroadcastReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        getActivity().registerReceiver(wifiReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(wifiReceiver);
    }

    /**
     * 扫描附近wifi
     */
    private void scanWifiInfo() {
        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        adapter = new WifiListAdapter(getActivity(), mWifiList);
        show_wifi_lv.setAdapter(adapter);

        String wserviceName = Context.WIFI_SERVICE;
        mWifiManager = (WifiManager) getActivity().getSystemService(wserviceName);

        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || !mWifiManager.isWifiEnabled()) {
            //打开wifi
            mWifiManager.setWifiEnabled(true);
//            Log.d(TAG, "wifi是否连接: " + false);
        }
        mWifiManager.startScan();
        mWifiList.clear();
    }

    //我老觉得获取wiif列表的时候卡顿 这里使用子线程获取到wifi列表
    public void setAdapterList() {
        final List<ScanResult> ScanResults = mWifiManager.getScanResults();
//        Log.d(TAG, "wifi列表个数: " + mWifiManager.getScanResults().size());
        mWifiList.clear();
        new Thread() {
            @Override
            public void run() {
                super.run();
                //过滤掉空的SSID的wifi
                for (int i = 0; i < ScanResults.size() - 1; i++) {
                    Log.d(TAG, "mWifiList: " + ScanResults.get(i).SSID);
                    if (!StringUtils.isBlank(ScanResults.get(i).SSID)) {
                        mWifiList.add(ScanResults.get(i));
                    }
                }
                handler.sendEmptyMessage(setScrendList);
            }
        }.start();
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            if (mWifiList != null && mWifiList.size() > 0) {
                progressBar.setVisibility(View.GONE);
                show_wifi_lv.setVisibility(View.VISIBLE);
                adapter.setwifiList(mWifiList);
                adapter.setSSID(ConnectSSID, NetworkInfo.State.CONNECTED + "");
            }
        }
    };

    private void ConnectWifi(int position) {
        final ScanResult scanResult = mWifiList.get(position);

        if (ConnectSSID.equals("NULL") || !(ConnectSSID.trim().contains(scanResult.SSID.trim()))) {
            final EditText editText = (EditText) LayoutInflater.from(getActivity()).inflate(R.layout.editext,null);
//            singleLine = true
//            editText.setSingleLine(true);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

            builder.setTitle(scanResult.SSID)
                    .setMessage(R.string.password)
                    .setView(editText)
                    .setPositiveButton(R.string.connect, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String passwordTxt = editText.getText().toString().trim();
                            connectWifi(scanResult.SSID, passwordTxt,
                                    WifiManger.getInstance().getCipherType(scanResult.capabilities));
                        }
                    }).setNegativeButton(R.string.cancel, null)
                    .setOnKeyListener(new DialogInterface.OnKeyListener() {
                        @Override
                        public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {

                            IsSoftKeyBoard = SoftKeyBoardListener.isSoftShowing(getActivity(),true);
//                            Log.e("TAG", "获取到的键值是:" + i + " KeyEvent: " + keyEvent.getKeyCode()
//                                    + "   键盘是否显示===:" + IsSoftKeyBoard);
//
                            if (i == KeyEvent.KEYCODE_DPAD_UP) {
                                editText.setFocusable(true);
                                editText.requestFocus();
                            }

                            if (i == KeyEvent.KEYCODE_DPAD_DOWN) {
                                if (!IsSoftKeyBoard)
                                    editText.setFocusable(false);
                            }
                            return false;
                        }
                    });
            dialog = builder.create();
            dialog.show();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(scanResult.SSID)
                    .setMessage(R.string.password)
                    .setPositiveButton(R.string.disconnect, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mWifiManager.disconnect();
                        }
                    }).setNegativeButton(R.string.cancel, null);
            dialog = builder.create();
            dialog.show();
            StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
        }


    }

    /**
     * 连接wifi
     *
     * @param targetSsid wifi的SSID
     * @param targetPsd  密码
     * @param enc        加密类型
     */
    @SuppressLint("WifiManagerLeak")
    public void connectWifi(String targetSsid, String targetPsd, String enc) {

//        Log.e("TAG", "要连接的wifi是:" + targetSsid + " 密码:" + targetPsd + " 加密类型:" + enc);

        // 1、注意热点和密码均包含引号，此处需要需要转义引号
        String ssid = "\"" + targetSsid + "\"";
        String psd = "\"" + targetPsd + "\"";

        //2、配置wifi信息
        WifiConfiguration conf = new WifiConfiguration();
        conf.SSID = ssid;
        switch (enc) {
            case "WEP":
                // 加密类型为WEP
                conf.wepKeys[0] = psd;
                conf.wepTxKeyIndex = 0;
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                conf.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                break;
            case "WPA":
                // 加密类型为WPA
                conf.preSharedKey = psd;
                break;
            case "OPEN":
                //开放网络
                conf.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
        }
        //3、链接wifi
        WifiManager wifiManager = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);
        wifiManager.addNetwork(conf);
        List<WifiConfiguration> list = wifiManager.getConfiguredNetworks();
        for (WifiConfiguration i : list) {
            if (i.SSID != null && i.SSID.equals(ssid)) {
                wifiManager.disconnect();
                wifiManager.enableNetwork(i.networkId, true);
                wifiManager.reconnect();
                break;
            }
        }
    }

    @Override
    public boolean onKeyDown(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                Selece--;
                if (Selece <= 0) {
                    Selece = 0;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Selece++;
                if (Selece >= mWifiList.size() - 1) {
                    Selece = mWifiList.size() - 1;
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                ConnectWifi(Selece);
//                AlertDialogUtils.getmAlertDialogUtils().WifiConnectDialog(getActivity());
                break;

        }
        return ret;
    }

    public void SmoothScroll() {
        show_wifi_lv.postDelayed(new Runnable() {
            @Override
            public void run() {
                show_wifi_lv.setSelection(Selece);
            }
        }, 100);
    }

    @Override
    public void Select(int postion) {
        int color = 0;
        if (postion == 0) {
            Selece = 0;
            color = getResources().getColor(R.color.MainBlue);
            SmoothScroll();
        } else {
            color = getResources().getColor(R.color.white);
        }
        show_wifi_lv.setSelector(new ColorDrawable(color));
    }

    //监听wifi状态广播接收器
    public class WifiBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(intent.getAction())) {

                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);
                switch (state) {
                    /**
                     * WIFI_STATE_DISABLED    WLAN已经关闭
                     * WIFI_STATE_DISABLING   WLAN正在关闭
                     * WIFI_STATE_ENABLED     WLAN已经打开
                     * WIFI_STATE_ENABLING    WLAN正在打开
                     * WIFI_STATE_UNKNOWN     未知
                     */
                    case WifiManager.WIFI_STATE_DISABLED: {
                        Log.i(TAG, "已经关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_DISABLING: {
                        Log.i(TAG, "正在关闭");
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLED: {
                        Log.i(TAG, "已经打开");
                        setAdapterList();
                        break;
                    }
                    case WifiManager.WIFI_STATE_ENABLING: {
                        Log.i(TAG, "正在打开");
                        break;
                    }
                    case WifiManager.WIFI_STATE_UNKNOWN: {
                        Log.i(TAG, "未知状态");
                        break;
                    }
                }
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
                NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Log.i(TAG, "--NetworkInfo--" + info.toString() + " wifi:" + info.getExtraInfo() + " adapter " + adapter
                        + "info.getState():" + info.getState());
                String[] wifistate = getResources().getStringArray(R.array.wifi);
                if (info.getExtraInfo() != null)
                    ConnectSSID = info.getExtraInfo();

                if (NetworkInfo.State.DISCONNECTED == info.getState()) {//wifi没连接上
                    Log.i(TAG, "wifi没连接上");
                    if (adapter != null)
                        adapter.setSSID(ConnectSSID, wifistate[5]);
                } else if (NetworkInfo.State.CONNECTED == info.getState()) {//wifi连接上了
                    Log.i(TAG, "wifi连接上了");
                    if (adapter != null)
                        adapter.setSSID(ConnectSSID, wifistate[1]);
                } else if (NetworkInfo.State.CONNECTING == info.getState()) {//正在连接
                    Log.i(TAG, "wifi正在连接");
                    if (adapter != null)
                        adapter.setSSID(ConnectSSID, wifistate[0]);
                } else {
                    if (adapter != null)
                        adapter.setSSID(ConnectSSID, wifistate[6]);
                }
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(intent.getAction())) {
                Log.i(TAG, "网络列" +
                        "表变化了");
                setAdapterList();
            }
        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
