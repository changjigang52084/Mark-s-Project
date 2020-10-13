package com.xunixianshi.vrshow.find;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.widget.NoScrollGridView;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshScrollView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.obj.FindSpecialDetailListObj;
import com.xunixianshi.vrshow.obj.FindSpecialDetailObj;
import com.xunixianshi.vrshow.player.VRPlayerActivity;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.utils.PicassoUtil;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 专题详情页
 *
 * @author HeChuang
 * @ClassName SpecialDetailActivity
 * @time 2016/11/1 15:27
 */
public class SpecialDetailActivity extends BaseAct {
    @Bind(R.id.title_left_back_iv)
    ImageButton title_left_back_iv;
    @Bind(R.id.title_center_text_iv)
    TextView title_center_text_iv;
    @Bind(R.id.find_special_icon_iv)
    ImageView find_special_icon_iv;
    @Bind(R.id.find_special_info_tv)
    TextView find_special_info_tv;
    @Bind(R.id.find_special_ptrgv)
    NoScrollGridView find_special_ptrgv;
    @Bind(R.id.find_special_detail_ptrsv)
    PullToRefreshScrollView find_special_detail_ptrsv;

    private ArrayList<FindSpecialDetailListObj> mVideoListsLists;
    private SpecialDetailAdapter mVideoItemAdapter;
    private LoadingAnimationDialog progressDialog;
    private NetWorkDialog netWorkDialog;
    private int thisPage = 1;
    private String specialId;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_special_detail_xml);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.title_left_back_iv)
    public void onClick() {
        SpecialDetailActivity.this.finish();
    }

    @Override
    protected void initData() {
        super.initData();
        specialId = getIntent().getStringExtra("videoTypeId");
        progressDialog = new LoadingAnimationDialog(SpecialDetailActivity.this);
        netWorkDialog = new NetWorkDialog(SpecialDetailActivity.this);
        mVideoItemAdapter = new SpecialDetailAdapter(SpecialDetailActivity.this);
        mVideoListsLists = new ArrayList<FindSpecialDetailListObj>();
        find_special_ptrgv.setAdapter(mVideoItemAdapter);
        initListener();
        getNetData(1);
    }

    private void initListener() {

        int screenWith = ScreenUtils.getScreenWidth(SpecialDetailActivity.this);
        ViewGroup.LayoutParams lp = find_special_icon_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        find_special_icon_iv.setLayoutParams(lp);
        find_special_detail_ptrsv.setMode(PullToRefreshBase.Mode.BOTH);
        find_special_ptrgv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                VRPlayerActivity.intentTo(SpecialDetailActivity.this,mVideoListsLists.get(position).getResourceTitle(),mVideoListsLists.get(position).getResourceId());
            }
        });
        find_special_detail_ptrsv
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {

                    @Override
                    public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                        // 下拉刷新 重置所有数据
                        isPullToRefresh = true;
                        thisPage = 1;
                        getNetData(thisPage);
                    }

                    @Override
                    public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                        // 上拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                        isPullToRefresh = true;
                        getNetData(thisPage);
                    }
                });
    }

    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            progressDialog.show();
        }
        String action = "";
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("specialId", specialId);
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID); //16.12.28 接口改动
        hashMap.put("userId", Ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        action = "/special/resources/list/service";
        setCancelHttpTag(action);
        OkHttpAPI.postHttpData(action, jsonData, new GenericsCallback<FindSpecialDetailObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                find_special_detail_ptrsv.onRefreshComplete();
            }

            @Override
            public void onResponse(FindSpecialDetailObj response, int id) {
                MLog.d("response::::::" + response.getResCode());
                progressDialog.dismiss();
                find_special_detail_ptrsv.onRefreshComplete();
                title_center_text_iv.setText(response.getSpecialName());
                PicassoUtil.loadImage(SpecialDetailActivity.this, response.getCoverImgUrl() + "?imageView2/2/w/" + (ScreenUtils.getScreenWidth(SpecialDetailActivity.this) + 120), find_special_icon_iv);
                find_special_info_tv.setText(response.getSpecialIntro());
                if (response.getResCode().equals("0")) {
                    if (page == 1) {
                        mVideoListsLists.clear();
                        mVideoItemAdapter.clearGroup(true);
                    }
                    if (response.getList() != null
                            && response.getList().size() > 0) {
                        mVideoListsLists
                                .addAll(response.getList());
                        mVideoItemAdapter.setGroup(mVideoListsLists);
                        thisPage++;
                    } else {
                        if (page == 1) {
//                        page_empty_iv.setVisibility(View.VISIBLE);
                        }
                    }
                } else {

                }
            }
        });
    }

    /**
     * 获取播放地址
     *
     * @author hechuang
     * @time 2016/9/21 10:59
     */
    private int videoType;

    private void getPlayAddress(final int sourceId, final String videoName) {
        String uid;
        if (AppContent.UID.equals("")) {
            // uid = "-1";
            uid = "2147483647";
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
                progressDialog.dismiss();
//                showToastMsg("网络连接失败，请检查网络");
            }

            @Override
            public void onResponse(DownLoadAddressResult result, int id) {
                progressDialog.dismiss();
                if (result.getResCode().equals("0")) {
                    final String videoUrl = result.getUrl();
                    videoType = result.getResourcePlayType();
                    MLog.d("videoUrl:" + videoUrl);
                    final int lastProgress = result.getPlayTimes();
                    if (NetWorkDialog.getIsShowNetWorkDialog(SpecialDetailActivity.this)) {
                        if (netWorkDialog == null) {
                            netWorkDialog = new NetWorkDialog(SpecialDetailActivity.this);
                        }
                        netWorkDialog.showNetWorkChangePlayerVideoWarning(new NetWorkDialogButtonListener() {
                            @Override
                            public void okClick() {
                                openVideoPlayer(videoUrl, lastProgress, sourceId, videoName, videoType);
                            }
                        });
                    } else {
                        openVideoPlayer(videoUrl, lastProgress, sourceId, videoName, videoType);
                    }
                }
            }
        });
    }

    /**
     * 打开播放
     *
     * @author hechuang
     * @time 2016/9/21 11:02
     */
    private void openVideoPlayer(String playerUrl, int playerLastProgress, int sourceId, String videoName, int videoType) {
        if (playerUrl.equals("") && playerUrl.length() <= 0) {
            showToastMsg("播放路径不存在");
        }
        int lastProgress = 0;
        if (AppContent.UID.equals("")) {
            lastProgress = (int) SimpleSharedPreferences.getInt(
                    videoName, SpecialDetailActivity.this);
        } else {
            lastProgress = playerLastProgress;
        }
        VRPlayerActivity.intentTo(SpecialDetailActivity.this, videoName, playerUrl, videoType, sourceId, lastProgress, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
