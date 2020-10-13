package com.unccr.zclh.dsdps.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.unccr.zclh.dsdps.R;
import com.unccr.zclh.dsdps.models.Area;
import com.unccr.zclh.dsdps.util.DateTimeUtil;
import com.unccr.zclh.dsdps.util.StringUtil;
import com.unccr.zclh.dsdps.util.Util;

/**
 * @author  jigangchang Email:changjigang@sunchip.com
 * @version 1.0
 * @data 创建时间：2019年10月08日 下午5:11:45
 * @parameter DefaultFragment
 */
@SuppressLint("ValidFragment")
public class DefaultFragment extends BaseFragment {

    private static final String TAG = "DefaultFragment";

    private View view = null;
    private TextView mac_tv;
    private TextView textView;
    private TextView mDateTv;

    public DefaultFragment(){

    }

    public DefaultFragment(Area area) {
        super(area);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (null == view ) {
            view 	 = inflater.inflate(R.layout.default_fragment_layout, container, false);
            mac_tv = view.findViewById(R.id.mac_tv);
            textView = (TextView) view.findViewById(R.id.default_alert_msg);
            mDateTv  = (TextView) view.findViewById(R.id.default_date_tv);
            view.setBackgroundResource(R.drawable.zclh_logo);
//            AnimationDrawable animationDrawable = (AnimationDrawable) view
//                    .getBackground();
//            animationDrawable.start();
        }
        return view;
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void startLoadingTask() {
        Bundle bundle = getArguments();
        if(bundle != null){
            String alertMsg = bundle.getString("alertMsg");
            if (!StringUtil.isNullStr(alertMsg) && null != textView) {
                textView.setText(alertMsg);
            }
        }
        mac_tv.setText("MAC: " + Util.getEthernetMacAddress() + "\r\n" +" SN: " + android.os.Build.SERIAL);
        mDateTv.setText(DateTimeUtil.getStringTimeToFormat(null));
    }
    /**
     * 设置显示的提示信息
     * @param alertMsg
     * 			信息内容
     */
    public void setShowInfo(final String alertMsg) {
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
