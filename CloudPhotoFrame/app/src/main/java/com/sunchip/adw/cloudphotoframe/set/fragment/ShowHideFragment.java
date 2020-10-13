package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

public class ShowHideFragment extends BaseFragment {

    private View view = null;

    private TextView show_clock_rb;
    private String[] show_clock;

    private TextView show_caption_rb;
    private String[] show_captio;
    //默认选中内容
    private int Select = 0;

    public AlertDialog dialog;

    public ShowHideFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();

        show_clock = getActivity().getResources().getStringArray(R.array.showhide);
        String showClock = show_clock[SharedUtil.newInstance().getInt("showClock")];
        show_clock_rb.setText(getResources().getString(R.string.show_clock) + "\r\n\r\r" + showClock);
        show_clock_rb.setTypeface(CloudFrameApp.typeFace);
        show_captio = new String[]{getResources().getString(R.string.off),
                getResources().getString(R.string.on)};
        String showCaption = show_captio[SharedUtil.newInstance().getInt("showCaption")];
        show_caption_rb.setText(getResources().getString(R.string.show_caption) + "\r\n\r\r" + showCaption);
        show_caption_rb.setTypeface(CloudFrameApp.typeFace);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.show_hide_fragment, container, false);

            show_clock_rb = view.findViewById(R.id.show_clock_rb);
            show_caption_rb = view.findViewById(R.id.show_caption_rb);
        }
//        初始化
        IntoBackground();
        return view;
    }


    /**
     * 显示时钟列表对话框
     */
    private void showClockDialog() {
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.show_clock);
        builder.setItems(show_clock, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                show_clock_rb.setText(getResources().getString(R.string.show_clock) + "\r\n\r\r" + show_clock[i]);
                SharedUtil.newInstance().setInt("showClock", i);
                if (i >= 0) {
                    SystemInterfaceUtils.getInstance().setTime24((i == 1) ? false : true);
                }
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting,getActivity());
//                showMsg("调用系统接口执行 show_clock_rb 操作.");
            }
        });
        dialog = builder.create();
        dialog .show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }

    /**
     * 显示标题列表对话框
     */
    private void showCaptionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.show_caption);
        builder.setItems(show_captio, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                show_caption_rb.setText(getResources().getString(R.string.show_caption) + "\r\n\r\r" + show_captio[i]);
                SharedUtil.newInstance().setInt("showCaption", i);
//                showMsg("调用系统接口执行 show_caption_rb 操作.");
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting,getActivity());
            }
        });
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


    //初始化颜色
    private void IntoBackground() {
        show_clock_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        show_caption_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
    }

    @Override
    public boolean onKeyDown(KeyEvent event) {
        boolean ret = false;
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_UP:
                IntoBackground();
                show_clock_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                Select = 0;
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                IntoBackground();
                show_caption_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
                Select = 1;
                break;
            case KeyEvent.KEYCODE_ENTER:
                if (Select == 0) {
                    showClockDialog();
                } else {
                    showCaptionDialog();
                }
                break;

        }
        return ret;
    }

    @Override
    public void Select(int postion) {
        if (postion == 0) {
            Select = 0;
            IntoBackground();
            show_clock_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else {
            IntoBackground();
        }
    }
}
