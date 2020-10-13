package com.lzmr.bindtool.ui.fragment;

import com.baize.adpress.core.utils.AesCoderUtils;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.api.LoginRequestManager;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.http.HttpUtil;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.impl.LogoutListener;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;
import com.lzmr.bindtool.util.ShareUtil;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import static android.R.attr.mode;

/**
 *登录界面
 *
 * @author longyihuang
 * @date 2016-2-24 上午11:11:59
 */
@SuppressLint("ValidFragment")
public class LoginFragment extends BaseFragment implements OnClickListener, SessionRequestListener,LogoutListener{
	private static final LogUtils.LogTag TAG = LogUtils.getLogTag(LoginFragment.class.getSimpleName(), true);

	private RelativeLayout bgLayout;
	/**
	 * 模式开关
	 */
//	private Switch modeSwitch;
	private RadioGroup modeGroup;
	private RadioButton testModeBtn,garyModeBtn,onlineModeBtn,blendModeBtn;
	/**
	 * 绑定选项开关
	 */
	private Switch bindSwitch;
	
	private TextView bindTxt;
	private TextView testTxt;
	

	/** 登录按钮,取消按钮 */
	private Button login_btn;
	/** 密码,设备名,用户名输入框 */
	private EditText password_et, userId_et;
	/** 设备名,用户名,密码 */
	private String  userName, password;
	/** 显示圆形进度条 */
	private ProgressBar progressBar;
	/** 显示请求的结果 */
	private TextView resultTv;
	private CheckBox rememberAccountCb;
	private ImageButton clearUserEt_btn,clearPasswordEt_btn;
	private ImageView bgTxtImg;

	public LoginFragment(ControlFragmentListener switchFragmentListener) {
		super(switchFragmentListener);
		
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils.d(TAG, "onCreateView", getClass().getSimpleName()+" onCreateView:"+this.toString());
		Bundle bundle = getArguments();
		if (null != bundle) {
			boolean sessionInvalid = bundle.getBoolean("sessionInvalid");
			LogUtils.d(LogUtils.getStackTraceElement(), "sessionInvalid:"+sessionInvalid);
			if (sessionInvalid) {
				BindToolApp.getApplication().showDialog(getActivity(),getString(R.string.session_invalid),null);
			}
		}

		if(view == null){
			view = inflater.inflate(R.layout.fragment_login, container, false);
			initView(view);
		}
		setRememberAccount();
		return view;
	}
	
	@Override
	public void onDestroyView() {
		LogUtils.d(TAG, "onDestroyView", getClass().getSimpleName()+" onDestroyView");
		super.onDestroyView();
		
	}

	private void initYWSystemInfo(){
		if(ConfigSettings.CLIENT_TYPE == Constants.YW){
			ConfigSettings.setTestMode(Constants.MODE_ONLINE);
			ConfigSettings.setBindMode(false);
		}
	}
	
	private void initView(View view){
//		testTxt = (TextView) view.findViewById(R.id.switch_test_txt);
		bgLayout = (RelativeLayout) view.findViewById(R.id.rl_login_layout);
		bgTxtImg = (ImageView) view.findViewById(R.id.wecome_bg_img);

		modeGroup = (RadioGroup) view.findViewById(R.id.radio_group_mode);

		bindTxt = (TextView) view.findViewById(R.id.switch_bind_txt);
		bindSwitch = (Switch) view.findViewById(R.id.switch_bind);

		password_et = (EditText) view.findViewById(R.id.passwd_et);
		password_et.setText("");
		password_et.addTextChangedListener(passwd_textWatcher);


		userId_et = (EditText) view.findViewById(R.id.user_et);
		userId_et.setText("");
		userId_et.addTextChangedListener(userId_textWatcher);

		clearUserEt_btn = (ImageButton) view.findViewById(R.id.clear_user_et);
		clearUserEt_btn.setOnClickListener(this);

		clearPasswordEt_btn = (ImageButton) view.findViewById(R.id.clear_passwd_et);
		clearPasswordEt_btn.setOnClickListener(this);

		login_btn = (Button) view.findViewById(R.id.login_btn);
		login_btn.setOnClickListener(this);
		login_btn.setEnabled(true);

		resultTv = (TextView) view.findViewById(R.id.result_tv);

		rememberAccountCb = (CheckBox) view.findViewById(R.id.checkbox_remember_account);
		rememberAccountCb.setOnCheckedChangeListener(listener);

		if(ConfigSettings.CLIENT_TYPE == Constants.YW){
			bgTxtImg.setImageResource(R.drawable.welcome_bg_icon_yw);
			bgLayout.setBackgroundResource(R.drawable.login_bg_yw);
		}else{
			initModeGroup();
			initSystemSwitch();
		}

		initEditTextDraw();
		
		if (!ConfigSettings.IS_SHOW_SELET_DEBUG) {
			modeGroup.setVisibility(View.GONE);
			bindTxt.setVisibility(View.GONE);
			bindSwitch.setVisibility(View.GONE);
		}
		
	}

	/**
	 * 初始化系统选项开关
     */
	private void initSystemSwitch() {
		if (null!=bindSwitch && null!=bindTxt) {
			boolean isBindToSocial = ConfigSettings.getBindMode();
			if(isBindToSocial){
				bindTxt.setText(getString(R.string.bind_to_social));
			}else{
				bindTxt.setText(getString(R.string.bind_to_dsplug));
			}

			bindSwitch.setChecked(isBindToSocial);
			bindSwitch.setOnCheckedChangeListener(new  CompoundButton.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) {
						bindTxt.setText(getString(R.string.bind_to_social));
					} else {
						bindTxt.setText(getString(R.string.bind_to_dsplug));
					}
					ConfigSettings.setBindMode(isChecked);
					LogUtils.d(TAG, "onCheckedChanged", "bing to social:"
							+ HttpUtil.getBindServer());
				}
			});
		}
	}

	/**
	 * 初始化环境按钮组
	 */
	private void initModeGroup(){
		if (null!=modeGroup) {
			int mode = ConfigSettings.getTestMode();
			LogUtils.e(LogUtils.getStackTraceElement(),"loginMode:"+mode);
			switch (mode){
				case Constants.MODE_OFFLINE:
					modeGroup.check(R.id.btn_test_mode);
					break;
				case Constants.MODE_GARY:
					modeGroup.check(R.id.btn_gary_mode);
					break;
				case Constants.MODE_ONLINE:
					modeGroup.check(R.id.btn_online_mode);
					break;
				default:
					break;
			}
			modeGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
				@Override
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					switch (checkedId){
						case R.id.btn_test_mode:
							ConfigSettings.setTestMode(Constants.MODE_OFFLINE);
							break;
						case R.id.btn_gary_mode:
							ConfigSettings.setTestMode(Constants.MODE_GARY);
							break;
						case R.id.btn_online_mode:
							ConfigSettings.setTestMode(Constants.MODE_ONLINE);
							break;
						default:
							break;
					}
					LogUtils.d(TAG, "onCheckedChanged", "http mode:"+HttpUtil.getBindServer());
				}
			});
		}
	}
	
	/**
	 * 初始化editText　draw
	 */
	private void initEditTextDraw() {
		int width = 40;
		int height = 40;
        Drawable drawable1 = getResources().getDrawable(R.drawable.user_input_icon);
        drawable1.setBounds(0, 0, height, width);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        userId_et.setCompoundDrawables(drawable1, null, null, null);//只放左边
        Drawable passwordDraw = getResources().getDrawable(R.drawable.password_input_icon);
        passwordDraw.setBounds(0, 0, height, width);//第一0是距左边距离，第二0是距上边距离，40分别是长宽
        password_et.setCompoundDrawables(passwordDraw, null, null, null);//只放左边
	}
	
	CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {  
        @Override  
        public void onCheckedChanged(CompoundButton buttonView,  
                boolean isChecked) {  
        	LogUtils.d(TAG, "onCheckedChanged", "isChecked:"+isChecked);
            ShareUtil.newInstance().setBoolean(ShareUtil.REMEMBER_ACCOUNT, isChecked);
        }  
    };  
	
	/** 监听输入密码的textWatcher */
	private TextWatcher passwd_textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() > 0) {
				clearPasswordEt_btn.setVisibility(View.VISIBLE);
			} else {
				clearPasswordEt_btn.setVisibility(View.GONE);
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {}
		@Override
		public void afterTextChanged(Editable s) {}
	};
	/** 监听输入用户名的textWatcher */
	private TextWatcher userId_textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() > 0) {
				clearUserEt_btn.setVisibility(View.VISIBLE);
			} else {
				clearUserEt_btn.setVisibility(View.GONE);
			}
		}
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}
		@Override
		public void afterTextChanged(Editable s) {
		}
	};
	
	/** 清除用户名 */
	private void clearUserEt() {
		userId_et.setText("");
		LogUtils.e(TAG, "setRememberAccount", "userET:"+userId_et.toString());
	}

	/** 清除密码 */
	private void clearPasswordEt() {
		password_et.setText("");
	}
	
	/**
	 * 登录
	 */
	private void onLogin() {
		password = password_et.getText().toString();
		userName = userId_et.getText().toString();
		if (!checkEmpty(password, userName)) {
				resultTv.setText(R.string.logining);
				resultTv.setVisibility(View.VISIBLE);
				LoginRequestManager loginRequestManager = new LoginRequestManager(userName,password,this);
				loginRequestManager.getPublicKeyForLogin();
//				LoginRequestManager.get().getPublicKeyForLogin(userName,password,this);
				login_btn.setEnabled(false);
		}
	}
	
	/**
	 * 判断用户提交的信息是否有空值,返回true表示空，false表示不是空
	 * 
	 * @param password
	 * @param userName
	 * @return
	 */
	private boolean checkEmpty(String password,String userName) {
		if (null == userName || "".equals(userName)) {
			Toast.makeText(getActivity(), R.string.user_name_is_null,
					Toast.LENGTH_SHORT).show();
			return true;
		} else if (null == password || "".equals(password)) {// 用户名称不能为空
			Toast.makeText(getActivity(), R.string.passwd_is_null,
					Toast.LENGTH_SHORT).show();
			return true;
		}
		return false;
	}
	


	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.login_btn:
			onLogin();
			break;
		case R.id.clear_user_et:
			clearUserEt();
			break;
		case R.id.clear_passwd_et:
			clearPasswordEt();
			break;
		default:
			break;
		}
	}
	
	/** 
	* @Title: rememberAccount 
	* @Description: 记住账号
	* @return void    返回类型
	*/ 
	private void rememberAccount(){
		LogUtils.d(TAG, "rememberAccount", "Login success,remember account.");
		if(rememberAccountCb.isChecked()){
			try {
				ShareUtil.newInstance().setString(ShareUtil.USER_PASSWORD, AesCoderUtils.aesEncrypt(password, Constants.AES_ENCRYP_KEY));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}else{
			ShareUtil.newInstance().removeKey(ShareUtil.USER_PASSWORD);
		}
		ShareUtil.newInstance().setString(ShareUtil.USER_ACCOUNT, userName);
	}
	
	/** 
	* @Title: setRememberAccount 
	* @Description: 设置已被记录的账号信息
	*/ 
	private void setRememberAccount(){
		if(ShareUtil.newInstance().getBoolean(ShareUtil.REMEMBER_ACCOUNT)){
			rememberAccountCb.setChecked(true);
			String userNameRemember = ShareUtil.newInstance().getString(ShareUtil.USER_ACCOUNT);
			String passwordRemember = ShareUtil.newInstance().getString(ShareUtil.USER_PASSWORD);
			if(!StringUtils.isEmpty(userNameRemember) && !StringUtils.isEmpty(passwordRemember)) {
				try {
					userName = userNameRemember;
					password = AesCoderUtils.aesDecrypt(passwordRemember, Constants.AES_ENCRYP_KEY);
					userId_et.setText(userNameRemember);
					password_et.setText(this.password);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}else{
			rememberAccountCb.setChecked(false);
		}
	}



	@Override
	public void onSuccess(final String msg) {
		login_btn.setEnabled(true);
		rememberAccount();
		resultTv.post(new Runnable() {
			@Override
			public void run() {
				resultTv.setText(msg);
			}
		});
		resultTv.postDelayed(new Runnable() {

			@Override
			public void run() {
				switchMenu(null,true);
			}
		}, 1000);
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		fragmentTransaction.remove(this).commit();


	}


	@Override
	public void onFailure(final String msg) {
		login_btn.setEnabled(true);
		resultTv.post(new Runnable() {
			@Override
			public void run() {
				resultTv.setText(msg);
			}
		});
		
	}

	@Override
	public void onSessionInvalid() {
		login_btn.setEnabled(true);
		resultTv.post(new Runnable() {
			@Override
			public void run() {
				resultTv.setText(getString(R.string.session_invalid));
			}
		});
	}

	@Override
	public void onLogout() {
//		LogUtils.e(TAG, "onLogout", "onLogout");
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				clearPasswordEt();
//				clearUserEt();
//				rememberAccountCb.setChecked(false);
//			}
//		}, 300);
	}


}
