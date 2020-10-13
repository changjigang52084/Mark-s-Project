package com.xnxs.mediaplayer.widget.media;

import android.app.Activity;
import android.view.Surface;

import com.asha.vrlib.MDVRLibrary;

/**
 * Created by Lin on 2017/1/4.
 */

public class VRControl implements IMediaPlayerVRControl {

    private GLSurfaceRenderView mRenderView;
    private MDVRLibrary mMDVRLibrary;
    private Activity mActivity;

    public VRControl(GLSurfaceRenderView glSurfaceRenderView){
        mRenderView = glSurfaceRenderView;
    }

    @Override
    public boolean bindVRLibrary(Activity activity) {
        mActivity = activity;
        mRenderView.setSurfaceCreateMode(GLSurfaceRenderView.SURFACE_CREATE_MODE_MD_VR);
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onDestroy();
        }
        mActivity = activity;
        mMDVRLibrary = MDVRLibrary.with(activity)
                .displayMode(MDVRLibrary.DISPLAY_MODE_NORMAL)
                .interactiveMode(MDVRLibrary.INTERACTIVE_MODE_MOTION)
                .asVideo(mRenderView.getSurfaceCallback())
                .build(mRenderView);

        mRenderView.setIOnSurfaceTextureResize(new IOnSurfaceTextureResize() {
            @Override
            public void onTextureResize(float width, float height) {
                if(mMDVRLibrary!=null){
                    mMDVRLibrary.onTextureResize(width,height);
                }
            }
        });
        return true;
    }


    @Override
    public boolean isBindVRLibrary() {
        return mMDVRLibrary != null;
    }

    @Override
    public MDVRLibrary getMDVRLibrary() {
        return mMDVRLibrary;
    }

    @Override
     public void onPause() {
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onPause(mActivity);
        }
    }
    @Override
    public void onResume() {
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onResume(mActivity);
        }
    }
    @Override
    public void onDestroy() {
        if (mMDVRLibrary != null) {
            mMDVRLibrary.onDestroy();
        }
        mActivity = null;
        mMDVRLibrary = null;
    }

    public interface IOnSurfaceTextureResize {
        void onTextureResize(float width, float height);
    }
}
