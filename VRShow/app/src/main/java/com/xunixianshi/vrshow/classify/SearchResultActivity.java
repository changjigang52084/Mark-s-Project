package com.xunixianshi.vrshow.classify;

import android.content.Intent;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.obj.search.SearchResultListObj;
import com.xunixianshi.vrshow.obj.search.SearchResultObj;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 搜索结果类
 *
 * @author HeChuang
 * @time 2016/9/29 16:24
 */
public class SearchResultActivity extends BaseAct {

    @Bind(R.id.search_result_activity_ptrlv)
    PullToRefreshListView search_result_activity_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private String searchKey; // 搜索关键字
    private int thisPage = 1;
    private LoadingAnimationDialog animationDialog;
    private SearchResultAdapter searchResultAdapter;
    private ArrayList<SearchResultListObj> dataList;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_search_result);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
    }

    @Override
    protected void initData() {
        super.initData();
        Intent data = getIntent();
        if (data != null) {
            searchKey = data.getStringExtra("mHistoryKeyword");
        }
        animationDialog = new LoadingAnimationDialog(SearchResultActivity.this);
        dataList = new ArrayList<SearchResultListObj>();
        searchResultAdapter = new SearchResultAdapter(SearchResultActivity.this);
        search_result_activity_ptrlv.setAdapter(searchResultAdapter);
        initListener();
        getNetData(1);
    }

    private void initListener() {
        search_result_activity_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        search_result_activity_ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

    /**
     * 获取网络数据
     *
     * @param page
     */
    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            animationDialog.show();
        }
        try {
            String utf_searchKey = URLEncoder.encode(searchKey, "utf-8");
            // 封装参数
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("currPage", page);
            hashMap.put("pageSize", 10);
            hashMap.put("searchKey", utf_searchKey);
            hashMap.put("resourcesType", 1); // 0表示搜索所有资源(视频和应用) 1表示搜索视频资源 2表示搜索应用资源
            hashMap.put("clientType", 0); // 0表示查询所有 1表示查询PC端资源 2表示查询Android资源 3表示查询IOS资源
            hashMap.put("bySort", 1); // 1表示按时间排序(最新) 2表示按最热排序
            // 转参数
            String jsonData = StringUtil.MapToJson(hashMap);
            // 获取网络数据
            setCancelHttpTag("/pc/resources/list/search/service");
            OkHttpAPI.postHttpData("/pc/resources/list/search/service", jsonData, new GenericsCallback<SearchResultObj>() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    if (dataList.size() <= 0) {
                        page_error_rl.setVisibility(View.VISIBLE);
                    }
                    animationDialog.dismiss();
                    search_result_activity_ptrlv.onRefreshComplete();
//                    showToastMsg("网络连接失败，请检查网络！");
                }

                @Override
                public void onResponse(SearchResultObj response, int id) {
                    animationDialog.dismiss();
                    search_result_activity_ptrlv.onRefreshComplete();
                    if (response.getResCode().equals("0")) {
                        if (response.getDataList() != null && response.getDataList().size() > 0) {
                            if (page == 1) {
                                dataList.clear();
                                searchResultAdapter.clearGroup(true);
                            }
                            if (response.getDataList().size() > 0) {
                                dataList.addAll(response.getDataList());
                                searchResultAdapter.setGroup(dataList);
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
                        if (dataList.size() <= 0) {
                            page_error_rl.setVisibility(View.VISIBLE);
                        }
//                        showToastMsg(response.getResDesc());
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
    @OnClick({R.id.search_result_activity_back_iv,R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search_result_activity_back_iv:
                SearchResultActivity.this.finish();
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

