package com.sunchip.adw.cloudphotoframe.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class BitmapUtils {
    public static BitmapUtils mBitmapUtils = new BitmapUtils();

    public BitmapUtils() {
    }

    public static BitmapUtils getInstance() {
        if (mBitmapUtils == null) {
            mBitmapUtils = new BitmapUtils();
        }
        return mBitmapUtils;
    }


    /**
     * 将本地图片转成Bitmap
     *
     * @param path 已有图片的路径
     * @return
     */
    public  Bitmap openImage(String path) {
        Bitmap bitmap = null;
        try {
            BufferedInputStream bis = new BufferedInputStream(new FileInputStream(path));
            bitmap = BitmapFactory.decodeStream(bis);
            bis.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }


    /**
     * 将本地图片转化为缩略图加载，以防止加载 的 OOM
     */
    public  Bitmap thumbleImages(String path){

        File file = new File(path);
        FileInputStream stream = null;
        try {
            stream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeStream(stream , null, opts);
        return bitmap;
    }


    public void otherBitmapWay(String path){
        //该方法直接传文件路径的字符串，即可将指定路径的图片读取到Bitmap对象。
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        //该方法可从资源文件中读取图片信息。第一个参数一般传getResources(),第二个参数传drawable图片的资源id，如下
//        Bitmap bitmap1 = BitmapFactory.decodeResource(getResources(),R.mipmap.aaa);
    }

//    //给TextView设置   参考函数如下
//    // public void  setCompoundDrawables  (Drawable left, Drawable top, Drawable right, Drawable bottom);

    public void setDrable(TextView textView,int drawable,Context context,int Count){
        Drawable mDrawable = context.getResources().getDrawable(drawable);
        mDrawable.setBounds(0, 0, mDrawable.getMinimumWidth(), mDrawable.getMinimumHeight());
        if (Count == 1) {
            textView.setCompoundDrawables(mDrawable, null, null, null);
        } else if (Count == 2) {
            textView.setCompoundDrawables(null, mDrawable, null, null);
        } else if (Count == 3) {
            textView.setCompoundDrawables(null, null, mDrawable, null);
        } else if (Count == 4) {
            textView.setCompoundDrawables(null, null, null, mDrawable);
        }
    }

}
