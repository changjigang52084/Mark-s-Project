package com.xunixianshi.vrshow.classify;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
 * 发送评论页
 *
 * @author HeChuang
 * @ClassName CommentPushActivity
 * @time 2016/11/1 15:24
 */
public class CommentPushActivity extends BaseAct {

    @Bind(R.id.comment_cancel_iv)
    ImageView comment_cancel_iv;
    @Bind(R.id.comment_push_et)
    EditText comment_push_et;
    @Bind(R.id.comment_push_send_bt)
    Button comment_push_send_bt;
    @Bind(R.id.comment_enter_words_number_tv)
    TextView comment_enter_words_number_tv; // 用来显示剩余字数

    private LoadingAnimationDialog progressDialog;
    private int inputMaxNum = 300; // 评论最多可以输入的字数
    private int sourceId;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_comment_push);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        progressDialog = new LoadingAnimationDialog(CommentPushActivity.this);
        sourceId = getIntent().getIntExtra("sourceId", 0);
        comment_push_et.clearFocus();
        comment_enter_words_number_tv.setText("还可以输入" + inputMaxNum + "个字");
        comment_push_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                int number = inputMaxNum - s.length();
                comment_enter_words_number_tv.setText("还可以输入" + number + "个字");
            }
        });
    }

    @OnClick({R.id.comment_cancel_iv, R.id.comment_push_et, R.id.comment_push_send_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.comment_cancel_iv:
                CommentPushActivity.this.finish();
                break;
            case R.id.comment_push_et:
                break;
            case R.id.comment_push_send_bt:
                progressDialog.show();
                pushComment();
                break;
        }
    }

    /**
     * @Title: collectVideo
     * @Description: TODO 发布评论接口
     * @author changjigang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void pushComment() {
        try {
            // 加密用户ID
            String ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
            // 转码评论内容
            String commentContent = comment_push_et.getText().toString();
            String utf8_comment = URLEncoder.encode(commentContent, "utf-8");
            ;
            // 封装参数
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("resourcesId", sourceId);
            hashMap.put("userId", ras_uid);
            hashMap.put("commentContent", utf8_comment);
            hashMap.put("commentScore", 0);
            // 转参数
            String jsonData = StringUtil.MapToJson(hashMap);
            OkHttpAPI.postObjectData("/resources/comment/service", jsonData, new HttpSuccess<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result, int id) {
                    progressDialog.dismiss();
                    try {
                        if (result.get("resCode").equals("0")) {
                            showToastMsg("评论成功！");
                            setResult(200);
                            CommentPushActivity.this.finish();
                        } else if (result.get("resCode").equals("-96")) {
                            showToastMsg(result.get("resDesc").toString());
                        } else {
                            showToastMsg("评论失败！");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new HttpError() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    progressDialog.dismiss();
//                    showToastMsg("网络连接失败!请检查网络");
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
