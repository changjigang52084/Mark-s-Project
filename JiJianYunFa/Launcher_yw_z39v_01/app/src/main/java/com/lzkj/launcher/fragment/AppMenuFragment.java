package com.lzkj.launcher.fragment;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.lzkj.launcher.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：Launcher
 * 类名称：AppMenuFragment
 * 类描述：展示所有已安装的app界面
 * 创建人："lyhuang"
 * 创建时间：2015年8月8日 下午12:51:14
 */
@SuppressLint("NewApi")
public class AppMenuFragment extends Fragment {

    private static final String TAG = "AppMenuFragment";

    private View view = null;
    private GridView gridView;
    private List<ResolveInfo> apps = new ArrayList<ResolveInfo>();
    private AppsAdapter adapter;
    private Context ctx;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "AppMenuFragment onCreate.");
        ctx = getActivity();
    }

    @Override
    public void onResume() {
        Log.d(TAG, "AppMenuFragment onResume.");
        loadApps();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "AppMenuFragment onCreateView.");
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_appmenu, container, false);
            gridView = (GridView) view.findViewById(R.id.apps_list);
            gridView.setOnItemClickListener(listener);
            adapter = new AppsAdapter();
            gridView.setAdapter(adapter);
        }
        return view;
    }


    @Override
    public void onDestroy() {
        Log.d(TAG, "AppMenuFragment onDestroy.");
        super.onDestroy();
    }


    /**
     * @return void    返回类型
     * @Title: loadApps
     * @Description: TODO(通过PackageManager的api 查询已经安装的apk)
     * @Param 设定文件
     */
    private void loadApps() {
        Intent intent = new Intent(Intent.ACTION_MAIN, null);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        apps = ctx.getPackageManager().queryIntentActivities(intent, 0);
        Log.d(TAG, "AppMenuFragment loadApps apps size: " + apps.size());
    }


    /**
     * 项目名称：Launcher
     * 类名称：AppsAdapter
     * 类描述：实现用于显示Gridview的Adapter，使其显示获得的应用程序列表
     * 创建人："lyhuang"
     * 创建时间：2015年8月8日 下午2:14:57
     */
    public class AppsAdapter extends BaseAdapter {
        private LayoutInflater inflater;

        public AppsAdapter() {
            inflater = LayoutInflater.from(ctx);
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.app_item, null);
                holder.appImage = (ImageView) convertView.findViewById(R.id.app_image);
                holder.appName = (TextView) convertView.findViewById(R.id.app_name);
                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            ResolveInfo info = apps.get(position);
            holder.appImage.setImageDrawable(info.loadIcon(ctx.getPackageManager()));
            holder.appName.setText(info.loadLabel(ctx.getPackageManager()).toString());
            return convertView;
        }

        public final int getCount() {
            return apps.size();
        }

        public final Object getItem(int position) {
            return apps.get(position);
        }

        public final long getItemId(int position) {
            return position;
        }
    }

    public final class ViewHolder {
        public ImageView appImage;
        public TextView appName;
    }

    private OnItemClickListener listener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            ResolveInfo info = apps.get(position);
            //该应用的包名
            String pkg = info.activityInfo.packageName;
            //应用的主activity类
            String cls = info.activityInfo.name;

            ComponentName component = new ComponentName(pkg, cls);
            Intent i = new Intent();
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            i.setComponent(component);
            startActivity(i);
        }
    };
}
