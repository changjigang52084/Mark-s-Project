package com.sunchip.adw.cloudphotoframe.adapter;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.signature.StringSignature;
import com.sunchip.adw.cloudphotoframe.R;
import com.sunchip.adw.cloudphotoframe.app.CloudFrameApp;
import com.sunchip.adw.cloudphotoframe.http.HttpURLUtils;
import com.sunchip.adw.cloudphotoframe.http.request.Bean.Event.PhotosEvent;
import com.sunchip.adw.cloudphotoframe.util.MediaFileUtils;
import com.sunchip.adw.cloudphotoframe.util.ViewUtil;
import com.sunchip.adw.cloudphotoframe.viewholder.RightListViewHolder;

import java.io.File;
import java.util.List;
import java.util.UUID;

import me.zhouzhuo.zzsecondarylinkage.adapter.RightMenuBaseListAdapter;

/**
 * Created by zz on 2016/8/20.
 */
public class RightContentListAdapter extends RightMenuBaseListAdapter<RightListViewHolder, PhotosEvent> {

    private static final String TAG = "RightContentListApdater";

    public RightContentListAdapter(Context ctx, List<PhotosEvent> list) {
        super(ctx, list);
    }

    @Override
    public RightListViewHolder getViewHolder() {
        return new RightListViewHolder();
    }

    @Override
    public void bindView(RightListViewHolder rightListViewHolder, View itemView) {
        ViewUtil.scaleContentView((ViewGroup) itemView.findViewById(R.id.root));
        rightListViewHolder.ivPic = (ImageView) itemView.findViewById(R.id.iv_pic);
        rightListViewHolder.IsVideoImage = (ImageView) itemView.findViewById(R.id.IsVideo);
    }

    @Override
    public void bindData(RightListViewHolder rightListViewHolder, int position) {
        String Key = getItem(position).getKey();
        int Sm = Key.lastIndexOf(".");
        String Suffix = getItem(position).getKey().substring(Sm);

        String PhotoPathName = getItem(position).getMd5() + Suffix;
        //拼接地址 本地图片地址 如果没有本地图片 那么就不用显示其网页图片了
        String Path = CloudFrameApp.BASE + PhotoPathName;


        boolean IsPhoto = HttpURLUtils.IsFileExists(Path);
        //为了判读下载地址是否存在 如果不存在基本上就不需要显示照片了 这是最基本的操作
        if (IsPhoto) {
            if (MediaFileUtils.isImageFileType(Path))
                Glide.with(context).load(Uri.fromFile(new File(Path))).placeholder(R.mipmap.wait_for)
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .centerCrop().signature(new StringSignature(UUID.randomUUID().toString())).into(rightListViewHolder.ivPic);
            else
                Glide.with(context).load(Uri.fromFile(new File(Path))).placeholder(R.mipmap.wait_for)
                        .centerCrop().signature(new StringSignature(UUID.randomUUID().toString())).into(rightListViewHolder.ivPic);

            rightListViewHolder.IsVideoImage.setVisibility(!MediaFileUtils.isImageFileType(Path) ? View.VISIBLE : View.GONE);
        } else {
            rightListViewHolder.IsVideoImage.setVisibility(View.GONE);
            Glide.with(context).load(R.mipmap.wait_for).into(rightListViewHolder.ivPic);
        }
    }

    @Override
    public int getLayoutId() {
        return R.layout.list_item_content;
    }
}
