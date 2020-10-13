package com.xunixianshi.vrshow.fine;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.google.vr.sdk.widgets.pano.VrPanoramaView;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.zhy.http.okhttp.utils.PicassoUtil;

/**
 * Created by duan on 2016/9/28.
 */

public class FineVrPanoramaView extends VrPanoramaView {
    VrPanoramaView.Options panoramaOptions = null;
    public FineVrPanoramaView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FineVrPanoramaView(Context context) {
        super(context);
    }

    public void loadImage(Context context,String url) {
        if(panoramaOptions==null){
            panoramaOptions= new VrPanoramaView.Options();
            panoramaOptions.inputType = Options.TYPE_MONO;
        }
        FineVrPanoramaView.this.setVisibility(GONE);
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                FineVrPanoramaView.this.loadImageFromBitmap(bitmap,panoramaOptions);
                FineVrPanoramaView.this.setVisibility(VISIBLE);
            }
            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }
            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(context).load(PicassoUtil.utf8Togb2312(url)).into(target);
    }

    public void loadBitmap(Bitmap bitmap){
        FineVrPanoramaView.this.loadImageFromBitmap(bitmap,panoramaOptions);
    }
}
