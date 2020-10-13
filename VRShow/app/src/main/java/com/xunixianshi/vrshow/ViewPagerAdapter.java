package com.xunixianshi.vrshow;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Parcelable;
import android.view.View;
import android.view.View.OnClickListener;

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class ViewPagerAdapter extends PagerAdapter {

	private List<View> views;
	private Activity activity;
	private static final String SHAREDPREFERENCES_NAME = "first_pref";

	private void setGuided() {
		SharedPreferences preferences = activity.getSharedPreferences(SHAREDPREFERENCES_NAME, Context.MODE_PRIVATE);
		Editor editor = preferences.edit();

		editor.putBoolean("bisFirstIn", false);

		editor.commit();
	}

	public ViewPagerAdapter(List<View> views, Activity activity) {
		this.views = views;
		this.activity = activity;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		((ViewPager) arg0).removeView(views.get(arg1));
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		if (views != null) {
			return views.size();
		}
		return 0;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		((ViewPager) arg0).addView(views.get(arg1), 0);
//		if (arg1 == views.size() - 1) {
//
//			views.get(arg1).setOnClickListener(new OnClickListener() {
//
//				@Override
//				public void onClick(View v) {
//
//					setGuided();
//					goHome();
//
//				}
//
//			});
//		}
		return views.get(arg1);
	}

	private void goHome() {
		activity.startActivity(new Intent(activity, MainActivity.class));

		activity.finish();
	}

	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		return (arg0 == arg1);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}

}
