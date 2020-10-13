package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.RecoverySystem;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresPermission;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;

import java.io.File;

public class SoftwareUpdateFragment extends BaseFragment implements View.OnClickListener {

    private View view = null;
    private TextView check_for_updates_rb;
    private TextView textView;

    public SoftwareUpdateFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.software_update_fragment_layout, container, false);
            check_for_updates_rb = view.findViewById(R.id.check_for_updates_rb);
            check_for_updates_rb.setTypeface(CloudFrameApp.typeFace);
            check_for_updates_rb.setOnClickListener(this);
            textView = view.findViewById(R.id.textView);
            textView.setText(R.string.show_software_content);
            textView.setTypeface(CloudFrameApp.typeFace);
        }
        return view;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.check_for_updates_rb:

                break;
        }
    }
//    @RequiresPermission("android.Manifest.permission.RECOVERY")
    @Override
    public boolean onKeyDown(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                check_for_updates_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                break;
            case KeyEvent.KEYCODE_ENTER:

//                try {
//                    RecoverySystem.installPackage(getActivity(),
//                            new File(Environment.getExternalStorageDirectory().toString()+ "/update.zip"));
//                } catch (Exception e) {
//                    //java.lang.SecurityException: Neither user 10008 nor current process has android.permission.RECOVERY.
//                    Log.e("TAG","异常报错:"+e.toString());
//                }
//                com.adups.fota/.GoogleOtaClient

                Intent intent = new Intent();
                ComponentName componentName = new ComponentName("com.adups.fota","com.adups.fota.GoogleOtaClient");
                intent.setComponent(componentName);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
        }
        return ret;
    }

    @Override
    public void Select(int postion) {
        if (postion == 0) {
            check_for_updates_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else {
            check_for_updates_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        }
    }
}
