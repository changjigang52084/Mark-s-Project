package com.zhy.http.okhttp.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.hch.utils.MLog;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.zhy.http.okhttp.R;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Created by Administrator on 2016/10/18.
 */

public class PicassoUtil {
    private static String newUrl = "-1";
    public static void loadImage(Context context, String imageUrl, ImageView thisView){
        if(imageUrl != null && imageUrl.trim().length()!=0){
            newUrl = utf8Togb2312(imageUrl);
        }
        Picasso.with(context).load(newUrl)
                .placeholder(R.drawable.image_defult_icon_small)
                .error(R.drawable.image_defult_icon_small)
                .into(thisView);
    }
    public static void loadImage(Context context, File imageUrl, ImageView thisView){
        Picasso.with(context).load(imageUrl)
                .placeholder(R.drawable.image_defult_icon_small)
                .error(R.drawable.image_defult_icon_small)
                .into(thisView);
    }

    public static void loadImage(Context context, String imageUrl, ImageView thisView, Callback callback){
        if(imageUrl != null && imageUrl.trim().length()!=0){
            newUrl = utf8Togb2312(imageUrl);
        }
        Picasso.with(context).load(newUrl)
                .placeholder(R.drawable.image_defult_icon_small)
                .error(R.drawable.image_defult_icon_small)
                .into(thisView,callback);
    }

    public static void loadImage(Context context, String imageUrl, ImageView thisView,int defultResId) {
        if(imageUrl != null && imageUrl.trim().length()!=0){
            newUrl = utf8Togb2312(imageUrl);
        }
        Picasso.with(context).load(newUrl)
                .fit()
                .placeholder(defultResId)
                .error(defultResId)
                .into(thisView);
    }

    public static void loadImage(Context context, String imageUrl, ImageView thisView, int width, int height) {
        if(imageUrl != null && imageUrl.trim().length()!=0){
            newUrl = utf8Togb2312(imageUrl);
        }
        Picasso.with(context).load(newUrl)
                .resize(width, height)
                .centerCrop()
                .placeholder(R.drawable.image_defult_icon_small)
                .error(R.drawable.image_defult_icon_small)
                .into(thisView);
    }
    public static void loadBannerImage(Context context, String imageUrl, ImageView thisView,int errorResId){
        if(imageUrl != null && imageUrl.trim().length()!=0){
            newUrl = utf8Togb2312(imageUrl);
        }
        Picasso.with(context).load(newUrl)
                .error(errorResId)
                .into(thisView);
    }

    public  static String utf8Togb2312(String str){
        String data="";
//        try {
//            for(int i=0;i<str.length();i++){
//                char c=str.charAt(i);
//                if(String.valueOf(c).getBytes().length>1&&c!=':'&&c!='/'&&c!='?'){
//                    data = data+URLEncoder.encode(String.valueOf(c),"utf-8");
//                }else if(c==' '){
//                    data = data+"%20";
//                }else{
//                    data=data+String.valueOf(c);
//                }
//            }
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }finally{
//        }
        try {
            str = URLDecoder.decode(str,"utf-8");
            data = URLEncoder.encode(str,"utf-8").replaceAll("\\+", "%20");
            data = data.replaceAll("%3A", ":").replaceAll("%2F", "/").replaceAll("%3F","?");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return  data;
    }

}
