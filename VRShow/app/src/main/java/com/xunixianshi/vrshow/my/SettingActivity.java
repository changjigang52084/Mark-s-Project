package com.xunixianshi.vrshow.my;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.filedownloader.download.DownloaderManager;
import com.hch.viewlib.util.AppCacheRootManage;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.NetUtil;
import com.hch.viewlib.util.SDCardUtil;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.OpenFileDialog;
import com.xunixianshi.vrshow.interfaces.CallbackBundle;
import com.xunixianshi.vrshow.util.UpdateManager;
import com.hch.viewlib.widget.WiperSwitch;
import com.hch.viewlib.widget.WiperSwitch.OnChangedListener;


import java.util.HashMap;
import java.util.Map;

/**
 * TODO ym
 *
 * @author MarkChang
 * @ClassName SettingActivity
 * @time 2016/11/1 15:48
 */
public class SettingActivity extends BaseAct {

    private static int openfileDialogId = 0;
    private static final int REFRESH_UPDATE_DIAL = 1;

    private ImageView title_left_iv;
    private WiperSwitch user_setting_allow_push_ws;
    private WiperSwitch user_setting_allow_network_cache_ws;
    private WiperSwitch user_setting_allow_auto_cache_ws;
    private WiperSwitch user_setting_allow_auto_play_ws;
    private WiperSwitch user_setting_filter_file_ws;
    private RelativeLayout user_setting_offline_cache_path_rl;
    private RelativeLayout user_setting_check_update_rl;
    private TextView user_setting_offline_cache_path_select_tv;
    private ImageView user_setting_offline_cache_path_select_iv;
    private RelativeLayout user_setting_select_storage_rl;
    private RelativeLayout user_setting_internal_storage_rl;
    private TextView user_setting_internal_storage_size_tv;
    private ImageView user_setting_internal_storage_select_iv;
    private RelativeLayout user_setting_sdcard_storage_rl;
    private TextView user_setting_sdcard_storage_text_tv;
    private TextView user_setting_sdcard_storage_size_tv;
    private ImageView user_setting_sdcard_storage_select_iv;
    private TextView user_setting_offline_cache_path_tv;
    private HintDialog mHintDialog;
    private Context mContext;
    private boolean isShowDetailInformation = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_setting);
        mContext = SettingActivity.this;
    }

    @Override
    protected void initView() {
        super.initView();
        title_left_iv = (ImageView) findViewById(R.id.title_left_iv);
        user_setting_allow_push_ws = (WiperSwitch) findViewById(R.id.user_setting_allow_push_ws);
        user_setting_allow_network_cache_ws = (WiperSwitch) findViewById(R.id.user_setting_allow_network_cache_ws);
        user_setting_allow_auto_cache_ws = (WiperSwitch) findViewById(R.id.user_setting_allow_auto_cache_ws);
        user_setting_allow_auto_play_ws = (WiperSwitch) findViewById(R.id.user_setting_allow_auto_play_ws);
        user_setting_filter_file_ws = (WiperSwitch) findViewById(R.id.user_setting_filter_file_ws);
        user_setting_offline_cache_path_rl = (RelativeLayout) findViewById(R.id.user_setting_offline_cache_path_rl);
        user_setting_check_update_rl = (RelativeLayout) findViewById(R.id.user_setting_check_update_rl);
        user_setting_offline_cache_path_select_tv = (TextView) findViewById(R.id.user_setting_offline_cache_path_select_tv);
        user_setting_offline_cache_path_select_iv = (ImageView) findViewById(R.id.user_setting_offline_cache_path_select_iv);
        user_setting_offline_cache_path_tv = (TextView) findViewById(R.id.user_setting_offline_cache_path_tv);
        user_setting_select_storage_rl = (RelativeLayout) findViewById(R.id.user_setting_select_storage_rl);
        user_setting_internal_storage_rl = (RelativeLayout) findViewById(R.id.user_setting_internal_storage_rl);
        user_setting_internal_storage_size_tv = (TextView) findViewById(R.id.user_setting_internal_storage_size_tv);
        user_setting_internal_storage_select_iv = (ImageView) findViewById(R.id.user_setting_internal_storage_select_iv);
        user_setting_sdcard_storage_rl = (RelativeLayout) findViewById(R.id.user_setting_sdcard_storage_rl);
        user_setting_sdcard_storage_text_tv = (TextView) findViewById(R.id.user_setting_sdcard_storage_text_tv);
        user_setting_sdcard_storage_size_tv = (TextView) findViewById(R.id.user_setting_sdcard_storage_size_tv);
        user_setting_sdcard_storage_select_iv = (ImageView) findViewById(R.id.user_setting_sdcard_storage_select_iv);
        user_setting_allow_push_ws.setChecked(false);
        user_setting_allow_network_cache_ws.setChecked(SimpleSharedPreferences.getBoolean("allowUseNetwork", SettingActivity.this));
        user_setting_allow_auto_cache_ws.setChecked(SimpleSharedPreferences.getBoolean("allowAutoCache", SettingActivity.this));
        user_setting_allow_auto_play_ws.setChecked(SimpleSharedPreferences.getBoolean("allowAutoPlay", SettingActivity.this));
        user_setting_filter_file_ws.setChecked(SimpleSharedPreferences.getBoolean("filterSmallFile", SettingActivity.this));
        if (AppCacheRootManage.getInstance(mContext).getCacheSaveRootPathType() == 0) {
            user_setting_internal_storage_select_iv.setSelected(true);
        } else {
            user_setting_sdcard_storage_select_iv.setSelected(true);
        }
        initListener();
    }

    private void initListener() {
        title_left_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                SettingActivity.this.finish();
            }
        });

        user_setting_allow_push_ws
                .setOnChangedListener(new WiperSwitch.OnChangedListener() {

                    @Override
                    public void OnChanged(WiperSwitch wiperSwitch,
                                          boolean checkState) {

                    }
                });
        user_setting_allow_network_cache_ws
                .setOnChangedListener(new OnChangedListener() {

                    @Override
                    public void OnChanged(WiperSwitch wiperSwitch,
                                          boolean checkState) {
                        MLog.d("checkState:" + checkState);
                        SimpleSharedPreferences.putBoolean("allowUseNetwork", checkState, SettingActivity.this);

                    }
                });
        user_setting_allow_auto_cache_ws
                .setOnChangedListener(new OnChangedListener() {

                    @Override
                    public void OnChanged(WiperSwitch wiperSwitch,
                                          boolean checkState) {
                        SimpleSharedPreferences.putBoolean("allowAutoCache", checkState, SettingActivity.this);
                    }
                });
        user_setting_allow_auto_play_ws
                .setOnChangedListener(new OnChangedListener() {

                    @Override
                    public void OnChanged(WiperSwitch wiperSwitch, boolean checkState) {
                        SimpleSharedPreferences.putBoolean("allowAutoPlay", checkState, SettingActivity.this);
                    }
                });
        user_setting_filter_file_ws
                .setOnChangedListener(new OnChangedListener() {

                    @Override
                    public void OnChanged(WiperSwitch wiperSwitch,
                                          boolean checkState) {
                        SimpleSharedPreferences.putBoolean("filterSmallFile", checkState, SettingActivity.this);
                    }
                });


        user_setting_check_update_rl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!NetUtil.hasAvailableNet(SettingActivity.this)) {
                    showToastMsg("网络连接失败！请检查网络");
                } else {
                    // 此接口被调用时会进行版本检测，若有新版本会弹出对话框提示用户。
                    UpdateManager manager = new UpdateManager(SettingActivity.this);
                    manager.getServerVersionCode(true);
                }
            }
        });

        user_setting_offline_cache_path_rl
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (isShowDetailInformation) {
                            user_setting_offline_cache_path_select_iv.setImageResource(R.drawable.arrow_right);
                            user_setting_select_storage_rl.setVisibility(View.GONE);
                            isShowDetailInformation = false;
                        } else {
                            user_setting_offline_cache_path_select_iv.setImageResource(R.drawable.arrow_up);
                            user_setting_select_storage_rl.setVisibility(View.VISIBLE);
                            isShowDetailInformation = true;
                        }
                    }
                });
        user_setting_internal_storage_rl
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        user_setting_offline_cache_path_select_tv
                                .setText("手机存储");
                        user_setting_internal_storage_select_iv
                                .setSelected(true);
                        user_setting_sdcard_storage_select_iv
                                .setSelected(false);
                        if (AppCacheRootManage.getInstance(mContext).changeCacheSaveRootPath(mContext, AppCacheRootManage.STORAGE_DIRECTORY_TYPE)) {
                            DownloaderManager.getInstance().setDownloadStorePath(AppCacheRootManage.getInstance(mContext).getCacheSaveRootPath());
                        }
                    }
                });
        user_setting_sdcard_storage_rl
                .setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        user_setting_offline_cache_path_select_tv
                                .setText("外置sd卡存储");
                        user_setting_internal_storage_select_iv
                                .setSelected(false);
                        user_setting_sdcard_storage_select_iv.setSelected(true);
                        if (AppCacheRootManage.getInstance(mContext).changeCacheSaveRootPath(mContext, AppCacheRootManage.STORAGE_SDCARD_TYPE)) {
                            DownloaderManager.getInstance().setDownloadStorePath(AppCacheRootManage.getInstance(mContext).getCacheSaveRootPath());
                        }
                    }
                });
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        mHintDialog = new HintDialog(mContext);
        MLog.d("是否有sd卡：" + SDCardUtil.externalMemoryAvailable());
        MLog.d("系统总内存："
                + SDCardUtil.formatFileSize(
                SDCardUtil.getTotalMemorySize(mContext), true));
        MLog.d("手机内部总内存："
                + SDCardUtil.formatFileSize(
                SDCardUtil.getTotalInternalMemorySize(), true));
        MLog.d("手机sd卡总内存："
                + SDCardUtil.formatFileSize(
                SDCardUtil.getTotalExternalMemorySize(), true));
        MLog.d("手机sd卡剩余内存："
                + SDCardUtil.formatFileSize(
                SDCardUtil.getAvailableExternalMemorySize(), true));
        MLog.d("手机内部剩余内存："
                + SDCardUtil.formatFileSize(
                SDCardUtil.getAvailableInternalMemorySize(), true));
        MLog.d("当前可用内存："
                + SDCardUtil.formatFileSize(
                SDCardUtil.getAvailableMemory(mContext), true));
        MLog.d("外置sd卡：" + SDCardUtil.getExtSDCardPath());
        user_setting_internal_storage_size_tv.setText("剩余内存："
                + SDCardUtil.formatFileSize(
                SDCardUtil.getAvailableInternalMemorySize(), true));
        if (SDCardUtil.getExtSDCardPath().size() > 0) {
            user_setting_sdcard_storage_rl.setEnabled(true);
            user_setting_sdcard_storage_size_tv.setText("外置SD卡剩余内存："
                    + SDCardUtil.formatFileSize(
                    SDCardUtil.getAvailableExternalMemorySize(), true));
        } else {
            user_setting_sdcard_storage_rl.setEnabled(false);
            user_setting_sdcard_storage_select_iv.setVisibility(View.GONE);
            user_setting_sdcard_storage_text_tv.setTextColor(Color
                    .parseColor("#999999"));
            user_setting_sdcard_storage_size_tv.setText("暂无外置SD卡");
        }
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == openfileDialogId) {
            Map<String, Integer> images = new HashMap<String, Integer>();
            // 下面几句设置各文件类型的图标， 需要你先把图标添加到资源文件夹
            images.put(OpenFileDialog.sRoot, R.drawable.filedialog_root); // 根目录图标
            images.put(OpenFileDialog.sParent, R.drawable.filedialog_folder_up); // 返回上一层的图标
            images.put(OpenFileDialog.sFolder, R.drawable.filedialog_folder); // 文件夹图标
            images.put("wav", R.drawable.filedialog_wavfile); // wav文件图标
            images.put(OpenFileDialog.sEmpty, R.drawable.filedialog_root);
            Dialog dialog = OpenFileDialog.createDialog(id, this, "打开文件",
                    new CallbackBundle() {
                        @Override
                        public void callback(Bundle bundle) {
                            String filepath = bundle.getString("path");
                            Log.d("TAG", "filepath::" + filepath);
                            user_setting_offline_cache_path_tv
                                    .setText(filepath);
                        }
                    }, ".wav;", images);
            return dialog;
        }
        return null;
    }

}
