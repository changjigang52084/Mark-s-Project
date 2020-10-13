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
import com.xunixianshi.vrshow.adpater.MyContentItemAdapter;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.obj.ResourcesCommentResult;
import com.xunixianshi.vrshow.obj.ResourcesCommentResultList;
import com.xunixianshi.vrshow.obj.UserCommentListResult;
import com.xunixianshi.vrshow.obj.UserCommentResult;
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
 * TODO 我的评论
 *
 * @author MarkChang
 * @ClassName MyCommentsActivity
 * @time 2016/11/1 15:36
 */

public class MyCommentsActivity extends BaseAct {

    @Bind(R.id.my_comments_input_subtitle_tv)
    TextView my_comments_input_subtitle_tv;
    @Bind(R.id.my_comments_content_ptrl)
    PullToRefreshListView my_comments_content_ptrl;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private LoadingAnimationDialog myProgressDialog;
    private int thisPage = 1;
    private MyContentItemAdapter myContentItemAdapter;
    private ArrayList<UserCommentListResult> resourcesCommentResultList;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_comments);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        my_comments_input_subtitle_tv.setText("我的评论");
    }

    @Override
    protected void initData() {
        super.initData();
        myProgressDialog = new LoadingAnimationDialog(MyCommentsActivity.this);
        resourcesCommentResultList = new ArrayList<UserCommentListResult>();
        myContentItemAdapter = new MyContentItemAdapter(MyCommentsActivity.this);
        my_comments_content_ptrl.setAdapter(myContentItemAdapter);
        initListener();
        getNetData(1);
    }

    private void initListener() {
        my_comments_content_ptrl.setMode(PullToRefreshBase.Mode.BOTH);
        my_comments_content_ptrl.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                getPlayAddress(mDataLists.get(position - 1).getResourceId(), mDataLists.get(position - 1).getResourceTitle());
                Bundle bundle = new Bundle();
                bundle.putInt("videoTypeId", resourcesCommentResultList.get(position - 1).getResourceId());
                showActivity(MyCommentsActivity.this, ClassifyVideoDetialActivity.class, bundle);

            }
        });
        my_comments_content_ptrl.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
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
     * TODO 资源评论获取列表  这个接口不对，要换一个查询用户全部评论的接口
     *
     * @param page
     */
    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            myProgressDialog.show();
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("userId", Ras_uid);
        hashMap.put("bySort", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        setCancelHttpTag("/user/comment/resources/list/service");
        OkHttpAPI.postHttpData("/user/comment/resources/list/service", jsonData, new GenericsCallback<UserCommentResult>() {

            @Override
            public void onError(Call call, Exception e, int id) {
                if (resourcesCommentResultList.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                myProgressDialog.dismiss();
                my_comments_content_ptrl.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserCommentResult response, int id) {
                myProgressDialog.dismiss();
                my_comments_content_ptrl.onRefreshComplete();
                if (response.getResCode().equals("0")) {
                    if (response.getList()!= null && response.getList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            resourcesCommentResultList.clear();
                            myContentItemAdapter.clearGroup(true);
                        }
                        resourcesCommentResultList.addAll(response.getList());
                        myContentItemAdapter.setGroup(resourcesCommentResultList);
                        thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (resourcesCommentResultList.size() <= 0) {
                        page_error_rl.setVisibility(View.VISIBLE);
                    }
//                    showToastMsg(response.getResDesc());
                }
            }
        });
    }

    @OnClick({R.id.my_comments_back_ib, R.id.my_comments_delete_all_tv, R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_comments_back_ib:
                MyCommentsActivity.this.finish();
                break;
            case R.id.my_comments_delete_all_tv:
//                deleteAll();
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
