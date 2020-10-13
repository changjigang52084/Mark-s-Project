package com.xunixianshi.vrshow.my.assemble.detail;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshBase.Mode;
import com.hch.viewlib.widget.PullToRefreshRecyclerView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.assemble.AssembleDetailsObject;
import com.xunixianshi.vrshow.my.assemble.AssembleListActivity;
import com.xunixianshi.vrshow.my.assemble.addAssemble.EditAssembleActivity;
import com.xunixianshi.vrshow.my.assemble.addAssemble.EditContentFragment;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleCollectionListObj;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleContentItemObj;
import com.xunixianshi.vrshow.obj.assembleObj.AssembleDetailObj;
import com.xunixianshi.vrshow.recyclerview.BindHeadViewHolderListener;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseItemOnClickListener;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemCallBack;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 * 合集详情界面
 *@author DuanChunLin
 *@time 2016/10/11 16:24
 */
public class AssembleDetailsFragment extends BaseFra {

    @Bind(R.id.title_left_back_iv)
    ImageButton title_left_back_iv;
    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;
    @Bind(R.id.title_right_function_tv)
    TextView title_right_function_tv;

    @Bind(R.id.my_assemble_content_list_rv)
    PullToRefreshRecyclerView my_assemble_content_list_rv;

    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;

    private LoadingAnimationDialog progressDialog;
    private AssembleDetailsContentAdapter mAssembleContentAdapter;

    private AssembleDetailObj mAssembleDetailObj; //集合详情OBJ
    private int thisPage = 1;
    private String mCompilationName;
    private String mCompilationId;
    private EditContentFragment editContentFragment;
    private String assembleId;

    private int mAssembleUserType;
    private AssembleDetailsObject mAssembleDetailsObject;

    private View mRecyclerHeaderView;
    private AssembleDetailsHeadHolder mAssembleDetailsHeadHolder;

    private  boolean isPullToRefresh = false;


    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_assemble_content, container, false);
        rootView.setClickable(true);// 防止点击穿透，底层的fragment响应上层点击触摸事件
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    protected void initView(View parentView) {
        mCompilationId = getArguments().getString("assembleId");
        mCompilationName = getArguments().getString("assembleName");
        mAssembleUserType = getArguments().getInt(AssembleListActivity.ASSEMBLE_USER_TYPE,1);
        title_center_name_tv.setText(mCompilationName);
        if(mAssembleUserType==0){
            title_right_function_tv.setText("编辑合集");
        }
        else{
            title_right_function_tv.setVisibility(View.GONE);
        }
    }

    @Override
    protected void initData() {
        progressDialog = new LoadingAnimationDialog(getActivity());
        my_assemble_content_list_rv.getRefreshableView().setLayoutManager(new LinearLayoutManager(getActivity()));
        mAssembleContentAdapter = new AssembleDetailsContentAdapter(getActivity());
        my_assemble_content_list_rv.getRefreshableView().setAdapter(mAssembleContentAdapter);
        my_assemble_content_list_rv.setMode(Mode.BOTH);
        my_assemble_content_list_rv.getRefreshableView().addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL,ScreenUtils.dip2px(getActivity(),5), ContextCompat.getColor(getActivity(),R.color.color_e6e6e6)));
        mRecyclerHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_assemble_detail_head, my_assemble_content_list_rv.getRefreshableView(), false);

        mAssembleDetailsHeadHolder = new AssembleDetailsHeadHolder(mRecyclerHeaderView,getActivity());
        mAssembleContentAdapter.setHeaderViewInterface( new BindHeadViewHolderListener() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateHeadViewHolder(ViewGroup parent, int viewType) {
                return mAssembleDetailsHeadHolder;
            }
            @Override
            public void onBindHeadViewHolder(RecyclerView.ViewHolder viewHolder) {
                AssembleDetailsHeadHolder assembleDetailsHeadHolder = (AssembleDetailsHeadHolder)viewHolder;
                assembleDetailsHeadHolder.setHeadHolderDate(mAssembleDetailObj);
            }
        });
        initListener();
    }


    @Override
    public void onResume() {
        super.onResume();
        isPullToRefresh = false;
        postHttpCollectionDetail();
    }

    private void initListener() {
        //加载更多
        my_assemble_content_list_rv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isPullToRefresh = true;
                postHttpCollectionDetail();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isPullToRefresh = true;
                postHttpCollectionContentListData(assembleId, thisPage);
            }
        });

        /**
         * 打开内容 目前都是视频，全部跳转到视频详情页面
         *@author DuanChunLin
         *@time 2016/10/11 16:56
         */
        mAssembleContentAdapter.setRecyclerItemOnClickListener(new RecyclerItemOnClickListener() {
            @Override
            public void onClick(int position, int expandableType, RecyclerItemCallBack callBack) {
                AssembleContentItemObj assembleContentItemObj = mAssembleContentAdapter.getItem(position);
                switch (expandableType){
                    case 1: //合集详情
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), ClassifyVideoDetialActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("videoTypeId", assembleContentItemObj.getResourceId());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        break;
                }
            }
        });

    }

    @OnClick({R.id.title_left_back_iv, R.id.title_right_function_tv,R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_back_iv:
                exitBack();
                break;
            case R.id.title_right_function_tv:
                Bundle bundle = new Bundle();
                mAssembleDetailsObject =  new AssembleDetailsObject();
                mAssembleDetailsObject.setAssembleDetailObj(mAssembleDetailObj);
                mAssembleDetailsObject.setTotalPage(thisPage);
                mAssembleDetailsObject.setContentList(mAssembleContentAdapter.getGroup());
                bundle.putSerializable(EditAssembleActivity.ASSEMBLE_DETAIL_INTENT_KEY,mAssembleDetailsObject);
                showActivity(getActivity(), EditAssembleActivity.class, bundle);
                break;
            case R.id.page_no_have_reload_bt:
                isPullToRefresh = false;
                postHttpCollectionDetail();
                break;
        }
    }

    public void onBackPressed() {
        if (editContentFragment != null && !editContentFragment.isRemoving()) {
            editContentFragment.exitBack();
            editContentFragment = null;
        } else {
            exitBack();
        }
    }

    private void exitBack() {
        getActivity().finish();
    }


    private void setNetDate(AssembleDetailObj assembleDetailObj){
        mAssembleDetailObj = assembleDetailObj;
        if(assembleDetailObj!=null){
            assembleId = mAssembleDetailObj.getCompilationId();
            if(assembleDetailObj.getCompilationName()!=null){
                title_center_name_tv.setText(assembleDetailObj.getCompilationName());
            }
        }
    }
    /**
     * 获取合集资源详情
     *
     * @author HeChuang
     * @time 2016/10/10 16:57
     */
    private void postHttpCollectionDetail() {
        if(!isPullToRefresh){
            progressDialog.show();
        }
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("compilationId", mCompilationId);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/compilation/detail/service", jsonData, new GenericsCallback<AssembleDetailObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showGetNetDateError("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(AssembleDetailObj result, int id) {
                progressDialog.dismiss();
                if (result.getResCode().equals("0")) {
                    setNetDate(result);
                    thisPage = 1;
                    postHttpCollectionContentListData(assembleId, thisPage);
                } else {
                    showGetNetDateError(result.getResDesc());
                }
            }
        });
    }

    private void attachPostHttpResultView(){
        page_error_rl.setVisibility(View.GONE);
        progressDialog.dismiss();
    }

    private void showGetNetDateError(String msg) {
        page_error_rl.setVisibility(View.GONE);
        progressDialog.dismiss();
        if(!StringUtil.isEmpty(msg)){
//            showToastMsg(msg);
        }
        if(thisPage == 1){
            page_error_rl.setVisibility(View.VISIBLE);
        }
    }


    /**
     * 获取合集下资源列表
     *
     * @author hechuang
     * @time 2016/9/21 10:40
     */
    private void postHttpCollectionContentListData(String compilationId, int page) {
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("compilationId", compilationId);
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("bySort", 1);
        hashMap.put("queryType", 2);

        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID); //16.12.28 接口改动
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/compilation/resource/list/service", jsonData, new GenericsCallback<AssembleCollectionListObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(isDestroy()){ return;}
                my_assemble_content_list_rv.onRefreshComplete();
                showGetNetDateError("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(AssembleCollectionListObj result, int id) {
                if(isDestroy()){ return;}
                progressDialog.dismiss();
                my_assemble_content_list_rv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (thisPage == 1) {
                        mAssembleContentAdapter.clearGroup(true);
                    }
                    if (result.getList() != null && result.getList().size() > 0) {
                        mAssembleContentAdapter.addItems(result.getList());
                        thisPage++;
                    }
                    attachPostHttpResultView();
                }
                else {
                    showGetNetDateError(result.getResDesc());
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
