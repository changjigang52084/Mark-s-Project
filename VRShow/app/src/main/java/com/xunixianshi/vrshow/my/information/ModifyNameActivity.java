package com.xunixianshi.vrshow.my.information;

import android.content.Intent;
import android.graphics.Color;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * TODO 修改用户名称页面
 *
 * @author MarkChang
 * @ClassName ModifyNameActivity
 * @time 2016/11/1 15:44
 */
public class ModifyNameActivity extends BaseAct {

    @Bind(R.id.my_information_modify_name_cancel_rl)
    RelativeLayout my_information_modify_name_cancel_rl;
    @Bind(R.id.my_information_modify_name_save_rl)
    RelativeLayout my_information_modify_name_save_rl;
    @Bind(R.id.my_information_modify_user_username_et)
    EditText my_information_modify_user_username_et;
    @Bind(R.id.my_information_modify_username_cancel_iv)
    ImageView my_information_modify_username_cancel_iv;
    @Bind(R.id.introduce_profile_enter_words_number_tv)
    TextView introduce_profile_enter_words_number_tv;


    private LoadingAnimationDialog mLoadingAnimationDialog;

    private boolean canEdit = true;
    private String username;
    private int inputMaxNum = 20;

    public static final String KEY_USER_NAME = "KEY_USER_NAME";

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_modify_name);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        canEdit = getIntent().getExtras().getBoolean("canEdit");
        username = getIntent().getExtras().getString("username");

        mLoadingAnimationDialog = new LoadingAnimationDialog(this);
        if (!StringUtil.isEmpty(username)) {
            introduce_profile_enter_words_number_tv.setText(inputMaxNum - username.length() + "");
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
        my_information_modify_user_username_et.setText(username);
        my_information_modify_user_username_et.setSelection(my_information_modify_user_username_et.getText().length());
        if (canEdit) {
            my_information_modify_user_username_et.setEnabled(true);
        } else {
            my_information_modify_user_username_et.setEnabled(false);
        }
    }

    @OnClick({R.id.my_information_modify_name_cancel_rl, R.id.my_information_modify_name_save_rl, R.id.my_information_modify_username_cancel_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_information_modify_name_cancel_rl:
                ModifyNameActivity.this.finish();
                break;
            case R.id.my_information_modify_name_save_rl:
                if (!canEdit) {
                    showToastMsg("该用户已经修改过用户名了");
                    return;
                } else {
                    uploadNameToServer();
                }
                break;
            case R.id.my_information_modify_username_cancel_iv:
                my_information_modify_user_username_et.setText("");
                break;
        }
    }

    // 把用户修改后的名字上传到服务器
    private void uploadNameToServer() {
        String ModifyName = my_information_modify_user_username_et.getText().toString().trim();
        if (ModifyName.equals("")) {
            showToastMsg("用户名不能为空");
            return;
        }
        username = ModifyName;
        mLoadingAnimationDialog.show();
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        String Ras_username = VR_RsaUtils.encodeByPublicKey(ModifyName);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userId", Ras_uid);
        hashMap.put("loginName", Ras_username);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/modify/name/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                if(isDestroy()){return;}
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {

                        canEdit = false;
                        Intent intent = new Intent();
                        intent.putExtra(KEY_USER_NAME, username);
                        intent.putExtra("canEdit",canEdit);
                        setResult(RESULT_OK, intent);
                        showToastMsg("保存成功");
                        ModifyNameActivity.this.finish();
                    } else {
                        showToastMsg(result.getString("resDesc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mLoadingAnimationDialog.dismiss();
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败！请检查网络");
                if(isDestroy()){return;}
                mLoadingAnimationDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
