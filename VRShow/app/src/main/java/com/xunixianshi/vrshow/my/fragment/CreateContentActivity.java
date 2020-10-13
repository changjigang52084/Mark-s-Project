package com.xunixianshi.vrshow.my.fragment;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hch.utils.OkhttpConstant;
import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.FileUtil;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SDCardUtil;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.customview.NetWorkDialog;
import com.xunixianshi.vrshow.interfaces.UploadInterface;
import com.xunixianshi.vrshow.my.fragment.actionsheet.ActionSheet;
import com.xunixianshi.vrshow.my.fragment.database.UploadBean;
import com.xunixianshi.vrshow.my.fragment.database.UploadDatabaseTools;
import com.xunixianshi.vrshow.my.fragment.database.UploadManageUtil;
import com.xunixianshi.vrshow.obj.QuerySubClassificationListObj;
import com.xunixianshi.vrshow.obj.QuerySubClassificationObj;
import com.xunixianshi.vrshow.obj.ResourcesNameIsRepeatObj;
import com.xunixianshi.vrshow.obj.UserIconTokenObj;
import com.xunixianshi.vrshow.obj.eventBus.uploadNoticeEvent;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.ImageTools;
import com.xunixianshi.vrshow.util.StringUtil;
import com.xunixianshi.vrshow.webView.ShowWebViewActivity;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;

import org.greenrobot.eventbus.EventBus;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;

/**
 * TODO 创建内容页面
 *
 * @author MarkChang
 * @ClassName CreateContentActivity
 * @time 2016/11/1 15:53
 */
public class CreateContentActivity extends BaseAct implements ActionSheet.MenuItemClickListener {

    @Bind(R.id.my_information_create_content_tv)
    TextView my_information_create_content_tv;
    @Bind(R.id.my_information_create_content_cancel_rl)
    RelativeLayout my_information_create_content_cancel_rl;
    @Bind(R.id.my_information_create_content_cover_photo_iv)
    ImageView my_information_create_content_cover_photo_iv;
    @Bind(R.id.my_information_create_content_title_et)
    EditText my_information_create_content_title_et;
    @Bind(R.id.my_information_create_content_brief_introduction_et)
    EditText my_information_create_content_brief_introduction_et;
    @Bind(R.id.my_information_create_content_panorama_rl)
    RelativeLayout my_information_create_content_panorama_rl;
    @Bind(R.id.my_information_create_content_submitted_btn)
    Button my_information_create_content_submitted_btn;
    @Bind(R.id.protocol_frame_selection_iv)
    ImageView protocol_frame_selection_iv;
    @Bind(R.id.protocol_frame_selection_rl)
    RelativeLayout protocol_frame_selection_rl;
    @Bind(R.id.protocol_frame_selection_submitted_rl)
    RelativeLayout protocol_frame_selection_submitted_rl;
    @Bind(R.id.my_information_create_content_panorama_tv)
    TextView my_information_create_content_panorama_tv;
    @Bind(R.id.create_content_action_fail_rl)
    RelativeLayout create_content_action_fail_rl;
    @Bind(R.id.create_content_action_fail_desc_tv)
    TextView create_content_action_fail_desc_tv;
    private static final int EDIT_VIDEO_COVER_REQUEST = 1;
    private UploadManager uploadManager;
    private UploadManageUtil uploadManageUtil;
    private ArrayList<UploadBean> videoBeanArrayList = new ArrayList<UploadBean>();
    private String videoTitle;
    private String videoBriefIntroduction;
    private String videoPath;
    private String videoName;
    private String videoIntroduce;
    private String qiNiuImageUrl;
    private boolean videoIsNative = true;

    private String videoSize;
    private static Bitmap videoImageBitmap;
    private volatile boolean isCancelled = false;
    private LoadingAnimationDialog myProgressDialog;
    private boolean isChoiceProtocol = true;
    private String coverImage; // 编辑图片封面后获取的图片
    private CommonProgressDialog mDialog;
    private int videoTypeId = -1; // 视频类型默认等于3
    private Uri mCoverUri; // 获取到的图片Uri
    private ContentValues values;
    private UploadDatabaseTools uploadDatabaseTools;
    private UploadBean uploadBean;
    private HintDialog mHintDialog;

    private ArrayList<QuerySubClassificationListObj> typeList;
    private ArrayList<String> itemsType;
    private ArrayList<String> typesId;
    private String[] typeId;
    private String videoFormat;
    private float mErrorDescAlpha = 1.0f;
    int thisProgress;
    String mD5videoTitle;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_create_content);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        setTheme(R.style.ActionSheetStyleIOS7);
    }

    Thread mThread = new Thread(new Runnable() {
        @Override
        public void run() {
            File f = new File(videoPath);
            //文件大小格式化
            videoSize= SDCardUtil.formatFileSize(f.length(),false);
            videoImageBitmap = getVideoThumbnail(videoPath);
            saveBitmap(videoImageBitmap,videoName);
        }
    });

    @Override
    protected void initData() {
        super.initData();
        fixMediaDir();
        // 获得写模式的数据仓库
        // 创建一个带值的表，它们的列名是keys
        values = new ContentValues();
        typeList = new ArrayList<QuerySubClassificationListObj>();
        itemsType = new ArrayList<String>();
        typesId = new ArrayList<String>();
        uploadDatabaseTools = new UploadDatabaseTools(CreateContentActivity.this);
        myProgressDialog = new LoadingAnimationDialog(CreateContentActivity.this);
//        videoBeanArrayList = uploadDatabaseTools.selectAllUploadData();
        videoBeanArrayList = uploadDatabaseTools.selectAllByUid(AppContent.UID);
        mHintDialog = new HintDialog(CreateContentActivity.this);
        uploadManageUtil = UploadManageUtil.getInstance();
        my_information_create_content_tv.setText("创建内容");
        Intent intent = getIntent();
        videoPath = intent.getStringExtra("videoPath");
        videoName = intent.getStringExtra("videoName");
        videoIntroduce = intent.getStringExtra("videoIntroduce");
        videoIsNative = intent.getBooleanExtra("isNative", true);
        MLog.d("videoPath:" + videoPath);
        videoFormat = StringUtil.subString(videoPath, ".", false);
        if (videoName != null) {
            my_information_create_content_title_et.setText(videoName);
            my_information_create_content_title_et.setSelection(my_information_create_content_title_et.getText().length());
        }
        if (videoIntroduce != null) {
            my_information_create_content_brief_introduction_et.setText(videoIntroduce);
            my_information_create_content_brief_introduction_et.setSelection(my_information_create_content_brief_introduction_et.getText().length());
        }
        if (videoIsNative) {
            mThread.start();
        }
        initListener();
        initDialog();
    }

    private void initListener(){
        my_information_create_content_title_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==50){
                    showToastMsg("标题最多只能输入50个字");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        my_information_create_content_brief_introduction_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()==500){
                    showToastMsg("简介最多只能输入500个字");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    public static void fixMediaDir() {
        File sdcard = Environment.getExternalStorageDirectory();
        if (sdcard == null) {
            return;
        }
        ;
        File dcim = new File(sdcard, "DCIM");
        if (dcim == null) {
            return;
        }
        File camera = new File(dcim, "Camera");
        if (camera.exists()) {
            return;
        }
        camera.mkdir();
    }

    private static final int GET_BITMAP = 100;
    private static final int SAVE_BITMAP = 200;
    private static final int CREATE_CONTENT_UI_CLOSE_ERROR_DESC = 300;
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_BITMAP:
                    try {
                        //  从视频中获取的缩略图bitmap 转成uri  在转成图片路径
//                        MLog.d("videoImageBitmap:"+videoImageBitmap);
//                        mCoverUri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), videoImageBitmap, null, null));
//                        MLog.d("mCoverUri:"+mCoverUri);
//                        coverImage = FileUtil.getRealFilePath(CreateContentActivity.this, mCoverUri);
                        my_information_create_content_cover_photo_iv.setImageBitmap(videoImageBitmap);
                    } catch (Exception e) {
                        my_information_create_content_cover_photo_iv.setImageResource(R.drawable.image_defult_icon_small);
                    }
                    break;
                case SAVE_BITMAP:
                    mThread = null;
                    break;
                case CREATE_CONTENT_UI_CLOSE_ERROR_DESC:
                    dismissCreateContentActionFail();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    @OnClick({R.id.my_information_create_content_cancel_rl, R.id.my_information_create_content_cover_photo_btn,
            R.id.my_information_create_content_cover_video_btn,
            R.id.protocol_frame_selection_rl,
            R.id.my_information_create_content_panorama_rl, R.id.my_information_create_content_submitted_btn,
            R.id.protocol_frame_selection_submitted_rl})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_information_create_content_cancel_rl: // 返回到上一层按钮
                CreateContentActivity.this.finish();
                break;
            case R.id.my_information_create_content_cover_photo_btn: // 编辑封面按钮
                editCoverPhoto();
                break;
            case R.id.my_information_create_content_cover_video_btn: // 编辑视频按钮
                videoName = my_information_create_content_title_et.getText().toString();
                videoIntroduce = my_information_create_content_brief_introduction_et.getText().toString();
                Bundle bundle1 = new Bundle();
                bundle1.putString("videoName", videoName);
                bundle1.putString("videoIntroduce", videoIntroduce);
                showActivity(CreateContentActivity.this, ChoiceVideoActivity.class, bundle1);
                CreateContentActivity.this.finish();// 这里有问题
                break;
            case R.id.my_information_create_content_panorama_rl: // 单画面全景选择按钮
                selectUploadSinglePicturePanorama();
                break;
            case R.id.protocol_frame_selection_rl: // 是否选中协议
                if (isChoiceProtocol) {
                    protocol_frame_selection_iv.setImageResource(R.drawable.protocol_frame_selection);
                    my_information_create_content_submitted_btn.setEnabled(true);
                    my_information_create_content_submitted_btn.setBackgroundColor(Color.parseColor("#00b0c7"));
                    isChoiceProtocol = false;
                } else {
                    protocol_frame_selection_iv.setImageResource(R.drawable.protocol_frame_selection_none);
                    my_information_create_content_submitted_btn.setEnabled(false);
                    my_information_create_content_submitted_btn.setBackgroundColor(Color.parseColor("#BEDDE0"));
                    isChoiceProtocol = true;
                }
                break;
            case R.id.protocol_frame_selection_submitted_rl:
                Bundle bundle = new Bundle();
                bundle.putString("loadUrl", AppContent.USER_PROTOCOL);
                bundle.putBoolean("showProgress", true);
                showActivity(CreateContentActivity.this, ShowWebViewActivity.class, bundle);
                break;
            case R.id.my_information_create_content_submitted_btn: // 内容上传提交按钮
                if (CreateContentActivity.this != null && NetWorkDialog.getIsShowNetWorkDialog(CreateContentActivity.this)) {
                    showNetWorkDialog();
                } else {
                    checkUpload();
                }
//                initUpload(videoTitle);
//                getCoverToken(coverImage);
                break;
        }
    }

    private void showNetWorkDialog() {
        mHintDialog.setTitleText("提示");
        mHintDialog.setContextText("目前在2G网络。是否继续上传");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkUpload();
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    private void  checkUpload(){
        mErrorDescAlpha=0;
        dismissCreateContentActionFail();
        videoTitle = my_information_create_content_title_et.getText().toString();
        videoBriefIntroduction = my_information_create_content_brief_introduction_et.getText().toString();
        if (videoImageBitmap == null) {
            showCreateContentActionFail("没有选择封面");
            return;
        }
        if (videoPath.equals("")) {
            showCreateContentActionFail("请先选择视频");
            return;
        }
        if (videoTitle.length() == 0) {
            showCreateContentActionFail("资源标题为空");
            return;
        } else if (videoBriefIntroduction.length() == 0) {
            showCreateContentActionFail("资源简介为空");
            return;
        } else if(videoTypeId == -1){
            showCreateContentActionFail("请选择视频类型");
        }else if (isChoiceProtocol = false) {
            showCreateContentActionFail("请点击我已阅读并接受提交协议");
            return;
        } else if (videoTypeId <= 0) {
            return;
        } else if (!checkNativeVideoName()){
            showCreateContentActionFail("视频名字跟正在上传中的视频名字一样");
        }else {
            myProgressDialog.show();
            // 三个条件都成立，执行资源名称是否重复接口
            EventBus.getDefault().post(new uploadNoticeEvent(""));
            resourceNameIsRepeat();
        }
        MLog.d("coverImage:" + coverImage);
    }

    private boolean checkNativeVideoName(){
        MLog.d("checkNativeVideoName:");
        for(int i = 0; i<videoBeanArrayList.size();i++){
            if(videoBeanArrayList.get(i).getResourceName().equals(videoTitle)){
                return false;
            }
        }
        return true;
    }

    /**
     * 隐藏合集提交错误
     */
    private void dismissCreateContentActionFail() {
        mErrorDescAlpha -= 0.05;
        if (mErrorDescAlpha < 0) {
            mErrorDescAlpha = 0;
            create_content_action_fail_rl.setVisibility(View.GONE);
        } else {
            Message msg = mHandler.obtainMessage();
            msg.what = CREATE_CONTENT_UI_CLOSE_ERROR_DESC;
            mHandler.sendMessageDelayed(msg, 60);
        }
        create_content_action_fail_rl.setAlpha(mErrorDescAlpha);
    }

    /**
     * 展示合集提交错误页面
     *
     * @author DuanChunLin
     * @time 2016/10/31 16:48
     */
    private void showCreateContentActionFail(String failMsg) {
        mErrorDescAlpha = 1.0f;
        create_content_action_fail_rl.setAlpha(mErrorDescAlpha);
        create_content_action_fail_rl.setVisibility(View.VISIBLE);
        create_content_action_fail_desc_tv.setText(failMsg);
        myProgressDialog.dismiss();
//        Toast.makeText(CreateContentActivity.this, failMsg, Toast.LENGTH_SHORT).show();

        mHandler.removeMessages(CREATE_CONTENT_UI_CLOSE_ERROR_DESC);
        Message msg = mHandler.obtainMessage();
        msg.what = CREATE_CONTENT_UI_CLOSE_ERROR_DESC;
        mHandler.sendMessageDelayed(msg, 3000);
    }

    // 编辑封面
    private void editCoverPhoto() {
            Intent intent = new Intent(CreateContentActivity.this, EditVideoCoverActivity.class);
            intent.putExtra("imageUri", coverImage);
            startActivityForResult(intent, EDIT_VIDEO_COVER_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EDIT_VIDEO_COVER_REQUEST:
                if (data != null) {
                    mCoverUri = data.getData();
                    coverImage = FileUtil.getRealFilePath(CreateContentActivity.this, mCoverUri);
                    my_information_create_content_cover_photo_iv.setImageBitmap(getBitmapFromUri(mCoverUri));
                }
                break;
        }
    }

    // 上传单画面全景
    private void selectUploadSinglePicturePanorama() {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("typeId", 1);
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/resources/type/list/id/service", jsonData, new GenericsCallback<QuerySubClassificationObj>() {

            @Override
            public void onError(Call call, Exception e, int id) {
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(QuerySubClassificationObj response, int id) {

                if (response.getResCode().equals("0")) {
                    if (response.getTypeList().size() != 0 && response.getTypeList().size() > 0) {
                        typeList = response.getTypeList();
                        itemsType.clear();
                        for (int i = 0; i < typeList.size(); i++) {
                            itemsType.add(typeList.get(i).getTypeName());
                            typesId.add(String.valueOf(typeList.get(i).getTypeId()));
                        }
                        MLog.d("itemsType:::" + itemsType);
                        initMenuView(itemsType, typesId);
                    }
                } else {
                    showToastMsg(response.getResDesc());
                }
            }
        });
    }

    private void initMenuView(final ArrayList<String> itemsType, final ArrayList<String> typesId) {
        ActionSheet menuView = new ActionSheet(CreateContentActivity.this);
        menuView.setCancelButtonTitle("取消");
        String[] typeName = itemsType.toArray(new String[itemsType.size()]); // 获取类型名称的字符串数组
        typeId = typesId.toArray(new String[typesId.size()]); // 获取类型id的字符串数组
//        menuView.setOnCancelListener(new DialogInterface.OnCancelListener() {
//            @Override
//            public void onCancel(DialogInterface dialog) {
//                MLog.d("ssssssssssssssssssssss");
//            }
//        });
        menuView.addItems(typeName);
        menuView.setItemClickListener(CreateContentActivity.this);
        menuView.setCancelableOnTouchMenuOutside(true);
        menuView.showMenu();
    }

    @Override
    public void onItemClick(int itemPosition) {
        my_information_create_content_panorama_tv.setText(itemsType.get(itemPosition));
        videoTypeId = Integer.parseInt(typeId[itemPosition]);
    }

    /**
     * 查询资源名称是否重复接口
     */
    private void resourceNameIsRepeat() {
        try {
            String resourcesName = URLEncoder.encode(videoTitle, "utf-8");
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("resourcesName", resourcesName);
            hashMap.put("resourcesType", 1);// 资源类型：1、表示视频分类。2、表示应用分类。
            // 转参数
            String jsonData = StringUtil.MapToJson(hashMap);
            // 获取网络参数
            OkHttpAPI.postHttpData("/resources/title/repeat/service", jsonData, new GenericsCallback<ResourcesNameIsRepeatObj>() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    myProgressDialog.dismiss();
//                    showToastMsg("网络连接失败，请检查网络！");
                }

                @Override
                public void onResponse(ResourcesNameIsRepeatObj response, int id) {
                    int isRepeat = response.getIsRepeat();
                    if (isRepeat == 0) {
                        // 资源名称不重复可以进行下一步
                        if (!UploadManageUtil.isCancelled) {
                            showDialog();
                        } else {
                            if (coverImage == null) {
                                showCreateContentActionFail("没有封面图");
                                return;
                            } else {
                                getCoverToken(coverImage);
                            }
                        }
                    } else if (isRepeat == 1) {
                        myProgressDialog.dismiss();
                        showCreateContentActionFail("您输入的资源名称重复了，请重新输入资源名称！");
                    }
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取上传封面图片的token
     *
     * @param file
     */
    private void getCoverToken(final String file) {
        //获取token成功后开始上传用户头像
        String ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("tokenType", 1);
        hashMap.put("userId", ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/qiniu/query/token/service", jsonData, new GenericsCallback<UserIconTokenObj>() {

            @Override
            public void onError(Call call, Exception e, int id) {
                myProgressDialog.dismiss();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserIconTokenObj response, int id) {
                if (response.getResCode().equals("0")) {
                    String token = response.getToken();
                    String fileName = response.getFileName();
                    uploadCoverImage(file, token, fileName);
                } else {
                    myProgressDialog.dismiss();
                    showToastMsg(response.getResDesc());
                }
            }
        });
    }

    /**
     * 上传封面图片到七牛服务器
     *
     * @param file
     * @param token
     * @param fileName
     */
    private void uploadCoverImage(final String file, String token, final String fileName) {
        MLog.d("上传图片的token:" + token);
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, fileName, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        if (info.isOK()) {
                            // 获得上传资源到七牛服务器token
                            qiNiuImageUrl = OkhttpConstant.pic_url + key;
                            if (videoIsNative) {
                                getResourcesToken();
                            } else {
                                showCreateContentActionFail("请先选择上传视频");
                                return;
                            }
                        } else {
                            myProgressDialog.dismiss();
                            showCreateContentActionFail("封面图片上传失败!请重新上传");
                        }
                    }
                }, null);
    }

    /**
     * 获取视频资源上传的token
     */
    private void getResourcesToken() {
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
                myProgressDialog.dismiss();
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserIconTokenObj response, int id) {
                if (response.getResCode().equals("0")) {
                    String token = response.getToken();
                    uploadVideoToQiniuServer(token);
                } else {
                    myProgressDialog.dismiss();
                    showToastMsg("上传视频失败，请检查网络！");
                }
            }
        });
    }

    /**
     * 上传视频资源到七牛服务器
     *
     * @param token
     */
    private void uploadVideoToQiniuServer(String token) {
        MLog.d("上传视频的token:" + token);
        mD5videoTitle = videoTitle+System.currentTimeMillis();
        uploadManageUtil.uploadVideoToQiniuServer(token, mD5videoTitle, videoPath, new UploadInterface() {
                    @Override
                    public void uploadComplete(String key, ResponseInfo info, JSONObject res) {
                        if (res != null) {
                            if (info.isOK() == true) {
                                MLog.d("CreateContentActivity~~~~上传完成！" + OkhttpConstant.video_url + key);
                                // (4)提交所有的数据到本地服务器
                                CommittedAllDataToLocalServer(OkhttpConstant.video_url + key);
                            }
                        }
                    }

                    @Override
                    public void uploadProgress(String key, double percent) {
                        int progress = (int) (percent * 100);
                        MLog.d("CreateContentActivity~~~~progress:" + progress);
                        thisProgress = progress;
                        //过一会在关闭
                        if (progress >= 1) {
                            saveUploadProgress(thisProgress, "1");
//                            uploadManageUtil.removeRepeatListener(mD5videoTitle,);
                            showToastMsg("开始后台上传，请稍后……");  
                            myProgressDialog.dismiss();
                            CreateContentActivity.this.finish();
                        }
                    }

                    @Override
                    public void uploadPause() {
                    }

                    @Override
                    public void uploadFail() {
                        myProgressDialog.dismiss();
                        showToastMsg("上传视频失败，请检查网络！");
                    }
                }
                , videoFormat,true);
    }

    public void showDialog() {
        mHintDialog.setTitleText("提示");
        mHintDialog.setContextText("只能同时上传一个，需要先暂停另一个上传。确定暂停上一个吗？");
        mHintDialog.setCancelText("取消");
        mHintDialog.setOkButText("确定");
        mHintDialog.setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myProgressDialog.dismiss();
                mHintDialog.dismiss();
            }
        });
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadManageUtil.cancelUpload();
                getCoverToken(coverImage);
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    /**
     * 保存上传进度
     *
     * @author HeChuang
     * @time 2016/10/11 10:33
     */
    private void saveUploadProgress(int progress, String uploadState) {
        MLog.d("保存上传进度:");
        uploadBean = new UploadBean();
        uploadBean.setUid(AppContent.UID);
        uploadBean.setResourceName(videoTitle);
        uploadBean.setImagePath(qiNiuImageUrl);
        uploadBean.setMd5ResourceName(mD5videoTitle);
        uploadBean.setResourceIntroduce(videoBriefIntroduction);
        uploadBean.setVideoPath(videoPath);
        uploadBean.setVideoSize(videoSize);
        uploadBean.setVideoTypeId(String.valueOf(videoTypeId));
        uploadBean.setUploadState(uploadState);
        uploadBean.setUploadResourcePath(videoTitle);
        uploadBean.setUploadProgress(String.valueOf(progress));
        uploadBean.setVideoFormat(videoFormat);
        uploadDatabaseTools.saveOrUpdata(uploadBean);
    }

    /**
     * 显示下载进度对话框
     *
     * @param progress
     */
    private void showDialogs(int progress) {
//        if (!mDialog.isShowing()) {
//            mDialog.show();
//        }
        if (progress == 100) {
            mDialog.setMessage("上传完成");
            mDialog.dismiss();
        }
        mDialog.setProgress(progress);
    }

    private void initDialog() {
        mDialog = new CommonProgressDialog(CreateContentActivity.this);
        mDialog.setMessage("正在上传");
        mDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                mDialog.dismiss();
            }
        });
        mDialog.setMax(100);
    }

    private void CommittedAllDataToLocalServer(String resourcesPath) {
        try {
            // 封面图片地址 参数进行URLEncoder.encode编码(UTF-8)
            String encode_coverImage = URLEncoder.encode(qiNiuImageUrl, "UTF-8");
            // 开发商名称 非空 字符串，参数进行URLEncoder.encode编码(UTF-8)
            String encode_developers = URLEncoder.encode("1", "UTF-8");
            // 焦点图URL地址集合 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            ArrayList<String> imgList = new ArrayList<String>();
            String encode_videoImage = URLEncoder.encode(qiNiuImageUrl, "UTF-8");
            imgList.add(encode_videoImage);
            // 资源介绍 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesContent = URLEncoder.encode(videoBriefIntroduction, "UTF-8");
            // 是否原创 -1表示原创资源，参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesOriginal = URLEncoder.encode("-1", "UTF-8");
            // 资源文件地址 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesPath = URLEncoder.encode(resourcesPath, "UTF-8");
            // 资源文件大小 非空 字符串 单位MB 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesSize = URLEncoder.encode(videoSize, "UTF-8");
            // 资源副标题 可为空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesSubtitle = URLEncoder.encode("", "UTF-8");
            // 资源名称 非空 字符串 唯一性验证 参数进行URLEncoder.encode编码(URF-8)
            String encode_resourcesTitle = URLEncoder.encode(videoTitle, "UTF-8");
            // 资源版本号 非空 字符串 参数进行URLEncoder.encode编码(UTF-8)
            String encode_resourcesVersion = URLEncoder.encode("", "UTF-8");
            // 用户ID 非空 字符串 RSA加密
            String ras_userId = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
            // 封装参数
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("coverImg", encode_coverImage);
            hashMap.put("currencyClient", -1);
            hashMap.put("developers", encode_developers);
            hashMap.put("imgList", imgList);
            hashMap.put("resourcesContent", encode_resourcesContent);
            hashMap.put("resourcesDefinition", "");
            hashMap.put("resourcesDriveVersion", "");
            hashMap.put("resourcesLanguage", "1");
            hashMap.put("resourcesOriginal", encode_resourcesOriginal);
            hashMap.put("resourcesPath", encode_resourcesPath);
            hashMap.put("resourcesSize", encode_resourcesSize);
            hashMap.put("resourcesSubtitle", encode_resourcesSubtitle);
            hashMap.put("resourcesTags", "-1"); // 资源标签，非空，字符串
            hashMap.put("resourcesTitle", encode_resourcesTitle);
            hashMap.put("resourcesColumn", 1);
            hashMap.put("resourcesType", videoTypeId);
            hashMap.put("resourcesVersion", encode_resourcesVersion);
            hashMap.put("userId", ras_userId);
            hashMap.put("resourcesLinkType", 1);
            // 转参数
            String jsonData = StringUtil.MapToJson(hashMap);
            // 提交创建内容后的所有数据到本地服务器
            OkHttpAPI.postObjectData("/user/add/resources/service", jsonData, new HttpSuccess<JSONObject>() {
                @Override
                public void onSuccess(JSONObject result, int id) {
                    try {
                        if (result.getString("resCode").equals("0")) {
                            myProgressDialog.dismiss();
//                            showToastMsg(result.getString("resDesc"));
                            uploadDatabaseTools.delete(videoTitle);
                            CreateContentActivity.this.finish();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new HttpError() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    myProgressDialog.dismiss();
//                    showToastMsg("网络连接失败，请检查网络连接！");
                }
            });
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据视频path 获取视频的缩略图
     *
     * @param filePath
     * @return
     */
    public Bitmap  getVideoThumbnail(String filePath) {
        Bitmap bitmap = null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            bitmap = retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();
        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return bitmap;
    }

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public void saveBitmap(Bitmap bitmap,String videoName) {
        coverImage = ImageTools.saveBitmap(CreateContentActivity.this,bitmap,videoName);
        mHandler.sendEmptyMessage(GET_BITMAP);
        mHandler.sendEmptyMessage(SAVE_BITMAP);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(videoImageBitmap!=null){
            videoImageBitmap.recycle();
        }

        if(mHandler!=null){
            mHandler.removeMessages(CREATE_CONTENT_UI_CLOSE_ERROR_DESC);
        }
        ButterKnife.unbind(this);
    }
}
