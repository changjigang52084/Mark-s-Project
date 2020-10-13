package jpushdemo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.BaseErrarEvent;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode;
import com.sunchip.adw.cloudphotoframe.http.request.HttpRequestAuxiliary;
import com.sunchip.adw.cloudphotoframe.util.SharedUtil;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import static com.sunchip.adw.cloudphotoframe.http.request.Bean.InterFaceCode.isAllow_Binding;

/**
 * 自定义接收器
 * <p>
 * 如果不定义这个 Receiver，则：
 * 1) 默认用户会打开主界面
 * 2) 接收不到自定义消息
 */
public class MyReceiver extends BroadcastReceiver {
    private static final String TAG = "JIGUANG-Example";

    //1绑定，2解绑，3相框设置，4相片变化
    public static final int JPUSH_PAIR = 1;
    public static final int JPUSH_UNPAIR = 2;
    public static final int JPUSH_FRAME_SETTING = 3;
    public static final int JPUSH_ADD_PHOTO = 4;

    public static final int HANDLER_JPUSH_ADD_PHOTO = 0x124;

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            Bundle bundle = intent.getExtras();
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
//				Log.d(TAG, "[MyReceiver] 接收Registration Id : " + regId);
//				send the Registration Id to your server...

            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {
//				Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));

                String msg = bundle.getString(JPushInterface.EXTRA_MESSAGE);

                JSONObject jsonObject = new JSONObject(msg);
                int code = jsonObject.getInt("message");
                Log.d(TAG, "[MyReceiver] 接收到推送下来的自定义消息的状态值是: " + code);

                if (code == JPUSH_PAIR) {
                    //相框绑定
                } else if ((code == JPUSH_UNPAIR)) {
                    //相框解除绑定
                    SharedUtil.newInstance().setBoolean(InterFaceCode.IsItBound, false);
                    EventBus.getDefault().post(new BaseErrarEvent("", isAllow_Binding));
                } else if ((code == JPUSH_FRAME_SETTING)) {
                    //相框设置 更新本地的值
                    HttpRequestAuxiliary.getInstance().getheartbest(InterFaceCode.Get_Holder_Resources);
                } else if ((code == JPUSH_ADD_PHOTO)) {
                    //新增相片 去刷新一遍列表
                    //防止频繁刷新 去掉一次
                    handler.removeMessages(HANDLER_JPUSH_ADD_PHOTO);
                    handler.sendEmptyMessageDelayed(HANDLER_JPUSH_ADD_PHOTO, 10000);
                }
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
//				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知");
                int notifactionId = bundle.getInt(JPushInterface.EXTRA_NOTIFICATION_ID);
//				Log.d(TAG, "[MyReceiver] 接收到推送下来的通知的ID: " + notifactionId);

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {
//				Log.d(TAG, "[MyReceiver] 用户点击打开了通知");

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
//				Log.d(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..

            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
//				Log.w(TAG, "[MyReceiver]" + intent.getAction() +" connected state change to "+connected);
            } else {
//				Log.d(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {

        }
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.e("TAG","刷新相框列表=========================================");
            HttpRequestAuxiliary.getInstance().getPlayList(InterFaceCode.Get_Playback_Resources);
        }
    };
}