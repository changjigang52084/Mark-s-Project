package com.airiche.newworktest;

import android.app.Activity;
import android.util.Log;

import com.hjq.permissions.OnPermission;
import com.hjq.permissions.XXPermissions;

import java.util.List;

/**
 * Created by yingmuliang on 2020/1/10.
 */
public class PermissionUtils {

    private String TAG = "PermissionUtils";

    public static PermissionUtils mPermissionUtils = new PermissionUtils();

    public PermissionUtils() {
    }

    public static PermissionUtils getInstance() {
        if (mPermissionUtils == null) {
            mPermissionUtils = new PermissionUtils();
        }
        return mPermissionUtils;
    }

    public void Permission(Activity activity){
        XXPermissions.with(activity)
                // 可设置被拒绝后继续申请，直到用户授权或者永久拒绝
                .constantRequest()
                .request(new OnPermission() {
            @Override
            public void hasPermission(List<String> granted, boolean isAll) {
                Log.e(TAG, "hasPermission=" + granted.size() + "       " + isAll);
            }

            @Override
            public void noPermission(List<String> denied, boolean quick) {
                Log.e(TAG, "noPermission=" + denied.size() + "       " + quick);
            }
        });
    }
}
