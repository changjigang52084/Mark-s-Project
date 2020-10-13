package com.xunixianshi.vrshow.recyclerview;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.hch.filedownloader.download.DownloaderManager;
import com.hch.filedownloader.download.FileDownloaderModel;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.hch.viewlib.util.StringUtils;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.customview.ShareDialog;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;
import com.xunixianshi.vrshow.my.LoginActivity;
import com.xunixianshi.vrshow.obj.DownLoadAddressResult;
import com.xunixianshi.vrshow.obj.HttpZanObj;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.videodetail.ClassifyVideoDetialActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import okhttp3.Call;

/**
 * Created by duan on 2016/10/24.
 */

public abstract class VideoExpandableActivity extends BaseAct{

    private ShareDialog mShareDialog;
    private NetWorkDialog netWorkDialog;
    private int shareId;

    private String mVideoIconUrl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mShareDialog = new ShareDialog(this);
    }

    /**
     * @Title: collectVideo
     * @Description: TODO 点赞接口
     * @author changjigang @param
     * 设定文件 @return void 返回类型 @throws
     */
    public void zanVideo(final int sourceId,final RecyclerItemCallBack callBack) {
        // 加密用户ID
        String uid;
        if (AppContent.UID.equals("")) {
            showToastMsg("请登录后再点赞");
            showActivity(VideoExpandableActivity.this, LoginActivity.class);
            return;
        } else if (AppContent.LIMIT_LOGIN == 2) {
            showToastMsg("当前登录状态未激活，请激活后重新登录。");
            showActivity(VideoExpandableActivity.this,
                    LoginActivity.class);
            return;
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
                showToastMsg("网络连接失败!请检查网络");
            }
            @Override
            public void onResponse(HttpZanObj result, int id) {
                if (result.getResCode().equals("0")) {
                    if(callBack!=null){
                        callBack.onSuccess(result);
                    }
                    if (result.getLikesStatus() == 0) {
                        showToastMsg("取消成功");
                    } else if (result.getLikesStatus() == 1) {
                        showToastMsg("点赞成功");
                    }
                } else {
                    if(callBack!=null){
                        callBack.onFail(result);
                    }
                    showToastMsg(result.getResDesc());
                }
            }
        });
    }
    private String mVideoIntroduce = "";
    public void shareShow(String videoIntroduce,final String videoIconUrl,final String videoName,final int videoId) {
        shareId = videoId;
        mVideoIconUrl = videoIconUrl;
        MLog.d("mVideoIconUrl::"+mVideoIconUrl);
        if(!mVideoIconUrl.contains("?imageView2")){
            mVideoIconUrl = mVideoIconUrl + "?imageView2/2/w/200";
        }
        if (StringUtil.isEmpty(videoIntroduce)) {
            mVideoIntroduce = "  ";
        }else{
            mVideoIntroduce = videoIntroduce;
        }
        mShareDialog.setWenxinClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ShareAction(VideoExpandableActivity.this)
                        .setPlatform(SHARE_MEDIA.WEIXIN).withMedia(new
                        UMImage(VideoExpandableActivity.this, mVideoIconUrl))
                        .setCallback(umShareListener).withText(mVideoIntroduce)
                        .withTitle(videoName)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html")
                        .share();
            }
        });
        mShareDialog.setPengyouquanClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ShareAction(VideoExpandableActivity.this)
                        .withMedia(new UMImage(VideoExpandableActivity.this, mVideoIconUrl)).withText(mVideoIntroduce)
                        .withTitle(videoName)
                        .setPlatform(SHARE_MEDIA.WEIXIN_CIRCLE)
                        .setCallback(umShareListener)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
            }
        });
        mShareDialog.setSinaClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ShareAction(VideoExpandableActivity.this)
                        .setPlatform(SHARE_MEDIA.SINA).withText(mVideoIntroduce)
                        .withTitle(videoName).withMedia(new UMImage(VideoExpandableActivity.this, mVideoIconUrl))
                        .setCallback(umShareListener)
                        .withTargetUrl("http://www.vrshow.com/resource/detail/" + videoId + ".html").share();
            }
        });
        mShareDialog.setQqClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new ShareAction(VideoExpandableActivity.this)
                        .setPlatform(SHARE_MEDIA.QQ).withText(mVideoIntroduce)
                        .withTitle(videoName).setCallback(umShareListener)
                        .withMedia(new UMImage(VideoExpandableActivity.this, mVideoIconUrl))
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
//            Toast.makeText(VideoExpandableActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享成功啦", Toast.LENGTH_SHORT).show();
            if(!AppContent.UID.equals("")){
                gotoReport();
            }
        }

        @Override
        public void onError(SHARE_MEDIA platform, Throwable t) {
//            Toast.makeText(VideoExpandableActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享失败啦", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCancel(SHARE_MEDIA platform) {
//            Toast.makeText(VideoExpandableActivity.this,
//                    getStringForXml(platform.toSnsPlatform().mShowWord) + "分享取消了", Toast.LENGTH_SHORT).show();
        }
    };

    private String getStringForXml(String key) {
        int stringID = getResources().getIdentifier(key,// string.xml内配置的名字
                "string",
                getPackageName());

        return getResources().getString(stringID);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(VideoExpandableActivity.this).onActivityResult(
                requestCode, resultCode, data);
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
                    } else {
                        showToastMsg(result.getString("resDesc"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new HttpError() {
            @Override
            public void onError(Call call, Exception e, int id) {
                showToastMsg("网络连接失败!请检查网络");
            }
        });
    }

    public void startDownload(final int sourceId,final String videoIconUrl,final String videoName) {
        /*sourceId: 2308
        videoIconUrl: https://xnxs.img.vrshow.com/o_1b3blc0thcb61jtf1tqrp3jqeoa.png
        videoName: 《灵魂寄生》第十二集*/
        Log.d("cjg","startDownload \nsourceId: " + sourceId + "\nvideoIconUrl: " + videoIconUrl + "\nvideoName: " + videoName);
        if (NetWorkDialog.getIsShowNetWorkDialog(VideoExpandableActivity.this)) {
            if (netWorkDialog == null) {
                netWorkDialog = new NetWorkDialog(VideoExpandableActivity.this);
            }
            netWorkDialog.showNetWorkChangeDownLoadWarning(new NetWorkDialogButtonListener() {
                @Override
                public void okClick() {
                    getCurrentNetSourceData(sourceId,videoIconUrl,videoName);
                }
            });
        } else {
            getCurrentNetSourceData(sourceId,videoIconUrl,videoName);
        }
    }
    private void getCurrentNetSourceData(final int sourceId,final String videoIconUrl,final String videoName) {
        String uid = "-1";
        if (sourceId == -1) {
            return;
        }
        if (AppContent.UID.equals("")) {
            showToastMsg("请登录后再下载该资源");
            showActivity(VideoExpandableActivity.this, LoginActivity.class);
            return;
        } else if (AppContent.LIMIT_LOGIN == 2) {
            showToastMsg("当前登录状态未激活，请激活后重新登录。");
            showActivity(VideoExpandableActivity.this,
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
                showToastMsg("网络连接失败，请检查网络");
            }

            @Override
            public void onResponse(DownLoadAddressResult result, int id) {
                if (result.getResCode().equals("0")) {
                    final String videoPath = result.getUrl();
                    // http://xnxs.video.vrshow.com/o_1b39jujvhpn91pq32gs1ep9vokl.mp4?sign=4fd63bab1ae2b7a40c3b4381a7462417
                    Log.d("cjg","onResponse videoPath: " + videoPath);
                    if (videoPath.equals("") && videoPath.length() <= 0) {
                        showToastMsg("资源找不到");
                    } else if (result.getUrlType() != 1) {
                        showToastMsg("不是视频格式");
                    } else {
                        int resourcePlayType = result.getResourcePlayType();
                        downLoadVideo(videoPath,
                                String.valueOf(sourceId),
                                String.valueOf(resourcePlayType), "0",videoIconUrl,videoName);
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
                               String downVideoType, String fileSize,String videoIconUrl,String videoName) {
        /*downUrl: http://xnxs.video.vrshow.com/o_1b39jujvhpn91pq32gs1ep9vokl.mp4?sign=4fd63bab1ae2b7a40c3b4381a7462417
        resourcesId: 2308
        downVideoType: 3
        fileSize: 0
        videoIconUrl: https://xnxs.img.vrshow.com/o_1b3blc0thcb61jtf1tqrp3jqeoa.png
        videoName: 《灵魂寄生》第十二集
         */
        Log.d("cjg","downloadVideo \ndownUrl: " + downUrl+"\nresourcesId: " + resourcesID+"\ndownVideoType: " + downVideoType+"\nfileSize: " +
                fileSize+"\nvideoIconUrl: " + videoIconUrl+"\nvideoName: " + videoName);
        String url = StringUtil.subString(downUrl, "?", true); //带有下载密钥的地址，都要截取下载地址,这里截取前半段
        // http://xnxs.video.vrshow.com/o_1b39jujvhpn91pq32gs1ep9vokl.mp4
        Log.d("cjg","url: " + url);
        String url_key = StringUtil.subString(downUrl, "?", false);//带有下载密钥的地址，都要截取下载地址,这里截取后半段
        // ?sign=4fd63bab1ae2b7a40c3b4381a7462417
        Log.d("cjg","url_key: " + url_key);
        if (!StringUtils.isBlank(downUrl) && DownloaderManager.getInstance().getFileDownloaderModelByUrl(url) == null) {
            String coverImgUrl = videoIconUrl
                    + "?imageView2/2/w/"
                    + (100 + ScreenUtils
                    .getScreenWidth(VideoExpandableActivity.this) / 2);
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
                Toast.makeText(VideoExpandableActivity.this, "已添加到下载列队",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            if (StringUtils.isBlank(downUrl)) {
                Toast.makeText(VideoExpandableActivity.this, "下载出错",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(VideoExpandableActivity.this, "不能重复下载",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
