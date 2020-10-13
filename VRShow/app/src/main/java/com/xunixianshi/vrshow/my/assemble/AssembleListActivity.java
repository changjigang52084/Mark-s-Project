package com.xunixianshi.vrshow.my.assemble;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshRecyclerView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.assemble.addAssemble.EditAssembleActivity;
import com.xunixianshi.vrshow.my.assemble.detail.AssembleDetailsActivity;
import com.xunixianshi.vrshow.obj.CollectResult;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleItemObj;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleListObj;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemCallBack;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 我的集合
 *
 * @author hechuang
 * @time 2016/9/23 11:10
 */
public class AssembleListActivity extends BaseAct {

    public static final String ASSEMBLE_USER_ID = "assembleUserId";
    public static final String ASSEMBLE_USER_TYPE = "assembleUserType";

    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;
    @Bind(R.id.title_right_function_tv)
    TextView title_right_function_tv;
    @Bind(R.id.my_assemble_list_ptr_lv)
    PullToRefreshRecyclerView my_assemble_list_ptr_lv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_empty_rl;
    private RecyclerView mRecyclerView;
    private LoadingAnimationDialog progressDialog;
    private AssembleAdapter mAssembleAdapter;
    private int thisPage = 1;

    private String mAssembleUserId;
    private int mAssembleUserType;
    private boolean isPullToRefresh = false;
    private HintDialog mHintDialog;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_assemble);
        ButterKnife.bind(this);
        mRecyclerView = my_assemble_list_ptr_lv.getRefreshableView();
        my_assemble_list_ptr_lv.setMode(PullToRefreshBase.Mode.BOTH);
    }

    @Override
    protected void initData() {
        super.initData();
        mAssembleUserId = getIntent().getStringExtra(ASSEMBLE_USER_ID);
        mAssembleUserType = getIntent().getIntExtra(ASSEMBLE_USER_TYPE, 1);
        if (mAssembleUserType == 0) {
            title_center_name_tv.setText("我的合集");
            title_right_function_tv.setText("创建合集");
        } else {
            title_center_name_tv.setText("他的合集");
            title_right_function_tv.setVisibility(View.GONE);
        }
        mHintDialog = new HintDialog(this);
        progressDialog = new LoadingAnimationDialog(AssembleListActivity.this);
        mAssembleAdapter = new AssembleAdapter(this);
        mAssembleAdapter.setMode(mAssembleUserType);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setAdapter(mAssembleAdapter);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(this, LinearLayoutManager.VERTICAL, ScreenUtils.dip2px(this, 5), ContextCompat.getColor(this, R.color.c_layout_blue_bg)));
        initListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        thisPage = 1;
        postHttpCollectionList(thisPage);
    }

    private void initListener() {
        //刷新
        my_assemble_list_ptr_lv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // 上拉刷新 重置所有数据
                isPullToRefresh = true;
                thisPage = 1;
                postHttpCollectionList(thisPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // 下拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                isPullToRefresh = true;
                postHttpCollectionList(thisPage);
            }
        });

        mAssembleAdapter.setRecyclerItemOnClickListener(new RecyclerItemOnClickListener() {
            @Override
            public void onClick(int position, int expandableType, RecyclerItemCallBack callBack) {
                AssembleItemObj assembleItemObj = mAssembleAdapter.getItem(position);
                switch (expandableType) {
                    case 1:
                        Intent intent = new Intent();
                        intent.setClass(AssembleListActivity.this, AssembleDetailsActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("assembleId", assembleItemObj.getUserCompilationId());
                        bundle.putString("assembleName", assembleItemObj.getCompilationName());
                        bundle.putInt(ASSEMBLE_USER_TYPE, mAssembleUserType);
                        intent.putExtras(bundle);
                        AssembleListActivity.this.startActivity(intent);
                        break;
                    case 2:
                        showDeleteAssembleDialog(assembleItemObj.getUserCompilationId(), position);
                        break;
                }
            }
        });


    }

    @OnClick({R.id.title_left_back_iv, R.id.title_right_function_tv, R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_back_iv:
                AssembleListActivity.this.finish();
                break;
            case R.id.title_right_function_tv:
                //创建集合
                showActivity(this, EditAssembleActivity.class);
                break;
            case R.id.page_no_have_reload_bt:
                isPullToRefresh = false;
                thisPage = 1;
                postHttpCollectionList(thisPage);
                break;
        }
    }

    private void showDeleteAssembleDialog(final String assembleId, final int position) {
        mHintDialog.setContextText("是否确定删除");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                postHttpDeletedAssemble(assembleId, position);
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }


    private void attachPostHttpResultView() {
        page_error_rl.setVisibility(View.GONE);
        page_empty_rl.setVisibility(View.GONE);
        if (mAssembleAdapter.getGroup().size() == 0) {
            page_empty_rl.setVisibility(View.VISIBLE);
        }
        progressDialog.dismiss();
    }

    private void showPostHttpResultError(String msg) {
        page_empty_rl.setVisibility(View.GONE);
        page_error_rl.setVisibility(View.GONE);
        if (mAssembleAdapter.getGroup().size() == 0) {
            page_error_rl.setVisibility(View.VISIBLE);
//            showToastMsg(msg);
        }
        progressDialog.dismiss();
    }

    /**
     * 获取网络数据
     *
     * @author hechuang
     * @time 2016/9/21 10:40
     */
    private void postHttpCollectionList(int page) {
        if (!isPullToRefresh) {
            progressDialog.show();
        }
        if (StringUtil.isEmpty(mAssembleUserId)) {
            attachPostHttpResultView();
            return;
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(mAssembleUserId);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("clientId", -1);
        hashMap.put("userId", Ras_uid);
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 3);
        hashMap.put("bySort", 1);
        if (mAssembleUserType == 0) {
            hashMap.put("queryType", -1); //查询自己填-1
        } else {
            hashMap.put("queryType", 1); //查询已审核通过的集合
        }
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/user/compilation/list/service");
        OkHttpAPI.postHttpData("/user/compilation/list/service", jsonData, new GenericsCallback<AssembleListObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                my_assemble_list_ptr_lv.onRefreshComplete();
                showPostHttpResultError("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(AssembleListObj result, int id) {
                my_assemble_list_ptr_lv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (result.getList() != null && result.getList().size() > 0) {
                        if (thisPage == 1) {
                            mAssembleAdapter.clearGroup(true);
                        }
                        mAssembleAdapter.addItems(result.getList());
                        thisPage++;
                    }
                    attachPostHttpResultView();
                } else {
                    showPostHttpResultError(result.getResDesc());
                }
            }
        });
    }

    /**
     * 删除指定合集
     *
     * @ClassName AssembleListActivity
     * @author HeChuang
     * @time 2016/12/23 11:26
     */
    public void postHttpDeletedAssemble(String assembleId, final int position) {
        progressDialog.show();
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userCompilationId", assembleId);
        hashMap.put("userId", Ras_uid);
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/compilation/delete/service", jsonData, new GenericsCallback<CollectResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showPostHttpResultError("网络连接失败!请检查网络");
            }

            @Override
            public void onResponse(CollectResult result, int id) {
                if (result.getResCode().equals("0")) {
                    mAssembleAdapter.removeItem(position);
                    if (mAssembleAdapter.getGroup().size() == 0) {
                        thisPage = 1;
                        isPullToRefresh = true;
                        postHttpCollectionList(thisPage);
                    } else {
                        attachPostHttpResultView();
                    }
                } else {
                    showPostHttpResultError("删除失败");
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
