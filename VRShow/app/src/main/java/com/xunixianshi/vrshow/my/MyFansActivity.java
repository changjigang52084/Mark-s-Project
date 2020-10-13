package com.xunixianshi.vrshow.my;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.adpater.MyFansAdapter;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.homePage.OtherHomePageActivity;
import com.xunixianshi.vrshow.obj.UserConcernResult;
import com.xunixianshi.vrshow.obj.UserConcernResultList;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * TODO 根据传入的UID分为我的粉丝和他的粉丝
 *
 * @author MarkChang
 * @ClassName MyFansActivity
 * @time 2016/11/1 15:37
 */
public class MyFansActivity extends BaseAct {

    @Bind(R.id.my_content_tv)
    TextView my_content_tv;
    @Bind(R.id.my_fans_content_ptrl)
    PullToRefreshListView my_fans_content_ptrl;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private int thisPage = 1;
    private LoadingAnimationDialog myProgressDialog;
    private MyFansAdapter myFansAdapter;
    private ArrayList<UserConcernResultList> list;
    private String FansActivityUID;
    private String FansActivityName;
    private String FansActivityQueryType;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_fans);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        myProgressDialog = new LoadingAnimationDialog(MyFansActivity.this);
        FansActivityUID = getIntent().getStringExtra("FansActivityUID");
        FansActivityName = getIntent().getStringExtra("FansActivityName");
        FansActivityQueryType = getIntent().getStringExtra("FansActivityQueryType");
        list = new ArrayList<UserConcernResultList>();
        myFansAdapter = new MyFansAdapter(MyFansActivity.this);
        my_fans_content_ptrl.setAdapter(myFansAdapter);
        my_content_tv.setText(FansActivityName);
        initListener();
        getNetData(1);
    }

    private void initListener() {
        my_fans_content_ptrl.setMode(PullToRefreshBase.Mode.BOTH);
        my_fans_content_ptrl.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("showId", list.get(position - 1).getUserId());
                showActivity(MyFansActivity.this, OtherHomePageActivity.class,
                        bundle);
            }
        });
        my_fans_content_ptrl.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

    // 获取我的粉丝网络数据
    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            myProgressDialog.show();
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(FansActivityUID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("queryType", Integer.parseInt(FansActivityQueryType));
        hashMap.put("currPage", page);
        hashMap.put("userId", Ras_uid);
        hashMap.put("pageSize", 10);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        setCancelHttpTag("/user/attention/list/service");
        OkHttpAPI.postHttpData("/user/attention/list/service", jsonData, new GenericsCallback<UserConcernResult>() {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (list.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                myProgressDialog.dismiss();
                my_fans_content_ptrl.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserConcernResult response, int id) {
                myProgressDialog.dismiss();
                my_fans_content_ptrl.onRefreshComplete();
                if (response.getResCode().equals("0")) {
                    if (response.getList() != null && response.getList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            list.clear();
                            myFansAdapter.clearGroup(true);
                        }
                        list.addAll(response.getList());
                        myFansAdapter.setGroup(list);
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

    @OnClick({R.id.my_back_rl, R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_back_rl:
                MyFansActivity.this.finish();
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
