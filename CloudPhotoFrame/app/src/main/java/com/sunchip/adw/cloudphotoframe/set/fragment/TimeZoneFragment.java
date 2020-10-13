package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.thread.TimeThread;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

public class TimeZoneFragment extends BaseFragment implements View.OnClickListener {

    private View view = null;
    private TextView time_tv;
    private TextView time_zone_tv;
    private TextView time_zone_rb;
    private String[] items;
    private String[] item;
    public AlertDialog dialog;

    public TimeZoneFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.timezone_fragment_layout, container, false);

            time_tv = view.findViewById(R.id.time_tv);
            time_zone_tv = view.findViewById(R.id.time_zone_tv);
            time_zone_rb = view.findViewById(R.id.time_zone_rb);

            time_tv.setTypeface(CloudFrameApp.typeFace);
            time_zone_tv.setTypeface(CloudFrameApp.typeFace);
            time_zone_rb.setTypeface(CloudFrameApp.typeFace);

            time_zone_rb.setOnClickListener(this);

            items = CloudFrameApp.getCloudFrameApp().getResources().getStringArray(R.array.timezone);
            item = CloudFrameApp.getCloudFrameApp().getResources().getStringArray(R.array.timezones);

            //为0默认系统 否则不管
            int mTimeZone = SharedUtil.newInstance().getInt("TimeZone");
            time_zone_tv.setText(items[mTimeZone]);
            SystemInterfaceUtils.getInstance().SetTimeZone(item[mTimeZone]);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.time_zone_rb:
                break;
        }
    }

    @Override
    public boolean onKeyDown(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                time_zone_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                break;
            case KeyEvent.KEYCODE_ENTER:
                settimezoneDialog();
                break;
        }
        return ret;
    }

    private void settimezoneDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.when_frame_wakes_up);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                time_zone_tv.setText(items[i]);
                SystemInterfaceUtils.getInstance().SetTimeZone(item[i]);
                SharedUtil.newInstance().setInt("TimeZone", i);
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
        dialog = builder.create();
        dialog.show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }


    @Override
    public void Select(int postion) {
        if (postion == 0) {
            time_zone_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else {
            time_zone_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        }
    }
}
