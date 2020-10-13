package com.xnxs.mediaplayer.widget.media;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.asha.vrlib.MDVRLibrary;

import java.lang.ref.WeakReference;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by duanchunlin on 2016/8/4.
 */
public class GLSurfaceRenderView extends GLSurfaceView implements IRenderView {
    public static  final  int SURFACE_CREATE_MODE_DEFAULT = 0;
    public static final int SURFACE_CREATE_MODE_MD_VR = 1;

    private MeasureHelper mMeasureHelper;
    private SurfaceCallback mSurfaceCallback;
    private VRControl.IOnSurfaceTextureResize mIOnSurfaceTextureResize;


    public GLSurfaceRenderView(Context context) {
        super(context);
        initView();
    }

    public GLSurfaceRenderView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        mMeasureHelper = new MeasureHelper(this);
        mSurfaceCallback = new SurfaceCallback(this);
        getHolder().addCallback(mSurfaceCallback);
        //noinspection deprecation
        getHolder().setType(SurfaceHolder.SURFACE_TYPE_GPU);
    }

    /**
     * 参考 SurfaceCallback 方法
     *@author DuanChunLin
     *@time 2017/1/5 15:32
     */
    public void setSurfaceCreateMode(int mode){
        if(mSurfaceCallback!=null){
            mSurfaceCallback.setSurfaceCreateMode(mode);
        }
    }


    public void setIOnSurfaceTextureResize(VRControl.IOnSurfaceTextureResize onSurfaceTextureResize){
        mIOnSurfaceTextureResize = onSurfaceTextureResize;
    }

    public SurfaceCallback getSurfaceCallback(){
        return mSurfaceCallback;
    }

    @Override
    public View getView() {
        return this;
    }

    @Override
    public boolean shouldWaitForResize() {
        return true;
    }
    //--------------------
    // Layout & Measure
    //--------------------
    @Override
    public void setVideoSize(int videoWidth, int videoHeight) {
        if (videoWidth > 0 && videoHeight > 0) {
            mMeasureHelper.setVideoSize(videoWidth, videoHeight);
            getHolder().setFixedSize(videoWidth, videoHeight);
            if(mIOnSurfaceTextureResize!=null){
                mIOnSurfaceTextureResize.onTextureResize(videoWidth,videoHeight);
            }
            requestLayout();
        }
    }

    @Override
    public void setVideoSampleAspectRatio(int videoSarNum, int videoSarDen) {
        if (videoSarNum > 0 && videoSarDen > 0) {
            mMeasureHelper.setVideoSampleAspectRatio(videoSarNum, videoSarDen);
            requestLayout();
        }
    }

    @Override
    public void setVideoRotation(int degree) {
        Log.e("", "SurfaceView doesn't support rotation (" + degree + ")!\n");
    }

    @Override
    public void setAspectRatio(int aspectRatio) {
        mMeasureHelper.setAspectRatio(aspectRatio);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mMeasureHelper.doMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mMeasureHelper.getMeasuredWidth(), mMeasureHelper.getMeasuredHeight());
    }

    //-------------------------

    @Override
    public void addRenderCallback(IRenderCallback callback) {
        mSurfaceCallback.addRenderCallback(callback);
    }

    @Override
    public void removeRenderCallback(IRenderCallback callback) {
        mSurfaceCallback.removeRenderCallback(callback);
    }

    private static final class InternalSurfaceHolder implements IRenderView.ISurfaceHolder {
        private GLSurfaceRenderView mGLSurfaceView;
        private Surface mVRSurface;

        @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
        public void bindToMediaPlayer(IMediaPlayer mp) {
            if (mp != null && mVRSurface != null) {
                mp.setSurface(mVRSurface);
                if (mp instanceof IjkMediaPlayer) {
                    ((IjkMediaPlayer) mp).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "opensles", 0); //开启耗时。可考虑缓冲设置。
                    ((IjkMediaPlayer) mp).setOption(IjkMediaPlayer.OPT_CATEGORY_PLAYER, "overlay-format", IjkMediaPlayer.SDL_FCC_YV12);
                }
            }
        }

        public InternalSurfaceHolder(@NonNull GLSurfaceRenderView glSurfaceView, @Nullable Surface surface) {
            mGLSurfaceView = glSurfaceView;
            mVRSurface = surface;
        }

        @NonNull
        @Override
        public IRenderView getRenderView() {
            return mGLSurfaceView;
        }

        @Nullable
        @Override
        public SurfaceHolder getSurfaceHolder() {
            if (mGLSurfaceView == null) {
                return null;
            }
            return mGLSurfaceView.getHolder();
        }

        @Nullable
        @Override
        public SurfaceTexture getSurfaceTexture() {
            return null;
        }

        @Override
        public void onResume() {
            if(mGLSurfaceView!=null){
                mGLSurfaceView.onResume();
            }
        }

        @Override
        public void onPause() {
            if(mGLSurfaceView!=null){
                mGLSurfaceView.onPause();
            }
        }

        @Nullable
        @Override
        public Surface openSurface() {
            if (mVRSurface != null) {
                return mVRSurface;
            }
            if (mGLSurfaceView == null)
                return null;
            return mGLSurfaceView.getHolder().getSurface();
        }
    }

    private static final class SurfaceCallback implements SurfaceHolder.Callback, MDVRLibrary.IOnSurfaceReadyCallback {

        private SurfaceHolder mSurfaceHolder;
        private boolean mIsFormatChanged;
        private int mFormat;
        private int mWidth;
        private int mHeight;
        private boolean mIsSurfaceCreate = false;
        private int mSurfaceCreateMode = 0;

        private WeakReference<GLSurfaceRenderView> mWeakSurfaceView;
        private Map<IRenderCallback, Object> mRenderCallbackMap = new ConcurrentHashMap<IRenderCallback, Object>();

        public SurfaceCallback(@NonNull GLSurfaceRenderView surfaceView) {
            mWeakSurfaceView = new WeakReference<GLSurfaceRenderView>(surfaceView);
        }

        public void addRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.put(callback, callback);
            ISurfaceHolder surfaceHolder = null;
            if (mSurfaceHolder != null) {
                if (surfaceHolder == null)
                    surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
                callback.onSurfaceCreated(surfaceHolder, mWidth, mHeight);
            }
            if (mIsFormatChanged) {
                if (surfaceHolder == null)
                    surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
                callback.onSurfaceChanged(surfaceHolder, mFormat, mWidth, mHeight);
            }
        }

        public void removeRenderCallback(@NonNull IRenderCallback callback) {
            mRenderCallbackMap.remove(callback);
        }

        /**
         * 设置surface创建类型，0 默认，1 MdVR模式
         *@author DuanChunLin
         *@time 2017/1/5 15:30
         */
        public void setSurfaceCreateMode(int mode){
            mSurfaceCreateMode = mode;
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            if(mSurfaceCreateMode == SURFACE_CREATE_MODE_DEFAULT){
                mSurfaceHolder = holder;
                mIsFormatChanged = false;
                mFormat = 0;
                mWidth = 0;
                mHeight = 0;
                ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
                for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                    renderCallback.onSurfaceCreated(surfaceHolder, 0, 0);
                }
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            mSurfaceHolder = null;
            mIsFormatChanged = false;
            mFormat = 0;
            mWidth = 0;
            mHeight = 0;
            mIsSurfaceCreate = false;

            ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
            for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                renderCallback.onSurfaceDestroyed(surfaceHolder);
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format,
                                   int width, int height) {
            if (mIsSurfaceCreate) {
                mSurfaceHolder = holder;
                mIsFormatChanged = true;
                mFormat = format;
                mWidth = width;
                mHeight = height;
                ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), null);
                for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                    renderCallback.onSurfaceChanged(surfaceHolder, format, width, height);
                }
            }
        }

        @Override
        public void onSurfaceReady(Surface surface) {
            if(mSurfaceCreateMode == SURFACE_CREATE_MODE_MD_VR){
                mIsFormatChanged = false;
                mFormat = 0;
                mWidth = 0;
                mHeight = 0;
                mIsSurfaceCreate = true;
                ISurfaceHolder surfaceHolder = new InternalSurfaceHolder(mWeakSurfaceView.get(), surface);
                for (IRenderCallback renderCallback : mRenderCallbackMap.keySet()) {
                    renderCallback.onSurfaceCreated(surfaceHolder, 0, 0);
                }
            }
        }
    }
}
