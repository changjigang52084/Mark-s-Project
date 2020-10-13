package com.lzmr.bindtool.ui.fragment.bind;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.fastjson.JSON;
import com.baize.adpress.core.common.constant.SystemConstant;
import com.baize.adpress.core.protocol.dto.AdpressDeviceAuthorizationPackage;
import com.baize.adpress.core.protocol.dto.AuthorizationListItemPackage;
import com.baize.adpress.core.protocol.dto.AuthorizationListPackage;
import com.baize.adpress.core.utils.AesCoderUtils;
import com.lzkj.baize_android.utils.ListUtils;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.adapter.AuthSpinnerAdapter;
import com.lzmr.bindtool.adapter.AuthSpinnerAdapter.AuthViewHolder;
import com.lzmr.bindtool.adapter.CityAdapter;
import com.lzmr.bindtool.adapter.ProvinceAdapter;
import com.lzmr.bindtool.api.AuthListManager;
import com.lzmr.bindtool.api.BindDeviceManager;
import com.lzmr.bindtool.api.listener.SessionRequestListener;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.ui.fragment.BaseFragment;
import com.lzmr.bindtool.ui.fragment.MenuFragment;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;
import com.yqhd.baizelocationlib.CityDatabaseHandler;
import com.yqhd.baizelocationlib.entity.CityBo;
import com.yqhd.baizelocationlib.entity.ProvinceBo;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import static android.R.id.message;

/**   
*    
* 项目名称：BindTools   
* 类名称：BindFragment   
* 类描述：绑定界面   
* 创建人：lyhuang   
* 创建时间：2015-9-11 下午7:31:10    
* @version    
*    
*/
@SuppressLint("ValidFragment")
public class BindFragment extends BaseFragment implements OnClickListener, 
OnItemSelectedListener, CompoundButton.OnCheckedChangeListener{
	private static final LogTag TAG = LogUtils.getLogTag(BindFragment.class.getSimpleName(), true);
    
	/**
	 * mac地址，终端位置
	 */
	private TextView macAdressTxt,locationCityTxt;
	/**
	 * 省份spinner  
	 */
	private Spinner provinceSpinner;  
	/**
	 * 城市spinner 
	 */
    private Spinner citySpinner;   
      
	/**
	 * 终端名称输入框
	 */
	private EditText deviceNameEdit;
	/**
	 * 绑定按钮
	 */
	private Button bindBtn;
	/**
	 * 取消操作
	 */
	private Button cancelBindBtn;
	
	/**
	 * 横屏按钮
	 */
	private RadioButton horizontalScreen;
	/**
	 * 竖屏按钮
	 */
	private RadioButton verticalScreen;
	/**
	 * 授权列表
	 */
	private Spinner authListSpinner;
	/**
	 * 返回首页
	 */
	private RelativeLayout homePage;
	/**关联绑定social app**/
	private CheckBox mSocialCbox;
	/***
	 * 授权列表
	 */
	private AuthListManager authListManager;
	private AuthSpinnerAdapter authSpinnerAdapter;
	private CityAdapter cityAdapter;
	
	/**
	  * 省份集合
	  */
	private List<ProvinceBo> provinceSet;
	/**
	  * 城市集合
	  */
	private List<CityBo> citySet;
	
	private CityDatabaseHandler cityHandler;
	/**授权id**/
	private String authId;
	/**
	  * 横竖屏 1：横屏  2：竖屏
	  */
	private int screenType = 1;
	/**
	  * 城市选中下标
	  */
	private int citySelection = 0;
	/**
	  * 扫码结果城市缓存
	  */
	private CityBo cityCache = null;

	/**
	 * 缓存终端名，用于连续绑定
	 */
	private String deviceName = null;
	/**
	 * 绑定次数，用于连续绑定区分终端名
	 */
	private int numberOfBind = 0;



	
	private Handler mHandler = new Handler () {
		public void handleMessage(android.os.Message msg) {
			AuthorizationListPackage listPackage = JSON.parseObject((String)msg.obj, 
					AuthorizationListPackage.class);
			if(null == authListSpinner){
				LogUtils.e(LogUtils.getStackTraceElement(), "authListSpinner = null");
				return;
			}
			if (getActivity()==null) {
				LogUtils.i(LogUtils.getStackTraceElement(),"acitivty is null");
			}
			if (listPackage.getRows() == null) {
				LogUtils.i(LogUtils.getStackTraceElement(),"listPackage is null");
			}
			authSpinnerAdapter = new AuthSpinnerAdapter(getActivity(),listPackage.getRows());
			authListSpinner.setAdapter(authSpinnerAdapter);
			authListSpinner.setSelection(0, true);
			authSpinnerAdapter.notifyDataSetChanged();
		}
	};

	private AdpressDeviceAuthorizationPackage deviceAuthorizationPackage;
	public BindFragment(ControlFragmentListener switchFragmentListener) {
		super(switchFragmentListener);
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		authListManager = new AuthListManager(new SessionRequestListener() {

			@Override
			public void onSessionInvalid() {
				switchLoginAndCloseOther(true);
			}

			@Override
			public void onSuccess(String msg) {
				mHandler.obtainMessage(0, msg).sendToTarget();
			}
			
			@Override
			public void onFailure(String msg) {
			}
		});
		cityHandler = new CityDatabaseHandler(BindToolApp.getApplication());
		provinceSet = cityHandler.getProSet();
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		LogUtils.d(TAG, "onCreateView", this.getClass().getSimpleName()+" onCreateView");
		Bundle bundle = getArguments();
		if(null == bundle){
			toastMessage(StringUtils.getString(getActivity(), R.string.scanning_error));
			finish();
		}
		String scanningResult = bundle.getString("scanningResult");
		deviceAuthorizationPackage = parseQrcodeResult(scanningResult);
		if(null == deviceAuthorizationPackage){
			finish();
			return null;
		}
		//连续绑定重复使用终端名
		deviceName = bundle.getString("deviceName");
		numberOfBind = bundle.getInt("numberOfBind",0);

		LogUtils.d(TAG, "onCreate", scanningResult);
		if(view == null){
			view = inflater.inflate(R.layout.fragment_bind, container, false);
			initView();



		}
		return view;
	}
	
	/**
	 * 解析二维码内容
	 */
	private AdpressDeviceAuthorizationPackage parseQrcodeResult(String result) {
		if(StringUtils.isEmpty(result)){
			toastMessage(getActivity().getString(R.string.scanning_error));
			return null;
		}
		//Aes解密
		String decryptQRCodeStr = null;
		try {
			decryptQRCodeStr = AesCoderUtils.aesDecrypt(result, Constants.AES_ENCRYP_KEY);
		} catch (Exception e) {
			e.printStackTrace();
		}
		if(StringUtils.isEmpty(decryptQRCodeStr)){
			toastMessage(getActivity().getString(R.string.scanning_result_decrypt_failed));
			return null;
		}
		LogUtils.d(TAG, "parseQrcodeResult", "decryptQRCodeStr："+decryptQRCodeStr);
		String [] data = decryptQRCodeStr.split("\\|");
		if(data.length != 4){
			LogUtils.e(TAG, "parseQrcodeResult", "error scanning result format");
			toastMessage(getActivity().getString(R.string.scanning_qrcode_error));
			return null;
		}
		AdpressDeviceAuthorizationPackage deviceAuthorizationPackage = new AdpressDeviceAuthorizationPackage();
		String macAddress = data[0];
		String loacationCity = data[1];
		String getuiClientId = data[2];
		String jpushClientId = data[3];
		deviceAuthorizationPackage.setUserKey(ConfigSettings.getUserKey());
		deviceAuthorizationPackage.setMac(macAddress);
		deviceAuthorizationPackage.setGpush(getuiClientId);
		deviceAuthorizationPackage.setJpush(jpushClientId);
		cityCache = cityHandler.findCityByCityName(loacationCity);
		if(null != cityCache){
			LogUtils.e(LogUtils.getStackTraceElement(), cityCache.toString());
			deviceAuthorizationPackage.setLocation(cityCache.getCityId());
		}
		return deviceAuthorizationPackage;
	}
	
	private void setBindClickable(boolean clickable){
		bindBtn.setClickable(clickable);
		if(clickable){
			bindBtn.setBackgroundResource(R.drawable.btn_submit_bind_selector);
		}else{
			bindBtn.setBackgroundResource(R.drawable.bg_cancel_bind_defult);
		}
		
	}
	
	/** 
	* @Title: initView 
	* @Description: 初始化view
	*/ 
	private void initView(){
		if(deviceAuthorizationPackage == null){
			LogUtils.w(LogUtils.getStackTraceElement(), "deviceAuthorizationPackage is null.");
			return;
		}
		//mac
		macAdressTxt = (TextView) view.findViewById(R.id.txt_device_mac_value);
		macAdressTxt.setText(deviceAuthorizationPackage.getMac());
		//位置
//		locationCityTxt = (TextView) view.findViewById(R.id.txt_device_position_value);
//		locationCityTxt.setText(deviceAuthorizationPackage.getPosition());
		provinceSpinner = (Spinner) view.findViewById(R.id.provinces);
		provinceSpinner.setOnItemSelectedListener(new SelectProvinceListener());
		citySpinner = (Spinner) view.findViewById(R.id.city);
		citySpinner.setOnItemSelectedListener(new SelectCityListener());
		provinceSpinner.setAdapter(new ProvinceAdapter(getActivity(), provinceSet));
		setLocationView(cityCache);
		
		//确认绑定
		bindBtn = (Button) view.findViewById(R.id.btn_bind);
		bindBtn.setOnClickListener(this);
		setBindClickable(false);

		//输入终端名
		deviceNameEdit = (EditText) view.findViewById(R.id.edit_device_name);
		deviceNameEdit.addTextChangedListener(device_textWatcher);
		if (!StringUtils.isEmpty(deviceName)) {
			LogUtils.d(LogUtils.getStackTraceElement(),"repeat,deviceName:"+deviceName +",count："+numberOfBind);
			deviceNameEdit.setText(deviceName + "(" + (++numberOfBind) + ")");
		}


		//取消操作
		cancelBindBtn = (Button) view.findViewById(R.id.btn_cancel_bind);
		cancelBindBtn.setOnClickListener(this);
		
		homePage = (RelativeLayout) view.findViewById(R.id.rl_home_page);
		homePage.setOnClickListener(this);
		
		//选择关联绑定social的checkbox
		mSocialCbox = (CheckBox) view.findViewById(R.id.cbox_together_social);
		mSocialCbox.setTag(SystemConstant.SYSTEM_TYPE_DSPLUG_SOCIAL);
		mSocialCbox.setOnCheckedChangeListener(this);
		
		//横竖屏
		horizontalScreen = (RadioButton) view.findViewById(R.id.btn_horizontal_screen);
		verticalScreen = (RadioButton) view.findViewById(R.id.btn_vertical_screen);
		RadioGroup radiogroup = (RadioGroup) view.findViewById(R.id.radiogroup_device_screen);
		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(checkedId == horizontalScreen.getId()){
					LogUtils.d(TAG, "onCheckedChanged", "horizontalScreen");
					screenType = 1;
				}else if(checkedId == verticalScreen.getId()){
					LogUtils.d(TAG, "onCheckedChanged", "verticalScreen");
					screenType = 2;
				}
			}
		});
		
		//授权列表
		authListSpinner = (Spinner) view.findViewById(R.id.spn_auth_list);
		authListSpinner.setOnItemSelectedListener(this);
		
		if ((ConfigSettings.CLIENT_TYPE == Constants.YW) || ConfigSettings.getBindMode()) {
			mSocialCbox.setVisibility(View.GONE);
		}
	}
	

	@Override
	public void onStart() {
		super.onStart();
		authListManager.requestAuthList();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
	}
	
	/** 监听输入设备名的textWatcher */
	private TextWatcher device_textWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			if (s.length() > 0) {
				setBindClickable(true);
			} else {
				setBindClickable(false);
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

	/**
	 * 执行绑定
	 * @param 
	 * @return
	 */
	private void executeBind(){
		List<Integer> bindSystemType = new ArrayList<Integer>();
		bindSystemType.add(ConfigSettings.getSystemType());
		if(mSocialCbox.isChecked()){
			bindSystemType.add(SystemConstant.SYSTEM_TYPE_DSPLUG_SOCIAL);
		}
		deviceAuthorizationPackage.setName(deviceNameEdit.getText().toString());
		deviceAuthorizationPackage.setDirection(screenType);
		deviceAuthorizationPackage.setAuthorizationId(authId);
		deviceAuthorizationPackage.setSystemTypes(bindSystemType);
		startProgressDialog(getString(R.string.binding), false);
		BindDeviceManager bindDeviceManager = new BindDeviceManager(new SessionRequestListener() {

			@Override
			public void onSessionInvalid() {

			}

			@Override
			public void onSuccess(String msg) {
				dismissProgressDialog();

				showContinueBind(msg);
//				showDialog(msg,new DialogInterface.OnClickListener()
//				{
//					@Override
//					public void onClick(DialogInterface dialog, int which)
//					{
//						dialog.dismiss();
//						finish();
//					}
//				});
			}
			@Override
			public void onFailure(String msg) {
				dismissProgressDialog();
				showDialog(msg,null);
			}
		});
		
		bindDeviceManager.bind(deviceAuthorizationPackage);
	}

	/**
	 * 显示继续绑定对话框
	 */
	private void showContinueBind(String msg) {
		AlertDialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		builder.setMessage(msg);
		builder.setPositiveButton(getString(R.string.continue_bind),new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				LogUtils.d(LogUtils.getStackTraceElement(),"继续绑定");
				dialog.dismiss();
				Bundle bundle = new Bundle();
				bundle.putString("deviceName",StringUtils.isEmpty(deviceName) ?
						deviceNameEdit.getText().toString() : deviceName);
				bundle.putInt("numberOfBind",numberOfBind);
				switchScanning(bundle,true);

			}
		});
		builder.setNegativeButton(getActivity().getString(R.string.end_bind),new DialogInterface.OnClickListener()
		{
			@Override
			public void onClick(DialogInterface dialog, int which)
			{
				LogUtils.d(LogUtils.getStackTraceElement(),"结束绑定");
				dialog.dismiss();
				popBackStackToFragment(ScanningFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);

			}
		});
		dialog = builder.create();
		dialog.show();
	}

	/**
	  * @Title: setLocationView
	  * @Description: TODO 设置定位值，若二维码内包含定位，则显示二维码定位值
	  * @param  cityBo  城市定位id
	  */
	private void setLocationView(CityBo cityBo) {
		if(null != cityBo){
			ProvinceBo province = cityHandler.findProvinceByCityId(cityBo.getCityId());
			if(null != province){
				setProvinceSelection(province.getProName());
				setCtiySelection(cityBo.getCityId());
			}
		}
	}
	
	private void setProvinceSelection(String provinceName){
		if(StringUtils.isEmpty(provinceName)){
			LogUtils.w(LogUtils.getStackTraceElement(), "Province name is null.");
			return;
		}
		if(ListUtils.isEmpty(provinceSet)){
			LogUtils.w(LogUtils.getStackTraceElement(), "ProvinceSet is empty.");
			return;
		}
		if(null != provinceSpinner){
			for(int i=0;i<provinceSet.size();i++){
				ProvinceBo provinceBo = provinceSet.get(i);
				if(null != provinceBo){
					if(provinceName.equals(provinceBo.getProName())){
						provinceSpinner.setSelection(i);
						updateCityAdapter(provinceBo.getProId());
						break;
					}
				}
			}
		}
	}
	
	/**
	  * @Title: setCtiySelection
	  * @Description: TODO 设置城市选中项
	  * @param  cityId    城市id
	  */
	private void setCtiySelection(Integer cityId){
		if(null == cityId){
			LogUtils.w(LogUtils.getStackTraceElement(), "City id is null.");
			return;
		}
		if(ListUtils.isEmpty(citySet)){
			LogUtils.w(LogUtils.getStackTraceElement(), "CitySet is empty.");
			return;
		}
		if(null != citySpinner){
			for(int i=0;i<citySet.size();i++){
				CityBo city = citySet.get(i);
				if(null != city){
					if(cityId.equals(city.getCityId())){
						citySelection = i;
						citySpinner.setSelection(citySelection);
						LogUtils.d(LogUtils.getStackTraceElement(), "setSelection:"+i);
						break;
					}
				}
			}
		}
	}
	

	private void updateCityAdapter(Integer proviceId){
		if(null == citySpinner){
			LogUtils.w(LogUtils.getStackTraceElement(), "CitySpinner is null");
			return;
		}
		if(null == proviceId){
			LogUtils.w(LogUtils.getStackTraceElement(), "Provice Id is null");
			return;
		}
		citySet = cityHandler.findCitysByProvinceId(proviceId);
		if(null == cityAdapter){
			cityAdapter = new CityAdapter(getActivity(), citySet);
			citySpinner.setAdapter(cityAdapter);
		}else{
			cityAdapter.updateData(citySet);
		}
		citySpinner.setSelection(citySelection);
	}
	
	/**
	  * @ClassName: SelectProvinceListener
	  * @Description: TODO 省份选择监听器
	  * @author longyihuang
	  * @date 2016年9月24日 上午11:16:19
	  *
	  */
	class SelectProvinceListener implements OnItemSelectedListener{  
        public void onItemSelected(AdapterView<?> parent, View view,  
                int position, long id) { 
        	ProvinceBo province = (ProvinceBo) parent.getItemAtPosition(position);
        	if(null != province){
        		//获得省份ID  
        		Integer proId = province.getProId();
        		LogUtils.d(LogUtils.getStackTraceElement(), "select province:"+province.getProName());
        		updateCityAdapter(proId);
        	}
        }  
        public void onNothingSelected(AdapterView<?> arg0) {  
            // TODO Auto-generated method stub  
        }  
    }  
	 
    /**
      * @ClassName: SelectCityListener
      * @Description: TODO 城市选择监听器
      * @author longyihuang
      * @date 2016年9月24日 上午11:12:22
      */
    class SelectCityListener implements OnItemSelectedListener{  
        public void onItemSelected(AdapterView<?> parent, View view,  
                int position, long id) {  
            CityBo city = (CityBo) parent.getItemAtPosition(position);  
            LogUtils.d(LogUtils.getStackTraceElement(), "select city:"+city.getCityName()+"|"+position);
            if(null != deviceAuthorizationPackage){
            	deviceAuthorizationPackage.setLocation(city.getCityId());
            }
        }  
        
        public void onNothingSelected(AdapterView<?> arg0) {  
        }  
    }  

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_bind: //绑定
			LogUtils.d(TAG, "onClick","Bind device.");
			executeBind();
			break;
		case R.id.rl_home_page:
			popBackStackToFragment(MenuFragment.class.getSimpleName(),0);
			break;
		case R.id.btn_cancel_bind://取消
			finish();
			break;
		default:
			break;
		}
	}

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		AuthViewHolder authViewHolder = (AuthViewHolder)view.getTag();
		AuthorizationListItemPackage itemPackage = (AuthorizationListItemPackage) authViewHolder.authNameTv
				.getTag();
		authId = itemPackage.getId();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		LogUtils.d(TAG, "onCheckedChanged", "mSocialCbox.isChecked:" + mSocialCbox.isChecked());
	}

}
