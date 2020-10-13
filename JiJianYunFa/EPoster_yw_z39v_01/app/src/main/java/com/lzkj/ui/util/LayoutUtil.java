package com.lzkj.ui.util;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Program;
import com.lzkj.ui.app.EPosterApp;
import com.lzkj.ui.util.LogUtils.LogTag;

import java.io.File;
import java.util.List;
/**
 *@author kchang changkai@lz-mr.com
 *@Description:Layout Params 管理类
 *@time:2016年3月21日 下午5:38:48
 */
public class LayoutUtil {
	private static final LogTag LOG_TAG = LogUtils.getLogTag(LayoutUtil.class.getSimpleName(), true);
	/**区域id*/
	public static int MAX_AREA_ID = 520;
	public static int MIN_AREA_ID = 1;
	public static int AREA_ID = MIN_AREA_ID;
	/**屏幕基础宽度 1280*/
	public float BASE_WIDTH = 1080;
	/**屏幕基础高度 720*/
	public float BASE_HEIGHT = 1920;
	/**尺寸最大值 超过这个值则调用压缩展示图片*/
	public static final int MAX_WH = 2000;
	private static volatile LayoutUtil layoutUtil;
	private LayoutUtil(){}
	
	public static LayoutUtil getInstance() {
		if (null == layoutUtil) {
			synchronized (LayoutUtil.class) {
				if (null == layoutUtil) {
					layoutUtil = new LayoutUtil();
				}
			}
		}
		return layoutUtil;
	}
	/**
	 * get fullscreen layout params
	 * 
	 * @return
	 * 		全屏的layoutParams
	 */
	public LayoutParams getFullScreenLayoutParams() {
		LayoutParams params = new LayoutParams(LayoutUtil.getInstance().getRealDisplayWidth(),
				LayoutUtil.getInstance().getRealDisplayHeight());
		return params;
	}
	
	public LayoutParams getOnePxLayoutParams() {
		LayoutParams params = new LayoutParams(1, 1);
		return params;
	}
	/**
	 * 获取当前屏幕的宽度
	 * @return
	 * 		返回当前屏幕的宽度
	 */
	public int getRealDisplayWidth() {
		int widthtPixels;
		WindowManager w = ((WindowManager) EPosterApp.getApplication().getSystemService(Context.WINDOW_SERVICE));
		Display d = w.getDefaultDisplay();  
		DisplayMetrics metrics = new DisplayMetrics();  
		d.getMetrics(metrics);  
		// since SDK_INT = 1;  
		widthtPixels = metrics.widthPixels;  
		// includes window decorations (statusbar bar/navigation bar)  
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
			try {
				widthtPixels = (Integer) Display.class.getMethod("getRawWidth").invoke(d);
			} catch (Exception e) {
				LogUtils.e(LOG_TAG, "getRealDisplayWidth_1", e);
			}
		} else if (Build.VERSION.SDK_INT >= 17) { // includes window decorations (statusbar bar/navigation bar)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d,realSize);
				widthtPixels = realSize.x;
			} catch (Exception e) {
				LogUtils.e(LOG_TAG, "getRealDisplayWidth_1", e);
			}
		}
		LogUtils.d(LOG_TAG, "getRealDisplayWidth_1", "realWidthPixels:" + widthtPixels);
		return widthtPixels;
	}
	/**
	 * 获取当前屏幕的高度
	 * @return
	 * 		返回当前屏幕的高度
	 */
	public int getRealDisplayHeight() {
		int heightPixels;
		WindowManager w = ((WindowManager) EPosterApp.getApplication().getSystemService(Context.WINDOW_SERVICE));
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();  
		d.getMetrics(metrics);  
		// since SDK_INT = 1;  
		heightPixels = metrics.heightPixels;

//		LogUtils.d(LOG_TAG, "getRealDisplayHeight", "heightPixels : " + heightPixels);
		// includes window decorations (statusbar bar/navigation bar)
		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
			try {
				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);
			} catch (Exception e) {
				LogUtils.e(LOG_TAG, "getRealDisplayHeight_1", e);
			}
		} else if (Build.VERSION.SDK_INT >= 17) { // includes window decorations (statusbar bar/navigation bar)
			try {
				Point realSize = new Point();
				Display.class.getMethod("getRealSize", Point.class).invoke(d,realSize);
				heightPixels = realSize.y;
			} catch (Exception e) {
				LogUtils.e(LOG_TAG, "getRealDisplayHeight_1", e);
			}
		}
		if (ConfigSettings.IS_WEIGHT) {
			float weight = SharedUtil.newInstance().getFloat(SharedUtil.KEY_WEIGHT);
			LogUtils.d(LOG_TAG, "getRealDisplayHeight", "weight: " + weight);
			heightPixels = (int) (heightPixels * weight);
		}
		LogUtils.d(LOG_TAG, "getRealDisplayHeight_1", "realHightPixels: "+ heightPixels);
		return heightPixels;
	}
	
	/**
	 * 根据节目的区域，加载区域布局
	 * @param layout 当前布局
	 * @param program 节目对象
	 */
	public void loadFrameLayoutForArea(FrameLayout layout, Program program, boolean isPreLoad) {
		if (layout == null || program == null) {
			LogUtils.w(LOG_TAG, "loadFrameLayoutForArea", "layout or program is null! isPreLoad : " + isPreLoad);
			return;
		}
		
		List<Area> areaList = program.getAs();
		if (null == areaList || areaList.isEmpty()) {
			LogUtils.w(LOG_TAG, "loadFrameLayoutForArea", "Program area size is null isPreLoad : " + isPreLoad);
			return;
		}
		LogUtils.d(LOG_TAG, "loadFrameLayoutForArea", "BASE_HEIGHT:" + BASE_HEIGHT +", BASE_WIDTH :" + BASE_WIDTH);  
		Integer programDirection = program.getD();//节目方向 1表示横屏，2表示竖屏
		int areaSize = areaList.size();
		try {
			for(int i = 0; i < areaSize; i++){
				Area area = areaList.get(i);
				if (null != area) {
					LogUtils.w(LOG_TAG, "loadFrameLayoutForArea", "area id : " + (AREA_ID + i) + ",isPreLoad : " + isPreLoad );
					FrameLayout view = new FrameLayout(layout.getContext());
					view.setId(AREA_ID + i);
					view.setBackgroundColor(Color.TRANSPARENT);
					view.setTag(area);
					// instead of leftMargin, topMargin of LayoutParams
					view.setX(getLayoutXYForArea(area)[0]);
					view.setY(getLayoutXYForArea(area)[1]);
					layout.addView(view, getLayoutParamsForArea(area, programDirection));
				}
			}
		} catch (Exception e) {
			String path  = FileStore.getInstance().getLayoutFolderPath()
					+ File.separator + program.getKey() + FileStore.PRM_SUFFIX_NAME;
			File prmFile = new File(path);
			if (prmFile.exists()) {
				prmFile.delete();
			}
			e.printStackTrace();
		}
	}
	
	/**
	 * 根据Area获取LayoutParams对象
	 * @param area
	 * 			区域对象
	 * @return
	 * 		返回区域的LayoutParams
	 */
	private LayoutParams getLayoutParamsForArea(Area area, Integer programDirection) {
		if (isLandscapeOrVertical()) {//屏幕是横屏
			if (null == programDirection) {//兼容老版本
				if (!checkPrmIsLandscapeOrVertical(area)) {//判断节目是否为竖屏
					return getVerticalPrmToLandscape(area);
				}	
			} else if (Constants.PROGREM_VERTICAL == programDirection) {
				return getVerticalPrmToLandscape(area);
			}
		
		} else {//当前屏幕为竖屏
			if (null == programDirection) {//兼容老版本
				if (checkPrmIsLandscapeOrVertical(area)) {//节目为横屏
					return getLandscapePrmToVertical(area);
				}
			} else if (Constants.PROGREM_HORIZONTAL == programDirection) {
				return getLandscapePrmToVertical(area);
			}
		}
		int width  = (int)(area.getW() * getDifferenceW());
		int height = (int)(area.getH() * getDifferenceH());
		
		LogUtils.d(LOG_TAG, "getLayoutParamsForArea", "area w: " + area.getW() + "area h:" + area.getH()
				+  "width : " + width + ",height : " + height + ", getDifferenceH :" + getDifferenceH());
		if (width == getRealDisplayWidth()) {
			width = LayoutParams.MATCH_PARENT;
		}
		
		if (height == getRealDisplayHeight()) {
			height = LayoutParams.MATCH_PARENT;
		}
		LayoutParams params = new LayoutParams(width, height);
		LogUtils.d(LOG_TAG, "getLayoutParamsForArea", "params.w : " + params.width + ". params.h : " + params.height);
		return params;
	}
	
	/**
	 * 获取竖屏节目在横屏上面的layoutParams
	 * @param area 区域
	 * @return 返回LayoutParams
	 */
	private LayoutParams getVerticalPrmToLandscape(Area area) {
		int height = (int)(area.getW() * getDifferenceW());
		int width  = (int)(area.getH() * getDifferenceH());
		LayoutParams params = new LayoutParams(width, height);
		return params;
	}
	
	/**
	 * 获取横屏节目在竖屏上面的layoutParams
	 * @param area 区域
	 * @return 返回LayoutParams
	 */
	private LayoutParams getLandscapePrmToVertical(Area area) {
		int height = (int)(area.getW() * getDifferenceW());
		int width = (int)(area.getH() * getDifferenceH());
		
		LogUtils.d(LOG_TAG, "getLayoutXYForArea", "height : " + height + ", width : " + width);

		LayoutParams params = new LayoutParams(width, height);
		return params;
	}
	
	/** 
	* @Title: getAreaId 
	* @Description: 获取区域id
	* @return int    返回类型 
	*/ 
	public void updateAreaId(int areaSize){
		if(AREA_ID > MAX_AREA_ID){
			AREA_ID = MIN_AREA_ID;
		}
		AREA_ID += areaSize; 
		LogUtils.d(LOG_TAG, "updateAreaId", "now:"+AREA_ID);
	}
	
	/**
	 * 判断节目是横屏节目还是竖屏节目，true表示横屏，false表示竖屏
	 * @return
	 */
	private boolean checkPrmIsLandscapeOrVertical(Area area) {
		if (null != area) {
			if (area.getW() > area.getH()) {
				return true;
			}	
		}
		return false;
	}
	
	/**
	 * 获取xy
	 * @param area
	 * @return
	 */
	private  float[] getLayoutXYForArea(Area area) {
		float x = area.getX() * getDifferenceW();
		float y = area.getY() * getDifferenceH();
		LogUtils.d(LOG_TAG, "getLayoutXYForArea", "x : " + x + ", y : " + y);
		return new float[]{x, y};
	}
	
	/**
	 * 获取宽度的差值
	 * @return
	 */
	public float getDifferenceW() {
		LogUtils.d(LOG_TAG, "getDifferenceW", "BASE_HEIGHT:" + BASE_HEIGHT +", BASE_WIDTH :" + BASE_WIDTH);  
		if (isLandscapeOrVertical()) {//横屏
			return getRealDisplayWidth() / BASE_WIDTH;
		}
		return getRealDisplayWidth() / BASE_HEIGHT;//竖屏
	}
	
	/**
	 * 获取高度差值
	 * @return
	 */
	public float getDifferenceH() {
		LogUtils.d(LOG_TAG, "getDifferenceH", "BASE_HEIGHT:" + BASE_HEIGHT +", BASE_WIDTH :" + BASE_WIDTH);  
		if (isLandscapeOrVertical()) {//横屏
			return getRealDisplayHeight() / BASE_HEIGHT;
		}
		return getRealDisplayHeight() / BASE_WIDTH;//竖屏
	}
	/**
	 * 获取基础的宽度
	 * @return
	 */
	public int getBaseWidth() {
		LogUtils.d(LOG_TAG, "getBaseWidth", "BASE_HEIGHT:" + BASE_HEIGHT +", BASE_WIDTH :" + BASE_WIDTH);  
		if (isLandscapeOrVertical()) {//横屏
			return (int)BASE_WIDTH;
		} else {
			return (int)BASE_HEIGHT;
		}
	}
	/**
	 * 获取基础的高度
	 * @return
	 */
	public int getBaseHeight() {
		if (isLandscapeOrVertical()) {//横屏
			return (int)BASE_HEIGHT;
		} else {
			return (int)BASE_WIDTH;
		}
	}
	
	/**
	 * 判断是横屏还是竖屏，横屏返回true,竖屏返回false
	 * @return
	 */
	private boolean isLandscapeOrVertical() {
		if (getRealDisplayWidth() > getRealDisplayHeight()) {//横屏
			return true;
		}
		return false;
	}
	/**
	 * 获取字体大小
	 * @param areaWidth 区域宽度
	 * @param textLength 文字个数
	 * @return 返回字体大小
	 */
	public int getFontSize(int areaWidth, int textLength) {
		return (int) (areaWidth / textLength / 0.9f);
	}
}
