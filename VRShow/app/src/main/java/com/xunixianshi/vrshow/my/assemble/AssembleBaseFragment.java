package com.xunixianshi.vrshow.my.assemble;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;

import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;

import java.io.IOException;

/**
 * Created by duan on 2016/9/27.
 */

public abstract class AssembleBaseFragment  extends BaseFra {

    public static final int REQUEST_CODE = 0x101; //返回标签
    public static final int RESULT_CODE = 0x101; //返回成功结果id

    @Override
    protected void lazyLoad() {
    }

    public void exitBack() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.activity_right_in, R.anim.activity_right_out)
                .remove(this);
        fragmentTransaction.commit();
    }

    public void backResult(Intent intent){
        if( getTargetFragment() == null){
            return;
        }
        getTargetFragment().onActivityResult(REQUEST_CODE, RESULT_CODE, intent);
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.setCustomAnimations(R.anim.activity_right_in, R.anim.activity_right_out)
                .remove(this);
        fragmentTransaction.commit();
    }

    public Bitmap getBitmapFromUri(Uri uri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            return bitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
