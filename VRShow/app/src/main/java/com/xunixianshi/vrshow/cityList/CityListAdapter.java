package com.xunixianshi.vrshow.cityList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.my.PersonalInformationActivity;
import com.xunixianshi.vrshow.obj.CityListItemResult;
import com.xunixianshi.vrshow.obj.CityListResult;
import com.xunixianshi.vrshow.util.StringUtil;
import com.zhy.http.okhttp.api.OkHttpAPI;
import com.zhy.http.okhttp.callback.GenericsCallback;

import java.util.ArrayList;
import java.util.HashMap;

import butterknife.Bind;
import butterknife.ButterKnife;
import okhttp3.Call;


/**
 * Created by Administrator on 2016/10/24.
 */

public class CityListAdapter extends BaseAda<CityListItemResult> {

    private ArrayList<CityListItemResult> items;
    private Context mContext;

    public CityListAdapter(Context context) {
        super(context);
        this.mContext = context;
        items = new ArrayList<CityListItemResult>();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_city_item, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
       final CityListItemResult item = getItem(position);
        viewHolder.city_area_name_tv.setText(item.getAreaName());
        viewHolder.city_item_rl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getNetData(item.getAreaCode(),item.getAreaName());
            }
        });
        return convertView;
    }

    static class ViewHolder {
        @Bind(R.id.city_area_name_tv)
        TextView city_area_name_tv;
        @Bind(R.id.city_area_next_iv)
        ImageView city_area_next_iv;
        @Bind(R.id.city_item_rl)
        RelativeLayout city_item_rl;


        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }

    private void getNetData(final String thisAreaCode,final String areaName) {
        HashMap<String, Object> hashMap = new HashMap<String, Object>();
        hashMap.put("areaCode", thisAreaCode);
        // 转参数
        String jsonData = StringUtil.MapToJson(hashMap);
        OkHttpAPI.postHttpData("/config/area/list/service", jsonData, new GenericsCallback<CityListResult>() {
            @Override
            public void onError(Call call, Exception e, int id) {
            }

            @Override
            public void onResponse(CityListResult result, int id) {
                if(result.getResCode().equals("0")){
                    if(result.getList().size()>0){
                        Bundle bundle = new Bundle();
                        items.clear();
                        items.addAll(result.getList());
                        bundle.putSerializable("areaCode",items);
                        showActivity(mContext,CityListActivity.class,bundle);
                    }else{
                        Intent intent = new Intent();
                        intent.putExtra("areaName",areaName);
                        intent.putExtra("areaCode",thisAreaCode);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setClass(mContext, PersonalInformationActivity.class);
                        mContext.startActivity(intent);
                    }
                }
            }
        });
    }
    public void showActivity(Context context, Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        intent.putExtras(extras);
        intent.setClass(context, cls);
        context.startActivity(intent);
    }
}
