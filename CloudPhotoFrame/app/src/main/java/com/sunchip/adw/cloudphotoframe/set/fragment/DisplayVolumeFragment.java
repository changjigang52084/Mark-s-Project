package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sunchip.adw.cloudphotoframe.MainActivity;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.manager.AudioManger;
import com.sunchip.adw.cloudphotoframe.splash.SplashActivity;
import com.sunchip.adw.cloudphotoframe.util.AlertDialogUtils;
import com.sunchip.adw.cloudphotoframe.util.InitializationUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.isAllow_Binding;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.languagetype;

public class DisplayVolumeFragment extends BaseFragment {

    private static final String TAG = "DisplayVolumeFragment";
    private View view = null;
    public AlertDialog dialog;
    private TextView language_type_tv;

    RelativeLayout mlanguage_rl, msetting_volume_rl, msetting_brightness_rl;

    Locale loc;
    private TextView restore_default_display_settings_rb;


    private int Select = 0;


    public DisplayVolumeFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.display_volume_fragment_layout, container, false);
            language_type_tv = view.findViewById(R.id.language_type_tv);
            mlanguage_rl = view.findViewById(R.id.language_rl);
            msetting_volume_rl = view.findViewById(R.id.setting_volume_rl);
            msetting_brightness_rl = view.findViewById(R.id.setting_brightness_rl);
            restore_default_display_settings_rb = view.findViewById(R.id.restore_default_display_settings_rb);


            TextView textView = view.findViewById(R.id.language_tv);
            textView.setTypeface(CloudFrameApp.typeFace);

            TextView setting_volume_tv = view.findViewById(R.id.setting_volume_tv);
            setting_volume_tv.setTypeface(CloudFrameApp.typeFace);

            TextView setting_brightness_tv = view.findViewById(R.id.setting_brightness_tv);
            setting_brightness_tv.setTypeface(CloudFrameApp.typeFace);
            restore_default_display_settings_rb.setTypeface(CloudFrameApp.typeFace);


            int Language = SharedUtil.newInstance().getInt("isLanguage", 0);
            if (Language == 0) {
                language_type_tv.setText("English");
            } else if (Language == 1) {
                language_type_tv.setText("日本語");
            } else if (Language == 2) {
                language_type_tv.setText("Deutsch");
            } else if (Language == 3) {
                //法语
                language_type_tv.setText(R.string.Enfran);
            } else if (Language == 4) {
                //西班牙语
                language_type_tv.setText(R.string.espaol);
            }

            language_type_tv.setTypeface(CloudFrameApp.typeFace);
        }
        return view;
    }

    //初始化颜色
    private void IntoBackground() {
        mlanguage_rl.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        msetting_volume_rl.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        msetting_brightness_rl.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        restore_default_display_settings_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
    }

    private void Selected() {
        IntoBackground();
        if (Select == 0) {
            mlanguage_rl.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (Select == 1) {
            msetting_volume_rl.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (Select == 2) {
            msetting_brightness_rl.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (Select == 3) {
            restore_default_display_settings_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        }
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
                Selected();
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                Select++;
                if (Select >= 3) {
                    Select = 3;
                }
                Selected();
                break;
            case KeyEvent.KEYCODE_ENTER:
                Seecltd();
                break;

        }
        return ret;
    }

    private void Seecltd() {
        if (Select == 0) {
            //第一个
            setlanguageDialog();
        } else if (Select == 1) {
            AlertDialogUtils.getmAlertDialogUtils().VolumeDialog(getActivity());
        } else if (Select == 2) {
            //亮度
            AlertDialogUtils.getmAlertDialogUtils().BrightnessDialog(getActivity());
        } else if (Select == 3) {
            restoreDefaultDisplaySettingsDialog();
        }
    }

    private void setlanguageDialog() {
        final String[] items = {"English", "日本語", "Deutsch", "En français", "español"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.when_frame_wakes_up);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (i == 0) {
                    //英文
                    language_type_tv.setText(R.string.english);
                    loc = Locale.ENGLISH;
                } else if (i == 1) {
                    //日语
                    language_type_tv.setText(R.string.Japan);
                    loc = Locale.JAPANESE;
                } else if (i == 2) {
                    //德语
                    language_type_tv.setText(R.string.Deutsch);
                    loc = Locale.GERMANY;
                } else if (i == 3) {
                    //法语
                    language_type_tv.setText(R.string.Enfran);
                    loc = Locale.FRANCE;
                } else if (i == 4) {
                    //西班牙语
                    language_type_tv.setText(R.string.espaol);
                    loc = new Locale("es", "ES");
                }

                SharedUtil.newInstance().setInt("isLanguage", i);
                SystemInterfaceUtils.getInstance().setConfiguration(getActivity(), loc, false);

                //切换语言需要重新刷新,否则不生效
                getActivity().finish();
                //重启 apk
                EventBus.getDefault().post(new BaseErrarEvent("", languagetype));

            }
        });
        dialog = builder.create();
        dialog .show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }


    @Override
    public void Select(int postion) {
        if (postion == 0) {
            Select = 0;
            Selected();
        } else {
            IntoBackground();
        }
    }

    /**
     * 恢复默认显示设置列表对话框
     */
    private void restoreDefaultDisplaySettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.restore_default_display_settings);
        builder.setPositiveButton(getResources().getString(R.string.comitout), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                InitializationUtils.getInstance().setInitialization(getActivity());
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
}
