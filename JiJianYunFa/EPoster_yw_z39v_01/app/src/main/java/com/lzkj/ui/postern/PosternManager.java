package com.lzkj.ui.postern;

import java.util.Timer;
import java.util.TimerTask;

import com.lzkj.ui.R;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.StringUtil;

import android.os.Handler;
import android.util.Log;
import android.widget.Toast;


/**
 * 广告机后门管理器
 *
 * @author lyhuang
 * @date 2016-2-27 下午5:36:22
 */
public class PosternManager {
	private static final LogTag TAG = LogUtils.getLogTag(PosternManager.class.getSimpleName(), true);
	private static PosternManager ins;
	/**
	 * 监听时间
	 */
	private static final long LISTENER_TIME = 60 * 1000;
	/**
	 * 延时开启后门
	 */
	private static final long uptimeMillis = 3 * 1000;
	/**
	 * handler
	 */
	private Handler mHandler = null;
	/**
	 * 后门开关监听计时器
	 */
	private Timer mTimer = null;
	/**
	 * 后门开关是否开启监听
	 */
	private boolean isPosternSwitchListener = false;
	/**
	 * 后门触发顺序
	 */
//	private static final String POSTERN_SWITCH_PW = R.id.top_left_window + "_" +
//													R.id.top_right_window + "_" +
//													R.id.bottom_right_window + "_" +
//													R.id.bottom_left_window + "_";
	
	/**
	 * 用户输入的开关按钮缓存
	 */
	private StringBuilder posternKeyCache = null;
	
	private Runnable openPosternTask = null;
	
	/**
	 * 后门开关监听任务：规定时间内没有开启后门，则关闭监听 
	 */
	private TimerTask listenerTask = null;
	
	private PosternManager (Runnable openPosternTask){
		mTimer = new Timer();
		mHandler = new Handler();
		posternKeyCache = new StringBuilder();
		this.openPosternTask = openPosternTask;
	}
	
	public static PosternManager get (Runnable openPosternTask){
		if(null == ins){
			ins = new PosternManager(openPosternTask);
		}
		return ins;
	}
	
	/**
	 * 开启后门监听
	 * @param 
	 * @return
	 */
	public void openPosternListener(){
		closePosternListener();
		LogUtils.d(TAG, "openPosternListener", "Open postern switch.");
		isPosternSwitchListener = true;
		listenerTask = getTimerTask();
		mTimer.schedule(listenerTask, LISTENER_TIME);
	}
	
	/**
	 * 关闭后门开关
	 * @param 
	 * @return
	 */
	public void closePosternListener(){
		LogUtils.d(TAG, "openPosternListener", "Close postern switch.");
		isPosternSwitchListener = false;
		posternKeyCache.delete(0, posternKeyCache.length());
		if(listenerTask!=null){
			listenerTask.cancel();
		}
	}
	
	private TimerTask getTimerTask(){
		return new TimerTask() {
			@Override
			public void run() {
				closePosternListener();
			}
		};
	}
	
	/**
	 * 输入开启后门的key
	 * @param 
	 * @return
	 */
	public void inputPosternKey(String key){
		if(!isPosternSwitchListener()){
			LogUtils.i(TAG, "inputPosternKey", "Postern switch listener is closed.");
			return;
		}
		if(posternKeyCache == null){
			LogUtils.e(TAG, "inputPosternKey", "Postern key cache is null.");
			return;
		}
		String keys = posternKeyCache.toString();
		if(keys.split("_").length >= getPosternSwitchPwLength()){
			LogUtils.w(TAG, "inputPosternKey", "Postern key overflow.");
			closePosternListener();
			return;
		}
		LogUtils.d(TAG, "inputPosternKey", "PosternKey:"+key);
		posternKeyCache.append(key);
		posternKeyCache.append("_");
		keys = posternKeyCache.toString();
		LogUtils.d(TAG, "inputPosternKey", "PosternKeyCache:"+posternKeyCache.toString());
		
		if(checkPosternSwitch(posternKeyCache.toString())){
			openPostern();
			return;
		}
		if(keys.split("_").length >= getPosternSwitchPwLength()){
			LogUtils.i(TAG, "inputPosternKey", "Error postern key. Clear.");
			Toast.makeText(EPosterApp.getApplication()
					,StringUtil.getString(R.string.error_postern_key) , Toast.LENGTH_SHORT).show();
			closePosternListener();
		}
	}
	
	
	
	/**
	 * 获取后门触发顺序的长度
	 * @param 
	 * @return 后门触发顺序的长度
	 */
	private int getPosternSwitchPwLength(){
		return 0;
//		return POSTERN_SWITCH_PW.split("_").length;
	}
	
	/**
	 * 后门开关是否开启
	 * @param 
	 * @return boolean
	 */
	public boolean isPosternSwitchListener (){
		return isPosternSwitchListener;
	}
	
	/**
	 * 检测是否匹配后门开关
	 * @param posternWindowStr 触发后门开关的按钮顺序
	 * @return boolean
	 */
	public boolean checkPosternSwitch (String posternWindowStr){
//		if(POSTERN_SWITCH_PW.equals(posternWindowStr)){
//			return true;
//		}
		return false;
	}
	
	/**
	 * 开启后门
	 */
	private void openPostern(){
		if(openPosternTask == null){
			LogUtils.w(TAG, "openPostern", "Open postern task is null.");
			return;
		}
		LogUtils.i(TAG, "openPostern", "Open postern soon...");
		mHandler.postAtTime(openPosternTask, uptimeMillis);
		closePosternListener();
	}
	
	public void destory(){
		closePosternListener();
		mTimer.cancel();
		mTimer = null;
		mHandler.removeCallbacks(null);
		mHandler = null;
		ins = null;
	}
}
