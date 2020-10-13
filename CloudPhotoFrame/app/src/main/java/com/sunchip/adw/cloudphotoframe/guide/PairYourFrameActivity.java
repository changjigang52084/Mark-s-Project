package com.sunchip.adw.cloudphotoframe.guide;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sunchip.adw.cloudphotoframe.InitializationActivity;
import com.sunchip.adw.cloudphotoframe.MainActivity;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpErrarCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.util.DestoryActivityUtils;
import com.sunchip.adw.cloudphotoframe.util.DeviceUtils;
import com.sunchip.adw.cloudphotoframe.util.InitializationUtils;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;
import com.sunchip.adw.cloudphotoframe.util.StatusBarUtils;
import com.sunchip.adw.cloudphotoframe.util.SystemInterfaceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.StopPlayPhotoVideo;

public class PairYourFrameActivity extends InitialActivity {


    private String Sn = DeviceUtils.getDeviceSerialNumber();
    private TextView mTextSn_a, mTextSn_b, mTextSn_c, mTextSn_d;

    private ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatusBarUtils.getInstance().ImageStausBar(this, R.color.white);
        setContentView(R.layout.activity_pair_your_frame);
        DestoryActivityUtils.getInstance().addDestoryActivity(PairYourFrameActivity.this,"PairYourFrameActivity");
        EventBus.getDefault().register(this);
        initView();
    }

    private void initView() {
        mTextSn_a = findViewById(R.id.TextSn_a);
        mTextSn_b = findViewById(R.id.TextSn_b);
        mTextSn_c = findViewById(R.id.TextSn_c);
        mTextSn_d = findViewById(R.id.TextSn_d);

        mTextSn_a.setTypeface(CloudFrameApp.typeFace);
        mTextSn_b.setTypeface(CloudFrameApp.typeFace);
        mTextSn_c.setTypeface(CloudFrameApp.typeFace);
        mTextSn_d.setTypeface(CloudFrameApp.typeFace);


        TextView title = findViewById(R.id.title);
        title.setTypeface(CloudFrameApp.typeFace);

        TextView modler = findViewById(R.id.modler);
        modler.setTypeface(CloudFrameApp.typeFace);

        TextView pair = findViewById(R.id.pair);
        pair.setTypeface(CloudFrameApp.typeFace);

        TextView please = findViewById(R.id.please);
        please.setTypeface(CloudFrameApp.typeFace);


        //显示sn号准备绑定
        mTextSn_a.setText(Sn.substring(0, 4));
        mTextSn_b.setText(Sn.substring(4, 8));
        mTextSn_c.setText(Sn.substring(8, 12));
        mTextSn_d.setText(Sn.substring(12, 16));

        imageView = findViewById(R.id.erweima);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handler.removeMessages(0x123);
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //无限查询内容
        handler.sendEmptyMessage(0x123);
    }

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            handler.removeMessages(0x123);
            HttpRequestAuxiliary.getInstance().getheartbest(InterFaceCode.Get_Holder_Resources);
            handler.sendEmptyMessageDelayed(0x123, 2 * 1000);
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void PairYourFrameEvent(BaseErrarEvent event) {
        switch (event.getCode()) {
            case InterFaceCode.Get_Holder_Resources:
                //为空的时候直接设置为false
                SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, false);
                try {
                    JSONObject jsonObject = new JSONObject(event.getResult());
                    int code = jsonObject.optInt("err_code");
                    if (code == HttpErrarCode.RESULT_CODE_FRAME_UNPAIR) {
                        SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, false);
                    } else if (code == HttpErrarCode.RESULT_CODE_SUCCESS) {
                        //成功操作 获取信息内容
                        SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, true);
                        HttpRequestAuxiliary.getInstance().setSetting(123, this);
                        handler.removeMessages(0x123);
                        if(System.currentTimeMillis()-aLong>=3000){
                            aLong = System.currentTimeMillis();
                            startActivity(new Intent(PairYourFrameActivity.this, MainActivity.class));
                        }
//                        PairYourFrameActivity.this.finish();
                        DestoryActivityUtils.getInstance().destoryActivity();
                        //第一次绑定初始化需要重置状态
                        InitializationUtils.getInstance().setInitialization(this);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
    long aLong = 0;
}
