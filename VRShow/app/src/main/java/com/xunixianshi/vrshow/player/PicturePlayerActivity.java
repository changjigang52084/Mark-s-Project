package com.xunixianshi.vrshow.player;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.opengl.GLSurfaceView;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.asha.vrlib.MDVRLibrary;
import com.asha.vrlib.model.MDRay;
import com.asha.vrlib.plugins.IMDHotspot;
import com.asha.vrlib.texture.MD360BitmapTexture;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 全景图片查看页
 * @ClassName PicturePlayerActivity
 *@author HeChuang
 *@time 2016/11/1 15:52
 */
public class PicturePlayerActivity extends BaseAct implements MDVRLibrary.IBitmapProvider {

    private static final String PICTURE_INFO_URL_KEY = "url";
    private static final String PICTURE_INFO_NAME_KEY = "name";

    @Bind(R.id.vr_picture_gl_view)
    GLSurfaceView vr_picture_gl_view;

    @Bind(R.id.back_bt)
    Button back_bt;

    @Bind(R.id.name_tv)
    TextView name_tv;
    @Bind(R.id.picture_toolbar_btn_gyro_iv)
    ImageView picture_toolbar_btn_gyro_iv;
    @Bind(R.id.picture_toolbar_btn_screen_iv)
    ImageView picture_toolbar_btn_screen_iv;
    @Bind(R.id.vr_picture_tools_rl)
    RelativeLayout vr_picture_tools_rl;

    private MDVRLibrary mMDVRLibrary;
    private static final int FADE_OUT = 1;
    private String openUrl;
    private boolean mGeneralEnable = true;
    private boolean mShowing = false;
    private static final int sDefaultTimeout = 3000;
    private boolean isSplitScreen;

    /**
     * By Lin Date:2016/8/5  10:50
     *
     * @注释： 参数较多时，用类来表示
     */
    public static Intent newIntent(Context context, String name, String url) {
        Intent intent = new Intent(context, PicturePlayerActivity.class);
        intent.putExtra(PICTURE_INFO_URL_KEY, url);
        intent.putExtra(PICTURE_INFO_NAME_KEY, name);
        return intent;
    }

    public static void intentTo(Context context, String name, String url) {
        context.startActivity(newIntent(context, name, url));
    }

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_vr_picture);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        openUrl = getIntent().getStringExtra(PICTURE_INFO_URL_KEY);
        String name = getIntent().getStringExtra(PICTURE_INFO_NAME_KEY);
//        name_tv.setText(StringUtil.isEmpty(name)?"":name);


        bindVRLibrary();
    }

    @OnClick(R.id.back_bt)
    void back_bt() {
        this.finish();
    }

        private void loadImage(String url, final MD360BitmapTexture.Callback callback) {
            Picasso.with(this).load(url).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    getVRLibrary().onTextureResize(bitmap.getWidth(), bitmap.getHeight());
                    // texture
                    callback.texture(bitmap);
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
    }


    private MDVRLibrary getVRLibrary() {
        return mMDVRLibrary;
    }

    public MDVRLibrary bindVRLibrary() {
        mMDVRLibrary = MDVRLibrary.with(this)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .pinchEnabled(true)
                .asBitmap(this)
                .listenTouchPick(new MDVRLibrary.ITouchPickListener() {
                    @Override
                    public void onHotspotHit(IMDHotspot hitHotspot, MDRay ray) {
                    }
                })
                .build(vr_picture_gl_view);
        return mMDVRLibrary;
    }

    @Override
    public void onProvideBitmap(MD360BitmapTexture.Callback callback) {
        loadImage(openUrl, callback);
    }

    @OnClick({R.id.picture_toolbar_btn_gyro_iv, R.id.picture_toolbar_btn_screen_iv})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture_toolbar_btn_gyro_iv:
                // 全景
                if (mMDVRLibrary != null) {
                    mGeneralEnable = !mGeneralEnable;
                    mMDVRLibrary.switchInteractiveMode(this, mGeneralEnable ? MDVRLibrary.INTERACTIVE_MODE_MOTION:MDVRLibrary.INTERACTIVE_MODE_TOUCH);
                    mMDVRLibrary.switchProjectionMode(this, MDVRLibrary.PROJECTION_MODE_SPHERE);
                    refreshButtonView();
                }
//                show();
                break;
            case R.id.picture_toolbar_btn_screen_iv:
                //分屏
                if (mMDVRLibrary != null) {
                    if(mMDVRLibrary.getDisplayMode() == MDVRLibrary.DISPLAY_MODE_GLASS){
                        isSplitScreen = false;
                    }else{
                        isSplitScreen = true;
                    }
                    mMDVRLibrary.switchDisplayMode(this,isSplitScreen ? MDVRLibrary.DISPLAY_MODE_GLASS:MDVRLibrary.DISPLAY_MODE_NORMAL);
                    refreshButtonView();
                }
//                show();
                break;
        }
    }

    /**
     *By Lin Date:2016/8/17  18:15
     *@注释：刷新 按钮状态
     */
    public void refreshButtonView() {
        if (mGeneralEnable) {
            picture_toolbar_btn_gyro_iv.setBackgroundResource(R.drawable.gyroscope_btn_on);
        } else {
            picture_toolbar_btn_gyro_iv.setBackgroundResource(R.drawable.gyroscope_btn_off);
        }
        if(isSplitScreen){
            picture_toolbar_btn_screen_iv.setBackgroundResource(R.drawable.screen_btn_off); //需要美术出图
        }else{
            picture_toolbar_btn_screen_iv.setBackgroundResource(R.drawable.screen_btn_on);
        }
    }
    /**
     *By Lin Date:2016/8/17  18:20
     *@注释：显示播放器UI
     */
    public void show() {
        show(sDefaultTimeout);
    }
    private final Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case FADE_OUT:
                    hide();
                    break;
            }
        }
    };

    /**
     *By Lin Date:2016/8/17  18:20
     *@注释：显示播放器UI
     */
    public void show(int timeout) {
        if (!mShowing) {
            vr_picture_tools_rl.setVisibility(View.VISIBLE);
            mShowing = true;
        }
        if (timeout != 0) {
            mHandler.removeMessages(FADE_OUT);
            Message msg = mHandler.obtainMessage(FADE_OUT);
            mHandler.sendMessageDelayed(msg, timeout);
        }
    }
    public void hide() {
        if (mShowing) {
            vr_picture_tools_rl.setVisibility(View.GONE);
            mShowing = false;
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mMDVRLibrary.onResume(this);
    }
    @Override
    protected void onPause() {
        super.onPause();
        mMDVRLibrary.onPause(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mMDVRLibrary.onDestroy();
        ButterKnife.unbind(this);
    }

}

