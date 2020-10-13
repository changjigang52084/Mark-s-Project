package com.sunchip.adw.cloudphotoframe.adapter;

import android.content.Context;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;

import java.util.ArrayList;
import java.util.List;

public class WifiListAdapter extends BaseAdapter {

    private static final String TAG = "WifiListAdapter";

    private Context mContext;
    private List<ScanResult> wifiList = new ArrayList<>();

    //正在连接的wifi
    private String SSID = "NULL";
    //连接信息
    private String Content = "NULL";

    private int Select = -1;

    public WifiListAdapter(Context context, List<ScanResult> wifiList) {
        this.mContext = context;
        this.wifiList = wifiList;
    }

    public void setwifiList(List<ScanResult> wifiList) {
        this.wifiList = wifiList;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return wifiList.size();
    }

    @Override
    public Object getItem(int i) {
        return wifiList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View convertView, ViewGroup viewGroup) {
        ViewHolder holder;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.wifi_item, null);
            holder = new ViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        ScanResult scanResult = wifiList.get(i);
        holder.tvName.setText(scanResult.SSID);

        holder.tvName.setTypeface(CloudFrameApp.typeFace);
        holder.tvContent.setTypeface(CloudFrameApp.typeFace);
        holder.tvType.setTypeface(CloudFrameApp.typeFace);


        int level = WifiManager.calculateSignalLevel(scanResult.level, 100);

        if (SSID.trim().contains(scanResult.SSID.trim())) {
            Log.e("TAG", "SSID:" + SSID + "  Content: " + Content);
            if (Content.equals(NetworkInfo.State.CONNECTED.toString())) {
                holder.tvContent.setText(Content + "");
                NotWifisrc(holder, level, R.drawable.not4, R.drawable.not3, R.drawable.not2, R.drawable.not);
            } else {
                holder.tvContent.setText(Content);
                NotWifisrc(holder, level, R.drawable.wifi4, R.drawable.wifi3, R.drawable.wifi2, R.drawable.wifi1);
            }
        } else {
            holder.tvContent.setText("");
            NotWifisrc(holder, level, R.drawable.wifi4, R.drawable.wifi3, R.drawable.wifi2, R.drawable.wifi1);
        }
        return convertView;
    }

    private void NotWifisrc(ViewHolder holder, int level, int p, int p2, int p3, int p4) {
        if (level > 80) {
            holder.ivwifi.setImageResource(p);
        } else if (level > 60 && level < 80) {
            holder.ivwifi.setImageResource(p2);
        } else if (level > 40 && level < 60) {
            holder.ivwifi.setImageResource(p3);
        } else {
            holder.ivwifi.setImageResource(p4);
        }
    }

    //状态连接的wifi
    public void setSSID(String mSSID, String mContent) {
        this.SSID = mSSID;
        this.Content = mContent;
        notifyDataSetChanged();
    }

    public void Selected(int postion) {
        this.Select = postion;
        notifyDataSetChanged();
    }

    static class ViewHolder {
        TextView tvName, tvType, tvContent;
        RelativeLayout mSifiRelayout;
        ImageView ivwifi;


        ViewHolder(View view) {
            tvName = view.findViewById(R.id.tv_name);
            tvType = view.findViewById(R.id.tv_type);
            tvContent = view.findViewById(R.id.tv_content);
            ivwifi = view.findViewById(R.id.wifi_iv);
            mSifiRelayout = view.findViewById(R.id.SifiRelayout);
        }

    }
}
