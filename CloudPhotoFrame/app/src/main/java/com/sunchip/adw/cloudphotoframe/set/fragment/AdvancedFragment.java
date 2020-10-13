package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.HttpURLUtils;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpErrarCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.util.AlertDialogUtils;
import com.sunchip.adw.cloudphotoframe.util.InitializationUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

public class AdvancedFragment extends BaseFragment {

    private static final String TAG = "AdvancedFragment";

    private View view = null;


    private TextView run_diagnostic_rb;

    private TextView resync_frame_rb;

    private int Select = 0;
    public AlertDialog dialog;
    public AdvancedFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.advanced_fragment_layout, container, false);
            run_diagnostic_rb = view.findViewById(R.id.run_diagnostic_rb);
            resync_frame_rb = view.findViewById(R.id.resync_frame_rb);

            run_diagnostic_rb.setTypeface(CloudFrameApp.typeFace);
            resync_frame_rb.setTypeface(CloudFrameApp.typeFace);

        }
        return view;
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void AdvancedAppEvent(BaseErrarEvent event) {
        switch (event.getCode()) {
            case InterFaceCode.Resyne_Frame:
                SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, true);
                try {
                    JSONObject jsonObject = new JSONObject(event.getResult());
                    int code = jsonObject.optInt("err_code");
                    if (code == HttpErrarCode.RESULT_CODE_SUCCESS) {
                        SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, false);
                        //操作成功以后初始化所有状态
                        InitializationUtils.getInstance().setInitialization(getActivity());
                        //再删除所有照片
                        HttpURLUtils.deleteDirWihtFile(new File(CloudFrameApp.BASE));
                    } else {
                        //操作失败
                        SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                //不管成功失败 都需要重启一遍
                AlertDialogUtils.getmAlertDialogUtils().AleraDialog(getActivity());
                break;
        }
    }


    /**
     * 恢复默认显示设置列表对话框
     */
    private void resyncFrameDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.resync_frame);
        builder.setPositiveButton(getResources().getString(R.string.comitout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                HttpRequestAuxiliary.getInstance().ResyneFrame(InterFaceCode.Resyne_Frame);
            }
        });
        builder.setNegativeButton(getResources().getString(R.string.comitput), null);
        dialog = builder.create();
        dialog .show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onKeyDown(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                Select--;
                if (Select <= 0) {
                    Select = 0;
                }
                setBackgrount(Select);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Select++;
                if (Select >= 1) {
                    Select = 1;
                }
                setBackgrount(Select);
                break;
            case KeyEvent.KEYCODE_ENTER:
                SetOnclen();
                break;

        }
        return ret;
    }

    //初始化颜色
    private void IntoBackground() {
        run_diagnostic_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
//        hotspot_test_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        resync_frame_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
    }

    private void setBackgrount(int select) {
        IntoBackground();
        if (select == 0) {
            run_diagnostic_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
//        } else if (select == 1) {
//            hotspot_test_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (select ==1) {
            resync_frame_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        }
    }

    private void SetOnclen() {
        if (Select == 0) {
//            showMsg("暂时搁置");
            AlertDialogUtils.getmAlertDialogUtils().RunDiagnostic(getActivity());
        }/* else if (Select == 1) {
            showMsg("暂时搁置");
        }*/ else if (Select == 1) {
            resyncFrameDialog();
        }
    }


    @Override
    public void Select(int postion) {
        if (postion == 0) {
            Select = 0;
            IntoBackground();
            run_diagnostic_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else {
            IntoBackground();
        }
    }
}
