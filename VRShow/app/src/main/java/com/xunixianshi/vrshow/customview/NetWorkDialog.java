package com.xunixianshi.vrshow.customview;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.View;

import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.interfaces.NetWorkDialogButtonListener;

/**
 * Created by xnxs on 2016/5/14.
 */
public class NetWorkDialog  extends HintDialog {
    private NetWorkDialogButtonListener mNetWorkDialogButtonInterface;
    private boolean isCheckOkBtn = false;

    public NetWorkDialog(Context context) {
        super(context);
    }
    public void showNetWorkChangePlayerVideoWarning(NetWorkDialogButtonListener netWorkDialogButtonInterface){
        this.cancel();
        this.mNetWorkDialogButtonInterface = netWorkDialogButtonInterface;
        setContextText("检测到您wifi没有打开，是否使用数据网络进行观看视频");
        setOkButText("继续");
        setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNetWorkDialogButtonInterface!=null){
                    mNetWorkDialogButtonInterface.okClick();
                }
                NetWorkDialog.this.dismiss();
            }
        });
        setCancelText("取消");
        setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNetWorkDialogButtonInterface!=null){
                    mNetWorkDialogButtonInterface.cancelClick();
                }
                NetWorkDialog.this.dismiss();
            }
        });
        show();
    }

    public void showNetWorkChangeDownLoadWarning(NetWorkDialogButtonListener netWorkDialogButtonInterface){
        this.cancel();
        this.mNetWorkDialogButtonInterface = netWorkDialogButtonInterface;
        setContextText("当前不是WIFI网络，继续下载可能会产生额外流量费用，是否继续下载？");
        setOkButText("继续");
        setOkClickListaner(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNetWorkDialogButtonInterface!=null){
                    mNetWorkDialogButtonInterface.okClick();
                }
                isCheckOkBtn = true;
                NetWorkDialog.this.dismiss();
            }
        });
        setCancelText("取消");
        setCancelClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mNetWorkDialogButtonInterface!=null){
                    mNetWorkDialogButtonInterface.cancelClick();
                }
                NetWorkDialog.this.dismiss();
            }
        });
        show();
    }

    public boolean isCheckOkBtn() {
        return isCheckOkBtn;
    }

    public static boolean getIsShowNetWorkDialog(Context context){
        ConnectivityManager connectivityManager=(ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo =connectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE &&  !SimpleSharedPreferences.getBoolean("isWiFi",context)) {
            return true;
        }
        return false;
    }
}
