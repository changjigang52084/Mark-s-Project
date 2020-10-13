package com.xunixianshi.vrshow.show;

import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.ViewPager;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseFra;
import com.xunixianshi.vrshow.find.FindFragment;
import com.xunixianshi.vrshow.find.FindTabFragment;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;

import static androidx.viewpager.widget.PagerAdapter.POSITION_NONE;

/**
 * 首页-show模块管理
 * @ClassName ShowFragment
 *@author HeChuang
 *@time 2016/11/1 15:45
 */
public class ShowFragment extends BaseFra implements View.OnClickListener {

    @Bind(R.id.show_fragment_tab_manage_ll)
    LinearLayout show_fragment_tab_manage_ll;
    @Bind(R.id.show_fragment_tab_sroll_sv)
    HorizontalScrollView show_fragment_tab_sroll_sv;
    @Bind(R.id.show_page_vp)
    ViewPager show_page_vp;

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
    private int showItemNum = 2;//控制tab显示的个数
    private DisplayMetrics dm;

    @Override
    protected void lazyLoad() {
    }
    @Override
    protected View inflaterView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
        View rootView = inflater.inflate(R.layout.fragment_show_fragment, container, false);
        ButterKnife.bind(this, rootView);
        return rootView;
    }

    @Override
    protected void initData() {
        super.initData();
        dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getTabListData();
        initViewPage();
    }

    private void initViewPage() {
//        for (int i = 0; i < 10; i++) {
//            Bundle data = new Bundle();
//            data.putInt("tagId", 15);
//            ShowTabFragment fragment = new ShowTabFragment();
//            fragment.setArguments(data);
//            mFragmentList.add(fragment);
//        }


        //***************我爱秀
        Bundle data = new Bundle();
        data.putInt("tagId", 2); // 2我爱秀
        ShowTabFragment fragment = new ShowTabFragment();
        fragment.setArguments(data);
        mFragmentList.add(fragment);

        //****************大咖秀
        Bundle data1 = new Bundle();
        data1.putInt("tagId", 1); //1大咖修
        ShowTabFragment fragment1 = new ShowTabFragment();
        fragment1.setArguments(data1);
        mFragmentList.add(fragment1);

        MyFragmentPagerAdapter fragmentPagerAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), mFragmentList);
        show_page_vp.setAdapter(fragmentPagerAdapter);
        fragmentPagerAdapter.setFragments(mFragmentList);
        show_page_vp.setOnPageChangeListener(new MyOnPageChangeListener());
        show_page_vp.setCurrentItem(0);
    }

    /**
     * 获取tab列表数据
     *
     * @author hechuang
     * @time 2016/9/19 14:38
     */
    private void getTabListData() {
        //网络获取tab个数并添加到tab列表里面
        //获取到tab个数后设置showItemNum
        mScreenWidth = dm.widthPixels - 50;
        item_width = (int) ((mScreenWidth / showItemNum + 0.5f));
//        for (int i = 0; i < 10; i++) {
//            TextView view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.tab_text, null).findViewById(R.id.tab_text);
//            layout = new RelativeLayout(getActivity());
//            view.setText("美女" + i);
//            if(i == 0){
//                view.setTextColor(Color.BLUE);
//            }else{
//                view.setTextColor(Color.WHITE);
//            }
//            mTextViewList.add(view);
//            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//            params.addRule(RelativeLayout.CENTER_IN_PARENT);
//            layout.addView(view, params);
//            show_fragment_tab_manage_ll.addView(layout, (int) (mScreenWidth / showItemNum + 1 + 0.5f), 70);
//            layout.setOnClickListener(this);
//            layout.setTag(i);
//        }
        //****************秀自己
        TextView view = (TextView) getActivity().getLayoutInflater().inflate(R.layout.tab_text, null).findViewById(R.id.tab_text);
        layout = new RelativeLayout(getActivity());
        view.setText("我爱秀");
        view.setTextColor(Color.parseColor("#00B0C7"));
        mTextViewList.add(view);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(view, params);
        show_fragment_tab_manage_ll.addView(layout, (int) (mScreenWidth / showItemNum + 1 + 0.5f), LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setOnClickListener(this);
        layout.setTag(0);
        //****************秀品牌
        TextView view1 = (TextView) getActivity().getLayoutInflater().inflate(R.layout.tab_text, null).findViewById(R.id.tab_text);
        layout = new RelativeLayout(getActivity());
        view1.setText("大咖秀");
        view1.setTextColor(Color.WHITE);
        mTextViewList.add(view1);
        RelativeLayout.LayoutParams params1 = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        params1.addRule(RelativeLayout.CENTER_IN_PARENT);
        layout.addView(view1, params1);
        show_fragment_tab_manage_ll.addView(layout, (int) (mScreenWidth / showItemNum + 1 + 0.5f), LinearLayout.LayoutParams.MATCH_PARENT);
        layout.setOnClickListener(this);
        layout.setTag(1);
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {
        private ArrayList<Fragment> fragments;
        private FragmentManager fm;

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
            this.fm = fm;
        }

        public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
            super(fm);
            this.fm = fm;
            this.fragments = fragments;
        }

        @Override
        public int getCount() {
            return fragments.size();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }


        public void setFragments(ArrayList<Fragment> fragments) {
            if (this.fragments != null) {
                FragmentTransaction ft = fm.beginTransaction();
                for (Fragment f : this.fragments) {
                    ft.remove(f);
                }
                ft.commit();
                ft = null;
                fm.executePendingTransactions();
            }
            this.fragments = fragments;
            notifyDataSetChanged();
        }


        @Override
        public Object instantiateItem(ViewGroup container, final int position) {
            Object obj = super.instantiateItem(container, position);
            return obj;
        }

    }

    public class MyOnPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageSelected(final int position) {
            beginPosition = position * item_width;
            mTextViewList.get(position).setTextColor(Color.parseColor("#00B0C7"));
            mTextViewList.get(currentFragmentIndex).setTextColor(Color.WHITE);
            currentFragmentIndex = position;
            show_fragment_tab_sroll_sv.smoothScrollTo((currentFragmentIndex - 1) * item_width, 0);
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
                show_fragment_tab_sroll_sv.invalidate();
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
                if (show_page_vp.getCurrentItem() == currentFragmentIndex) {
                    show_fragment_tab_sroll_sv.invalidate();
                    endPosition = currentFragmentIndex * item_width;
                }
            }
        }

    }

    @Override
    public void onClick(View v) {
        show_page_vp.setCurrentItem((Integer) v.getTag());

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}