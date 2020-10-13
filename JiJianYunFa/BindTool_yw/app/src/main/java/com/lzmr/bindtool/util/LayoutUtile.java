package com.lzmr.bindtool.util;

import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

import com.lzmr.bindtool.app.BindToolApp;

/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年8月7日 下午7:25:56 
 * @version 1.0 
 * @parameter  
 */
public class LayoutUtile {
	public static int getRealDisplayWidth() {
		int widthtPixels;
		WindowManager w = ((WindowManager) BindToolApp.getApplication().getSystemService(Context.WINDOW_SERVICE));
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
			}
		}else if (Build.VERSION.SDK_INT >= 17) { // includes window decorations (statusbar bar/navigation bar)  
			try {  
				Point realSize = new Point();  
				Display.class.getMethod("getRealSize", Point.class).invoke(d,realSize);  
				widthtPixels = realSize.x;
			} catch (Exception e) {
			} 
		}
		return widthtPixels;
	}
	
	public static int getRealDisplayHeight() {
		int heightPixels;
		WindowManager w = ((WindowManager) BindToolApp.getApplication().getSystemService(Context.WINDOW_SERVICE));
		Display d = w.getDefaultDisplay();
		DisplayMetrics metrics = new DisplayMetrics();  
		d.getMetrics(metrics);  
		// since SDK_INT = 1;  
		heightPixels = metrics.heightPixels;
		// includes window decorations (statusbar bar/navigation bar)
//		if (Build.VERSION.SDK_INT >= 14 && Build.VERSION.SDK_INT < 17) {
//			try {  
//				heightPixels = (Integer) Display.class.getMethod("getRawHeight").invoke(d);  
//			} catch (Exception e) {
//			}
//		}else if (Build.VERSION.SDK_INT >= 17) { // includes window decorations (statusbar bar/navigation bar)  
//			try {
//				Point realSize = new Point();
//				Display.class.getMethod("getRealSize", Point.class).invoke(d,realSize);  
//				heightPixels = realSize.y;
//			} catch (Exception e) {
//			} 
//		}
		return heightPixels;
	}
	/**
	 * 根据列计算宽度
	 * @param column
	 * @return
	 */
	public static int calculateW(int column) {
		return ((getRealDisplayWidth()/column) - 10);
	}
	/**
	 * 获取评论listview的高度
	 * @return
	 */
	public static int getMallCommentItemListViewH() {
		 return getRealDisplayHeight() - 100;
	}
}
