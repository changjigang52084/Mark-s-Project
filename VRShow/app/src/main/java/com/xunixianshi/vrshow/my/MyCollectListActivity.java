package com.xunixianshi.vrshow.my;

import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.DeleteItemInterface;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.obj.CollectResult;
import com.xunixianshi.vrshow.obj.CollectResultDataList;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.player.VRPlayerActivity;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 我的收藏列表
 *
 * @author hechuang
 * @className MyCollectListActivity
 * @time 2016/9/21 10:27
 */
public class MyCollectListActivity extends BaseAct {
    @Bind(R.id.title_left_back_iv)
    ImageButton title_left_back_iv;
    @Bind(R.id.titile_right_delete_tv)
    TextView titile_right_delete_tv;
    @Bind(R.id.my_collect_ptrlv)
    PullToRefreshListView my_collect_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private LoadingAnimationDialog progressDialog;
    private NetWorkDialog netWorkDialog;
    private MyCollectListAdapter myCollectListAdapter;
    private ArrayList<CollectResultDataList> myCollectResultDataLists;
    private HintDialog mHintDialog;
    private int thisPage = 1;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_collect);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        progressDialog = new LoadingAnimationDialog(MyCollectListActivity.this);
        netWorkDialog = new NetWorkDialog(MyCollectListActivity.this);
        mHintDialog = new HintDialog(MyCollectListActivity.this);
        myCollectListAdapter = new MyCollectListAdapter(MyCollectListActivity.this);
        myCollectListAdapter.setDeleteInterface(new DeleteItemInterface() {
            @Override
            public void showDeleteDialog(int resourcesId,int position) {
                deleteResources(resourcesId,position);
            }
        });
        myCollectResultDataLists = new ArrayList<CollectResultDataList>();
        my_collect_ptrlv.setAdapter(myCollectListAdapter);
        initListener();
        getNetData(1);
    }

    private void initListener() {
        my_collect_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        my_collect_ptrlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getPlayAddress(myCollectResultDataLists.get(position - 1).getResourcesId(), myCollectResultDataLists.get(position - 1).getResourcesName());

            }
        });
        my_collect_ptrlv
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
                MyCollectListActivity.this.finish();
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
        hashMap.put("collectionType", 1);
        hashMap.put("userId", Ras_uid);
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/user/resources/collection/list/service");
        OkHttpAPI.postHttpData("/user/resources/collection/list/service", jsonData, new GenericsCallback<CollectResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (myCollectResultDataLists.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                my_collect_ptrlv.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(CollectResult result, int id) {
                progressDialog.dismiss();
                my_collect_ptrlv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (result.getDataList() != null && result.getDataList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            myCollectResultDataLists.clear();
                            myCollectListAdapter.clearGroup(true);
                        }
                            myCollectResultDataLists.addAll(result
                                    .getDataList());
                            myCollectListAdapter.setGroup(myCollectResultDataLists);
                            thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (myCollectResultDataLists.size() <= 0) {
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
                    if (NetWorkDialog.getIsShowNetWorkDialog(MyCollectListActivity.this)) {
                        if (netWorkDialog == null) {
                            netWorkDialog = new NetWorkDialog(MyCollectListActivity.this);
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
                    videoName, MyCollectListActivity.this);
        } else {
            lastProgress = playerLastProgress;
        }
        VRPlayerActivity.intentTo(MyCollectListActivity.this, videoName, playerUrl, videoType, sourceId, lastProgress, true);
    }

    private void deleteResources(final int collectId,final int position){
        mHintDialog.setContextText("是否要删除这条收藏");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
                deleteCollect(collectId,position);
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

    private void deleteCollect(int collectId,final int position){
        progressDialog.show();
// 加密用户ID
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("collectIds", collectId);
        hashMap.put("userId", Ras_uid);
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/user/resources/collection/cancel/service", jsonData, new GenericsCallback<CollectResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败!请检查网络");
                progressDialog.dismiss();
            }

            @Override
            public void onResponse(CollectResult result, int id) {
                progressDialog.dismiss();
                if (result.getResCode().equals("0")) {
                    myCollectResultDataLists.remove(position);
                    myCollectListAdapter.removeItem(position);
                    showToastMsg("删除成功");
                    if(myCollectResultDataLists.size()<=0){
                        page_emity_rl.setVisibility(View.VISIBLE);
                    }
                }else{
                    showToastMsg("删除失败");
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
