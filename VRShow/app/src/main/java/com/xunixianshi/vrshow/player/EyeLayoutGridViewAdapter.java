package com.xunixianshi.vrshow.player;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.hch.viewlib.util.ScreenUtils;
import com.xunixianshi.vrshow.R;
import com.xunixianshi.vrshow.actmanager.BaseAda;
import com.xunixianshi.vrshow.customview.CustomImageView;
import com.xunixianshi.vrshow.interfaces.ImageViewInterface;
import com.xunixianshi.vrshow.interfaces.LoadFinishedInterface;
import com.xunixianshi.vrshow.obj.ClassifyVideoTypeResultResourcesList;
import com.zhy.http.okhttp.utils.PicassoUtil;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * User: hch
 * Date: 2016-06-22
 * Time: 18:10
 * FIXME
 */
public class EyeLayoutGridViewAdapter extends BaseAda<ClassifyVideoTypeResultResourcesList> {

    private Context mContext;
    private LoadFinishedInterface mLoadFinishedInterface;
    private CustomImageView eyes_controller_center1_iv;
    private int screenWith;

    @Override
    public int getCount() {
        return (group == null) ? 0 : group.size() > 9 ? 9 : group.size();
    }

    public EyeLayoutGridViewAdapter(Context context) {
        super(context);
        this.mContext = context;
        screenWith = ScreenUtils.getScreenWidth(mContext);
    }

    public void setInterface(LoadFinishedInterface loadFinishedInterface) {
        this.mLoadFinishedInterface = loadFinishedInterface;
    }

    public void setImageview(CustomImageView eyes_controller_center1_iv) {
        this.eyes_controller_center1_iv = eyes_controller_center1_iv;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = mInflater
                    .inflate(R.layout.item_eyelayout_gridview, null);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ClassifyVideoTypeResultResourcesList classifyVideoTypeResultResourcesList = getItem(position);
        PicassoUtil.loadImage(mContext, classifyVideoTypeResultResourcesList.getCoverImgUrl() + ("?imageView2/2/w/" + screenWith / 8 + "/q/300"), viewHolder.item_eyelayout_gridview_video_image_iv);
        viewHolder.item_eyelayout_gridview_video_name_tv.setText(classifyVideoTypeResultResourcesList.getResourceName());
        return convertView;
    }

    class ViewHolder {
        @Bind(R.id.item_eyelayout_gridview_video_image_iv)
        ImageView item_eyelayout_gridview_video_image_iv;
        @Bind(R.id.item_eyelayout_gridview_video_name_tv)
        TextView item_eyelayout_gridview_video_name_tv;

        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
