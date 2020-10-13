package com.xunixianshi.vrshow.my;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.obj.ActivitysListResult;
import com.xunixianshi.vrshow.obj.ActivitysResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResultDataList;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.webView.ShowWebViewActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 展示用户活动  通过传入不同的uid去获取列表
 *
 * @author hechuang
 * @className UserActivitysActivity
 * @time 2016/9/21 10:26
 */
public class UserActivitysActivity extends BaseAct {
    @Bind(R.id.title_left_back_iv)
    ImageButton title_left_back_iv;
    @Bind(R.id.title_center_text_iv)
    TextView title_center_text_iv;
    @Bind(R.id.titile_right_delete_tv)
    TextView titile_right_delete_tv;
    @Bind(R.id.user_activity_ptrlv)
    PullToRefreshListView user_activity_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private LoadingAnimationDialog progressDialog;
    private NetWorkDialog netWorkDialog;
    private UserActivitysAdapter userActivitysAdapter;
    private ArrayList<ActivitysListResult> itemLists;
    private int thisPage = 1;
    private String ActivitysUID ;//所要获取谁（ActivityUID）的 活动列表
    private String ActivitysName ;//谁的活动
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_user_activitys);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        progressDialog = new LoadingAnimationDialog(UserActivitysActivity.this);
        netWorkDialog = new NetWorkDialog(UserActivitysActivity.this);
        userActivitysAdapter = new UserActivitysAdapter(UserActivitysActivity.this);
        ActivitysUID = getIntent().getStringExtra("ActivitysUID");
        ActivitysName = getIntent().getStringExtra("ActivitysName");
        if(ActivitysName == null){
            ActivitysName = "我的活动";
        }
        MLog.d("ActivitysUID:"+ActivitysUID);
        if(ActivitysUID == null){
            ActivitysUID = AppContent.UID;
        }
        itemLists = new ArrayList<ActivitysListResult>();
        user_activity_ptrlv.setAdapter(userActivitysAdapter);
        title_center_text_iv.setText(ActivitysName);
        initListener();
        getNetData(1);
    }

    /**
     * 初始化监听器
     *
     * @author hechuang
     * @time 2016/9/21 12:01
     */
    private void initListener() {
        user_activity_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        user_activity_ptrlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                //每个活动的点击事件
                String rsaUserId = "-1";
                Bundle bundle = new Bundle();
                String rsaActivityId  = VR_RsaUtils.encodeTo16(VR_RsaUtils.encodeByPublicKey(String.valueOf(itemLists.get(position - 1).getActiveId())));
                if(itemLists.get(position - 1).getIsUserLogin() == 1){
                    if(AppContent.UID.equals("")){
//                        showToastMsg("");
                        showActivity(UserActivitysActivity.this, LoginActivity.class);
                        return;
                    }
                }
                if(AppContent.UID.equals("")){
                    rsaUserId = "-1";
                }else{
                    rsaUserId = VR_RsaUtils.encodeTo16(VR_RsaUtils.encodeByPublicKey(AppContent.UID));
                }
                String targetUrl= itemLists.get(position - 1).getActiveTargetUrl()+"?activeId="+rsaActivityId+"&userId="+rsaUserId+"&resourceType=android";
                MLog.d("loadUrl::"+targetUrl);
                bundle.putString("title", itemLists.get(position - 1).getActiveTitle());
                bundle.putString("loadUrl", targetUrl);
                bundle.putBoolean("showProgress",false);
                showActivity(UserActivitysActivity.this, ShowWebViewActivity.class,
                        bundle);
            }
        });
        user_activity_ptrlv
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 上拉刷新 重置所有数据
                        isPullToRefresh = true;
                        thisPage = 1;
                        getNetData(thisPage);
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 下拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                        isPullToRefresh = true;
                        getNetData(thisPage);
                    }
                });
    }

    @OnClick({R.id.title_left_back_iv, R.id.titile_right_delete_tv,R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_back_iv:
                UserActivitysActivity.this.finish();
                break;
            case R.id.titile_right_delete_tv:
                break;
            case R.id.page_no_have_reload_bt:
                getNetData(1);
                break;
        }
    }

    /**
     * 获取网络数据
     *
     * @author hechuang
     * @time 2016/9/21 10:40
     */
    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            progressDialog.show();
        }
        MLog.d("ActivitysUID:"+ActivitysUID);
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(ActivitysUID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("userId", Ras_uid);
        hashMap.put("bySort", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/query/user/active/list/service");
        OkHttpAPI.postHttpData("/query/user/active/list/service", jsonData, new GenericsCallback<ActivitysResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (itemLists.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                user_activity_ptrlv.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(ActivitysResult result, int id) {
                progressDialog.dismiss();
                user_activity_ptrlv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (result.getList() != null && result.getList().size() > 0) {
                        if (page == 1) {
                            itemLists.clear();
                            userActivitysAdapter.clearGroup(true);
                        }
                        if (result.getList().size() > 0) {
                            itemLists.addAll(result
                                    .getList());
                            userActivitysAdapter.setGroup(itemLists);
                            thisPage++;
                        } else {
                            if (page == 1) {
                                page_emity_rl.setVisibility(View.VISIBLE);
                            }
                        }
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (itemLists.size() <= 0) {
                        page_error_rl.setVisibility(View.VISIBLE);
                    }
//                    showToastMsg(result.getResDesc());
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
