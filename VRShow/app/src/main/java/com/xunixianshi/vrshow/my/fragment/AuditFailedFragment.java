package com.xunixianshi.vrshow.my.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.adpater.AuditFailedAdapter;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.obj.AuditFailedObj;
import com.xunixianshi.vrshow.obj.AuditFailedResult;
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
 * TODO 内容审核失败页面
 *
 * @author MarkChang
 * @ClassName AuditFailedFragment
 * @time 2016/11/1 15:51
 */
public class AuditFailedFragment extends BaseFra {

    @Bind(R.id.audit_failed_ptrl)
    PullToRefreshListView audit_failed_ptrl;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private AuditFailedAdapter auditFailedAdapter;
    private ArrayList<AuditFailedResult> auditFailedResults;
    private LoadingAnimationDialog myProgressDialog;
    private int thisPage = 1;
    private boolean isPullToRefresh = false;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_audit_failed, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void lazyLoad() {

    }

    @Override
    protected void initView(View parentView) {
        super.initView(parentView);

    }

    @Override
    protected void initData() {
        super.initData();
        myProgressDialog = new LoadingAnimationDialog(getActivity());
        auditFailedResults = new ArrayList<AuditFailedResult>();
        auditFailedAdapter = new AuditFailedAdapter(getActivity());
        audit_failed_ptrl.setAdapter(auditFailedAdapter);
        initListener();
        getNetData(1);
    }

    private void initListener() {
        audit_failed_ptrl.setMode(PullToRefreshBase.Mode.BOTH);
        audit_failed_ptrl.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
        String rsa_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("resourceStatus", 2); // 2表示查询审核失败的资源
        hashMap.put("userId", rsa_uid);
        hashMap.put("resourcesType", 1); // 1表示视频资源
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        setCancelHttpTag("/user/resources/list/service");
        OkHttpAPI.postHttpData("/user/resources/list/service", jsonData, new GenericsCallback<AuditFailedObj>() {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (auditFailedResults.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                myProgressDialog.dismiss();
                audit_failed_ptrl.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(AuditFailedObj response, int id) {
                myProgressDialog.dismiss();
                if (audit_failed_ptrl != null) {
                    audit_failed_ptrl.onRefreshComplete();
                }
                if (response.getResCode().equals("0")) {
                    if (response.getDataList() != null && response.getDataList().size() > 0) {
//                        page_error_rl.setVisibility(View.GONE);
//                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            auditFailedResults.clear();
                            auditFailedAdapter.clearGroup(true);
                        }
                        auditFailedResults.addAll(response.getDataList());
                        auditFailedAdapter.setGroup(auditFailedResults);
                        thisPage++;
                    } else {
                        if (page == 1) {
                            auditFailedAdapter.clearGroup(true);
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else if (response.getResCode().equals("-97")) {
                    if (page == 1) {
                        auditFailedResults.clear();
                        auditFailedAdapter.clearGroup(true);
                    }
                    auditFailedResults.addAll(response.getDataList());
                    if (auditFailedResults.size() <= 0) {
                        auditFailedAdapter.clearGroup(true);
                        page_emity_rl.setVisibility(View.VISIBLE);
//                        showToastMsg(response.getResDesc());
                    }
                } else {
                    if (auditFailedResults.size() <= 0) {
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
