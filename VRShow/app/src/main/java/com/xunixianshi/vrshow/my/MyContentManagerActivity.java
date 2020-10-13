package com.xunixianshi.vrshow.my;

import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.hch.viewlib.util.MLog;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.adpater.MyAdapter;
import com.xunixianshi.vrshow.my.fragment.AlreadyReleasedFragment;
import com.xunixianshi.vrshow.my.fragment.AuditFailedFragment;
import com.xunixianshi.vrshow.my.fragment.AuditFragment;
import com.xunixianshi.vrshow.my.fragment.CommittedFragment;
import com.xunixianshi.vrshow.my.fragment.CreateVideoContentActivity;
import com.xunixianshi.vrshow.obj.eventBus.uploadNoticeEvent;
import com.xunixianshi.vrshow.recyclerview.VideoExpandableActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * TODO 我的内容页面
 *
 * @author MarkChang
 * @ClassName MyContentManagerActivity
 * @time 2016/11/1 15:49
 */

public class MyContentManagerActivity extends VideoExpandableActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    @Bind(R.id.my_content_back_rl)
    RelativeLayout my_content_back_rl;
    @Bind(R.id.my_content_tv)
    TextView my_content_tv;
    @Bind(R.id.my_content_create_content_iv)
    TextView my_content_create_content_iv;

    @Bind(R.id.content_manager_already_released_tv) //已发布文字控件
            TextView content_manager_already_released_tv;
    @Bind(R.id.content_manager_already_released_ll) //已发布页面布局控件
            LinearLayout content_manager_already_released_ll;

    @Bind(R.id.content_manager_committed_tv) // 提交中文字控件
            TextView content_manager_committed_tv;
    @Bind(R.id.content_manager_committed_ll) // 提交中页面布局控件
            LinearLayout content_manager_committed_ll;

    @Bind(R.id.content_manager_audit_tv) //审核中文字控件
            TextView content_manager_audit_tv;
    @Bind(R.id.content_manager_audit_ll) //审核中页面布局控件
            LinearLayout content_manager_audit_ll;

    @Bind(R.id.content_manager_audit_failed_tv) //审核失败文字控件
            TextView content_manager_audit_failed_tv;
    @Bind(R.id.content_manager_audit_failed_ll) //审核失败页面布局控件
            LinearLayout content_manager_audit_failed_ll;

    @Bind(R.id.content_manage_viewpager) //ViewPager对象
            ViewPager content_manage_viewpager;

    //已发布页面常量
    private final int ALREADY_RELEASED = 0;
    // 提交中页面常量
    private final int COMMITTED = 1;
    //审核中页面常量
    private final int AUDIT = 2;
    //审核失败页面常量
    private final int AUDIT_FAILED = 3;


    // 已发布Fragment
    private Fragment alreadyReleasedFragment;
    // 提交中Fragment
    private Fragment committedFragment;
    // 审核中Fragment
    private Fragment auditFragment;
    // 审核失败Fragment
    private Fragment auditFailedFragment;

    //适配器数据集
    private List<Fragment> mFragments;
    //适配器对象
    private MyAdapter mAdapter;
    //上一个被选中的底部控件
    private int mPreviousItem;
    //当前被选中的底部控件
    private int mCurrentItem;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_my_content_management);
        ButterKnife.bind(this);
        initData();
        event();
        showViewPagerData(mFragments);
    }

    @Override
    protected void initData() {
        super.initData();
        content_manager_already_released_ll.performClick();
        my_content_tv.setText("我的内容");
        mFragments = new ArrayList<>();
        if (alreadyReleasedFragment == null) {
            alreadyReleasedFragment = new AlreadyReleasedFragment();
            mFragments.add(alreadyReleasedFragment);
        }
        if (committedFragment == null) {
            committedFragment = new CommittedFragment();
            mFragments.add(committedFragment);
        }
        if (auditFragment == null) {
            auditFragment = new AuditFragment();
            mFragments.add(auditFragment);
        }
        if (auditFailedFragment == null) {
            auditFailedFragment = new AuditFailedFragment();
            mFragments.add(auditFailedFragment);
        }
    }

    private void showViewPagerData(List<Fragment> fragments) {
        if (mAdapter == null) {
            //设置对应的适配器
            mAdapter = new MyAdapter(getSupportFragmentManager(), fragments);
            content_manage_viewpager.setAdapter(mAdapter);
        } else {
            //这里是更新数据时，刷新ViewPager显示的数据
            mFragments.addAll(fragments);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * 初始化事件
     */
    private void event() {
        content_manage_viewpager.addOnPageChangeListener(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void uploadNoticeEventBus(uploadNoticeEvent str) {
        //通知显示提交中页面
        setCurrentItem(1);
    }

    private void setCurrentItem(int current) {
        com.hch.utils.MLog.d("current:"+current);
        //每次先隐藏上一个控件
        hidePreviousColor(mPreviousItem);
        switch (current) {
            case ALREADY_RELEASED:
                //设置对应的
                content_manager_already_released_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_00b0c7));
                break;
            case COMMITTED:
                content_manager_committed_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_00b0c7));
                break;
            case AUDIT:
                content_manager_audit_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_00b0c7));
                break;
            case AUDIT_FAILED:
                content_manager_audit_failed_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_00b0c7));
                break;
        }
        content_manage_viewpager.setCurrentItem(current);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        //记录当前的位置
        mCurrentItem = position;
        //隐藏上一次的位置
        hidePreviousColor(mPreviousItem);
        //设置上一个控件的位置
        mPreviousItem = mCurrentItem;
        //设置当前需要选择的控件
        setCurrentItem(mCurrentItem);
    }

    /**
     * 隐藏上一个被选中的控件的橙色颜色，设置为未选中的灰色颜色
     *
     * @param previousItem
     */
    private void hidePreviousColor(int previousItem) {
        MLog.d("IT_Real", "hidePreviousColor: current = " + mCurrentItem + "previous = " + previousItem);
        switch (previousItem) {
            case ALREADY_RELEASED:
                content_manager_already_released_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_e0000000));
                break;
            case COMMITTED:
                content_manager_committed_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_e0000000));
                break;
            case AUDIT:
                content_manager_audit_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_e0000000));
                break;
            case AUDIT_FAILED:
                content_manager_audit_failed_tv.setTextColor(ContextCompat.getColor(MyContentManagerActivity.this, R.color.color_e0000000));
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @OnClick({R.id.my_content_back_rl, R.id.my_content_create_content_iv, R.id.content_manager_committed_ll,
            R.id.content_manager_already_released_ll, R.id.content_manager_audit_ll, R.id.content_manager_audit_failed_ll})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_content_back_rl:
                MyContentManagerActivity.this.finish();
                break;
            case R.id.my_content_create_content_iv:
                startActivity(new Intent(MyContentManagerActivity.this, CreateVideoContentActivity.class));
                break;
            case R.id.content_manager_already_released_ll:
                //当前控件项减去上一次的控件项，表示的就是他们之间的一个偏移量
                //例如上一次控件选中的是第3个item，当前要选择的是第0个，则相减
                //之后的偏移量为FL - mPreviousItem = 0 - 3 = -3
                int disItem = ALREADY_RELEASED - mPreviousItem;
                //然后设置当前被选择的是第几项，mViewPager.getCurrentItem()获取的是当前
                // 选中的为3，即mViewPager.getCurrentItem() + disItem = 3 + (-3) = 0
                //拿到当前需要被选择的位置后，设置一下即可
                mCurrentItem = content_manage_viewpager.getCurrentItem() + disItem;
                break;
            case R.id.content_manager_committed_ll:
                disItem = COMMITTED - mPreviousItem;
                mCurrentItem = content_manage_viewpager.getCurrentItem() + disItem;
                break;
            case R.id.content_manager_audit_ll:
                disItem = AUDIT - mPreviousItem;
                mCurrentItem = content_manage_viewpager.getCurrentItem() + disItem;
                break;
            case R.id.content_manager_audit_failed_ll:
                disItem = AUDIT_FAILED - mPreviousItem;
                mCurrentItem = content_manage_viewpager.getCurrentItem() + disItem;
                break;
        }
//        //先隐藏上一个被选中的控件的为未选中状态，就是改变上一个控件显示的颜色
//        hidePreviousColor(mPreviousItem);
        //设置当前被选中的控件为选中状态，改变颜色为橙色
        setCurrentItem(mCurrentItem);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
