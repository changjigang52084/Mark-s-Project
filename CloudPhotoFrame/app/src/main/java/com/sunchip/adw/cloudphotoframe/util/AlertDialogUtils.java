package com.sunchip.adw.cloudphotoframe.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.example.Launcher.Downling.image.SunchipManager;
import com.github.lzyzsd.circleprogress.DonutProgress;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.adapter.TransitionAdpate;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.manager.AudioManger;
import com.sunchip.adw.cloudphotoframe.netty.itemListener;

import java.util.ArrayList;
import java.util.List;

public class AlertDialogUtils {
    public static AlertDialogUtils mAlertDialogUtils = new AlertDialogUtils();

    public AlertDialog dialog;

    private AlertDialog.Builder dialogBuilder;

    private DonutProgress mDonutProgress;


    public AlertDialogUtils() {

    }

    public static AlertDialogUtils getmAlertDialogUtils() {
        if (mAlertDialogUtils == null) {
            mAlertDialogUtils = new AlertDialogUtils();
        }
        return mAlertDialogUtils;
    }

    public void VolumeDialog(final Context mContext) {
        dialogBuilder = new AlertDialog.Builder(mContext, R.style.Remote_dialog);
        dialog = dialogBuilder.create();
        dialog.show();// show方法放在此处，如果先SetContentView之后在show会报错
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        StatusBarUtils.getInstance().ImageStausBarDialog((Activity) mContext, dialog);

        window.setContentView(R.layout.volume_layout);


        // 因为setContentView的原因会一直隐藏键盘 所以做一下操作默认不显示，点击显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        final TextView VolumeText = window.findViewById(R.id.MolumeText);

        SeekBar VolumeSeekBar = window.findViewById(R.id.VolumeSeekBar);

        VolumeSeekBar.setMax(AudioManger.getInstance().getmusicMaxVolume());

        VolumeSeekBar.setProgress(AudioManger.getInstance().getmusicVolume());

        VolumeText.setText(AudioManger.getInstance().getmusicVolume() + "");

        VolumeSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                VolumeText.setText(i + "");
                AudioManger.getInstance().SetMusicVolume(i, false);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface mdialog) {
                //进行你想要的操作
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, mContext);

            }
        });

        TextView ColseVolume = window.findViewById(R.id.ColseVolume);
        ColseVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DissDialog();
            }
        });
    }


    public void mshowNormalDialog(final Context mContext) {
        dialogBuilder = new AlertDialog.Builder(mContext, R.style.Remote_dialog);
        dialog = dialogBuilder.create();
        dialog.show();// show方法放在此处，如果先SetContentView之后在show会报错
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        StatusBarUtils.getInstance().ImageStausBarDialog((Activity) mContext, dialog);
        window.setContentView(R.layout.mvolume_layout);
        // 因为setContentView的原因会一直隐藏键盘 所以做一下操作默认不显示，点击显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        SunchipManager.downLoadFile(SunchipManager.ApkPath(), "Sunchip", mContext);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
    }


    public void mshowalreadyDialog(final Context mContext) {
        dialogBuilder = new AlertDialog.Builder(mContext, R.style.Remote_dialog);
        dialog = dialogBuilder.create();
        dialog.show();// show方法放在此处，如果先SetContentView之后在show会报错
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        StatusBarUtils.getInstance().ImageStausBarDialog((Activity) mContext, dialog);
        window.setContentView(R.layout.mvolume_layoutallay);
        // 因为setContentView的原因会一直隐藏键盘 所以做一下操作默认不显示，点击显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        SharedUtil.newInstance().setBoolean("Install", false);
        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                return true;
            }
        });
    }


    public void BrightnessDialog(final Context mContext) {
        dialogBuilder = new AlertDialog.Builder(mContext, R.style.Remote_dialog);
        dialog = dialogBuilder.create();
        dialog.show();// show方法放在此处，如果先SetContentView之后在show会报错
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        StatusBarUtils.getInstance().ImageStausBarDialog((Activity) mContext, dialog);
        window.setContentView(R.layout.bram_layout);
        // 因为setContentView的原因会一直隐藏键盘 所以做一下操作默认不显示，点击显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        final TextView BrightnessText = window.findViewById(R.id.MolumeText);

        final SeekBar BrightnessSeekBar = window.findViewById(R.id.BrightnessSeekBar);

        final float pressent = (float) AudioManger.getInstance().getSystemBrightness() / BrightnessSeekBar.getMax() * 100;

        BrightnessText.setText((Math.round(pressent)) + "%");
        BrightnessSeekBar.setProgress(AudioManger.getInstance().getSystemBrightness());
        BrightnessSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                BrightnessText.setText((Math.round((float) i / BrightnessSeekBar.getMax() * 100)) + "%");
                AudioManger.getInstance().setScreenBrightness(i, (Activity) mContext);

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                //进行你想要的操作
                HttpRequestAuxiliary.getInstance().setSetting(InterFaceCode.Setting, mContext);
            }
        });

        TextView ColseVolume = window.findViewById(R.id.ColseVolume);
        ColseVolume.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DissDialog();
            }
        });
    }


    public void AleraDialog(final Context mContext) {
        dialogBuilder = new AlertDialog.Builder(mContext, R.style.Remote_dialog);
        dialog = dialogBuilder.create();
        dialog.show();// show方法放在此处，如果先SetContentView之后在show会报错
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        StatusBarUtils.getInstance().ImageStausBarDialog((Activity) mContext, dialog);

        window.setContentView(R.layout.showom);
        // 因为setContentView的原因会一直隐藏键盘 所以做一下操作默认不显示，点击显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {
                    sleep(3000);
                    SystemInterfaceUtils.getInstance().SetAdbKeyevent("reboot\n");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }


    public void RunDiagnostic(final Context mContext) {
        dialogBuilder = new AlertDialog.Builder(mContext, R.style.Remote_dialog);
        dialog = dialogBuilder.create();
        dialog.show();// show方法放在此处，如果先SetContentView之后在show会报错
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        StatusBarUtils.getInstance().ImageStausBarDialog((Activity) mContext, dialog);
        window.setContentView(R.layout.rundiagnostic);
        // 因为setContentView的原因会一直隐藏键盘 所以做一下操作默认不显示，点击显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        final ImageView NewWorkImage = window.findViewById(R.id.NetWorkImage);
        final ProgressBar NewWorkProg = window.findViewById(R.id.NetWorkProg);
        final ImageView ServerImage = window.findViewById(R.id.ServerImage);
        final ProgressBar ServerProg = window.findViewById(R.id.ServerProg);
        final ImageView PhotoImage = window.findViewById(R.id.PhotoImage);
        final ProgressBar PhotoProg = window.findViewById(R.id.PhotoProg);
        boolean IsNetWork = NetworkUtrl.getInstance().isNetworkAvailable(mContext);
        NewWorkImage.setBackgroundResource(IsNetWork ? R.drawable.yes : R.drawable.not);
        ServerImage.setBackgroundResource(IsNetWork ? R.drawable.yes : R.drawable.not);
        PhotoImage.setBackgroundResource(CloudFrameApp.IsSocketPhone ? R.drawable.yes : R.drawable.not);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                NewWorkImage.setVisibility(View.VISIBLE);
                NewWorkProg.setVisibility(View.GONE);
            }
        }, 2000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ServerImage.setVisibility(View.VISIBLE);
                ServerProg.setVisibility(View.GONE);
            }
        }, 4000);
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                PhotoProg.setVisibility(View.GONE);
                PhotoImage.setVisibility(View.VISIBLE);
            }
        }, 6000);


    }

    public AlertDialog TransitionType(final Context mContext, final itemListener mitemListener) {
        dialogBuilder = new AlertDialog.Builder(mContext, R.style.Remote_dialog_full);
        dialog = dialogBuilder.create();
        dialog.show();// show方法放在此处，如果先SetContentView之后在show会报错
        Window window = dialog.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        window.setAttributes(lp);
        StatusBarUtils.getInstance().ImageStausBarDialog((Activity) mContext, dialog);
        window.setContentView(R.layout.transition_type);
        // 因为setContentView的原因会一直隐藏键盘 所以做一下操作默认不显示，点击显示
        window.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        final int color = mContext.getResources().getColor(R.color.MainBlue);

        final ListView listView = window.findViewById(R.id.TransitionListview);
        final ListView listView1 = window.findViewById(R.id.TypeListview);

        final List<String> datalist = new ArrayList<>();
        final List<String> datalisttype = new ArrayList<>();

        final int[] selected = {0};
        final TransitionAdpate mTransitionAdpate = new TransitionAdpate(mContext);
        final TransitionAdpate mTransitionAdpate1 = new TransitionAdpate(mContext);

        String[] data = mContext.getResources().getStringArray(R.array.AlphaAnimation_type);
        String[] data1 = mContext.getResources().getStringArray(R.array.AlphaAnimation);

        for (int i = 0; i <= data.length - 1; i++) {    //创建 18  个 map 数据对象 ，每个map 对象 有两个键值数据
            datalist.add(data[i]);
        }
        mTransitionAdpate.setList(datalist);
        for (int i = 0; i <= data1.length - 1; i++) {    //创建 18  个 map 数据对象 ，每个map 对象 有两个键值数据
            datalisttype.add(data1[i]);
        }
        mTransitionAdpate1.setList(datalisttype);
        //将数据和布局 显示到列表
        listView1.setAdapter(mTransitionAdpate);
        listView.setAdapter(mTransitionAdpate1);

        listView.setSelector(new ColorDrawable(color));
        listView1.setSelector(new ColorDrawable(color));
        int transitionType = SharedUtil.newInstance().getInt("transitionType");
        int transitionType1 = SharedUtil.newInstance().getInt("transitionType1");
        mTransitionAdpate1.setselected(transitionType1);
        mTransitionAdpate.setselected(transitionType);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTransitionAdpate1.setselected(position);
                mTransitionAdpate.setselected(0);
                mitemListener.onItemListen(position, 0);
                selected[0] = position;
                Log.e("TAG", "左侧列表:" + datalisttype.get(position));
            }
        });

        listView1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mTransitionAdpate.setselected(position);
                mitemListener.onItemListen(selected[0], position);
                Log.e("TAG", "右侧列表:" + datalist.get(position));
            }
        });
        return dialog;
    }


    private Handler mHandler = new Handler();

    //关闭dialog
    public void DissDialog() {
        if (dialog != null) {
            dialog.dismiss();
        }
    }

}
