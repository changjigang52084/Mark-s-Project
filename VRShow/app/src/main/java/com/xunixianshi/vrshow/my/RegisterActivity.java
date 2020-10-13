package com.xunixianshi.vrshow.my;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.view.View.OnClickListener;
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
import com.xunixianshi.vrshow.webView.ShowWebViewActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

/**
 * @author changjigang
 * @ClassName: RegistActivity
 * @Description: TODO 手机注册页
 * @date 2016-3-10 下午 5:31:03
 */

public class RegisterActivity extends BaseAct implements OnClickListener {

    // 返回上一页面按钮
    private ImageView home_back_icon_iv;
    // 用户图片按钮
    private ImageView home_user_icon_iv;
    // 用户手机号输入框
    private EditText home_user_phone_et;
    // 验证码输入框
    private EditText home_user_verification_et;
    // 登录密码输入框
    private EditText home_user_password_et;
    // 获取验证码按钮
    private TextView home_user_verification_tv;
    // 注册按钮
    private Button home_user_register_btn;
    // 立即登录按钮
    private TextView home_user_email_register_tv;
    private CheckBox regist_regist_check_cb;
    private TextView regist_user_protocol_tv;
    private TimeCount time;
    private boolean timeRun = false;
    private String randomNum;

    private String registPhoneNum;
    private String registPassWord;
    private String registCode;
    private boolean isRegist = false;
    private HintDialog mHintDialog;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_user_register);
    }

    @Override
    protected void initView() {
        super.initView();
        home_back_icon_iv = (ImageView) findViewById(R.id.home_back_icon_iv);
        home_user_icon_iv = (ImageView) findViewById(R.id.home_user_icon_iv);
        home_user_phone_et = (EditText) findViewById(R.id.home_user_phone_et);
        home_user_verification_tv = (TextView) findViewById(R.id.home_user_verification_tv);
        home_user_register_btn = (Button) findViewById(R.id.home_user_register_btn);
        home_user_email_register_tv = (TextView) findViewById(R.id.home_user_email_register_tv);
        regist_user_protocol_tv = (TextView) findViewById(R.id.regist_user_protocol_tv);
        regist_regist_check_cb = (CheckBox) findViewById(R.id.regist_regist_check_cb);
        home_user_verification_et = (EditText) findViewById(R.id.home_user_verification_et);
        home_user_password_et = (EditText) findViewById(R.id.home_user_password_et);
        initListener();
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: initListener
     * @Description: TODO 初始化监听器
     * @author changjigang
     */
    private void initListener() {
        home_back_icon_iv.setOnClickListener(this);
        home_user_icon_iv.setOnClickListener(this);
        home_user_verification_tv.setOnClickListener(this);
        home_user_email_register_tv.setOnClickListener(this);
        home_user_register_btn.setOnClickListener(this);
        home_user_phone_et.setOnClickListener(this);
        home_user_verification_et.setOnClickListener(this);
        home_user_password_et.setOnClickListener(this);
        home_user_register_btn.setOnClickListener(this);
        regist_user_protocol_tv.setOnClickListener(this);
        home_user_phone_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                randomNum = "0";
                if (s.length() == 11) {
                    home_user_verification_tv.setClickable(true);
                    home_user_verification_tv.setText("获取验证码");
                } else {
                    if (timeRun) {
                        time.onFinish();
                        time.cancel();
                    }
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            // 返回上一页面
            case R.id.home_back_icon_iv:
                RegisterActivity.this.finish();
                break;
            // 注册按钮
            case R.id.home_user_register_btn:
                MLog.d("---------------------------------------------------");
                registValidate();
                break;
            // 获取验证码按钮
            case R.id.home_user_verification_tv:
                isRegist = false;
                if (validateInput(false)) {
                    getValidateCode();
                }
                break;
            // 立即登录
            case R.id.home_user_login_show_tv:
                showActivity(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.finish();
                break;
            case R.id.regist_user_protocol_tv:
                Bundle bundle = new Bundle();
                bundle.putString("loadUrl", OkhttpConstant.USER_PROTOCOL);
                bundle.putBoolean("showProgress", true);
                showActivity(RegisterActivity.this, ShowWebViewActivity.class, bundle);
                break;
            case R.id.home_user_email_register_tv:
                showActivity(RegisterActivity.this, EmailRegister.class);
                RegisterActivity.this.finish();
                break;
            default:
                break;
        }
    }

    /**
     * @param @return 设定文件
     * @return boolean 返回类型
     * @Title: validateInput
     * @Description: TODO 验证用户输入格式是否符合规则
     * @author hechuang
     * @date 2015-12-2
     */
    private boolean validateInput(boolean isRegist) {
        registPhoneNum = home_user_phone_et.getText().toString().trim();
        registPassWord = home_user_password_et.getText().toString();
        registCode = home_user_verification_et.getText().toString();
        if (registPhoneNum.length() == 0) {
            showToastMsg(getResources()
                    .getString(R.string.hint_empty_phone_num));
            return false;
        }
        Pattern emailRegex = Pattern.compile("^((1[3-8]))\\d{9}$");
        Matcher emailMatcher = emailRegex.matcher(registPhoneNum);
        boolean isEmailMatched = emailMatcher.matches();

        if (!isEmailMatched) {
            showToastMsg(getResources()
                    .getString(R.string.hint_input_phone_num));
            return false;
        }
        if (isRegist) {
            // if (!home_user_check_cb.isChecked()) {
            // showToastMsg(getResources().getString(
            // R.string.user_protocol_no_check));
            // return false;
            // }
//            Pattern etPasswordRegex = Pattern.compile("[^?!@#$%\\^ &*()',=]+");
//            Matcher etPasswordMatcher = etPasswordRegex.matcher(registPassWord);
//            boolean etPasswordMatched = etPasswordMatcher.matches();
            if (registPassWord.equals("")) {
                showToastMsg(getResources().getString(
                        R.string.hint_empty_password));
                return false;
            } else if (registPassWord.length() < 6
                    || registPassWord.length() > 18) {
                showToastMsg(getResources().getString(
                        R.string.hint_long_password));
                return false;
            }
//            else if (!etPasswordMatched) {
//                showToastMsg(getResources().getString(
//                        R.string.hint_password_limit));
//                return false;
//            }
            if (registCode.equals("") && registCode.length() <= 0) {
                showToastMsg(getResources().getString(
                        R.string.hint_empty_validate));
                return false;
            }
            if (!regist_regist_check_cb.isChecked()) {
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
     * @Title: gotoRegist
     * @Description: TODO 去注册
     * @author hechuang
     */
    private void registValidate() {
        isRegist = true;
        if (validateInput(true)) {
            gotoRegist();
        }
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: getRegistCode
     * @Description: TODO 获得注册码
     * @author hechuang
     */

    private void gotoRegist() {
        // 加密登录密码
        String Ras_registPasswordNum = VR_RsaUtils
                .encodeByPublicKey(registPassWord);
        // 加密手机号
        String Ras_registPhoneNum = VR_RsaUtils
                .encodeByPublicKey(registPhoneNum);
        // 加密验证码
        String Ras_registCode = VR_RsaUtils.encodeByPublicKey(registCode);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("loginPassword", Ras_registPasswordNum);
        hashMap.put("registerText", Ras_registPhoneNum);
        hashMap.put("validateCode", Ras_registCode);
        hashMap.put("registerType", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/register/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        SimpleSharedPreferences.putString("registerPhoneNum", registPhoneNum, RegisterActivity.this);
                        showHintDialog();
                    } else if (code.equals("-91")) {
                        showToastMsg("验证码错误或已过期");
                    }else {
                        showToastMsg("注册失败！");
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
     * @param
     * @return void 返回类型
     * @throws
     * @Title: getValidateCode
     * @Description: TODO 获取验证码
     * @author hechuang
     */
    private void getValidateCode() {
        // 加密手机号
        String Ras_registPhoneNum = VR_RsaUtils.encodeByPublicKey(registPhoneNum);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("mobile", Ras_registPhoneNum);
        hashMap.put("msgType", 2);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/send/smsCode/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = (String) result.get("resCode");
                    if (code.equals("0")) {
                        time.start();
                        String codeDesc = (String) result
                                .get("resDesc");
                        showToastMsg("验证码已发送，请注意查收");
                    } else if (code.equals("-91")) {
                        showToastMsg("手机号码已被注册");
                    } else {
                        showToastMsg("您的手机号已经注册,请直接登录!");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
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
     * @author changjigang
     * @ClassName: TimeCount
     * @Description: TODO 验证码倒计时
     * @date 2016-3-10 下午 5:25:01
     */
    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);// 参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {// 计时完毕时触发
            timeRun = false;// 停止计时
            home_user_verification_tv.setText(getResources().getString(
                    R.string.register_validate_again));
            home_user_verification_tv.setClickable(true);
            home_user_verification_tv.setText("重新获取");
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            timeRun = true;// 正在计时
            home_user_verification_tv.setClickable(false);
            MLog.d("millisUntilFinished:" + millisUntilFinished);
            home_user_verification_tv.setText(millisUntilFinished / 1000 + "s");

        }

    }

    private void showHintDialog() {
        mHintDialog = new HintDialog(RegisterActivity.this);
        mHintDialog.setContextText("恭喜您注册成功");
        mHintDialog.setOkButText("登录");
        mHintDialog.setOkClickListaner(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(RegisterActivity.this, LoginActivity.class);
                RegisterActivity.this.finish();
            }
        });
        mHintDialog.show();
    }
}