package com.xunixianshi.vrshow.my.fragment;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.MD5Encryption;
import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.qiniu.android.http.ResponseInfo;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.interfaces.EditDialogTextInterface;
import com.xunixianshi.vrshow.interfaces.UploadActivityToItemInterface;
import com.xunixianshi.vrshow.interfaces.UploadInterface;
import com.xunixianshi.vrshow.interfaces.UploadItemToActivityInterface;
import com.xunixianshi.vrshow.my.fragment.database.UploadBean;
import com.xunixianshi.vrshow.my.fragment.database.UploadDatabaseTools;
import com.xunixianshi.vrshow.my.fragment.database.UploadManageUtil;
import com.xunixianshi.vrshow.obj.UserIconTokenObj;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * User: Mark.Chang(Mark.Chang@3glasses.com)
 * Date: 2016-10-09
 * Time: 17:10
 * FIXME
 */
public class CommittedAdapter extends BaseAda<UploadBean> {

    private UploadManageUtil mUploadManageUtil;
    private UploadDatabaseTools uploadDatabaseTools;
    private boolean isUpload = false;
    private int tempPosition;
    private String qiniuPath;
    private boolean isWiFi = true;
    private UploadItemToActivityInterface uploadItemToActivityInterface;

    public CommittedAdapter(Context context) {
        super(context);
        mUploadManageUtil = UploadManageUtil.getInstance();
        uploadDatabaseTools = new UploadDatabaseTools(context);
    }

    public void setInterface(UploadItemToActivityInterface uploadItemToActivityInterface) {
        this.uploadItemToActivityInterface = uploadItemToActivityInterface;
    }

    public void cancelUpload() {
        mUploadManageUtil.cancelUpload();
    }

    public void isWiFi(boolean isWifi) {
        this.isWiFi = isWifi;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_user_video_download, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final UploadBean item = getItem(position);
        int screenWith = ScreenUtils.getScreenWidth(mContext);
        ViewGroup.LayoutParams lp;
        ViewGroup.LayoutParams btn_progress_lp;
        lp = viewHolder.down_load_image_window_rl.getLayoutParams();
        btn_progress_lp = viewHolder.down_load_btn_progress_rl.getLayoutParams();
        btn_progress_lp.width = lp.width = screenWith;
        btn_progress_lp.height = lp.height = (int) (screenWith * 0.56);
        viewHolder.down_load_image_window_rl.setLayoutParams(lp);
        viewHolder.down_load_btn_progress_rl.setLayoutParams(btn_progress_lp);

        ViewGroup.LayoutParams alpha_lp;
        alpha_lp = viewHolder.down_load_bg_alpha_iv.getLayoutParams();
        alpha_lp.width = screenWith;
        alpha_lp.height = (int) (screenWith * 0.5) + (int) (61 * ScreenUtils.getDensity(mContext));
        viewHolder.down_load_bg_alpha_iv.setLayoutParams(alpha_lp);
        PicassoUtil.loadImage(mContext, item.getImagePath(), viewHolder.down_load_image_iv);
        MLog.d("item.getResourceName():" + item.getResourceName());
        MLog.d("item.getImagePath():" + item.getImagePath());
        viewHolder.down_load_name_tv.setText(item.getResourceName());
        viewHolder.down_load_play_num_tv.setText(item.getResourceIntroduce());
        viewHolder.down_load_progress_tv.setText(item.getUploadProgress() + "%");
        viewHolder.down_load_progressBar.setProgress(Integer.parseInt(item.getUploadProgress()));
        MLog.d("item.getUploadState()::" + item.getUploadState());
        if (item.getUploadState().equals("1")) {
            tempPosition = position;
            isUpload = true;
            viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_download_pause);
        } else {
            isUpload = false;
            viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_icon);
        }
        viewHolder.down_load_btn_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果是开始状态则调用暂停方法  如果是暂停状态 则重新获取token 断点续传
                if (item.getUploadProgress().equals("100")) {
                    CommittedAllDataToLocalServer(item, item.getQiniuPath());
                    viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_download_pause);
                    return;
                }
                MLog.d("isUpload::" + isUpload);
                if (isUpload) {
                    MLog.d("position::" + position);
                    MLog.d("tempPosition::" + tempPosition);
                    if (position == tempPosition) {
                        mUploadManageUtil.cancelUpload();
                        viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_icon);
                        isUpload = false;
                        uploadItemToActivityInterface.dissmissLoading();
                    } else {
                        uploadItemToActivityInterface.showDialog(new UploadActivityToItemInterface() {
                            @Override
                            public void okButtonClick() {
                                mUploadManageUtil.cancelUpload();
                                viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_icon);
                                isUpload = false;
                                uploadItemToActivityInterface.dissmissLoading();
                            }

                            @Override
                            public void okCancelClick() {

                            }
                        });
                    }
                } else {
                    //判断是否是wifi  如果是直接上传  如果不是 检测弹框提示
                    if(isWiFi){
                        getResourcesToken(item, viewHolder);
                        tempPosition = position;
                    }else{
                        uploadItemToActivityInterface.checkNetWork(new UploadActivityToItemInterface() {
                            @Override
                            public void okButtonClick() {
                                getResourcesToken(item, viewHolder);
                                tempPosition = position;
                            }

                            @Override
                            public void okCancelClick() {

                            }
                        });
                    }
                }

            }
        });
        viewHolder.public_video_item_delete_iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadItemToActivityInterface.showHint(new UploadActivityToItemInterface() {
                    @Override
                    public void okButtonClick() {
                        item.setDelete(true);
                        uploadDatabaseTools.delete(item.getMd5ResourceName());
                        uploadItemToActivityInterface.upData();
                        mUploadManageUtil.cancelUpload();
                    }

                    @Override
                    public void okCancelClick() {

                    }
                });
            }
        });
        mUploadManageUtil.registUploadListener(item.getMd5ResourceName(), addInterface(item, viewHolder));
        return convertView;
    }

    /**
     * 获取视频资源上传的token
     */
    private void getResourcesToken(final UploadBean item, final ViewHolder viewHolder) {
        //获取token成功后开始上传文件
        String ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("tokenType", 2);
        hashMap.put("userId", ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/qiniu/query/token/service", jsonData, new GenericsCallback<UserIconTokenObj>() {

            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserIconTokenObj response, int id) {
                if (response.getResCode().equals("0")) {
                    String token = response.getToken();
                    uploadVideoToQiniuServer(token, item, viewHolder);
                }
            }
        });
    }

    /**
     * 上传视频资源到七牛服务器
     *
     * @param token
     */
    private void uploadVideoToQiniuServer(String token, final UploadBean item, final ViewHolder viewHolder) {
        MLog.d("上传视频的token:" + token);
        mUploadManageUtil.uploadVideoToQiniuServer(token, item.getMd5ResourceName(), item.getVideoPath(), addInterface(item, viewHolder), item.getVideoFormat(), false);

    }

    public UploadInterface addInterface(final UploadBean item, final ViewHolder viewHolder) {
        UploadInterface uploadInterface = new UploadInterface() {
            @Override
            public void uploadComplete(String key, ResponseInfo info, JSONObject res) {
                if (res != null) {
                    if (info.isOK() == true) {
                        MLog.d("11111111111111111111111~~~list~~~ 上传成功" + (OkhttpConstant.video_url + key));
                        isUpload = false;
                        tempPosition = -1;
                        viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_icon);
                        //上传成功后更改上传是暂停状态
                        mUploadManageUtil.cancelUpload();
                        // (4)提交所有的数据到本地服务器
                        CommittedAllDataToLocalServer(item, OkhttpConstant.video_url + key);
                    }
                }
            }

            @Override
            public void uploadProgress(String key, double percent) {

                viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_download_pause);
                int progress = (int) (percent * 100);
                item.setUploadProgress(String.valueOf(progress));
                item.setUploadState("1");
                //保存数据库
                if (!isUpload) {
                    uploadDatabaseTools.saveOrUpdata(item);
                    isUpload = true;
                }
                viewHolder.down_load_progress_tv.setText(progress + "%");
                viewHolder.down_load_progressBar.setProgress(progress);
                if (progress == 100) {
                    viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_icon);
                }
            }

            @Override
            public void uploadPause() {
                isUpload = false;
                MLog.d("2222222222222222222~~~~uploadPause" + item.getResourceName());
                if (!item.isDelete()) {
                    viewHolder.down_load_btn_iv.setImageResource(R.drawable.video_upload_icon);
                    item.setUploadState("2");
                    uploadDatabaseTools.saveOrUpdata(item);
                }
            }

            @Override
            public void uploadFail() {

            }
        };
        return uploadInterface;
    }

    /**
     * 提交参数到服务器
     *
     * @ClassName CommittedAdapter
     * @author HeChuang
     * @time 2016/12/9 13:34
     */
    private void CommittedAllDataToLocalServer(final UploadBean item, String resourcesPath) {
        if (resourcesPath == null) {
            if (item.getQiniuPath() == null) {
                resourcesPath = "";
            } else {
                resourcesPath = item.getQiniuPath();
            }
        }
        qiniuPath = resourcesPath;
        try {
            // 封面图片地址 参数进行URLEncoder.encode编码(UTF-8)
            String encode_coverImage = URLEncoder.encode(item.getImagePath(), "UTF-8");
            // 开发商名称 非空 字符串，参数进行URLEncoder.encode编码(UTF-8)
            String encode_developers = URLEncoder.encode("1", "UTF-8");
            // 焦点图URL地址集合 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            ArrayList<String> imgList = new ArrayList<String>();
            String encode_videoImage = URLEncoder.encode(item.getImagePath(), "UTF-8");
            imgList.add(encode_videoImage);
            // 资源介绍 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesContent = URLEncoder.encode(item.getResourceIntroduce(), "UTF-8");
            // 是否原创 -1表示原创资源，参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesOriginal = URLEncoder.encode("-1", "UTF-8");
            // 资源文件地址 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesPath = URLEncoder.encode(resourcesPath, "UTF-8");
            // 资源文件大小 非空 字符串 单位MB 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesSize = URLEncoder.encode(item.getVideoSize(), "UTF-8");
            // 资源副标题 可为空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesSubtitle = URLEncoder.encode("", "UTF-8");
            // 资源名称 非空 字符串 唯一性验证 参数进行URLEncoder.encode编码(URF-8)
            String encode_resourcesTitle = URLEncoder.encode(item.getResourceName(), "UTF-8");
            // 资源版本号 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesVersion = URLEncoder.encode("", "UTF-8");
            // 用户ID 非空 字符串 RSA加密
            String ras_userId = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
            // 封装参数
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("userId", ras_userId);
            hashMap.put("resourcesTitle", encode_resourcesTitle);
            hashMap.put("resourcesColumn", 1);
            MLog.d("item.getVideoTypeId():" + item.getVideoTypeId());
            hashMap.put("resourcesType", item.getVideoTypeId());
            hashMap.put("resourcesSubtitle", encode_resourcesSubtitle);
            hashMap.put("resourcesOriginal", encode_resourcesOriginal);
            hashMap.put("resourcesPath", encode_resourcesPath);
            hashMap.put("resourcesContent", encode_resourcesContent);
            hashMap.put("resourcesSize", encode_resourcesSize);
            hashMap.put("resourcesDefinition", "");
            hashMap.put("resourcesVersion", encode_resourcesVersion);
            hashMap.put("developers", encode_developers);
            hashMap.put("resourcesDriveVersion", "");
            hashMap.put("resourcesTags", "-1"); // 资源标签，非空，字符串
            hashMap.put("resourcesLanguage", "-1");
            hashMap.put("imgList", imgList);
            hashMap.put("coverImg", encode_coverImage);
            hashMap.put("currencyClient", -1);
            hashMap.put("resourcesLinkType", 1);
            // 转参数
            String jsonData = StringUtil.MapToJson(hashMap);
            // 提交创建内容后的所有数据到本地服务器
            OkHttpAPI.postObjectData("/user/add/resources/service", jsonData, new HttpSuccess<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result, int id) {
                    try {
                        if (result.getString("resCode").equals("0")) {
                            showToastMsg(result.getString("resDesc"));
                            uploadDatabaseTools.delete(item.getMd5ResourceName());
                            uploadItemToActivityInterface.upData();
                        } else if (result.getString("resCode").equals("-91")) {
//                            uploadDatabaseTools.delete(item.getMd5ResourceName());
                            item.setQiniuPath(qiniuPath);
                            showToastMsg("资源名称被占用，请修改视频名称!");
                            uploadDatabaseTools.saveOrUpdata(item);
                            uploadItemToActivityInterface.editName(new EditDialogTextInterface() {
                                @Override
                                public void okButtonCarryText(String editText) {
                                    //重命名资源名称
                                    item.setResourceName(editText);
                                    uploadItemToActivityInterface.upData();
                                    CommittedAllDataToLocalServer(item,qiniuPath);
                                }
                            },item.getResourceName(),item.getImagePath());
                        } else {
                            item.setQiniuPath(qiniuPath);
                            showToastMsg("上传视频服务端异常，请稍后再试！");
                            uploadDatabaseTools.saveOrUpdata(item);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new HttpError() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    item.setQiniuPath(qiniuPath);
                    showToastMsg("上传视频失败，请检查网络");
                    uploadDatabaseTools.saveOrUpdata(item);
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    static class ViewHolder {
        @Bind(R.id.down_load_image_iv)
        ImageView down_load_image_iv;
        @Bind(R.id.down_load_image_window_rl)
        RelativeLayout down_load_image_window_rl;
        @Bind(R.id.down_load_name_tv)
        TextView down_load_name_tv;
        @Bind(R.id.down_load_play_num_tv)
        TextView down_load_play_num_tv;
        @Bind(R.id.down_load_more_operation_rl)
        RelativeLayout down_load_more_operation_rl;
        @Bind(R.id.down_load_bg_alpha_iv)
        ImageView down_load_bg_alpha_iv;
        @Bind(R.id.down_load_btn_iv)
        ImageView down_load_btn_iv;
        @Bind(R.id.down_load_progress_tv)
        TextView down_load_progress_tv;
        @Bind(R.id.down_load_progressBar)
        ProgressBar down_load_progressBar;
        @Bind(R.id.down_load_progressBar_rl)
        RelativeLayout down_load_progressBar_rl;
        @Bind(R.id.down_load_btn_progress_rl)
        RelativeLayout down_load_btn_progress_rl;
        @Bind(R.id.public_video_item_delete_iv)
        ImageView public_video_item_delete_iv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}