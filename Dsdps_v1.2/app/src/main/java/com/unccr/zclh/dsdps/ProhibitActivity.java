package com.unccr.zclh.dsdps;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;


import com.unccr.zclh.dsdps.util.SharedUtil;

public class ProhibitActivity extends Activity {

    private static final String TAG = "ProhibitActivity";

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prohibit);
        SharedUtil.newInstance().setBoolean("isProhibit",true);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                Log.d(TAG, "keyCode: " +keyCode);
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"onDestroy.");
    }
}
