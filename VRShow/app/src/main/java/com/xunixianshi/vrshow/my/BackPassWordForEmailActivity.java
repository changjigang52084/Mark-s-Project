package com.xunixianshi.vrshow.my;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.util.Validator;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Call;

public class BackPassWordForEmailActivity extends BaseAct {

    private ImageView title_close_iv;

    private TextView back_password_for_Phone_tv;

    private EditText back_password_email_et;


    private Button back_password_eamil_commit_bt;

    private String registEmailNum;

    private LoadingAnimationDialog myProgressDialog;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_back_password_email);
    }

    @Override
    protected void initView() {
        super.initView();
        title_close_iv = (ImageView) findViewById(R.id.title_close_iv);
        back_password_for_Phone_tv = (TextView) findViewById(R.id.back_password_for_Phone_tv);
        back_password_email_et = (EditText) findViewById(R.id.back_password_email_et);
        back_password_eamil_commit_bt = (Button) findViewById(R.id.back_password_eamil_commit_bt);
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
                BackPassWordForEmailActivity.this.finish();
            }
        });
        back_password_email_et.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND || (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    //隐藏软键盘
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;

                }
                return false;
            }
        });
        back_password_eamil_commit_bt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (!validateInput()) {
                    return;
                } else {
                    gotoNext();
                }
            }
        });

        back_password_for_Phone_tv.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showActivity(BackPassWordForEmailActivity.this, BackPassWordOneActivity.class);
                BackPassWordForEmailActivity.this.finish();
            }
        });
    }

    @Override
    protected void initData() {
        // TODO Auto-generated method stub
        super.initData();
        myProgressDialog = new LoadingAnimationDialog(BackPassWordForEmailActivity.this);
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
        registEmailNum = back_password_email_et.getText().toString().trim();
        if (registEmailNum.equals("")) {
            showToastMsg(getResources()
                    .getString(R.string.inputEmailNumber));
            return false;
        }

        //^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$  //^[a-zA-Z][\w\.-]*[a-zA-Z0-9]@[a-zA-Z0-9][\w\.-]*[a-zA-Z0-9]\.[a-zA-Z][a-zA-Z\.]*[a-zA-Z]$
      //"^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$"
        boolean isEmailMatched = Validator.isEmail(registEmailNum);
        if (!isEmailMatched) {
            showToastMsg(getResources()
                    .getString(R.string.hint_input_email_num));
            return false;
        }
        return true;
    }

    private void gotoNext() {
        // 加密手机号
        myProgressDialog.show();
        final String inputTxt = VR_RsaUtils
                .encodeByPublicKey(registEmailNum);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("inputTxt", inputTxt);
        hashMap.put("validateType", 3);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/info/validate/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = (String) result.get("resCode");
                    if (code.equals("0")) {
                        int isExistence = (int) result.get("isExistence");
                        if (isExistence == 0) {
                            showToastMsg("用户信息不存在");
                            myProgressDialog.dismiss();
                        } else if (isExistence == 1) {
                            String res_userId = (String) result.get("userId");
                            if(StringUtil.isEmpty(res_userId)){
                                showToastMsg("用户信息不存在");
                            }
                            else{
                                String userId = VR_RsaUtils.decryptByPublicKey(res_userId);
                                String res_userIds = VR_RsaUtils.encodeByPublicKey(userId);
                                MLog.i("userId:" + res_userIds);
                                getActiveEmail(inputTxt, res_userIds);
                            }
                        }
                    } else {
                        showToastMsg("验证码不正确或已过期");
                        myProgressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    myProgressDialog.dismiss();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    private void getActiveEmail(String inputTxt, String res_userIds) {
        String email = inputTxt;
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("email", email);
        hashMap.put("userId", res_userIds);
        hashMap.put("msgType", 3);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/send/active/email/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = (String) result.get("resCode");
                    if (code.equals("0")) {
                        showToastMsg("邮件" + result.getString("resDesc"));
                        startActivity(new Intent(BackPassWordForEmailActivity.this, LoginActivity.class));
                        BackPassWordForEmailActivity.this.finish();
                        myProgressDialog.dismiss();
                    } else {
                        showToastMsg("邮件" + result.getString("resDesc"));
                        myProgressDialog.dismiss();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    myProgressDialog.dismiss();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
