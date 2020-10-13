package com.xunixianshi.vrshow.my;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResultDataList;
import com.xunixianshi.vrshow.obj.UserShareListObj;
import com.xunixianshi.vrshow.obj.UserShareObj;
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
 * 我的分享列表 分享过的内容
 *
 * @author hechuang
 * @className MyShareListActivity
 * @time 2016/9/21 11:41
 */
public class MyShareListActivity extends BaseAct {
    @Bind(R.id.title_left_back_iv)
    ImageButton title_left_back_iv;
    @Bind(R.id.titile_right_delete_tv)
    TextView titile_right_delete_tv;
    @Bind(R.id.my_share_list_ptrlv)
    PullToRefreshListView my_share_list_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private LoadingAnimationDialog progressDialog;
    private NetWorkDialog netWorkDialog;
    private MyShareListAdapter myShareListAdapter;
    private ArrayList<UserShareListObj> mDataLists;
    private int thisPage = 1;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_share_list);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        progressDialog = new LoadingAnimationDialog(MyShareListActivity.this);
        netWorkDialog = new NetWorkDialog(MyShareListActivity.this);
        myShareListAdapter = new MyShareListAdapter(MyShareListActivity.this);
        mDataLists = new ArrayList<UserShareListObj>();
        my_share_list_ptrlv.setAdapter(myShareListAdapter);
        initListener();
        getNetData(1);
    }
    /**
     * 初始化监听器
     *@author hechuang
     *@time 2016/9/21 12:01
     */
    private void initListener(){
        my_share_list_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        my_share_list_ptrlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
//                getPlayAddress(mDataLists.get(position - 1).getResourceId(), mDataLists.get(position - 1).getResourceTitle());
                Bundle bundle = new Bundle();
                bundle.putInt("videoTypeId",mDataLists.get(position - 1).getResourceId());
                showActivity(MyShareListActivity.this,ClassifyVideoDetialActivity.class,bundle);

            }
        });
        my_share_list_ptrlv
                .setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {

                    @Override
                    public void onPullDownToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 上拉刷新 重置所有数据
                        isPullToRefresh = true;
                        thisPage = 1;
                        getNetData(thisPage);
                    }

                    @Override
                    public void onPullUpToRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        // 下拉加载更多 数据获取成功后thispage自动加1 这里不做++操作
                        isPullToRefresh = true;
                        getNetData(thisPage);
                    }
                });
    }

    @OnClick({R.id.title_left_back_iv, R.id.titile_right_delete_tv,R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_back_iv:
                MyShareListActivity.this.finish();
                break;
            case R.id.titile_right_delete_tv:
                break;
            case R.id.page_no_have_reload_bt:
                getNetData(1);
                break;
        }
    }

    /**
     * 获取网络数据
     *
     * @author hechuang
     * @time 2016/9/21 10:40
     */
    private void getNetData(final int page) {
        if (!isPullToRefresh) {
            progressDialog.show();
        }
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("userId", Ras_uid);
        hashMap.put("clientId", -1);//1表示PC端分享历史，2表示IOS端分享历史,3表示android分享历史、4表示VRSHOW客户端分享历史，-1表示所有端
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/user/share/resource/list/service");
        OkHttpAPI.postHttpData("/user/share/resource/list/service", jsonData, new GenericsCallback<UserShareObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (mDataLists.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                my_share_list_ptrlv.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserShareObj result, int id) {
                progressDialog.dismiss();
                my_share_list_ptrlv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (result.getList() != null && result.getList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            mDataLists.clear();
                            myShareListAdapter.clearGroup(true);
                        }
                            mDataLists.addAll(result
                                    .getList());
                            myShareListAdapter.setGroup(mDataLists);
                            thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (mDataLists.size() <= 0) {
                        page_error_rl.setVisibility(View.VISIBLE);
                    }
//                    showToastMsg(result.getResDesc());
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
                    if (NetWorkDialog.getIsShowNetWorkDialog(MyShareListActivity.this)) {
                        if (netWorkDialog == null) {
                            netWorkDialog = new NetWorkDialog(MyShareListActivity.this);
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
     *@author hechuang
     *@time 2016/9/21 11:02
     */
    private void openVideoPlayer(String playerUrl, int playerLastProgress, int sourceId, String videoName, int videoType) {
        if (playerUrl.equals("") && playerUrl.length() <= 0) {
            showToastMsg("播放路径不存在");
        }
        int lastProgress = 0;
        if (AppContent.UID.equals("")) {
            lastProgress = (int) SimpleSharedPreferences.getInt(
                    videoName, MyShareListActivity.this);
        } else {
            lastProgress = playerLastProgress;
        }
//        VRPlayerActivity.intentTo(MyPlayHistoryActivity.this, videoName, playerUrl, videoType, sourceId, lastProgress, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}

