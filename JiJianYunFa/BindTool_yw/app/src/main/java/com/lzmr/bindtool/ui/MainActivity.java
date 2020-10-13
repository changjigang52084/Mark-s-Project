package com.lzmr.bindtool.ui;

import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.ui.fragment.LoginFragment;
import com.lzmr.bindtool.ui.fragment.MenuFragment;
import com.lzmr.bindtool.ui.fragment.bind.BindFragment;
import com.lzmr.bindtool.ui.fragment.bind.ScanningFragment;
import com.lzmr.bindtool.ui.fragment.devices.DeviceSettingFragment;
import com.lzmr.bindtool.ui.fragment.devices.DevicesManagerFragment;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;
import com.lzmr.bindtool.util.LogoutUtil.LogoutEvent;
import com.yqhd.baizelocationlib.CityDatabaseHandler;
import com.yqhd.baizelocationlib.entity.ProvinceBo;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.widget.Toast;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


/**   
*    
* 项目名称：MerchantMall   
* 类名称：MainActivity   
* 类描述：   程序的主界面
* 创建人：lyhuang   
* 创建时间：2015-8-27 下午7:15:54    
* @version    
*    
*/ 
@SuppressLint("NewApi")
public class MainActivity extends FragmentActivity  implements ControlFragmentListener{
	private static final LogTag TAG = LogUtils.getLogTag(MainActivity.class.getSimpleName(), true);
	
	
	private FragmentManager fragmentManager;
	
	/** 
	* @Fields menuFragment :  TODO 菜单界面
	*/ 
	private MenuFragment menuFragment;
	
	/** 
	* @Fields loginFragment :  TODO 登录界面
	*/ 
	private LoginFragment loginFragment;
	
	/** 
	* @Fields bindFragment :  TODO 绑定界面
	*/ 
	private BindFragment bindFragment;
	
	/** 
	* @Fields deviceListFragment :  TODO 终端管理界面
	*/ 
	private DevicesManagerFragment devicesManagerFragment;
	
	/** 
	* @Fields scanningFragment :  TODO 扫码界面
	*/ 
	private ScanningFragment scanningFragment;

	/**
	 * @Fields deviceSettingFragment :  TODO 终端设置界面
	 */
	private DeviceSettingFragment deviceSettingFragment;
	
	private Handler mHandler = null;

	/**
	 * @Fields isExit :  TODO 是否退出应用
	 */
	private boolean isExit = false;

	private AtomicBoolean isjump = new AtomicBoolean(true);
	
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new Handler();
		EventBus.getDefault().register(this);
		initSystemEnvironment();
		initView();

		CityDatabaseHandler cityHandler = new CityDatabaseHandler(BindToolApp.getApplication());
		List<ProvinceBo> proSet = cityHandler.getProSet();
		LogUtils.d(LogUtils.getStackTraceElement(),"proSet:"+proSet.size());
		
	}



	@Override
	protected void onDestroy() {
		LogUtils.e(TAG, "onDestroy", "Activity onDestroy");
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	private void initSystemEnvironment() {
		if(ConfigSettings.CLIENT_TYPE == Constants.YW){
			ConfigSettings.setTestMode(Constants.MODE_ONLINE);
			ConfigSettings.setBindMode(false);
			ConfigSettings.IS_SHOW_SELET_DEBUG = false;
		}
	}
	
	/**
	 * 初始化view
	 */
	private void initView() {
		fragmentManager = getSupportFragmentManager();
		if(ConfigSettings.checkLogin()){
			switchToMenuFragment(null,true);
		}else{
			switchToLoginFragment(null,false);
		}
	}
	
	
	
	/** 
	* @Title: switchToMenuFragment 
	* @Description: TODO(切换到菜单界面) 
	* @Param     设定文件 
	* @return void    返回类型 
	*/ 
	private void switchToMenuFragment(Bundle bundle,boolean addToBackStack){
		menuFragment = new MenuFragment(this);
		if(null != bundle){
			menuFragment.setArguments(bundle);
		}
		switchFragment(menuFragment, addToBackStack);
	}

	/**
	 * 切换到登录界面
	 * @param
	 * @return
	 */
	private void switchToLoginFragment(Bundle bundle,boolean addToBackStack){
		loginFragment = new LoginFragment(this);
		if(null != bundle){
			loginFragment.setArguments(bundle);
		}
		switchFragment(loginFragment, addToBackStack);
	}
	
	/** 
	* @Title: switchToScanningFragment 
	* @Description: 切换到扫码界面
	* @return void    返回类型
	*/ 
	private void switchToScanningFragment(Bundle bundle,boolean addToBackStack){
		scanningFragment = new ScanningFragment(this);
		if(null != bundle){
			scanningFragment.setArguments(bundle);
		}
		switchFragment(scanningFragment, addToBackStack);
	}
	
	/** 
	* @Title: switchToScannFragment 
	* @Description: TODO(切换到绑定界面) 
	* @Param     设定文件 
	* @return void    返回类型 
	*/ 
	private void switchToBindFragment(Bundle bundle,boolean addToBackStack){
		bindFragment = new BindFragment(this);
		if(null != bundle){
			bindFragment.setArguments(bundle);
		}
		switchFragment(bindFragment, addToBackStack);
	}
	

	/** 
	* @Title: switchToDeviceListFragment 
	* @Description: 切换到终端管理界面
	* @Param     设定文件 
	* @return void    返回类型 
	*/ 
	private void switchToDevicesManagerFragment(Bundle bundle,boolean addToBackStack){
		devicesManagerFragment = new DevicesManagerFragment(this);
		if(null != bundle){
			devicesManagerFragment.setArguments(bundle);
		}
		switchFragment(devicesManagerFragment, addToBackStack);
	}


	private void switchToDeviceSettingFragment(Bundle bundle,boolean addToBackStack){
		deviceSettingFragment = new DeviceSettingFragment(this);
		if(null != bundle){
			deviceSettingFragment.setArguments(bundle);
		}
		switchFragment(deviceSettingFragment, addToBackStack);
	}

	
	
	private void switchFragment(Fragment fragment, boolean isBackStack) {
		LogUtils.d(TAG, "switchFragment", " fragment name is :" + fragment.getClass().getSimpleName()+",addBackStack:"+isBackStack);
		try{
			FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
			if (fragment!=null) {
				if (checkFragmentActivity(fragment.getClass().getSimpleName())) {
					LogUtils.i(LogUtils.getStackTraceElement(),"栈内有相同的fragment.");
				}else{
					LogUtils.i(LogUtils.getStackTraceElement(),"栈内木有相同的fragment.");

				}
				fragmentTransaction.replace(R.id.mall_frame_layout, fragment);
				if (isBackStack){
					fragmentTransaction.addToBackStack(fragment.getClass().getSimpleName());
				}
			}
			fragmentTransaction.commitAllowingStateLoss();
		}catch (Exception e){
			e.printStackTrace();
		}

		printStack();

	}

	private boolean checkFragmentActivity(String tag){
		int backStackEntryCount = fragmentManager.getBackStackEntryCount();
		for (int i = 0; i < backStackEntryCount; i++) {
			FragmentManager.BackStackEntry backStackEntry = fragmentManager.getBackStackEntryAt(i);
			String name = backStackEntry.getName();
			if (tag.equals(name)) {
				fragmentManager.popBackStack(tag,1);
				return true;
			}
		}
		return false;
	}
	

	@Override
	public void switchFragment(int fragmentId,Bundle bundle,boolean addToBackStack) {
		switch (fragmentId) {
		case Constants.MENU_FRAGMENT:
			switchToMenuFragment(bundle,addToBackStack);
			break;
		case Constants.BIND_FRAGMENT:
			switchToBindFragment(bundle,addToBackStack);
			break;
		case Constants.LOGIN_FRAGMENT:
			switchToLoginFragment(bundle,addToBackStack);
			break;
		case Constants.DEVICE_LIST_FRAGMENT:
			switchToDevicesManagerFragment(bundle,addToBackStack);
			break;
		case Constants.SCANNING_FRAGMENT:
			switchToScanningFragment(bundle,addToBackStack);
			break;
		case Constants.DEVICE_SETTING_FRAGMENT:
			switchToDeviceSettingFragment(bundle,addToBackStack);
			break;
		default:
			break;
		}


	}

	@Override
	public void switchFragmentAndCloseOther(int fragmentId, Bundle bundle) {
		if (isjump.compareAndSet(true,false)) {
			while (fragmentManager.getBackStackEntryCount() > 0) {
				fragmentManager.popBackStackImmediate();
			}
			switchFragment(fragmentId,bundle,false);
			printStack();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					isjump.set(true);
				}
			},3000);
		}else{
			LogUtils.d(LogUtils.getStackTraceElement(),"hahaha");
		}
	}

	@Override
	public void popBackStackToFragment(String fragmentName,int flag) {
		LogUtils.d(LogUtils.getStackTraceElement(),"pop "+fragmentName);
		fragmentManager.popBackStack(fragmentName, flag);
		printStack();
	}

	private void printStack(){
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				int count = fragmentManager.getBackStackEntryCount();
				for (int i = 0; i < count; i++) {
					FragmentManager.BackStackEntry backStackEntryAt = fragmentManager.getBackStackEntryAt(i);
					LogUtils.i(LogUtils.getStackTraceElement(), "backStackEntry"+i+":" + backStackEntryAt.getName());
				}
				LogUtils.i(LogUtils.getStackTraceElement(), "BackStackEntryCount:" + count);
			}
		},1000);
	}



	
	@Subscribe
	public void onEventMainThread(final LogoutEvent event) {
		if(null == event){
			return;
		}
//		final boolean requestLogout = event.isRequestLogout();
//		mHandler.post(new Runnable() {
//			@Override
//			public void run() {
//				LogUtils.d(LogUtils.getStackTraceElement(),"logout event");
//				logout(requestLogout);
//			}
//		});

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getRepeatCount() == 0) {
			int count = fragmentManager.getBackStackEntryCount();
			if (count == 0) {
				tipsExit();
				return true;
			}else if(count == 1 &&
					fragmentManager.getBackStackEntryAt(0).getName()
							.equals(MenuFragment.class.getSimpleName())){
				tipsExit();
				return true;
			}else if(fragmentManager.getBackStackEntryAt(0).getName()
					.equals(LoginFragment.class.getSimpleName())){
				tipsExit();
				return true;
			}
			isExit = false;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void tipsExit(){
		if(isExit){
			isExit = false;
			MainActivity.this.finish();
		}else{
			Toast.makeText(this,getString(R.string.tip_exit),Toast.LENGTH_SHORT).show();
			mHandler.postDelayed(new Runnable() {
				@Override
				public void run() {
					isExit = false;
				}
			},3000);
			isExit = true;
		}

	}


	/**
	* @Title: logout
	* @Description: 退出登录
	* @return void    返回类型
	*/
	protected void logout(boolean requestLogout) {
		LogUtils.d(TAG, "switchFragment", " 栈数 :" + fragmentManager.getBackStackEntryCount());
		LogUtils.d(LogUtils.getStackTraceElement(),"登出");
		switchFragmentAndCloseOther(Constants.LOGIN_FRAGMENT,null);
//		fragmentManager.popBackStack();
//		if(loginFragment == null){
//			LogUtils.d(LogUtils.getStackTraceElement(),"logout 1");
//			switchToLoginFragment(null,false);
//		}else{
//			LogUtils.d(LogUtils.getStackTraceElement(),"logout 2");
//			switchFragment(loginFragment,false);
//		}
//		if(requestLogout){
//			LogUtils.d(LogUtils.getStackTraceElement(),"logout 3");
//			loginFragment.onLogout();
//		}


	}
	
}
