package com.sunchip.adw.cloudphotoframe.guide;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.util.DestoryActivityUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import java.util.Locale;

public class LauguageActivity extends AppCompatActivity {

    Locale loc;
    ListView mlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        setContentView(R.layout.activity_lauguage);
        DestoryActivityUtils.getInstance().addDestoryActivity(LauguageActivity.this,"LauguageActivity");
        loc = Locale.ENGLISH;
        mlistView = findViewById(R.id.vListView);
        final String[] items = {"English", "日本語", "Deutsch", "En français", "español"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, items);//listdata和str均可
        mlistView.setAdapter(arrayAdapter);
        int color = getResources().getColor(me.zhouzhuo.zzsecondarylinkage.R.color.MainBlue);
        mlistView.setSelector(new ColorDrawable(color));

        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("TAG", "position==========" + position);
                if (position == 0) {
                    //英文
                    loc = Locale.ENGLISH;
                } else if (position == 1) {
                    //日语
                    loc = Locale.JAPANESE;
                } else if (position == 2) {
                    //德语
                    loc = Locale.GERMANY;
                } else if (position == 3) {
                    //法语
                    loc = Locale.FRANCE;
                } else if (position == 4) {
                    //西班牙语
                    loc = new Locale("es", "ES");
                }
                SharedUtil.newInstance().setInt("isLanguage", position);
                SystemInterfaceUtils.getInstance().setConfiguration(LauguageActivity.this, loc, false);
                startActivity(new Intent(LauguageActivity.this, GuideWifiNewActivity.class));
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e("TAG", "keyCode=====LauguageActivity==" + keyCode);
        return super.onKeyDown(keyCode, event);
    }
}
