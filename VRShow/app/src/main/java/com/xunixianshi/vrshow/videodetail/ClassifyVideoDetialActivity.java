package com.xunixianshi.vrshow.videodetail;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentTransaction;

import com.google.gson.Gson;
import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.FileDownloaderModel;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.customview.NoScrollListView;
import com.hch.viewlib.customview.UpDownNoScrollGridView;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.NetUtil;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.util.StringUtils;
import com.hch.viewlib.widget.NoScrollGridView;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.hch.viewlib.widget.view.CircleImageView;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.classify.CommentPushActivity;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.customview.ShareDialog;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.my.LoginActivity;
import com.xunixianshi.vrshow.my.MyCustomerServiceActivity;
import com.xunixianshi.vrshow.my.homePage.OtherHomePageActivity;
import com.xunixianshi.vrshow.obj.ClassifyVideoDetailResult;
import com.xunixianshi.vrshow.obj.ClassifyVideoDetailResultSimilarList;
import com.xunixianshi.vrshow.obj.CommentListObj;
import com.xunixianshi.vrshow.obj.CommentObj;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.obj.HttpZanObj;
import com.xunixianshi.vrshow.player.VRPlayerFragment;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;
import com.zhy.http.okhttp.utils.PicassoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * @author hechuang
 * @ClassName: ClassifyVideoSurfaceActivity
 * @Description: TODO 视频播放详情页面
 * @date 2016年3月15日 下午4:46:17
 */
public class ClassifyVideoDetialActivity extends BaseAct {

    public static final String ACTION_NAME = "videoDownload";
    @Bind(R.id.classify_detail_play_icon_iv)
    ImageView classify_detail_play_icon_iv;
    @Bind(R.id.classify_detail_video_play_iv)
    ImageView classify_detail_video_play_iv;
    @Bind(R.id.classify_detail_back_iv)
    ImageView classify_detail_back_iv;
    @Bind(R.id.classify_detail_title_rl)
    RelativeLayout classify_detail_title_rl;
    @Bind(R.id.classify_detail_video_name_tv)
    TextView classify_detail_video_name_tv;
    @Bind(R.id.classify_detail_video_play_count_tv)
    TextView classify_detail_video_play_count_tv;
    @Bind(R.id.classify_detail_arrow_down_grey_img_btn)
    ImageButton classify_detail_arrow_down_grey_img_btn;
    @Bind(R.id.classify_detail_video_user_rl)
    RelativeLayout classify_detail_video_user_rl;
    @Bind(R.id.classify_detail_video_message_ll)
    LinearLayout classify_detail_video_message_ll;
    @Bind(R.id.classify_detail_video_introduce_tv)
    TextView classify_detail_video_introduce_tv;
    @Bind(R.id.classify_detail_video_introduce_rl)
    RelativeLayout classify_detail_video_introduce_rl;
    @Bind(R.id.classify_detail_video_title_rl)
    RelativeLayout classify_detail_video_title_rl;
    @Bind(R.id.classify_detail_video_zan_ib)
    ImageButton classify_detail_video_zan_ib;
    @Bind(R.id.classify_detail_video_zan_num_tv)
    TextView classify_detail_video_zan_num_tv;
    @Bind(R.id.classify_detail_video_zan_add_one_tv)
    TextView classify_detail_video_zan_add_one_tv;
    @Bind(R.id.classify_detail_video_zan_ll)
    LinearLayout classify_detail_video_zan_ll;
    @Bind(R.id.classify_detail_video_download_ll)
    LinearLayout classify_detail_video_download_ll;
    @Bind(R.id.classify_detail_video_share_ll)
    LinearLayout classify_detail_video_share_ll;
    @Bind(R.id.classify_detail_video_collect_ib)
    ImageButton classify_detail_video_collect_ib;
    @Bind(R.id.classify_detail_video_collect_ll)
    LinearLayout classify_detail_video_collect_ll;
    @Bind(R.id.classify_detail_video_recommend_list_gv)
    UpDownNoScrollGridView classify_detail_video_recommend_list_gv;
    @Bind(R.id.classify_detail_video_recommend_list_hsv)
    HorizontalScrollView classify_detail_video_recommend_list_hsv;
    @Bind(R.id.classify_detail_video_recommend_list_rl)
    RelativeLayout classify_detail_video_recommend_list_rl;
    @Bind(R.id.classify_all_comment_arrow_right_iv)
    ImageView classify_all_comment_arrow_right_iv;
    @Bind(R.id.classify_comment_count_tv)
    TextView classify_comment_count_tv;
    @Bind(R.id.classify_all_comment_num_rl)
    RelativeLayout classify_all_comment_num_rl;
    @Bind(R.id.classify_comment_ll)
    RelativeLayout classify_comment_ll;
    @Bind(R.id.classify_comment_nslv)
    NoScrollListView classify_comment_nslv;
    @Bind(R.id.classify_more_comment_ll)
    LinearLayout classify_more_comment_ll;
    @Bind(R.id.classify_more_comment_iv)
    ImageView classify_more_comment_iv;
    @Bind(R.id.classify_detail_video_scroll_view_rl)
    RelativeLayout classify_detail_video_scroll_view_rl;
    @Bind(R.id.myScrollView)
    ScrollView myScrollView;
    @Bind(R.id.classify_more_comment_rl)
    RelativeLayout classify_more_comment_rl;
    @Bind(R.id.classify_more_comment_prlv)
    PullToRefreshListView classify_more_comment_prlv;
    @Bind(R.id.classify_more_comment_push_ll)
    LinearLayout classify_more_comment_push_ll;

    @Bind(R.id.classify_detail_video_user_name)
    TextView classify_detail_video_user_name;
    @Bind(R.id.classify_detail_video_user_grade)
    TextView classify_detail_video_user_grade;
    @Bind(R.id.classify_detail_video_user_civ)
    CircleImageView classify_detail_video_user_civ;

    @Bind(R.id.classify_more_comment_defult_icon_iv)
    CircleImageView classify_more_comment_defult_icon_iv;

    @Bind(R.id.classify_detail_video_more_rl)
    RelativeLayout classify_detail_video_more_rl;
    @Bind(R.id.classify_more_comment_defult_icon_rl)
    RelativeLayout classify_more_comment_defult_icon_rl;

    private ClassifyVideoDetialAdapter classifyVideoDetialAdapter;
    private ClassifyVideoCommentAdapter classifyVideoCommentAdapter;
    private ClassifyVideoCommentAdapter classifyVideoMoreCommentAdapter;
    private ArrayList<ClassifyVideoDetailResultSimilarList> classifyVideoDetailResultSimilarLists;
    private ArrayList<CommentListObj> classifyVideoCommentLists;
    private ArrayList<CommentListObj> classifyVideoMoreCommentLists;

    private boolean isShowIntroduce = false;
    long size;
    int percent;
    long beginTime;
    long endTime;
    private ShareDialog mShareDialog;
    private int videoId;
    private int zanNum = 0;
    private String videoName;
    private String videoIntroduce;
    private int isComment;

    private String videoIconUrl; // duanchunlin by 2016.3.23
    private String fileSize; // duanchunlin by2016.3.30
    private int sourceId = -1;
    private int collectId = 0;
    private boolean isSelect = false;
    private boolean isZan = false;
    private Gson gson = new Gson();
    private LoadingAnimationDialog progressDialog;
    UMImage image;
    private int scollTo = 0;
    private Handler handler = new Handler();

    private NetWorkDialog netWorkDialog;
    private VRPlayerFragment mVRPlayerFragment = null;
    private int thisPage = 1;
    Animation btnAnim;
    private int commentTotal;
    private String attachmentUserId;


    @Override
    public void setRootView() {
        setContentView(R.layout.activity_classify_play_video_detail);
        ButterKnife.bind(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    protected void initView() {
        super.initView();
        initListener();
    }

    /**
     * @Title: initListener @Description: TODO 初始化监听器 @author hechuang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void initListener() {
        classify_comment_nslv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                String uid = VR_RsaUtils.decryptByPublicKey(classifyVideoCommentLists.get(position).getUserId());
                bundle.putString("showId", uid);
                showActivity(ClassifyVideoDetialActivity.this, OtherHomePageActivity.class,
                        bundle);
            }
        });
        classify_more_comment_prlv.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                String uid = VR_RsaUtils.decryptByPublicKey(classifyVideoMoreCommentLists.get(position - 1).getUserId());
                bundle.putString("showId", uid);
                showActivity(ClassifyVideoDetialActivity.this, OtherHomePageActivity.class,
                        bundle);
            }
        });

        progressDialog = new LoadingAnimationDialog(ClassifyVideoDetialActivity.this);
        classify_detail_video_recommend_list_gv
                .setOnItemClickListener(new OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        if (!progressDialog.isShowing()) {
                            videoId = classifyVideoDetailResultSimilarLists.get(
                                    position).getResourceId();
                            myScrollView.fullScroll(ScrollView.FOCUS_UP);
                            progressDialog.show();
                            getNetData();
                        }
                    }
                });

        classify_more_comment_prlv
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 下拉刷新 重置所有数据
                        progressDialog.show();
                        thisPage = 1;
                        getCommentData(thisPage);
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 上拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                        getCommentData(thisPage);

                    }
                });

        int screenWith = ScreenUtils
                .getScreenWidth(ClassifyVideoDetialActivity.this);
        int screenHeight = ScreenUtils.getScreenHeight(this);
        LayoutParams lp;
        lp = classify_detail_title_rl.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * screenWith) / screenHeight;
        classify_detail_title_rl.setLayoutParams(lp);


        LayoutParams lp1;
        lp1 = video_detail_player_fragment_rl.getLayoutParams();
        lp1.width = screenWith;
        lp1.height = (int) (screenWith * screenWith) / screenHeight;
        video_detail_player_fragment_rl.setLayoutParams(lp1);

        classify_more_comment_rl.setClickable(true);
        classify_more_comment_defult_icon_rl.setClickable(true);
    }

    private Runnable runnable = new Runnable() {

        @Override
        public void run() {
            myScrollView.scrollTo(0, classify_detail_video_scroll_view_rl.getMeasuredHeight() - myScrollView.getHeight());// 改变滚动条的位置
        }
    };

    @Override
    protected void initData() {
        super.initData();
        String userIconUrl = SimpleSharedPreferences.getString("userIconUrl", ClassifyVideoDetialActivity.this);
        if (!StringUtil.isEmpty(userIconUrl)) {
            PicassoUtil.loadImage(this, userIconUrl + "?imageView2/2/w/" + (ScreenUtils.dip2px(ClassifyVideoDetialActivity.this, 40) + 120), classify_more_comment_defult_icon_iv,R.drawable.registered_status);
        }
        btnAnim = AnimationUtils.loadAnimation(this, R.anim.zan_add_translate);
        videoId = getIntent().getExtras().getInt("videoTypeId");
        mShareDialog = new ShareDialog(ClassifyVideoDetialActivity.this);
        classifyVideoDetialAdapter = new ClassifyVideoDetialAdapter(
                ClassifyVideoDetialActivity.this);
        classifyVideoCommentAdapter = new ClassifyVideoCommentAdapter(
                ClassifyVideoDetialActivity.this, true);
        classifyVideoDetailResultSimilarLists = new ArrayList<ClassifyVideoDetailResultSimilarList>();
        classifyVideoCommentLists = new ArrayList<CommentListObj>();

        classifyVideoDetialAdapter
                .setGroup(classifyVideoDetailResultSimilarLists);

        classifyVideoCommentAdapter
                .setGroup(classifyVideoCommentLists);

        classify_detail_video_recommend_list_gv
                .setAdapter(classifyVideoDetialAdapter);
        classify_comment_nslv
                .setAdapter(classifyVideoCommentAdapter);

        classifyVideoMoreCommentAdapter = new ClassifyVideoCommentAdapter(ClassifyVideoDetialActivity.this, false);
        classifyVideoMoreCommentLists = new ArrayList<CommentListObj>();
        classifyVideoMoreCommentAdapter
                .setGroup(classifyVideoMoreCommentLists);
        classify_more_comment_prlv
                .setAdapter(classifyVideoMoreCommentAdapter);
        classify_more_comment_prlv.setMode(PullToRefreshBase.Mode.BOTH);
        this.percent = 0;
        this.size = 0;
        this.beginTime = 0;
        this.endTime = 0;
        // qq
        PlatformConfig.setQQZone("1105439134", "uUaXoSZAE48yD0fY");
        // 新浪微博
        PlatformConfig.setSinaWeibo("2835953282",
                "5c2053f74978afddea92560f05af2eb5");
        // 微信
        PlatformConfig.setWeixin("wx633b9cf100b10371",
                "50bcd5d0b66fcc0b9b5064ea9aceb169");
        progressDialog.show();
        getNetData();
//        mHandler.sendEmptyMessageDelayed(0,10);
        initPlayerFragment();
        attachToPlayer();
    }

//    private Handler mHandler = new Handler(){
//        @Override
//        public void handleMessage(Message msg) {
//            initPlayerFragment();
//            attachToPlayer();
//        }
//    };
//    @OnClick(R.id.classify_comment_input_send)
//    void classify_comment_input_send() {
//        if (AppContent.UID.equals("")) {
//            showToastMsg("用户没有登录，请先登录！");
//            showActivity(ClassifyVideoDetialActivity.this,
//                    LoginActivity.class);
//        } else if (AppContent.LIMIT_LOGIN == 2) {
//            showToastMsg("当前登录状态未激活，请激活后重新登录。");
//            showActivity(ClassifyVideoDetialActivity.this,
//                    LoginActivity.class);
//        } else {
//            publishComment();
//        }
//    }

    @OnClick({R.id.classify_detail_video_play_iv, R.id.classify_detail_back_iv, R.id.classify_detail_arrow_down_grey_img_btn,
            R.id.classify_detail_video_zan_ll, R.id.classify_detail_video_download_ll,
            R.id.classify_detail_video_share_ll, R.id.classify_detail_video_collect_ll,
            R.id.classify_more_comment_iv, R.id.classify_more_comment_cancel_iv,
            R.id.classify_all_comment_rl, R.id.classify_more_comment_push_iv,
            R.id.classify_detail_video_user_rl, R.id.classify_detail_video_name_play_cunt_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.classify_detail_video_play_iv:
                if(mVRPlayerFragment!=null){
                    video_detail_player_fragment_rl.setVisibility(View.VISIBLE);
                    mVRPlayerFragment.readyToPlay(sourceId, videoName, new VRPlayerFragment.CancelPlayerListener() {
                        @Override
                        public void cancel() {
                            video_detail_player_fragment_rl.setVisibility(View.INVISIBLE);
                        }
                    });
                }
                break;
            case R.id.classify_detail_back_iv:
                ClassifyVideoDetialActivity.this.finish();
                break;
            case R.id.classify_detail_arrow_down_grey_img_btn:
            case R.id.classify_detail_video_name_play_cunt_rl:
                if (isShowIntroduce) {
                    classify_detail_arrow_down_grey_img_btn
                            .setImageResource(R.drawable.arrow_down);
                    classify_detail_video_introduce_rl.setVisibility(View.GONE);
                    isShowIntroduce = false;
                } else {
                    classify_detail_arrow_down_grey_img_btn
                            .setImageResource(R.drawable.arrow_up);
                    classify_detail_video_introduce_rl.setVisibility(View.VISIBLE);
                    isShowIntroduce = true;
                }
                break;
            case R.id.classify_detail_video_zan_ll:
                MLog.d("AppContent.UID:" + AppContent.UID);
                if (AppContent.UID.equals("")) {
                    showToastMsg("用户没有登录，请先登录！");
                    showActivity(ClassifyVideoDetialActivity.this,
                            LoginActivity.class);
                } else if (AppContent.LIMIT_LOGIN == 2) {
                    showToastMsg("当前登录状态未激活，请激活后重新登录。");
                    showActivity(ClassifyVideoDetialActivity.this,
                            LoginActivity.class);
                } else {
                    progressDialog.show();
                    zanVideo();
                }
                break;
//            case R.id.classify_detail_video_comment_ib:
//                handler.postDelayed(runnable, 100);
//                break;
            case R.id.classify_detail_video_download_ll:
                startDownload();
                break;
            case R.id.classify_detail_video_share_ll:
                shareShow();
                break;
            case R.id.classify_detail_video_collect_ll:
                MLog.d("AppContent.UID:" + AppContent.UID);
                if (AppContent.UID.equals("")) {
                    showToastMsg("用户没有登录，请先登录！");
                    showActivity(ClassifyVideoDetialActivity.this,
                            LoginActivity.class);
                } else if (AppContent.LIMIT_LOGIN == 2) {
                    showToastMsg("当前登录状态未激活，请激活后重新登录。");
                    showActivity(ClassifyVideoDetialActivity.this,
                            LoginActivity.class);
                } else {
                    if (isSelect) {
                        cancelCollectVideo();
                    } else {
                        collectVideo();
                    }
                }
                break;
            case R.id.classify_more_comment_iv:
//                Bundle bundle = new Bundle();
//                bundle.putInt("videoId", sourceId);
//                showActivity(ClassifyVideoDetialActivity.this, MoreCommentActivity.class,
//                        bundle);
                classify_more_comment_rl.setVisibility(View.VISIBLE);

                getCommentData(1);
                break;
            case R.id.classify_all_comment_rl:
                if (commentTotal > 0) {
                    classify_more_comment_rl.setVisibility(View.VISIBLE);
                    getCommentData(1);
                }
                break;
            case R.id.classify_more_comment_cancel_iv:
                classify_more_comment_rl.setVisibility(View.GONE);
                break;
            case R.id.classify_more_comment_push_iv:
                MLog.d("AppContent.UID:" + AppContent.UID);
                if (AppContent.UID.equals("")) {
                    showToastMsg("用户没有登录，请先登录！");
                    showActivity(ClassifyVideoDetialActivity.this,
                            LoginActivity.class);
                } else if (AppContent.LIMIT_LOGIN == 2) {
                    showToastMsg("当前登录状态未激活，请激活后重新登录。");
                    showActivity(ClassifyVideoDetialActivity.this,
                            LoginActivity.class);
                } else {
                    Bundle bundle2 = new Bundle();
                    bundle2.putInt("sourceId", sourceId);
                    showActivityForResult(ClassifyVideoDetialActivity.this, CommentPushActivity.class,
                            bundle2);
                }
                break;
            case R.id.classify_detail_video_user_rl:
                Bundle bundle = new Bundle();
                bundle.putString("showId", attachmentUserId);
                showActivity(ClassifyVideoDetialActivity.this, OtherHomePageActivity.class, bundle);
                break;
        }
    }

    /**
     * @Title: getNetData @Description: TODO 获取网络数据 @author hechuang @param 设定文件 @return
     * void 返回类型 @throws
     */
    private void getNetData() {
        String uid;
        if (AppContent.UID.equals("")) {
            uid = "-1";
        } else {
            uid = AppContent.UID;
        }
        // 加密手机号
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourcesId", videoId);
        hashMap.put("userId", Ras_uid);
        /* hashMap.put("userId", Ras_uid); */
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/app/resources/id/detail/service", jsonData, new GenericsCallback<ClassifyVideoDetailResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
//                showToastMsg("网络连接失败， 请检查网络！");
                video_detail_player_fragment_rl.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onResponse(ClassifyVideoDetailResult result, int id) {
                progressDialog.dismiss();
                String historyData = gson.toJson(result);
                SimpleSharedPreferences.putString(
                        "defultClassifyVideoDetailData", historyData,
                        getApplicationContext());
                if (result.getResCode().equals("0")) {
                    setPageData(result);
                } else {
//                    showToastMsg(result.getResDesc());
                    video_detail_player_fragment_rl.setVisibility(View.INVISIBLE);
                }
                getCommentData();
            }
        });

    }

    private void getCommentData() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        MLog.d("!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!videoId:" + videoId);
        hashMap.put("resourcesId", videoId);
        hashMap.put("currPage", 1);
        hashMap.put("pageSize", 5);
        /* hashMap.put("userId", Ras_uid); */
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/comment/list/service", jsonData, new GenericsCallback<CommentObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (isDestroy()) {
                    return;
                }
                progressDialog.dismiss();
//                showToastMsg("网络连接失败， 请检查网络！");
            }

            @Override
            public void onResponse(CommentObj result, int id) {
                if (isDestroy()) {
                    return;
                }
                if (result.getResCode().equals("0")) {
                    commentTotal = result.getTotal();
                    classify_comment_count_tv.setText(result.getTotal() + "条");
                    //  如果评论大于3条则显示更多评论 不大于不显示
                    if (result.getTotal() == 0) {
                        classify_comment_ll.setVisibility(View.GONE);
                    } else {
                        classify_comment_ll.setVisibility(View.VISIBLE);
                    }
                    if (result.getTotal() > 5) {
                        classify_more_comment_ll.setVisibility(View.VISIBLE);
                    } else {
                        classify_more_comment_ll.setVisibility(View.GONE);
                    }
                    if (result.getResourcesCommentList() != null && result.getResourcesCommentList().size() > 0) {
                        classifyVideoCommentLists.clear();
                        classifyVideoCommentLists.addAll(result.getResourcesCommentList());
                        classifyVideoCommentAdapter.setGroup(classifyVideoCommentLists);
                        setListHeight();
                    } else {
                        classifyVideoCommentLists.clear();
                        classifyVideoCommentAdapter.clearGroup(true);
                    }
                } else {
//                    showToastMsg(result.getResDesc());
                }
            }
        });
    }

    private void setListHeight() {
        int totalHeight = 0;
        if (classifyVideoCommentAdapter == null) {
            return;
        }
        for (int i = 0; i < classifyVideoCommentAdapter.getCount(); i++) {
            View listViewItem = classifyVideoCommentAdapter.getView(i, null, classify_comment_nslv);
            // 计算子项View 的宽高
            listViewItem.measure(0, 0);
            // 计算所有子项的高度和
            totalHeight += listViewItem.getMeasuredHeight();
        }
        ViewGroup.LayoutParams params = classify_comment_nslv.getLayoutParams();
        // listView.getDividerHeight()获取子项间分隔符的高度
        // params.height设置ListView完全显示需要的高度
        params.height = totalHeight + ((classify_comment_nslv.getDividerHeight() + 8) * (classifyVideoCommentAdapter.getCount() - 1));
        classify_comment_nslv.setLayoutParams(params);
    }

    /**
     * @param
     * @return void 返回类型
     * @throws
     * @Title: setPageData
     * @Description: TODO 加载页面数据
     * @author changjigang
     */
    private void setPageData(ClassifyVideoDetailResult result) {
        MLog.d("result.getResourcesIcon():" + result.getResourcesIcon());
        PicassoUtil.loadImage(this, result.getResourcesIcon() + "?imageView2/2/w/" + (ScreenUtils.getScreenWidth(ClassifyVideoDetialActivity.this) + 120), classify_detail_play_icon_iv);
        isComment = result.getIsComment();
        videoName = result.getResourcesName();
        sourceId = result.getResourcesId();
        zanNum = result.getLikesTotal();
        videoIntroduce = result.getSmallIntroduce();
        videoIconUrl = result.getResourcesIcon();
        collectId = result.getIsCollection();
        attachmentUserId = result.getAttachmentUserId();
        fileSize = StringUtils.isBlank(result.getFileSize()) ? "0M" : result
                .getFileSize().toUpperCase();
//        try {
//            String gurl2Fix = videoIconUrl.substring(videoIconUrl.lastIndexOf("/") + 1, videoIconUrl.length());
//            gurl2Fix = URLEncoder.encode(gurl2Fix, "UTF-8");
//            videoIconUrl = videoIconUrl.substring(0, videoIconUrl.lastIndexOf("/") + 1) + gurl2Fix;
//            // URL中含有+号，则手动转义
//            videoIconUrl = videoIconUrl.replaceAll("\\+", "%20");
            if(!videoIconUrl.contains("?imageView2")){
                videoIconUrl = videoIconUrl + "?imageView2/2/w/200";
            }
            image = new UMImage(ClassifyVideoDetialActivity.this, videoIconUrl);
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        if (isComment == 0) {
//            classifyCommentContentRl.setVisibility(View.VISIBLE);
//            classifyNotLoginRl.setVisibility(View
// .GONE);
//            return;
//        } else if (isComment == 1) {
//            classifyCommentContentRl.setVisibility(View.GONE);
//            classifyNotLoginRl.setVisibility(View.VISIBLE);
//            return;
//        } else if (isComment == 2) {
//            classifyCommentContentRl.setVisibility(View.GONE);
//            classifyNotLoginRl.setVisibility(View.VISIBLE);
//            return;
//        }
        if (result.getIsCollection() == 0) {
            isSelect = false;
            classify_detail_video_collect_ib
                    .setImageResource(R.drawable.classify_detail_collect_normal);
        } else {
            isSelect = true;
            classify_detail_video_collect_ib
                    .setImageResource(R.drawable.classify_detail_collect_press);
        }
        if (result.getIsLikes() == 0) {
            isZan = false;
            classify_detail_video_zan_ib
                    .setImageResource(R.drawable.classify_detail_zan_normal);
        } else {
            isZan = true;
            classify_detail_video_zan_ib
                    .setImageResource(R.drawable.classify_detail_zan_press);
        }
        classify_detail_video_name_tv.setText(videoName);
        classify_detail_video_play_count_tv.setText("播放:" + result
                .getResourcesNumbers() + "次");
        classify_detail_video_user_name.setText(result.getAttachmentUserName());
        // 由于后台暂时没有等级数据 先暂时设为LV1
//        classify_detail_video_user_grade.setText("LV1");
        classify_detail_video_user_grade.setText("LV" + String.valueOf(result.getAttachmentUserLvl() < 1 ? 1:result.getAttachmentUserLvl()));
        // 由于后台暂时没有数据所以先注释
        PicassoUtil.loadImage(this, result.getAttachmentUserIcon()+"?imageView2/2/w/200", classify_detail_video_user_civ,R.drawable.registered_status);
        classify_detail_video_introduce_tv.setText(result.getLongIntroduce());
        classify_detail_video_zan_num_tv.setText("赞 " + StringUtil.getNumToString(zanNum));
        if (result.getSimilarList() != null && result.getSimilarList().size() > 0) {
            classifyVideoDetailResultSimilarLists.clear();
            classifyVideoDetailResultSimilarLists.addAll(result.getSimilarList());
            setGridView(classifyVideoDetailResultSimilarLists);
        }
        attachToPlayer();
    }


    private void attachToPlayer(){
        if(sourceId!= -1 && mVRPlayerFragment!=null){
            if (NetUtil.isWIFI(this)) {
              mVRPlayerFragment.readyToPlay(sourceId, videoName);
            } else {
                video_detail_player_fragment_rl.setVisibility(View.INVISIBLE);
            }
        }
    }


    private void setGridView(ArrayList<ClassifyVideoDetailResultSimilarList> classifyVideoDetailResultSimilarLists) {
        if (classifyVideoDetailResultSimilarLists == null) {
            return;
        }
        int size = classifyVideoDetailResultSimilarLists.size();
        DisplayMetrics dm = new DisplayMetrics();
        this.getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;     // 屏幕宽度（像素）
        int height = dm.heightPixels;   // 屏幕高度（像素）
        int gridHeight = (int) (dm.density * 25);
        MLog.d("gridHeight::" + gridHeight);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                (width) * size / 2 + 0, (int) ((width / 2) * 0.57) + gridHeight);
//        classify_detail_video_recommend_list_hsv.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,(int) ((width / 2) * 0.57) + gridHeight));
        classify_detail_video_recommend_list_gv.setLayoutParams(params); // 重点
        classify_detail_video_recommend_list_gv.setColumnWidth((width / 2)); // 重点
        classify_detail_video_recommend_list_gv.setHorizontalSpacing(0); //设置间隔
        classify_detail_video_recommend_list_rl.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, (int) ((width / 2) * 0.57) + gridHeight));
//        images_grid.setMinimumHeight((int)( width*(1.0f*width/height)));
        classify_detail_video_recommend_list_gv.setStretchMode(GridView.NO_STRETCH);
        classify_detail_video_recommend_list_gv.setNumColumns(size); // 重点

        classifyVideoDetialAdapter.setGroup(classifyVideoDetailResultSimilarLists);
    }

    /**
     * @Title: collectVideo
     * @Description: TODO 点赞接口
     * @author changjigang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void zanVideo() {
        // 加密用户ID
        String uid;
        if (AppContent.UID.equals("")) {
            uid = "-1";
        } else {
            uid = AppContent.UID;
        }
        String ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourceId", sourceId);
        hashMap.put("userId", ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resource/like/service", jsonData, new GenericsCallback<HttpZanObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
//                showToastMsg("网络连接失败!请检查网络");
            }
            @Override
            public void onResponse(HttpZanObj result, int id) {
                progressDialog.dismiss();
                if (result.getResCode().equals("0")) {
                    zanNum = result.getLikesTotal();
                    if(result.getLikesStatus()==1){
                        classify_detail_video_zan_add_one_tv.setVisibility(View.VISIBLE);
                        classify_detail_video_zan_add_one_tv.startAnimation(btnAnim);
                        new Handler().postDelayed(new Runnable() {
                            public void run() {
                                classify_detail_video_zan_add_one_tv.setVisibility(View.GONE);
                            }
                        }, 1000);
                    }
                    attachZanUi(result.getLikesStatus());
                } else {
//                    showToastMsg(result.getResDesc());
                }
            }
        });
    }


    private void attachZanUi(int likesStatus){
        if (likesStatus == 0) {
            showToastMsg("取消成功");
            classify_detail_video_zan_ib.setImageResource(R.drawable.classify_detail_zan_normal);
        } else if (likesStatus == 1) {
            showToastMsg("点赞成功");
            isZan = true;
            classify_detail_video_zan_ib.setImageResource(R.drawable.classify_detail_zan_press);
        }
        classify_detail_video_zan_num_tv.setText("赞 " + StringUtil.getNumToString(zanNum));
    }


    /**
     * @Title: collectVideo @Description: TODO 收藏视频接口 @author hechuang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void collectVideo() {
        // 加密用户ID
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("collectionType", 1);
        hashMap.put("sourceId", sourceId);
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/resources/collection/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.get("resCode").equals("0")) {
                        collectId = (int) result.get("collectId");
                        showToastMsg("收藏成功");
                        classify_detail_video_collect_ib
                                .setImageResource(R.drawable.classify_detail_collect_press);
                        isSelect = true;
                    } else {
                        showToastMsg("收藏失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    /**
     * @Title: cancelCollectVideo @Description: TODO 取消收藏视频接口 @author hechuang @param
     * 设定文件 @return void 返回类型 @throws
     */
    private void cancelCollectVideo() {
        // 加密用户ID
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("collectIds", collectId);
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/resources/collection/cancel/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.get("resCode").equals("0")) {
                        showToastMsg("收藏已取消");
                        classify_detail_video_collect_ib
                                .setImageResource(R.drawable.classify_detail_collect_normal);
                        isSelect = false;
                    } else {
                        showToastMsg("取消失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    private void getCommentData(final int page) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourcesId", videoId);
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        /* hashMap.put("userId", Ras_uid); */
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/comment/list/service", jsonData, new GenericsCallback<CommentObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                progressDialog.dismiss();
                classify_more_comment_prlv.onRefreshComplete();
//                showToastMsg("网络连接失败， 请检查网络！");
            }

            @Override
            public void onResponse(CommentObj result, int id) {
                progressDialog.dismiss();
                classify_more_comment_prlv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (result.getResourcesCommentList() != null && result.getResourcesCommentList().size() > 0) {
                        if (page == 1) {
                            classifyVideoMoreCommentLists.clear();
                        }
                        thisPage++;
                        classifyVideoMoreCommentLists.addAll(result.getResourcesCommentList());
                        classifyVideoMoreCommentAdapter.setGroup(classifyVideoMoreCommentLists);
                    }
                } else {
//                    showToastMsg(result.getResDesc());
                }
            }
        });

    }

    /**
     * @return void 返回类型
     * @throws
     * @Title: gotoReport
     * @Description: TODO 分享数据上报
     * @author mark
     */
    private void gotoReport() {
        // 封装参数
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("shareTextId", String.valueOf(sourceId));
        hashMap.put("shareType", 1);
        hashMap.put("clientId", 3);
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/share/data/report/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    if (result.getString("resCode").equals("0")) {
//                        showToastMsg(result.getString("resDesc"));
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
//                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    private void shareShow() {
        mShareDialog.setWenxinClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(videoIntroduce)) {
                    videoIntroduce = "  ";
                }
                new ShareAction(ClassifyVideoDetialActivity.this)
                        .setPlatform(SHARE_MEDIA.WEIXIN).withMedia(image)
                        .setCallback(umShareListener).withText(videoIntroduce)
                        .withTitle(videoName)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html")
                        .share();
            }
        });
        mShareDialog.setPengyouquanClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(videoIntroduce)) {
                    videoIntroduce = "  ";
                }
                new ShareAction(ClassifyVideoDetialActivity.this)
                        .withMedia(image).withText(videoIntroduce)
                        .withTitle(videoName)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
            }
        });
        mShareDialog.setSinaClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
//                UMShareAPI.get(ClassifyVideoDetialActivity.this).doOauthVerify(ClassifyVideoDetialActivity.this, SHARE_MEDIA.SINA, new UMAuthListener() {
//                    @Override
//                    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
//                        MLog.d("onComplete");
//                        MLog.d("videoIntroduce:" + videoIntroduce);
//                        MLog.d("videoName:" + videoName);
//                        MLog.d("image:" + image.asUrlImage());
//                        if (StringUtil.isEmpty(videoIntroduce)) {
//                            videoIntroduce = "  ";
//                        }
                        new ShareAction(ClassifyVideoDetialActivity.this)
                                .setPlatform(SHARE_MEDIA.SINA).withText(videoIntroduce)
                                .withTitle(videoName).withMedia(image)
                                .setCallback(umShareListener)
                                .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
//                    }
//
//                    @Override
//                    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
//                        MLog.d("onError");
//                    }
//
//                    @Override
//                    public void onCancel(SHARE_MEDIA share_media, int i) {
//                        MLog.d("onCancel");
//                    }
//                });

            }
        });
        mShareDialog.setQqClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (StringUtil.isEmpty(videoIntroduce)) {
                    videoIntroduce = "  ";
                }
                new ShareAction(ClassifyVideoDetialActivity.this)
                        .setPlatform(SHARE_MEDIA.QQ).withText(videoIntroduce)
                        .withTitle(videoName).setCallback(umShareListener)
                        .withMedia(image)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
            }
        });
        mShareDialog.setOnCancelClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mShareDialog.dismiss();
            }
        });
        mShareDialog.show();
    }

    private UMShareListener umShareListener = new UMShareListener() {
        @Override
        public void onResult(SHARE_MEDIA platform) {
//            Toast.makeText(ClassifyVideoDetialActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享成功啦", Toast.LENGTH_SHORT).show();
            if (!AppContent.UID.equals("")) {
                gotoReport();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(ClassifyVideoDetialActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(ClassifyVideoDetialActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    private String getStringForXml(String key) {
        int stringID = getResources().getIdentifier(key,// string.xml内配置的名字
                "string",
                getPackageName());

        return getResources().getString(stringID);
    }

    /**
     * @author 段春林
     * @time 2016/8/30 18:23
     * 播放器添加横竖屏代码
     */
    @Bind(R.id.video_detail_player_fragment_rl)
    FrameLayout video_detail_player_fragment_rl;

    private void initPlayerFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        video_detail_player_fragment_rl.setVisibility(View.VISIBLE);
        if (mVRPlayerFragment != null) {
            transaction.remove(mVRPlayerFragment);
        }
        //创建一个fragment
        mVRPlayerFragment = VRPlayerFragment.build().setCanConfigurationChanged(true);
        //实例化fragment事务管理器
        //用新创建的fragment来代替fragment_container
        transaction.add(R.id.video_detail_player_fragment_rl, mVRPlayerFragment);
        //提交事务
        transaction.commit();
    }


    /**
     * @author 段春林
     * @time 2016/9/6 11:16
     * 横竖屏监听。
     * 如果遇到界面上有弹出框，禁止横竖屏切换
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
//        if(mShareDialog.isShowing()){return;}

        switch (newConfig.orientation) {
            case Configuration.ORIENTATION_PORTRAIT:
                classify_more_comment_push_ll.setVisibility(View.VISIBLE);
                classify_detail_video_more_rl.setVisibility(View.VISIBLE);
                break;
            case Configuration.ORIENTATION_LANDSCAPE:
                classify_detail_video_more_rl.setVisibility(View.GONE);
                classify_more_comment_push_ll.setVisibility(View.GONE);
                break;
        }
        int screenWith = ScreenUtils.getScreenWidth(ClassifyVideoDetialActivity.this);
        int screenHeight = ScreenUtils.getScreenHeight(this);
        LayoutParams lp;
        lp = classify_detail_title_rl.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * screenWith) / screenHeight;
        classify_detail_title_rl.setLayoutParams(lp);


        LayoutParams lp1;
        lp1 = video_detail_player_fragment_rl.getLayoutParams();
        lp1.width = screenWith;
        lp1.height = (int) (screenWith * screenWith) / screenHeight;
        video_detail_player_fragment_rl.setLayoutParams(lp1);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    public void onBackPressed() {
        if (mVRPlayerFragment != null) {
            mVRPlayerFragment.onBackPressed();
        } else {
            super.onBackPressed();
        }
    }

    /**
     * @return void 返回类型
     * @throws 1.普通视频，DOWN_LOAD_DEFAULT_VIDEO_TYPE_NAME 2.3D视频,DOWN_LOAD_3D_VIDEO_TYPE_NAME
     *                                                  3.全景视频,DOWN_LOAD_DEFAULT_VIDEO_TYPE_NAME
     * @Title: downLoadVideoOnClick
     * @Description: TODO(这里用一句话描述这个方法的作用)
     * @author duanchunlin
     */
    private void downLoadVideo(String downUrl, String resourcesID,
                               String downVideoType, String fildeeSize) {
        String url = StringUtil.subString(downUrl, "?", true); //带有下载密钥的地址，都要截取下载地址,这里截取前半段
        String url_key = StringUtil.subString(downUrl, "?", false);//带有下载密钥的地址，都要截取下载地址,这里截取后半段
        if (!StringUtils.isBlank(downUrl) && DownloaderManager.getInstance().getFileDownloaderModelByUrl(url) == null) {
            String coverImgUrl = videoIconUrl
                    + "?imageView2/2/w/"
                    + (100 + ScreenUtils
                    .getScreenWidth(ClassifyVideoDetialActivity.this) / 2);
            FileDownloaderModel model = new FileDownloaderModel();
            model.setUrl(url);   //带有下载密钥的地址，都要截取下载地址,这里截取前半段
            model.setUrlKey(url_key);  //带有下载密钥的地址，都要截取下载地址,这里截取后半段

            model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_TYPE, OkhttpConstant.DOWN_LOAD_NET_VIDEO_TYPE_NAME);
            model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_NAME, videoName);
            model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_ICON_URL, coverImgUrl);
            model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_MARK_ID, resourcesID);
            model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_FILE_SIZE, fileSize);
            model.putExtField(OkhttpConstant.SQL_KEY_FILE_DOWN_LOADER_VIDEO_TYPE, downVideoType);
            FileDownloaderModel fileDownLoadModel = DownloaderManager
                    .getInstance().addTask(model);
            if (fileDownLoadModel != null) {
                DownloaderManager.getInstance().startTask(
                        fileDownLoadModel.getId());
                Toast.makeText(ClassifyVideoDetialActivity.this, "已添加到下载列队",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (StringUtils.isBlank(downUrl)) {
                Toast.makeText(ClassifyVideoDetialActivity.this, "下载出错",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ClassifyVideoDetialActivity.this, "不能重复下载",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void startDownload() {
        // TODO Auto-generated method stub
        if (NetWorkDialog.getIsShowNetWorkDialog(ClassifyVideoDetialActivity.this)) {
            if (netWorkDialog == null) {
                netWorkDialog = new NetWorkDialog(ClassifyVideoDetialActivity.this);
            }
            netWorkDialog.showNetWorkChangeDownLoadWarning(new NetWorkDialogButtonListener() {
                @Override
                public void okClick() {
                    getCurrentNetSourceData();
                }
            });
        } else {
            getCurrentNetSourceData();
        }
    }


    private void getCurrentNetSourceData() {
        String uid = "-1";
        if (sourceId == -1) {
            return;
        }

        if (AppContent.UID.equals("")) {
            showToastMsg("请登录后再下载该资源");
            showActivity(ClassifyVideoDetialActivity.this, LoginActivity.class);
            return;
        } else if (AppContent.LIMIT_LOGIN == 2) {
            showToastMsg("当前登录状态未激活，请激活后重新登录。");
            showActivity(ClassifyVideoDetialActivity.this,
                    LoginActivity.class);
            return;
        } else {
            uid = AppContent.UID;
        }
        // 加密手机号
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(uid);
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("resourcesId", sourceId);
        hashMap.put("userId", Ras_uid);
        hashMap.put("urlType",2); //1是播放，2是下载
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/new/url/service", jsonData, new GenericsCallback<DownLoadAddressResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络");
            }

            @Override
            public void onResponse(DownLoadAddressResult result, int id) {
                if (result.getResCode().equals("0")) {
                    final String videoPath = result.getUrl();
                    if (videoPath.equals("") && videoPath.length() <= 0) {
                        showToastMsg("资源找不到");
                    } else if (result.getUrlType() != 1) {
                        showToastMsg("不是视频格式");
                    } else {
                        int resourcePlayType = result.getResourcePlayType();
                        downLoadVideo(videoPath,
                                String.valueOf(sourceId),
                                String.valueOf(resourcePlayType), fileSize);
                    }

                } else {
                    showToastMsg("权限不够,请联系客服");
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        /** attention to this below ,must add this **/
        UMShareAPI.get(ClassifyVideoDetialActivity.this).onActivityResult(
                requestCode, resultCode, data);
        if (resultCode == 200) {
            if (classify_more_comment_rl.getVisibility() == View.VISIBLE) {
                getCommentData(1);
            } else {
                getCommentData();
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        getCommentData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
//        mHandler.removeMessages(0);
    }
}
