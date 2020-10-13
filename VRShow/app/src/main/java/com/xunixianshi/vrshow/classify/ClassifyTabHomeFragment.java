package com.xunixianshi.vrshow.classify;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.customview.viewpagercycletes.ADInfo;
import com.hch.viewlib.customview.viewpagercycletes.CycleViewPager;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.widget.NoScrollGridView;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshScrollView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.find.SpecialDetailActivity;
import com.xunixianshi.vrshow.my.LoginActivity;
import com.xunixianshi.vrshow.obj.HomeResult;
import com.xunixianshi.vrshow.obj.HomeResultFocusImgList;
import com.xunixianshi.vrshow.obj.HomeResultList;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.util.ViewFactory;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.xunixianshi.vrshow.webView.ShowWebViewActivity;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 分类-首页
 * Created by Administrator on 2016/9/19.
 */
public class ClassifyTabHomeFragment extends BaseFra {

    @Bind(R.id.classify_tab_home_cycle_view_icv)
    CycleViewPager classify_tab_home_cycle_view_icv;
    @Bind(R.id.myScrollView)
    PullToRefreshScrollView myScrollView;
    @Bind(R.id.classify_home_custom_list_one_title_tv)
    TextView classify_home_custom_list_one_title_tv;
    @Bind(R.id.classify_home_custom_list_one_rl)
    RelativeLayout classify_home_custom_list_one_rl;
    @Bind(R.id.classify_home_custom_list_one_gv)
    NoScrollGridView classify_home_custom_list_one_gv;

    @Bind(R.id.classify_home_spread_one_cvp)
    CycleViewPager classify_home_spread_one_cvp;

    @Bind(R.id.classify_home_custom_list_two_title_tv)
    TextView classify_home_custom_list_two_title_tv;
    @Bind(R.id.classify_home_custom_list_two_rl)
    RelativeLayout classify_home_custom_list_two_rl;
    @Bind(R.id.classify_home_custom_list_two_gv)
    NoScrollGridView classify_home_custom_list_two_gv;

    @Bind(R.id.classify_home_spread_two_cvp)
    CycleViewPager classify_home_spread_two_cvp;

    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_no_have_reload_bt)
    Button page_no_have_reload_bt;
    @Bind(R.id.home_page_emity_view)
    View home_page_emity_view;

    private Gson gson = new Gson();
    private ArrayList<HomeResultFocusImgList> imageUrlList;// 图片路径地址的数组集合

    private LoadingAnimationDialog progressDialog;
    private int oneVideoTagId;
    private int twoVideoTagId;
    private String oneVideoTypeName;
    private String twoVideoTypeName;

    private ClassifyTabHomeFragmentAdapter oneClassifyTabHomeFragmentAdapter;
    private ClassifyTabHomeFragmentAdapter twoClassifyTabHomeFragmentAdapter;
    private ArrayList<HomeResultList> mOneVideoList;
    private ArrayList<HomeResultList> mTwoVideoList;
    private boolean isPullToRefresh = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            myScrollView.getRefreshableView().fullScroll(ScrollView.FOCUS_UP);
        }
    };

    @Override
    protected void lazyLoad() {
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_classify_tab_home, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        super.initData();
        page_error_rl.setClickable(true);
        imageUrlList = new ArrayList<HomeResultFocusImgList>();
        oneClassifyTabHomeFragmentAdapter = new ClassifyTabHomeFragmentAdapter(getActivity());
        twoClassifyTabHomeFragmentAdapter = new ClassifyTabHomeFragmentAdapter(getActivity());
        mOneVideoList = new ArrayList<HomeResultList>();
        mTwoVideoList = new ArrayList<HomeResultList>();
        progressDialog = new LoadingAnimationDialog(getActivity());
        oneClassifyTabHomeFragmentAdapter.setGroup(mOneVideoList);
        twoClassifyTabHomeFragmentAdapter.setGroup(mTwoVideoList);
        classify_home_custom_list_one_gv.setAdapter(oneClassifyTabHomeFragmentAdapter);
        classify_home_custom_list_two_gv.setAdapter(twoClassifyTabHomeFragmentAdapter);
        int screenWith = ScreenUtils.getScreenWidth(getActivity());
        classify_tab_home_cycle_view_icv.setLayoutParams(new LinearLayout.LayoutParams(screenWith, (int) (screenWith * 0.56)));
        initListener();
        getNetData();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    protected void initView(View parentView) {
        super.initView(parentView);
    }

    private void initListener() {
        int screenWith = ScreenUtils.getScreenWidth(getActivity());
        ViewGroup.LayoutParams lp;
        lp = classify_home_spread_one_cvp.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.28);
        classify_home_spread_one_cvp.setLayoutParams(lp);
        classify_home_spread_two_cvp.setLayoutParams(lp);

        myScrollView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                isPullToRefresh = true;
                getNetData();
            }
        });
        page_no_have_reload_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MLog.d("home--reload");
                progressDialog.show();
                home_page_emity_view.setVisibility(View.VISIBLE);
                getNetData();
            }
        });

    }


    /**
     * 轮播图监听
     *
     * @author hechuang
     * @time 2016/9/20 12:00
     */
    private CycleViewPager.ImageCycleViewListener mAdCycleViewListener = new CycleViewPager.ImageCycleViewListener() {
        @Override
        public void onImageClick(ADInfo info, int position, View imageView) {
                Bundle bundle;
                switch (info.getTargetType()) {
                    case 2://类型列表页
                        bundle = new Bundle();
//                        bundle.putInt("videoTypeId", Integer.parseInt(imageUrlList.get(position).getBannerContent()));
//                        bundle.putString("videoTypeName", imageUrlList.get(position).getBannerName());
//                        showActivity(getActivity(), ClassifyVideoTypeActivity.class,
//                                bundle);
                        break;
                    case 3://视频详情页
                        bundle = new Bundle();
                        bundle.putInt("videoTypeId", Integer.parseInt(info.getTargetValue()));
                        showActivity(getActivity(), ClassifyVideoDetialActivity.class, bundle);
                        break;
                    case 12: //专题详情
                        bundle = new Bundle();
                        bundle.putString("videoTypeId", info.getTargetValue());
                        showActivity(getActivity(), SpecialDetailActivity.class,
                                bundle);
                        break;
                    case 11: //活动详情
                        if (AppContent.UID.equals("")) {
                            showActivity(getActivity(), LoginActivity.class);
                            return;
                        }
                        String   rsaUserId = VR_RsaUtils.encodeTo16(VR_RsaUtils.encodeByPublicKey(AppContent.UID));
                        String   targetUrl = info.getTargetValue();
//                        String  targetUrl = "http://192.168.200.194:8888/VR_Show/active/detail.html?activeId=1&aa=0";
                        String   activeId = StringUtil.subString(targetUrl,"activeId=",false);
                        if(!StringUtil.isEmpty(activeId)){
                            if(activeId.contains("&")){
                                activeId =  StringUtil.subString(activeId,"&",true);
                            }
                            activeId = StringUtil.subString(activeId,"=",false).replace("=","");
                            String rsaActiveId = VR_RsaUtils.encodeTo16(VR_RsaUtils.encodeByPublicKey(activeId));
                            targetUrl = targetUrl.replace("activeId="+activeId,"activeId="+rsaActiveId);
                        }
                        targetUrl = targetUrl   + "&userId=" + rsaUserId + "&resourceType=android";
                        bundle = new Bundle();
                        bundle.putString("loadUrl", targetUrl);
                        bundle.putString("title", ""+ info.getBannerName());
                        bundle.putBoolean("showProgress", false);
                        showActivity(getActivity(), ShowWebViewActivity.class,
                                bundle);
                        break;
                    case 10://web页面
                        bundle = new Bundle();
                        bundle.putString("loadUrl", info.getTargetValue());
                        bundle.putBoolean("showProgress", true);
                        showActivity(getActivity(), ShowWebViewActivity.class, bundle);
                        break;

                    default:
                        break;
                }
        }

    };

    private void setImageCycleDate(ArrayList<HomeResultFocusImgList> focusImgList) {
        if (classify_tab_home_cycle_view_icv == null || focusImgList == null || focusImgList.size() ==0) return;

        List<ADInfo> infos = new ArrayList<ADInfo>();
        List<ImageView> views = new ArrayList<ImageView>();
        for (int i = 0; i < focusImgList.size(); i++) {
            ADInfo info = new ADInfo();
            info.setUrl(focusImgList.get(i).getBannerImgUrl());
            info.setBannerName(focusImgList.get(i).getBannerName());
            info.setTargetType(focusImgList.get(i).getTargetType());
            info.setContent(focusImgList.get(i).getBannerContent());
            info.setTargetValue(focusImgList.get(i).getTargetValue());
            infos.add(info);
        }
        views.add(ViewFactory.getImageView(getActivity(), infos.get(infos.size() - 1).getUrl()));
        for (int i = 0; i < infos.size(); i++) {
            views.add(ViewFactory.getImageView(getActivity(), infos.get(i).getUrl()));
        }
        // 将第一个ImageView添加进来
        views.add(ViewFactory.getImageView(getActivity(), infos.get(0).getUrl()));

        // 设置循环，在调用setData方法前调用
        classify_tab_home_cycle_view_icv.setCycle(true);

        // 在加载数据前设置是否循环
        classify_tab_home_cycle_view_icv.setData(views, infos, mAdCycleViewListener);
        //设置轮播
        classify_tab_home_cycle_view_icv.setWheel(true);

        // 设置轮播时间，默认5000ms
        classify_tab_home_cycle_view_icv.setTime(5000);
        //设置圆点指示图标组居中显示，默认靠右
        classify_tab_home_cycle_view_icv.setIndicatorCenter();
    }

    /**
     * 这个 广告条不滚动。
     *@author DuanChunLin
     *@time 2016/11/17 11:45
     */
    private  void setAdvertisingDate(CycleViewPager cycleViewPager,ArrayList<HomeResultFocusImgList> oneAdvertisingList){
        if(cycleViewPager==null){return;}
        List<ADInfo> advertising = new ArrayList<ADInfo>();
        List<ImageView> views = new ArrayList<ImageView>();
        if(oneAdvertisingList!=null && oneAdvertisingList.size()>0){
            for (int i = 0; i < oneAdvertisingList.size(); i++) {
                ADInfo info = new ADInfo();
                info.setType("url");
                info.setUrl(oneAdvertisingList.get(i).getBannerImgUrl());
                info.setUrl(oneAdvertisingList.get(i).getBannerImgUrl());
                info.setBannerName(oneAdvertisingList.get(i).getBannerName());
                info.setTargetType(oneAdvertisingList.get(i).getTargetType());
                info.setContent(oneAdvertisingList.get(i).getBannerContent());
                info.setTargetValue(oneAdvertisingList.get(i).getTargetValue());
                advertising.add(info);
            }
            for (int i = 0; i < advertising.size(); i++) {
                views.add(ViewFactory.getImageView(getActivity(), advertising.get(i).getUrl()));
            }
            // 设置循环，在调用setData方法前调用
            cycleViewPager.setCycle(false);
            cycleViewPager.setWheel(false);
        }
        else {
            ADInfo info = new ADInfo();
            info.setType("resId");
            info.setTargetType(-1);
            info.setResId(R.drawable.tuiguangtu);
            advertising.add(info);
            views.add(ViewFactory.getImageView(getActivity(), info.getResId()));
            cycleViewPager.setCycle(false);
        }
        cycleViewPager.setIndicatorInvisible();
        // 在加载数据前设置是否循环
        cycleViewPager.setData(views, advertising, mAdCycleViewListener);
    }


    /**
     * 获取网络数据
     *
     * @author hechuang
     * @time 2016/9/20 12:01
     */
    private void getNetData() {
        if (!isPullToRefresh) {
            progressDialog.show();
        }
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("hotPageSize", 6);
        hashMap.put("newPageSize", 6);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
//        OkHttpAPI.addTag("classifyHome");
//        try {
//            Thread.sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        OkHttpAPI.postHttpData("/column/homepage/data/service", jsonData, new GenericsCallback<HomeResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(isDestroy()){return;}
                progressDialog.dismiss();
                myScrollView.onRefreshComplete();
                page_error_rl.setVisibility(View.VISIBLE);
                home_page_emity_view.setVisibility(View.GONE);
            }

            @Override
            public void onResponse(HomeResult response, int id) {
                if(isDestroy()){return;}
                progressDialog.dismiss();
                if (myScrollView != null) {
                    myScrollView.onRefreshComplete();
                }
                String code = response.getResCode();
                if (code.equals("0")) {
                    String historyData = gson.toJson(response);
                    SimpleSharedPreferences.putString("defaultHomeData",
                            historyData, getActivity());
                    setPageData(response);
                } else {
                    page_error_rl.setVisibility(View.VISIBLE);
                    home_page_emity_view.setVisibility(View.GONE);
//                    showToastMsg(response.getResDesc());
                }
            }
        });
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: setPageData
     * @Description: TODO 加载页面数据
     * @author hechuang
     */
    private void setPageData(HomeResult result) {
        page_error_rl.setVisibility(View.GONE);
        home_page_emity_view.setVisibility(View.GONE);
        if (result != null) {
            if (result.getBannerListOne() != null) {
                imageUrlList = new ArrayList<HomeResultFocusImgList>();
                imageUrlList.addAll(result.getBannerListOne());
                setImageCycleDate(imageUrlList);
            }

            if (result.getHotList() != null) {
                mOneVideoList.clear();
                oneVideoTagId = 3;
                oneVideoTypeName = "热门视频";
                classify_home_custom_list_one_title_tv.setText(oneVideoTypeName);
                mOneVideoList.addAll(result.getHotList());
                oneClassifyTabHomeFragmentAdapter.setGroup(mOneVideoList);

                if (oneClassifyTabHomeFragmentAdapter.getGroup().size() > 0) {
                    classify_home_spread_one_cvp.setVisibility(View.VISIBLE);
                    classify_home_custom_list_one_rl.setVisibility(View.VISIBLE);
                } else {
                    classify_home_spread_one_cvp.setVisibility(View.GONE);
                    classify_home_custom_list_one_rl.setVisibility(View.INVISIBLE);
                }
            }

            if (result.getNewList() != null) {
                mTwoVideoList.clear();
                twoVideoTagId = 1;
                twoVideoTypeName = "最新推荐";
                classify_home_custom_list_two_title_tv.setText(twoVideoTypeName);
                mTwoVideoList.addAll(result.getNewList());
                twoClassifyTabHomeFragmentAdapter.setGroup(mTwoVideoList);
                if (twoClassifyTabHomeFragmentAdapter.getGroup().size() > 0) {
                    classify_home_spread_two_cvp.setVisibility(View.VISIBLE);
                    classify_home_custom_list_two_rl.setVisibility(View.VISIBLE);
                } else {
                    classify_home_spread_two_cvp.setVisibility(View.GONE);
                    classify_home_custom_list_two_rl.setVisibility(View.INVISIBLE);
                }
            }
            setAdvertisingDate(classify_home_spread_one_cvp,result.getBannerListTow());
            setAdvertisingDate(classify_home_spread_two_cvp,result.getBannerListThree());
//            if (result.getThreeList() != null) {
//                threeVideoTagId = result.getThreeList().getTagId();
//                threeVideoTypeName = result.getThreeList().getTagName();
//                home_game_explode_tv.setText(threeVideoTypeNamemeitingguo );
//                homeFragmentAdapterForGame.setGroup(result.getThreeList()
//                        .getResourcesList());
//                homeFragmentAdapterForGame.notifyDataSetChanged();
//            }
            handler.sendEmptyMessageDelayed(0,20);
        }
    }

    @Override
    public void onDestroyView() {
        classify_tab_home_cycle_view_icv.onDestroyView();
        classify_home_spread_one_cvp.onDestroyView();
        classify_home_spread_two_cvp.onDestroyView();
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        oneClassifyTabHomeFragmentAdapter = null;
        twoClassifyTabHomeFragmentAdapter=null;
        mAdCycleViewListener = null;
        handler.removeMessages(0);
    }



    @OnClick({R.id.classify_home_custom_list_one_rl, R.id.classify_home_custom_list_two_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.classify_home_custom_list_one_rl:
//                if(aa){
//                    MLog.d("开始请求");
//                    getNetData();
//                    aa = false;
//                }else{
//                    MLog.d("取消请求");
//                    OkHttpAPI.cancelTag("classifyHome");
//                    aa = true;
//                }
                Bundle bundle = new Bundle();
                bundle.putInt("videoTypeId", oneVideoTagId);
                bundle.putString("videoTypeName", oneVideoTypeName);
                showActivity(getActivity(), ClassifyTypeListActivity.class,
                        bundle);
                break;
            case R.id.classify_home_custom_list_two_rl:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("videoTypeId", twoVideoTagId);
                bundle1.putString("videoTypeName", twoVideoTypeName);
                showActivity(getActivity(), ClassifyTypeListActivity.class,
                        bundle1);
                break;
        }
    }
}
