package com.xunixianshi.vrshow.my;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class BackPassWordOneActivity extends BaseAct {

    private ImageView title_close_iv;

    private TextView back_password_for_email_tv;

    private EditText back_password_phone_et;

    private EditText back_password_verification_et;

    private TextView back_password_verification_tv;

    private Button back_password_next_bt;

    private String registPhoneNum;
    private String validateNum;
    private TimeCount time;
    private String randomNum;
    private boolean timeRun = false;
    private String back_uid;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_back_password_one);
    }

    @Override
    protected void initView() {
        super.initView();
        title_close_iv = (ImageView) findViewById(R.id.title_close_iv);
        back_password_for_email_tv = (TextView) findViewById(R.id.back_password_for_email_tv);
        back_password_phone_et = (EditText) findViewById(R.id.back_password_phone_et);
        back_password_verification_et = (EditText) findViewById(R.id.back_password_verification_et);
        back_password_verification_tv = (TextView) findViewById(R.id.back_password_verification_tv);
        back_password_next_bt = (Button) findViewById(R.id.back_password_next_bt);
        initListener();
    }

    /**
     * @return void 返回类型
     * @Title: initListener
     * @Description: TODO 初始化监听器
     * @author hechuang
     * @date 2015-12-8
     */
    private void initListener() {
        // 关闭按钮
        title_close_iv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                BackPassWordOneActivity.this.finish();
            }
        });
        back_password_phone_et.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                randomNum = "0";
                if (s.length() == 11) {
                    back_password_verification_tv.setClickable(true);
                    back_password_verification_tv.setText("获取验证码");
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
        back_password_verification_tv.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    validatePhoneNum();
                }
            }
        });
        back_password_next_bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                validateNum = back_password_verification_et.getText()
                        .toString().trim();
                MLog.d("validateNum" + validateNum);
                registPhoneNum = back_password_phone_et.getText().toString()
                        .trim();
                if (!validateInput()) {
                    return;
                } else if (validateNum.equals("")) {
                    showToastMsg("输入你的验证码");
                    return;
                } else {
                    gotoNext();
                }
            }
        });

        back_password_for_email_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(BackPassWordOneActivity.this, BackPassWordForEmailActivity.class);
                BackPassWordOneActivity.this.finish();
            }
        });
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        time = new TimeCount(60000, 1000);// 构造CountDownTimer对象
    }

    /**
     * @param @return 设定文件
     * @return boolean 返回类型
     * @Title: validateInput
     * @Description: TODO 验证用户输入格式是否符合规则
     * @author hechuang
     * @date 2015-12-2
     */
    private boolean validateInput() {
        registPhoneNum = back_password_phone_et.getText().toString().trim();
        if (registPhoneNum != null && registPhoneNum.equals("")) {
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
        return true;
    }

    private void validatePhoneNum() {
        // 加密手机号
        String Ras_registPhoneNum = VR_RsaUtils
                .encodeByPublicKey(registPhoneNum);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("inputTxt", Ras_registPhoneNum);
        hashMap.put("validateType", 2);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/info/validate/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.getString("resCode").equals("0")) {

                        int isExistence = (int) result.get("isExistence");
                        String rsa_userId = result.getString("userId");
                        if (isExistence==0 || StringUtil.isEmpty(rsa_userId)) { //判断返回的用户id是否是正确的
                            showToastMsg("用户不存在或用户存在异常");
                        } else {
                            back_uid = VR_RsaUtils.decryptByPublicKey(rsa_userId);
                            time.start();
                            getValidateCode();
                        }
                    } else if (result.getString("resCode").equals("-91")) {
                        showToastMsg("用户不存在");
                    } else {
                        showToastMsg("验证码不正确或已过期");
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: getValidateCode
     * @Description: TODO 获取验证码
     * @author hechuang
     */
    private void getValidateCode() {
        // 加密手机号
        String Ras_registPhoneNum = VR_RsaUtils
                .encodeByPublicKey(registPhoneNum);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("mobile", Ras_registPhoneNum);
        hashMap.put("msgType", 3);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/send/smsCode/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = (String) result.get("resCode");
                    String codeDesc = (String) result.get("resDesc");
                    if (code.equals("0")) {
                        time.start();
                        showToastMsg("验证码已发送，请注意查收");
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

    private void gotoNext() {
        // 加密手机号
        String Ras_registPhoneNum = VR_RsaUtils
                .encodeByPublicKey(registPhoneNum);
        // 加密手机号
        String Ras_validateNum = VR_RsaUtils.encodeByPublicKey(validateNum);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("mobile", Ras_registPhoneNum);
        hashMap.put("validateCode", Ras_validateNum);
        hashMap.put("validateType", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/validate/sms/code/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = (String) result.get("resCode");
                    String codeDesc = (String) result
                            .get("resDesc");
                    if (code.equals("0")) {
                        time.start();
                        showToastMsg(codeDesc);
                        Bundle bundle = new Bundle();
                        bundle.putString("back_uid", back_uid);
                        skipActivity(BackPassWordOneActivity.this,
                                BackPassWordTwoActivity.class, bundle);
                    } else {
                        showToastMsg("验证码错误或已过期");
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
            back_password_verification_tv.setClickable(true);
            back_password_verification_tv.setText("重新获取");
        }

        @Override
        public void onTick(long millisUntilFinished) {// 计时过程显示
            timeRun = true;// 正在计时
            back_password_verification_tv.setClickable(false);
            MLog.d("millisUntilFinished:" + millisUntilFinished);
            back_password_verification_tv.setText(millisUntilFinished / 1000
                    + "s");

        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        time.cancel();
    }
}
