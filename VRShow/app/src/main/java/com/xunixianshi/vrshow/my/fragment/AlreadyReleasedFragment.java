package com.xunixianshi.vrshow.my.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.adpater.AlreadyReleasedAdapter;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.ShareDialog;
import com.xunixianshi.vrshow.interfaces.ShareSendInterface;
import com.xunixianshi.vrshow.obj.ContentReviewedListObj;
import com.xunixianshi.vrshow.obj.ContentReviewedObj;
import com.xunixianshi.vrshow.recyclerview.VideoExpandableActivity;
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
 * TODO 内容已发布页面
 *
 * @author MarkChang
 * @ClassName AlreadyReleasedFragment
 * @time 2016/11/1 15:50
 */
public class AlreadyReleasedFragment extends BaseFra {

    @Bind(R.id.already_released_ptrlv)
    PullToRefreshListView already_released_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private int thisPage = 1;
    private AlreadyReleasedAdapter alreadyReleasedAdapter;
    private ArrayList<ContentReviewedListObj> itemLists;
    private LoadingAnimationDialog myProgressDialog;
    private ShareDialog mShareDialog;
    private int shareId;
    private boolean isPullToRefresh = false;

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_already_released, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView(View parentView) {
        super.initView(parentView);
    }

    @Override
    protected void initData() {
        super.initData();
        myProgressDialog = new LoadingAnimationDialog(getActivity());
        itemLists = new ArrayList<ContentReviewedListObj>();
        mShareDialog = new ShareDialog(getActivity());
        alreadyReleasedAdapter = new AlreadyReleasedAdapter(getActivity(), true);
        alreadyReleasedAdapter.setShareInterface(new ShareSendInterface() {
            @Override
            public void share(String videoIntroduce, String videoIconUrl, String videoName, int videoId) {
                ((VideoExpandableActivity) getActivity()).shareShow(videoIntroduce, videoIconUrl, videoName, videoId);
            }

            @Override
            public void downLoad(int sourceId, String videoIconUrl, String videoName) {

            }
        });
        already_released_ptrlv.setAdapter(alreadyReleasedAdapter);
        initListener();
        getNetData(1);
    }

    private void initListener() {
        already_released_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        already_released_ptrlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putInt("videoTypeId", itemLists.get(position - 1).getResourceId());
                showActivity(getActivity(), ClassifyVideoDetialActivity.class,
                        bundle);
            }
        });
        already_released_ptrlv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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

    @OnClick(R.id.page_no_have_reload_bt)
    public void onClick() {
        getNetData(1);
    }

    // 获取网络数据
    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            myProgressDialog.show();
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(String.valueOf(AppContent.UID));
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("resourceStatus", 3); // 查询审核成功且上架成功的资源
        hashMap.put("userId", Ras_uid);
        hashMap.put("resourcesType", 1); // 视频资源
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        setCancelHttpTag("/user/resources/list/service");
        OkHttpAPI.postHttpData("/user/resources/list/service", jsonData, new GenericsCallback<ContentReviewedObj>() {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (itemLists.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                myProgressDialog.dismiss();
                already_released_ptrlv.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(ContentReviewedObj response, int id) {
                myProgressDialog.dismiss();
                if (already_released_ptrlv != null) {
                    already_released_ptrlv.onRefreshComplete();
                }
                if (response.getResCode().equals("0")) {
                    if (response.getDataList() != null && response.getDataList().size() > 0) {
//                        page_error_rl.setVisibility(View.GONE);
//                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            itemLists.clear();
                            alreadyReleasedAdapter.clearGroup(true);
                        }
                        itemLists.addAll(response.getDataList());
                        alreadyReleasedAdapter.setGroup(itemLists);
                        thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (response.getResCode().equals("-97")) {// 代码是执行的这里
                    if (itemLists.size() <= 0) {
                        page_emity_rl.setVisibility(View.VISIBLE);
//                        showToastMsg(response.getResDesc()); // 未查询到资源相关数据
                    }
                } else {
                    if (itemLists.size() <= 0) {
                        page_error_rl.setVisibility(View.VISIBLE);
//                        showToastMsg(response.getResDesc());
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
