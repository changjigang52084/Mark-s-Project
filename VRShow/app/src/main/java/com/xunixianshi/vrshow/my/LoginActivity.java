/**
 * @Title: UserLoginActivity.java
 * @Package com.xunixianshi.vrshow.user.login
 * @Description: TODO 用户登录界面
 * @author hechuang
 * @date 2016年3月8日 下午4:10:26
 * @version V1.0
 */
package com.xunixianshi.vrshow.my;

import android.Manifest;
import android.content.Intent;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ViewlibConstant;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.xunixianshi.vrshow.MainActivity;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.permissions.RxPermissions;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import rx.functions.Action1;

/**
 * @author hechuang
 * @ClassName: UserLoginActivity
 * @Description: TODO 用户登录页面
 * @date 2016年3月8日 下午4:10:26
 */

public class LoginActivity extends BaseAct implements OnClickListener {
    // 返回键
    private ImageView home_back_icon_iv;
    // 用户图像
    private ImageView home_user_icon_iv;
    // 手机号输入框
    private EditText home_user_phone_et;
    // 密码输入框
    private EditText home_user_password_et;
    // 忘记密码
    private TextView home_user_login_register_tv;
    // 点击登录按钮
    private Button home_user_register_btn;
    // 立即注册
    private TextView home_user_login_show_tv;

    private ImageButton login_for_sina_ib;
    private ImageButton login_for_qq_ib;
    private ImageButton login_for_wechat_ib;

    private UMShareAPI mShareAPI = null;
    private SHARE_MEDIA platform = null;
    private LoadingAnimationDialog progressDialog;

    private String loginPhoneNum;
    private String loginPassword;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_user_login);
    }

    @Override
    protected void initView() {
        super.initView();
        home_back_icon_iv = (ImageView) findViewById(R.id.home_back_icon_iv);
        home_user_icon_iv = (ImageView) findViewById(R.id.home_user_icon_iv);
        home_user_phone_et = (EditText) findViewById(R.id.home_user_phone_et);
        home_user_password_et = (EditText) findViewById(R.id.home_user_password_et);
        home_user_login_register_tv = (TextView) findViewById(R.id.home_user_login_register_tv);
        home_user_register_btn = (Button) findViewById(R.id.home_user_register_btn);
        home_user_login_show_tv = (TextView) findViewById(R.id.home_user_login_show_tv);
        login_for_sina_ib = (ImageButton) findViewById(R.id.login_for_sina_ib);
        login_for_qq_ib = (ImageButton) findViewById(R.id.login_for_qq_ib);
        login_for_wechat_ib = (ImageButton) findViewById(R.id.login_for_wechat_ib);
        initListener();
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: initListener
     * @Description: TODO 初始化监听器
     * @author changjigang
     */
    private void initListener() {
        home_back_icon_iv.setOnClickListener(this);
        home_user_icon_iv.setOnClickListener(this);
        home_user_phone_et.setOnClickListener(this);
        home_user_password_et.setOnClickListener(this);
        home_user_login_register_tv.setOnClickListener(this);
        home_user_register_btn.setOnClickListener(this);
        home_user_login_show_tv.setOnClickListener(this);
        login_for_sina_ib.setOnClickListener(this);
        login_for_qq_ib.setOnClickListener(this);
        login_for_wechat_ib.setOnClickListener(this);
        home_user_phone_et.setFilters(new InputFilter[]{filter});
    }

    private InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
            if (source.equals(" ") || source.toString().contentEquals("\n")) return "";
            else return null;
        }
    };

    @Override
    protected void initData() {
        super.initData();
        progressDialog = new LoadingAnimationDialog(LoginActivity.this);
        mShareAPI = UMShareAPI.get(LoginActivity.this);
        //从文件保存中取出上次登录的用户
        String loginPhoneNum = SimpleSharedPreferences.getString("registerPhoneNum", this);
        home_user_phone_et.setText(loginPhoneNum);
        home_user_phone_et.setSelection(loginPhoneNum.length());
        //检查是否获取了指定权限
        RxPermissions rxPermissions = RxPermissions.getInstance(this);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_PHONE_STATE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                        } else {
                            // 未获取权限
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回上一页面
            case R.id.home_back_icon_iv:
                LoginActivity.this.finish();
                break;
            // 忘记密码
            case R.id.home_user_login_register_tv:
                showActivity(LoginActivity.this, BackPassWordOneActivity.class);
                LoginActivity.this.finish();
                break;
            // 登录按钮
            case R.id.home_user_register_btn:
                MLog.d("------------------------------------------------------");
                gotoLogin();
                break;
            // 立即注册
            case R.id.home_user_login_show_tv:
                showActivity(LoginActivity.this, RegisterActivity.class);
                LoginActivity.this.finish();
                break;
            // 新浪微博登录
            case R.id.login_for_sina_ib:
                platform = SHARE_MEDIA.SINA;
                mShareAPI.doOauthVerify(LoginActivity.this, platform,
                        umAuthListener);
                break;
            // QQ登录
            case R.id.login_for_qq_ib:
                platform = SHARE_MEDIA.QQ;
                mShareAPI.doOauthVerify(LoginActivity.this, platform,
                        umAuthListener);
                break;
            // 微信登录55555555555
            case R.id.login_for_wechat_ib:
                platform = SHARE_MEDIA.WEIXIN;
                mShareAPI.doOauthVerify(LoginActivity.this, platform,
                        umAuthListener);
                break;

            default:
                break;
        }
    }

    /**
     * 登录校验通过则登录
     * @ClassName LoginActivity
     *@author HeChuang
     *@time 2016/12/22 15:52
     */
    private void gotoLogin() {
        if (validateInput()) {
            getLoginCode();
        }
    }

    /**
     * 登录请求
     * @ClassName LoginActivity
     * @author HeChuang
     * @time 2016/12/22 15:51
     */
    private void getLoginCode() {
        progressDialog.show();
        MLog.d("loginPhoneNum" + loginPhoneNum);
        // 加密登录手机号
        String Ras_loginPhoneNum = VR_RsaUtils.encodeByPublicKey(loginPhoneNum);
        // 加密登录密码
        String Ras_loginPassword = VR_RsaUtils.encodeByPublicKey(loginPassword);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("loginName", Ras_loginPhoneNum);
        hashMap.put("loginPassword", Ras_loginPassword);
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/login/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                progressDialog.dismiss();
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        String rsa_userId = result.getString("userId");
                        int limitLogin = result.getInt("isLimitLogin");
                        String rsa_imAccount = result.getString("imAccount");
                        String rsa_imPassword = result.getString("imPassword");
                        String userId = VR_RsaUtils.decryptByPublicKey(rsa_userId);
                        String imAccount = VR_RsaUtils.decryptByPublicKey(rsa_imAccount);
                        String imPassword = VR_RsaUtils.decryptByPublicKey(rsa_imPassword);
                        AppContent.UID = userId;
                        SimpleSharedPreferences.putString("loginUid", userId, LoginActivity.this);
                        SimpleSharedPreferences.putInt("limitLogin", limitLogin, LoginActivity.this);
                        SimpleSharedPreferences.putString("customerUserName", imAccount, LoginActivity.this);
                        SimpleSharedPreferences.putString("customerPassWord", imPassword, LoginActivity.this);
                        if (limitLogin == 2) {
                            showToastMsg("登录成功，当前用户属于未激活状态.");
                        } else {
                            showToastMsg("登录成功");
                        }
                        SimpleSharedPreferences.putString("registerPhoneNum", loginPhoneNum, LoginActivity.this);
                        LoginActivity.this.finish();
                    } else if (code.equals("-90")) {
                        showToastMsg("密码错误，登录失败");
                    } else if (code.equals("-97")) {
                        showToastMsg("帐户不存在，登录失败");
                    } else {
                        showToastMsg(result.getString("resDesc"));
                    }
                } catch (JSONException e) {
                    showToastMsg("解析数据异常");
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    /**
     * 登录前验证
     * @ClassName LoginActivity
     *@author HeChuang
     *@time 2016/12/22 15:51
     */
    private boolean validateInput() {
        loginPhoneNum = home_user_phone_et.getText().toString().trim();
        loginPassword = home_user_password_et.getText().toString();
        if (loginPhoneNum.length() == 0) {
            showToastMsg(getResources()
                    .getString(R.string.hint_empty_phone_num));
            return false;
        }
        if (loginPassword.length() == 0) {
            showToastMsg(getResources().getString(R.string.hint_empty_password));
            return false;
        }
        return true;
    }

    /**
     * @Title: otherLogin
     * @Description: TODO 第三方登录
     * @author hechuang
     * @param 设定文件
     * @return void 返回类型
     * @throws
     */
    private UMAuthListener umAuthListener = new UMAuthListener() {

        @Override
        public void onComplete(SHARE_MEDIA platform, int action,
                               Map<String, String> data) {
            String openid = data.get("openid");
            SimpleSharedPreferences.putString("openid", openid, LoginActivity.this);
            MLog.i("openid", "" + openid);
            showToastMsg(data.toString());
        }

        @Override
        public void onError(SHARE_MEDIA platform, int action, Throwable t) {
            Toast.makeText(LoginActivity.this, "授权失败",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform, int action) {
            Toast.makeText(LoginActivity.this, "授权取消",
                    Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mShareAPI.onActivityResult(requestCode, resultCode, data);
    }
}
