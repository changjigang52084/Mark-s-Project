package com.xunixianshi.vrshow.my.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * TODO 自定义对话框
 *
 * @author MarkChang
 * @ClassName CustomerDialogActivity
 * @time 2016/11/1 15:54
 */

public class CustomerDialogActivity extends BaseAct {

    @Bind(R.id.customer_dialog_upload_tv)
    TextView customer_dialog_upload_tv;
    @Bind(R.id.customer_dialog_progress_pb)
    ProgressBar customer_dialog_progress_pb;
    @Bind(R.id.customer_dialog_percent_tv)
    TextView customer_dialog_percent_tv;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_customer_dialog);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
//        IntentFilter filter = new IntentFilter(CreateContentActivity.action);
//        registerReceiver(broadcastReceiver, filter);
    }

    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            customer_dialog_progress_pb.setProgress(Integer.parseInt(intent.getExtras().getString("progress")));
            customer_dialog_percent_tv.setText(intent.getExtras().getString("percent"));
        }
    };

    @OnClick(R.id.customer_dialog_cancel_btn)
    public void onClick() {
        CustomerDialogActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        ButterKnife.unbind(this);
    }
}
