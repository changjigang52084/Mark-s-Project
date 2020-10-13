package com.xunixianshi.vrshow.classify;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.gson.Gson;
import com.hch.viewlib.util.MLog;
import com.hch.viewlib.util.SimpleSharedPreferences;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.customview.LoadingAnimationDialog;
import com.xunixianshi.vrshow.obj.MainClassifyHomeColumnListObj;
import com.xunixianshi.vrshow.obj.MainClassifyHomeColumnObj;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;


/**
 *  首页-分类管理页
 *@author HeChuang
 *@time 2016/11/1 15:20
 */
public class ClassifyFragment extends BaseFra implements View.OnClickListener {
    @Bind(R.id.classify_fragment_tab_sroll_sv)
    HorizontalScrollView classify_fragment_tab_sroll_sv;
    @Bind(R.id.classify_fragment_tab_manage_ll)
    LinearLayout classify_fragment_tab_manage_ll;
    @Bind(R.id.find_fragment_page_vp)
    ViewPager find_fragment_page_vp;
//    @Bind(R.id.2)
//    RelativeLayout page_error_rl;

    private ClassifyTabHomeFragment mClassifyTabHomeFragment;
    private ArrayList<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ArrayList<TextView> mTextViewList = new ArrayList<TextView>();
    /**
     * ViewPager的当前选中页
     */
    private int currentIndex = 0;
    private int mScreenWidth;
    private RelativeLayout layout;

    private int item_width;
    private int endPosition;
    private int beginPosition;
    private int currentFragmentIndex;
    private boolean isEnd;
    private int showItemNum = 1;//控制tab显示的个数
    private ArrayList<MainClassifyHomeColumnListObj> cilumnTabs;
    private LoadingAnimationDialog myProgressDialog;

    @Override
    protected void lazyLoad() {
    }

    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_classify_home_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        super.initData();
        cilumnTabs = new ArrayList<MainClassifyHomeColumnListObj>();
        myProgressDialog = new LoadingAnimationDialog(getActivity());
        getTabListData();
    }

    private void initViewPage() {
        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager());
        find_fragment_page_vp.setAdapter(fragmentPagerAdapter);
        fragmentPagerAdapter.setFragments(cilumnTabs);
        find_fragment_page_vp.setOnPageChangeListener(new MyOnPageChangeListener());
        find_fragment_page_vp.setCurrentItem(0);
    }

    /**
     * 获取tab列表数据
     *
     * @author hechuang
     * @time 2016/9/19 14:38
     */
    private void getTabListData() {
        String tabs = SimpleSharedPreferences.getString("tabs", getActivity());
        Gson gson = new Gson();
        MainClassifyHomeColumnObj aa = gson.fromJson(tabs, MainClassifyHomeColumnObj.class);
        if (aa != null && aa.getList() != null) {
            cilumnTabs.addAll(aa.getList());
            MLog.d("cilumnTabs:" + cilumnTabs.size());
            showItemNum = cilumnTabs.size() >= 4 ? 4 : cilumnTabs.size() + 1;
            addHomeTab();
            addTabs(cilumnTabs);
            initViewPage();
        } else {
            //为空只加载首页
            addHomeTab();
            initViewPage();
            //暂不更新tab
//            myProgressDialog.show();
//            getClassifyTab();
        }
    }


    private void addHomeTab() {
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        mScreenWidth = dm.widthPixels - 100;
        item_width = (int) ((mScreenWidth / showItemNum + 0.5f));
//        img1.getLayoutParams().width = item_width;
        /**************手动添加首页 start********************/
        layout = new RelativeLayout(getActivity());
        TextView view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.tab_text, null).findViewById(R.id.tab_text);
        view.setText("热点");
        view.setTextColor(Color.parseColor("#00B0C7"));
        mTextViewList.add(view);
        LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(view, params);
        classify_fragment_tab_manage_ll.addView(layout, (int) (mScreenWidth / showItemNum + 0.5f), LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setOnClickListener(this);
        layout.setTag(0);
//        mClassifyTabHomeFragment = new ClassifyTabHomeFragment();
//        mFragmentList.add(mClassifyTabHomeFragment);

        /**************手动添加首页 end********************/
    }
    /**
     * 添加头部标签
     *@author HeChuang
     *@time 2016/9/29 16:24
     */
    private void addTabs(ArrayList<MainClassifyHomeColumnListObj> tabs) {
        for (int i = 0; i < tabs.size(); i++) {
            TextView view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.tab_text, null).findViewById(R.id.tab_text);
            layout = new RelativeLayout(getActivity());
            view.setText(tabs.get(i).getColumnName());
            view.setTextColor(Color.WHITE);
            mTextViewList.add(view);
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.CENTER_IN_PARENT);
            layout.addView(view, params);
            classify_fragment_tab_manage_ll.addView(layout, (int) (mScreenWidth / showItemNum + 1 + 0.5f), LayoutParams.MATCH_PARENT);
            layout.setOnClickListener(this);
            layout.setTag(i + 1);
        }
    }

    @OnClick(R.id.classify_fragment_tab_search_ib)
    public void onClick() {
        startActivity(new Intent(getActivity(), VRshowSearchActivity.class));
    }

    private class MyFragmentPagerAdapter extends FragmentStatePagerAdapter {

        private ArrayList<MainClassifyHomeColumnListObj> mainClassifyHomeColumnList;
        private FragmentManager fm;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
            this.mainClassifyHomeColumnList = new ArrayList<>();
        }


        @Override
        public int getCount() {
            return mainClassifyHomeColumnList.size()+1;
        }

        @Override
        public Fragment getItem(int position) {
            Fragment fragment;
            if(position==0){
                fragment =  new ClassifyTabHomeFragment();
            }
            else{
                Bundle data = new Bundle();
                data.putInt("tagId", cilumnTabs.get(position-1).getColumnId());
                fragment = new ClassifyTabOtherFragment();
                fragment.setArguments(data);
            }
            return fragment;
        }


        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        public void setFragments(ArrayList<MainClassifyHomeColumnListObj> fragments) {
            this.mainClassifyHomeColumnList.clear();
            this.mainClassifyHomeColumnList.addAll(fragments);
            notifyDataSetChanged();
        }

        @Override
        public Object instantiateItem(ViewGroup container,  int position) {
            return super.instantiateItem(container, position);
        }

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(final int position) {
//            Animation animation = new TranslateAnimation(endPosition, position* item_width, 0, 0);

            beginPosition = position * item_width;
            mTextViewList.get(position).setTextColor(Color.parseColor("#00B0C7"));
            mTextViewList.get(currentFragmentIndex).setTextColor(Color.WHITE);
            currentFragmentIndex = position;
//            if (animation != null) {
//                animation.setFillAfter(true);
//                animation.setDuration(0);
//                img1.startAnimation(animation);
//            layout.getChildAt(position).setSelected(true);
            classify_fragment_tab_sroll_sv.smoothScrollTo((currentFragmentIndex - 1) * item_width, 0);
//            }
        }

        @Override
        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            if (!isEnd) {
                if (currentFragmentIndex == position) {
                    endPosition = item_width * currentFragmentIndex +
                            (int) (item_width * positionOffset);
                }
                if (currentFragmentIndex == position + 1) {
                    endPosition = item_width * currentFragmentIndex -
                            (int) (item_width * (1 - positionOffset));
                }

//                Animation mAnimation = new TranslateAnimation(beginPosition, endPosition, 0, 0);
//                mAnimation.setFillAfter(true);
//                mAnimation.setDuration(0);
//                img1.startAnimation(mAnimation);
                classify_fragment_tab_sroll_sv.invalidate();
                beginPosition = endPosition;
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            if (state == ViewPager.SCROLL_STATE_DRAGGING) {
                isEnd = false;
            } else if (state == ViewPager.SCROLL_STATE_SETTLING) {
                isEnd = true;
                beginPosition = currentFragmentIndex * item_width;
                if (find_fragment_page_vp.getCurrentItem() == currentFragmentIndex) {
                    // 未跳入下一个页面
//                    img1.clearAnimation();
//                    Animation animation = null;
//                    // 恢复位置
//                    animation = new TranslateAnimation(endPosition, currentFragmentIndex * item_width, 0, 0);
//                    animation.setFillAfter(true);
//                    animation.setDuration(1);
//                    img1.startAnimation(animation);
                    classify_fragment_tab_sroll_sv.invalidate();
                    endPosition = currentFragmentIndex * item_width;
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        find_fragment_page_vp.setCurrentItem((Integer) v.getTag());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }


}
