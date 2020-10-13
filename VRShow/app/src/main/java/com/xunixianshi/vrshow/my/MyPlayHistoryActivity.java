package com.xunixianshi.vrshow.my;

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
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.DeleteItemInterface;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.obj.CollectResult;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResult;
import com.xunixianshi.vrshow.obj.PlayHistoryResultDataList;
import com.xunixianshi.vrshow.player.VRPlayerActivity;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * 我的播放历史
 *
 * @author hechuang
 * @className MyPlayHistoryActivity
 * @time 2016/9/21 10:27
 */
public class MyPlayHistoryActivity extends BaseAct {
    @Bind(R.id.title_left_back_iv)
    ImageButton title_left_back_iv;
    @Bind(R.id.titile_right_delete_tv)
    TextView titile_right_delete_tv;
    @Bind(R.id.my_play_history_ptrlv)
    PullToRefreshListView my_play_history_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private LoadingAnimationDialog progressDialog;
    private NetWorkDialog netWorkDialog;
    private MyPlayHistoryAdapter myListPublicAdapter;
    private ArrayList<PlayHistoryResultDataList> playHistoryResultDataLists;
    private int thisPage = 1;
    private HintDialog mHintDialog;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_play_history);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        progressDialog = new LoadingAnimationDialog(MyPlayHistoryActivity.this);
        netWorkDialog = new NetWorkDialog(MyPlayHistoryActivity.this);
        mHintDialog = new HintDialog(MyPlayHistoryActivity.this);
        myListPublicAdapter = new MyPlayHistoryAdapter(MyPlayHistoryActivity.this);
        myListPublicAdapter.setDeleteInterface(new DeleteItemInterface() {
            @Override
            public void showDeleteDialog(int resourcesId,int position) {
                deleteResources(resourcesId,position);
            }
        });
        playHistoryResultDataLists = new ArrayList<PlayHistoryResultDataList>();
        my_play_history_ptrlv.setAdapter(myListPublicAdapter);
        initListener();
        getNetData(1);
    }

    private void initListener() {
        my_play_history_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        my_play_history_ptrlv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                getPlayAddress(playHistoryResultDataLists.get(position - 1).getResourcesId(), playHistoryResultDataLists.get(position - 1).getResourcesTitle());

            }
        });
        my_play_history_ptrlv
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
                MyPlayHistoryActivity.this.finish();
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
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag("/user/play/history/list/service");
        OkHttpAPI.postHttpData("/user/play/history/list/service", jsonData, new GenericsCallback<PlayHistoryResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if (playHistoryResultDataLists.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                my_play_history_ptrlv.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(PlayHistoryResult result, int id) {
                progressDialog.dismiss();
                my_play_history_ptrlv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
                    if (result.getDataList() != null && result.getDataList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            playHistoryResultDataLists.clear();
                            myListPublicAdapter.clearGroup(true);
                        }
                            playHistoryResultDataLists.addAll(result
                                    .getDataList());
                            myListPublicAdapter.setGroup(playHistoryResultDataLists);
                            thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (playHistoryResultDataLists.size() <= 0) {
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
                    if (NetWorkDialog.getIsShowNetWorkDialog(MyPlayHistoryActivity.this)) {
                        if (netWorkDialog == null) {
                            netWorkDialog = new NetWorkDialog(MyPlayHistoryActivity.this);
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
                    videoName, MyPlayHistoryActivity.this);
        } else {
            lastProgress = playerLastProgress;
        }
        VRPlayerActivity.intentTo(MyPlayHistoryActivity.this, videoName, playerUrl, videoType, sourceId, lastProgress, true);
    }

    private void deleteResources(final int pId,final int position){
        mHintDialog.setContextText("是否要删除这条历史");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
                deleteHistory(pId,position);
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

    private void deleteHistory(int pId,final int position){
        progressDialog.show();
// 加密用户ID
        String Ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("playHistoryIds", pId);
        hashMap.put("userId", Ras_uid);
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postObjectData("/user/play/history/del/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                progressDialog.dismiss();
                try {
                    if (result.get("resCode").equals("0")) {
                        playHistoryResultDataLists.remove(position);
                        myListPublicAdapter.removeItem(position);
                        showToastMsg("删除成功");
                        if(playHistoryResultDataLists.size()<=0){
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }else{
                        showToastMsg("删除失败");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败!请检查网络");
                progressDialog.dismiss();
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
