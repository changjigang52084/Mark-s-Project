package com.xunixianshi.vrshow.my.assemble;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.ScreenUtils;
import com.kevin.crop.UCrop;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.CropActivity;
import com.xunixianshi.vrshow.customview.SheetDialog;
import com.xunixianshi.vrshow.permissions.RxPermissions;
import com.zhy.http.okhttp.utils.PicassoUtil;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * 编辑封面图片
 *@author DuanChunLin
 *@time 2016/10/11 16:17
 */
public class EditCoverFragment extends AssembleBaseFragment {

    public static final String COVER_URI_TAG = "coverUri";
    public static final String COVER_URL_TAG = "coverUrl";
    private SheetDialog sheetDialog;

    @Bind(R.id.edit_cover_image_cover_iv)
    ImageView edit_cover_image_cover_iv;

    @Bind(R.id.title_center_name_tv)
    TextView title_center_name_tv;

    private Uri mCoverUri;
    private String mCoverUrl;
    private Bitmap mBitmap;

    private boolean isCanOpenCamera;
    private static final int GALLERY_REQUEST_CODE = 0;    // 相册选图标记
    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记
    // 拍照临时图片
    private String mTempPhotoPath;
    // 剪切后图像文件
    private Uri mDestinationUri;
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_edit_cover, container, false);
        rootView.setClickable(true);// 防止点击穿透，底层的fragment响应上层点击触摸事件
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initView(View parentView) {
        title_center_name_tv.setText("封面");
        sheetDialog = new SheetDialog(getActivity());
    }

    @Override
    protected void initData() {
        Bundle bundle = getArguments();
        if (bundle != null) {
            mCoverUri = (Uri) bundle.get(COVER_URI_TAG);
            mCoverUrl = (String) bundle.get(COVER_URL_TAG);
        }
        mDestinationUri = Uri.fromFile(new File(getActivity().getCacheDir(), "cropImage.jpg"));
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpg";
        int screenWith = ScreenUtils.getScreenWidth(getActivity());
        ViewGroup.LayoutParams lp;
        lp = edit_cover_image_cover_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int)(screenWith*0.56);
        edit_cover_image_cover_iv.setLayoutParams(lp);

        if(mCoverUri!=null){
            edit_cover_image_cover_iv.setImageBitmap(getBitmapFromUri(mCoverUri));
        }else if(mCoverUrl !=null){
            Picasso.with(getActivity()).load(PicassoUtil.utf8Togb2312(mCoverUrl)).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    mBitmap = bitmap;
                    edit_cover_image_cover_iv.setImageBitmap(mBitmap);
                }
                @Override
                public void onBitmapFailed(Drawable errorDrawable) {
                }
                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {
                }
            });
            selectVideoCoverDialog();
        }
        else{
            selectVideoCoverDialog();
        }

        RxPermissions rxPermissions = RxPermissions.getInstance(getContext());
        rxPermissions.request(Manifest.permission.CAMERA)
                .subscribe(new Action1<Boolean>() {
                    @Override
                    public void call(Boolean granted) {
                        if (granted) { // 在android 6.0之前会默认返回true
                            // 已经获取权限
                            isCanOpenCamera = true;
                        } else {
                            // 未获取权限
                            isCanOpenCamera = false;
                        }
                    }
                });
    }

    @OnClick({R.id.title_left_back_iv,R.id.title_right_function_tv,R.id.edit_cover_cancel_tv,R.id.edit_cover_sure_tv})
    public void onClick(View view){
        switch (view.getId()){
            case R.id.title_left_back_iv: //关闭
            case R.id.edit_cover_cancel_tv: //取消
                exitBack();
                break;
            case R.id.title_right_function_tv: //拍照
                selectVideoCoverDialog();
                break;
            case R.id.edit_cover_sure_tv: //选取
                Intent intent = new Intent();
                intent.putExtra(COVER_URI_TAG,mCoverUri);
                backResult(intent);
                break;
        }
    }

    // 选择视频封面dialog
    private void selectVideoCoverDialog() {
        sheetDialog.setTopButtonText("拍照");
        sheetDialog.setCenterButtonText("从手机相册选择");
        sheetDialog.setCancelButtonText("取消");
        sheetDialog.setOnTopClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCanOpenCamera){
                    takePhoto();
                }
            }
        });

        sheetDialog.setOnCenterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isCanOpenCamera) {
                    pickFromGallery();
                }
            }
        });
        if(mCoverUri!= null || mBitmap!=null){
            sheetDialog.setBottomButtonText("保存图片");
            sheetDialog.setOnBottomClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                            if(mCoverUri!=null) {
                                saveBitmap(getBitmapFromUri(mCoverUri));
                            } else{
                                saveBitmap(mBitmap);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    showToastMsg("已保存");
                    sheetDialog.dismiss();
                }
            });
        }
        sheetDialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
            }
        });
        sheetDialog.show();
    }

    public File  saveBitmap(Bitmap bm) throws IOException {
        String path = Environment.getExternalStorageDirectory().toString()+ "/image/";
        File dirFile = new File(path);
        if(!dirFile.exists()){
            dirFile.mkdirs();
        }
        Date dt= new Date();
        Long time= dt.getTime();
        File myIconFile= new File(path + time.toString() + ".jpg");
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myIconFile));
        bm.compress(Bitmap.CompressFormat.JPEG, 80, bos);
        bos.flush();
        bos.close();
        return myIconFile;
    }

    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            MLog.d("没有权限");
//            requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,
//                    "选择图片时需要读取权限",102);
        } else {
            Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //下面这句指定调用相机拍照后的照片存储的路径
            takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTempPhotoPath)));
            getActivity().startActivityForResult(takeIntent, CAMERA_REQUEST_CODE);
        }
    }

    private void pickFromGallery() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            MLog.d("没有权限");
//            requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE,
//                    "选择图片时需要读取权限",101);
        } else {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, null);
            // 如果限制上传到服务器的图片类型时可以直接写如："image/jpeg 、 image/png等的类型"
            pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
            getActivity().startActivityForResult(pickIntent, GALLERY_REQUEST_CODE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == getActivity().RESULT_OK) {
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
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startCropActivity(Uri uri) {
        UCrop.of(uri, mDestinationUri)
                .withAspectRatio(16, 9)
                .withMaxResultSize(512, 512)
                .withTargetActivity(CropActivity.class)
                .start(getActivity());
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        mCoverUri = UCrop.getOutput(result);
        edit_cover_image_cover_iv.setImageBitmap(getBitmapFromUri(mCoverUri));
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
            MLog.e("handleCropError: ", cropError);
            Toast.makeText(getActivity(), cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getActivity(), "无法剪切选择图片", Toast.LENGTH_SHORT).show();
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
        if(mBitmap!=null && !mBitmap.isRecycled()){
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}
