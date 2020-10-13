package com.unccr.zclh.dsdps.service.second;

import android.app.Presentation;
import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.SurfaceView;
import android.widget.ImageView;

import com.unccr.zclh.dsdps.R;


/**
 *
 * @version 1.0
 * @data 创建时间：2019年10月14日 上午9:35:13
 * @parameter SecondScreenPresentation
 */
public class SecondScreenPresentation extends Presentation {

    private SurfaceView presentSV;
    private ImageView presentIV;

    public SecondScreenPresentation(Context outerContext, Display display) {
        super(outerContext, display);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.presentation_second_screen);
        presentSV = findViewById(R.id.surfaceView);
        presentIV = findViewById(R.id.imageView);
    }

    public SurfaceView getPresentSV() {
        return presentSV;
    }

    public ImageView getPresentIV() {
        return presentIV;
    }
}
