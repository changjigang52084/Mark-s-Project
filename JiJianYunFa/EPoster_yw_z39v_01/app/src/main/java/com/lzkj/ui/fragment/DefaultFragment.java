package com.lzkj.ui.fragment;

import android.annotation.SuppressLint;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baize.adpress.core.protocol.bo.protocol.push.command.base.Area;
import com.lzkj.ui.R;
import com.lzkj.ui.util.DateTimeUtil;
import com.lzkj.ui.util.LogUtils;
import com.lzkj.ui.util.LogUtils.LogTag;
import com.lzkj.ui.util.StringUtil;
/**
 *没有节目的界面
 * @author changkai
 *
 */
@SuppressLint("ValidFragment")
public class DefaultFragment extends BaseFragment {
	private static final LogTag TAG = LogUtils.getLogTag(DefaultFragment.class.getSimpleName(), true);
	private View view = null;
	private TextView textView;
	private TextView mDateTv;
	
	public DefaultFragment(Area area) {
		super(area);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null == view ) {
			view 	 = inflater.inflate(R.layout.default_fragment_laout, container, false);
			textView = (TextView) view.findViewById(R.id.default_alert_msg);
			mDateTv  = (TextView) view.findViewById(R.id.default_date_tv);
			view.setBackgroundResource(R.drawable.bg_anim);
			AnimationDrawable animationDrawable = (AnimationDrawable) view
					.getBackground();
			animationDrawable.start();
		}
		return view;
	}
	
	@Override
	protected void startLoadingTask() {
		Bundle bundle = getArguments();
		if(bundle != null){
			String alertMsg = bundle.getString("alertMsg");
			if (!StringUtil.isNullStr(alertMsg) && null != textView) {
				textView.setText(alertMsg);
			}
		}
		mDateTv.setText(DateTimeUtil.getStringTimeToFormat(null));
	}
	/**
	 * 设置显示的提示信息
	 * @param alertMsg
	 * 			信息内容
	 */
	public void setShowInfo(final String alertMsg) {
		LogUtils.d(TAG, "setShowInfo", "alertMsg:"+alertMsg);
		if (null != alertMsg && null != textView) {
			textView.post(new Runnable() {
				@Override
				public void run() {
					textView.setText(alertMsg);
				}
			});
		}
	}
	
	@Override
	public void pausePlayback() {
	}
	
	@Override
	public void stopPlayback() {
	}
	
	@Override
	public void resumePlayback() {
	}
}
