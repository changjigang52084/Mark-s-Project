package com.xunixianshi.vrshow.actmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.baidu.mobstat.SendStrategyEnum;
import com.baidu.mobstat.StatService;
import com.hch.utils.MLog;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.obj.eventBus.uploadNoticeEvent;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

/**
 * base类  可以写入一些公共方法
 */
@SuppressLint("NewApi")
public abstract class BaseAct extends BaseActivity {
    public boolean isDestroyed;

    public String httpTagStr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StatService.setSessionTimeOut(30);// 默认为30秒
        StatService.setOn(this, StatService.EXCEPTION_LOG);
        StatService.setLogSenderDelayed(0);
        StatService.setForTv(this, false);
        StatService.setSendLogStrategy(this, SendStrategyEnum.SET_TIME_INTERVAL, 1, false);
        StatService.setDebugOn(false);
        StatService.start(this);
        StatService.setAppKey("KhtmRwsCa1Ikb0YySKHoQbLkeXmfEuzC");
        StatService.setAppChannel(this, "", false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(!EventBus.getDefault().isRegistered(this)){//加上判断
            EventBus.getDefault().register(this);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(uploadNoticeEvent event) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        MLog.d("~~~~~~~~~~~~~~~~onResume");
        StatService.onResume(this);
        StatService.onPageStart(this, "BaseAct");
        isDestroyed = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        MLog.d("~~~~~~~~~~~~~~~~onPause");
        StatService.onPause(this);
        StatService.onPageEnd(this, "BaseAct");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MLog.d("~~~~~~~~~~~~~~~~onDestroy");
        isDestroyed = true;
        EventBus.getDefault().unregister(this);
        cancelHttpTag();
    }

    public boolean isDestroy() {
        return isDestroyed;
    }

    @Override
    public void startActivity(Intent intent) {
        super.startActivity(intent);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(intent, requestCode);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);
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
            toast = Toast.makeText(BaseAct.this, msg, Toast.LENGTH_SHORT);  //正常执行
        } else {
            toast.setText(msg);  //用于覆盖前面未消失的提示信息
        }
        toast.show();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }
        return super.onTouchEvent(event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
    }

    /**
     * 关闭activity
     */
    public void isFinish() {
        this.finish();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
    }

    /**
     * 请求权限
     * <p>
     * 如果权限被拒绝过，则提示用户需要权限
     */
    protected void requestPermission(final String permission, String rationale, final int requestCode) {
        if (shouldShowRequestPermissionRationale(permission)) {
//            showAlertDialog(getString(R.string.permission_title_rationale), rationale,
//                    new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialog, int which) {
//                            requestPermissions(new String[]{permission}, requestCode);
//                        }
//                    }, getString(R.string.label_ok), null, getString(R.string.label_cancel));
            MLog.d("权限被拒！");
        } else {
            requestPermissions(new String[]{permission}, requestCode);
        }
    }

    /**
     * 设置当前页面请求的action
     * @ClassName BaseAct
     * @author HeChuang
     * @time 2016/12/27 16:10
     */
    public void setCancelHttpTag(String httpTagStr) {
        this.httpTagStr = httpTagStr;
    }

    /**
     * 取消指定action的http请求
     * @ClassName BaseAct
     * @author HeChuang
     * @time 2016/12/27 16:10
     */
    private void cancelHttpTag() {
        if(!StringUtil.isEmpty(httpTagStr)){
            OkHttpAPI.cancelTag(httpTagStr);
            httpTagStr = "";
        }
    }
}