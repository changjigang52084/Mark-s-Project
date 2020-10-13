package com.lzmr.bindtool.ui.fragment.devices;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.design.widget.TabLayout.OnTabSelectedListener;
import android.support.design.widget.TabLayout.Tab;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baize.adpress.core.protocol.dto.DeviceListDto;
import com.baize.adpress.core.protocol.dto.DeviceStatisticsDto;
import com.lzkj.baize_android.utils.LogUtils;
import com.lzkj.baize_android.utils.LogUtils.LogTag;
import com.lzmr.bindtool.R;
import com.lzmr.bindtool.adapter.FragmentMainViewPagerAdapter;
import com.lzmr.bindtool.api.DevicesInfoManager;
import com.lzmr.bindtool.api.listener.GetDevicesInfoListener;
import com.lzmr.bindtool.impl.ControlFragmentListener;
import com.lzmr.bindtool.ui.fragment.BaseFragment;
import com.lzmr.bindtool.ui.fragment.MenuFragment;
import com.lzmr.bindtool.util.LogoutUtil;

/**
 * 终端管理界面
 *
 * @author longyihuang
 * @date 2016-3-11 下午4:20:53
 */
@SuppressLint("ValidFragment")
public class DevicesManagerFragment extends BaseFragment implements OnClickListener, GetDevicesInfoListener {
    private static final LogTag TAG = LogUtils.getLogTag(DevicesManagerFragment.class.getSimpleName(), true);
    /**
     * TabLayout
     */
    private TabLayout tabs;
    /**
     * ViewPager
     */
    private ViewPager viewPager;
    /**
     * @Fields bindDevice :  TODO 扫码绑定
     */
    private ImageView bindDeviceImg;

    /**
     * viewpager的适配器
     **/
    private FragmentMainViewPagerAdapter viewPagerAdapter;

    /**
     * 返回首页
     */
    private RelativeLayout homePage;

    /**
     * 刷新
     */
    private ImageView refreshImg;

    /**
     * 所有终端列表界面
     */
    private AllDeviceFragment allDeviceFragment;
    private OfflineDeviceFragment offlineDeviceFragment;
    private StandbyDeviceFragment standbyDeviceFragment;
    /**
     * tab 标题显示终端数
     **/
    private TextView[] tabTitle;
    /**
     * tab 标题显示终端状态
     **/
    private TextView[] tabTitleName;
    /**
     * tab 标题显示终端图标
     **/
    private CheckBox[] tabImgIcon;
    private int tabsIndex = 0;

    public DevicesManagerFragment(ControlFragmentListener switchFragmentListener) {
        super(switchFragmentListener);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        if (null != bundle) {
            tabsIndex = bundle.getInt("tabsIndex");
            LogUtils.d(TAG, "onCreate", "tabsIndex:" + tabsIndex);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        LogUtils.d(TAG, "onCreateView", "onCreateView");

        if (view == null) {
            view = inflater.inflate(R.layout.fragment_device_manager, null);
            initView(view);
        }

        return view;
    }

    private void initView(View view) {
        tabTitle = new TextView[3];
        tabTitleName = new TextView[3];
        tabImgIcon = new CheckBox[3];
        LogUtils.d(TAG, "onCreateView", "initView");
        bindDeviceImg = (ImageView) view.findViewById(R.id.img_scanning_bind);
        bindDeviceImg.setOnClickListener(this);
        tabs = (TabLayout) view.findViewById(R.id.tabs_device);
        viewPager = (ViewPager) view.findViewById(R.id.viewpage_device);

        homePage = (RelativeLayout) view.findViewById(R.id.rl_home_page);
        homePage.setOnClickListener(this);

        refreshImg = (ImageView) view.findViewById(R.id.img_refresh);
        refreshImg.setOnClickListener(this);
    }


    @Override
    public void onDestroyView() {
        LogUtils.d(TAG, "onDestroyView", "onDestroyView");
        super.onDestroyView();
    }

    /**
     * 初始化main view pager适配器显示所有的fragment
     */
    private void initMainViewPagerAdapter() {
        // 设置ViewPager的适配器
        viewPagerAdapter = new FragmentMainViewPagerAdapter(getActivity(), getChildFragmentManager(),
                getFragments(), getTabTitle(), getTabIcons());
        DevicesInfoManager devicesInfoManager = new DevicesInfoManager(this, AllDeviceFragment.UPDATE_FLAG_REFRESH, 1, 1, 0);
        devicesInfoManager.loadDevices();
//	    	DevicesInfoManager.getInstance().getDeviceStatisticsDto(this);
        viewPager.setAdapter(viewPagerAdapter);
        tabs.setupWithViewPager(viewPager);
//			tabs.setTabsFromPagerAdapter(viewPagerAdapter);
        tabs.getTabAt(tabsIndex).select();
        tabs.setOnTabSelectedListener(new OnTabSelectedListener() {

            @Override
            public void onTabUnselected(Tab arg0) {
                // TODO Auto-generated method stub

            }

            @Override
            public void onTabSelected(Tab tab) {
                LogUtils.d(TAG, "onTabSelected", "position:" + tab.getPosition());
                tabsIndex = tab.getPosition();
                updateTabTitleState(tab.getPosition());
                // 每当我们选择了一个Tab就将ViewPager滚动至对应的Page
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabReselected(Tab arg0) {
                // TODO Auto-generated method stub

            }
        });
        for (int i = 0; i < tabs.getTabCount(); i++) {
            TabLayout.Tab tab = tabs.getTabAt(i);
            tab.setCustomView(viewPagerAdapter.getTabView(i, tabTitle, tabTitleName, tabImgIcon));
        }
        updateTabTitleState(tabsIndex);
    }

    /**
     * 更新tab状态
     *
     * @param position
     */
    private void updateTabTitleState(int position) {
        for (int i = 0; i < 3; i++) {
            LogUtils.d(TAG, "onTabSelected", "position:" + position + ", i : " + i);
            if (i == position) {
                tabTitle[position].setTextColor(getResources().getColor(R.color.tab_select_color));
                tabTitleName[position].setTextColor(getResources().getColor(R.color.tab_select_color));
                tabImgIcon[position].setChecked(true);
                continue;
            }
            tabTitle[i].setTextColor(getResources().getColor(R.color.tab_unselect_color));
            tabTitleName[i].setTextColor(getResources().getColor(R.color.tab_unselect_color));
            tabImgIcon[i].setChecked(false);
        }
    }

    /**
     * 获取在main viewpager里面展示的一组fragment
     *
     * @return 返回一组fragment
     */
    private ArrayList<Fragment> getFragments() {
        ArrayList<Fragment> viewPagerFragments = new ArrayList<Fragment>();
        if (null == allDeviceFragment) {
            allDeviceFragment = new AllDeviceFragment(switchFragmentListener);
        }
        if (null == offlineDeviceFragment) {
            offlineDeviceFragment = new OfflineDeviceFragment(switchFragmentListener);
        }
        if (null == standbyDeviceFragment) {
            standbyDeviceFragment = new StandbyDeviceFragment(switchFragmentListener);
        }
        viewPagerFragments.add(allDeviceFragment);
        viewPagerFragments.add(offlineDeviceFragment);
        viewPagerFragments.add(standbyDeviceFragment);
        return viewPagerFragments;
    }

    /**
     * 获取tab显示的标题
     *
     * @return 一组tab标题
     */
    private String[] getTabTitle() {
        String[] tabTitles = new String[]{
                getString(R.string.title_tabhost_all_device),
                getString(R.string.title_tabhost_offline_device),
                getString(R.string.title_tabhost_standby_device),
        };
        return tabTitles;
    }

    /**
     * 获取在tab上面显示的icon
     *
     * @return
     */
    private int[] getTabIcons() {
        int[] tabIcons = new int[]{R.drawable.tab_item_title_icon,
                R.drawable.tab_offline_drawable,
                R.drawable.tab_standby_drawable};
        return tabIcons;
    }

    @Override
    public void onStart() {
        LogUtils.d(TAG, "onStart", "onStart");
        super.onStart();
        initMainViewPagerAdapter();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_scanning_bind: //绑定
                switchScanning(null, true);
                break;
            case R.id.rl_home_page:
                popBackStackToFragment(MenuFragment.class.getSimpleName(), 0);
                break;
            case R.id.img_refresh:
                refreshFragment(tabsIndex);
                break;
            default:
                break;
        }

    }

    /**
     * @param @param tabsIndex 终端列表下标： 0-所有终端 1-离线终端 2-待机终端
     * @Title: refreshFragment
     * @Description: 刷新设备列表
     */
    private void refreshFragment(int tabsIndex) {
//			DevicesInfoManager.getInstance().getDeviceStatisticsDto(this);
        switch (tabsIndex) {
            case 0:
                allDeviceFragment.loadDeviceData(AllDeviceFragment.UPDATE_FLAG_REFRESH, 1, 0, "DevicesManagerFragment");
                break;
            case 1:
                offlineDeviceFragment.loadDeviceData(AllDeviceFragment.UPDATE_FLAG_REFRESH, 1, 2, "DevicesManagerFragment");
                break;
            case 2:
                standbyDeviceFragment.loadDeviceData(AllDeviceFragment.UPDATE_FLAG_REFRESH, 1, 3, "DevicesManagerFragment");
            default:
                break;
        }
    }

    @Override
    public void onGetDevicesInfo(int updateFlag, DeviceListDto devices) {
        if (null == devices) {
            LogUtils.e(TAG, "onGetDevicesInfo", "Load device data failed.");
            return;
        }
        DeviceStatisticsDto statistics = devices.getStatistics();
        final String[] devicesNumber = new String[]{String.valueOf(statistics.getTotal()),
                String.valueOf(statistics.getOffline()),
                String.valueOf(statistics.getShutdown())};
        bindDeviceImg.post(new Runnable() {
            @Override
            public void run() {
                int size = tabTitle.length;
                for (int i = 0; i < size; i++) {
                    tabTitle[i].setText(devicesNumber[i]);
                }
            }
        });
    }

    @Override
    public void onSuccess(String msg) {

    }

    @Override
    public void onFailure(String msg) {
        showDialog(msg, null);
    }

    @Override
    public void onSessionInvalid() {
        switchLoginAndCloseOther(true);
    }
}
