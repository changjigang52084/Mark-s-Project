package com.xunixianshi.vrshow.find;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hch.viewlib.util.MLog;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.obj.MainFindListObj;
import com.xunixianshi.vrshow.obj.MainFindResultObj;
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
 * 发现专题标签
 * @ClassName FindTabFragment
 *@author HeChuang
 *@time 2016/11/1 15:26
 */
public class FindTabFragment extends BaseFra {
    @Bind(R.id.classify_tab_other_ptrlv)
    PullToRefreshListView classify_tab_other_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;
    private int tagId;
    private int thisPage = 1;
    private FindListAdapter myVideoListAdapter;
    private ArrayList<MainFindListObj> mVideoListsLists;
    private LoadingAnimationDialog progressDialog;
    private boolean isPullToRefresh = false;
    private boolean canLoad = false;

    @Override
    protected void lazyLoad() {
        if(canLoad){
            getNetData(1);
        }
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_classify_tab_other, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        super.initData();
        Bundle args = getArguments();
        tagId = args != null ? args.getInt("tagId") : -1;
        myVideoListAdapter = new FindListAdapter(getActivity());
        mVideoListsLists = new ArrayList<MainFindListObj>();
        classify_tab_other_ptrlv.setAdapter(myVideoListAdapter);
        progressDialog = new LoadingAnimationDialog(getActivity());
        classify_tab_other_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        initListener();
        getNetData(1);
    }

    @Override
    public void onResume() {
        super.onResume();
        getNetData(1);
    }

    /**
     * 初始化监听器
     *
     * @author hechuang
     * @time 2016/9/18 15:21
     */
    private void initListener() {
        classify_tab_other_ptrlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("videoTypeId", mVideoListsLists.get(position - 1).getSpecialId());
                showActivity(getActivity(), SpecialDetailActivity.class,
                        bundle);
            }
        });
        classify_tab_other_ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 下拉刷新 重置所有数据
                isPullToRefresh = true;
                thisPage = 1;
                getNetData(thisPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                // 上拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                isPullToRefresh = true;
                getNetData(thisPage);
            }
        });
    }

    @OnClick(R.id.page_no_have_reload_bt)
    public void onClick() {
        getNetData(1);
    }

    /**
     * 获取网络数据
     *
     * @author hechuang
     * @time 2016/9/18 15:25
     */
    private void getNetData(final int page) {
        canLoad = true;
        if (!isPullToRefresh) {
            progressDialog.show();
        }
        String action = "";
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("clientId", tagId);
        /*
        1表示时间倒序
        2表示时间顺序
        3表示排序值倒序
        4表示排序值顺序
        5阅读量倒序
        6表示阅读量顺序
        */
        hashMap.put("bySort", 4);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        action = "/resources/special/list/service";
        setCancelHttpTag(action);
        OkHttpAPI.postHttpData(action, jsonData, new GenericsCallback<MainFindResultObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                page_error_rl.setVisibility(View.VISIBLE);
                progressDialog.dismiss();
                classify_tab_other_ptrlv.onRefreshComplete();
            }

            @Override
            public void onResponse(MainFindResultObj response, int id) {
                MLog.d("response::::::" + response.getResCode());
                progressDialog.dismiss();
                classify_tab_other_ptrlv.onRefreshComplete();
                if (response.getResCode().equals("0")) {
                    if (page == 1) {
                        mVideoListsLists.clear();
                        myVideoListAdapter.clearGroup(true);
                    }
                    if (response.getList() != null
                            && response.getList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        mVideoListsLists
                                .addAll(response.getList());
                        myVideoListAdapter.setGroup(mVideoListsLists);
                        thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    progressDialog.dismiss();
//                    showToastMsg(response.getResDesc());
                    if (page == 1) {
                        classify_tab_other_ptrlv.setVisibility(View.GONE);
                        page_error_rl.setVisibility(View.VISIBLE);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
