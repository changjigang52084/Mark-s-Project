package com.lzkj.launcher.impl;

/**
 * 登录状态接口
 *
 * @author changkai
 */
public interface LoginCallback {
    void onSuccess(String msg);

    void onFail(String errMsg);
}
