package com.lzmr.bindtool.ui.fragment;



import com.lzkj.baize_android.utils.StringUtils;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.app.BindToolApp;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.impl.LogoutListener;
import com.lzmr.bindtool.util.ConfigSettings;
import com.lzmr.bindtool.util.Constants;
import com.lzmr.bindtool.util.MyProgressDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年8月19日 下午5:46:26 
 * @version 1.0 
 * @parameter  
 */
@SuppressLint("ValidFragment")
public class BaseFragment extends Fragment {
	/**控制切换fragment的接口**/
	protected ControlFragmentListener switchFragmentListener;
	/**fragment要显示的view*/
	protected View view;
	
	protected LogoutListener logoutListener;
	
	protected MyProgressDialog progressDialog;

	public BaseFragment(){

	}

	public BaseFragment(ControlFragmentListener switchFragmentListener) {
		this.switchFragmentListener = switchFragmentListener;
	}
	@Override
	public void onDestroyView() {
		if (null != view) {
			ViewParent parent = view.getParent();
			if (null != parent) {
				((ViewGroup)parent).removeView(view);
			}
		}
		super.onDestroyView();
	}
	/**
	 * 隐藏键盘
	 */
	protected void hideSoftInputFromWindow() {
		InputMethodManager im = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE); 
		View view = getActivity().getCurrentFocus();
		if (null != view) {
			IBinder iBinder = view.getApplicationWindowToken();
			if (null != iBinder) {
				im.hideSoftInputFromWindow(iBinder, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}
	}
	
	 /**
     * 显示进度对话框
     */
    protected void startProgressDialog(String message,boolean cancelable) {
        if (progressDialog == null) {
            progressDialog = MyProgressDialog.show(getActivity(), message,cancelable);
        }
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }
    
    protected synchronized void showDialog(String message,DialogInterface.OnClickListener listener){
		BindToolApp.getApplication().showDialog(getActivity(),message,listener);
	}
    
    /** 
   	* @Title: switchMenu 
   	* @Description: TODO(切换菜单界面) 
   	* @Param     设定文件 
   	* @return void    返回类型 
   	*/ 
	protected void switchMenu(Bundle bundle,boolean addToBackStack){
   		switchFragmentListener.switchFragment(Constants.MENU_FRAGMENT,bundle,addToBackStack);
   	}
    
    /** 
	* @Title: switchBind 
	* @Description: TODO(切换绑定界面) 
	* @Param     设定文件 
	* @return void    返回类型 
	*/ 
    protected void switchBind(Bundle bundle,boolean addToBackStack){
		switchFragmentListener.switchFragment(Constants.BIND_FRAGMENT,bundle,addToBackStack);
	}
	
	/** 
	* @Title: switchLogin 
	* @Description: TODO(切换解绑界面) 
	* @Param     设定文件 
	* @return void    返回类型 
	*/ 
	protected void switchLogin(Bundle bundle,boolean addToBackStack){
		switchFragmentListener.switchFragment(Constants.LOGIN_FRAGMENT,bundle,addToBackStack);
	}
	
	/** 
	* @Title: switchDevice 
	* @Description: 切换到终端管理界面
	* @return void    返回类型
	*/ 
	protected void switchDevice(Bundle bundle,boolean addToBackStack){
		switchFragmentListener.switchFragment(Constants.DEVICE_LIST_FRAGMENT,bundle,addToBackStack);
	}
	
	/** 
	* @Title: switchToDeviceListFragment 
	* @Description: 切换到扫码界面
	* @return void    返回类型
	*/ 
	protected void switchScanning(Bundle bundle,boolean addToBackStack){
		switchFragmentListener.switchFragment(Constants.SCANNING_FRAGMENT,bundle,addToBackStack);
	}

	/**
	 * @Title: switchToDeviceListFragment
	 * @Description: 切换到终端设置界面
	 * @return void    返回类型
	 */
	protected void switchDeviceSetting(Bundle bundle,boolean addToBackStack){
		switchFragmentListener.switchFragment(Constants.DEVICE_SETTING_FRAGMENT,bundle,addToBackStack);

	}


	/**
	 * @Title: switchLoginAndCloseOther
	 * @Description: 切换登录界面,并清空返回栈
	 * @return void    返回类型
	 */
	protected void switchLoginAndCloseOther(boolean isSessionInvalid){
		Bundle bundle = new Bundle();
		bundle.putBoolean("sessionInvalid",isSessionInvalid);
		switchFragmentListener.switchFragmentAndCloseOther(Constants.LOGIN_FRAGMENT,bundle);
	}



	/**
	 * @Title: popBackStackFragment
	 * @Description: 指定fragment置顶
	 */
	protected void popBackStackToFragment(String fragmentName,int flag){
		switchFragmentListener.popBackStackToFragment(fragmentName, flag);
	}
	
	
	/** 
	* @Title: checkLogin 
	* @Description: 检测是否有UserKey
	* @return boolean    
	*/ 
	protected boolean checkUserKey(){
		String userKey = ConfigSettings.getUserKey();
		return StringUtils.isEmpty(userKey);
	}
	

	protected void finish() {
		getFragmentManager().popBackStack();
	}
	
	protected void toastMessage(String message) {
		Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
	}
	
}
