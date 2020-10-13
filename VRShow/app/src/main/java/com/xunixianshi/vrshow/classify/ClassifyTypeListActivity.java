package com.xunixianshi.vrshow.classify;

import android.content.Intent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.FileDownloaderModel;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.StringUtils;
import com.hch.viewlib.widget.PullToRefreshBase;
import com.hch.viewlib.widget.PullToRefreshListView;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.customview.ShareDialog;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.interfaces.ShareSendInterface;
import com.xunixianshi.vrshow.my.LoginActivity;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.obj.MainClassifyTypeListObj;
import com.xunixianshi.vrshow.obj.MainClassifyTypeObj;
import com.xunixianshi.vrshow.obj.MainOtherTabListObj;
import com.xunixianshi.vrshow.obj.MainOtherTabObj;
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
 * 指定视频类型视频列表
 * @ClassName ClassifyTypeListActivity
 *@author HeChuang
 *@time 2016/11/1 15:23
 */
public class ClassifyTypeListActivity extends BaseAct {
    @Bind(R.id.title_left_back_iv)
    ImageButton title_left_back_iv;
    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;
    @Bind(R.id.titile_right_delete_tv)
    TextView titile_right_delete_tv;
    @Bind(R.id.type_list_ptrlv)
    PullToRefreshListView type_list_ptrlv;
    @Bind(R.id.page_error_rl)
    RelativeLayout page_error_rl;
    @Bind(R.id.page_empty_rl)
    RelativeLayout page_emity_rl;

    private LoadingAnimationDialog progressDialog;
    private ClassifyVideoListAdapter myVideoListAdapter;
    private ArrayList<MainClassifyTypeListObj> classifyVideoTypeResultResourcesLists;
    private int thisPage = 1;
    private int tagId;
    private String tagName;
    private int bySort = 0;
    private ShareDialog mShareDialog;
    private int shareId;
    private NetWorkDialog netWorkDialog;
    private boolean isPullToRefresh = false;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_type_list);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        progressDialog = new LoadingAnimationDialog(ClassifyTypeListActivity.this);
        netWorkDialog = new NetWorkDialog(ClassifyTypeListActivity.this);
        mShareDialog = new ShareDialog(ClassifyTypeListActivity.this);
        myVideoListAdapter = new ClassifyVideoListAdapter(ClassifyTypeListActivity.this);
        tagId = getIntent().getExtras().getInt("videoTypeId");
        tagName = getIntent().getExtras().getString("videoTypeName");
        title_center_name_tv.setText(tagName);
        type_list_ptrlv.setMode(PullToRefreshBase.Mode.BOTH);
        classifyVideoTypeResultResourcesLists = new ArrayList<MainClassifyTypeListObj>();
        myVideoListAdapter.setShareInterface(new ShareSendInterface() {
            @Override
            public void share(String videoIntroduce, String videoIconUrl, String videoName, int videoId) {
                if (StringUtil.isEmpty(videoIntroduce)) {
                    videoIntroduce = "  ";
                }
                shareShow(videoIntroduce, videoIconUrl, videoName, videoId);
            }

            @Override
            public void downLoad(int sourceId, String videoIconUrl, String videoName) {
                startDownload(sourceId, videoIconUrl, videoName);
            }


        });
        type_list_ptrlv.setAdapter(myVideoListAdapter);
        initListener();
//        // qq
//        PlatformConfig.setQQZone("1105439134", "uUaXoSZAE48yD0fY");
//        // 新浪微博
//        PlatformConfig.setSinaWeibo("2835953282",
//                "5c2053f74978afddea92560f05af2eb5");
//        // 微信
//        PlatformConfig.setWeixin("wx633b9cf100b10371",
//                "50bcd5d0b66fcc0b9b5064ea9aceb169");
        getNetData(1);
    }

    @OnClick({R.id.title_left_back_iv, R.id.titile_right_delete_tv, R.id.page_no_have_reload_bt})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_left_back_iv:
                ClassifyTypeListActivity.this.finish();
                break;
            case R.id.titile_right_delete_tv:
                break;
            case R.id.page_no_have_reload_bt:
                getNetData(1);
                break;
        }
    }

    private void initListener() {
        type_list_ptrlv
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
        String action = "";
//        action = "/column/more/resources/list/service";
//
//        hashMap.put("userId", Ras_uid);
//        hashMap.put("currPage", page);
//        hashMap.put("pageSize", 10);
//        hashMap.put("queryType", tagId);
        //暂时不换
        action = "/date/index/customized/more/service";
        hashMap.put("userId", Ras_uid);
        hashMap.put("currPage", page);
        hashMap.put("pageSize", 10);
        hashMap.put("dataClient", 8);
        hashMap.put("moduleId", tagId);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        setCancelHttpTag(action);
        OkHttpAPI.postHttpData(action, jsonData, new GenericsCallback<MainClassifyTypeObj>() {
            @Override
            public void onError(Call call, Exception e, int id) {
                if(isDestroy()){return;}
                if (classifyVideoTypeResultResourcesLists.size() <= 0) {
                    page_error_rl.setVisibility(View.VISIBLE);
                }
                progressDialog.dismiss();
                type_list_ptrlv.onRefreshComplete();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(MainClassifyTypeObj result, int id) {
                if(isDestroy()){return;}
                progressDialog.dismiss();
                type_list_ptrlv.onRefreshComplete();
                if (result.getResCode().equals("0")) {
//                    page_failed_page_rl.setVisibility(View.GONE);
                    if (result.getList() != null && result.getList().size() > 0) {
                        page_error_rl.setVisibility(View.GONE);
                        page_emity_rl.setVisibility(View.GONE);
                        if (page == 1) {
                            classifyVideoTypeResultResourcesLists.clear();
                            myVideoListAdapter.clearGroup(true);
                        }
                        classifyVideoTypeResultResourcesLists.addAll(result
                                .getList());
                        myVideoListAdapter.setGroup(classifyVideoTypeResultResourcesLists);
                        thisPage++;
                    } else {
                        if (page == 1) {
                            page_emity_rl.setVisibility(View.VISIBLE);
                        }
                    }
                } else {
                    if (classifyVideoTypeResultResourcesLists.size() <= 0) {
                        page_error_rl.setVisibility(View.VISIBLE);
                    }
//                    showToastMsg(result.getResDesc());
                }
            }
        });
    }

    private void shareShow(final String videoIntroduce, final String videoIconUrl, final String videoName, final int videoId) {
        shareId = videoId;
        mShareDialog.setWenxinClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                new ShareAction(ClassifyTypeListActivity.this)
                        .setPlatform(SHARE_MEDIA.WEIXIN).withMedia(new UMImage(ClassifyTypeListActivity.this, videoIconUrl))
                        .setCallback(umShareListener).withText(videoIntroduce)
                        .withTitle(videoName)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html")
                        .share();
            }
        });
        mShareDialog.setPengyouquanClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ShareAction(ClassifyTypeListActivity.this)
                        .withMedia(new UMImage(ClassifyTypeListActivity.this, videoIconUrl)).withText(videoIntroduce)
                        .withTitle(videoName)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
            }
        });
        mShareDialog.setSinaClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ShareAction(ClassifyTypeListActivity.this)
                        .setPlatform(SHARE_MEDIA.SINA).withText(videoIntroduce)
                        .withTitle(videoName).withMedia(new UMImage(ClassifyTypeListActivity.this, videoIconUrl))
                        .setCallback(umShareListener)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
            }
        });
        mShareDialog.setQqClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ShareAction(ClassifyTypeListActivity.this)
                        .setPlatform(SHARE_MEDIA.QQ).withText(videoIntroduce)
                        .withTitle(videoName).setCallback(umShareListener)
                        .withMedia(new UMImage(ClassifyTypeListActivity.this, videoIconUrl))
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
            }
        });
        mShareDialog.setOnCancelClickListener(new View.OnClickListener() {

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
//            Toast.makeText(ClassifyTypeListActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享成功啦", Toast.LENGTH_SHORT).show();
            if(!AppContent.UID.equals("")){
                gotoReport();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(ClassifyTypeListActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(ClassifyTypeListActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(ClassifyTypeListActivity.this).onActivityResult(
                requestCode, resultCode, data);
    }

    private String getStringForXml(String key) {
        int stringID = ClassifyTypeListActivity.this.getResources().getIdentifier(key,// string.xml内配置的名字
                "string",
                ClassifyTypeListActivity.this.getPackageName());

        return ClassifyTypeListActivity.this.getResources().getString(stringID);
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
        hashMap.put("shareTextId", String.valueOf(shareId));
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

    public void startDownload(final int sourceId, final String videoIconUrl, final String videoName) {
        // TODO Auto-generated method stub
        if (NetWorkDialog.getIsShowNetWorkDialog(ClassifyTypeListActivity.this)) {
            if (netWorkDialog == null) {
                netWorkDialog = new NetWorkDialog(ClassifyTypeListActivity.this);
            }
            netWorkDialog.showNetWorkChangeDownLoadWarning(new NetWorkDialogButtonListener() {
                @Override
                public void okClick() {
                    getCurrentNetSourceData(sourceId, videoIconUrl, videoName);
                }
            });
        } else {
            getCurrentNetSourceData(sourceId, videoIconUrl, videoName);
        }
    }

    private void getCurrentNetSourceData(final int sourceId, final String videoIconUrl, final String videoName) {
        String uid = "-1";
        if (sourceId == -1) {
            return;
        }

        if (AppContent.UID.equals("")) {
            showToastMsg("请登录后再下载该资源");
            showActivity(ClassifyTypeListActivity.this, LoginActivity.class);
            return;
        } else if (AppContent.LIMIT_LOGIN == 2) {
            showToastMsg("当前登录状态未激活，请激活后重新登录。");
            showActivity(ClassifyTypeListActivity.this,
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
                                String.valueOf(resourcePlayType), "0", videoIconUrl, videoName);
                    }

                } else {
                    showToastMsg("权限不够,请联系客服");
                }
            }
        });
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
                               String downVideoType, String fileSize, String videoIconUrl, String videoName) {
        String url = StringUtil.subString(downUrl, "?", true); //带有下载密钥的地址，都要截取下载地址,这里截取前半段
        String url_key = StringUtil.subString(downUrl, "?", false);//带有下载密钥的地址，都要截取下载地址,这里截取后半段
        if (!StringUtils.isBlank(downUrl) && DownloaderManager.getInstance().getFileDownloaderModelByUrl(url) == null) {
            String coverImgUrl = videoIconUrl
                    + "?imageView2/2/w/"
                    + (100 + ScreenUtils
                    .getScreenWidth(ClassifyTypeListActivity.this) / 2);
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
                Toast.makeText(ClassifyTypeListActivity.this, "已添加到下载列队",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (StringUtils.isBlank(downUrl)) {
                Toast.makeText(ClassifyTypeListActivity.this, "下载出错",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ClassifyTypeListActivity.this, "不能重复下载",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
