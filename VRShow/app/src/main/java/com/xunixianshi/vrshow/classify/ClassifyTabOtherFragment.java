package com.xunixianshi.vrshow.classify;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.customview.ShareDialog;
import com.xunixianshi.vrshow.interfaces.ShareSendInterface;
import com.xunixianshi.vrshow.obj.MainOtherTabListObj;
import com.xunixianshi.vrshow.obj.MainOtherTabObj;
import com.xunixianshi.vrshow.recyclerview.VideoExpandableActivity;
import com.xunixianshi.vrshow.util.AppContent;
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
 * 分类通用标签页
 *
 * @author HeChuang
 * @ClassName ClassifyTabOtherFragment
 * @time 2016/11/1 15:23
 */
public class ClassifyTabOtherFragment extends BaseFra {

    @Bind(R.id.classify_tab_other_ptrlv)
    PullToRefreshListView classify_tab_other_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;
    @Bind(R.id.page_no_have_reload_bt)
    Button pageNoHaveReloadBt;
    private int tagId;
    private int thisPage = 1;
    private ClassifyTabOtherFragmentAdapter myVideoListAdapter;
    private LoadingAnimationDialog progressDialog;
    private ArrayList<MainOtherTabListObj> classifyVideoTypeResultResourcesLists;
    private ShareDialog mShareDialog;
    private int shareId;
    private NetWorkDialog netWorkDialog;
    private boolean isPullToRefresh = false;

    @Override
    protected void lazyLoad() {

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
        myVideoListAdapter = new ClassifyTabOtherFragmentAdapter(getActivity());
        progressDialog = new LoadingAnimationDialog(getActivity());
        mShareDialog = new ShareDialog(getActivity());
        classifyVideoTypeResultResourcesLists = new ArrayList<MainOtherTabListObj>();
        myVideoListAdapter.setShareInterface(new ShareSendInterface() {
            @Override
            public void share(String videoIntroduce, String videoIconUrl, String videoName, int videoId) {
                ((VideoExpandableActivity) getActivity()).shareShow(videoIntroduce, videoIconUrl, videoName, videoId);
            }

            @Override
            public void downLoad(int sourceId, String videoIconUrl, String videoName) {
                ((VideoExpandableActivity) getActivity()).startDownload(sourceId, videoIconUrl, videoName);
            }


        });
        classify_tab_other_ptrlv.setAdapter(myVideoListAdapter);
        initListener();
        getNetData(1);
    }

    /**
     * 初始化监听器
     *
     * @author hechuang
     * @time 2016/9/18 15:21
     */
    private void initListener() {
        classify_tab_other_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        classify_tab_other_ptrlv.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

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
        if (!isPullToRefresh) {

        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("columnId", tagId);
        hashMap.put("bySort", 1);
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID); //16.12.28 接口改动
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/column/resources/list/service");
        OkHttpAPI.postHttpData("/column/resources/list/service", jsonData, new GenericsCallback<MainOtherTabObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (isDestroy()) {
                    return;
                }
                progressDialog.dismiss();
                classify_tab_other_ptrlv.onRefreshComplete();
                page_error_rl.setVisibility(View.VISIBLE);
            }

            @Override
            public void onResponse(MainOtherTabObj response, int id) {
                if (isDestroy()) {
                    return;
                }
                MLog.d("response::::::" + response.getResCode());
                progressDialog.dismiss();
                if (classify_tab_other_ptrlv != null) {
                    classify_tab_other_ptrlv.onRefreshComplete();
                }
                if (response.getResCode().equals("0")) {
                    if (page == 1) {
                        classifyVideoTypeResultResourcesLists.clear();
                        myVideoListAdapter.clearGroup(true);
                    }
                    if (response.getList() != null && response.getList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        classifyVideoTypeResultResourcesLists
                                .addAll(response.getList());
                        myVideoListAdapter.setGroup(classifyVideoTypeResultResourcesLists);
                        thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
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
