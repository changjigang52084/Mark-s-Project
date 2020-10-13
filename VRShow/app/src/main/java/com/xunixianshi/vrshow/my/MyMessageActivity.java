package com.xunixianshi.vrshow.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.sina.weibo.sdk.call.Position;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.adpater.MyMessageAdapter;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.homePage.OtherHomePageActivity;
import com.xunixianshi.vrshow.obj.LeaveMessageResult;
import com.xunixianshi.vrshow.obj.LeaveMessageResultList;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * TODO 根据传入的UID跳转到我的留言和他的留言
 *
 * @author MarkChang
 * @ClassName MyMessageActivity
 * @time 2016/11/1 15:46
 */
public class MyMessageActivity extends BaseAct {

    @Bind(R.id.my_back_rl)
    RelativeLayout my_back_rl;
    @Bind(R.id.my_content_tv)
    TextView my_content_tv;
    @Bind(R.id.my_message_ptrlv)
    PullToRefreshListView my_message_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;
    @Bind(R.id.classify_push_comment_iv_ll)
    LinearLayout classify_push_comment_iv_ll;
    @Bind(R.id.classify_push_comment_iv_rl)
    RelativeLayout classify_push_comment_iv_rl;

    private int thisPage = 1;
    private LoadingAnimationDialog myProgressDialog;
    private MyMessageAdapter myMessageAdapter;
    private ArrayList<LeaveMessageResultList> list;
    private String MessageActivityUID;
    private String MessageActivityName;
    private String MessageActivityType;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_message);
        ButterKnife.bind(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNetData(1);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        myProgressDialog = new LoadingAnimationDialog(MyMessageActivity.this);
        list = new ArrayList<LeaveMessageResultList>();
        myMessageAdapter = new MyMessageAdapter(MyMessageActivity.this);
        MessageActivityUID = getIntent().getStringExtra("MessageActivityUID");
        MessageActivityName = getIntent().getStringExtra("MessageActivityName");
        MessageActivityType = getIntent().getStringExtra("MessageActivityType");
        if (MessageActivityUID.equals(AppContent.UID)) {
            classify_push_comment_iv_rl.setVisibility(View.GONE);
        } else {
            classify_push_comment_iv_rl.setVisibility(View.VISIBLE);
        }
        my_message_ptrlv.setAdapter(myMessageAdapter);
        my_content_tv.setText(MessageActivityName);
        initListener();
    }

    private void initListener() {
        my_message_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        my_message_ptrlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("showId", String.valueOf(list.get(position - 1).getCommenterId()));
                showActivity(MyMessageActivity.this, OtherHomePageActivity.class,
                        bundle);
            }
        });
        my_message_ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉刷新，重置所有数据
                isPullToRefresh = true;
                thisPage = 1;
                getNetData(thisPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉加载更多数据，获取成功后thisPage自动加1 这里不做++操作
                isPullToRefresh = true;
                getNetData(thisPage);
            }
        });
    }


    // 获取用户留言列表网络数据
    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            myProgressDialog.show();
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(MessageActivityUID);
        int queryType = Integer.parseInt(MessageActivityType);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("queryType", queryType);// 查詢類型 1、表示查詢自己的留言列表。2、表示查詢他人的留言列表。
        hashMap.put("userId", Ras_uid);
        hashMap.put("pageSize", 10);
        hashMap.put("currPage", page);// 当前页面
        hashMap.put("clientId", -1);// 查询安卓端留言
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        setCancelHttpTag("/user/message/list/service");
        OkHttpAPI.postHttpData("/user/message/list/service", jsonData, new GenericsCallback<LeaveMessageResult>() {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (list.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                myProgressDialog.dismiss();
                my_message_ptrlv.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(LeaveMessageResult response, int id) {
                myProgressDialog.dismiss();
                my_message_ptrlv.onRefreshComplete();
                if (response.getResCode().equals("0")) {
                    if (response.getList() != null && response.getList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            list.clear();
                            myMessageAdapter.clearGroup(true);
                        }
                        list.addAll(response.getList());
                        myMessageAdapter.setGroup(list);
                        thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (list.size() <= 0) {
                        page_error_rl.setVisibility(View.VISIBLE);
                    }
//                    showToastMsg(response.getResDesc());
                }
            }
        });
    }


    @OnClick({R.id.my_back_rl, R.id.classify_push_comment_iv_ll, R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_back_rl:
                MyMessageActivity.this.finish();
                break;
            case R.id.classify_push_comment_iv_ll:
                if (AppContent.UID.equals("")) {
                    showToastMsg("用户没有登录，请先登录！");
                    showActivity(MyMessageActivity.this, LoginActivity.class);
                } else if (AppContent.LIMIT_LOGIN == 2) {
                    showToastMsg("当前登录状态未激活，请激活后重新登录。");
                    showActivity(MyMessageActivity.this, LoginActivity.class);
                }else{
                    Intent intent = new Intent();
                    intent.setClass(MyMessageActivity.this, MyMessageCommittedActivity.class);
                    intent.putExtra("MessageActivityUID", MessageActivityUID);
                    startActivity(intent);
                }
                break;
            case R.id.page_no_have_reload_bt:
                getNetData(1);
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
