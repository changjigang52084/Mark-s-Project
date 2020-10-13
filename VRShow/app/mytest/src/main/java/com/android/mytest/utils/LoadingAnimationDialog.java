package com.android.mytest.utils;

import android.app.Dialog;
import android.content.Context;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.android.mytest.R;

public class LoadingAnimationDialog extends Dialog {
    Animation operatingAnim;
    private ImageView loading_animation_icon_iv;
    public LoadingAnimationDialog(Context context) {
        super(context, R.style.custom_loading_dialog);
        setCanceledOnTouchOutside(false);
        setContentView(R.layout.dialog_loading_animation);
        Window dialogWindow = getWindow();
        WindowManager.LayoutParams lp = dialogWindow.getAttributes();
        dialogWindow.setGravity(Gravity.CENTER);
        lp.width = 300; // 宽度
        lp.height = 300; // 高度
        lp.alpha = 0.7f; // 透明度
        dialogWindow.setAttributes(lp);
        loading_animation_icon_iv = (ImageView)findViewById(R.id.loading_animation_icon_iv);
        operatingAnim = AnimationUtils.loadAnimation(context,R.anim.loading_animation);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        loading_animation_icon_iv.startAnimation(operatingAnim);
    }

    @Override
    public void show() {
        loading_animation_icon_iv.startAnimation(operatingAnim);
        if (!this.isShowing()) {
            super.show();
        }
    }

    @Override
    public void dismiss() {
        loading_animation_icon_iv.clearAnimation();
        if (this.isShowing()) {
            super.dismiss();
        }
    }
}
