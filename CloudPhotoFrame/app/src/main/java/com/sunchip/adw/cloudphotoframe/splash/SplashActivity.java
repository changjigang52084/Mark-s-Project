package com.sunchip.adw.cloudphotoframe.splash;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.sunchip.adw.cloudphotoframe.MainActivity;
import com.sunchip.adw.cloudphotoframe.guide.GuideActivity;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import java.util.Locale;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LanguageInto();
        Intent intent;
        //判断是否绑定了 如果没有绑定则直接显示绑定界面 如果绑定了就显示主界面
        boolean IsItBound = SharedUtil.newInstance().getBoolean(InterFaceCode.IsItBound);
        if (IsItBound) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
        } else {
            //  未绑定
            intent = new Intent(getApplicationContext(), GuideActivity.class);
        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void LanguageInto() {
        Locale loc = null;
        int isLanguage = SharedUtil.newInstance().getInt("isLanguage", 0);
        if (isLanguage == 0) {
            //英文
            loc = Locale.ENGLISH;
        } else if (isLanguage == 1) {
            //日语
            loc = Locale.JAPANESE;
        } else if (isLanguage == 2) {
            //德语
            loc = Locale.GERMANY;
        } else if (isLanguage == 3) {
            //法语
            loc = Locale.FRANCE;
        } else if (isLanguage == 4) {
            //西班牙语
            loc = new Locale("es", "ES");
        }
        SystemInterfaceUtils.getInstance().setConfiguration(SplashActivity.this, loc, false);
    }
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
