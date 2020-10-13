package com.lzmr.bindtool.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzmr.bindtool.R;



/** 
 * @author kchang Email:changkai@lz-mr.com
 * @date   创建时间：2015年9月16日 下午4:04:49 
 * @version 1.0 
 * @parameter  main view pager适配器
 */
public class FragmentMainViewPagerAdapter extends FragmentPagerAdapter {
	/**在viewPager中展示的fragment**/
	private ArrayList<Fragment> fragments;
	/***在tab上面展示标题**/
	private String[] tabTitles;
	private String[] tabDeviceNumbers;
	private int[] imgRes;
	private Context context;
	/**
	 * 构造方法
	 * @param fm
	 * @param fragments
	 * 			要展示的一组fragment
	 * @param titles
	 * 			tab的一组标题
	 */
	public FragmentMainViewPagerAdapter(Context context, 
			FragmentManager fm, 
			ArrayList<Fragment> fragments,  
			String[] titles, int[] imgRes) {
		super(fm);
		this.context = context;
		this.fragments = fragments;
		this.tabTitles = titles;
		this.imgRes = imgRes;
	}
	
	public void setDeviceNumbers(String[] tabDeviceNumbers) {
		this.tabDeviceNumbers = tabDeviceNumbers;
	}
	
    @Override
    public Fragment getItem(int position) {
        return fragments == null ? null : fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments == null ? 0 : fragments.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return null;
//        return tabTitles[position];
    }
    
    public View getTabView(int position, TextView[] tvs, TextView[] deviceStateTvs, CheckBox[] stateIconImgs){
        View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        TextView tv= (TextView) view.findViewById(R.id.txt_device_size);
        tv.setText("");
        tvs[position] = tv;
        TextView deviceSataeTv = (TextView) view.findViewById(R.id.txt_device_state_type);
        deviceSataeTv.setText(tabTitles[position]);
        deviceStateTvs[position] = deviceSataeTv;
        CheckBox deviceSataeIconImg = (CheckBox) view.findViewById(R.id.img_table_icon);
        deviceSataeIconImg.setBackgroundResource(imgRes[position]);
        stateIconImgs[position] = deviceSataeIconImg;
        return view;
    }
}
