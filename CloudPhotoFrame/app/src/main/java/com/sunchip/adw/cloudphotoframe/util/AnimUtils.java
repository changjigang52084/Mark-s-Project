package com.sunchip.adw.cloudphotoframe.util;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.View;

public class AnimUtils {

    /**
     * 透明动画
     *
     * @param v
     */
    public static void setAlphaAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "alpha", 0, 1);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * z轴旋转动画
     *
     * @param v
     */
    public static void setRotationZAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotation", 0, 180, 360);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * x轴旋转动画
     *
     * @param v
     */
    public static void setRotationXAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationX", 0, 180, 360);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * y轴旋转动画
     *
     * @param v
     */
    public static void setRotationYAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "rotationY", 0, 180, 360);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * translationX
     *
     * @param v
     */
    public static void setTranslationXAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationX", 0, 200, -200, 0);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * translationY
     *
     * @param v
     */
    public static void setTranslationYAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "translationY", 0, 200, -200, 0);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * scaleX
     *
     * @param v
     */
    public static void setScaleXAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "scaleX", 0, 1);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * scaleY
     *
     * @param v
     */
    public static void setScaleYAnimations(View v) {
        ObjectAnimator animator = ObjectAnimator.ofFloat(v, "scaleY", 0, 1);
        animator.setDuration(3000);
        animator.start();
    }

    /**
     * 混合动画
     *
     * @param v
     */
    public static void setFixAnimations(View v) {

        PropertyValuesHolder valuesHolder1 = PropertyValuesHolder.ofFloat("scaleX", 0, 1);
        PropertyValuesHolder valuesHolder2 = PropertyValuesHolder.ofFloat("scaleY", 0, 1);
        PropertyValuesHolder valuesHolder3 = PropertyValuesHolder.ofFloat("alpha", 0, 1);

        ObjectAnimator animator = ObjectAnimator.ofPropertyValuesHolder(v, valuesHolder1, valuesHolder2, valuesHolder3);
        animator.setDuration(3000);
        animator.start();
    }

    public static void setAnimations(View view, String animationType) {
        if (animationType.equals("AlphaAnimation")) {
            setAlphaAnimations(view);
        } else if (animationType.equals("FixAnimation")) {
            setFixAnimations(view);
        } else if (animationType.equals("RotationZAnimation")) {
            setRotationZAnimations(view);
        } else if (animationType.equals("RotationXAnimation")) {
            setRotationXAnimations(view);
        } else if (animationType.equals("RotationYAnimation")) {
            setRotationYAnimations(view);
        } else if (animationType.equals("TranslationXAnimation")) {
            setTranslationXAnimations(view);
        } else if (animationType.equals("TranslationYAnimation")) {
            setTranslationYAnimations(view);
        } else if (animationType.equals("ScaleXAnimation")) {
            setScaleXAnimations(view);
        } else if (animationType.equals("ScaleYAnimation")) {
            setScaleYAnimations(view);
        }
    }

}
