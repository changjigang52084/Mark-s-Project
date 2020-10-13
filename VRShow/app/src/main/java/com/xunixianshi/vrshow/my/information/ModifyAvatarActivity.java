package com.xunixianshi.vrshow.my.information;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.hch.utils.VR_RsaUtils;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.kevin.crop.UCrop;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.actmanager.CropActivity;
import com.xunixianshi.vrshow.customview.HintDialog;
import com.xunixianshi.vrshow.customview.SheetDialog;
import com.xunixianshi.vrshow.obj.UserIconTokenObj;
import com.xunixianshi.vrshow.permissions.RxPermissions;
import com.xunixianshi.vrshow.util.AppContent;
import com.xunixianshi.vrshow.util.ImageTools;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;
import com.zhy.http.okhttp.interfaces.HttpError;
import com.zhy.http.okhttp.interfaces.HttpSuccess;
import com.zhy.http.okhttp.utils.PicassoUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import rx.functions.Action1;

/**
 * TODO 个人头像页面
 *
 * @author MarkChang
 * @ClassName ModifyAvatarActivity
 * @time 2016/11/1 15:41
 */
public class ModifyAvatarActivity extends BaseAct {

    private static final String TAG = "ModifyAvatarActivity";
    @Bind(R.id.my_back_rl)
    RelativeLayout my_back_rl;
    @Bind(R.id.my_content_tv)
    TextView my_content_tv;
    @Bind(R.id.my_content_administration_iv)
    ImageView my_content_administration_iv;
    @Bind(R.id.my_content_administration_rl)
    RelativeLayout my_content_administration_rl;
    @Bind(R.id.my_information_personal_avatar_iv)
    ImageView my_information_personal_avatar_iv;
    @Bind(R.id.my_information_bottom_rl)
    RelativeLayout my_information_bottom_rl;

    private SheetDialog sheetDialog;
    private Bitmap iconBitmap;
    public static final String KEY_USER_ICON = "KEY_USER_ICON";
    private String userIcon;
    private Uri iconUri;
    private boolean iconType = true;//true 代表是网络路径  false代表是本地路径
    private HintDialog mHintDialog;
    private boolean isBitmap = false;

    private boolean isCanOpenCamera;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_modify_avatar);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();

    }

    @Override
    protected void initData() {
        super.initData();
        sheetDialog = new SheetDialog(ModifyAvatarActivity.this);
        mHintDialog = new HintDialog(ModifyAvatarActivity.this);
        mDestinationUri = Uri.fromFile(new File(ModifyAvatarActivity.this.getCacheDir(), "cropImage.jpg"));
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpg";
        my_content_tv.setText("个人头像");
        Intent intent = getIntent();
        if (intent != null) {
            userIcon = intent.getStringExtra("userIcon");
            iconType = intent.getBooleanExtra("iconType", true);
        }
        int screenWith = ScreenUtils.getScreenWidth(ModifyAvatarActivity.this);
        ViewGroup.LayoutParams lp;
        lp = my_information_personal_avatar_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = screenWith;
        my_information_personal_avatar_iv.setLayoutParams(lp);
        MLog.d("userIcon:" + userIcon);
        if (userIcon != null) {
            if (iconType) {
                PicassoUtil.loadImage(ModifyAvatarActivity.this, userIcon, my_information_personal_avatar_iv);
            } else {
                PicassoUtil.loadImage(ModifyAvatarActivity.this, new File(userIcon), my_information_personal_avatar_iv);
            }
        } else {
            selectUserIconDialog();
        }
        RxPermissions rxPermissions = RxPermissions.getInstance(this);
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        MLog.d("granted::" + granted);
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                            isCanOpenCamera = true;
                        } else {
                            // 未获取权限
                            isCanOpenCamera = false;
                            showHintDialog();
                        }
                    }
                });
    }

    private void showHintDialog() {
        mHintDialog.setTitleText("提示");
        mHintDialog.setContextText("请前往设置中开启VRShow允许使用摄像头权限");
        mHintDialog.setOkButText("确定");
        mHintDialog.setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mHintDialog.dismiss();
            }
        });
        mHintDialog.show();
    }

    @OnClick({R.id.my_back_rl, R.id.my_content_administration_rl, R.id.my_information_cancel_tv, R.id.my_information_sure_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_back_rl:
                ModifyAvatarActivity.this.finish();
                break;
            case R.id.my_content_administration_rl:
                selectUserIconDialog();
                break;
            case R.id.my_information_cancel_tv:
                ModifyAvatarActivity.this.finish();
                break;
            case R.id.my_information_sure_tv:
                if (iconBitmap == null) {
                    ModifyAvatarActivity.this.finish();
                } else {
                    getToken(iconUri.getPath());
                }
                break;
        }
    }
    /*=======================选择头像==========================*/

    /**
     * Callback received when a permissions request has been completed.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 101:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickFromGallery();
                }
                break;
            case 102:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    takePhoto();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * @return void 返回类型
     * @Title: selectUserIconDialog
     * @Description: TODO 选择用户头像
     * @author Mark.剛
     * @date 2016-9-22
     */
    private static final int GALLERY_REQUEST_CODE = 0;    // 相册选图标记
    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记
    // 拍照临时图片
    private String mTempPhotoPath;
    // 剪切后图像文件
    private Uri mDestinationUri;
    private void selectUserIconDialog() {
        sheetDialog.setTopButtonText("拍照");
        sheetDialog.setCenterButtonText("从手机相册选择");
        sheetDialog.setBottomButtonText("保存图片");
        sheetDialog.setCancelButtonText("取消");

        sheetDialog.setOnTopClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
//                if (isCanOpenCamera) {
//                    PicCrop.cropFromCamera(ModifyAvatarActivity.this, null, PicCrop.TYPE_1_1);
//                }
            }
        });

        sheetDialog.setOnCenterClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
//                PicCrop.cropFromGallery(ModifyAvatarActivity.this, null, PicCrop.TYPE_1_1);
            }
        });

        sheetDialog.setOnBottomClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MLog.e("保存图片1");
                String imgPath  = "";
                if(isBitmap){
                    imgPath = ImageTools.saveBitmap(ModifyAvatarActivity.this, iconBitmap, "" + System.currentTimeMillis());
                }else{
                    imgPath = ImageTools.saveBitmap(ModifyAvatarActivity.this, userIcon, "" + System.currentTimeMillis());
                }
                if (!StringUtil.isEmpty(imgPath)) {
                    showToastMsg("图片已经保存到" + imgPath);
                }
                //保存图片不关闭
//                ModifyAvatarActivity.this.finish();
            }
        });

        sheetDialog.setOnCancelClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
            }
        });

        sheetDialog.show();
    }

    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(ModifyAvatarActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    "选择图片时需要读取权限",102);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //下面这句指定调用相机拍照后的照片存储的路径
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
            startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(ModifyAvatarActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
                    "选择图片时需要读取权限",101);
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
        }
    }

    //使用时的调用:
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == ModifyAvatarActivity.this.RESULT_OK) {
            switch (requestCode) {
                case CAMERA_REQUEST_CODE:   // 调用相机拍照
                    File temp = new File(mTempPhotoPath);
                    startCropActivity(Uri.fromFile(temp));
                    break;
                case GALLERY_REQUEST_CODE:  // 直接从相册获取
                    startCropActivity(data.getData());
                    break;
                case UCrop.REQUEST_CROP:    // 裁剪图片结果
                    handleCropResult(data);
                    break;
                case UCrop.RESULT_ERROR:    // 裁剪图片错误
                    handleCropError(data);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
//        PicCrop.onActivityResult(requestCode, resultCode, data, ModifyAvatarActivity.this, new PicCrop.CropHandler() {
//            @Override
//            public void handleCropResult(Uri uri, int tag) {
//                MLog.d("uri:" + uri);
//                iconUri = uri;
//                iconBitmap = getBitmapFromUri(iconUri);
//                my_information_personal_avatar_iv.setImageBitmap(iconBitmap);
//                getToken(iconUri.getPath());
//            }
//
//            @Override
//            public void handleCropError(Intent data) {
//                showToastMsg("编辑照片失败！");
//            }
//        });
    }

    /**
     * 获取上传用户头像的token
     *
     * @param file
     */
    private void getToken(final String file) {
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
//                showToastMsg("网络连接失败，请检查网络！");
            }

            @Override
            public void onResponse(UserIconTokenObj response, int id) {
                if (response.getResCode().equals("0")) {
                    String token = response.getToken();
                    String fileName = response.getFileName();
                    upLoadUserIcon(file, token, fileName);
                } else {
                    showToastMsg(response.getResDesc());
                }
            }
        });
    }

    /**
     * 上报用户修改的用户头像
     *
     * @param file
     * @param token
     * @param fileName
     */
    private void upLoadUserIcon(final String file, String token, final String fileName) {
        UploadManager uploadManager = new UploadManager();
        uploadManager.put(file, fileName, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        if (info.isOK()) {
                            toKeyServer(key, file);
                        } else {
                            showToastMsg("头像上传失败!请重新上传");
                        }
                    }
                }, null);
    }

    /**
     * 图片在七牛上传成功后把返回的key上传给服务器
     *
     * @param key
     */
    private void toKeyServer(String key, final String file) {
        String avatarUrl = key;
        MLog.d("avatarUrl:" + avatarUrl);
        String ras_uid = VR_RsaUtils.encodeByPublicKey(AppContent.UID);
        // 封装参数
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("avatarUrl", avatarUrl);
        hashMap.put("userId", ras_uid);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);

        OkHttpAPI.postObjectData("/modify/user/avatar/service", jsonData, new HttpSuccess<JSONObject>() {
            @Override
            public void onSuccess(JSONObject result, int id) {
                try {
                    String code = result.getString("resCode");
                    if (code.equals("0")) {
                        showToastMsg("头像上传成功！");
                        Intent intent = new Intent();
                        intent.putExtra("filePath", file);
                        setResult(1, intent);
                    } else {
                        showToastMsg("头像上传失败！请重新上传");
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

    private Bitmap getBitmapFromUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if(iconBitmap != null){
            iconBitmap.recycle();
        }
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                //设置剪切比例
                .withAspectRatio(1, 1)
                .withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(ModifyAvatarActivity.this);
    }

    /**
     * 处理剪切成功的nd按
     *
     * @param result
     */
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        final Uri resultUri = UCrop.getOutput(result);
        if (null != resultUri) {
            try {
                iconBitmap = MediaStore.Images.Media.getBitmap(ModifyAvatarActivity.this.getContentResolver(), resultUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            isBitmap = true;
            my_information_personal_avatar_iv.setImageBitmap(iconBitmap);
            getToken(resultUri.getPath());
        } else {
            Toast.makeText(ModifyAvatarActivity.this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 处理剪切失败的返回值
     *
     * @param result
     */
    private void handleCropError(Intent result) {
        deleteTempPhotoFile();
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e(TAG, "handleCropError: ", cropError);
            Toast.makeText(ModifyAvatarActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(ModifyAvatarActivity.this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 删除拍照临时文件
     */
    private void deleteTempPhotoFile() {
        File tempFile = new File(mTempPhotoPath);
        if (tempFile.exists() && tempFile.isFile()) {
            tempFile.delete();
        }
    }

}
