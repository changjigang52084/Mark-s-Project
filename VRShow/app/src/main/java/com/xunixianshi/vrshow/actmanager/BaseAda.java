package com.xunixianshi.vrshow.actmanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.BaseAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseAda<T> extends BaseAdapter {
	protected List<T> group = new ArrayList<T>();
	protected Context mContext;
	protected LayoutInflater mInflater;

	public BaseAda(Context context) {
		super();
		mInflater = LayoutInflater.from(context);
		mContext = context;
	}

	@Override
	public int getCount() {
		return (group == null) ? 0 : group.size();
	}

	@Override
		public T getItem(int position) {
			/**防止外部调用时越界*/
			if(group==null || position<0 || position>group.size())
				return null;
			return group.get(position);
	}

		public void removeItem(int position){
		if(group==null || position<0 || position>group.size())
			return;
		group.remove(position);
		notifyDataSetChanged();
		return;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public boolean isEmpty() {
		return (group == null) ? true : group.isEmpty();
	}

	public void setGroup(List<T> g) {
		if(group != null){
			group.clear();
		}
		else{
			group = new ArrayList<>();
		}
		group.addAll(g);
		notifyDataSetChanged();
	}
	
	public List<T> getGroup() {
			return group;
		}
	
	public void addItem(T item){
		group.add(item);
		notifyDataSetChanged();
	}
	
	public void addItemNoNotify(T item){
		group.add(item);
	}
	
	public void addItems(List<T> items){
		if(items != null){
			group.addAll(items);
			notifyDataSetChanged();
		}
	}
	
	public void clearGroup(boolean notify){
		if(group != null){
			group.clear();
			if(notify){
				notifyDataSetChanged();
			}
		}
	}
	/**
	* @Title: showToast
	* @Description: TODO 消息提示
	* @author hechuang 
	* @date 2015-11-12
	* @param @param message    设定文件
	* @return void    返回类型
	*/
	private Toast toast = null;  //用于判断是否已有Toast执行
	public void showToastMsg(String msg) {
		if(toast == null)
		{
			toast = Toast.makeText(mContext, msg, Toast.LENGTH_SHORT);  //正常执行
		}
		else {
			toast.setText(msg);  //用于覆盖前面未消失的提示信息
		}
		toast.show();
	}

}
