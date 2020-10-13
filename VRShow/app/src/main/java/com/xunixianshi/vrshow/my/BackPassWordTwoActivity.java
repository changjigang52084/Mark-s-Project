package com.xunixianshi.vrshow.my;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ViewlibConstant;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.util.AppContent;
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

public class BackPassWordTwoActivity extends BaseAct {

	private ImageView title_close_iv;

	private EditText reset_new_password_et;
	private EditText reset_new_password_again_et;
	private Button reset_new_password_ok_bt;

	private String newPassword;
	private String againPassword;
	private String back_uid;


	@Override
	public void setRootView() {
		setContentView(R.layout.activity_back_password_two);
	}

	@Override
	protected void initView() {
		super.initView();
		title_close_iv = (ImageView) findViewById(R.id.title_close_iv);
		reset_new_password_et = (EditText) findViewById(R.id.reset_new_password_et);
		reset_new_password_again_et = (EditText) findViewById(R.id.reset_new_password_again_et);
		reset_new_password_ok_bt = (Button) findViewById(R.id.reset_new_password_ok_bt);
		initListener();
	}

	private void initListener() {
		title_close_iv.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				BackPassWordTwoActivity.this.finish();
			}
		});

		reset_new_password_ok_bt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				newPassword = reset_new_password_et.getText().toString().trim();
				againPassword = reset_new_password_again_et.getText().toString().trim();
				if (newPassword.length() < 6|| newPassword.length() > 18) {
					showToastMsg(getResources().getString(
							R.string.hint_long_password));
					return;
				} else if (!newPassword.equals(againPassword)) {
					showToastMsg("两次输入的密码不一致");
					return;
				} else {
					if (validateInput()) {
						commitNewPassword();
					}
				}
			}
		});
	}

	@Override
	protected void initData() {
		super.initData();
		back_uid = getIntent().getExtras().getString("back_uid");
	}

	private boolean validateInput() {
		if (newPassword.equals("")) {
			showToastMsg(getResources().getString(R.string.hint_empty_password));
			return false;
		} else if (newPassword.length() < 6 || newPassword.length() > 20) {
			showToastMsg(getResources().getString(R.string.hint_long_password));
			return false;
		}
		return true;
	}

	private void commitNewPassword() {
		// 加密手机号
		String Ras_newPassword = VR_RsaUtils
				.encodeByPublicKey(newPassword);
		// 加密手机号
		String Ras_back_uid = VR_RsaUtils
				.encodeByPublicKey(back_uid);
		// 封装参数
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("modifyTxt", Ras_newPassword	);
		hashMap.put("modifyType", 1);
		hashMap.put("userId",Ras_back_uid);
		// 转参数
		String jsonData = StringUtil.MapToJson(hashMap);
		OkHttpAPI.postObjectData("/modify/user/info/id/service", jsonData, new HttpSuccess<JSONObject>() {
			@Override
			public void onSuccess(JSONObject result, int id) {
				try {
					String code = (String) result.get("resCode");
					String codeDesc = (String) result
							.get("resDesc");
					if (code.equals("0")) {
						AppContent.UID = "";
						SimpleSharedPreferences.putString("loginUid", "", BackPassWordTwoActivity.this);
						showToastMsg(codeDesc);
						skipActivity(BackPassWordTwoActivity.this,
								LoginActivity.class);
					} else {
						showToastMsg(codeDesc);
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
}
