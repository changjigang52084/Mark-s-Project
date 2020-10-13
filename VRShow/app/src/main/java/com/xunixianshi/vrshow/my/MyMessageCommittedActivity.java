package com.xunixianshi.vrshow.my;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * TODO 我的留言提交页面，我是被留言者
 *
 * @author MarkChang
 * @ClassName MyMessageCommittedActivity
 * @time 2016/11/1 15:47
 */
public class MyMessageCommittedActivity extends BaseAct {

    @Bind(R.id.committed_message_content_et)
    EditText committed_message_content_et;
    @Bind(R.id.message_enter_words_number_tv)
    TextView message_enter_words_number_tv;

    private LoadingAnimationDialog loadingAnimationDialog;
    private String MessageActivityUID;
    private String messageContent; // 留言内容
    private int inputMaxNum = 300;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_message_committed);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        loadingAnimationDialog = new LoadingAnimationDialog(MyMessageCommittedActivity.this);
        MessageActivityUID = getIntent().getStringExtra("MessageActivityUID");
        message_enter_words_number_tv.setText("还可以输入" + inputMaxNum + "个字");
        committed_message_content_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = inputMaxNum - s.length();
                message_enter_words_number_tv.setText("还可以输入" + number + "个字");
            }
        });
    }

    @OnClick({R.id.committed_message_cancel_iv, R.id.committed_message_content_send_btn})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.committed_message_cancel_iv:
                MyMessageCommittedActivity.this.finish();
                break;
            case R.id.committed_message_content_send_btn:
                loadingAnimationDialog.show();
                messageContent = committed_message_content_et.getText().toString();
                if (!StringUtil.isEmpty(messageContent) && messageContent.length() != 0) {
                    committedMessage();
                } else {
                    showToastMsg("请输入你的留言内容！");
                    loadingAnimationDialog.dismiss();
                }
                break;
        }
    }

    /**
     * @Title: collectVideo
     * @Description: TODO 用户空间提交留言接口
     * @author changjigang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void committedMessage() {
        try {
            String rsa_toUserId = VR_RsaUtils.encodeByPublicKey(MessageActivityUID); // 被留言者用戶ID
            String rsa_userId = VR_RsaUtils.encodeByPublicKey(AppContent.UID); // 留言者用戶ID
            String utf_messageContent = URLEncoder.encode(messageContent, "utf-8"); // 留言内容 UTF-8中文编码
            // 封装参数
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("toUserId", rsa_toUserId);
            hashMap.put("userId", rsa_userId);
            hashMap.put("messageContent", utf_messageContent);
            hashMap.put("clientId", 3); // 3表示安卓客户端留言
            // 转参数
            String jsonData = StringUtil.MapToJson(hashMap);
            OkHttpAPI.postObjectData("/user/message/service", jsonData, new HttpSuccess<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result, int id) {
                    try {
                        if (result.getString("resCode").equals("0")) {
                            showToastMsg("留言成功！");
                            loadingAnimationDialog.dismiss();
                            MyMessageCommittedActivity.this.finish();
                        } else {
                            showToastMsg("留言失败！");
                            loadingAnimationDialog.dismiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new HttpError() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    loadingAnimationDialog.dismiss();
//                    showToastMsg("网络连接失败，请检查网络！");
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}