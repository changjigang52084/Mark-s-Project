package com.xunixianshi.vrshow.cityList;

import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.widget.PullToRefreshListView;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAct;
import com.xunixianshi.vrshow.obj.CityListItemResult;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2016/10/24.
 */

public class CityListActivity extends BaseAct {
    @Bind(R.id.title_back_iv)
    ImageView title_back_iv;
    @Bind(R.id.title_center_tv)
    TextView title_center_tv;
    @Bind(R.id.city_list_ptrlv)
    PullToRefreshListView city_list_ptrlv;

    private String areaCode = "";
    private ArrayList<CityListItemResult> items;
    private CityListAdapter mCityListAdapter;

    @Override
    public void setRootView() {
        setContentView(R.layout.activity_city_list);
        ButterKnife.bind(this);
    }

    @Override
    protected void initData() {
        super.initData();
        items = new ArrayList<CityListItemResult>();
        mCityListAdapter = new CityListAdapter(CityListActivity.this);
        city_list_ptrlv.setAdapter(mCityListAdapter);
        items = (ArrayList<CityListItemResult>)getIntent().getSerializableExtra("areaCode");
        mCityListAdapter.setGroup(items);
    }

    @OnClick(R.id.title_back_iv)
    public void onClick() {
        CityListActivity.this.finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ButterKnife.unbind(this);
    }
}
