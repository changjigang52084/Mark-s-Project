package com.sunchip.adw.cloudphotoframe.guide;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.receiver.ScreenOffAdminReceiver;
import com.sunchip.adw.cloudphotoframe.util.DestoryActivityUtils;
import com.sunchip.adw.cloudphotoframe.util.PermissionUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.Locale;

import static com.sunchip.adw.cloudphotoframe.app.CloudFrameApp.policyManager;
import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.StopPlayPhotoVideo;

/*
 * 引导页
 * */

public class GuideActivity extends AppCompatActivity {

    private ImageView imageView;
    Locale loc;
    private TextView textView;
    ComponentName adminReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        setContentView(R.layout.activity_guide);
        DestoryActivityUtils.getInstance().addDestoryActivity(GuideActivity.this,"GuideActivity");
        imageView = findViewById(R.id.Beijinshezhi);
        textView = findViewById(R.id.LocalText);
        TextView welcome = findViewById(R.id.welcome);
        welcome.setTypeface(CloudFrameApp.typeFace);
        textView.setTypeface(CloudFrameApp.typeFace);
        TextView lastwelcome = findViewById(R.id.lastwelcome);
        lastwelcome.setTypeface(CloudFrameApp.typeFace);
        registerPermission();
        isOpen();
    }



    private void LanguageInto() {
        int isLanguage = SharedUtil.newInstance().getInt("isLanguage", 0);
        if (isLanguage == 0) {
            //英文
            textView.setText(R.string.english);
            loc = Locale.ENGLISH;
        } else if (isLanguage == 1) {
            //日语
            textView.setText(R.string.Japan);
            loc = Locale.JAPANESE;
        } else if (isLanguage == 2) {
            //德语
            textView.setText(R.string.Deutsch);
            loc = Locale.GERMANY;
        } else if (isLanguage == 3) {
            //法语
            textView.setText(R.string.Enfran);
            loc = Locale.FRANCE;
        } else if (isLanguage == 4) {
            //西班牙语
            textView.setText(R.string.espaol);
            loc = new Locale("es", "ES");
        }
        SystemInterfaceUtils.getInstance().setConfiguration(GuideActivity.this, loc, false);
    }

    private void registerPermission() {
        PermissionUtils.getInstance().Permission(this);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isOpen();
    }

    /**
     * 检测用户是否开启了超级管理员
     */
    private boolean isOpen() {
        //申请锁屏权限
        adminReceiver = new ComponentName(this, ScreenOffAdminReceiver.class);
        if (policyManager.isAdminActive(adminReceiver)) {//判断超级管理员是否激活
            return true;
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminReceiver);
            intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "开启后就可以使用锁屏功能了...");
            startActivityForResult(intent, 0);
            return false;
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
        LanguageInto();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_ENTER) {
            //进入wifi界面
            startActivity(new Intent(GuideActivity.this, GuideWifiNewActivity.class));
            return false;
        }

//        //上键切换语音
        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            setlanguageDialog();
        }

        if (keyCode == 349) {
            //人工息屏
            SharedUtil.newInstance().setInt("vMensor", 1);
            EventBus.getDefault().post(new BaseErrarEvent("", StopPlayPhotoVideo));
            if (CloudFrameApp.IsScreen) {
//                Ismedia = false;
                SystemInterfaceUtils.getInstance().setScreen(false);
            } else {
//                Ismedia = true;
                SystemInterfaceUtils.getInstance().setScreen(true);
            }
            return super.onKeyDown(keyCode, event);
        }
        return true;
    }

    public AlertDialog dialog;

    private void setlanguageDialog() {
        final String[] items = {"English", "日本語", "Deutsch", "En français", "español"};
        AlertDialog.Builder builder = new AlertDialog.Builder(GuideActivity.this);
        builder.setTitle(R.string.when_frame_wakes_up);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (i == 0) {
                    //英文
                    textView.setText(R.string.english);
                    loc = Locale.ENGLISH;
                } else if (i == 1) {
                    //日语
                    textView.setText(R.string.Japan);
                    loc = Locale.JAPANESE;
                } else if (i == 2) {
                    //德语
                    textView.setText(R.string.Deutsch);
                    loc = Locale.GERMANY;
                } else if (i == 3) {
                    //法语
                    textView.setText(R.string.Enfran);
                    loc = Locale.FRANCE;
                } else if (i == 4) {
                    //西班牙语
                    textView.setText(R.string.espaol);
                    loc = new Locale("es", "ES");
                }
                SharedUtil.newInstance().setInt("isLanguage", i);
                SystemInterfaceUtils.getInstance().setConfiguration(GuideActivity.this, loc, true);
            }
        });
        dialog = builder.create();

        StatusBarUtils.getInstance().ImageStausBarDialog(GuideActivity.this, dialog);

        dialog.show();
    }
}
