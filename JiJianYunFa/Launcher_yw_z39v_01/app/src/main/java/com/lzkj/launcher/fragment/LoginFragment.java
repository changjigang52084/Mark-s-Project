package com.lzkj.launcher.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Build.VERSION_CODES;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.baize.adpress.core.utils.AesCoderUtils;
import com.lzkj.launcher.R;
import com.lzkj.launcher.app.LauncherApp;
import com.lzkj.launcher.impl.LoginCallback;
import com.lzkj.launcher.impl.SwitchLayoutImpl;
import com.lzkj.launcher.postern.PosternManager;
import com.lzkj.launcher.service.LoginRequestManager;
import com.lzkj.launcher.util.AppUtil;
import com.lzkj.launcher.util.ConfigSettings;
import com.lzkj.launcher.util.Constant;
import com.lzkj.launcher.util.FileStore;
import com.lzkj.launcher.util.QRCodeUtil;
import com.lzkj.launcher.util.ShareUtil;
import com.lzkj.launcher.util.StringUtil;

import java.io.File;
import java.lang.reflect.Field;

@SuppressLint({"NewApi", "ValidFragment"})
public class LoginFragment extends Fragment implements OnClickListener, LoginCallback {

    private static final String TAG = "LoginFragment";

    private final int delayMillis = 3 * 1000;

    private ImageView qrCode;
    private TextView resultTv;
    private TextView loginTv;
    private TextView appInfoTv;
    private TextView clientIdTv;
    private TextView debugMsgTv;
    private View rootView = null;
    private Handler handler = new Handler(LauncherApp.getApplication().getMainLooper());
    private SwitchLayoutImpl listener;

    private TextView mTipLoadTv;
    private ProgressBar mProgressBar;
    /**
     * 后门管理器
     */
    private PosternManager posternManager = null;

    /**
     * 后门窗口：左上角，右上角，左下角，右下角
     */
    private FrameLayout posternWindowTL, posternWindowTR, posternWindowBL, posternWindowBR;

    public LoginFragment(SwitchLayoutImpl listener) {
        this.listener = listener;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "LoginFragment onCreate.");
        posternManager = PosternManager.get(new Runnable() {
            @Override
            public void run() {
                listener.switchLayout(Constant.ALL_APP_FRAGMENT);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "LoginFragment onCreateView.");
        rootView = inflater.inflate(R.layout.fragment_login, container, false);
        rootView.setBackgroundResource(R.drawable.bg_anim);

        qrCode = (ImageView) rootView.findViewById(R.id.qr_code);
        resultTv = (TextView) rootView.findViewById(R.id.result_tv);
        loginTv = (TextView) rootView.findViewById(R.id.login_tv);
        appInfoTv = (TextView) rootView.findViewById(R.id.app_info_tv);
        clientIdTv = (TextView) rootView.findViewById(R.id.client_id_tv);
        debugMsgTv = (TextView) rootView.findViewById(R.id.debug_tv);
        mTipLoadTv = (TextView) rootView.findViewById(R.id.tv_tip_data_loading);
        mProgressBar = (ProgressBar) rootView.findViewById(R.id.prb_tip);

        posternWindowTL = (FrameLayout) rootView.findViewById(R.id.top_left_window);
        posternWindowTR = (FrameLayout) rootView.findViewById(R.id.top_right_window);
        posternWindowBL = (FrameLayout) rootView.findViewById(R.id.bottom_left_window);
        posternWindowBR = (FrameLayout) rootView.findViewById(R.id.bottom_right_window);
        posternWindowTL.setOnClickListener(this);
        posternWindowTR.setOnClickListener(this);
        posternWindowBL.setOnClickListener(this);
        posternWindowBR.setOnClickListener(this);
        loginTv.setOnClickListener(this);

        AnimationDrawable animationDrawable = (AnimationDrawable) rootView.getBackground();
        animationDrawable.start();
        createQRCode();
        return rootView;
    }

    /**
     * @Description: TODO(生成二维码图片)
     */
    private void createQRCode() {
        String content = getQRCodeContent();
        if (StringUtil.isNullStr(content)) {
            Log.e(TAG, "LoginFragment createQRImage reload device info after 5 second.");
            getQRCodeInfoFromCommunication();
            handler.postDelayed(showQRCodeRun, delayMillis);
            return;
        }
        initAppInfo();
        if (getActivity() != null) {
            mTipLoadTv.setVisibility(View.GONE);
            mProgressBar.setVisibility(View.GONE);
            //二维码中间图片
            Bitmap logoBm = null;
            try {
                logoBm = BitmapFactory.decodeResource(getResources(), R.drawable.app_logo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            if (null != logoBm) {
                //二维码宽高
                int width = (int) (getResources().getDimension(R.dimen.qr_code_w));
                String qrCodePath = FileStore.getQrcodeFolderPath() + File.separator + "macAddress.jpg";
                Boolean createCode = QRCodeUtil.createQRImage(content, width, width, logoBm, qrCodePath);
                if (createCode && new File(qrCodePath).exists()) {
                    Uri qrCodeUri = Uri.parse("file://" + qrCodePath);
                    qrCode.setImageURI(qrCodeUri);
                } else {
                    createQRCode();
                    Log.w(TAG, "LoginFragment createQRCode create QRCode fail.");
                }
            }
        }
    }

    /**
     * 初始化app信息
     */
    private void initAppInfo() {
        try {
            String appInfo = AppUtil.getAppInfo(false);
            if (!StringUtil.isNullStr(appInfo)) {
                appInfoTv.setText(appInfo);
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    private Runnable showQRCodeRun = new Runnable() {
        @Override
        public void run() {
            createQRCode();
        }
    };

    /**
     * @Title: getQRCodeInfoFromCommunication
     * @Description: 发送广播向Communication获取二维码信息
     */
    private void getQRCodeInfoFromCommunication() {
        Intent intent = new Intent(Constant.LAUNCHER_GET_QRCODE_INFO_ACTION);
        LauncherApp.getApplication().sendBroadcast(intent);
    }

    /**
     * 获取二维码内容
     * 二维码的内容由：设备mac地址、当前城市名、个推id、极光id 组成。
     */
    public String getQRCodeContent() {
        if (StringUtil.isNullStr(ConfigSettings.MAC_ADDRESS)) {
            Log.w(TAG, "LoginFragment getQRCodeContent mac address is null.");
            resultTv.setText("Mac address is null");
            return null;
        }

        /*if (StringUtil.isNullStr(ConfigSettings.getLocationCity())) {
            Log.w(TAG, "LoginFragment getQRCodeContent location city is null.");
            resultTv.setText("Location city is null");
            return null;
        }*/

        if (StringUtil.isNullStr(ConfigSettings.getGetuiClientId())) {
            resultTv.setText("Getui client id is null");
            Log.w(TAG, "LoginFragment getQRCodeContent getui client id is null.");
            return null;
        }

        debugMsgTv.setText("clientId: " + ConfigSettings.getGetuiClientId());
        if (StringUtil.isNullStr(ConfigSettings.getJpushClientId())) {
            resultTv.setText("Jpush client id is null");
            Log.w(TAG, "LoginFragment getQRCodeContent jpush client id is null.");
            return null;
        }
        resultTv.setText("");
        String qrCodeContent = ConfigSettings.MAC_ADDRESS + "|" +
                ConfigSettings.getLocationCity() + "|" +
                ConfigSettings.getGetuiClientId() + "|" +
                ConfigSettings.getJpushClientId();
        Log.d(TAG, "LoginFragment getQRCodeContent qrCodeContent: " + qrCodeContent);
        //AES加密
        String encryptQrCode = null;
        try {
            encryptQrCode = AesCoderUtils.aesEncrypt(qrCodeContent, Constant.AES_ENCRYP_KEY);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.d(TAG, "LoginFragment getQRCodeContent encryptQrCode: " + encryptQrCode);
        return encryptQrCode;
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "LoginFragment onDestroy.");
        LoginRequestManager.newInstance().unbindLoginRequestService();
        posternManager.destory();
        super.onDestroy();
    }

    @Override
    public void onSuccess(final String result) {
        Log.d(TAG, "LoginFragment onSuccess result: " + result);
        resultTv.post(new Runnable() {
            @Override
            public void run() {
                resultTv.setVisibility(View.VISIBLE);
                // 获取返回结果,是否绑定成功!
                if (null != result) {
                    ShareUtil.newInstance().setBoolean(ShareUtil.AUTHORIZE_KEY, true);
                    resultTv.setText(result);
                    listener.switchLayout(Constant.ALL_APP_FRAGMENT);
                }
            }
        });
    }

    @Override
    public void onFail(final String errMsg) {
        Log.e(TAG, "LoginFragment onFail errMsg: " + errMsg);
        resultTv.post(new Runnable() {
            @Override
            public void run() {
                resultTv.setVisibility(View.VISIBLE);
                if (null != errMsg) {
                    resultTv.setText(errMsg);
                }
            }
        });
    }

    /**
     * 输入后门key
     *
     * @param
     * @return
     */
    private void inputPosternKey(String key) {
        posternManager.inputPosternKey(key);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.login_tv:
                if (!posternManager.isPosternSwitchListener()) {
                    posternManager.openPosternListener();
                }
                break;
            case R.id.top_left_window:
                inputPosternKey(R.id.top_left_window + "");
                break;
            case R.id.top_right_window:
                inputPosternKey(R.id.top_right_window + "");
                break;
            case R.id.bottom_left_window:
                inputPosternKey(R.id.bottom_left_window + "");
                break;
            case R.id.bottom_right_window:
                inputPosternKey(R.id.bottom_right_window + "");
                break;
            default:
                break;
        }
    }

    /**
     * 这段可以解决fragment嵌套fragment会崩溃的问题
     */
    @Override
    public void onDetach() {
        super.onDetach();
        if (Build.VERSION.SDK_INT > VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            try {
                // 参数是固定写法
                Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
                childFragmentManager.setAccessible(true);
                childFragmentManager.set(this, null);
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
