package com.xunixianshi.vrshow.my.fragment;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;

import java.text.NumberFormat;

/**
 * Created by markIron on 2016/9/30.
 */

public class CommonProgressDialog extends AlertDialog {

    private TextView customer_dialog_upload_tv;
    private ProgressBar customer_dialog_progress_pb;
    private TextView customer_dialog_percent_tv;
    private TextView customer_dialog_number_tv;
    private Button customer_dialog_cancel_btn;
    private Handler mViewUpdateHandler;
    private int mMax;
    private CharSequence mMessage;
    private boolean mHasStarted;
    private int mProgressVal;
    private String TAG = "CommonProgressDialog";
    private String mProgressNumberFormat;
    private NumberFormat mProgressPercentFormat;

    protected CommonProgressDialog(Context context) {
        super(context);
        initFormats();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.common_progress_dialog);
        customer_dialog_upload_tv = (TextView) findViewById(R.id.customer_dialog_upload_tv);
        customer_dialog_progress_pb = (ProgressBar) findViewById(R.id.customer_dialog_progress_pb);
        customer_dialog_percent_tv = (TextView) findViewById(R.id.customer_dialog_percent_tv);
        customer_dialog_number_tv = (TextView) findViewById(R.id.customer_dialog_number_tv);
        customer_dialog_cancel_btn = (Button) findViewById(R.id.customer_dialog_cancel_btn);
        mViewUpdateHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                int progress = customer_dialog_progress_pb.getProgress();
                int max = customer_dialog_progress_pb.getMax();
                double dProgress = (double) progress / (double) (1024 * 1024);
                double dMax = (double) max / (double) (1024 * 1024);
                if (mProgressNumberFormat != null) {
                    String format = mProgressNumberFormat;
                    customer_dialog_number_tv.setText(String.format(format, dProgress, dMax));
                } else {
                    customer_dialog_number_tv.setText("");
                }
                if (mProgressPercentFormat != null) {
                    double percent = (double) progress / (double) max;
                    SpannableString tmp = new SpannableString(mProgressPercentFormat.format(percent));
                    tmp.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), 0, tmp.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                    customer_dialog_percent_tv.setText(tmp);
                } else {
                    customer_dialog_percent_tv.setText("");
                }
            }
        };
        onProgressChanged();
        if (mMessage != null) {
            setMessage(mMessage);
        }
        if (mMax > 0) {
            setMax(mMax);
        }
        if (mProgressVal > 0) {
            setProgress(mProgressVal);
        }
    }

    private void initFormats() {
        mProgressNumberFormat = "%1.2fM/%2.2fM";
        mProgressPercentFormat = NumberFormat.getPercentInstance();
        mProgressPercentFormat.setMaximumFractionDigits(0);
    }

    private void onProgressChanged() {
        mViewUpdateHandler.sendEmptyMessage(0);
    }

    public void setProgressStyle(int style) {
//        mProgressStyle = style;
    }

    public int getMax() {
        if (customer_dialog_progress_pb != null) {
            return customer_dialog_progress_pb.getMax();
        }
        return mMax;
    }

    public void setMax(int max) {
        if (customer_dialog_progress_pb != null) {
            customer_dialog_progress_pb.setMax(max);
            onProgressChanged();
        } else {
            mMax = max;
        }
    }

    public void setIndeterminate(boolean indeterminate) {
        if (customer_dialog_progress_pb != null) {
            customer_dialog_progress_pb.setIndeterminate(indeterminate);
        }
    }

    public void setProgress(int value) {
        if (mHasStarted) {
            customer_dialog_progress_pb.setProgress(value);
            onProgressChanged();
        } else {
            mProgressVal = value;
        }
    }

    @Override
    public void setMessage(CharSequence message) {
        super.setMessage(message);
        if (customer_dialog_upload_tv != null) {
            customer_dialog_upload_tv.setText(message);
        } else {
            mMessage = message;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mHasStarted = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHasStarted = false;
    }
}
