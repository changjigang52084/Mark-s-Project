package com.xunixianshi.vrshow.my;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.TextMessageBody;
import com.hch.utils.MLog;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.util.StringUtils;
import com.hch.viewlib.widget.view.CircleImageView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;
import com.zhy.http.okhttp.utils.PicassoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * TODO VRShow客服页面
 *
 * @author MarkChang
 * @ClassName MyCustomerServiceActivity
 * @time 2016/11/1 15:48
 */
public class MyCustomerServiceActivity extends BaseAct {

    private static final int UPDATE_ADPATER = 1000;
    private static final int SHOW_DIALOG = 1001;

    @Bind(R.id.main_vrShow_customer_tv)
    TextView mainVrShowCustomerTv;
    @Bind(R.id.main_vrShow_customer_back_iv)
    ImageView mainVrShowCustomerBackIv;
    @Bind(R.id.customer_message_listview_lv)
    ListView customerMessageListview;
    @Bind(R.id.custom_send_message_et)
    EditText custom_send_message_et;
    @Bind(R.id.custom_send_message_defult_icon_iv)
    CircleImageView custom_send_message_defult_icon_iv;
    @Bind(R.id.custom_send_rl)
    RelativeLayout custom_send_rl;
    private EMConversation conversation;
    private String toChatUsername = "vampire";//接收方
    private DataAdapter adapter;
    private boolean isSuccess = false;         //是否登录客服系统成功
    private String customerUserName;//与客服聊天的帐号
    private String customerPassWord;//与客服聊天的密码
    private HintDialog mHintDialog;
    private boolean isLogin = false;
    private NewMessageBroadcastReceiver msgReceiver;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE_ADPATER:
                    adapter.notifyDataSetChanged();
                    customerMessageListview.setSelection(customerMessageListview.getCount() - 1);
                    break;
                case SHOW_DIALOG:
                    showHintDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_customer_service);
        ButterKnife.bind(MyCustomerServiceActivity.this);
    }

    @Override
    protected void initView() {
        super.initView();
        customerMessageListview.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (null != getCurrentFocus()) {
                    /**
                     * 点击空白位置 隐藏软键盘
                     */
                    InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    return mInputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                }
                return false;
            }
        });
    }

    @OnClick(R.id.main_vrShow_customer_back_iv)
    void main_vrShow_customer_back_iv() {
        isFinish();
    }

    @OnClick(R.id.custom_send_rl)
    void customer_input_send() {
        setInputSend(true);
    }


    @Override
    protected void initData() {
        super.initData();
        String userIconUrl = SimpleSharedPreferences.getString("userIconUrl", MyCustomerServiceActivity.this);
        if (!StringUtil.isEmpty(userIconUrl)) {
            PicassoUtil.loadImage(this, userIconUrl + "?imageView2/2/w/" + (ScreenUtils.dip2px(MyCustomerServiceActivity.this, 40) + 120), custom_send_message_defult_icon_iv,R.drawable.registered_status);
        }
        //连接适配器
        adapter = new DataAdapter();
        customerMessageListview.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!isSuccess) {
            //  未登录客服 重新登录
            signCustomerSystem();
        } else {
            //已成功连接到客服  无需操作
        }
    }

    /**
     * 初始话客服系统信息
     */
    private void initCustomer() {
        //获取对于的对话
        conversation = EMChatManager.getInstance().getConversation(toChatUsername);
        conversation.getAllMessages().clear();
        setInputSend(false);
        mHandler.sendEmptyMessage(UPDATE_ADPATER);
        //注册接收消息的监听广播
        //只有注册了广播才能接收到新消息，目前离线消息，在线消息都是走接收消息的广播（离线消息目前无法监听，在登录以后，接收消息广播会执行一次拿到所有的离线消息）
        msgReceiver = new NewMessageBroadcastReceiver();
        IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
        intentFilter.setPriority(3);
        registerReceiver(msgReceiver, intentFilter);
        //最后要通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
        EMChat.getInstance().setAppInited();
    }

    //设置发送按钮
    private void setInputSend(boolean a) {
        //获取到与聊天人的会话对象。参数username为聊天人的userid或者groupid，后文中的username皆是如此
        EMConversation conversation = EMChatManager.getInstance().getConversation(toChatUsername);
        //创建一条文本消息
        EMMessage message = EMMessage.createSendMessage(EMMessage.Type.TXT);
        //如果是群聊，设置chattype,默认是单聊
        //设置接收人
        message.setReceipt(toChatUsername);
        if (a) {
            String sendMessage = custom_send_message_et.getText().toString().trim();
            if (sendMessage.equals("") && sendMessage.length() <= 0) {
                return;
            }
            //设置消息body
            TextMessageBody txtBody = new TextMessageBody(sendMessage);
            message.addBody(txtBody);
            //把消息加入到此会话对象中
            conversation.addMessage(message);
            //发送时刷新listview保证发送的消息显示到UI上
            mHandler.sendEmptyMessage(UPDATE_ADPATER);
            custom_send_message_et.setText("");
        } else {
            //设置消息body
            TextMessageBody txtBody = new TextMessageBody("正在为您接入客服！");
            message.addBody(txtBody);
        }
        //发送消息
        EMChatManager.getInstance().sendMessage(message, new EMCallBack() {

            @Override
            public void onError(int arg0, String arg1) {
                // TODO Auto-generated method stub
                MyCustomerServiceActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
//                                Toast.makeText(MainActivity.this, "error", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            @Override
            public void onProgress(int arg0, String arg1) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onSuccess() {
                // TODO Auto-generated method stub
                MyCustomerServiceActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
//                                Toast.makeText(MainActivity.this, "发送成功", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    /**
     * 登录客服系统逻辑
     */
    private boolean signCustomerSystem() {
        if (AppContent.UID.equals("")) {
            // 未登录   请求后台分配游客帐号
            getAccountAndPassword();
        } else {
            //已登录  直接用帐号登录客服系统
            customerUserName = SimpleSharedPreferences.getString("customerUserName", this);
            customerPassWord = SimpleSharedPreferences.getString("customerPassWord", this);

            signCustomer(customerUserName, customerPassWord);
        }
        return isSuccess;
    }

    private void signCustomer(String userName, String password) {
        if (StringUtils.isBlank(userName) || StringUtils.isBlank(password)) {
            mHandler.sendEmptyMessage(SHOW_DIALOG);
            return;
        }
        EMChatManager.getInstance().login(userName, password, new EMCallBack() {//回调
            @Override
            public void onSuccess() {
                MyCustomerServiceActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        EMGroupManager.getInstance().loadAllGroups();
                        EMChatManager.getInstance().loadAllConversations();
                    }
                });
                //登录成功
                MLog.d("登录环信成功");
                initCustomer();
                isLogin = true;
                isSuccess = true;
            }

            @Override
            public void onProgress(int progress, String status) {

            }

            @Override
            public void onError(int code, String message) {
                //登录失败
                MLog.d("连接客服失败");
                isSuccess = false;
                isLogin = false;
//                showToastMsg("连接客服失败，请重新连接！");
                mHandler.sendEmptyMessage(SHOW_DIALOG);
            }
        });
    }

    private class NewMessageBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            MLog.d("======================");
            // 注销广播
            abortBroadcast();
            // 消息id（每条消息都会生成唯一的一个id，目前是SDK生成）
            String msgId = intent.getStringExtra("msgid");
            //发送方
            String username = intent.getStringExtra("from");
            // 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
            EMMessage message = EMChatManager.getInstance().getMessage(msgId);
            EMConversation conversation = EMChatManager.getInstance().getConversation(username);
            // 如果是群聊消息，获取到group id
            if (message != null && message.getChatType() == EMMessage.ChatType.GroupChat) {
                username = message.getTo();
            }
            if (!username.equals(username)) {
                // 消息不是发给当前会话，return
                return;
            }
            //刷新接收消息的UI
            conversation.addMessage(message);
            mHandler.sendEmptyMessage(UPDATE_ADPATER);
        }
    }

    /**
     * 获取游客的帐号和密码
     */
    private void getAccountAndPassword() {
        OkHttpAPI.postObjectData("/im/equ/account/service", "-1", new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.get("resCode").equals("0")) {
                        customerUserName = VR_RsaUtils.decryptByPublicKey(String.valueOf(result.get("imAccount")));
                        customerPassWord = VR_RsaUtils.decryptByPublicKey(String.valueOf(result.get("imPassword")));
                        MLog.d("customerPassWord:" + customerPassWord + "customerUserName:" + customerUserName);
                        signCustomer(customerUserName, customerPassWord);
                    } else {
                        isLogin = false;
                        showHintDialog();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                isLogin = false;
                showHintDialog();
            }
        });
    }

    private class DataAdapter extends BaseAdapter {

        TextView textViewName;
        TextView textViewContent;

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return conversation == null ? 0 : conversation.getAllMessages().size();
        }

        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return conversation.getAllMessages().get(position);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // TODO Auto-generated method stub
            EMMessage message = conversation.getAllMessages().get(position);
            TextMessageBody body = (TextMessageBody) message.getBody();
            if (message.direct == EMMessage.Direct.RECEIVE) {
                if (message.getType() == EMMessage.Type.TXT) {
                    convertView = LayoutInflater.from(MyCustomerServiceActivity.this).inflate(R.layout.item_customer_listview_from, null);
                }
            } else {
                if (message.getType() == EMMessage.Type.TXT) {
                    convertView = LayoutInflater.from(MyCustomerServiceActivity.this).inflate(R.layout.item_customer_listview_to, null);
                }
            }
            textViewContent = (TextView) convertView.findViewById(R.id.text_content_tv);
            textViewContent.setText(body.getMessage());

            return convertView;
        }

    }

    private void showHintDialog() {
        mHintDialog = new HintDialog(MyCustomerServiceActivity.this);
        mHintDialog.setContextText("连接客服系统失败，是否重新连接客服系统");
        mHintDialog.setOkButText("确定");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
                signCustomerSystem();
            }
        });
        mHintDialog.setCancelText("取消");
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
                MyCustomerServiceActivity.this.finish();
            }
        });
        mHintDialog.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (msgReceiver != null) {
            unregisterReceiver(msgReceiver);
            msgReceiver=null;
        }
        ButterKnife.unbind(this);
    }
}
