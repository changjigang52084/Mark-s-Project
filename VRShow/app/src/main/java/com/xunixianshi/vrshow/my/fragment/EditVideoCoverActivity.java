package com.xunixianshi.vrshow.my.fragment;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;

import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SDCardUtil;
import com.hch.viewlib.util.ScreenUtils;
import com.kevin.crop.UCrop;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.actmanager.CropActivity;
import com.xunixianshi.vrshow.customview.SheetDialog;
import com.xunixianshi.vrshow.permissions.RxPermissions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.functions.Action1;

/**
 * TODO 编辑视频封面页面
 *
 * @author MarkChang
 * @ClassName EditVideoCoverActivity
 * @time 2016/11/1 15:54
 */
public class EditVideoCoverActivity extends BaseAct {

    private static final String TAG = "EditVideoCoverActivity";
    @Bind(R.id.my_content_tv)
    TextView my_content_tv;
    @Bind(R.id.my_fragment_edit_video_covers_iv)
    ImageView my_fragment_edit_video_covers_iv;
    @Bind(R.id.edit_cover_bottom_rl)
    RelativeLayout edit_cover_bottom_rl;

    private static final int GALLERY_REQUEST_CODE = 0;    // 相册选图标记
    private static final int CAMERA_REQUEST_CODE = 1;    // 相机拍照标记
    // 拍照临时图片
    private String mTempPhotoPath;
    // 剪切后图像文件
    private Uri mDestinationUri;
    private SheetDialog sheetDialog;
    private Bitmap photoBitmap;
    private Uri mCoverUri;
    private static final String IMAGE_FILE_VIDEO_COVER = "VRShow_video_cover.jpg";

    private boolean isCanOpenCamera;
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            msg.what = 0x123;
            selectVideoCoverDialog();
        }
    };

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_edit_video_cover);
        ButterKnife.bind(this);
    }

    @Override
    protected void initView() {
        super.initView();
        String imageUri = null;
        my_content_tv.setText("封面");
        Intent intent = getIntent();
        if (intent != null) {
            imageUri = intent.getStringExtra("imageUri");
        }
        int screenWith = ScreenUtils.getScreenWidth(EditVideoCoverActivity.this);
        ViewGroup.LayoutParams lp;
        lp = my_fragment_edit_video_covers_iv.getLayoutParams();
        lp.width = screenWith;
        lp.height = (int) (screenWith * 0.56);
        my_fragment_edit_video_covers_iv.setLayoutParams(lp);

        if (imageUri != null) {
            photoBitmap = convertToBitmap(imageUri,lp.width,lp.height);
            my_fragment_edit_video_covers_iv.setImageBitmap(photoBitmap);
        } else {
            handler.sendEmptyMessage(0x123);
        }
    }

    @Override
    protected void initData() {
        super.initData();
        sheetDialog = new SheetDialog(EditVideoCoverActivity.this);
        mDestinationUri = Uri.fromFile(new File(EditVideoCoverActivity.this.getCacheDir(), "cropImage.jpg"));
        mTempPhotoPath = Environment.getExternalStorageDirectory() + File.separator + "photo.jpg";
        RxPermissions rxPermissions = RxPermissions.getInstance(this);
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

    @OnClick({R.id.my_back_rl, R.id.my_content_choice_photo_rl, R.id.edit_cover_cancel_tv, R.id.edit_cover_sure_tv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_back_rl:
                EditVideoCoverActivity.this.finish();
                break;
            case R.id.my_content_choice_photo_rl:
                selectVideoCoverDialog();
                break;
            case R.id.edit_cover_cancel_tv:
                EditVideoCoverActivity.this.finish();
                break;
            case R.id.edit_cover_sure_tv:
                Intent intent = new Intent(EditVideoCoverActivity.this, CreateContentActivity.class);
                if (photoBitmap == null) {
                    EditVideoCoverActivity.this.finish();
                } else {
                    intent.setData(mCoverUri);
                    setResult(RESULT_OK, intent);
                    EditVideoCoverActivity.this.finish();
                }
                break;
        }
    }

    // 选择视频封面dialog
    private void selectVideoCoverDialog() {
        sheetDialog.setTopButtonText("拍照");
        sheetDialog.setCenterButtonText("从手机相册选择");
        sheetDialog.setBottomButtonText("保存图片");
        sheetDialog.setCancelButtonText("取消");

        sheetDialog.setOnTopClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhoto();
//                if(isCanOpenCamera){
//                    PicCrop.cropFromCamera(EditVideoCoverActivity.this, null, PicCrop.TYPE_16_9);
//                }
            }
        });

        sheetDialog.setOnCenterClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickFromGallery();
//                if(isCanOpenCamera){
//                    PicCrop.cropFromGallery(EditVideoCoverActivity.this, null, PicCrop.TYPE_16_9);
//                }
            }
        });

        sheetDialog.setOnBottomClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveBitmap(""+System.currentTimeMillis());
                showToastMsg("保存成功");
//                EditVideoCoverActivity.this.finish();
            }
        });

        sheetDialog.setOnCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sheetDialog.dismiss();
            }
        });

        sheetDialog.show();
    }

    private void takePhoto() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN // Permission was added in API Level 16
                && ActivityCompat.checkSelfPermission(EditVideoCoverActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
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
                && ActivityCompat.checkSelfPermission(EditVideoCoverActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
        if (resultCode == EditVideoCoverActivity.this.RESULT_OK) {
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

    public void saveBitmap(String imageName) {
        MLog.e(TAG, "保存图片");
        File f = new File("/sdcard/videoCover/", imageName+".jpg");
        if (SDCardUtil.fileIsExists("/sdcard/videoCover/", imageName+".jpg")) {
            f.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(f);
            photoBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.flush();
            out.close();
            MLog.i(TAG, "已经保存");
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public Bitmap convertToBitmap(String path, int w, int h) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        // 设置为ture只获取图片大小
        opts.inJustDecodeBounds = true;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        // 返回为空
        BitmapFactory.decodeFile(path, opts);
        int width = opts.outWidth;
        int height = opts.outHeight;
        float scaleWidth = 0.f, scaleHeight = 0.f;
        if (width > w || height > h) {
            // 缩放
            scaleWidth = ((float) width) / w;
            scaleHeight = ((float) height) / h;
        }
        opts.inJustDecodeBounds = false;
        float scale = Math.max(scaleWidth, scaleHeight);
        opts.inSampleSize = (int)scale;
        WeakReference<Bitmap> weak = new WeakReference<Bitmap>(BitmapFactory.decodeFile(path, opts));
        return Bitmap.createScaledBitmap(weak.get(), w, h, true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
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
                .start(EditVideoCoverActivity.this);
    }

    /**
     * 处理剪切成功的返回值
     *
     * @param result
     */
    private void handleCropResult(Intent result) {
        deleteTempPhotoFile();
        mCoverUri = UCrop.getOutput(result);
        if (null != mCoverUri) {
            try {
                photoBitmap = MediaStore.Images.Media.getBitmap(EditVideoCoverActivity.this.getContentResolver(), mCoverUri);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            my_fragment_edit_video_covers_iv.setImageBitmap(photoBitmap);
            edit_cover_bottom_rl.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(EditVideoCoverActivity.this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
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
            Toast.makeText(EditVideoCoverActivity.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(EditVideoCoverActivity.this, "无法剪切选择图片", Toast.LENGTH_SHORT).show();
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
