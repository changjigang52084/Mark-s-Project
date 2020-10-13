package com.xunixianshi.vrshow.actmanager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.hch.viewlib.util.MLog;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;

/**
 * User: hch
 * Date: 2016-03-03
 * Time: 17:41
 * FIXME
 */
public abstract class BaseFra extends BaseFragment {
    protected boolean isVisible;
    private boolean isDestroy;
    public String httpTagStr;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MLog.state(this.getClass().getName(), "---------onCreateView ");
        StatService.setSessionTimeOut(30);// 默认为30秒
        StatService.setOn(getActivity(), StatService.EXCEPTION_LOG);
        StatService.setLogSenderDelayed(0);
        StatService.setForTv(getActivity(), false);
        StatService.setSendLogStrategy(getActivity(), SendStrategyEnum.SET_TIME_INTERVAL, 1, false);
        StatService.setDebugOn(false);
        StatService.start(getActivity());
        StatService.setAppKey("KhtmRwsCa1Ikb0YySKHoQbLkeXmfEuzC");
        StatService.setAppChannel(getActivity(), "", false);
    }

    /**
     * 用于查看fragment是否可见
     *
     * @author hechuang
     * @time 2016/9/18 15:36
     */
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isVisible = true;
            onVisible();
        } else {
            isVisible = false;
            onInvisible();
        }
    }

    /**
     * fragment可见
     *
     * @author hechuang
     * @time 2016/9/18 15:29
     */
    protected void onVisible() {
        lazyLoad();
    }

    /**
     * 懒加载方法
     *
     * @author hechuang
     * @time 2016/9/18 15:30
     */
    protected abstract void lazyLoad();

    /**
     * fragment不可见
     *
     * @author hechuang
     * @time 2016/9/18 15:29
     */
    protected void onInvisible() {
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        EventBus.getDefault().register(this);
//    }
//
//    @Subscribe(threadMode = ThreadMode.MAIN)
//    public void onMessageEvent(uploadNoticeEvent event) {
//
//    }

    @Override
    public void onResume() {
        MLog.state(this.getClass().getName(), "---------onResume ");
        super.onResume();
        StatService.onResume(getActivity());
        StatService.onPageStart(getActivity(), "BaseFra");
        isDestroy = false;
    }

    @Override
    public void onPause() {
        MLog.state(this.getClass().getName(), "---------onPause ");
        super.onPause();
        StatService.onResume(getActivity());
        StatService.onPageEnd(getActivity(), "BaseFra");
    }

    @Override
    public void onStop() {
        MLog.state(this.getClass().getName(), "---------onStop ");
//        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        MLog.state(this.getClass().getName(), "---------onDestroy ");
        isDestroy = true;
        cancelHttpTag();
    }

    public boolean isDestroy() {
        return isDestroy;
    }

    @Override
    public void onDestroyView() {
        MLog.state(this.getClass().getName(), "---------onDestroyView ");
        super.onDestroyView();
    }

    /**
     * show to @param(cls)，but can't finish activity
     */
    public void showActivity(Context aty, Class<?> cls) {
        Intent intent = new Intent();
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }

    /**
     * show to @param(cls)，but can't finish activity
     */
    public void showActivity(Context aty, Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(aty, cls);
        aty.startActivity(intent);
    }

    /**
     * @param @param message    设定文件
     * @return void    返回类型
     * @Title: showToast
     * @Description: TODO 消息提示
     * @author hechuang
     * @date 2015-11-12
     */
    private Toast toast = null;  //用于判断是否已有Toast执行

    public void showToastMsg(String msg) {
        if (toast == null) {
            toast = Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT);  //正常执行
        } else {
            toast.setText(msg);  //用于覆盖前面未消失的提示信息
        }
        toast.show();
    }

    /**
     * 设置当前页面请求的action
     *
     * @ClassName BaseAct
     * @author HeChuang
     * @time 2016/12/27 16:10
     */
    public void setCancelHttpTag(String httpTagStr) {
        this.httpTagStr = httpTagStr;
    }

    /**
     * 取消指定action的http请求
     *
     * @ClassName BaseAct
     * @author HeChuang
     * @time 2016/12/27 16:10
     */
    private void cancelHttpTag() {
        if (!StringUtil.isEmpty(httpTagStr)) {
            OkHttpAPI.cancelTag(httpTagStr);
            httpTagStr = "";
        }
    }

}
