package com.xunixianshi.vrshow.fine;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshRecyclerView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.obj.DelicacyListObj;
import com.xunixianshi.vrshow.obj.DelicacyResult;
import com.xunixianshi.vrshow.player.VRPlayerActivity;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseItemOnClickListener;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemCallBack;
import com.xunixianshi.vrshow.recyclerview.RecyclerItemOnClickListener;
import com.xunixianshi.vrshow.recyclerview.VideoExpandableActivity;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import com.xunixianshi.vrshow.fine.FinePanoramaAdapter.FineVrPanoramaHolder;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 精品页
 * @ClassName FineFragment
 *@author HeChuang
 *@time 2016/11/1 15:28
 */
public class FineFragment extends BaseFra {

    @Bind(R.id.fragment_fine_recycler_list_rv)
    PullToRefreshRecyclerView fragment_fine_recycler_list_rv;

    @Bind(R.id.fine_top_rl)
    RelativeLayout fine_top_rl;

    @Bind(R.id.fragment_fine_empty_rl)
    RelativeLayout fragment_fine_empty_rl;

    @Bind(R.id.fragment_fine_net_fail)
    RelativeLayout fragment_fine_net_fail;

    private RecyclerView mRecyclerView;
    private FineVrPanoramaView mVrPanoramaView;
    private FinePanoramaAdapter mFinePanoramaAdapter;
    private LoadingAnimationDialog progressDialog;
    private LinearLayoutManager mLinearLayoutManager;
    private FinePanoramaAdapter.FineVrPanoramaHolder mOldViewHolder;
    private FinePanoramaAdapter.FineVrPanoramaHolder mNewViewHolder;
    private int thisPage = 1;
    private boolean isPanorama = false;
    private int showPanoramaIndex = 0;
    private boolean isPullToRefresh = false;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_fine, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    protected void initView(View parentView) {
        fragment_fine_recycler_list_rv.setMode(PullToRefreshBase.Mode.BOTH);
        mRecyclerView = fragment_fine_recycler_list_rv.getRefreshableView();
        progressDialog = new LoadingAnimationDialog(getActivity());


        mFinePanoramaAdapter = new FinePanoramaAdapter(getActivity());
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setHasFixedSize(true);

        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        mRecyclerView.setAdapter(mFinePanoramaAdapter);
        mRecyclerView.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, ScreenUtils.dip2px(getActivity(),5), ContextCompat.getColor(getActivity(),R.color.color_e6e6e6)));

        mRecyclerView.addOnChildAttachStateChangeListener(onChildAttachStateChangeListener);
        mRecyclerView.addOnScrollListener(onScrollListener);

        mVrPanoramaView = new FineVrPanoramaView(getActivity());
        mVrPanoramaView.setFullscreenButtonEnabled(false);
        mVrPanoramaView.setStereoModeButtonEnabled(false);
        mVrPanoramaView.setInfoButtonEnabled(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        mVrPanoramaView.pauseRendering();
    }

    @Override
    public void onResume() {
        super.onResume();
        mVrPanoramaView.resumeRendering();
    }


    public void onDestroy() {
        super.onDestroy();
        mVrPanoramaView.shutdown();
        ButterKnife.unbind(this);
    }


    @Override
    protected void initData() {
        mFinePanoramaAdapter.setGroup(new ArrayList<DelicacyListObj>());
        isPullToRefresh = false;
        initListener();
        getNetData(thisPage);
    }

    private void initListener() {
        fragment_fine_recycler_list_rv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isPullToRefresh = true;
                thisPage = 1;
                isPanorama = false;
                getNetData(thisPage);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isPullToRefresh = true;
                getNetData(thisPage);
            }
        });

        mFinePanoramaAdapter.setRecyclerBaseItemOnClickListener(new RecyclerBaseItemOnClickListener() {
            @Override
            public void onClick(int position) {
                DelicacyListObj delicacyListObj = mFinePanoramaAdapter.getItem(position);
                VRPlayerActivity.intentTo(getActivity(),delicacyListObj.getResourceTitle(),delicacyListObj.getResourceId());//播放全景VR视频
            }
        });

        mFinePanoramaAdapter.setRecyclerItemOnClickListener(new RecyclerItemOnClickListener() {
            @Override
            public void onClick(int position, int expandableType, RecyclerItemCallBack callBack) {
                DelicacyListObj delicacyListObj = mFinePanoramaAdapter.getItem(position);
                switch (expandableType){
                    case 1://点赞
                        ((VideoExpandableActivity)getActivity()).zanVideo(delicacyListObj.getResourceId(),callBack);
                        break;
                    case 2://下载
                        ((VideoExpandableActivity)getActivity()).startDownload(delicacyListObj.getResourceId(),delicacyListObj.getResourceCoverImgUrl(),delicacyListObj.getResourceTitle());
                        break;
                    case 3://分享
                        ((VideoExpandableActivity)getActivity()).shareShow(delicacyListObj.getResourceSmallIntro(),delicacyListObj.getResourceCoverImgUrl(),delicacyListObj.getResourceTitle(),delicacyListObj.getResourceId());
                        break;
                    case 4://详情
                        Intent intent = new Intent();
                        intent.setClass(getActivity(), ClassifyVideoDetialActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putInt("videoTypeId",delicacyListObj.getResourceId());
                        intent.putExtras(bundle);
                        getActivity().startActivity(intent);
                        break;
                }
            }
        });
    }

    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    if(mOldViewHolder!=null){
                        mOldViewHolder.removePanoramaView();
                    }
                    mNewViewHolder.addPanoramaView(mVrPanoramaView);
                    mHandler.removeMessages(2);
                    mHandler.sendEmptyMessageDelayed(2,100);
                    mVrPanoramaView.setVisibility(View.GONE);
                    mOldViewHolder = mNewViewHolder;
                    break;
                case 2:
                    if(mNewViewHolder.getPanoramaImageLoadingState()==-1){
                        mHandler.sendEmptyMessageDelayed(2,100);
                    }
                    else if(mNewViewHolder.getPanoramaImageLoadingState() ==1){
                        mVrPanoramaView.loadBitmap(((BitmapDrawable)mNewViewHolder.getFinePanoramaImage().getDrawable()).getBitmap());
                        mVrPanoramaView.setVisibility(View.VISIBLE);
                    }
                    break;
            }
        }
    };

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
            if(newState == RecyclerView.SCROLL_STATE_IDLE){
                int newItem =  mLinearLayoutManager.findFirstCompletelyVisibleItemPosition();
                if(newItem < 0){
                    newItem = mLinearLayoutManager.findFirstVisibleItemPosition();
                }
                if(showPanoramaIndex != newItem){
                   FineVrPanoramaHolder viewHolder =  (FineVrPanoramaHolder)mRecyclerView.findViewHolderForLayoutPosition(newItem);
                    if(viewHolder!=null){
                        mNewViewHolder = viewHolder;
                        mHandler.sendEmptyMessageDelayed(1,20);
                    }
                    showPanoramaIndex = newItem;
                }
            }
        }
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
        }
    };

    private RecyclerView.OnChildAttachStateChangeListener onChildAttachStateChangeListener = new RecyclerView.OnChildAttachStateChangeListener() {
        @Override
        public void onChildViewAttachedToWindow(View view) {
            FineVrPanoramaHolder viewHolder = (FineVrPanoramaHolder)mRecyclerView.getChildViewHolder(view);
            if (viewHolder != null && !isPanorama) {
                showPanoramaIndex = 0;
                isPanorama = true;
                mNewViewHolder = viewHolder;
                mHandler.sendEmptyMessageDelayed(1,500);
            }
        }

        @Override
        public void onChildViewDetachedFromWindow(View view) {
        }
    };

    @OnClick(R.id.page_no_have_reload_bt)
    void reloadBtnClick(){
        thisPage = 1;
        isPanorama = false;
        getNetData(thisPage);
    }

    private void attachPostHttpResultView(){
        fragment_fine_net_fail.setVisibility(View.GONE);
        fragment_fine_empty_rl.setVisibility(View.GONE);
        if(mFinePanoramaAdapter.getGroup().size() == 0 ){
            fragment_fine_empty_rl.setVisibility(View.VISIBLE);
        }
    }

    private void showPostHttpResultError(String msg){
        fragment_fine_net_fail.setVisibility(View.GONE);
        fragment_fine_empty_rl.setVisibility(View.GONE);
        if(mFinePanoramaAdapter.getGroup().size() == 0 ){
            fragment_fine_net_fail.setVisibility(View.VISIBLE);
        }
//        showToastMsg(msg);
    }

    /**
     * 获取网络数据
     *
     * @author hechuang
     * @time 2016/9/18 15:25
     */
    private void getNetData(  int page) {
        if(!isPullToRefresh){
            progressDialog.show();
        }
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("queryType", 1); //1表示查询精品资源 2表示待定
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        String Ras_uid  = VR_RsaUtils.encodeByPublicKey(AppContent.UID); //16.12.28 接口改动
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/delicacy/resources/list/service", jsonData, new GenericsCallback<DelicacyResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                fragment_fine_recycler_list_rv.onRefreshComplete();
                showPostHttpResultError("网络连接失败，请检查网络！");
            }
            @Override
            public void onResponse(DelicacyResult response, int id) {
                progressDialog.dismiss();
                fragment_fine_recycler_list_rv.onRefreshComplete();
                if (response.getResCode().equals("0")) {
                    if(thisPage==1){
                        mFinePanoramaAdapter.clearGroup(false);
                    }
                    if (response.getList() != null&&response.getList().size() > 0) {
                        mFinePanoramaAdapter.addItems(response.getList());
                        thisPage++;
                    }
                    attachPostHttpResultView();
                } else {
                    showPostHttpResultError(response.getResDesc());
                }
            }
        });
    }
}
