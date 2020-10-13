package com.sunchip.adw.cloudphotoframe.set.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.netty.itemListener;
import com.sunchip.adw.cloudphotoframe.util.AlertDialogUtils;
import com.sunchip.adw.cloudphotoframe.util.FileUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;

import java.io.File;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.NotDialogSystemUI;

public class SettingsFragment extends BaseFragment {

    private static final String TAG = "SettingsFragment";

    private View view = null;

    private TextView playback_mode_rb;
    private String[] playback_mode;

    private TextView shuffle_rb;
    private String[] shuffle;

    private TextView transition_type_rb;
    private String[] transition_type;
    private String[] transition_type1;


    private TextView transition_time_rb;
    private String[] transition_time;

    private TextView motion_sensor_rb;
    private String[] motion_sensor;

    private TextView when_frame_goes_to_sleep_rb;
    private String[] when_frame_goes_to_sleep;

    private TextView background_music_rb;
    private String[] background_music;
    public AlertDialog dialog;

    //默认选中
    private int Select = 0;


    public SettingsFragment() {

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        playback_mode = getActivity().getResources().getStringArray(R.array.photo_setting);
        String playbackMode = playback_mode[SharedUtil.newInstance().getInt("playbackMode")];
        playback_mode_rb.setText(getResources().getString(R.string.playback_mode) + "\r\n\r\r" + playbackMode);

        shuffle = new String[]{getResources().getString(R.string.off), getResources().getString(R.string.on)};
        String mShuffle = shuffle[SharedUtil.newInstance().getInt("shuffle")];
        shuffle_rb.setText(getResources().getString(R.string.shuffle) + "\r\n\r\r" + mShuffle);

        transition_type = getActivity().getResources().getStringArray(R.array.AlphaAnimation_type);
        transition_type1 = getActivity().getResources().getStringArray(R.array.AlphaAnimation);
        String transitionType = transition_type[SharedUtil.newInstance().getInt("transitionType")];
        String transitionType1 = transition_type1[SharedUtil.newInstance().getInt("transitionType1")];
        transition_type_rb.setText(getResources().getString(R.string.transition_type) + "\r\n\r\r" + transitionType1 + "-" + transitionType);

        transition_time = getActivity().getResources().getStringArray(R.array.time);
        String transitionTime = transition_time[SharedUtil.newInstance().getInt("transitionTime", 1)];
        transition_time_rb.setText(getResources().getString(R.string.transition_time) + "\r\n\r\r" + transitionTime);

        motion_sensor = new String[]{getResources().getString(R.string.off), getResources().getString(R.string.on)};
        String motionSensor = motion_sensor[SharedUtil.newInstance().getInt("motionSensor")];
        motion_sensor_rb.setText(getResources().getString(R.string.motion_sensor) + "\r\n\r\r" + motionSensor);

        when_frame_goes_to_sleep = getActivity().getResources().getStringArray(R.array.clock);
        String whenFrameGoesToSleep = when_frame_goes_to_sleep[SharedUtil.newInstance().getInt("whenFrameGoesToSleep")];
        when_frame_goes_to_sleep_rb.setText(getResources().getString(R.string.when_frame_goes_to_sleep) + "\r\n\r\r" + whenFrameGoesToSleep);


        background_music = getDataMusic();
        String Music = SharedUtil.newInstance().getString("MusicSetting");
        if (TextUtils.isEmpty(Music))
            background_music_rb.setText("Background Music" + "\r\n\r\r" + CloudFrameApp.getCloudFrameApp().
                    getResources().getString(R.string.on));
        else background_music_rb.setText("Background Music" + "\r\n\r\r" + Music);

        playback_mode_rb.setTypeface(CloudFrameApp.typeFace);
        shuffle_rb.setTypeface(CloudFrameApp.typeFace);
        transition_type_rb.setTypeface(CloudFrameApp.typeFace);
        transition_time_rb.setTypeface(CloudFrameApp.typeFace);
        motion_sensor_rb.setTypeface(CloudFrameApp.typeFace);
        when_frame_goes_to_sleep_rb.setTypeface(CloudFrameApp.typeFace);
        background_music_rb.setTypeface(CloudFrameApp.typeFace);
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.settings_fragment_layout, container, false);
            playback_mode_rb = view.findViewById(R.id.playback_mode_rb);
            shuffle_rb = view.findViewById(R.id.shuffle_rb);
            transition_type_rb = view.findViewById(R.id.transition_type_rb);
            transition_time_rb = view.findViewById(R.id.transition_time_rb);
            motion_sensor_rb = view.findViewById(R.id.motion_sensor_rb);
            when_frame_goes_to_sleep_rb = view.findViewById(R.id.when_frame_goes_to_sleep_rb);
            background_music_rb = view.findViewById(R.id.background_music);
        }
        return view;
    }

    /**
     * playbackMode列表对话框
     */
    private void playbackModeDialog() {
        ;
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.playback_mode);
        builder.setItems(playback_mode, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                playback_mode_rb.setText(getResources().getString(R.string.playback_mode) + "\r\n\r\r" + playback_mode[i]);
                SharedUtil.newInstance().setInt("playbackMode", i);
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
        dialog = builder.create();
        dialog.show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }

    /**
     * shuffle列表对话框
     */
    private void shuffleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.shuffle);
        builder.setItems(shuffle, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                shuffle_rb.setText(getResources().getString(R.string.shuffle) + "\r\n\r\r" + shuffle[i]);
                SharedUtil.newInstance().setInt("shuffle", i);
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
        dialog = builder.create();
        dialog.show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }

    /**
     * 过渡类型列表对话框
     */
    private void transitionTypeDialog() {

        dialog = AlertDialogUtils.getmAlertDialogUtils().TransitionType(getActivity(), new itemListener() {
            @Override
            public void onItemListen(int i1, int i2) {
                transition_type_rb.setText(getResources().getString(R.string.transition_type) + "\r\n\r\r" + transition_type1[i1] + "-" +
                        transition_type[i2]);
                SharedUtil.newInstance().setInt("transitionType", i2);
                SharedUtil.newInstance().setInt("transitionType1", i1);
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
    }

    /**
     * 过渡时间列表对话框
     */
    private void transitionTimeDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.transition_time);
        builder.setItems(transition_time, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                transition_time_rb.setText(getResources().getString(R.string.transition_time) + "\r\n\r\r" + transition_time[i]);
                SharedUtil.newInstance().setInt("transitionTime", i);
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
        dialog = builder.create();
        dialog.show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }


    /**
     * 多久没有感应到人体后熄屏列表对话框
     */
    private void motionSensorDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.motion_sensor);
        builder.setItems(motion_sensor, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                motion_sensor_rb.setText(getResources().getString(R.string.motion_sensor) + "\r\n\r\r" + motion_sensor[i]);
                SharedUtil.newInstance().setInt("motionSensor", i);
                SystemInterfaceUtils.getInstance().setPIR((i == 0) ? false : true, 10);
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
        dialog = builder.create();
        dialog.show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);

    }

    /**
     * 当相框进入睡眠状态时列表对话框
     */
    private void whenFrameGoesToSleepDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(R.string.when_frame_goes_to_sleep);
        builder.setItems(when_frame_goes_to_sleep, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                when_frame_goes_to_sleep_rb.setText(
                        getResources().getString(R.string.when_frame_goes_to_sleep) + "\r\n\r\r" + when_frame_goes_to_sleep[i]);
                SharedUtil.newInstance().setInt("whenFrameGoesToSleep", i);
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
        dialog = builder.create();
        dialog.show();
        StatusBarUtils.getInstance().ImageStausBarDialog(getActivity(), dialog);
    }

    /*
     * 选择背景音乐
     * */
//
//    1.0.11更改:
//            1. 完善acttimeout系统广播
//2. 增加媒体预装:
//            /data/pre_sel_del (用户可以删除)
///data/pre_sel  (用户不能删除)

    //获取文件夹里面的所有音乐文件
    public String[] getDataMusic() {
        String[] Music = new String[2];
        Music[0] = getResources().getString(R.string.off);
        Music[1] = getResources().getString(R.string.on);
        return Music;
    }

    private void background_musicDialog() {
        //获取本地的音乐数据组
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("background music");
        builder.setItems(background_music, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                background_music_rb.setText("Background Music" + "\r\n\r\r" + background_music[i]);

                SharedUtil.newInstance().setString("MusicSetting", background_music[i]);
//                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, getActivity());
            }
        });
        dialog = builder.create();
        dialog.show();
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
        playback_mode_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        shuffle_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        transition_type_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        transition_time_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        motion_sensor_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        when_frame_goes_to_sleep_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
        background_music_rb.setBackgroundResource(R.drawable.radiobutton_background_unchecked);
    }

    private void SetSelect() {
        if (Select == 0) {
            playbackModeDialog();
        } else if (Select == 1) {
            shuffleDialog();
        } else if (Select == 2) {
            transitionTypeDialog();
        } else if (Select == 3) {
            transitionTimeDialog();
        } else if (Select == 4) {
            motionSensorDialog();
        } else if (Select == 5) {
            whenFrameGoesToSleepDialog();
        } else if (Select == 6) {
            background_musicDialog();
        }
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface mdialog) {
                //进行你想要的操作
                EventBus.getDefault().post(new BaseErrarEvent("", NotDialogSystemUI));
            }
        });
    }

    private void setBackgrount(int select) {
        IntoBackground();
        if (select == 0) {
            playback_mode_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (select == 1) {
            shuffle_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (select == 2) {
            transition_type_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (select == 3) {
            transition_time_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (select == 4) {
            motion_sensor_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (select == 5) {
            when_frame_goes_to_sleep_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        } else if (select == 6) {
            background_music_rb.setBackgroundResource(R.drawable.radiobutton_background_checked);
        }
    }

    @Override
    public boolean onKeyDown(KeyEvent event) {
//        Log.e("TAG", "onKeyDown Select:" + Select + " event :" + event.getKeyCode());
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
                if (Select >= 6) {
                    Select = 6;
                }
                setBackgrount(Select);
                break;
            case KeyEvent.KEYCODE_ENTER:
                SetSelect();
                break;
        }
        return ret;
    }

    @Override
    public void Select(int postion) {
        if (postion == 0) {
            Select = 0;
            setBackgrount(postion);
        } else {
            IntoBackground();
        }
    }
}
