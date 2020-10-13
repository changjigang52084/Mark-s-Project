package com.xunixianshi.vrshow.my.homePage;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshRecyclerView;
import com.hch.viewlib.widget.view.CircleImageView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.my.EmptyActivity;
import com.xunixianshi.vrshow.my.LoginActivity;
import com.xunixianshi.vrshow.my.MyConcernActivity;
import com.xunixianshi.vrshow.my.MyCustomerServiceActivity;
import com.xunixianshi.vrshow.my.MyFansActivity;
import com.xunixianshi.vrshow.my.MyMessageActivity;
import com.xunixianshi.vrshow.my.UserActivitysActivity;
import com.xunixianshi.vrshow.my.assemble.AssembleListActivity;
import com.xunixianshi.vrshow.my.viewItem.UserEqualLabel;
import com.xunixianshi.vrshow.obj.ContentReviewedListObj;
import com.xunixianshi.vrshow.obj.ContentReviewedObj;
import com.xunixianshi.vrshow.obj.UserMessageResult;
import com.xunixianshi.vrshow.recyclerview.BindHeadViewHolderListener;
import com.xunixianshi.vrshow.recyclerview.RecycleViewDivider;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseAdapter;
import com.xunixianshi.vrshow.recyclerview.RecyclerBaseItemOnClickListener;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;
import com.zhy.http.okhttp.utils.PicassoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * Created by duan on 2016/9/20.
 */
public class OtherHomePageFragment extends BaseFra {

    private static final int POST_RESULT_ERROR = -1;
    private static final int POST_RESULT_OK = 0;

    @Bind(R.id.homepage_recycler_view)
    PullToRefreshRecyclerView mPullToRefreshRecyclerView;

    RecyclerView homepage_recycler_view;
    private SingleContentAdapter mSingleContentAdapter;

    private Map<String, UserEqualLabel> userEqualLabelMap = new HashMap<>();
    private Map<String, OtherHomePageFragment.EqualItem> equalItemMap = new HashMap<>();
    private boolean mIsFollow = false;
    private int mFansTotal = 0;

    private LoadingAnimationDialog loadingAnimationDialog;
    private String otherHomeId;

    private int mContentListPage = 1;
    private View mRecyclerHeaderView;
    private UserMessageResult mUserMessageResult;

    private boolean isPullToRefresh = false;

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View view = inflater.inflate(R.layout.fragment_homepage_others, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void initView(View parentView) {
        mPullToRefreshRecyclerView.setMode(PullToRefreshBase.Mode.BOTH);
        homepage_recycler_view = mPullToRefreshRecyclerView.getRefreshableView();
        homepage_recycler_view.setHasFixedSize(true);

        loadingAnimationDialog = new LoadingAnimationDialog(getActivity());
        homepage_recycler_view.setLayoutManager(new LinearLayoutManager(getActivity()));
        mSingleContentAdapter = new SingleContentAdapter(getContext());
        homepage_recycler_view.setAdapter(mSingleContentAdapter);
        homepage_recycler_view.addItemDecoration(new RecycleViewDivider(getActivity(), LinearLayoutManager.VERTICAL, ScreenUtils.dip2px(getActivity(), 5), ContextCompat.getColor(getActivity(), R.color.color_e6e6e6)));

        mRecyclerHeaderView = LayoutInflater.from(getActivity()).inflate(R.layout.item_homepage_others_header, homepage_recycler_view, false);
        mSingleContentAdapter.setHeaderViewInterface(new BindHeadViewHolderListener() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateHeadViewHolder(ViewGroup parent, int viewType) {
                return new OtherHomeHeadHolder(mRecyclerHeaderView);
            }

            @Override
            public void onBindHeadViewHolder(RecyclerView.ViewHolder viewHolder) {
                OtherHomeHeadHolder otherHomeHeadHolder = (OtherHomeHeadHolder) viewHolder;
                otherHomeHeadHolder.attachFollowAndFansView();
                otherHomeHeadHolder.setNetDate(mUserMessageResult);
            }
        });
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    protected void initData() {
        otherHomeId = getArguments().getString("showId");
        initListener();
    }

    /**
     * 初始化监听器
     *
     * @author hechuang
     * @time 2016/9/21 12:01
     */
    private void initListener() {
        mPullToRefreshRecyclerView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<RecyclerView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                isPullToRefresh = true;
                postHomePageInfo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                // 下拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                isPullToRefresh = true;
                postHomePageContentList(mContentListPage);
            }
        });

        mSingleContentAdapter.setRecyclerBaseItemOnClickListener(new RecyclerBaseItemOnClickListener() {
            @Override
            public void onClick(int position) {
                ContentReviewedListObj contentReviewedListObj = mSingleContentAdapter.getItem(position);
                Intent intent = new Intent();
                intent.setClass(getActivity(), ClassifyVideoDetialActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("videoTypeId", contentReviewedListObj.getResourceId());
                intent.putExtras(bundle);
                getActivity().startActivity(intent);
            }
        });
    }


    @Override
    public void onResume() {
        super.onResume();
        isPullToRefresh = false;
        postHomePageInfo();
    }


    @OnClick({R.id.info_personal_back_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.info_personal_back_iv:
                getActivity().finish();
                break;
        }
    }

    private void postStart() {
        if (!isPullToRefresh) {
            loadingAnimationDialog.show();
        }
    }

    private void postComplete(int state, String msg) {
        loadingAnimationDialog.dismiss();
        if (state == POST_RESULT_ERROR) {
//            showToastMsg(msg);
        }
    }

    private void setNetDate(UserMessageResult result) {
        mUserMessageResult = result;
        mIsFollow = result.getIsFollow() == 1;
        mFansTotal = result.getUserFansTotal();
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: getUserInfo
     * @Description: TODO 获取用户信息
     * @author hechuang
     */
    private void postHomePageInfo() {
        postStart();
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        String Ras_loginUserId = VR_RsaUtils.encodeByPublicKey(otherHomeId);
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID.equals("") ? "-1" : AppContent.UID);
        // 封装参数
        hashMap.put("userId", Ras_loginUserId);
        hashMap.put("loginUserId", Ras_uid);
        hashMap.put("queryType", 2);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData(
                "/user/zone/homepage/service", jsonData,
                new GenericsCallback<UserMessageResult>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        postComplete(POST_RESULT_ERROR, "网络连接失败，请检查网络！");
                    }

                    @Override
                    public void onResponse(UserMessageResult result, int id) {
                        if (result.getResCode().equals("0")) {
                            setNetDate(result);
                            mSingleContentAdapter.notifyDataSetChanged();
                            mContentListPage = 1;
                            postHomePageContentList(mContentListPage);
                        } else {
                            postComplete(POST_RESULT_ERROR, result.getResDesc());
                        }
                    }
                });
    }


    /**
     * 获取主页内容列表
     *
     * @author DuanChunLin
     * @time 2016/10/9 15:20
     */
    private void postHomePageContentList(int page) {
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(otherHomeId);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("resourceStatus", 3); //3已上架，-1全部内容
        hashMap.put("userId", Ras_uid);
        hashMap.put("resourcesType", 1);

        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        // 获取网络数据
        OkHttpAPI.postHttpData("/user/resources/list/service", jsonData, new GenericsCallback<ContentReviewedObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                postComplete(POST_RESULT_OK, null);
                mPullToRefreshRecyclerView.onRefreshComplete();
            }

            @Override
            public void onResponse(ContentReviewedObj response, int id) {
                postComplete(POST_RESULT_OK, null);
                mPullToRefreshRecyclerView.onRefreshComplete();
                if (response.getResCode().equals("0")) {
                    if (mContentListPage == 1) {
                        mSingleContentAdapter.clearGroup(true);
                    }
                    if (response.getDataList().size() > 0) {
                        mSingleContentAdapter.addItems(response.getDataList());
                        mContentListPage++;
                    }
                }
            }
        });
    }

    private void postUserFollow() {
        if (AppContent.UID.equals("")) {
            showToastMsg("用户没有登录，请先登录！");
            showActivity(getActivity(), LoginActivity.class);
            return;
        }
        postStart();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        String Ras_uid_other = VR_RsaUtils.encodeByPublicKey(otherHomeId);
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);

        hashMap.put("beFollowerUserId", Ras_uid_other);  // 关注者用户ID(关注谁用户ID)，字符串，非空，RSA加密
        hashMap.put("followerUserId", Ras_uid); // 关注者用户ID(发起者用户用户ID)，字符串，非空，RSA加密
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/attention/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                postComplete(POST_RESULT_OK, null);
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        mFansTotal = mFansTotal + 1;
                        mIsFollow = true;
                        mSingleContentAdapter.notifyDataSetChanged();
                    } else {
//                        showToastMsg(result.getString("resDesc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                postComplete(POST_RESULT_ERROR, "网络连接失败!请检查网络");
            }
        });
    }

    private void postUserUnFollow() {
        if (AppContent.UID.equals("")) {
            showToastMsg("请登录!");
            return;
        }
        postStart();
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        String Ras_uid_other = VR_RsaUtils.encodeByPublicKey(otherHomeId);
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);

        hashMap.put("beFollowerUserId", Ras_uid_other);  // 关注者用户ID(关注谁用户ID)，字符串，非空，RSA加密
        hashMap.put("followerUserId", Ras_uid);// 关注者用户ID(发起者用户用户ID)，字符串，非空，RSA加密
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);

        OkHttpAPI.postObjectData("/user/attention/cancel/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                postComplete(POST_RESULT_OK, null);
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        mFansTotal = mFansTotal - 1;
                        mIsFollow = false;
                        mSingleContentAdapter.notifyDataSetChanged();
                    } else {
//                        showToastMsg(result.getString("resDesc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                postComplete(POST_RESULT_ERROR, "网络连接失败!请检查网络");
            }
        });
    }


    public class OtherHomeHeadHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.home_user_username_tv)
        TextView home_user_username_tv; //名称

        @Bind(R.id.home_user_icon_civ)
        CircleImageView home_user_icon_civ;

        @Bind(R.id.home_user_brief_introduction_content_tv)
        TextView home_user_brief_introduction_content_tv; //描述

        @Bind(R.id.user_lv_title)
        TextView user_lv_title; //等级

        @Bind(R.id.user_vitality_tv)
        TextView user_vitality_tv; //人气

        @Bind(R.id.user_leave_a_message_tv)
        TextView user_leave_a_message_tv; //留言

        @Bind(R.id.user_his_content_tv)
        TextView user_his_content_tv; //内容条数

        @Bind(R.id.home_user_follow_btn)
        TextView home_user_follow_btn;

        @Bind(R.id.home_user_un_follow_btn)
        TextView home_user_un_follow_btn;


        public OtherHomeHeadHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
            equalItemMap.put("COMPILATIONS", new EqualItem(R.id.user_equal_compilations_rl, R.string.user_compilations, AssembleListActivity.class.getName()));
            equalItemMap.put("FOLLOW", new EqualItem(R.id.user_equal_follow_rl, R.string.user_attention, MyConcernActivity.class.getName()));
            equalItemMap.put("FANS", new EqualItem(R.id.user_equal_fans_rl, R.string.user_fans, MyFansActivity.class.getName()));
            equalItemMap.put("ACTIVITY", new EqualItem(R.id.user_equal_activity_rl, R.string.user_activity, UserActivitysActivity.class.getName()));
            initEqualLabel(view);
        }

        private void initEqualLabel(View view) {
            userEqualLabelMap.clear();
            for (String key : equalItemMap.keySet()) {
                EqualItem equalItem = equalItemMap.get(key);
                userEqualLabelMap.put(key, new UserEqualLabel(view.findViewById(equalItem.mId), key, equalItem.mStrRes, equalLabelClick));
            }
        }

        private void attachFollowAndFansView() {
            if (AppContent.UID.equals(otherHomeId)) {
                home_user_follow_btn.setVisibility(View.GONE);
                home_user_un_follow_btn.setVisibility(View.GONE);
            } else {
                if (mIsFollow) {
                    home_user_follow_btn.setVisibility(View.GONE);
                    home_user_un_follow_btn.setVisibility(View.VISIBLE);
                } else {
                    home_user_follow_btn.setVisibility(View.VISIBLE);
                    home_user_un_follow_btn.setVisibility(View.GONE);
                }
            }
            userEqualLabelMap.get("FANS").setContentNum(mFansTotal);
        }

        @OnClick({R.id.user_leave_a_message_tv, R.id.home_user_follow_btn, R.id.home_user_un_follow_btn})
        public void onClick(View view) {
            switch (view.getId()) {

                case R.id.user_leave_a_message_tv:
                    Intent intentMessage = new Intent(getActivity(), MyMessageActivity.class);
                    intentMessage.putExtra("MessageActivityUID", otherHomeId);
                    intentMessage.putExtra("MessageActivityName", "他的留言");
                    intentMessage.putExtra("MessageActivityType", "2");
                    startActivity(intentMessage);
                    break;
                case R.id.home_user_follow_btn:
                    postUserFollow();
                    break;
                case R.id.home_user_un_follow_btn:
                    postUserUnFollow();
                    break;
            }
        }

        private View.OnClickListener equalLabelClick = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UserEqualLabel equalLabel = userEqualLabelMap.get(v.getTag());
                if (equalLabel != null) {
                    EqualItem equalItem = equalItemMap.get(v.getTag());
                    Intent intent = new Intent();
                    switch (equalItem.mId) {
                        case R.id.user_equal_follow_rl: // 他的关注
                            intent.putExtra("ConcernActivityUID", otherHomeId);
                            intent.putExtra("ConcernActivityName", "他的关注");
                            intent.putExtra("ConcernActivityQueryType", "1");
                            break;
                        case R.id.user_equal_fans_rl: // 他的粉丝
                            intent.putExtra("FansActivityUID", otherHomeId);
                            intent.putExtra("FansActivityName", "他的粉丝");
                            intent.putExtra("FansActivityQueryType", "2");
                            break;
                        case R.id.user_equal_compilations_rl: // 他的合集
                            intent.putExtra(AssembleListActivity.ASSEMBLE_USER_ID, otherHomeId);
                            intent.putExtra(AssembleListActivity.ASSEMBLE_USER_TYPE, 1);
                            break;
                        case R.id.user_equal_activity_rl: // 他的合集
                            intent.putExtra("ActivitysUID", otherHomeId);
                            intent.putExtra("ActivitysName", "他的活动");
                            break;
                    }
                    try {
                        intent.setClass(getActivity(), Class.forName(equalItem.mJumpClassName));
                        startActivity(intent);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        /**
         * 填充数据
         *
         * @author DuanChunLin
         * @time 2016/10/9 15:25
         */
        private void setNetDate(UserMessageResult userMessageResult) {
            if (userMessageResult == null) {
                return;
            }
            String personalProfile = "这人很懒……，什么都没有留下！";
            if (userMessageResult.getUserPersonalProfile() != null && !userMessageResult.getUserPersonalProfile().equals("-1")) {
                personalProfile = userMessageResult.getUserPersonalProfile();
            }
            home_user_brief_introduction_content_tv.setText(personalProfile);
            home_user_username_tv.setText(userMessageResult.getUserName());
            PicassoUtil.loadImage(getActivity(), userMessageResult.getUserIconUrl() + "?imageView2/2/w/" + (ScreenUtils.dip2px(getActivity(), 100) + 120), home_user_icon_civ, R.drawable.registered_status);
            user_lv_title.setText("LV:" + userMessageResult.getUserLevel());
            user_vitality_tv.setText("人气：" + userMessageResult.getUserPopularity());
            user_leave_a_message_tv.setText("留言：" + userMessageResult.getUserMessageOtherTotal());
            user_his_content_tv.setText(String.format(getString(R.string.user_num_format), userMessageResult.getUserResourceTotal()));
            userEqualLabelMap.get("COMPILATIONS").setContentNum(userMessageResult.getUserStruggleTotal());
            userEqualLabelMap.get("FOLLOW").setContentNum(userMessageResult.getUserFollowTotal());
            userEqualLabelMap.get("FANS").setContentNum(userMessageResult.getUserFansTotal());
            userEqualLabelMap.get("ACTIVITY").setContentNum(userMessageResult.getUserActivityTotal());
            attachFollowAndFansView();
        }
    }

    private class EqualItem {
        final int mId;
        final int mStrRes;
        final String mJumpClassName;

        EqualItem(@IdRes final int id, @StringRes final int strRes, String className) {
            this.mId = id;
            this.mStrRes = strRes;
            this.mJumpClassName = className;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
