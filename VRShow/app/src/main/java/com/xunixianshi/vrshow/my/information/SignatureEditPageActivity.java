package com.xunixianshi.vrshow.my.information;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.util.StringUtil;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * TODO 用户签名编辑页面
 *
 * @author MarkChang
 * @ClassName SignatureEditPageActivity
 * @time 2016/11/1 15:45
 */
public class SignatureEditPageActivity extends BaseAct {

    @Bind(R.id.my_information_modify_user_username_et)
    EditText my_information_modify_user_username_et;
    @Bind(R.id.introduce_profile_enter_words_number_tv)
    TextView introduce_profile_enter_words_number_tv;
    private String signatureProfile;
    private String modifyUserPersonalProfile;
    private int inputMaxNum = 50;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_signature_edit_page);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        Intent data = getIntent();
        if (data != null) {
            signatureProfile = data.getStringExtra("userPersonalProfile");
            if (signatureProfile != null) {
                my_information_modify_user_username_et.setText(signatureProfile);
                Spannable spanText = my_information_modify_user_username_et.getText();
                Selection.setSelection(spanText, my_information_modify_user_username_et.getText().length());
            }
        }
        if (!StringUtil.isEmpty(signatureProfile)) {
            introduce_profile_enter_words_number_tv.setText(inputMaxNum - signatureProfile.length() + "");
        }
        my_information_modify_user_username_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = inputMaxNum - s.length();
                introduce_profile_enter_words_number_tv.setText("" + number);
            }
        });
    }

    @OnClick({R.id.my_information_modify_name_cancel_rl, R.id.my_information_modify_name_save_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_information_modify_name_cancel_rl:
                InputMethodManager imm1 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm1.hideSoftInputFromWindow(my_information_modify_user_username_et.getWindowToken(), 0);
                SignatureEditPageActivity.this.finish();
                break;
            case R.id.my_information_modify_name_save_rl:
//                modifyUserSexInformation();
                Intent intent = new Intent();
                intent.putExtra("signatureProfile", my_information_modify_user_username_et.getText().toString());
                setResult(RESULT_OK, intent);
                InputMethodManager imm2 = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm2.hideSoftInputFromWindow(my_information_modify_user_username_et.getWindowToken(), 0);
                SignatureEditPageActivity.this.finish();
                break;
        }
    }

    /**
     * 修改用户签名信息
     */
//    private void modifyUserSexInformation() {
//        if (AppContent.UID.equals("")) {
//            return;
//        }
//        try {
//            modifyUserPersonalProfile = my_information_modify_user_username_et.getText().toString();
//            String utf_userPersonalProfile = URLEncoder.encode(modifyUserPersonalProfile, "utf-8");
//            String rsa_userId = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
//            // 封装参数
//            HashMap<String, Object> hashMap = new HashMap<String, Object>();
//            hashMap.put("userId", rsa_userId);
//            hashMap.put("userSex", -1);
//            hashMap.put("userDateBirth", -1);
//            hashMap.put("userResidenceAreaCode", -1);
//            hashMap.put("userResidenceAreaDetail", -1);
//            hashMap.put("userPersonalProfile", utf_userPersonalProfile);
//            // 转参数
//            String jsonData = StringUtil.MapToJson(hashMap);
//            // 获取网络数据
//            OkHttpAPI.postObjectData("/user/info/edit/service", jsonData, new HttpSuccess<JSONObject>() {
//                @Override
//                public void onSuccess(JSONObject result, int id) {
//                    try {
//                        if (result.getString("resCode").equals("0")) {
//                            showToastMsg(result.getString("resDesc"));
//                        }
//                    } catch (JSONException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }, new HttpError() {
//                @Override
//                public void onError(Call call, Exception e, int id) {
//                    showToastMsg("网络连接失败，请检查网络！");
//                }
//            });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}