package com.xunixianshi.vrshow.my;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ViewlibConstant;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.util.Validator;
import com.xunixianshi.vrshow.webView.ShowWebViewActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * @author changjigang
 * @ClassName: EmailRegisterActivity
 * @Description: TODO 邮箱注册页
 * @date 2016-3-10 下午 5:31:03
 */

public class EmailRegister extends BaseAct implements View.OnClickListener {

    @Bind(R.id.home_back_icon_iv)
    ImageView homeBackIconIv;
    @Bind(R.id.home_user_email_et)
    EditText homeUserEmailEt;
    @Bind(R.id.home_user_password_et)
    EditText homeUserPasswordEt;
    @Bind(R.id.register_check_cb)
    CheckBox registerCheckCb;
    @Bind(R.id.register_user_protocol_tv)
    TextView registerUserProtocolTv;
    @Bind(R.id.home_user_email_register_btn)
    Button homeUserEmailRegisterBtn;
    @Bind(R.id.home_user_email_register_tv)
    TextView homeUserEmailRegisterTv;
    private String registerEmail;
    private String registerPassword;
    private boolean isRegister = false;
    private HintDialog mHintDialog;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_email_register);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
    }

    @OnClick({R.id.home_back_icon_iv, R.id.register_check_cb, R.id.register_user_protocol_tv, R.id.home_user_email_register_btn, R.id.home_user_email_register_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            // 返回上一页面
            case R.id.home_back_icon_iv:
                EmailRegister.this.finish();
                break;
            case R.id.register_user_protocol_tv:
                Bundle bundle = new Bundle();
                bundle.putString("loadUrl", OkhttpConstant.USER_PROTOCOL);
                bundle.putBoolean("showProgress", true);
                showActivity(EmailRegister.this, ShowWebViewActivity.class, bundle);
                break;
            // 邮箱注册按钮
            case R.id.home_user_email_register_btn:
                MLog.d("---------------------------------------------");
                goToEmailRegister();
                break;
            case R.id.home_user_email_register_tv:
                showActivity(EmailRegister.this, RegisterActivity.class);
                EmailRegister.this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * 注册前验证 通过则发起注册请求
     * @ClassName EmailRegister
     *@author HeChuang
     *@time 2016/12/22 15:54
     */
    private void goToEmailRegister() {
        isRegister = true;
        if (validateInput(true)) {
            gotoRegister();
        }
    }

    /**
     * @param @return 设定文件
     * @return boolean 返回类型
     * @Title: validateInput
     * @Description: TODO 验证用户输入格式是否符合规则
     * @author changjigang
     * @date 2015-12-2
     */
    private boolean validateInput(boolean isRegister) {
        registerEmail = homeUserEmailEt.getText().toString().trim();
        registerPassword = homeUserPasswordEt.getText().toString();
        if (registerEmail.length() == 0) {
            showToastMsg(getResources()
                    .getString(R.string.hint_empty_email_num));
            return false;
        }

        boolean isEmailMatched = Validator.isEmail(registerEmail);
        if (!isEmailMatched) {
            showToastMsg(getResources()
                    .getString(R.string.hint_input_email_num));
            return false;
        }
        if (isRegister) {
//            Pattern etPasswordRegex = Pattern.compile("[^?!@#$%\\^ &*()',=]+");
//            Matcher etPasswordMatcher = etPasswordRegex.matcher(registerPassword);
//            boolean etPasswordMatched = etPasswordMatcher.matches();
            if (registerPassword.equals("")) {
                showToastMsg(getResources().getString(
                        R.string.hint_empty_password));
                return false;
            } else if (registerPassword.length() < 6
                    || registerPassword.length() > 18) {
                showToastMsg(getResources().getString(
                        R.string.hint_long_password));
                return false;
            }
//            else if (!etPasswordMatched) {
//                showToastMsg(getResources().getString(
//                        R.string.hint_password_limit));
//                return false;
//            }
            if (!registerCheckCb.isChecked()) {
                showToastMsg(getResources().getString(
                        R.string.read_user_protocol));
                return false;
            }
        }
        return true;
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: getRegisterCode
     * @Description: TODO 获得注册码
     * @author hechuang
     */
    private void gotoRegister() {
        // 加密邮箱号
        String ras_registerEmail = VR_RsaUtils.encodeByPublicKey(registerEmail);
        // 加密登录密码
        String ras_registerPassword = VR_RsaUtils.encodeByPublicKey(registerPassword);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("loginPassword", ras_registerPassword);
        hashMap.put("registerText", ras_registerEmail);
        hashMap.put("registerType", 2);
        hashMap.put("validateCode", "");
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/register/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        SimpleSharedPreferences.putString("registerPhoneNum", registerEmail, EmailRegister.this);
                        showHintDialog();
                    } else if (code.equals("-91")) {
                        showToastMsg("邮箱已被注册");
                    } else {
                        showToastMsg("参数不合法或信息不正确");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    /**
     * 注册成功后弹框提示用户去邮箱激活
     * @ClassName EmailRegister
     *@author HeChuang
     *@time 2016/12/22 15:55
     */
    private void showHintDialog() {
        mHintDialog = new HintDialog(EmailRegister.this);
        mHintDialog.setContextText("邮箱已注册成功请您到邮箱激活后即可登录");
        mHintDialog.setOkButText("登录");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(EmailRegister.this, LoginActivity.class);
                EmailRegister.this.finish();
            }
        });
        mHintDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
