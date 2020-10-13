package com.xunixianshi.vrshow.my;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.IdRes;
import androidx.annotation.StringRes;

import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshScrollView;
import com.hch.viewlib.widget.view.CircleImageView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.my.assemble.AssembleListActivity;
import com.xunixianshi.vrshow.my.localVideo.ObtainPhoneVideoActivity;
import com.xunixianshi.vrshow.my.localVideo.util.LocalVideoUtil;
import com.xunixianshi.vrshow.my.myDownLoad.MyDownLoadsActivity;
import com.xunixianshi.vrshow.my.viewItem.UserEqualLabel;
import com.xunixianshi.vrshow.my.viewItem.UserLinearLabel;
import com.xunixianshi.vrshow.obj.UserMessageResult;
import com.xunixianshi.vrshow.permissions.RxPermissions;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.SearchFileUtil;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.PicassoUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * Created by Administrator on 2016/9/18.
 */
public class MyFragment extends BaseFra implements View.OnClickListener {

    @Bind(R.id.my_fragment_login_tv)
    TextView my_fragment_login_tv;
    @Bind(R.id.my_fragment_register_tv)
    TextView my_fragment_register_tv;

    @Bind(R.id.home_user_icon_civ)
    CircleImageView home_user_icon_civ;

    @Bind(R.id.my_fragment_login_rl)
    RelativeLayout my_fragment_login_rl;

    @Bind(R.id.my_fragment_unLogin_rl)
    RelativeLayout my_fragment_unLogin_rl;

    @Bind(R.id.my_fragment_username_tv)
    TextView my_fragment_username_tv;

    @Bind(R.id.my_fragment_rl)
    RelativeLayout my_fragment_rl;

    @Bind(R.id.my_fragment_user_equal_ll)
    LinearLayout my_fragment_user_equal_ll;

    @Bind(R.id.user_leave_a_message_tv)
    TextView user_leave_a_message_num_tv;

    @Bind(R.id.my_fragment_brief_introduction_content_tv)
    TextView my_fragment_brief_introduction_content_tv;

    @Bind(R.id.user_lv_title_tv)
    TextView user_lv_title_tv;

    @Bind(R.id.user_lv_progress_pb)
    ProgressBar user_lv_progress_pb;
    @Bind(R.id.user_vitality_tv)
    TextView user_vitality_tv;

    @Bind(R.id.home_user_pull_to_refresh_sv)
    PullToRefreshScrollView home_user_pull_to_refresh_sv;

    private boolean isCanRefreshLocalVideo;

    private String username;
    private String userPersonalProfile;
    private Map<String, UserLinearLabel> linearLabelMap = new HashMap<>();
    private Map<String, UserEqualLabel> equalLabelMap = new HashMap<>();

    private Map<String, LinearItem> linearItemMap = new HashMap<>();
    private Map<String, EqualItem> equalItemMap = new HashMap<>();

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_homepage_user, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView(View parentView) {
        linearItemMap.put("MyCompilations", new LinearItem(R.id.user_linear_my_compilations_rl, R.drawable.my_collection_login, R.string.user_label_my_compilations, AssembleListActivity.class.getName()));
        linearItemMap.put("MyActivity", new LinearItem(R.id.user_linear_my_activity_rl, R.drawable.my_activites_login, R.string.user_label_my_activity, UserActivitysActivity.class.getName()));
        linearItemMap.put("MyCollection", new LinearItem(R.id.user_linear_my_collection_rl, R.drawable.my_collect_login, R.string.user_label_my_collection, MyCollectListActivity.class.getName()));
        linearItemMap.put("Watched", new LinearItem(R.id.user_linear_watched_rl, R.drawable.my_viewing_history_login, R.string.user_label_watched, MyPlayHistoryActivity.class.getName()));
        linearItemMap.put("ShareContent", new LinearItem(R.id.user_linear_share_content_rl, R.drawable.my_share_content_login, R.string.user_label_share_content, MyShareListActivity.class.getName()));
        linearItemMap.put("MyComment", new LinearItem(R.id.user_linear_my_comment_rl, R.drawable.my_comments_login, R.string.user_label_my_comment, MyCommentsActivity.class.getName()));
        linearItemMap.put("MyDownload", new LinearItem(R.id.user_linear_my_download_rl, R.drawable.my_download_login, R.string.user_label_my_download, MyDownLoadsActivity.class.getName()));
        linearItemMap.put("LocalVideo", new LinearItem(R.id.user_linear_local_video_rl, R.drawable.my_local_video, R.string.user_label_local_video, ObtainPhoneVideoActivity.class.getName()));
        linearItemMap.put("MyService", new LinearItem(R.id.user_linear_my_service_rl, R.drawable.my_customer_service, R.string.user_label_my_service, MyCustomerServiceActivity.class.getName()));
        linearItemMap.put("MySetting", new LinearItem(R.id.user_linear_my_setting_rl, R.drawable.my_setting, R.string.user_label_my_setting, SettingActivity.class.getName()));


        equalItemMap.put("CONTENT", new EqualItem(R.id.user_equal_content_rl, R.string.user_content, MyContentManagerActivity.class.getName()));
        equalItemMap.put("FOLLOW", new EqualItem(R.id.user_equal_follow_rl, R.string.user_attention, MyConcernActivity.class.getName()));
        equalItemMap.put("FANS", new EqualItem(R.id.user_equal_fans_rl, R.string.user_fans, MyFansActivity.class.getName()));

        initLinearLabel(parentView);
        initEqualLabel(parentView);
        initListener();
    }

    private View.OnClickListener linearLabelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserLinearLabel linearLabel = linearLabelMap.get(v.getTag());
            if (linearLabel != null) {
                LinearItem linearItem = linearItemMap.get(v.getTag());
                Intent intent = new Intent();

                switch (linearItem.mId) {
                    case R.id.user_linear_my_setting_rl:
                    case R.id.user_linear_my_service_rl:
                    case R.id.user_linear_local_video_rl:
                        break;
                    case R.id.user_linear_my_activity_rl:
                        if (!checkUserLogin()) {
                            return;
                        }
                        break;
                    case R.id.user_linear_my_compilations_rl:
                        intent.putExtra(AssembleListActivity.ASSEMBLE_USER_ID, AppContent.UID);
                        intent.putExtra(AssembleListActivity.ASSEMBLE_USER_TYPE, 0);
                        if (!checkUserLogin()) {
                            return;
                        }
                        break;
                    default:
                        if (!checkUserLogin()) {
                            return;
                        }
                        break;
                }
                try {
                    intent.setClass(getActivity(), Class.forName(linearItem.mJumpClassName));
                    startActivity(intent);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    private void initListener() {
        home_user_pull_to_refresh_sv.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        home_user_pull_to_refresh_sv.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                attachView();
                getUserInfo();
                refreshDownLoadNum();
                refreshLocalVideoNum();
            }
        });
    }

    private boolean checkUserLogin() {
        if (AppContent.UID.equals("")) {
            showToastMsg("用户没有登录，请先登录！");
            showActivity(getActivity(), LoginActivity.class);
            return false;
        } else if (AppContent.LIMIT_LOGIN == 2) {
            showToastMsg("当前登录状态未激活，请激活后重新登录。");
            showActivity(getActivity(), LoginActivity.class);
            return false;
        }
        return true;
    }

    private View.OnClickListener equalLabelClick = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            UserEqualLabel equalLabel = equalLabelMap.get(v.getTag());
            if (equalLabel != null) {
                EqualItem equalItem = equalItemMap.get(v.getTag());
                Intent intent = new Intent();
                if (!checkUserLogin()) {
                    return;
                }
                switch (equalItem.mId) {
                    case R.id.user_equal_content_rl:// 我的内容

                    case R.id.user_equal_follow_rl:// 我的关注
                        intent.putExtra("ConcernActivityUID", AppContent.UID);
                        intent.putExtra("ConcernActivityName", "我的关注");
                        intent.putExtra("ConcernActivityQueryType", "1");
                        break;
                    case R.id.user_equal_fans_rl:// 我的粉丝
                        intent.putExtra("FansActivityUID", AppContent.UID);
                        intent.putExtra("FansActivityName", "我的粉丝");
                        intent.putExtra("FansActivityQueryType", "2");
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

    private void initLinearLabel(View view) {
        linearLabelMap.clear();
        for (String key : linearItemMap.keySet()) {
            LinearItem linearItem = linearItemMap.get(key);
            linearLabelMap.put(key, new UserLinearLabel(view.findViewById(linearItem.mId), key, linearItem.mImageRes, linearItem.mStrRes, linearLabelClick));
        }
    }

    private void initEqualLabel(View view) {
        equalLabelMap.clear();
        for (String key : equalItemMap.keySet()) {
            EqualItem equalItem = equalItemMap.get(key);
            equalLabelMap.put(key, new UserEqualLabel(view.findViewById(equalItem.mId), key, equalItem.mStrRes, equalLabelClick));
        }
    }

    @Override
    protected void lazyLoad() {
    }

    @Override
    protected void initData() {
        RxPermissions rxPermissions = RxPermissions.getInstance(getContext());
        rxPermissions.setLogging(true);
        rxPermissions.request(Manifest.permission.READ_EXTERNAL_STORAGE)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                            isCanRefreshLocalVideo = true;
                            refreshLocalVideoNum();
                        } else {
                            // 未获取权限
                            isCanRefreshLocalVideo = false;
                        }
                    }
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }

    @OnClick({R.id.my_fragment_unLogin_rl, R.id.my_fragment_login_rl, R.id.user_leave_a_message_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_fragment_unLogin_rl:
                showActivity(getActivity(), LoginActivity.class);
                break;
            case R.id.my_fragment_login_rl: // 跳转到个人信息页面
                Intent intent = new Intent();
                intent.setClass(getActivity(), PersonalInformationActivity.class);
                startActivity(intent);
                break;
            case R.id.user_leave_a_message_tv:
                Intent intentMessage = new Intent(getActivity(), MyMessageActivity.class);
                intentMessage.putExtra("MessageActivityUID", AppContent.UID);
                intentMessage.putExtra("MessageActivityName", "我的留言");
                intentMessage.putExtra("MessageActivityType", "2");
                startActivity(intentMessage);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("cjg","onResume...");
        attachView();
        getUserInfo();
        refreshDownLoadNum();
        refreshLocalVideoNum();
    }

    //根据数据刷新界面
    private void attachView() {
        boolean isUID = !AppContent.UID.equals("");
        Log.d("cjg","attachView isUID: " + isUID);
        my_fragment_unLogin_rl.setVisibility(isUID ? View.GONE : View.VISIBLE);
        my_fragment_login_rl.setVisibility(isUID ? View.VISIBLE : View.GONE);
    }

    /**
     * 刷新本地视频数据库
     *
     * @author DuanChunLin
     * @time 2016/10/19 18:59
     */
    private void refreshLocalVideoNum() {
        new Handler().post(new Runnable() {
            @Override
            public void run() {
                if (isCanRefreshLocalVideo) {
                    final int num = LocalVideoUtil.getLocalVideoSize(getActivity(), getActivity().getContentResolver());
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            linearLabelMap.get("LocalVideo").setUserLinearNum(getString(R.string.user_num_format), num);
                        }
                    });
                } else {
                    linearLabelMap.get("LocalVideo").setUserLinearNum(getString(R.string.user_num_format), 0);
                }
            }
        });
    }

    private void refreshDownLoadNum() {
        linearLabelMap.get("MyDownload").setUserLinearNum(getString(R.string.user_num_format), SearchFileUtil.getInstance(getActivity()).getDownLoadModeSize(OkhttpConstant.DOWN_LOAD_NET_VIDEO_TYPE_NAME));
    }

    /**
     * 填充数据
     *
     * @author DuanChunLin
     * @time 2016/10/9 18:49
     * equalItemMap.put("CONTENT", new EqualItem(R.id.user_equal_content_rl, R.string.user_content, MyContentManagerActivity.class.getName()));
     * equalItemMap.put("FOLLOW", new EqualItem(R.id.user_equal_follow_rl, R.string.user_attention, MyConcernActivity.class.getName()));
     * equalItemMap.put("FANS", new EqualItem(R.id.user_equal_fans_rl, R.string.user_fans, MyFansActivity.class.getName()));
     */
    private void setNetDate(UserMessageResult result) {
        String userIcon = result.getUserIconUrl();
        username = result.getUserName();
        my_fragment_username_tv.setText(username);
        user_lv_title_tv.setText("LV:" + result.getUserLevel());
        user_vitality_tv.setText("人气：" + result.getUserPopularity());
        user_leave_a_message_num_tv.setText("留言：" + result.getUserMessageOtherTotal());

        userPersonalProfile = result.getUserPersonalProfile();
        if (StringUtil.isEmpty(userPersonalProfile) || userPersonalProfile.equals("-1")) {
            userPersonalProfile = "还没有写签名，赶紧去写一个威武霸气的签名吧！";
        }
        my_fragment_brief_introduction_content_tv.setText(userPersonalProfile);
        SimpleSharedPreferences.putString("username", username, getActivity());
        PicassoUtil.loadImage(getActivity(), userIcon + "?imageView2/2/w/" + ScreenUtils.dip2px(getActivity(), 100) + 100, home_user_icon_civ, R.drawable.registered_status);
        SimpleSharedPreferences.putString("userIconUrl", userIcon, getActivity());
        linearLabelMap.get("MyCompilations").setUserLinearNum(getString(R.string.user_num_format), result.getUserStruggleTotal());
        linearLabelMap.get("MyActivity").setUserLinearNum(getString(R.string.user_num_format), result.getUserActivityTotal());
        linearLabelMap.get("Watched").setUserLinearNum(getString(R.string.user_num_format), result.getUserPlayerTotal());
        linearLabelMap.get("MyCollection").setUserLinearNum(getString(R.string.user_num_format), result.getUsercollectionsTotal());
        linearLabelMap.get("ShareContent").setUserLinearNum(getString(R.string.user_num_format), result.getUserShareTotal());
        linearLabelMap.get("MyComment").setUserLinearNum(getString(R.string.user_num_format), result.getUserCommentTotal());


        equalLabelMap.get("CONTENT").setContentNum(result.getUserResourceTotal());
        equalLabelMap.get("FOLLOW").setContentNum(result.getUserFollowTotal());
        equalLabelMap.get("FANS").setContentNum(result.getUserFansTotal());
    }

    private void attachLoginView() {
        boolean isVisibility = !AppContent.UID.equals("");
        linearLabelMap.get("MyCompilations").setLinearNumVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        linearLabelMap.get("MyActivity").setLinearNumVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        linearLabelMap.get("Watched").setLinearNumVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        linearLabelMap.get("MyCollection").setLinearNumVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        linearLabelMap.get("ShareContent").setLinearNumVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        linearLabelMap.get("MyComment").setLinearNumVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        linearLabelMap.get("MyDownload").setLinearNumVisibility(isVisibility ? View.VISIBLE : View.INVISIBLE);
        my_fragment_user_equal_ll.setVisibility(isVisibility ? View.VISIBLE : View.GONE);
    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: getUserInfo
     * @Description: TODO 获取用户信息
     * @author hechuang
     */
    private void getUserInfo() {
        attachLoginView();
        if (AppContent.UID.equals("")) {
            home_user_pull_to_refresh_sv.onRefreshComplete();
            return;
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId", Ras_uid);
        hashMap.put("loginUserId", Ras_uid);
        hashMap.put("queryType", 1);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData(
                "/user/zone/homepage/service", jsonData,
                new GenericsCallback<UserMessageResult>() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
//                        showToastMsg("网络连接失败，请检查网络！");
                        home_user_pull_to_refresh_sv.onRefreshComplete();
                    }

                    @Override
                    public void onResponse(UserMessageResult result, int id) {
                        home_user_pull_to_refresh_sv.onRefreshComplete();
                        if (result.getResCode().equals("0")) {
                            setNetDate(result);
                        } else {
//                            showToastMsg(result.getResDesc());
                        }
                    }
                });
    }

    private class LinearItem {
        final int mId;
        final int mImageRes;
        final int mStrRes;
        final String mJumpClassName;

        LinearItem(@IdRes final int id, @DrawableRes final int imageRes, @StringRes final int strRes, String className) {
            this.mId = id;
            this.mImageRes = imageRes;
            this.mStrRes = strRes;
            this.mJumpClassName = className;
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
}
